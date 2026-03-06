package com.billing.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.billing.model.document.DeliveryNote;
import com.billing.model.document.DocumentLine;
import com.billing.model.item.Item;
import com.billing.service.ClientService;
import com.billing.service.DeliveryService;
import com.billing.service.ItemService;
import com.billing.service.SalesService;

/**
 * Order workflow panel: quotation -> order -> delivery note.
 */
public class OrderManagementPanel extends JPanel {

    private final DeliveryService deliveryService;
    private final ClientService clientService;
    private final ItemService itemService;
    private final SalesService salesService;

    private JTable quotationTable;
    private JTable orderTable;
    private DefaultTableModel quotationModel;
    private DefaultTableModel orderModel;

    private JButton newQuotationButton;
    private JButton sendQuotationButton;
    private JButton acceptQuotationButton;
    private JButton rejectQuotationButton;
    private JButton sendOrderButton;
    private JButton refreshButton;
    private JButton viewQuotationButton;
    private JButton viewOrderButton;

    private List<SalesService.Quotation> quotations = java.util.Collections.emptyList();
    private List<SalesService.SalesOrder> orders = java.util.Collections.emptyList();

    public OrderManagementPanel(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
        this.clientService = new ClientService();
        this.itemService = new ItemService();
        this.salesService = deliveryService.getSalesService();
        initializeComponents();
        setupLayout();
        setupEvents();
        loadData();
    }

    private void initializeComponents() {
        newQuotationButton = UIConstants.createSuccessButton("ADD", UIConstants.loadIcon("/icons/add.png", 16));
        sendQuotationButton = UIConstants.createPrimaryButton("SEND", UIConstants.loadIcon("/icons/next.png", 16));
        acceptQuotationButton = UIConstants.createSuccessButton("ACCEPT", UIConstants.loadIcon("/icons/accept.png", 16));
        rejectQuotationButton = UIConstants.createDangerButton("REJECT", UIConstants.loadIcon("/icons/cancel.png", 16));
        sendOrderButton = UIConstants.createPrimaryButton("SEND", UIConstants.loadIcon("/icons/delivery.png", 16));
        refreshButton = UIConstants.createSecondaryButton("REFRESH", UIConstants.loadIcon("/icons/refresh.png", 16));
        viewQuotationButton = UIConstants.createSecondaryButton("VIEW DETAILS", UIConstants.loadIcon("/icons/details.png", 16));
        viewOrderButton = UIConstants.createSecondaryButton("VIEW DETAILS", UIConstants.loadIcon("/icons/details.png", 16));

        setButtonTextColor();

        quotationModel = new DefaultTableModel(new String[]{"Code", "Date", "Status", "Customer", "Lines", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        orderModel = new DefaultTableModel(new String[]{"Code", "Date", "Customer", "Lines", "Total"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        quotationTable = new JTable(quotationModel);
        orderTable = new JTable(orderModel);

        styleTable(quotationTable);
        styleTable(orderTable);
    }

    private void styleTable(JTable table) {
        table.setRowHeight(28);
        table.setFont(UIConstants.UI_FONT);
        table.getTableHeader().setFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        table.getTableHeader().setOpaque(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        table.getTableHeader().setCursor(Cursor.getDefaultCursor());
        table.setAutoCreateRowSorter(false);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setBackground(UIConstants.MODULE_ORDERS_TEXT);
                l.setForeground(Color.WHITE);
                l.setFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
                l.setOpaque(true);
                l.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
                return l;
            }
        };
        table.getTableHeader().setDefaultRenderer(headerRenderer);

        DefaultTableCellRenderer rowRenderer = new DefaultTableCellRenderer() {
            private final Color EVEN = Color.WHITE;
            private final Color ODD = UIConstants.MODULE_ORDERS_BG;

            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, s, f, r, c);
                if (s) {
                    l.setBackground(UIConstants.MODULE_ORDERS_ICON);
                    l.setForeground(Color.WHITE);
                } else {
                    l.setBackground(r % 2 == 0 ? EVEN : ODD);
                    l.setForeground(new Color(0x212121));
                }
                return l;
            }
        };

        table.setDefaultRenderer(Object.class, rowRenderer);
        table.setDefaultRenderer(Number.class, rowRenderer);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(UIConstants.MODULE_ORDERS_BG);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(UIConstants.MODULE_ORDERS_TEXT);
        header.setPreferredSize(new Dimension(0, 80));
        JLabel title = new JLabel("Order Management", SwingConstants.CENTER);
        title.setFont(UIConstants.TITLE_FONT.deriveFont(24f));
        title.setForeground(Color.WHITE);
        header.add(title, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        controls.setOpaque(false);
        controls.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));
        controls.add(Box.createHorizontalStrut(12));
        controls.add(refreshButton);

        top.add(header, BorderLayout.NORTH);
        top.add(controls, BorderLayout.CENTER);

        JPanel center = new JPanel(new GridLayout(2, 1, 0, 12));
        center.setOpaque(false);

        center.add(wrapTable("Quotations", quotationTable,
            newQuotationButton,
            sendQuotationButton,
            acceptQuotationButton,
            rejectQuotationButton,
            viewQuotationButton));
        center.add(wrapTable("Orders", orderTable, sendOrderButton, viewOrderButton));

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
    }

    private JPanel wrapTable(String title, JTable table, JButton... buttons) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(0, 4, 6, 4));
        
        JLabel label = new JLabel(title);
        label.setFont(UIConstants.UI_FONT.deriveFont(Font.BOLD, 14f));
        label.setForeground(UIConstants.MODULE_ORDERS_TEXT);
        titlePanel.add(label, BorderLayout.WEST);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        buttonPanel.setOpaque(false);
        for (JButton button : buttons) {
            button.setForeground(Color.WHITE);
            buttonPanel.add(button);
        }
        titlePanel.add(buttonPanel, BorderLayout.EAST);
        
        p.add(titlePanel, BorderLayout.NORTH);
        JScrollPane sp = new JScrollPane(table);
        UIConstants.enhanceScroll(sp);
        p.add(sp, BorderLayout.CENTER);
        return p;
    }

    private void setButtonTextColor() {
        newQuotationButton.setForeground(Color.WHITE);
        sendQuotationButton.setForeground(Color.WHITE);
        acceptQuotationButton.setForeground(Color.WHITE);
        rejectQuotationButton.setForeground(Color.WHITE);
        sendOrderButton.setForeground(Color.WHITE);
        refreshButton.setForeground(Color.WHITE);
        viewQuotationButton.setForeground(Color.WHITE);
        viewOrderButton.setForeground(Color.WHITE);
    }

    private void setupEvents() {
        refreshButton.addActionListener(e -> loadData());
        newQuotationButton.addActionListener(e -> createQuotation());
        sendQuotationButton.addActionListener(e -> updateSelectedQuotationStatus(SalesService.QuotationStatus.SENT));
        acceptQuotationButton.addActionListener(e -> acceptSelectedQuotation());
        rejectQuotationButton.addActionListener(e -> updateSelectedQuotationStatus(SalesService.QuotationStatus.REJECTED));
        sendOrderButton.addActionListener(e -> sendOrder());
        viewQuotationButton.addActionListener(e -> viewQuotationDetails());
        viewOrderButton.addActionListener(e -> viewOrderDetails());

        quotationTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateQuotationButtonsState();
            }
        });

        orderTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateOrderButtonsState();
            }
        });

        quotationTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) viewQuotationDetails();
            }
        });

        orderTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) viewOrderDetails();
            }
        });
    }

    private void loadData() {
        try {
            quotations = deliveryService.listQuotations();
            if (quotations == null) {
                quotations = java.util.Collections.emptyList();
            }
            quotations = quotations.stream()
                .filter(q -> q.status == null
                    || (q.status != SalesService.QuotationStatus.ACCEPTED
                        && q.status != SalesService.QuotationStatus.REJECTED))
                .toList();
            orders = deliveryService.listOrders();
            if (orders == null) {
                orders = java.util.Collections.emptyList();
            }
            orders = orders.stream()
                .filter(o -> o.status == null || o.status != SalesService.OrderStatus.SENT)
                .toList();
            refreshQuotations();
            refreshOrders();
            updateQuotationButtonsState();
            updateOrderButtonsState();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading data: " + ex.getMessage() + "\n" + ex.getClass().getName(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void refreshQuotations() {
        quotationModel.setRowCount(0);
        for (SalesService.Quotation q : quotations) {
            quotationModel.addRow(new Object[]{
                q.code,
                q.date,
                q.status != null ? q.status.name() : "",
                q.customer != null ? q.customer.getName() : "",
                q.lines != null ? q.lines.size() : 0,
                q.total
            });
        }
    }

    private void refreshOrders() {
        orderModel.setRowCount(0);
        for (SalesService.SalesOrder o : orders) {
            orderModel.addRow(new Object[]{
                o.code,
                o.date,
                o.customer != null ? o.customer.getName() : "",
                o.lines != null ? o.lines.size() : 0,
                o.total
            });
        }
    }

    private void createQuotation() {
        try {
            Frame parent = JOptionPane.getFrameForComponent(this);
            OrderFormDialog dialog = new OrderFormDialog(
                parent,
                clientService,
                itemService,
                salesService
            );
            dialog.setVisible(true);
            
            if (dialog.isSaved()) {
                try {
                    loadData();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Error reloading quotations: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error creating quotation: " + ex.getMessage() + "\n" + ex.getClass().getName(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private List<DocumentLine> captureQuotationLines() {
        List<Item> items = itemService.getAllItems();
        if (items == null || items.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No items available. Create an item first.",
                "Warning",
                JOptionPane.WARNING_MESSAGE);
            return java.util.Collections.emptyList();
        }

        List<DocumentLine> lines = new ArrayList<>();
        boolean continueAdding = true;

        while (continueAdding) {
            JComboBox<Item> itemCombo = new JComboBox<>(items.toArray(new Item[0]));
            itemCombo.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
                java.awt.Component c = new JLabel(
                    value == null ? "" : value.getCode() + " - " + value.getDescription()
                );
                if (isSelected) {
                    c.setBackground(list.getSelectionBackground());
                    c.setForeground(list.getSelectionForeground());
                } else {
                    c.setBackground(list.getBackground());
                    c.setForeground(list.getForeground());
                }
                ((JLabel)c).setOpaque(true);
                return c;
            });
            JSpinner qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));

            JPanel linePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            linePanel.add(new JLabel("Item:"));
            linePanel.add(itemCombo);
            linePanel.add(new JLabel("Qty:"));
            linePanel.add(qtySpinner);

            int lineResult = JOptionPane.showConfirmDialog(
                this,
                linePanel,
                "Add Quotation Line",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (lineResult != JOptionPane.OK_OPTION) break;

            Item selectedItem = (Item) itemCombo.getSelectedItem();
            int quantity = (Integer) qtySpinner.getValue();
            if (selectedItem != null) {
                DocumentLine line = new DocumentLine();
                line.setItem(selectedItem);
                line.setQuantity(quantity);
                line.setUnitPrice(selectedItem.getSalePrice());
                BigDecimal taxRate = BigDecimal.ZERO;
                if (selectedItem.getIVAPercent() != null) {
                    taxRate = BigDecimal.valueOf(selectedItem.getIVAPercent())
                        .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
                }
                line.setTaxRate(taxRate);
                lines.add(line);
            }

            int more = JOptionPane.showConfirmDialog(
                this,
                "Add another line?",
                "Quotation Lines",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            continueAdding = more == JOptionPane.YES_OPTION;
        }

        return lines;
    }

    private void acceptSelectedQuotation() {
        int selectedRow = quotationTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Select a quotation first.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = quotationTable.convertRowIndexToModel(selectedRow);
        if (modelRow < 0 || modelRow >= quotations.size()) return;

        SalesService.Quotation selected = quotations.get(modelRow);
        SalesService.QuotationStatus current = selected.status != null
            ? selected.status
            : SalesService.QuotationStatus.DRAFT;
        if (current != SalesService.QuotationStatus.SENT) {
            JOptionPane.showMessageDialog(this,
                "Quotation must be SENT before it can be accepted.",
                "Invalid Status",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            SalesService.SalesOrder order = deliveryService.acceptQuotation(selected.id);
            loadData();
            String code = order != null && order.code != null
                ? order.code
                : (order != null && order.id != null
                    ? String.format("SO%04d", order.id)
                    : "(unknown)");
            JOptionPane.showMessageDialog(this,
                "Order created: " + code,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error accepting quotation: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSelectedQuotationStatus(SalesService.QuotationStatus status) {
        int selectedRow = quotationTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Select a quotation first.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = quotationTable.convertRowIndexToModel(selectedRow);
        if (modelRow < 0 || modelRow >= quotations.size()) return;

        SalesService.Quotation selected = quotations.get(modelRow);
        SalesService.QuotationStatus current = selected.status != null
            ? selected.status
            : SalesService.QuotationStatus.DRAFT;
        if (!isQuotationTransitionAllowed(current, status)) {
            JOptionPane.showMessageDialog(this,
                "Invalid transition: " + current + " -> " + status,
                "Invalid Status",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            SalesService.Quotation q = deliveryService.updateQuotationStatus(selected.id, status);
            loadData();
            String code = q != null && q.code != null
                ? q.code
                : (q != null && q.id != null
                    ? String.format("QT%04d", q.id)
                    : "(unknown)");
            JOptionPane.showMessageDialog(this,
                "Quotation " + code + " -> " + status.name(),
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error updating quotation status: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Select an order first.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        int modelRow = orderTable.convertRowIndexToModel(selectedRow);
        if (modelRow < 0 || modelRow >= orders.size()) return;

        SalesService.SalesOrder selected = orders.get(modelRow);
        SalesService.OrderStatus current = selected.status != null
            ? selected.status
            : SalesService.OrderStatus.CREATED;
        if (current != SalesService.OrderStatus.CREATED) {
            JOptionPane.showMessageDialog(this,
                "Order is already SENT.",
                "Invalid Status",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            DeliveryNote deliveryNote = deliveryService.sendOrder(selected.id);
            loadData();
            String code = deliveryNote != null && deliveryNote.getCode() != null
                ? deliveryNote.getCode()
                : (deliveryNote != null && deliveryNote.getId() != null
                    ? String.format("DN%04d", deliveryNote.getId())
                    : "(unknown)");
            JOptionPane.showMessageDialog(this,
                "Delivery note created: " + code,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error creating delivery note: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean isQuotationTransitionAllowed(SalesService.QuotationStatus current, SalesService.QuotationStatus target) {
        if (current == SalesService.QuotationStatus.ACCEPTED || current == SalesService.QuotationStatus.REJECTED) {
            return false;
        }
        if (current == SalesService.QuotationStatus.DRAFT) {
            return target == SalesService.QuotationStatus.DRAFT || target == SalesService.QuotationStatus.SENT;
        }
        if (current == SalesService.QuotationStatus.SENT) {
            return target == SalesService.QuotationStatus.SENT
                || target == SalesService.QuotationStatus.REJECTED;
        }
        return false;
    }

    private void updateQuotationButtonsState() {
        int selectedRow = quotationTable.getSelectedRow();
        if (selectedRow < 0) {
            sendQuotationButton.setEnabled(false);
            acceptQuotationButton.setEnabled(false);
            rejectQuotationButton.setEnabled(false);
            viewQuotationButton.setEnabled(false);
            return;
        }

        int modelRow = quotationTable.convertRowIndexToModel(selectedRow);
        if (modelRow < 0 || modelRow >= quotations.size()) {
            sendQuotationButton.setEnabled(false);
            acceptQuotationButton.setEnabled(false);
            rejectQuotationButton.setEnabled(false);
            viewQuotationButton.setEnabled(false);
            return;
        }

        SalesService.Quotation selected = quotations.get(modelRow);
        SalesService.QuotationStatus current = selected.status != null
            ? selected.status
            : SalesService.QuotationStatus.DRAFT;

        sendQuotationButton.setEnabled(current == SalesService.QuotationStatus.DRAFT);
        acceptQuotationButton.setEnabled(current == SalesService.QuotationStatus.SENT);
        rejectQuotationButton.setEnabled(current == SalesService.QuotationStatus.SENT);
        viewQuotationButton.setEnabled(true);
    }

    private void updateOrderButtonsState() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow < 0) {
            sendOrderButton.setEnabled(false);
            viewOrderButton.setEnabled(false);
            return;
        }

        int modelRow = orderTable.convertRowIndexToModel(selectedRow);
        if (modelRow < 0 || modelRow >= orders.size()) {
            sendOrderButton.setEnabled(false);
            viewOrderButton.setEnabled(false);
            return;
        }

        SalesService.SalesOrder selected = orders.get(modelRow);
        SalesService.OrderStatus current = selected.status != null
            ? selected.status
            : SalesService.OrderStatus.CREATED;

        sendOrderButton.setEnabled(current == SalesService.OrderStatus.CREATED);
        viewOrderButton.setEnabled(true);
    }

    private void viewQuotationDetails() {
        int selectedRow = quotationTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select a quotation to view.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int modelRow = quotationTable.convertRowIndexToModel(selectedRow);
        if (modelRow < 0 || modelRow >= quotations.size()) {
            JOptionPane.showMessageDialog(this,
                "Invalid selection.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            SalesService.Quotation quotation = quotations.get(modelRow);
            Frame parent = JOptionPane.getFrameForComponent(this);
            OrderDetailsDialog dialog = new OrderDetailsDialog(parent, quotation);
            dialog.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error viewing quotation details: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewOrderDetails() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                "Please select an order to view.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int modelRow = orderTable.convertRowIndexToModel(selectedRow);
        if (modelRow < 0 || modelRow >= orders.size()) {
            JOptionPane.showMessageDialog(this,
                "Invalid selection.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            SalesService.SalesOrder order = orders.get(modelRow);
            Frame parent = JOptionPane.getFrameForComponent(this);
            OrderDetailsDialog dialog = new OrderDetailsDialog(parent, order);
            dialog.setVisible(true);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error viewing order details: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

}

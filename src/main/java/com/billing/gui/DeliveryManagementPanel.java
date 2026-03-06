package com.billing.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import com.billing.model.document.DeliveryNote;
import com.billing.service.DeliveryService;
import com.billing.service.SalesService;

/**
 * Delivery workflow panel (NOT CRUD)
 * Same visual style as other modules but process-oriented behaviour
 */
public class DeliveryManagementPanel extends JPanel {

    private final DeliveryService deliveryService;

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> sortComboBox;

    private JButton createButton;
    private JButton deliveredButton;
    private JButton invoiceButton;
    private JButton viewButton;
    private JButton refreshButton;
    private JButton searchButton;
    private JButton clearButton;

    private List<DeliveryService.DeliveryRow> currentRows = java.util.Collections.emptyList();

    private final String[] columnNames = {
        "Code", "Date", "Customer", "Lines", "Total", "Delivered", "Pending Invoice"
    };

    public DeliveryManagementPanel(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
        initializeComponents();
        setupLayout();
        setupEvents();
        loadDeliveries();
    }

    private void initializeComponents() {

        searchField = new JTextField(20);
        searchField.setFont(UIConstants.UI_FONT);

        sortComboBox = new JComboBox<>(new String[]{
            "Sort by Code", "Sort by Date", "Sort by Customer", "Sort by Status"
        });
        sortComboBox.setFont(UIConstants.UI_FONT);
        sortComboBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        createButton = UIConstants.createSuccessButton("ADD", UIConstants.loadIcon("/icons/add.png",16));
        deliveredButton = UIConstants.createPrimaryButton("DELIVERED", UIConstants.loadIcon("/icons/delivery.png",16));
        invoiceButton = UIConstants.createPrimaryButton("INVOICE", UIConstants.loadIcon("/icons/next.png",16));
        viewButton = UIConstants.createSecondaryButton("DETAILS", UIConstants.loadIcon("/icons/details.png",16));
        refreshButton = UIConstants.createSecondaryButton("REFRESH", UIConstants.loadIcon("/icons/refresh.png",16));
        searchButton = UIConstants.createSecondaryButton("SEARCH", UIConstants.loadIcon("/icons/search.png",16));
        clearButton = UIConstants.createSecondaryButton("CLEAR", UIConstants.loadIcon("/icons/clear.png",16));

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
            @Override public Class<?> getColumnClass(int col) {
                return switch(col){
                    case 4 -> BigDecimal.class;
                    default -> String.class;
                };
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(UIConstants.UI_FONT);
        table.getTableHeader().setFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        table.getTableHeader().setOpaque(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getTableHeader().setReorderingAllowed(false);
        table.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        table.getTableHeader().setCursor(Cursor.getDefaultCursor());
        table.setAutoCreateRowSorter(false);

        styleTable();
        setColumnWidths();
    }

    private void styleTable() {
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c){
                JLabel l=(JLabel)super.getTableCellRendererComponent(t,v,s,f,r,c);
                l.setHorizontalAlignment(SwingConstants.CENTER);
                l.setBackground(UIConstants.MODULE_DELIVERY_TEXT);
                l.setForeground(Color.WHITE);
                l.setFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
                l.setOpaque(true);
                l.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
                return l;
            }
        };
        table.getTableHeader().setDefaultRenderer(headerRenderer);

        DefaultTableCellRenderer rowRenderer = new DefaultTableCellRenderer(){
            private final Color EVEN=Color.WHITE;
            private final Color ODD=UIConstants.MODULE_DELIVERY_BG;
            @Override
            public Component getTableCellRendererComponent(JTable t,Object v,boolean s,boolean f,int r,int c){
                JLabel l=(JLabel)super.getTableCellRendererComponent(t,v,s,f,r,c);
                if(s){ l.setBackground(UIConstants.MODULE_DELIVERY_ICON); l.setForeground(Color.WHITE);}
                else{ l.setBackground(r%2==0?EVEN:ODD); l.setForeground(new Color(0x212121));}
                return l;
            }
        };
        table.setDefaultRenderer(Object.class,rowRenderer);
        table.setDefaultRenderer(Number.class,rowRenderer);
    }

    private void setColumnWidths() {
        int[] widths = {100, 110, 200, 70, 90, 90, 130};
        for (int i = 0; i < widths.length && i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }
    }

    private void setupLayout() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setBackground(UIConstants.MODULE_DELIVERY_BG);

        add(createHeader(), BorderLayout.NORTH);
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setPreferredSize(new Dimension(0, 400));
        UIConstants.enhanceScroll(tableScrollPane);
        add(tableScrollPane, BorderLayout.CENTER);
        add(createBottom(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel main=new JPanel(new BorderLayout());
        main.setBackground(UIConstants.MODULE_DELIVERY_BG);

        JLabel title=new JLabel("Delivery Notes Management",SwingConstants.CENTER);
        title.setFont(UIConstants.TITLE_FONT.deriveFont(24f));
        title.setForeground(Color.WHITE);

        JPanel header=new JPanel(new BorderLayout());
        header.setBackground(UIConstants.MODULE_DELIVERY_TEXT);
        header.setPreferredSize(new Dimension(0,80));
        header.add(title,BorderLayout.CENTER);

        JPanel controls=new JPanel(new BorderLayout());
        controls.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
        controls.setOpaque(false);

        JPanel leftControls=new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftControls.setOpaque(false);
        leftControls.add(new JLabel("Search:"));
        leftControls.add(searchField);
        leftControls.add(searchButton);
        leftControls.add(clearButton);
        leftControls.add(Box.createHorizontalStrut(20));
        leftControls.add(new JLabel("Sort:"));
        leftControls.add(sortComboBox);

        JPanel rightControls=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightControls.setOpaque(false);
        rightControls.add(createButton);
        rightControls.add(refreshButton);

        controls.add(leftControls, BorderLayout.WEST);
        controls.add(rightControls, BorderLayout.EAST);

        main.add(header,BorderLayout.NORTH);
        main.add(controls,BorderLayout.CENTER);
        return main;
    }

    private JPanel createBottom() {
        JPanel p=new JPanel(new FlowLayout());
        p.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        p.setBackground(UIConstants.MODULE_DELIVERY_BG);
        p.add(viewButton);
        p.add(deliveredButton);
        p.add(invoiceButton);
        return p;
    }

    private void setupEvents() {
        refreshButton.addActionListener(e->loadDeliveries());
        searchButton.addActionListener(e->performSearch());
        clearButton.addActionListener(e->{searchField.setText("");loadDeliveries();});
        sortComboBox.addActionListener(e->applySorting());
        createButton.addActionListener(e->createDelivery());
        deliveredButton.addActionListener(e->markDelivered());
        invoiceButton.addActionListener(e->generateInvoice());
        viewButton.addActionListener(e->viewDetails());

        table.addMouseListener(new MouseAdapter(){
            @Override public void mouseClicked(MouseEvent e){
                if(e.getClickCount()==2) viewDetails();
            }
        });
    }

    private void loadDeliveries() {
        currentRows = deliveryService.listTrackingRows();
        refreshTable(currentRows);
    }

    public void reloadDeliveries() {
        loadDeliveries();
    }

    private void refreshTable(List<DeliveryService.DeliveryRow> rows){
        tableModel.setRowCount(0);
        for(var d:rows){
            tableModel.addRow(new Object[]{
                d.number,d.date,d.customer,d.items,d.total,
                d.delivered?"Yes":"No",
                d.pendingInvoice?"Yes":"No"
            });
        }
    }

    private Long getSelectedId(){
        int row=table.getSelectedRow();
        if(row<0){JOptionPane.showMessageDialog(this,"Select a delivery");return null;}
        int modelRow = table.convertRowIndexToModel(row);
        if (modelRow >= 0 && modelRow < currentRows.size()) {
            return currentRows.get(modelRow).id;
        }
        return null;
    }

    private void createDelivery(){
        try {
            Long orderId = chooseOrderId();
            if (orderId == null) return;
            DeliveryNote deliveryNote = deliveryService.createFromOrder(orderId);
            loadDeliveries();
            String code = deliveryNote != null && deliveryNote.getCode() != null
                ? deliveryNote.getCode()
                : (deliveryNote != null && deliveryNote.getId() != null
                    ? String.format("DN%04d", deliveryNote.getId())
                    : "(unknown)");
            JOptionPane.showMessageDialog(this, "Delivery note created: " + code);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error creating delivery note: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private Long chooseOrderId() {
        List<SalesService.SalesOrder> orders = deliveryService.listOrders();
        if (orders == null || orders.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No orders available. Confirm an order first.",
                "Warning",
                JOptionPane.WARNING_MESSAGE);
            return null;
        }

        String[] options = orders.stream()
            .map(o -> String.format("%s | %s | Total %s", o.code, o.customer != null ? o.customer.getName() : "", o.total))
            .toArray(String[]::new);

        Object selected = JOptionPane.showInputDialog(
            this,
            "Select order:",
            "Create Delivery",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        if (selected == null) return null;
        int index = Arrays.asList(options).indexOf(selected.toString());
        if (index < 0 || index >= orders.size()) return null;
        return orders.get(index).id.longValue();
    }

    private void markDelivered(){
        Long id=getSelectedId();
        if(id!=null){deliveryService.markDelivered(id);loadDeliveries();}
    }

    private void generateInvoice(){
        Long id=getSelectedId();
        if(id==null) return;
        
        try {
            com.billing.model.document.Invoice invoice = deliveryService.generateInvoice(id);
            loadDeliveries();
            String code = invoice != null && invoice.getCode() != null
                ? invoice.getCode()
                : (invoice != null && invoice.getId() != null
                    ? String.format("INV%04d", invoice.getId())
                    : "(unknown)");
            JOptionPane.showMessageDialog(this,
                "Invoice created: " + code,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error generating invoice: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void viewDetails(){
        Long id=getSelectedId();
        if(id==null) {
            JOptionPane.showMessageDialog(this,
                "Please select a delivery note to view.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            DeliveryNote deliveryNote = deliveryService.findById(id);
            if (deliveryNote == null) {
                JOptionPane.showMessageDialog(this,
                    "Delivery note not found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Frame parent = JOptionPane.getFrameForComponent(this);
            DeliveryNoteDetailsDialog dialog = new DeliveryNoteDetailsDialog(parent, deliveryNote);
            dialog.setVisible(true);
        } catch (RuntimeException re) {
            JOptionPane.showMessageDialog(this,
                "Error viewing delivery note: " + re.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performSearch(){
        refreshTable(deliveryService.search(searchField.getText()));
    }

    private void applySorting(){
        refreshTable(deliveryService.sort(sortComboBox.getSelectedIndex()));
    }
}

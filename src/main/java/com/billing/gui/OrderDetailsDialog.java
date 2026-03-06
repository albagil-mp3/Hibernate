package com.billing.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import com.billing.model.document.DocumentLine;
import com.billing.model.party.Client;
import com.billing.service.SalesService;

/**
 * Dialog for displaying quotation or sales order details in a read-only format
 */
public class OrderDetailsDialog extends JDialog {
    
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.00 €");
    private static final DecimalFormat QUANTITY_FORMAT = new DecimalFormat("#,##0.00");
    
    private final String code;
    private final LocalDate date;
    private final Client customer;
    private final List<DocumentLine> lines;
    private final BigDecimal subtotal;
    private final BigDecimal taxes;
    private final BigDecimal total;
    private final String documentType;
    private final String status;
    private final Color themeColor;
    
    public OrderDetailsDialog(Frame parent, SalesService.Quotation quotation) {
        super(parent, "Quotation Details - " + quotation.code, true);
        this.code = quotation.code;
        this.date = quotation.date;
        this.status = quotation.status != null ? quotation.status.name() : "DRAFT";
        this.customer = quotation.customer;
        this.lines = quotation.lines;
        this.subtotal = quotation.subtotal;
        this.taxes = quotation.taxes;
        this.total = quotation.total;
        this.documentType = "Quotation";
        this.themeColor = UIConstants.MODULE_ORDERS_TEXT;
        initializeDialog();
    }
    
    public OrderDetailsDialog(Frame parent, SalesService.SalesOrder order) {
        super(parent, "Sales Order Details - " + order.code, true);
        this.code = order.code;
        this.date = order.date;
        this.status = order.status != null ? order.status.name() : "CREATED";
        this.customer = order.customer;
        this.lines = order.lines;
        this.subtotal = order.subtotal;
        this.taxes = order.taxes;
        this.total = order.total;
        this.documentType = "Sales Order";
        this.themeColor = UIConstants.MODULE_ORDERS_ICON;
        initializeDialog();
    }
    
    private void initializeDialog() {
        setLayout(new BorderLayout());
        add(createContentPanel(), BorderLayout.CENTER);
        setSize(800, 600);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private JPanel createContentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(0xF5F5F5));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JPanel header = createHeaderPanel();
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        header.setMaximumSize(new Dimension(Integer.MAX_VALUE, header.getPreferredSize().height));
        infoPanel.add(header);
        infoPanel.add(Box.createVerticalStrut(12));

        JPanel linesPanel = createLinesPanel();
        linesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(linesPanel);
        infoPanel.add(Box.createVerticalStrut(12));

        JPanel totals = createTotalsPanel();
        totals.setAlignmentX(Component.LEFT_ALIGNMENT);
        totals.setMaximumSize(new Dimension(Integer.MAX_VALUE, totals.getPreferredSize().height));
        infoPanel.add(totals);

        JScrollPane scrollPane = new JScrollPane(infoPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder(documentType + " Information");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(themeColor);
        panel.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(8,12,8,12)));
        panel.setBackground(UIConstants.MODULE_ORDERS_BG);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 12, 6, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = UIConstants.UI_FONT.deriveFont(Font.BOLD);
        Font valueFont = UIConstants.UI_FONT;

        int y = 0;

        // Document Type
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setFont(labelFont);
        typeLabel.setForeground(new Color(0x212121));
        panel.add(typeLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        JLabel typeValue = new JLabel(documentType);
        typeValue.setFont(valueFont.deriveFont(Font.BOLD));
        typeValue.setForeground(themeColor);
        panel.add(typeValue, gbc);

        // Code
        gbc.gridx = 2; gbc.weightx = 0.0;
        JLabel codeLabel = new JLabel("Code:");
        codeLabel.setFont(labelFont);
        codeLabel.setForeground(new Color(0x212121));
        panel.add(codeLabel, gbc);
        
        gbc.gridx = 3; gbc.weightx = 1.0;
        JLabel codeValue = new JLabel(code != null ? code : "N/A");
        codeValue.setFont(valueFont);
        panel.add(codeValue, gbc);
        y++;

        // Date
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(labelFont);
        dateLabel.setForeground(new Color(0x212121));
        panel.add(dateLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        JLabel dateValue = new JLabel(date != null ? date.toString() : "N/A");
        dateValue.setFont(valueFont);
        panel.add(dateValue, gbc);

        // Customer
        gbc.gridx = 2; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel customerLabel = new JLabel("Customer:");
        customerLabel.setFont(labelFont);
        customerLabel.setForeground(new Color(0x212121));
        panel.add(customerLabel, gbc);
        
        gbc.gridx = 3; gbc.weightx = 1.0;
        String customerName = customer != null ? customer.getName() : "N/A";
        JLabel customerValue = new JLabel(customerName);
        customerValue.setFont(valueFont);
        panel.add(customerValue, gbc);

        y++;

        // Status
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(labelFont);
        statusLabel.setForeground(new Color(0x212121));
        panel.add(statusLabel, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        JLabel statusValue = new JLabel(status != null ? status : "N/A");
        statusValue.setFont(valueFont.deriveFont(Font.BOLD));
        statusValue.setForeground(themeColor);
        panel.add(statusValue, gbc);
        
        return panel;
    }
    
    private JPanel createLinesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Items");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(themeColor);
        panel.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(8,8,8,8)));
        panel.setBackground(Color.WHITE);
        
        String[] columnNames = {"Item", "Description", "Quantity", "Unit Price", "Discount %", "Tax %", "Line Total"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        if (lines != null) {
            for (DocumentLine line : lines) {
                String itemName = line.getItem() != null ? line.getItem().getName() : "N/A";
                String description = line.getItem() != null && line.getItem().getDescription() != null ? 
                    line.getItem().getDescription() : "";
                BigDecimal taxRate = line.getTaxRate() != null ? line.getTaxRate() : BigDecimal.ZERO;
                String taxPercent = taxRate.multiply(new BigDecimal("100")).stripTrailingZeros().toPlainString();
                Object[] row = new Object[]{
                    itemName,
                    description,
                    QUANTITY_FORMAT.format(line.getQuantity() != null ? line.getQuantity() : 0),
                    CURRENCY_FORMAT.format(line.getUnitPrice() != null ? line.getUnitPrice() : BigDecimal.ZERO),
                    "0",
                    taxPercent,
                    CURRENCY_FORMAT.format(line.lineTotal())
                };
                model.addRow(row);
            }
        }
        
        JTable table = new JTable(model);
        table.setFont(UIConstants.UI_FONT);
        table.setRowHeight(28);
        table.getTableHeader().setFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        table.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(750, 200));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTotalsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Totals");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(themeColor);
        panel.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(8,12,8,12)));
        panel.setBackground(UIConstants.MODULE_ORDERS_BG);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 12, 6, 12);
        gbc.anchor = GridBagConstraints.EAST;

        Font labelFont = UIConstants.UI_FONT.deriveFont(Font.BOLD);
        Font valueFont = UIConstants.UI_FONT;
        Font totalFont = UIConstants.UI_FONT.deriveFont(Font.BOLD, 14f);

        int y = 0;

        // Subtotal
        gbc.gridx = 0; gbc.gridy = y;
        JLabel subtotalLabel = new JLabel("Subtotal:");
        subtotalLabel.setFont(labelFont);
        panel.add(subtotalLabel, gbc);
        
        gbc.gridx = 1;
        JLabel subtotalValue = new JLabel(CURRENCY_FORMAT.format(
            subtotal != null ? subtotal : BigDecimal.ZERO));
        subtotalValue.setFont(valueFont);
        panel.add(subtotalValue, gbc);
        y++;

        // Taxes
        gbc.gridx = 0; gbc.gridy = y;
        JLabel taxesLabel = new JLabel("Taxes:");
        taxesLabel.setFont(labelFont);
        panel.add(taxesLabel, gbc);
        
        gbc.gridx = 1;
        JLabel taxesValue = new JLabel(CURRENCY_FORMAT.format(
            taxes != null ? taxes : BigDecimal.ZERO));
        taxesValue.setFont(valueFont);
        panel.add(taxesValue, gbc);
        y++;

        // Total
        gbc.gridx = 0; gbc.gridy = y;
        gbc.insets = new Insets(12, 12, 6, 12);
        JLabel totalLabel = new JLabel("TOTAL:");
        totalLabel.setFont(totalFont);
        totalLabel.setForeground(themeColor);
        panel.add(totalLabel, gbc);
        
        gbc.gridx = 1;
        JLabel totalValue = new JLabel(CURRENCY_FORMAT.format(
            total != null ? total : BigDecimal.ZERO));
        totalValue.setFont(totalFont);
        totalValue.setForeground(themeColor);
        panel.add(totalValue, gbc);
        
        return panel;
    }
}

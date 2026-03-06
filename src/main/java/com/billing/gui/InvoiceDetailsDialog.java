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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import com.billing.model.document.Invoice;
import com.billing.model.document.DocumentLine;

/**
 * Dialog for displaying complete invoice details in a read-only format
 */
public class InvoiceDetailsDialog extends BaseDetailsDialog<Invoice> {
    
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.00 €");
    private static final DecimalFormat QUANTITY_FORMAT = new DecimalFormat("#,##0.00");
    
    public InvoiceDetailsDialog(Frame parent, Invoice invoice) {
        super(parent, invoice, "Invoice Details - " + 
            (invoice.getCode() != null ? invoice.getCode() : "INV-" + invoice.getId()));
    }
    
    @Override
    protected JPanel createContentPanel() {
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

        JPanel lines = createLinesPanel();
        lines.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.add(lines);
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
        TitledBorder border = BorderFactory.createTitledBorder("Invoice Information");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.MODULE_INVOICES_TEXT);
        panel.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(8,12,8,12)));
        panel.setBackground(UIConstants.MODULE_INVOICES_BG);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 12, 6, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = UIConstants.UI_FONT.deriveFont(Font.BOLD);
        Font valueFont = UIConstants.UI_FONT;

        int y = 0;

        // Code
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel codeLabel = new JLabel("Invoice No:");
        codeLabel.setFont(labelFont);
        codeLabel.setForeground(new Color(0x212121));
        panel.add(codeLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        JLabel codeValue = new JLabel(entity.getCode() != null ? entity.getCode() : "INV-" + entity.getId());
        codeValue.setFont(valueFont.deriveFont(Font.BOLD));
        codeValue.setForeground(UIConstants.MODULE_INVOICES_TEXT);
        panel.add(codeValue, gbc);

        // Date
        gbc.gridx = 2; gbc.weightx = 0.0;
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(labelFont);
        dateLabel.setForeground(new Color(0x212121));
        panel.add(dateLabel, gbc);
        
        gbc.gridx = 3; gbc.weightx = 1.0;
        JLabel dateValue = new JLabel(entity.getDate() != null ? entity.getDate().toString() : "N/A");
        dateValue.setFont(valueFont);
        panel.add(dateValue, gbc);
        y++;

        // Customer
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.0;
        gbc.gridwidth = 1;
        JLabel customerLabel = new JLabel("Customer:");
        customerLabel.setFont(labelFont);
        customerLabel.setForeground(new Color(0x212121));
        panel.add(customerLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        gbc.gridwidth = 3;
        String customerName = entity.getCustomer() != null ? entity.getCustomer().getName() : "N/A";
        JLabel customerValue = new JLabel(customerName);
        customerValue.setFont(valueFont);
        panel.add(customerValue, gbc);
        
        return panel;
    }
    
    private JPanel createLinesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Invoice Items");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.MODULE_INVOICES_TEXT);
        panel.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(8,8,8,8)));
        panel.setBackground(Color.WHITE);
        
        String[] columnNames = {"Item", "Description", "Quantity", "Unit Price", "Discount %", "Tax %", "Line Total"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        if (entity.getLines() != null) {
            for (DocumentLine line : entity.getLines()) {
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
        TitledBorder border = BorderFactory.createTitledBorder("Invoice Totals");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.MODULE_INVOICES_TEXT);
        panel.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(8,12,8,12)));
        panel.setBackground(UIConstants.MODULE_INVOICES_BG);
        
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
            entity.getSubtotal() != null ? entity.getSubtotal() : BigDecimal.ZERO));
        subtotalValue.setFont(valueFont);
        panel.add(subtotalValue, gbc);
        y++;

        // Taxes
        gbc.gridx = 0; gbc.gridy = y;
        JLabel taxesLabel = new JLabel("VAT:");
        taxesLabel.setFont(labelFont);
        panel.add(taxesLabel, gbc);
        
        gbc.gridx = 1;
        JLabel taxesValue = new JLabel(CURRENCY_FORMAT.format(
            entity.getTaxes() != null ? entity.getTaxes() : BigDecimal.ZERO));
        taxesValue.setFont(valueFont);
        panel.add(taxesValue, gbc);
        y++;

        // Total
        gbc.gridx = 0; gbc.gridy = y;
        gbc.insets = new Insets(12, 12, 6, 12);
        JLabel totalLabel = new JLabel("TOTAL:");
        totalLabel.setFont(totalFont);
        totalLabel.setForeground(UIConstants.MODULE_INVOICES_TEXT);
        panel.add(totalLabel, gbc);
        
        gbc.gridx = 1;
        JLabel totalValue = new JLabel(CURRENCY_FORMAT.format(
            entity.getTotal() != null ? entity.getTotal() : BigDecimal.ZERO));
        totalValue.setFont(totalFont);
        totalValue.setForeground(UIConstants.MODULE_INVOICES_TEXT);
        panel.add(totalValue, gbc);
        
        return panel;
    }

    @Override
    protected void configureDialog() {
        // Make this dialog wider/taller so the invoice table is fully visible
        setMinimumSize(new Dimension(900, 600));
        setSize(950, 650);
        setLocationRelativeTo(getParent());
    }
}

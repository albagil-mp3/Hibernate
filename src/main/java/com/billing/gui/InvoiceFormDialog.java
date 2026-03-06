package com.billing.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import com.billing.model.document.DocumentLine;
import com.billing.model.document.Invoice;
import com.billing.model.item.Item;
import com.billing.model.party.Client;
import com.billing.service.ClientService;
import com.billing.service.ItemService;
import com.billing.service.SalesService;

/**
 * Dialog to create a direct invoice (without preceding delivery note)
 */
public class InvoiceFormDialog extends JDialog {

    private final ClientService clientService;
    private final ItemService itemService;
    private final SalesService salesService;

    private boolean saved = false;
    private Invoice createdInvoice;

    private JComboBox<Client> clientCombo;
    private JComboBox<Item> itemCombo;
    private JSpinner qtySpinner;
    private JTable linesTable;
    private DefaultTableModel linesModel;
    private final List<DocumentLine> lines = new ArrayList<>();

    public InvoiceFormDialog(Window owner,
                            ClientService clientService,
                            ItemService itemService,
                            SalesService salesService) {
        super(owner, "Create Direct Invoice", ModalityType.APPLICATION_MODAL);
        this.clientService = clientService;
        this.itemService = itemService;
        this.salesService = salesService;
        initialize();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initialize() {
        setLayout(new BorderLayout(8,8));
        
        // Top panel - client selection
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));
        top.setBackground(UIConstants.MODULE_INVOICES_BG);
        
        JLabel clientLabel = new JLabel("Customer:");
        clientLabel.setFont(UIConstants.UI_FONT);
        top.add(clientLabel);
        
        clientCombo = new JComboBox<>();
        clientCombo.setFont(UIConstants.UI_FONT);
        clientCombo.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            java.awt.Component c = new javax.swing.JLabel(
                value == null ? "" : value.getCode() + " - " + value.getName()
            );
            if (isSelected) {
                c.setBackground(list.getSelectionBackground());
                c.setForeground(list.getSelectionForeground());
            } else {
                c.setBackground(list.getBackground());
                c.setForeground(list.getForeground());
            }
            ((javax.swing.JLabel)c).setOpaque(true);
            return c;
        });
        List<Client> clients = clientService.getAllClients();
        for (Client c : clients) {
            clientCombo.addItem(c);
        }
        top.add(clientCombo);
        
        add(top, BorderLayout.NORTH);

        // Center panel - lines editor
        JPanel center = new JPanel(new BorderLayout(6,6));
        center.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        
        JPanel lineEditor = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lineEditor.setBackground(UIConstants.MODULE_INVOICES_BG);
        
        JLabel itemLabel = new JLabel("Item:");
        itemLabel.setFont(UIConstants.UI_FONT);
        lineEditor.add(itemLabel);
        
        itemCombo = new JComboBox<>();
        itemCombo.setFont(UIConstants.UI_FONT);
        itemCombo.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            java.awt.Component c = new javax.swing.JLabel(
                value == null ? "" : value.getCode() + " - " + value.getDescription()
            );
            if (isSelected) {
                c.setBackground(list.getSelectionBackground());
                c.setForeground(list.getSelectionForeground());
            } else {
                c.setBackground(list.getBackground());
                c.setForeground(list.getForeground());
            }
            ((javax.swing.JLabel)c).setOpaque(true);
            return c;
        });
        List<Item> items = itemService.getAllItems();
        for (Item it : items) {
            itemCombo.addItem(it);
        }
        lineEditor.add(itemCombo);
        
        JLabel qtyLabel = new JLabel("Quantity:");
        qtyLabel.setFont(UIConstants.UI_FONT);
        lineEditor.add(qtyLabel);
        
        qtySpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.01, 9999.99, 1.0));
        qtySpinner.setFont(UIConstants.UI_FONT);
        lineEditor.add(qtySpinner);
        
        JButton addLineBtn = UIConstants.createSuccessButton("ADD LINE", UIConstants.loadIcon("/icons/add.png", 16));
        addLineBtn.addActionListener(e -> addLine());
        lineEditor.add(addLineBtn);
        
        JButton removeLineBtn = UIConstants.createDangerButton("REMOVE", UIConstants.loadIcon("/icons/delete.png", 16));
        removeLineBtn.addActionListener(e -> removeLine());
        lineEditor.add(removeLineBtn);
        
        center.add(lineEditor, BorderLayout.NORTH);

        // Lines table
        String[] cols = {"Item", "Description", "Quantity", "Unit Price", "VAT %"};
        linesModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        linesTable = new JTable(linesModel);
        linesTable.setFont(UIConstants.UI_FONT);
        linesTable.setRowHeight(28);
        linesTable.getTableHeader().setFont(UIConstants.UI_FONT);
        linesTable.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(linesTable);
        center.add(scrollPane, BorderLayout.CENTER);
        
        add(center, BorderLayout.CENTER);

        // Bottom panel - actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));
        
        JButton saveBtn = UIConstants.createPrimaryButton("CREATE INVOICE", UIConstants.loadIcon("/icons/save.png", 16));
        saveBtn.addActionListener(e -> onSave());
        
        JButton cancelBtn = UIConstants.createSecondaryButton("CANCEL", UIConstants.loadIcon("/icons/cancel.png", 16));
        cancelBtn.addActionListener(e -> onCancel());
        
        actions.add(cancelBtn);
        actions.add(saveBtn);
        
        add(actions, BorderLayout.SOUTH);
        
        setMinimumSize(new java.awt.Dimension(750, 450));
    }

    private void addLine() {
        Item item = (Item) itemCombo.getSelectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Please select an item", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        double qty = ((Number) qtySpinner.getValue()).doubleValue();
        
        DocumentLine line = new DocumentLine();
        line.setItem(item);
        line.setQuantity((int) Math.round(qty));
        line.setUnitPrice(item.getSalePrice());
        line.setTaxRate(new BigDecimal("0.21")); // Default 21% VAT
        
        lines.add(line);
        
        linesModel.addRow(new Object[]{
            item.getName(),
            item.getDescription(),
            qty,
            item.getSalePrice(),
            "21"
        });
    }

    private void removeLine() {
        int selectedRow = linesTable.getSelectedRow();
        if (selectedRow >= 0 && selectedRow < lines.size()) {
            lines.remove(selectedRow);
            linesModel.removeRow(selectedRow);
        }
    }

    private void onSave() {
        try {
            Client client = (Client) clientCombo.getSelectedItem();
            if (client == null) {
                JOptionPane.showMessageDialog(this,
                    "Please select a customer",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (lines.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please add at least one item",
                    "Validation Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            createdInvoice = salesService.createDirectInvoice(client, lines);
            saved = true;
            
            JOptionPane.showMessageDialog(this,
                "Invoice " + createdInvoice.getCode() + " created successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error creating invoice: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void onCancel() {
        saved = false;
        dispose();
    }

    public boolean isSaved() {
        return saved;
    }

    public Invoice getCreatedInvoice() {
        return createdInvoice;
    }
}

package com.billing.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Window;
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
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import com.billing.model.document.DeliveryNote;
import com.billing.model.document.DocumentLine;
import com.billing.model.item.Item;
import com.billing.model.party.Client;
import com.billing.service.ClientService;
import com.billing.service.DeliveryNoteService;
import com.billing.service.ItemService;

/**
 * Simple dialog to create a Delivery Note (basic fields + lines)
 */
public class DeliveryNoteFormDialog extends JDialog {

    private final ClientService clientService;
    private final ItemService itemService;
    private final DeliveryNoteService deliveryNoteService;

    private boolean saved = false;

    private JTextField codeField;
    private JComboBox<Client> clientCombo;
    private JComboBox<Item> itemCombo;
    private JSpinner qtySpinner;
    private JTable linesTable;
    private DefaultTableModel linesModel;
    private final List<DocumentLine> lines = new ArrayList<>();

    public DeliveryNoteFormDialog(Window owner,
                                  ClientService clientService,
                                  ItemService itemService,
                                  DeliveryNoteService deliveryNoteService) {
        super(owner, "Create Delivery Note", ModalityType.APPLICATION_MODAL);
        this.clientService = clientService;
        this.itemService = itemService;
        this.deliveryNoteService = deliveryNoteService;
        initialize();
        pack();
    }

    private void initialize() {
        setLayout(new BorderLayout(8,8));
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setBorder(BorderFactory.createEmptyBorder(8,8,0,8));
        top.add(new JLabel("Code:"));
        codeField = new JTextField(18);
        top.add(codeField);
        top.add(new JLabel("Client:"));
        clientCombo = new JComboBox<>();
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
        for (Client c : clientService.getAllClients()) clientCombo.addItem(c);
        top.add(clientCombo);
        add(top, BorderLayout.NORTH);

        // Lines editor
        JPanel center = new JPanel(new BorderLayout(6,6));
        JPanel lineEditor = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itemCombo = new JComboBox<>();
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
        for (Item it : itemService.getAllItems()) itemCombo.addItem(it);
        qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        JButton addLineBtn = new JButton("Add Line");
        addLineBtn.addActionListener(e -> addLine());
        lineEditor.add(new JLabel("Item:"));
        lineEditor.add(itemCombo);
        lineEditor.add(new JLabel("Qty:"));
        lineEditor.add(qtySpinner);
        lineEditor.add(addLineBtn);
        center.add(lineEditor, BorderLayout.NORTH);

        String[] cols = {"Item","Qty"};
        linesModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r,int c){return false;} };
        linesTable = new JTable(linesModel);
        center.add(new JScrollPane(linesTable), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        // Actions
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton ok = UIConstants.createPrimaryButton("Save", UIConstants.loadIcon("/icons/save.png", 16));
        JButton cancel = UIConstants.createDangerButton("Cancel", UIConstants.loadIcon("/icons/cancel.png", 16));
        ok.addActionListener(e -> onSave());
        cancel.addActionListener(e -> onCancel());
        actions.add(ok);
        actions.add(cancel);
        add(actions, BorderLayout.SOUTH);
    }

    private void addLine() {
        Item it = (Item) itemCombo.getSelectedItem();
        int qty = (Integer) qtySpinner.getValue();
        if (it == null) return;
        DocumentLine dl = new DocumentLine();
        dl.setItem(it);
        dl.setQuantity(qty);
        lines.add(dl);
        linesModel.addRow(new Object[]{ it.getName(), qty });
    }

    private void onSave() {
        try {
            DeliveryNote dn = new DeliveryNote();
            dn.setCode(codeField.getText() == null || codeField.getText().isBlank() ? null : codeField.getText().trim());
            dn.setClient((Client) clientCombo.getSelectedItem());
            dn.setLines(lines);
            deliveryNoteService.createDeliveryNote(dn);
            saved = true;
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving delivery note: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onCancel() {
        saved = false;
        dispose();
    }

    public boolean isSaved() { return saved; }
}

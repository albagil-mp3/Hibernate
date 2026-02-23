package com.billing.gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.billing.model.SpanishProvince;
import com.billing.model.party.Supplier;
import com.billing.service.SupplierService;

/**
 * Dialog for creating and editing supplier information.
 */
public class SupplierFormDialog extends BaseFormDialog<Supplier> {

    private final SupplierService supplierService;

    private JTextField nameField;
    private JTextField taxIdField;
    private JTextField addressField;
    private JTextField cityField;
    private JComboBox<SpanishProvince> provinceComboBox;
    private JTextField postalCodeField;

    private JTextField fixedPhoneField;
    private JTextField mobilePhoneField;
    private JTextField emailField;
    private JTextField websiteField;
    private JCheckBox activeCheckBox;

    public SupplierFormDialog(Frame parent, String title, Supplier supplier, SupplierService supplierService) {
        super(parent, title, true);
        this.entity = supplier;
        this.supplierService = supplierService;

        initializeComponents();
        setupLayout();
        setupFormLayout();
        setupEventHandlers();

        if (supplier != null) {
            populateFields();
        }

        configureDialog();
    }

    private void initializeComponents() {
        Font fieldFont = UIConstants.UI_FONT;

        nameField = new JTextField(30);
        nameField.setFont(fieldFont);

        taxIdField = new JTextField(20);
        taxIdField.setFont(fieldFont);

        addressField = new JTextField(30);
        addressField.setFont(fieldFont);

        cityField = new JTextField(20);
        cityField.setFont(fieldFont);

        provinceComboBox = new JComboBox<>(SpanishProvince.values());
        provinceComboBox.setFont(fieldFont);

        postalCodeField = new JTextField(6);
        postalCodeField.setFont(fieldFont);

        fixedPhoneField = new JTextField(12);
        fixedPhoneField.setFont(fieldFont);

        mobilePhoneField = new JTextField(12);
        mobilePhoneField.setFont(fieldFont);

        emailField = new JTextField(25);
        emailField.setFont(fieldFont);

        websiteField = new JTextField(25);
        websiteField.setFont(fieldFont);

        activeCheckBox = new JCheckBox("Active");
        activeCheckBox.setFont(fieldFont);
        activeCheckBox.setSelected(true);
    }

    private void setupLayout() {
        getContentPane().setBackground(UIConstants.MODULE_SUPPLIERS_BG);
    }

    @Override
    protected java.awt.Component createFormPanel() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.DEFAULT_FONT);
        tabbedPane.setBackground(UIConstants.MODULE_SUPPLIERS_BG);
        tabbedPane.setFocusable(false);

        tabbedPane.addTab("Basic Information", createBasicInfoPanel());
        tabbedPane.addTab("Contact Information", createContactInfoPanel());

        return tabbedPane;
    }

    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Basic Information");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.MODULE_SUPPLIERS_TEXT);
        panel.setBorder(border);
        panel.setBackground(UIConstants.MODULE_SUPPLIERS_BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabel("Name *:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(createLabel("Tax ID:"), gbc);
        gbc.gridx = 1;
        panel.add(taxIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(createLabel("Address:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(addressField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(createLabel("City:"), gbc);
        gbc.gridx = 1;
        panel.add(cityField, gbc);

        gbc.gridx = 2;
        panel.add(createLabel("Province:"), gbc);
        gbc.gridx = 3;
        panel.add(provinceComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(createLabel("Postal Code:"), gbc);
        gbc.gridx = 1;
        panel.add(postalCodeField, gbc);

        gbc.gridx = 3;
        panel.add(activeCheckBox, gbc);

        return panel;
    }

    private JPanel createContactInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Contact Information");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.MODULE_SUPPLIERS_TEXT);
        panel.setBorder(border);
        panel.setBackground(UIConstants.MODULE_SUPPLIERS_BG);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(createLabel("Fixed Phone:"), gbc);
        gbc.gridx = 1;
        panel.add(fixedPhoneField, gbc);

        gbc.gridx = 2;
        panel.add(createLabel("Mobile Phone:"), gbc);
        gbc.gridx = 3;
        panel.add(mobilePhoneField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(createLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(emailField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(createLabel("Website:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(websiteField, gbc);

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.UI_FONT);
        label.setForeground(new java.awt.Color(0x212121));
        return label;
    }

    private void setupEventHandlers() {
        postalCodeField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                String postalCode = postalCodeField.getText().trim();
                if (postalCode.length() == 5) {
                    SpanishProvince province = SpanishProvince.getByPostalCode(postalCode);
                    if (province != null) {
                        provinceComboBox.setSelectedItem(province);
                    }
                }
            }
        });
    }

    private void configureDialog() {
        getContentPane().setBackground(UIConstants.MODULE_SUPPLIERS_BG);
        setResizable(true);
        pack();
        setMinimumSize(new Dimension(640, 380));
        setLocationRelativeTo(getParent());
    }

    @Override
    protected void populateFields() {
        if (entity == null) {
            return;
        }

        nameField.setText(entity.getName() != null ? entity.getName() : "");
        taxIdField.setText(entity.getTaxId() != null ? entity.getTaxId() : "");
        addressField.setText(entity.getAddress() != null ? entity.getAddress() : "");
        cityField.setText(entity.getCity() != null ? entity.getCity() : "");
        provinceComboBox.setSelectedItem(entity.getProvince());
        postalCodeField.setText(entity.getPostalCode() != null ? entity.getPostalCode() : "");

        fixedPhoneField.setText(entity.getFixedPhone() != null ? entity.getFixedPhone() : "");
        mobilePhoneField.setText(entity.getMobilePhone() != null ? entity.getMobilePhone() : "");
        emailField.setText(entity.getEmail() != null ? entity.getEmail() : "");
        websiteField.setText(entity.getWebsite() != null ? entity.getWebsite() : "");
        activeCheckBox.setSelected(Boolean.TRUE.equals(entity.getActive()));
    }

    @Override
    protected boolean validateForm() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        return true;
    }

    @Override
    protected boolean saveEntity() {
        try {
            if (entity == null) {
                entity = new Supplier();
            }

            entity.setName(nameField.getText().trim());

            String taxId = taxIdField.getText().trim();
            entity.setTaxId(taxId.isEmpty() ? null : taxId);

            String address = addressField.getText().trim();
            entity.setAddress(address.isEmpty() ? null : address);

            String city = cityField.getText().trim();
            entity.setCity(city.isEmpty() ? null : city);

            entity.setProvince((SpanishProvince) provinceComboBox.getSelectedItem());

            String postalCode = postalCodeField.getText().trim();
            entity.setPostalCode(postalCode.isEmpty() ? null : postalCode);

            String fixedPhone = fixedPhoneField.getText().trim();
            entity.setFixedPhone(fixedPhone.isEmpty() ? null : fixedPhone);

            String mobilePhone = mobilePhoneField.getText().trim();
            entity.setMobilePhone(mobilePhone.isEmpty() ? null : mobilePhone);

            String email = emailField.getText().trim();
            entity.setEmail(email.isEmpty() ? null : email);

            String website = websiteField.getText().trim();
            entity.setWebsite(website.isEmpty() ? null : website);

            entity.setActive(activeCheckBox.isSelected());

            if (entity.getId() == null) {
                supplierService.createSupplier(entity);
                JOptionPane.showMessageDialog(this, "Supplier created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                supplierService.updateSupplier(entity);
                JOptionPane.showMessageDialog(this, "Supplier updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }

            return true;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(),
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return false;
        } catch (RuntimeException e) {
            JOptionPane.showMessageDialog(this,
                "Error saving supplier: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}

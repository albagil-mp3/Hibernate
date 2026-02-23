package com.billing.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.BigDecimal;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import com.billing.model.PaymentMethod;
import com.billing.model.SpanishProvince;
import com.billing.model.party.Client;
import com.billing.service.ClientService;

/**
 * Dialog for creating and editing client information
 * Provides comprehensive form with validation for all client fields
 */
public class ClientFormDialog extends BaseFormDialog<Client> {
    
    private final ClientService clientService;
    
    // Form fields
    private JTextField nameField;
    private JTextField dniField;
    private JTextField addressField;
    private JTextField cityField;
    private JComboBox<SpanishProvince> provinceComboBox;
    private JTextField postalCodeField;
    private JTextField fixedPhoneField;
    private JTextField mobilePhoneField;
    private JTextField emailField;
    private JTextField websiteField;
    private JComboBox<PaymentMethod> paymentMethodComboBox;
    private JTextField creditLimitField;
    private JTextField bankAccountField;
    private JCheckBox activeCheckBox;
    private JTextArea observationsArea;
    
    public ClientFormDialog(Frame parent, String title, Client client, ClientService clientService) {
        super(parent, title, true);
        this.entity = client;
        this.clientService = clientService;
        
        initializeComponents();
        setupLayout();
        setupFormLayout();
        setupEventHandlers();
        
        if (client != null) {
            populateFields();
        }
        
        configureDialog();
    }
    
    private void initializeComponents() {
        Font fieldFont = UIConstants.UI_FONT;
        
        // Basic information fields
        nameField = new JTextField(30);
        nameField.setFont(fieldFont);
        
        dniField = new JTextField(10);
        dniField.setFont(fieldFont);
        
        addressField = new JTextField(30);
        addressField.setFont(fieldFont);
        
        cityField = new JTextField(20);
        cityField.setFont(fieldFont);
        
        provinceComboBox = new JComboBox<>(SpanishProvince.values());
        provinceComboBox.setFont(fieldFont);
        
        postalCodeField = new JTextField(6);
        postalCodeField.setFont(fieldFont);
        
        // Contact information fields
        fixedPhoneField = new JTextField(12);
        fixedPhoneField.setFont(fieldFont);
        
        mobilePhoneField = new JTextField(12);
        mobilePhoneField.setFont(fieldFont);
        
        emailField = new JTextField(25);
        emailField.setFont(fieldFont);
        
        websiteField = new JTextField(25);
        websiteField.setFont(fieldFont);
        
        // Payment information fields
        paymentMethodComboBox = new JComboBox<>(PaymentMethod.values());
        paymentMethodComboBox.setFont(fieldFont);
        
        creditLimitField = new JTextField(12);
        creditLimitField.setFont(fieldFont);
        creditLimitField.setText("0.00");
        
        bankAccountField = new JTextField(25);
        bankAccountField.setFont(fieldFont);
        
        // Status and observations
        activeCheckBox = new JCheckBox("Active");
        activeCheckBox.setFont(fieldFont);
        activeCheckBox.setSelected(true);
        
        observationsArea = new JTextArea(3, 30);
        observationsArea.setFont(fieldFont);
        observationsArea.setLineWrap(true);
        observationsArea.setWrapStyleWord(true);
    }
    
    private void setupLayout() {
        getContentPane().setBackground(UIConstants.MODULE_CLIENTS_BG);
    }
    
    @Override
    protected java.awt.Component createFormPanel() {
        // Main panel with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.DEFAULT_FONT);
        tabbedPane.setBackground(UIConstants.MODULE_CLIENTS_BG);
        tabbedPane.setFocusable(false);
        
        // Basic information tab
        tabbedPane.addTab("Basic Information", createBasicInfoPanel());
        
        // Contact information tab
        tabbedPane.addTab("Contact Information", createContactInfoPanel());
        
        // Payment information tab
        tabbedPane.addTab("Payment & Observations", createPaymentObservationsPanel());
        
        return tabbedPane;
    }
    
    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Basic Information");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.MODULE_CLIENTS_TEXT);
        panel.setBorder(border);
        panel.setBackground(UIConstants.MODULE_CLIENTS_BG);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Name *:");
        nameLabel.setFont(UIConstants.UI_FONT);
        nameLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(nameLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(nameField, gbc);
        
        // DNI
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        JLabel dniLabel = new JLabel("DNI *:");
        dniLabel.setFont(UIConstants.UI_FONT);
        dniLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(dniLabel, gbc);
        gbc.gridx = 1;
        panel.add(dniField, gbc);
        
        // Address
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel addressLabel = new JLabel("Address *:");
        addressLabel.setFont(UIConstants.UI_FONT);
        addressLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(addressLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(addressField, gbc);
        
        // City and Province
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        JLabel cityLabel = new JLabel("City *:");
        cityLabel.setFont(UIConstants.UI_FONT);
        cityLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(cityLabel, gbc);
        gbc.gridx = 1;
        panel.add(cityField, gbc);
        
        gbc.gridx = 2;
        JLabel provinceLabel = new JLabel("Province *:");
        provinceLabel.setFont(UIConstants.UI_FONT);
        provinceLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(provinceLabel, gbc);
        gbc.gridx = 3;
        panel.add(provinceComboBox, gbc);
        
        // Postal Code
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel postalLabel = new JLabel("Postal Code:");
        postalLabel.setFont(UIConstants.UI_FONT);
        postalLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(postalLabel, gbc);
        gbc.gridx = 1;
        panel.add(postalCodeField, gbc);
        
        return panel;
    }
    
    private JPanel createContactInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Contact Information");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.MODULE_CLIENTS_TEXT);
        panel.setBorder(border);
        panel.setBackground(UIConstants.MODULE_CLIENTS_BG);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Phones
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel fixedPhoneLabel = new JLabel("Fixed Phone:");
        fixedPhoneLabel.setFont(UIConstants.UI_FONT);
        fixedPhoneLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(fixedPhoneLabel, gbc);
        gbc.gridx = 1;
        panel.add(fixedPhoneField, gbc);
        
        gbc.gridx = 2;
        JLabel mobilePhoneLabel = new JLabel("Mobile Phone:");
        mobilePhoneLabel.setFont(UIConstants.UI_FONT);
        mobilePhoneLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(mobilePhoneLabel, gbc);
        gbc.gridx = 3;
        panel.add(mobilePhoneField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(UIConstants.UI_FONT);
        emailLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(emailLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(emailField, gbc);
        
        // Website
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        JLabel websiteLabel = new JLabel("Website:");
        websiteLabel.setFont(UIConstants.UI_FONT);
        websiteLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(websiteLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(websiteField, gbc);
        
        return panel;
    }
    
    private JPanel createPaymentInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Payment Information");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.MODULE_CLIENTS_TEXT);
        panel.setBorder(border);
        panel.setBackground(UIConstants.MODULE_CLIENTS_BG);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Payment Method
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel pmLabel = new JLabel("Payment Method *:");
        pmLabel.setFont(UIConstants.UI_FONT);
        pmLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(pmLabel, gbc);
        gbc.gridx = 1;
        panel.add(paymentMethodComboBox, gbc);
        
        // Credit Limit
        gbc.gridx = 2;
        JLabel clLabel = new JLabel("Credit Limit (€):");
        clLabel.setFont(UIConstants.UI_FONT);
        clLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(clLabel, gbc);
        gbc.gridx = 3;
        panel.add(creditLimitField, gbc);
        
        // Bank Account
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel bankLabel = new JLabel("Bank Account (IBAN):");
        bankLabel.setFont(UIConstants.UI_FONT);
        bankLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(bankLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(bankAccountField, gbc);
        
        // Active status
        gbc.gridx = 3; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(activeCheckBox, gbc);
        
        return panel;
    }
    
    private JPanel createPaymentObservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(UIConstants.MODULE_CLIENTS_BG);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Payment information
        JPanel paymentPanel = createPaymentInfoPanel();
        panel.add(paymentPanel, BorderLayout.NORTH);
        
        // Observations panel
        JPanel observationsPanel = createObservationsPanel();
        panel.add(observationsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createObservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Observations");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.MODULE_CLIENTS_TEXT);
        panel.setBorder(border);
        panel.setBackground(UIConstants.MODULE_CLIENTS_BG);
        
        JScrollPane scrollPane = new JScrollPane(observationsArea);
        scrollPane.setPreferredSize(new Dimension(0, 120));
        UIConstants.enhanceScroll(scrollPane);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void setupEventHandlers() {
        // Base class buttons handle Save/Cancel
        // Auto-fill province based on postal code
        postalCodeField.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                String postalCode = postalCodeField.getText().trim();
                if (postalCode.length() == 5) {
                    SpanishProvince province = clientService.getProvinceByPostalCode(postalCode);
                    if (province != null) {
                        provinceComboBox.setSelectedItem(province);
                    }
                }
            }
        });
    }
    
    private void configureDialog() {
        getContentPane().setBackground(UIConstants.MODULE_CLIENTS_BG);
        // allow resizing and sensible minimum size; center relative to parent
        setResizable(true);
        pack();
        setMinimumSize(new Dimension(640, 420));
        setLocationRelativeTo(getParent());
    }
    
    @Override
    protected void populateFields() {
        if (entity == null) return;
        
        nameField.setText(entity.getName());
        dniField.setText(entity.getDni());
        addressField.setText(entity.getAddress());
        cityField.setText(entity.getCity());
        provinceComboBox.setSelectedItem(entity.getProvince());
        
        postalCodeField.setText(entity.getPostalCode() != null ? entity.getPostalCode() : "");
        fixedPhoneField.setText(entity.getFixedPhone() != null ? entity.getFixedPhone() : "");
        mobilePhoneField.setText(entity.getMobilePhone() != null ? entity.getMobilePhone() : "");
        emailField.setText(entity.getEmail() != null ? entity.getEmail() : "");
        websiteField.setText(entity.getWebsite() != null ? entity.getWebsite() : "");
        
        paymentMethodComboBox.setSelectedItem(entity.getPaymentMethod());
        
        creditLimitField.setText(entity.getCreditLimit() != null ? entity.getCreditLimit().toString() : "");
        bankAccountField.setText(entity.getBankAccountNumber() != null ? entity.getBankAccountNumber() : "");
        
        Boolean active = ((Client)entity).getActive();
        activeCheckBox.setSelected(active != null ? active : true);
        observationsArea.setText(entity.getObservations() != null ? entity.getObservations() : "");
    }
    
    @Override
    protected boolean validateForm() {
        // Validate required fields
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        
        if (dniField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "DNI is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            dniField.requestFocus();
            return false;
        }
        
        if (addressField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Address is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            addressField.requestFocus();
            return false;
        }
        
        if (cityField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "City is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            cityField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    @Override
    protected boolean saveEntity() {
        try {
            // Create or update client
            if (entity == null) {
                entity = new Client();
            }
            
            entity.setName(nameField.getText().trim());
            entity.setDni(dniField.getText().trim().toUpperCase());
            entity.setAddress(addressField.getText().trim());
            entity.setCity(cityField.getText().trim());
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
            
            entity.setPaymentMethod((PaymentMethod) paymentMethodComboBox.getSelectedItem());
            
            try {
                String creditLimitText = creditLimitField.getText().trim();
                if (!creditLimitText.isEmpty()) {
                    entity.setCreditLimit(new BigDecimal(creditLimitText));
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid credit limit format.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                creditLimitField.requestFocus();
                return false;
            }
            
            String bankAccount = bankAccountField.getText().trim();
            entity.setBankAccountNumber(bankAccount.isEmpty() ? null : bankAccount);
            
            entity.setActive(activeCheckBox.isSelected());
            
            String observations = observationsArea.getText().trim();
            entity.setObservations(observations.isEmpty() ? null : observations);
            
            // Save client
            if (entity.getId() == null) {
                clientService.createClient(entity);
                JOptionPane.showMessageDialog(this, "Client created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                clientService.updateClient(entity);
                JOptionPane.showMessageDialog(this, "Client updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            
            return true;
            
        } catch (IllegalArgumentException e) {
            // Validation or business rule failures
            JOptionPane.showMessageDialog(this,
                e.getMessage(),
                "Validation Error",
                JOptionPane.WARNING_MESSAGE);
            return false;
        } catch (RuntimeException e) {
            // Persistence or unexpected runtime errors
            JOptionPane.showMessageDialog(this,
                "Error saving client: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
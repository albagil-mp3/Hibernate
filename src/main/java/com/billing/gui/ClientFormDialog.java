package com.billing.gui;

import com.billing.entity.Client;
import com.billing.entity.PaymentMethod;
import com.billing.entity.SpanishProvince;
import com.billing.service.ClientService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;

/**
 * Dialog for creating and editing client information
 * Provides comprehensive form with validation for all client fields
 */
public class ClientFormDialog extends JDialog {
    
    private ClientService clientService;
    private Client client;
    private boolean confirmed = false;
    
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
        this.client = client;
        this.clientService = clientService;
        
        initializeComponents();
        setupLayout();
        setupEventHandlers();
        populateFields();
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
        setLayout(new BorderLayout());
        
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(UIConstants.LAVENDER_LIGHT);
        
        // Title / header area
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JLabel titleLabel = new JLabel(getTitle(), SwingConstants.CENTER);
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        titleLabel.setForeground(UIConstants.ANTHRACITE);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        // add maximize / restore control to the header
        UIConstants.addMaxRestoreControl(this, topPanel);
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Create form sections
        JPanel formPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        formPanel.setOpaque(false);
        
        formPanel.add(createBasicInfoPanel());
        formPanel.add(createContactInfoPanel());
        formPanel.add(createPaymentInfoPanel());
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Observations panel
        JPanel observationsPanel = createObservationsPanel();
        mainPanel.add(observationsPanel, BorderLayout.SOUTH);
        
        // Wrap main content in a scroll pane so dialog can be resized and scrolled when needed
        JScrollPane contentScroll = new JScrollPane(mainPanel,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentScroll.setBorder(null);
        contentScroll.setPreferredSize(new Dimension(800, 600)); // default size
        UIConstants.enhanceScroll(contentScroll);
        add(contentScroll, BorderLayout.CENTER);
        
        // Required-note placed just above buttons and button panel in dialog south
        JPanel southWrapper = new JPanel(new BorderLayout());
        southWrapper.setOpaque(false);
        JLabel requiredNote = new JLabel("Los campos marcados con * son obligatorios", SwingConstants.CENTER);
        requiredNote.setFont(UIConstants.UI_FONT.deriveFont(Font.ITALIC, 12f));
        requiredNote.setForeground(UIConstants.PURPLE_DARK);
        requiredNote.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        southWrapper.add(requiredNote, BorderLayout.NORTH);
        southWrapper.add(createButtonPanel(), BorderLayout.SOUTH);
        add(southWrapper, BorderLayout.SOUTH);
    }
    
    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Basic Information");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.PURPLE_DARK);
        panel.setBorder(border);
        panel.setBackground(UIConstants.LAVENDER_LIGHT);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Name
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Name *:");
        nameLabel.setFont(UIConstants.UI_FONT);
        nameLabel.setForeground(UIConstants.ANTHRACITE);
        panel.add(nameLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(nameField, gbc);
        
        // DNI
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        JLabel dniLabel = new JLabel("DNI *:");
        dniLabel.setFont(UIConstants.UI_FONT);
        dniLabel.setForeground(UIConstants.ANTHRACITE);
        panel.add(dniLabel, gbc);
        gbc.gridx = 1;
        panel.add(dniField, gbc);
        
        // Address
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel addressLabel = new JLabel("Address *:");
        addressLabel.setFont(UIConstants.UI_FONT);
        addressLabel.setForeground(UIConstants.ANTHRACITE);
        panel.add(addressLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(addressField, gbc);
        
        // City and Province
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        JLabel cityLabel = new JLabel("City *:");
        cityLabel.setFont(UIConstants.UI_FONT);
        cityLabel.setForeground(UIConstants.ANTHRACITE);
        panel.add(cityLabel, gbc);
        gbc.gridx = 1;
        panel.add(cityField, gbc);
        
        gbc.gridx = 2;
        JLabel provinceLabel = new JLabel("Province *:");
        provinceLabel.setFont(UIConstants.UI_FONT);
        provinceLabel.setForeground(UIConstants.ANTHRACITE);
        panel.add(provinceLabel, gbc);
        gbc.gridx = 3;
        panel.add(provinceComboBox, gbc);
        
        // Postal Code
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel postalLabel = new JLabel("Postal Code:");
        postalLabel.setFont(UIConstants.UI_FONT);
        postalLabel.setForeground(UIConstants.ANTHRACITE);
        panel.add(postalLabel, gbc);
        gbc.gridx = 1;
        panel.add(postalCodeField, gbc);
        
        return panel;
    }
    
    private JPanel createContactInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Contact Information");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.PURPLE_DARK);
        panel.setBorder(border);
        panel.setBackground(UIConstants.LAVENDER_LIGHT);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Phones
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel fixedPhoneLabel = new JLabel("Fixed Phone:");
        fixedPhoneLabel.setFont(UIConstants.UI_FONT);
        fixedPhoneLabel.setForeground(UIConstants.ANTHRACITE);
        panel.add(fixedPhoneLabel, gbc);
        gbc.gridx = 1;
        panel.add(fixedPhoneField, gbc);
        
        gbc.gridx = 2;
        JLabel mobilePhoneLabel = new JLabel("Mobile Phone:");
        mobilePhoneLabel.setFont(UIConstants.UI_FONT);
        mobilePhoneLabel.setForeground(UIConstants.ANTHRACITE);
        panel.add(mobilePhoneLabel, gbc);
        gbc.gridx = 3;
        panel.add(mobilePhoneField, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(UIConstants.UI_FONT);
        emailLabel.setForeground(UIConstants.ANTHRACITE);
        panel.add(emailLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(emailField, gbc);
        
        // Website
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        JLabel websiteLabel = new JLabel("Website:");
        websiteLabel.setFont(UIConstants.UI_FONT);
        websiteLabel.setForeground(UIConstants.ANTHRACITE);
        panel.add(websiteLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(websiteField, gbc);
        
        return panel;
    }
    
    private JPanel createPaymentInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Payment Information");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.PURPLE_DARK);
        panel.setBorder(border);
        panel.setBackground(UIConstants.LAVENDER_LIGHT);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Payment Method
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel pmLabel = new JLabel("Payment Method *:");
        pmLabel.setFont(UIConstants.UI_FONT);
        pmLabel.setForeground(UIConstants.ANTHRACITE);
        panel.add(pmLabel, gbc);
        gbc.gridx = 1;
        panel.add(paymentMethodComboBox, gbc);
        
        // Credit Limit
        gbc.gridx = 2;
        JLabel clLabel = new JLabel("Credit Limit (€):");
        clLabel.setFont(UIConstants.UI_FONT);
        clLabel.setForeground(UIConstants.ANTHRACITE);
        panel.add(clLabel, gbc);
        gbc.gridx = 3;
        panel.add(creditLimitField, gbc);
        
        // Bank Account
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel bankLabel = new JLabel("Bank Account (IBAN):");
        bankLabel.setFont(UIConstants.UI_FONT);
        bankLabel.setForeground(UIConstants.ANTHRACITE);
        panel.add(bankLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(bankAccountField, gbc);
        
        // Active status
        gbc.gridx = 3; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(activeCheckBox, gbc);
        
        return panel;
    }
    
    private JPanel createObservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Observations");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.PURPLE_DARK);
        panel.setBorder(border);
        panel.setBackground(UIConstants.LAVENDER_LIGHT);
        
        JScrollPane scrollPane = new JScrollPane(observationsArea);
        scrollPane.setPreferredSize(new Dimension(0, 80));
        UIConstants.enhanceScroll(scrollPane);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.setBackground(UIConstants.LAVENDER_LIGHT);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.setFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        cancelButton.setFont(UIConstants.UI_FONT);

        saveButton.setPreferredSize(new Dimension(80, 30));
        cancelButton.setPreferredSize(new Dimension(80, 30));

        // Apply consistent filled styling with hover/press feedback (delegate to UIConstants)
        UIConstants.styleDialogButton(saveButton, UIConstants.TURQUOISE_ACCENT, UIConstants.BONE_WHITE);
        UIConstants.styleDialogButton(cancelButton, UIConstants.PURPLE_MAIN, UIConstants.BONE_WHITE);

        panel.add(saveButton);
        panel.add(cancelButton);

        return panel;
    }
    
    private void setupEventHandlers() {
        // Save button
        Component[] buttonPanelComponents = ((JPanel) getContentPane().getComponent(1)).getComponents();
        for (Component comp : buttonPanelComponents) {
            if (comp instanceof JButton) {
                JButton button = (JButton) comp;
                if ("Save".equals(button.getText())) {
                    button.addActionListener(e -> saveClient());
                } else if ("Cancel".equals(button.getText())) {
                    button.addActionListener(e -> cancelDialog());
                }
            }
        }
        
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
    
    private void populateFields() {
        if (client != null) {
            nameField.setText(client.getName());
            dniField.setText(client.getDni());
            addressField.setText(client.getAddress());
            cityField.setText(client.getCity());
            
            if (client.getProvince() != null) {
                provinceComboBox.setSelectedItem(client.getProvince());
            }
            
            postalCodeField.setText(client.getPostalCode());
            fixedPhoneField.setText(client.getFixedPhone());
            mobilePhoneField.setText(client.getMobilePhone());
            emailField.setText(client.getEmail());
            websiteField.setText(client.getWebsite());
            
            if (client.getPaymentMethod() != null) {
                paymentMethodComboBox.setSelectedItem(client.getPaymentMethod());
            }
            
            if (client.getCreditLimit() != null) {
                creditLimitField.setText(client.getCreditLimit().toString());
            }
            
            bankAccountField.setText(client.getBankAccountNumber());
            activeCheckBox.setSelected(client.getActive() != null ? client.getActive() : true);
            observationsArea.setText(client.getObservations());
        }
    }
    
    private void configureDialog() {
        getContentPane().setBackground(UIConstants.LAVENDER_LIGHT);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        // allow resizing and sensible minimum size; center relative to parent
        setResizable(true);
        pack();
        setMinimumSize(new Dimension(640, 420));
        setLocationRelativeTo(getParent());
    }
    
    private void saveClient() {
        try {
            // Validate required fields
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                nameField.requestFocus();
                return;
            }
            
            if (dniField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "DNI is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                dniField.requestFocus();
                return;
            }
            
            if (addressField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Address is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                addressField.requestFocus();
                return;
            }
            
            if (cityField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "City is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                cityField.requestFocus();
                return;
            }
            
            // Create or update client
            if (client == null) {
                client = new Client();
            }
            
            client.setName(nameField.getText().trim());
            client.setDni(dniField.getText().trim().toUpperCase());
            client.setAddress(addressField.getText().trim());
            client.setCity(cityField.getText().trim());
            client.setProvince((SpanishProvince) provinceComboBox.getSelectedItem());
            
            String postalCode = postalCodeField.getText().trim();
            client.setPostalCode(postalCode.isEmpty() ? null : postalCode);
            
            String fixedPhone = fixedPhoneField.getText().trim();
            client.setFixedPhone(fixedPhone.isEmpty() ? null : fixedPhone);
            
            String mobilePhone = mobilePhoneField.getText().trim();
            client.setMobilePhone(mobilePhone.isEmpty() ? null : mobilePhone);
            
            String email = emailField.getText().trim();
            client.setEmail(email.isEmpty() ? null : email);
            
            String website = websiteField.getText().trim();
            client.setWebsite(website.isEmpty() ? null : website);
            
            client.setPaymentMethod((PaymentMethod) paymentMethodComboBox.getSelectedItem());
            
            try {
                String creditLimitText = creditLimitField.getText().trim();
                if (!creditLimitText.isEmpty()) {
                    client.setCreditLimit(new BigDecimal(creditLimitText));
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid credit limit format.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                creditLimitField.requestFocus();
                return;
            }
            
            String bankAccount = bankAccountField.getText().trim();
            client.setBankAccountNumber(bankAccount.isEmpty() ? null : bankAccount);
            
            client.setActive(activeCheckBox.isSelected());
            
            String observations = observationsArea.getText().trim();
            client.setObservations(observations.isEmpty() ? null : observations);
            
            // Save client
            if (client.getId() == null) {
                clientService.createClient(client);
                JOptionPane.showMessageDialog(this, "Client created successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                clientService.updateClient(client);
                JOptionPane.showMessageDialog(this, "Client updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
            
            confirmed = true;
            dispose();
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error saving client: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void cancelDialog() {
        dispose();
    }
    
    public boolean isConfirmed() {
        return confirmed;
    }
}
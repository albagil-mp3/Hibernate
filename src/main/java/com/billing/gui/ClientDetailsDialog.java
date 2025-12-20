package com.billing.gui;

import com.billing.entity.Client;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

/**
 * Dialog for displaying complete client details in a read-only format
 */
public class ClientDetailsDialog extends JDialog {
    
    private Client client;
    
    public ClientDetailsDialog(Frame parent, Client client) {
        super(parent, "Client Details", true);
        this.client = client;
        
        initializeComponents();
        setupLayout();
        configureDialog();
    }
    
    private void initializeComponents() {
        // Set the dialog properties
        setTitle("Client Details - " + (client.getName() != null ? client.getName() : "Unknown"));
        setModal(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setResizable(true);
    }
    
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(UIConstants.LAVENDER_LIGHT);
        
        // Title
        JLabel titleLabel = new JLabel("Client Information", SwingConstants.CENTER);
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        titleLabel.setForeground(UIConstants.ANTHRACITE);
        // header panel so we can attach maximize/restore control at the right
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(titleLabel, BorderLayout.CENTER);
        UIConstants.addMaxRestoreControl(this, header);
        mainPanel.add(header, BorderLayout.NORTH);
        
        // Create info sections
        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        infoPanel.setOpaque(false);
        
        infoPanel.add(createBasicInfoPanel());
        infoPanel.add(createContactInfoPanel());
        infoPanel.add(createPaymentInfoPanel());
        infoPanel.add(createObservationsPanel());
        
        JScrollPane scrollPane = new JScrollPane(infoPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(600, 500));
        // make scrolling more responsive
        UIConstants.enhanceScroll(scrollPane);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Button panel
        add(createButtonPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Basic Information");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.PURPLE_DARK);
        panel.setBorder(border);
        panel.setBackground(UIConstants.LAVENDER_LIGHT);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        Font labelFont = UIConstants.UI_FONT.deriveFont(Font.BOLD);
        Font valueFont = UIConstants.UI_FONT;
        
        // ID
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel idLabel = new JLabel("ID:");
        idLabel.setFont(labelFont);
        idLabel.setForeground(UIConstants.ANTHRACITE);
        panel.add(idLabel, gbc);
        gbc.gridx = 1;
        JLabel idValue = new JLabel(client.getId() != null ? client.getId().toString() : "N/A");
        idValue.setFont(valueFont);
        panel.add(idValue, gbc);
        
        // Name
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(labelFont);
        nameLabel.setForeground(UIConstants.ANTHRACITE);
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        JLabel nameValue = new JLabel(client.getName() != null ? client.getName() : "N/A");
        nameValue.setFont(valueFont);
        panel.add(nameValue, gbc);
        
        // DNI
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel dniLabel = new JLabel("DNI:");
        dniLabel.setFont(labelFont);
        dniLabel.setForeground(UIConstants.ANTHRACITE);
        panel.add(dniLabel, gbc);
        gbc.gridx = 1;
        JLabel dniValue = new JLabel(client.getDni() != null ? client.getDni() : "N/A");
        dniValue.setFont(valueFont);
        panel.add(dniValue, gbc);
        
        // Address
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(labelFont);
        addressLabel.setForeground(UIConstants.ANTHRACITE);
        panel.add(addressLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel addressValue = new JLabel(client.getAddress() != null ? client.getAddress() : "N/A");
        addressValue.setFont(valueFont);
        panel.add(addressValue, gbc);
        
        // City
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        JLabel cityLabel = new JLabel("City:");
        cityLabel.setFont(labelFont);
        cityLabel.setForeground(UIConstants.ANTHRACITE);
        panel.add(cityLabel, gbc);
        gbc.gridx = 1;
        JLabel cityValue = new JLabel(client.getCity() != null ? client.getCity() : "N/A");
        cityValue.setFont(valueFont);
        panel.add(cityValue, gbc);
        
        // Province
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel provinceLabel = new JLabel("Province:");
        provinceLabel.setFont(labelFont);
        provinceLabel.setForeground(UIConstants.ANTHRACITE);
        panel.add(provinceLabel, gbc);
        gbc.gridx = 1;
        JLabel provinceValue = new JLabel(client.getProvince() != null ? client.getProvince().getDisplayName() : "N/A");
        provinceValue.setFont(valueFont);
        panel.add(provinceValue, gbc);
        
        // Postal Code
        gbc.gridx = 0; gbc.gridy = 6;
        JLabel postalCodeLabel = new JLabel("Postal Code:");
        postalCodeLabel.setFont(labelFont);
        postalCodeLabel.setForeground(UIConstants.ANTHRACITE);
        panel.add(postalCodeLabel, gbc);
        gbc.gridx = 1;
        JLabel postalCodeValue = new JLabel(client.getPostalCode() != null ? client.getPostalCode() : "N/A");
        postalCodeValue.setFont(valueFont);
        panel.add(postalCodeValue, gbc);
        
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
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        Font labelFont = UIConstants.UI_FONT.deriveFont(Font.BOLD);
        Font valueFont = UIConstants.UI_FONT;
        
        // Fixed Phone
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel fixedPhoneLabel = new JLabel("Fixed Phone:");
        fixedPhoneLabel.setFont(labelFont);
        fixedPhoneLabel.setForeground(UIConstants.ANTHRACITE);
        panel.add(fixedPhoneLabel, gbc);
        gbc.gridx = 1;
        JLabel fixedPhoneValue = new JLabel(client.getFixedPhone() != null ? client.getFixedPhone() : "N/A");
        fixedPhoneValue.setFont(valueFont);
        panel.add(fixedPhoneValue, gbc);
        
        // Mobile Phone
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel mobilePhoneLabel = new JLabel("Mobile Phone:");
        mobilePhoneLabel.setFont(labelFont);
        mobilePhoneLabel.setForeground(UIConstants.ANTHRACITE);
        panel.add(mobilePhoneLabel, gbc);
        gbc.gridx = 1;
        JLabel mobilePhoneValue = new JLabel(client.getMobilePhone() != null ? client.getMobilePhone() : "N/A");
        mobilePhoneValue.setFont(valueFont);
        panel.add(mobilePhoneValue, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        emailLabel.setForeground(UIConstants.ANTHRACITE);
        panel.add(emailLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel emailValue = new JLabel(client.getEmail() != null ? client.getEmail() : "N/A");
        emailValue.setFont(valueFont);
        panel.add(emailValue, gbc);
        
        // Website
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        JLabel websiteLabel = new JLabel("Website:");
        websiteLabel.setFont(labelFont);
        websiteLabel.setForeground(UIConstants.ANTHRACITE);
        panel.add(websiteLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel websiteValue = new JLabel(client.getWebsite() != null ? client.getWebsite() : "N/A");
        websiteValue.setFont(valueFont);
        panel.add(websiteValue, gbc);
        
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
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        Font labelFont = UIConstants.UI_FONT.deriveFont(Font.BOLD);
        Font valueFont = UIConstants.UI_FONT;
        
        // Payment Method
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel paymentMethodLabel = new JLabel("Payment Method:");
        paymentMethodLabel.setFont(labelFont);
        paymentMethodLabel.setForeground(UIConstants.ANTHRACITE);
        panel.add(paymentMethodLabel, gbc);
        gbc.gridx = 1;
        JLabel paymentMethodValue = new JLabel(client.getPaymentMethod() != null ? client.getPaymentMethod().toString() : "N/A");
        paymentMethodValue.setFont(valueFont);
        panel.add(paymentMethodValue, gbc);
        
        // Credit Limit
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel creditLimitLabel = new JLabel("Credit Limit:");
        creditLimitLabel.setFont(labelFont);
        creditLimitLabel.setForeground(UIConstants.ANTHRACITE);
        panel.add(creditLimitLabel, gbc);
        gbc.gridx = 1;
        String creditLimitText = client.getCreditLimit() != null ? "€" + client.getCreditLimit().toString() : "€0.00";
        JLabel creditLimitValue = new JLabel(creditLimitText);
        creditLimitValue.setFont(valueFont);
        panel.add(creditLimitValue, gbc);
        
        // Bank Account
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel bankAccountLabel = new JLabel("Bank Account:");
        bankAccountLabel.setFont(labelFont);
        bankAccountLabel.setForeground(UIConstants.ANTHRACITE);
        panel.add(bankAccountLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel bankAccountValue = new JLabel(client.getBankAccountNumber() != null ? client.getBankAccountNumber() : "N/A");
        bankAccountValue.setFont(valueFont);
        panel.add(bankAccountValue, gbc);
        
        // Active Status
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        JLabel activeLabel = new JLabel("Status:");
        activeLabel.setFont(labelFont);
        activeLabel.setForeground(UIConstants.ANTHRACITE);
        panel.add(activeLabel, gbc);
        gbc.gridx = 1;
        String activeText = client.getActive() != null ? (client.getActive() ? "Active" : "Inactive") : "Inactive";
        JLabel activeValue = new JLabel(activeText);
        activeValue.setFont(valueFont);
        // Set color based on status using accent palette
        if (client.getActive() != null && client.getActive()) {
            activeValue.setForeground(UIConstants.TURQUOISE_ACCENT.darker());
        } else {
            activeValue.setForeground(UIConstants.PURPLE_MAIN.darker());
        }
        panel.add(activeValue, gbc);
        
        return panel;
    }
    
    private JPanel createObservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Observations");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.PURPLE_DARK);
        panel.setBorder(border);
        panel.setBackground(UIConstants.LAVENDER_LIGHT);
        
        JTextArea observationsArea = new JTextArea();
        observationsArea.setFont(UIConstants.UI_FONT);
        observationsArea.setEditable(false);
        observationsArea.setBackground(getBackground());
        observationsArea.setLineWrap(true);
        observationsArea.setWrapStyleWord(true);
        observationsArea.setText(client.getObservations() != null ? client.getObservations() : "No observations");
        
        JScrollPane scrollPane = new JScrollPane(observationsArea);
        scrollPane.setPreferredSize(new Dimension(0, 80));
        scrollPane.setBorder(null);
        // enhance mouse-wheel responsiveness for observations area
        UIConstants.enhanceScroll(scrollPane);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.setBackground(UIConstants.LAVENDER_LIGHT);
        
        JButton closeButton = new JButton("Close");
        closeButton.setFont(UIConstants.UI_FONT);
        closeButton.setPreferredSize(new Dimension(80, 30));
        UIConstants.styleDialogButton(closeButton, UIConstants.PURPLE_MAIN, UIConstants.BONE_WHITE);
         closeButton.addActionListener(e -> dispose());
        
        panel.add(closeButton);
        
        return panel;
    }
    
    private void configureDialog() {
        getContentPane().setBackground(UIConstants.LAVENDER_LIGHT);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        pack();
        setLocationRelativeTo(getParent());
        setResizable(true);
    }
}
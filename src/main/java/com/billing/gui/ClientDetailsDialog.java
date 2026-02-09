    package com.billing.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import com.billing.entity.Client;

/**
 * Dialog for displaying complete client details in a read-only format
 */
public class ClientDetailsDialog extends BaseDetailsDialog<Client> {
    
    public ClientDetailsDialog(Frame parent, Client client) {
        super(parent, client, "Client Details - " + (client.getName() != null ? client.getName() : "Unknown"));
        // entity field is set by base class constructor
    }
    
    
    @Override
    protected JPanel createContentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(UIConstants.MODULE_CLIENTS_BG);
        
        // Title
        JLabel titleLabel = new JLabel("Client Information", SwingConstants.CENTER);
        titleLabel.setFont(UIConstants.TITLE_FONT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        titleLabel.setForeground(UIConstants.MODULE_CLIENTS_TEXT);
        // header panel with centered title
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(header, BorderLayout.NORTH);
        
        // Create info sections
        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        infoPanel.setOpaque(false);
        
        infoPanel.add(createBasicInfoPanel());
        infoPanel.add(createContactInfoPanel());
        infoPanel.add(createPaymentInfoPanel());
        infoPanel.add(createObservationsPanel());
        
        // Wrap in scroll pane within content
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);
        contentWrapper.add(infoPanel, BorderLayout.CENTER);
        
        return contentWrapper;
    }
    
    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Basic Information");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.MODULE_CLIENTS_TEXT);
        panel.setBorder(border);
        panel.setBackground(UIConstants.MODULE_CLIENTS_BG);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        Font labelFont = UIConstants.UI_FONT.deriveFont(Font.BOLD);
        Font valueFont = UIConstants.UI_FONT;
        
        // ID
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel idLabel = new JLabel("ID:");
        idLabel.setFont(labelFont);
        idLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(idLabel, gbc);
        gbc.gridx = 1;
        JLabel idValue = new JLabel(((Client)entity).getId() != null ? ((Client)entity).getId().toString() : "N/A");
        idValue.setFont(valueFont);
        panel.add(idValue, gbc);

        // Code
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel codeLabel = new JLabel("Code:");
        codeLabel.setFont(labelFont);
        codeLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(codeLabel, gbc);
        gbc.gridx = 1;
        JLabel codeValue = new JLabel(((Client)entity).getCode() != null ? ((Client)entity).getCode() : "N/A");
        codeValue.setFont(valueFont);
        panel.add(codeValue, gbc);
        
        // Name
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(labelFont);
        nameLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(nameLabel, gbc);
        gbc.gridx = 1;
        JLabel nameValue = new JLabel(((Client)entity).getName() != null ? ((Client)entity).getName() : "N/A");
        nameValue.setFont(valueFont);
        panel.add(nameValue, gbc);
        
        // DNI
        gbc.gridx = 0; gbc.gridy = 3;
        JLabel dniLabel = new JLabel("DNI:");
        dniLabel.setFont(labelFont);
        dniLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(dniLabel, gbc);
        gbc.gridx = 1;
        JLabel dniValue = new JLabel(((Client)entity).getDni() != null ? ((Client)entity).getDni() : "N/A");
        dniValue.setFont(valueFont);
        panel.add(dniValue, gbc);
        
        // Address
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(labelFont);
        addressLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(addressLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel addressValue = new JLabel(((Client)entity).getAddress() != null ? ((Client)entity).getAddress() : "N/A");
        addressValue.setFont(valueFont);
        panel.add(addressValue, gbc);
        
        // City
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        JLabel cityLabel = new JLabel("City:");
        cityLabel.setFont(labelFont);
        cityLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(cityLabel, gbc);
        gbc.gridx = 1;
        JLabel cityValue = new JLabel(((Client)entity).getCity() != null ? ((Client)entity).getCity() : "N/A");
        cityValue.setFont(valueFont);
        panel.add(cityValue, gbc);
        
        // Province
        gbc.gridx = 0; gbc.gridy = 6;
        JLabel provinceLabel = new JLabel("Province:");
        provinceLabel.setFont(labelFont);
        provinceLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(provinceLabel, gbc);
        gbc.gridx = 1;
        JLabel provinceValue = new JLabel(((Client)entity).getProvince() != null ? ((Client)entity).getProvince().getDisplayName() : "N/A");
        provinceValue.setFont(valueFont);
        panel.add(provinceValue, gbc);
        
        // Postal Code
        gbc.gridx = 0; gbc.gridy = 7;
        JLabel postalCodeLabel = new JLabel("Postal Code:");
        postalCodeLabel.setFont(labelFont);
        postalCodeLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(postalCodeLabel, gbc);
        gbc.gridx = 1;
        JLabel postalCodeValue = new JLabel(((Client)entity).getPostalCode() != null ? ((Client)entity).getPostalCode() : "N/A");
        postalCodeValue.setFont(valueFont);
        panel.add(postalCodeValue, gbc);
        
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
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        Font labelFont = UIConstants.UI_FONT.deriveFont(Font.BOLD);
        Font valueFont = UIConstants.UI_FONT;
        
        // Fixed Phone
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel fixedPhoneLabel = new JLabel("Fixed Phone:");
        fixedPhoneLabel.setFont(labelFont);
        fixedPhoneLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(fixedPhoneLabel, gbc);
        gbc.gridx = 1;
        JLabel fixedPhoneValue = new JLabel(((Client)entity).getFixedPhone() != null ? ((Client)entity).getFixedPhone() : "N/A");
        fixedPhoneValue.setFont(valueFont);
        panel.add(fixedPhoneValue, gbc);
        
        // Mobile Phone
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel mobilePhoneLabel = new JLabel("Mobile Phone:");
        mobilePhoneLabel.setFont(labelFont);
        mobilePhoneLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(mobilePhoneLabel, gbc);
        gbc.gridx = 1;
        JLabel mobilePhoneValue = new JLabel(((Client)entity).getMobilePhone() != null ? ((Client)entity).getMobilePhone() : "N/A");
        mobilePhoneValue.setFont(valueFont);
        panel.add(mobilePhoneValue, gbc);
        
        // Email
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        emailLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(emailLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel emailValue = new JLabel(((Client)entity).getEmail() != null ? ((Client)entity).getEmail() : "N/A");
        emailValue.setFont(valueFont);
        panel.add(emailValue, gbc);
        
        // Website
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        JLabel websiteLabel = new JLabel("Website:");
        websiteLabel.setFont(labelFont);
        websiteLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(websiteLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel websiteValue = new JLabel(((Client)entity).getWebsite() != null ? ((Client)entity).getWebsite() : "N/A");
        websiteValue.setFont(valueFont);
        panel.add(websiteValue, gbc);
        
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
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        Font labelFont = UIConstants.UI_FONT.deriveFont(Font.BOLD);
        Font valueFont = UIConstants.UI_FONT;
        
        // Payment Method
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel paymentMethodLabel = new JLabel("Payment Method:");
        paymentMethodLabel.setFont(labelFont);
        paymentMethodLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(paymentMethodLabel, gbc);
        gbc.gridx = 1;
        JLabel paymentMethodValue = new JLabel(((Client)entity).getPaymentMethod() != null ? ((Client)entity).getPaymentMethod().toString() : "N/A");
        paymentMethodValue.setFont(valueFont);
        panel.add(paymentMethodValue, gbc);
        
        // Credit Limit
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel creditLimitLabel = new JLabel("Credit Limit:");
        creditLimitLabel.setFont(labelFont);
        creditLimitLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(creditLimitLabel, gbc);
        gbc.gridx = 1;
        String creditLimitText = ((Client)entity).getCreditLimit() != null ? "€" + ((Client)entity).getCreditLimit().toString() : "€0.00";
        JLabel creditLimitValue = new JLabel(creditLimitText);
        creditLimitValue.setFont(valueFont);
        panel.add(creditLimitValue, gbc);
        
        // Bank Account
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel bankAccountLabel = new JLabel("Bank Account:");
        bankAccountLabel.setFont(labelFont);
        bankAccountLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(bankAccountLabel, gbc);
        gbc.gridx = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel bankAccountValue = new JLabel(((Client)entity).getBankAccountNumber() != null ? ((Client)entity).getBankAccountNumber() : "N/A");
        bankAccountValue.setFont(valueFont);
        panel.add(bankAccountValue, gbc);
        
        // Active Status
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        JLabel activeLabel = new JLabel("Status:");
        activeLabel.setFont(labelFont);
        activeLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(activeLabel, gbc);
        gbc.gridx = 1;
        String activeText = ((Client)entity).getActive() != null ? (((Client)entity).getActive() ? "Active" : "Inactive") : "Inactive";
        JLabel activeValue = new JLabel(activeText);
        activeValue.setFont(valueFont);
        // Set color based on status using accent palette
            if (((Client)entity).getActive() != null && ((Client)entity).getActive()) {
            activeValue.setForeground(UIConstants.MODULE_CLIENTS_TEXT.darker());
        } else {
            activeValue.setForeground(UIConstants.MODULE_CLIENTS_TEXT.darker());
        }
        panel.add(activeValue, gbc);
        
        return panel;
    }
    
    private JPanel createObservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Observations");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.MODULE_CLIENTS_TEXT);
        panel.setBorder(border);
        panel.setBackground(UIConstants.MODULE_CLIENTS_BG);
        
        JTextArea observationsArea = new JTextArea();
        observationsArea.setFont(UIConstants.UI_FONT);
        observationsArea.setEditable(false);
        observationsArea.setBackground(getBackground());
        observationsArea.setLineWrap(true);
        observationsArea.setWrapStyleWord(true);
        observationsArea.setText(((Client)entity).getObservations() != null ? ((Client)entity).getObservations() : "No observations");
        
        JScrollPane scrollPane = new JScrollPane(observationsArea);
        scrollPane.setPreferredSize(new Dimension(0, 80));
        scrollPane.setBorder(null);
        // enhance mouse-wheel responsiveness for observations area
        UIConstants.enhanceScroll(scrollPane);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    @Override
    protected void configureDialog() {
        super.configureDialog();
        getContentPane().setBackground(UIConstants.MODULE_CLIENTS_BG);
    }
}
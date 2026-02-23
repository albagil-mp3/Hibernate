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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import com.billing.model.party.Client;

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
        mainPanel.setBackground(new Color(0xF5F5F5));

        // Create info sections stacked vertically and compact
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JPanel basic = createBasicInfoPanel();
        basic.setAlignmentX(Component.LEFT_ALIGNMENT);
        basic.setMaximumSize(new Dimension(Integer.MAX_VALUE, basic.getPreferredSize().height));
        infoPanel.add(basic);
        infoPanel.add(Box.createVerticalStrut(8));

        JPanel contact = createContactInfoPanel();
        contact.setAlignmentX(Component.LEFT_ALIGNMENT);
        contact.setMaximumSize(new Dimension(Integer.MAX_VALUE, contact.getPreferredSize().height));
        infoPanel.add(contact);
        infoPanel.add(Box.createVerticalStrut(8));

        JPanel payment = createPaymentInfoPanel();
        payment.setAlignmentX(Component.LEFT_ALIGNMENT);
        payment.setMaximumSize(new Dimension(Integer.MAX_VALUE, payment.getPreferredSize().height));
        infoPanel.add(payment);
        infoPanel.add(Box.createVerticalStrut(8));

        JPanel observations = createObservationsPanel();
        observations.setAlignmentX(Component.LEFT_ALIGNMENT);
        observations.setMaximumSize(new Dimension(Integer.MAX_VALUE, observations.getPreferredSize().height));
        infoPanel.add(observations);

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);
        contentWrapper.setBorder(BorderFactory.createEmptyBorder(10,12,10,12));
        contentWrapper.add(infoPanel, BorderLayout.CENTER);

        return contentWrapper;
    }
    
    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Basic Information");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.MODULE_CLIENTS_TEXT);
        panel.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(8,12,8,12)));
        panel.setBackground(UIConstants.MODULE_CLIENTS_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 12, 6, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = UIConstants.UI_FONT.deriveFont(Font.BOLD);
        Font valueFont = UIConstants.UI_FONT;

        int y = 0;

        // ID
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel idLabel = new JLabel("ID:"); idLabel.setFont(labelFont); idLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(idLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JLabel idValue = new JLabel(((Client)entity).getId() != null ? ((Client)entity).getId().toString() : "N/A"); idValue.setFont(valueFont); idValue.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(idValue, gbc);
        y++;

        // Code
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel codeLabel = new JLabel("Code:"); codeLabel.setFont(labelFont); codeLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(codeLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JLabel codeValue = new JLabel(((Client)entity).getCode() != null ? ((Client)entity).getCode() : "N/A"); codeValue.setFont(valueFont); codeValue.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(codeValue, gbc);
        y++;

        // Name
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel nameLabel = new JLabel("Name:"); nameLabel.setFont(labelFont); nameLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(nameLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JLabel nameValue = new JLabel(((Client)entity).getName() != null ? ((Client)entity).getName() : "N/A"); nameValue.setFont(valueFont); nameValue.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(nameValue, gbc);
        y++;

        // DNI
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel dniLabel = new JLabel("DNI:"); dniLabel.setFont(labelFont); dniLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(dniLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JLabel dniValue = new JLabel(((Client)entity).getDni() != null ? ((Client)entity).getDni() : "N/A"); dniValue.setFont(valueFont); dniValue.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(dniValue, gbc);
        y++;

        y = 0;

        // Address
        gbc.gridx = 2; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel addressLabel = new JLabel("Address:"); addressLabel.setFont(labelFont); addressLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(addressLabel, gbc);
        gbc.gridx = 3; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        JLabel addressValue = new JLabel(((Client)entity).getAddress() != null ? ((Client)entity).getAddress() : "N/A"); addressValue.setFont(valueFont); addressValue.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(addressValue, gbc);
        y++;

        // City
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 2; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel cityLabel = new JLabel("City:"); cityLabel.setFont(labelFont); cityLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(cityLabel, gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        JLabel cityValue = new JLabel(((Client)entity).getCity() != null ? ((Client)entity).getCity() : "N/A"); cityValue.setFont(valueFont); cityValue.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(cityValue, gbc);
        y++;

        // Province
        gbc.gridx = 2; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel provinceLabel = new JLabel("Province:"); provinceLabel.setFont(labelFont); provinceLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(provinceLabel, gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        JLabel provinceValue = new JLabel(((Client)entity).getProvince() != null ? ((Client)entity).getProvince().getDisplayName() : "N/A"); provinceValue.setFont(valueFont); provinceValue.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(provinceValue, gbc);
        y++;

        // Postal Code
        gbc.gridx = 2; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel postalCodeLabel = new JLabel("Postal Code:"); postalCodeLabel.setFont(labelFont); postalCodeLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(postalCodeLabel, gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        JLabel postalCodeValue = new JLabel(((Client)entity).getPostalCode() != null ? ((Client)entity).getPostalCode() : "N/A"); postalCodeValue.setFont(valueFont); postalCodeValue.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(postalCodeValue, gbc);
        
        return panel;
    }
    
    private JPanel createContactInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Contact Information");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.MODULE_CLIENTS_TEXT);
        panel.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(8,12,8,12)));
        panel.setBackground(UIConstants.MODULE_CLIENTS_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 12, 6, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = UIConstants.UI_FONT.deriveFont(Font.BOLD);
        Font valueFont = UIConstants.UI_FONT;

        int y = 0;

        // Fixed Phone
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel fixedPhoneLabel = new JLabel("Fixed Phone:"); fixedPhoneLabel.setFont(labelFont); fixedPhoneLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(fixedPhoneLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JLabel fixedPhoneValue = new JLabel(((Client)entity).getFixedPhone() != null ? ((Client)entity).getFixedPhone() : "N/A"); fixedPhoneValue.setFont(valueFont); fixedPhoneValue.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(fixedPhoneValue, gbc);
        y++;

        // Mobile Phone
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel mobilePhoneLabel = new JLabel("Mobile Phone:"); mobilePhoneLabel.setFont(labelFont); mobilePhoneLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(mobilePhoneLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JLabel mobilePhoneValue = new JLabel(((Client)entity).getMobilePhone() != null ? ((Client)entity).getMobilePhone() : "N/A"); mobilePhoneValue.setFont(valueFont); mobilePhoneValue.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(mobilePhoneValue, gbc);
        y++;

        y = 0;

        // Email
        gbc.gridx = 2; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel emailLabel = new JLabel("Email:"); emailLabel.setFont(labelFont); emailLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(emailLabel, gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        JLabel emailValue = new JLabel(((Client)entity).getEmail() != null ? ((Client)entity).getEmail() : "N/A"); emailValue.setFont(valueFont); emailValue.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(emailValue, gbc);
        y++;

        // Website
        gbc.gridx = 2; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel websiteLabel = new JLabel("Website:"); websiteLabel.setFont(labelFont); websiteLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(websiteLabel, gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        JLabel websiteValue = new JLabel(((Client)entity).getWebsite() != null ? ((Client)entity).getWebsite() : "N/A"); websiteValue.setFont(valueFont); websiteValue.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(websiteValue, gbc);
        
        return panel;
    }
    
    private JPanel createPaymentInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Payment Information");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.MODULE_CLIENTS_TEXT);
        panel.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(8,12,8,12)));
        panel.setBackground(UIConstants.MODULE_CLIENTS_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 12, 6, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = UIConstants.UI_FONT.deriveFont(Font.BOLD);
        Font valueFont = UIConstants.UI_FONT;

        int y = 0;

        // Payment Method
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel paymentMethodLabel = new JLabel("Payment Method:"); paymentMethodLabel.setFont(labelFont); paymentMethodLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(paymentMethodLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        JLabel paymentMethodValue = new JLabel(((Client)entity).getPaymentMethod() != null ? ((Client)entity).getPaymentMethod().toString() : "N/A"); paymentMethodValue.setFont(valueFont); paymentMethodValue.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(paymentMethodValue, gbc);
        y++;

        // Credit Limit
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel creditLimitLabel = new JLabel("Credit Limit:"); creditLimitLabel.setFont(labelFont); creditLimitLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(creditLimitLabel, gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        String creditLimitText = ((Client)entity).getCreditLimit() != null ? "€" + ((Client)entity).getCreditLimit().toString() : "€0.00";
        JLabel creditLimitValue = new JLabel(creditLimitText); creditLimitValue.setFont(valueFont); creditLimitValue.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(creditLimitValue, gbc);
        y++;

        y = 0;
        
        // Bank Account
        gbc.gridx = 2; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel bankAccountLabel = new JLabel("Bank Account:"); bankAccountLabel.setFont(labelFont); bankAccountLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(bankAccountLabel, gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        JLabel bankAccountValue = new JLabel(((Client)entity).getBankAccountNumber() != null ? ((Client)entity).getBankAccountNumber() : "N/A"); bankAccountValue.setFont(valueFont); bankAccountValue.setHorizontalAlignment(SwingConstants.LEFT);
        panel.add(bankAccountValue, gbc);
        y++;

        // Active Status
        gbc.gridx = 2; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel activeLabel = new JLabel("Status:"); activeLabel.setFont(labelFont); activeLabel.setForeground(new java.awt.Color(0x212121));
        panel.add(activeLabel, gbc);
        gbc.gridx = 3; gbc.weightx = 1.0;
        String activeText = ((Client)entity).getActive() != null ? (((Client)entity).getActive() ? "Active" : "Inactive") : "Inactive";
        JLabel activeValue = new JLabel(activeText); activeValue.setFont(valueFont); activeValue.setHorizontalAlignment(SwingConstants.LEFT);
        activeValue.setForeground(UIConstants.MODULE_CLIENTS_TEXT.darker());
        panel.add(activeValue, gbc);
        
        return panel;
    }
    
    private JPanel createObservationsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Observations");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.MODULE_CLIENTS_TEXT);
        panel.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(8,12,8,12)));
        panel.setBackground(UIConstants.MODULE_CLIENTS_BG);
        
        JTextArea observationsArea = new JTextArea();
        observationsArea.setFont(UIConstants.UI_FONT);
        observationsArea.setEditable(false);
        observationsArea.setBackground(Color.WHITE);
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
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
import javax.swing.border.TitledBorder;

import com.billing.model.party.Supplier;

/**
 * Dialog for displaying complete supplier details in a read-only format
 */
public class SupplierDetailsDialog extends BaseDetailsDialog<Supplier> {
    
    public SupplierDetailsDialog(Frame parent, Supplier supplier) {
        super(parent, supplier, "Supplier Details - " + (supplier.getName() != null ? supplier.getName() : "Unknown"));
    }
    
    @Override
    protected JPanel createContentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(new Color(0xF5F5F5));

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
        border.setTitleColor(UIConstants.MODULE_SUPPLIERS_TEXT);
        panel.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(8,12,8,12)));
        panel.setBackground(UIConstants.MODULE_SUPPLIERS_BG);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 12, 6, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = UIConstants.UI_FONT.deriveFont(Font.BOLD);
        Font valueFont = UIConstants.UI_FONT;

        int y = 0;

        // ID
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel idLabel = new JLabel("ID:");
        idLabel.setFont(labelFont);
        idLabel.setForeground(new Color(0x212121));
        panel.add(idLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        JLabel idValue = new JLabel(entity.getId() != null ? entity.getId().toString() : "N/A");
        idValue.setFont(valueFont);
        panel.add(idValue, gbc);
        y++;

        // Code
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel codeLabel = new JLabel("Code:");
        codeLabel.setFont(labelFont);
        codeLabel.setForeground(new Color(0x212121));
        panel.add(codeLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        JLabel codeValue = new JLabel(entity.getCode() != null ? entity.getCode() : "N/A");
        codeValue.setFont(valueFont);
        panel.add(codeValue, gbc);
        y++;

        // Name
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setFont(labelFont);
        nameLabel.setForeground(new Color(0x212121));
        panel.add(nameLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        JLabel nameValue = new JLabel(entity.getName() != null ? entity.getName() : "N/A");
        nameValue.setFont(valueFont);
        panel.add(nameValue, gbc);
        y++;

        // Tax ID
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel taxIdLabel = new JLabel("Tax ID:");
        taxIdLabel.setFont(labelFont);
        taxIdLabel.setForeground(new Color(0x212121));
        panel.add(taxIdLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        JLabel taxIdValue = new JLabel(entity.getTaxId() != null ? entity.getTaxId() : "N/A");
        taxIdValue.setFont(valueFont);
        panel.add(taxIdValue, gbc);
        y++;

        // Active Status
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel activeLabel = new JLabel("Status:");
        activeLabel.setFont(labelFont);
        activeLabel.setForeground(new Color(0x212121));
        panel.add(activeLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        String activeStatus = entity.getActive() != null && entity.getActive() ? "Active" : "Inactive";
        JLabel activeValue = new JLabel(activeStatus);
        activeValue.setFont(valueFont);
        activeValue.setForeground(entity.getActive() != null && entity.getActive() ? new Color(0x2E7D32) : new Color(0xC62828));
        panel.add(activeValue, gbc);
        
        return panel;
    }
    
    private JPanel createContactInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        TitledBorder border = BorderFactory.createTitledBorder("Contact Information");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.MODULE_SUPPLIERS_TEXT);
        panel.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(8,12,8,12)));
        panel.setBackground(UIConstants.MODULE_SUPPLIERS_BG);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 12, 6, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = UIConstants.UI_FONT.deriveFont(Font.BOLD);
        Font valueFont = UIConstants.UI_FONT;

        int y = 0;

        // Email
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        emailLabel.setForeground(new Color(0x212121));
        panel.add(emailLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        JLabel emailValue = new JLabel(entity.getEmail() != null ? entity.getEmail() : "N/A");
        emailValue.setFont(valueFont);
        panel.add(emailValue, gbc);
        y++;

        // Fixed Phone
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel fixedPhoneLabel = new JLabel("Fixed Phone:");
        fixedPhoneLabel.setFont(labelFont);
        fixedPhoneLabel.setForeground(new Color(0x212121));
        panel.add(fixedPhoneLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        JLabel fixedPhoneValue = new JLabel(entity.getFixedPhone() != null ? entity.getFixedPhone() : "N/A");
        fixedPhoneValue.setFont(valueFont);
        panel.add(fixedPhoneValue, gbc);
        y++;

        // Mobile Phone
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel mobilePhoneLabel = new JLabel("Mobile Phone:");
        mobilePhoneLabel.setFont(labelFont);
        mobilePhoneLabel.setForeground(new Color(0x212121));
        panel.add(mobilePhoneLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        JLabel mobilePhoneValue = new JLabel(entity.getMobilePhone() != null ? entity.getMobilePhone() : "N/A");
        mobilePhoneValue.setFont(valueFont);
        panel.add(mobilePhoneValue, gbc);
        y++;

        // Address
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.0;
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(labelFont);
        addressLabel.setForeground(new Color(0x212121));
        panel.add(addressLabel, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0;
        JLabel addressValue = new JLabel(entity.getAddress() != null ? entity.getAddress() : "N/A");
        addressValue.setFont(valueFont);
        panel.add(addressValue, gbc);
        
        return panel;
    }
}

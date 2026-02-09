package com.billing.gui;

import com.billing.entity.Article;
import com.billing.entity.ArticleCategory;
import com.billing.entity.ArticleFamily;
import com.billing.entity.Supplier;
import com.billing.entity.Unit;
import com.billing.service.ArticleService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.imageio.ImageIO;

public class ArticleFormDialog extends BaseFormDialog<Article> {
    
    private final ArticleService articleService;
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JComboBox<ArticleFamily> familyComboBox;
    private JComboBox<ArticleCategory> categoryComboBox;
    private JComboBox<Unit> unitComboBox;
    private JComboBox<Supplier> supplierComboBox;
    private JSpinner costPriceSpinner;
    private JSpinner salePriceSpinner;
    private JComboBox<BigDecimal> vatComboBox;
    private JSpinner currentStockSpinner;
    private JSpinner minimumStockSpinner;
    private JTextField barcodeField;
    private JCheckBox activeCheckBox;
    private JLabel imageLabel;
    private JTextArea observationsArea;
    
    // Image handling
    private String imagePath = null;
    private JButton selectImageButton;
    private JButton removeImageButton;
    
    // Validation
    private JLabel salePriceWithVATLabel;
    private JLabel profitMarginLabel;
    
    public ArticleFormDialog(Frame parent, ArticleService articleService) {
        this(parent, articleService, null);
    }
    
    public ArticleFormDialog(Frame parent, ArticleService articleService, Article article) {
        super(parent, article == null ? "Add New Article" : "Edit Article", true);
        this.articleService = articleService;
        this.entity = article;
        
        initializeComponents();
        setupLayout();
        setupFormLayout();
        setupEventHandlers();
        loadComboBoxData();
        
        if (article != null) {
            populateFields();
        } else {
            setDefaultValues();
        }
        
        pack();
        setLocationRelativeTo(parent);
        
        // Focus on first field
        SwingUtilities.invokeLater(() -> nameField.requestFocusInWindow());
    }
    
    private void initializeComponents() {
        // Basic fields
        nameField = new JTextField();
        nameField.setFont(UIConstants.DEFAULT_FONT);
        nameField.setPreferredSize(new Dimension(300, 25));
        
        descriptionArea = new JTextArea(3, 30);
        descriptionArea.setFont(UIConstants.DEFAULT_FONT);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        
        // ComboBoxes
        familyComboBox = new JComboBox<>();
        familyComboBox.setFont(UIConstants.DEFAULT_FONT);
        familyComboBox.setPreferredSize(new Dimension(200, 25));
        
        categoryComboBox = new JComboBox<>();
        categoryComboBox.setFont(UIConstants.DEFAULT_FONT);
        categoryComboBox.setPreferredSize(new Dimension(200, 25));
        
        unitComboBox = new JComboBox<>();
        unitComboBox.setFont(UIConstants.DEFAULT_FONT);
        unitComboBox.setPreferredSize(new Dimension(120, 25));
        
        supplierComboBox = new JComboBox<>();
        supplierComboBox.setFont(UIConstants.DEFAULT_FONT);
        supplierComboBox.setPreferredSize(new Dimension(250, 25));
        
        // Price spinners
        costPriceSpinner = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 999999.99, 0.01));
        costPriceSpinner.setFont(UIConstants.DEFAULT_FONT);
        costPriceSpinner.setPreferredSize(new Dimension(120, 25));
        
        salePriceSpinner = new JSpinner(new SpinnerNumberModel(0.00, 0.00, 999999.99, 0.01));
        salePriceSpinner.setFont(UIConstants.DEFAULT_FONT);
        salePriceSpinner.setPreferredSize(new Dimension(120, 25));
        
        // VAT ComboBox
        BigDecimal[] vatValues = {new BigDecimal("0"), new BigDecimal("4"), new BigDecimal("10"), new BigDecimal("21")};
        vatComboBox = new JComboBox<>(vatValues);
        vatComboBox.setFont(UIConstants.DEFAULT_FONT);
        vatComboBox.setSelectedItem(new BigDecimal("21")); // Default VAT
        
        // Stock spinners
        currentStockSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 999999, 1));
        currentStockSpinner.setFont(UIConstants.DEFAULT_FONT);
        currentStockSpinner.setPreferredSize(new Dimension(100, 25));
        
        minimumStockSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 999999, 1));
        minimumStockSpinner.setFont(UIConstants.DEFAULT_FONT);
        minimumStockSpinner.setPreferredSize(new Dimension(100, 25));
        
        // Barcode field
        barcodeField = new JTextField();
        barcodeField.setFont(UIConstants.DEFAULT_FONT);
        barcodeField.setPreferredSize(new Dimension(200, 25));
        
        // Active checkbox
        activeCheckBox = new JCheckBox("Active Article");
        activeCheckBox.setFont(UIConstants.DEFAULT_FONT);
        activeCheckBox.setSelected(true);
        
        // Image components
        imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(150, 150));
        imageLabel.setBorder(BorderFactory.createEtchedBorder());
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setText("No image");
        
        selectImageButton = new JButton("Select Image");
        selectImageButton.setFont(UIConstants.BUTTON_FONT);
        
        removeImageButton = new JButton("Remove");
        removeImageButton.setFont(UIConstants.BUTTON_FONT);
        removeImageButton.setEnabled(false);
        
        // Observations
        observationsArea = new JTextArea(3, 30);
        observationsArea.setFont(UIConstants.DEFAULT_FONT);
        observationsArea.setLineWrap(true);
        observationsArea.setWrapStyleWord(true);
        
        // Calculation labels
        salePriceWithVATLabel = new JLabel("Price with VAT: €0.00");
        salePriceWithVATLabel.setFont(UIConstants.DEFAULT_FONT);
        salePriceWithVATLabel.setForeground(Color.BLUE);
        
        profitMarginLabel = new JLabel("Margin: 0.00%");
        profitMarginLabel.setFont(UIConstants.DEFAULT_FONT);
        profitMarginLabel.setForeground(Color.GREEN);
    }
    
    private void setupLayout() {
        getContentPane().setBackground(UIConstants.MODULE_PRODUCTS_BG);
    }
    
    @Override
    protected java.awt.Component createFormPanel() {
        // Main panel with tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.DEFAULT_FONT);
        tabbedPane.setBackground(UIConstants.MODULE_PRODUCTS_BG);
        tabbedPane.setFocusable(false);
        
        // Basic data tab
        tabbedPane.addTab("Basic Data", createBasicDataPanel());
        
        // Pricing tab
        tabbedPane.addTab("Pricing & Stock", createPricingPanel());
        
        // Additional data tab
        tabbedPane.addTab("Additional Data", createAdditionalDataPanel());
        
        return tabbedPane;
    }
    
    private JPanel createBasicDataPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIConstants.MODULE_PRODUCTS_BG);
        
        // Add titled border
        TitledBorder border = BorderFactory.createTitledBorder("Basic Information");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.MODULE_PRODUCTS_TEXT);
        panel.setBorder(border);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Row 1: Nom
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Article Name *:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(nameField, gbc);
        
        // Row 2: Descripcio
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Description:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH;
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        panel.add(descScrollPane, gbc);
        
        // Row 3: Familia and Categoria
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Family:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(familyComboBox, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Category:"), gbc);
        
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(categoryComboBox, gbc);
        
        // Row 4: Unitat and Proveidor
        gbc.gridx = 0; gbc.gridy = 3; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Sales Unit:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(unitComboBox, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Supplier:"), gbc);
        
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(supplierComboBox, gbc);
        
        // Row 5: Active checkbox
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        panel.add(activeCheckBox, gbc);
        
        return panel;
    }
    
    private JPanel createPricingPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIConstants.MODULE_PRODUCTS_BG);
        
        // Add titled border
        TitledBorder border = BorderFactory.createTitledBorder("Pricing & Stock Information");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.MODULE_PRODUCTS_TEXT);
        panel.setBorder(border);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Row 1: Preu cost and Preu venda
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Cost Price (€):"), gbc);
        
        gbc.gridx = 1;
        panel.add(costPriceSpinner, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Sale Price (€):"), gbc);
        
        gbc.gridx = 3;
        panel.add(salePriceSpinner, gbc);
        
        // Row 2: IVA and calculations
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("VAT (%):"), gbc);
        
        gbc.gridx = 1;
        panel.add(vatComboBox, gbc);
        
        gbc.gridx = 2; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(salePriceWithVATLabel, gbc);
        
        // Row 3: Margin calculation
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 4;
        panel.add(profitMarginLabel, gbc);
        
        // Row 4: Stock
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Current Stock:"), gbc);
        
        gbc.gridx = 1;
        panel.add(currentStockSpinner, gbc);
        
        gbc.gridx = 2;
        panel.add(new JLabel("Minimum Stock:"), gbc);
        
        gbc.gridx = 3;
        panel.add(minimumStockSpinner, gbc);
        
        return panel;
    }
    
    private JPanel createAdditionalDataPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIConstants.MODULE_PRODUCTS_BG);
        
        // Add titled border
        TitledBorder border = BorderFactory.createTitledBorder("Additional Information");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.MODULE_PRODUCTS_TEXT);
        panel.setBorder(border);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Row 1: Barcode
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Barcode:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(barcodeField, gbc);
        
        // Row 2: Image section
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Article Image:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 1;
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        JPanel imageButtonPanel = new JPanel(new FlowLayout());
        imageButtonPanel.add(selectImageButton);
        imageButtonPanel.add(removeImageButton);
        imagePanel.add(imageButtonPanel, BorderLayout.SOUTH);
        
        panel.add(imagePanel, gbc);
        
        // Row 3: Observations
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1;
        panel.add(new JLabel("Observations:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        JScrollPane obsScrollPane = new JScrollPane(observationsArea);
        panel.add(obsScrollPane, gbc);
        
        return panel;
    }
    
    private void setupEventHandlers() {
        // Price calculation listeners
        costPriceSpinner.addChangeListener(e -> calculatePricing());
        salePriceSpinner.addChangeListener(e -> calculatePricing());
        vatComboBox.addActionListener(e -> calculatePricing());
        
        // Image buttons
        selectImageButton.addActionListener(e -> selectImage());
        removeImageButton.addActionListener(e -> removeImage());
        
        // Base class buttons handled by BaseFormDialog
        // okButton triggers validateForm() and then saveEntity()
        // cancelButton closes the dialog
        
        // Enter key handling - triggers save
        KeyStroke enterKeyStroke = KeyStroke.getKeyStroke("ENTER");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(enterKeyStroke, "ENTER");
        getRootPane().getActionMap().put("ENTER", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                okButton.doClick();
            }
        });
        
        // Escape key handling
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke("ESCAPE");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelButton.doClick();
            }
        });
    }
    
    private void loadComboBoxData() {
        try {
            // Load families
            familyComboBox.removeAllItems();
            familyComboBox.addItem(null);
            for (ArticleFamily family : articleService.getAllFamilies()) {
                familyComboBox.addItem(family);
            }
            
            // Load categories
            categoryComboBox.removeAllItems();
            categoryComboBox.addItem(null);
            for (ArticleCategory category : articleService.getAllCategories()) {
                categoryComboBox.addItem(category);
            }
            
            // Load units
            unitComboBox.removeAllItems();
            unitComboBox.addItem(null);
            for (Unit unit : articleService.getAllUnits()) {
                unitComboBox.addItem(unit);
            }
            
            // Load suppliers
            supplierComboBox.removeAllItems();
            supplierComboBox.addItem(null);
            for (Supplier supplier : articleService.getAllSuppliers()) {
                supplierComboBox.addItem(supplier);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading dropdown data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void calculatePricing() {
        try {
            double costPrice = ((Number) costPriceSpinner.getValue()).doubleValue();
            double salePrice = ((Number) salePriceSpinner.getValue()).doubleValue();
            BigDecimal vat = (BigDecimal) vatComboBox.getSelectedItem();
            
            // Calculate price with VAT
            double vatPercent = vat != null ? vat.doubleValue() / 100.0 : 0.0;
            double salePriceWithVat = salePrice * (1.0 + vatPercent);
            salePriceWithVATLabel.setText(String.format("Price with VAT: €%.2f", salePriceWithVat));
            
            // Calculate profit margin
            double margin = 0.0;
            if (costPrice > 0) {
                margin = ((salePrice - costPrice) / costPrice) * 100.0;
            }
            profitMarginLabel.setText(String.format("Margin: %.2f%%", margin));
            
            // Color coding for margin
            if (margin < 0) {
                profitMarginLabel.setForeground(Color.RED);
            } else if (margin < 10) {
                profitMarginLabel.setForeground(Color.ORANGE);
            } else {
                profitMarginLabel.setForeground(Color.GREEN);
            }
            
        } catch (Exception e) {
            salePriceWithVATLabel.setText("Price with VAT: Error");
            profitMarginLabel.setText("Margin: Error");
        }
    }
    
    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Images (*.jpg, *.jpeg, *.png, *.gif)", "jpg", "jpeg", "png", "gif"));
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = fileChooser.getSelectedFile();
                BufferedImage image = ImageIO.read(selectedFile);
                
                // Resize image if needed
                if (image.getWidth() > 150 || image.getHeight() > 150) {
                    Image scaledImage = image.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaledImage));
                } else {
                    imageLabel.setIcon(new ImageIcon(image));
                }
                
                imageLabel.setText("");
                removeImageButton.setEnabled(true);
                imagePath = selectedFile.getAbsolutePath();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error loading image: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void removeImage() {
        imageLabel.setIcon(null);
        imageLabel.setText("No image");
        imagePath = null;
        removeImageButton.setEnabled(false);
    }
    
    private void loadImageFromPath(String imagePath) {
        try {
            if (imagePath != null && !imagePath.trim().isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    ImageIcon icon = new ImageIcon(imagePath);
                    imageLabel.setIcon(icon);
                    removeImageButton.setEnabled(true);
                } else {
                    imageLabel.setText("Image file not found");
                }
            }
        } catch (Exception e) {
            imageLabel.setText("Error loading image");
        }
    }
    
    private boolean isValidBarcode(String barcode) {
        return barcode.matches("\\d{13}");
    }
    
    @Override
    protected void populateFields() {
        if (entity == null) return;
        
        nameField.setText(entity.getName());
        descriptionArea.setText(entity.getDescription() != null ? entity.getDescription() : "");
        
        // Set combo boxes
        familyComboBox.setSelectedItem(entity.getFamily());
        categoryComboBox.setSelectedItem(entity.getCategory());
        unitComboBox.setSelectedItem(entity.getUnit());
        supplierComboBox.setSelectedItem(entity.getSupplier());
        
        // Set spinners
        costPriceSpinner.setValue(entity.getCostPrice().doubleValue());
        salePriceSpinner.setValue(entity.getSalePrice().doubleValue());
        currentStockSpinner.setValue(entity.getCurrentStock());
        minimumStockSpinner.setValue(entity.getMinimumStock());
        
        // Set VAT
        for (int i = 0; i < vatComboBox.getItemCount(); i++) {
            if (vatComboBox.getItemAt(i).intValue() == entity.getIVAPercent()) {
                vatComboBox.setSelectedIndex(i);
                break;
            }
        }
        
        // Set other fields
        barcodeField.setText(entity.getBarcode() != null ? entity.getBarcode() : "");
        activeCheckBox.setSelected(entity.isActive());
        observationsArea.setText(entity.getObservations() != null ? entity.getObservations() : "");
        
        // Load image if exists
        if (entity.getImage() != null && !entity.getImage().isEmpty()) {
            imagePath = entity.getImage();
            loadImageFromPath(imagePath);
        }
        
        calculatePricing();
    }
    
    @Override
    protected boolean validateForm() {
        // Required field validation
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Article name is required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        
        // Price validation
        double costPrice = ((Number) costPriceSpinner.getValue()).doubleValue();
        double salePrice = ((Number) salePriceSpinner.getValue()).doubleValue();
        
        if (costPrice < 0) {
            JOptionPane.showMessageDialog(this, "Cost price cannot be negative.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            costPriceSpinner.requestFocus();
            return false;
        }
        
        if (salePrice < 0) {
            JOptionPane.showMessageDialog(this, "Sale price cannot be negative.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            salePriceSpinner.requestFocus();
            return false;
        }
        
        // Stock validation
        int currentStock = (Integer) currentStockSpinner.getValue();
        int minimumStock = (Integer) minimumStockSpinner.getValue();
        
        if (currentStock < 0 || minimumStock < 0) {
            JOptionPane.showMessageDialog(this, "Stock values cannot be negative.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        
        // Barcode validation
        String barcode = barcodeField.getText().trim();
        if (!barcode.isEmpty() && !isValidBarcode(barcode)) {
            JOptionPane.showMessageDialog(this, "Invalid barcode. Must have 13 digits.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            barcodeField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    @Override
    protected boolean saveEntity() {
        try {
            if (entity == null) {
                entity = new Article();
            }
            
            // Set field values
            entity.setName(nameField.getText().trim());
            entity.setDescription(descriptionArea.getText().trim());
            entity.setFamily((ArticleFamily) familyComboBox.getSelectedItem());
            entity.setCategory((ArticleCategory) categoryComboBox.getSelectedItem());
            entity.setUnit((Unit) unitComboBox.getSelectedItem());
            entity.setSupplier((Supplier) supplierComboBox.getSelectedItem());
            entity.setCostPrice(BigDecimal.valueOf(((Number) costPriceSpinner.getValue()).doubleValue()));
            entity.setSalePrice(BigDecimal.valueOf(((Number) salePriceSpinner.getValue()).doubleValue()));
            entity.setIVAPercent(((BigDecimal) vatComboBox.getSelectedItem()).intValue());
            entity.setCurrentStock((Integer) currentStockSpinner.getValue());
            entity.setMinimumStock((Integer) minimumStockSpinner.getValue());
            entity.setBarcode(barcodeField.getText().trim());
            entity.setActive(activeCheckBox.isSelected());
            entity.setImage(imagePath);
            entity.setObservations(observationsArea.getText().trim());
            
            // Save article
            if (entity.getId() == null) {
                articleService.createArticle(entity);
            } else {
                articleService.updateArticle(entity);
            }
            
            JOptionPane.showMessageDialog(getParent(),
                "Article saved successfully.",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            
            return true;
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error saving article: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    private void setDefaultValues() {
        // Set default values for new articles
        activeCheckBox.setSelected(true);
        vatComboBox.setSelectedItem(new BigDecimal("21"));
        currentStockSpinner.setValue(0);
        minimumStockSpinner.setValue(0);
        costPriceSpinner.setValue(0.00);
        salePriceSpinner.setValue(0.00);
        
        calculatePricing();
    }
}

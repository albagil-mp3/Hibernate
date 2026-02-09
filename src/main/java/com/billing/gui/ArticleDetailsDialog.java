package com.billing.gui;

import com.billing.entity.Article;
import com.billing.service.ArticleService;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Destination;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Locale;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.time.format.DateTimeFormatter;

public class ArticleDetailsDialog extends BaseDetailsDialog<Article> {
    
    private final ArticleService articleService;
    
    // Buttons (print/save)
    private JButton printButton;
    private JButton savePdfButton;
    
    // Display components
    private JLabel codeLabel;
    private JLabel nameLabel;
    private JTextArea descriptionArea;
    private JLabel familyLabel;
    private JLabel categoryLabel;
    private JLabel unitLabel;
    private JLabel supplierLabel;
    private JLabel costPriceLabel;
    private JLabel salePriceLabel;
    private JLabel ivaLabel;
    private JLabel salePriceWithIVALabel;
    private JLabel profitMarginLabel;
    private JLabel stockActualLabel;
    private JLabel stockMinimLabel;
    private JLabel barcodeLabel;
    private JLabel activeLabel;
    private JLabel imageLabel;
    private JTextArea observationsArea;
    private JLabel dateAddedLabel;
    
    // Status indicators
    private JLabel stockStatusLabel;
    private JLabel marginStatusLabel;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    public ArticleDetailsDialog(Frame parent, Article article, ArticleService articleService) {
        super(parent, article, "Article Details - " + (article != null && article.getName() != null ? article.getName() : "Unknown"));
        this.articleService = articleService;
        // entity field is set by base class constructor
        
        setupEventHandlers();
        populateFields();
    }

    @Override
    protected void onBeforeLayout() {
        initializeComponents();
    }
    
    private void initializeComponents() {
        // Display labels
        codeLabel = createDisplayLabel();
        nameLabel = createDisplayLabel();
        familyLabel = createDisplayLabel();
        categoryLabel = createDisplayLabel();
        unitLabel = createDisplayLabel();
        supplierLabel = createDisplayLabel();
        costPriceLabel = createDisplayLabel();
        salePriceLabel = createDisplayLabel();
        ivaLabel = createDisplayLabel();
        salePriceWithIVALabel = createDisplayLabel();
        profitMarginLabel = createDisplayLabel();
        stockActualLabel = createDisplayLabel();
        stockMinimLabel = createDisplayLabel();
        barcodeLabel = createDisplayLabel();
        activeLabel = createDisplayLabel();
        dateAddedLabel = createDisplayLabel();
        
        // Text areas for longer content
        descriptionArea = new JTextArea(3, 30);
        descriptionArea.setFont(UIConstants.DEFAULT_FONT);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBackground(UIConstants.MODULE_PRODUCTS_BG);
        
        observationsArea = new JTextArea(3, 30);
        observationsArea.setFont(UIConstants.DEFAULT_FONT);
        observationsArea.setEditable(false);
        observationsArea.setLineWrap(true);
        observationsArea.setWrapStyleWord(true);
        observationsArea.setBackground(UIConstants.MODULE_PRODUCTS_BG);
        
        // Image display
        imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(200, 200));
        imageLabel.setBorder(BorderFactory.createEtchedBorder());
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        
        // Status indicators
        stockStatusLabel = createDisplayLabel();
        stockStatusLabel.setFont(new Font(UIConstants.DEFAULT_FONT.getName(), Font.BOLD, UIConstants.DEFAULT_FONT.getSize()));
        
        marginStatusLabel = createDisplayLabel();
        marginStatusLabel.setFont(new Font(UIConstants.DEFAULT_FONT.getName(), Font.BOLD, UIConstants.DEFAULT_FONT.getSize()));
        
        // Buttons - print/save
        printButton = UIConstants.createSecondaryButton("PRINT", UIConstants.loadIcon("/icons/print.png", 16));
        savePdfButton = UIConstants.createSecondaryButton("SAVE PDF", UIConstants.loadIcon("/icons/pdf.png", 16));
    }
    
    private JLabel createDisplayLabel() {
        JLabel label = new JLabel();
        label.setFont(UIConstants.UI_FONT);
        label.setForeground(new Color(0x212121));
        return label;
    }
    
    /**
     * Helper method to cast entity to Article
     */
    private Article getArticle() {
        return (Article) entity;
    }
    
    @Override
    protected JPanel createContentPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(UIConstants.MODULE_PRODUCTS_BG);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JPanel basicPanel = createBasicInfoPanel();
        basicPanel.setAlignmentX(LEFT_ALIGNMENT);
        infoPanel.add(basicPanel);
        infoPanel.add(Box.createVerticalStrut(10));

        JPanel pricingPanel = createPricingStockPanel();
        pricingPanel.setAlignmentX(LEFT_ALIGNMENT);
        infoPanel.add(pricingPanel);
        infoPanel.add(Box.createVerticalStrut(10));

        JPanel additionalPanel = createAdditionalInfoPanel();
        additionalPanel.setAlignmentX(LEFT_ALIGNMENT);
        infoPanel.add(additionalPanel);

        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);
        contentWrapper.add(infoPanel, BorderLayout.CENTER);

        mainPanel.add(contentWrapper, BorderLayout.CENTER);

        return mainPanel;
    }
    
    
    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        javax.swing.border.TitledBorder border = BorderFactory.createTitledBorder("Basic Information");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.MODULE_PRODUCTS_TEXT);
        panel.setBorder(border);
        panel.setBackground(UIConstants.MODULE_PRODUCTS_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 5, 10);
        
        // Row 1: Code and Active status
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(createBoldLabel("Code:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(codeLabel, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        panel.add(createBoldLabel("Active:"), gbc);
        
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 0.5;
        panel.add(activeLabel, gbc);
        
        // Row 2: Name
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        panel.add(createBoldLabel("Article Name:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(nameLabel, gbc);
        
        // Row 3: Description
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.weightx = 0.0;
        panel.add(createBoldLabel("Description:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.3;
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setPreferredSize(new Dimension(400, 75));
        descScrollPane.setBorder(BorderFactory.createEtchedBorder());
        UIConstants.enhanceScroll(descScrollPane);
        panel.add(descScrollPane, gbc);
        
        // Row 4: Family and Category
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0; gbc.weighty = 0.0;
        panel.add(createBoldLabel("Family:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(familyLabel, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        panel.add(createBoldLabel("Category:"), gbc);
        
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(categoryLabel, gbc);
        
        // Row 5: Unit and Supplier
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.0;
        panel.add(createBoldLabel("Unit:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(unitLabel, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        panel.add(createBoldLabel("Supplier:"), gbc);
        
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(supplierLabel, gbc);
        
        // Row 6: Date Added
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0.0;
        panel.add(createBoldLabel("Date Added:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(dateAddedLabel, gbc);
        
        return panel;
    }
    
    private JPanel createPricingStockPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        javax.swing.border.TitledBorder border = BorderFactory.createTitledBorder("Pricing and Stock");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.MODULE_PRODUCTS_TEXT);
        panel.setBorder(border);
        panel.setBackground(UIConstants.MODULE_PRODUCTS_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 5, 10);

        // Row 1: Cost price and selling price
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; gbc.weightx = 0.0;
        panel.add(createBoldLabel("Cost Price:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(costPriceLabel, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        panel.add(createBoldLabel("Sale Price:"), gbc);
        
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(salePriceLabel, gbc);
        
        // Row 2: IVA and price with IVA
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.0;
        panel.add(createBoldLabel("IVA:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(ivaLabel, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        panel.add(createBoldLabel("Price with IVA:"), gbc);
        
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(salePriceWithIVALabel, gbc);
        
        // Row 3: Margin and status
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0;
        panel.add(createBoldLabel("Profit margin:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(profitMarginLabel, gbc);
        
        gbc.gridx = 2; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(marginStatusLabel, gbc);

        // Row 4: Current stock and minimum stock
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1; gbc.weightx = 0.0;
        panel.add(createBoldLabel("Current stock:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(stockActualLabel, gbc);
        
        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        panel.add(createBoldLabel("Minimum stock:"), gbc);
        
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(stockMinimLabel, gbc);
        
        // Row 5: Stock status
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(stockStatusLabel, gbc);
        
        return panel;
    }
    
    private JPanel createAdditionalInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        javax.swing.border.TitledBorder border = BorderFactory.createTitledBorder("Additional Information");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.MODULE_PRODUCTS_TEXT);
        panel.setBorder(border);
        panel.setBackground(UIConstants.MODULE_PRODUCTS_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 5, 10);
        
        // Row 1: Barcode
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 0.0;
        panel.add(createBoldLabel("Barcode:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(barcodeLabel, gbc);
        
        // Row 2: Image section
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        panel.add(createBoldLabel("Product image:"), gbc);
        
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.CENTER;
        panel.add(imageLabel, gbc);
        
        // Row 3: Observations
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.NORTHWEST; gbc.weightx = 0.0;
        panel.add(createBoldLabel("Observations:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        JScrollPane obsScrollPane = new JScrollPane(observationsArea);
        obsScrollPane.setPreferredSize(new Dimension(400, 150));
        obsScrollPane.setBorder(BorderFactory.createEtchedBorder());
        UIConstants.enhanceScroll(obsScrollPane);
        panel.add(obsScrollPane, gbc);
        
        return panel;
    }
    
    private JLabel createBoldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font(UIConstants.DEFAULT_FONT.getName(), Font.BOLD, UIConstants.DEFAULT_FONT.getSize()));
        label.setForeground(new Color(0x212121));
        return label;
    }
    
    private void setupEventHandlers() {
        printButton.addActionListener(e -> printArticle());
        savePdfButton.addActionListener(e -> saveArticlePdf());
        
        // Escape key to close
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke("ESCAPE");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
    
    @Override
    protected void handlePrint() {
        printArticle();
    }
    
    private void populateFields() {
        codeLabel.setText(" " + (getArticle().getCode() != null ? getArticle().getCode() : "Not available"));
        nameLabel.setText(" " + (getArticle().getName() != null ? getArticle().getName() : ""));
        descriptionArea.setText(getArticle().getDescription() != null ? getArticle().getDescription() : "");
        
        familyLabel.setText(" " + (getArticle().getFamily() != null ? getArticle().getFamily().getName() : "Not assigned"));
        categoryLabel.setText(" " + (getArticle().getCategory() != null ? getArticle().getCategory().getName() : "Not assigned"));
        unitLabel.setText(" " + (getArticle().getUnit() != null ? getArticle().getUnit().getName() : "Not assigned"));
        supplierLabel.setText(" " + (getArticle().getSupplier() != null ? getArticle().getSupplier().getName() : "Not assigned"));
        
        // Pricing information
        costPriceLabel.setText(" " + String.format("%.2f €", getArticle().getCostPrice()));
        salePriceLabel.setText(" " + String.format("%.2f €", getArticle().getSalePrice()));
        ivaLabel.setText(" " + String.format("%d%%", getArticle().getIVAPercent()));
        
        // Get price with IVA
        double priceWithIva = getArticle().getPriceWithIVA().doubleValue();
        salePriceWithIVALabel.setText(" " + String.format("%.2f €", priceWithIva));
        
        // Calculate and display margin
        double margin = getArticle().calculateProfitMargin().doubleValue();
        profitMarginLabel.setText(" " + String.format("%.2f%%", margin));
        
        // Margin status
        if (margin < 0) {
            marginStatusLabel.setText("Negative margin - Losses incurred");
            marginStatusLabel.setForeground(Color.RED);
        } else if (margin < 10) {
            marginStatusLabel.setText("Low margin - Review pricing");
            marginStatusLabel.setForeground(Color.ORANGE);
        } else {
            marginStatusLabel.setText("Adequate margin");
            marginStatusLabel.setForeground(Color.GREEN);
        }
        
        // Stock information
        stockActualLabel.setText(" " + getArticle().getCurrentStock().toString());
        stockMinimLabel.setText(" " + getArticle().getMinimumStock().toString());
        
        // Stock status
        if (getArticle().isLowStock()) {
            stockStatusLabel.setText("LOW STOCK - Replenish urgently");
            stockStatusLabel.setForeground(Color.RED);
        } else if (getArticle().getCurrentStock() <= getArticle().getMinimumStock() * 1.5) {
            stockStatusLabel.setText("Stock approaching minimum level");
            stockStatusLabel.setForeground(Color.ORANGE);
        } else {
            stockStatusLabel.setText("Adequate stock");
            stockStatusLabel.setForeground(Color.GREEN);
        }
        
        // Additional information
        barcodeLabel.setText(" " + (getArticle().getBarcode() != null ? getArticle().getBarcode() : "Not assigned"));
        activeLabel.setText(" " + (getArticle().isActive() ? "Yes" : "No"));
        
        if (!getArticle().isActive()) {
            activeLabel.setForeground(Color.RED);
        } else {
            activeLabel.setForeground(Color.GREEN);
        }
        
        observationsArea.setText(getArticle().getObservations() != null ? getArticle().getObservations() : "");
        
        // Date
        if (getArticle().getDateAdded() != null) {
            dateAddedLabel.setText(" " + getArticle().getDateAdded().format(dateFormatter));
        } else {
            dateAddedLabel.setText(" Not available");
        }
        
        // Image
        if (getArticle().getImage() != null && !getArticle().getImage().trim().isEmpty()) {
            try {
                File imageFile = new File(getArticle().getImage());
                if (imageFile.exists()) {
                    ImageIcon icon = new ImageIcon(getArticle().getImage());
                    Image scaledImage = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaledImage));
                }
            } catch (Exception e) {
                // Silently ignore errors - don't show anything if image can't be loaded
            }
        }
        // If no image, imageLabel remains empty (no text or icon)

    }

    @Override
    protected void configureDialog() {
        super.configureDialog();
        getContentPane().setBackground(UIConstants.MODULE_PRODUCTS_BG);
    }

    @Override
    protected JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(printButton);
        buttonPanel.add(savePdfButton);
        return buttonPanel;
    }
    
    private void printArticle() {
        try {
            // Build print content
            StringBuilder printContent = new StringBuilder();
            printContent.append("ARTICLE DETAILS REPORT\n");
            printContent.append("======================\n\n");
            printContent.append("Code: ").append(getArticle().getCode()).append("\n");
            printContent.append("Name: ").append(getArticle().getName()).append("\n");
            printContent.append("Description: ").append(getArticle().getDescription()).append("\n");
            printContent.append("Family: ").append(getArticle().getFamily() != null ? getArticle().getFamily().getName() : "N/A").append("\n");
            printContent.append("Category: ").append(getArticle().getCategory() != null ? getArticle().getCategory().getName() : "N/A").append("\n");
            printContent.append("Unit: ").append(getArticle().getUnit() != null ? getArticle().getUnit().getName() : "N/A").append("\n");
            printContent.append("Supplier: ").append(getArticle().getSupplier() != null ? getArticle().getSupplier().getName() : "N/A").append("\n");
            printContent.append("Cost Price: €").append(String.format("%.2f", getArticle().getCostPrice())).append("\n");
            printContent.append("Sale Price: €").append(String.format("%.2f", getArticle().getSalePrice())).append("\n");
            printContent.append("VAT: ").append(getArticle().getIVAPercent()).append("%\n");
            printContent.append("Current Stock: ").append(getArticle().getCurrentStock()).append("\n");
            printContent.append("Minimum Stock: ").append(getArticle().getMinimumStock()).append("\n");
            printContent.append("Barcode: ").append(getArticle().getBarcode()).append("\n");
            printContent.append("Active: ").append(getArticle().isActive() ? "Yes" : "No").append("\n");
            printContent.append("Date Added: ").append(getArticle().getDateAdded() != null ? getArticle().getDateAdded().format(dateFormatter) : "N/A").append("\n");
            printContent.append("Observations: ").append(getArticle().getObservations()).append("\n");
            
            // Create a simple text area for printing
            JTextArea printArea = new JTextArea(printContent.toString());
            printArea.setFont(UIConstants.DEFAULT_FONT);
            
            try {
                PrinterJob job = PrinterJob.getPrinterJob();
                job.setJobName("Print - " + (getArticle().getName() != null ? getArticle().getName() : stripPdfExtension(getArticle().getCode())));
                job.setPrintable(printArea.getPrintable(null, null));
                if (job.printDialog()) {
                    job.print();
                }
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this,
                    "Print operation cancelled.",
                    "Print",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Print error: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private File choosePdfTargetFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save PDF");
        chooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
        chooser.setSelectedFile(new File(buildSuggestedPdfFileName()));

        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return null;
        }

        File selectedFile = chooser.getSelectedFile();
        String lowerName = selectedFile.getName().toLowerCase(Locale.ROOT);
        if (!lowerName.endsWith(".pdf")) {
            selectedFile = new File(selectedFile.getParentFile(), selectedFile.getName() + ".pdf");
        }

        if (selectedFile.exists()) {
            int overwrite = JOptionPane.showConfirmDialog(
                this,
                "The file already exists. Do you want to replace it?",
                "Confirm Save",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (overwrite != JOptionPane.YES_OPTION) {
                return null;
            }
        }

        return selectedFile;
    }

    private void saveArticlePdf() {
        try {
            // Build print content
            StringBuilder printContent = new StringBuilder();
            printContent.append("ARTICLE DETAILS REPORT\n");
            printContent.append("======================\n\n");
            printContent.append("Code: ").append(getArticle().getCode()).append("\n");
            printContent.append("Name: ").append(getArticle().getName()).append("\n");
            printContent.append("Description: ").append(getArticle().getDescription()).append("\n");
            printContent.append("Family: ").append(getArticle().getFamily() != null ? getArticle().getFamily().getName() : "N/A").append("\n");
            printContent.append("Category: ").append(getArticle().getCategory() != null ? getArticle().getCategory().getName() : "N/A").append("\n");
            printContent.append("Unit: ").append(getArticle().getUnit() != null ? getArticle().getUnit().getName() : "N/A").append("\n");
            printContent.append("Supplier: ").append(getArticle().getSupplier() != null ? getArticle().getSupplier().getName() : "N/A").append("\n");
            printContent.append("Cost Price: €").append(String.format("%.2f", getArticle().getCostPrice())).append("\n");
            printContent.append("Sale Price: €").append(String.format("%.2f", getArticle().getSalePrice())).append("\n");
            printContent.append("VAT: ").append(getArticle().getIVAPercent()).append("%\n");
            printContent.append("Current Stock: ").append(getArticle().getCurrentStock()).append("\n");
            printContent.append("Minimum Stock: ").append(getArticle().getMinimumStock()).append("\n");
            printContent.append("Barcode: ").append(getArticle().getBarcode()).append("\n");
            printContent.append("Active: ").append(getArticle().isActive() ? "Yes" : "No").append("\n");
            printContent.append("Date Added: ").append(getArticle().getDateAdded() != null ? getArticle().getDateAdded().format(dateFormatter) : "N/A").append("\n");
            printContent.append("Observations: ").append(getArticle().getObservations()).append("\n");

            JTextArea printArea = new JTextArea(printContent.toString());
            printArea.setFont(UIConstants.DEFAULT_FONT);

            File targetFile = choosePdfTargetFile();
            if (targetFile == null) {
                return;
            }

            PrinterJob job = PrinterJob.getPrinterJob();
            job.setJobName(stripPdfExtension(targetFile.getName()));
            job.setPrintable(printArea.getPrintable(null, null));

            PrintRequestAttributeSet attributes = new HashPrintRequestAttributeSet();
            attributes.add(new Destination(targetFile.toURI()));

            try {
                PrintService pdfService = findPdfPrintService();
                if (pdfService != null) {
                    job.setPrintService(pdfService);
                    job.print(attributes);
                } else {
                    job.print(attributes);
                }
                JOptionPane.showMessageDialog(this, "PDF saved: " + targetFile.getAbsolutePath(), "Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(this, "Could not save PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error saving PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private PrintService findPdfPrintService() {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        PrintService fallback = null;
        for (PrintService service : services) {
            String name = service.getName();
            if (name == null) {
                continue;
            }
            String normalized = name.toLowerCase(Locale.ROOT);
            if (normalized.equals("microsoft print to pdf")) {
                return service;
            }
            if (normalized.contains("pdf")) {
                fallback = service;
            }
        }
        return fallback;
    }

    private String buildSuggestedPdfFileName() {
        String code = getArticle().getCode();
        String name = getArticle().getName();
        String safeName = (name == null ? "" : name).replaceAll("[^A-Za-z0-9_-]+", "_").trim();

        String base;
        if (code != null && !code.trim().isEmpty()) {
            base = code.trim();
            if (!safeName.isEmpty()) {
                base = base + "_" + safeName;
            }
        } else if (!safeName.isEmpty()) {
            base = safeName;
        } else {
            base = "Article_Details";
        }

        return base + ".pdf";
    }

    private String stripPdfExtension(String fileName) {
        if (fileName == null) {
            return "Article_Details";
        }
        String lowerName = fileName.toLowerCase(Locale.ROOT);
        if (lowerName.endsWith(".pdf")) {
            return fileName.substring(0, fileName.length() - 4);
        }
        return fileName;
    }
}
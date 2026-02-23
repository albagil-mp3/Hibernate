package com.billing.gui;

import com.billing.service.ItemReportService;
import com.billing.dto.ItemDetailsView;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Destination;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

import java.awt.*;
import java.awt.print.PrinterJob;
import java.awt.print.PrinterException;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Dialog for displaying complete item details in a read-only format
 */
public class ItemDetailsDialog extends BaseDetailsDialog<Void> {
    
    private final ItemReportService reportService;
    private final ItemDetailsView view;
    
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
    
    public ItemDetailsDialog(Frame parent, ItemDetailsView view, ItemReportService reportService) {
        super(parent, null, "Item Details - " + (view != null && view.name != null ? view.name : "Unknown"));
        this.view = view;
        this.reportService = reportService;

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
        descriptionArea.setFont(UIConstants.UI_FONT);
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBackground(Color.WHITE);
        
        observationsArea = new JTextArea(3, 30);
        observationsArea.setFont(UIConstants.UI_FONT);
        observationsArea.setEditable(false);
        observationsArea.setLineWrap(true);
        observationsArea.setWrapStyleWord(true);
        observationsArea.setBackground(Color.WHITE);
        
        // Image display
        imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(80, 80));
        imageLabel.setBorder(BorderFactory.createEtchedBorder());
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(Color.WHITE);
        
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
    
    // No entity access: this dialog is a pure view and uses supplied ItemDetailsView
    
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

        JPanel classification = createClassificationPanel();
        classification.setAlignmentX(Component.LEFT_ALIGNMENT);
        classification.setMaximumSize(new Dimension(Integer.MAX_VALUE, classification.getPreferredSize().height));
        infoPanel.add(classification);
        infoPanel.add(Box.createVerticalStrut(8));

        JPanel pricing = createPricingPanel();
        pricing.setAlignmentX(Component.LEFT_ALIGNMENT);
        pricing.setMaximumSize(new Dimension(Integer.MAX_VALUE, pricing.getPreferredSize().height));
        infoPanel.add(pricing);
        infoPanel.add(Box.createVerticalStrut(8));

        JPanel stock = createStockPanel();
        stock.setAlignmentX(Component.LEFT_ALIGNMENT);
        stock.setMaximumSize(new Dimension(Integer.MAX_VALUE, stock.getPreferredSize().height));
        infoPanel.add(stock);
        infoPanel.add(Box.createVerticalStrut(8));

        JPanel additional = createAdditionalInfoPanel();
        additional.setAlignmentX(Component.LEFT_ALIGNMENT);
        additional.setMaximumSize(new Dimension(Integer.MAX_VALUE, additional.getPreferredSize().height));
        infoPanel.add(additional);

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
        border.setTitleColor(UIConstants.MODULE_ITEMS_TEXT);
        panel.setBorder(border);
        panel.setBackground(UIConstants.MODULE_ITEMS_BG);
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
        panel.add(createBoldLabel("Item Name:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(nameLabel, gbc);
        
        // Row 3: Description
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; gbc.weightx = 0.0;
        panel.add(createBoldLabel("Description:"), gbc);
        
        gbc.gridx = 1; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.0;
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        descScrollPane.setPreferredSize(new Dimension(400, 60));
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
    
    private JPanel createPricingPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        javax.swing.border.TitledBorder border = BorderFactory.createTitledBorder("Pricing");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.MODULE_ITEMS_TEXT);
        panel.setBorder(border);
        panel.setBackground(UIConstants.MODULE_ITEMS_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 5, 10);

        // Row 1: Cost price and selling price
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1; gbc.weightx = 0.0; gbc.anchor = GridBagConstraints.WEST;
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

        // Row 3: Margin
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.0;
        panel.add(createBoldLabel("Profit margin:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(profitMarginLabel, gbc);

        gbc.gridx = 2; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(marginStatusLabel, gbc);

        return panel;
    }

    private JPanel createStockPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        javax.swing.border.TitledBorder border = BorderFactory.createTitledBorder("Stock");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.MODULE_ITEMS_TEXT);
        panel.setBorder(border);
        panel.setBackground(UIConstants.MODULE_ITEMS_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 5, 10);

        // Current and minimum stock
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(createBoldLabel("Current stock:"), gbc);

        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(stockActualLabel, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        panel.add(createBoldLabel("Minimum stock:"), gbc);

        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(stockMinimLabel, gbc);

        // Stock status
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(stockStatusLabel, gbc);

        return panel;
    }

    private JPanel createClassificationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        javax.swing.border.TitledBorder border = BorderFactory.createTitledBorder("Classification");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.MODULE_ITEMS_TEXT);
        panel.setBorder(border);
        panel.setBackground(UIConstants.MODULE_ITEMS_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 5, 10);

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(createBoldLabel("Family:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(familyLabel, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        panel.add(createBoldLabel("Category:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(categoryLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        panel.add(createBoldLabel("Unit:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(unitLabel, gbc);

        gbc.gridx = 2; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0;
        panel.add(createBoldLabel("Supplier:"), gbc);
        gbc.gridx = 3; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(supplierLabel, gbc);

        return panel;
    }
    
    private JPanel createAdditionalInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        javax.swing.border.TitledBorder border = BorderFactory.createTitledBorder("Additional Information");
        border.setTitleFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        border.setTitleColor(UIConstants.MODULE_ITEMS_TEXT);
        panel.setBorder(border);
        panel.setBackground(UIConstants.MODULE_ITEMS_BG);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 5, 10);
        
        // Row 1: Barcode
        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 0.0;
        panel.add(createBoldLabel("Barcode:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
        panel.add(barcodeLabel, gbc);
        
        // Row 2: Image small preview inside additional info
        gbc.gridx = 0; gbc.gridy = 1; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0.0; gbc.anchor = GridBagConstraints.WEST;
        panel.add(createBoldLabel("Product image:"), gbc);

        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.NONE;
        panel.add(imageLabel, gbc);
        
        // Row 3: Observations
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.NORTHWEST; gbc.weightx = 0.0;
        panel.add(createBoldLabel("Observations:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 0.0;
        JScrollPane obsScrollPane = new JScrollPane(observationsArea);
        obsScrollPane.setPreferredSize(new Dimension(400, 120));
        obsScrollPane.setBorder(BorderFactory.createEtchedBorder());
        UIConstants.enhanceScroll(obsScrollPane);
        panel.add(obsScrollPane, gbc);
        
        return panel;
    }
    
    private JLabel createBoldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(UIConstants.UI_FONT.deriveFont(Font.BOLD));
        label.setForeground(new Color(0x212121));
        return label;
    }
    
    private void setupEventHandlers() {
        printButton.addActionListener(e -> {
            if (reportService != null) reportService.printReport(view, this);
        });
        savePdfButton.addActionListener(e -> {
            if (reportService != null) reportService.exportPdf(view, this);
        });
        
        // Escape key to close
        KeyStroke escapeKeyStroke = KeyStroke.getKeyStroke("ESCAPE");
        getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKeyStroke, "ESCAPE");
        getRootPane().getActionMap().put("ESCAPE", new AbstractAction() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });
    }
    
    @Override
    protected void handlePrint() {
        printItem();
    }
    
    private void populateFields() {
        if (view == null) return;

        codeLabel.setText(" " + (view.code != null ? view.code : "Not available"));
        nameLabel.setText(" " + (view.name != null ? view.name : ""));
        descriptionArea.setText(view.description != null ? view.description : "");

        familyLabel.setText(" " + (view.family != null ? view.family : "Not assigned"));
        categoryLabel.setText(" " + (view.category != null ? view.category : "Not assigned"));
        unitLabel.setText(" ");
        supplierLabel.setText(" " + (view.supplier != null ? view.supplier : "Not assigned"));

        // Pricing information
        costPriceLabel.setText(" " + (view.costPrice != null ? String.format("%.2f €", view.costPrice) : "0.00 €"));
        salePriceLabel.setText(" " + (view.salePrice != null ? String.format("%.2f €", view.salePrice) : "0.00 €"));
        ivaLabel.setText(" " + (view.vatPercent != null ? String.format("%d%%", view.vatPercent) : "0%"));
        salePriceWithIVALabel.setText(" " + (view.priceWithVat != null ? String.format("%.2f €", view.priceWithVat) : "0.00 €"));

        profitMarginLabel.setText(" " + (view.marginPercent != null ? String.format("%.2f%%", view.marginPercent) : "0.00%"));
        marginStatusLabel.setText("Margin: " + (view.marginStatus != null ? view.marginStatus : "N/A"));

        // Stock information
        stockActualLabel.setText(" " + (view.stock != null ? view.stock : 0));
        stockMinimLabel.setText(" ");
        stockStatusLabel.setText("Stock status: " + (view.stockStatus != null ? view.stockStatus : "N/A"));

        // Additional information
        barcodeLabel.setText(" " + (view.barcode != null ? view.barcode : "Not assigned"));
        activeLabel.setText(" " + (view.active != null && view.active ? "Yes" : "No"));
        if (view.active == null || !view.active) activeLabel.setForeground(Color.RED); else activeLabel.setForeground(Color.GREEN);
        observationsArea.setText(view.observations != null ? view.observations : "");

        if (view.dateAdded != null) {
            dateAddedLabel.setText(" " + view.dateAdded.format(dateFormatter));
        } else {
            dateAddedLabel.setText(" Not available");
        }

        // Image
        if (view.imagePath != null && !view.imagePath.trim().isEmpty()) {
            try {
                File imageFile = new File(view.imagePath);
                if (imageFile.exists()) {
                    ImageIcon icon = new ImageIcon(view.imagePath);
                    Image scaledImage = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaledImage));
                }
            } catch (Exception e) {
                // ignore
            }
        }

    }

    @Override
    protected void configureDialog() {
        super.configureDialog();
        getContentPane().setBackground(UIConstants.MODULE_ITEMS_BG);
    }

    @Override
    protected JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(printButton);
        buttonPanel.add(savePdfButton);
        return buttonPanel;
    }
    
    private void printItem() {
        try {
            if (view == null) return;
            // Build print content
            StringBuilder printContent = new StringBuilder();
            printContent.append("ITEM DETAILS REPORT\n");
            printContent.append("======================\n\n");
            printContent.append("Code: ").append(view.code).append("\n");
            printContent.append("Name: ").append(view.name).append("\n");
            printContent.append("Description: ").append(view.description).append("\n");
            printContent.append("Family: ").append(view.family != null ? view.family : "N/A").append("\n");
            printContent.append("Category: ").append(view.category != null ? view.category : "N/A").append("\n");
            printContent.append("Supplier: ").append(view.supplier != null ? view.supplier : "N/A").append("\n");
            printContent.append("Cost Price: €").append(String.format("%.2f", view.costPrice != null ? view.costPrice : 0.0)).append("\n");
            printContent.append("Sale Price: €").append(String.format("%.2f", view.salePrice != null ? view.salePrice : 0.0)).append("\n");
            printContent.append("VAT: ").append(view.vatPercent != null ? view.vatPercent : 0).append("%\n");
            printContent.append("Current Stock: ").append(view.stock != null ? view.stock : 0).append("\n");
            printContent.append("Barcode: ").append(view.barcode).append("\n");
            printContent.append("Active: ").append(view.active != null && view.active ? "Yes" : "No").append("\n");
            printContent.append("Date Added: ").append(view.dateAdded != null ? view.dateAdded.format(dateFormatter) : "N/A").append("\n");
            printContent.append("Observations: ").append(view.observations).append("\n");
            
            // Create a simple text area for printing
            JTextArea printArea = new JTextArea(printContent.toString());
            printArea.setFont(UIConstants.DEFAULT_FONT);
            
            try {
                PrinterJob job = PrinterJob.getPrinterJob();
                job.setJobName("Print - " + (view.name != null ? view.name : stripPdfExtension(view.code)));
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
            if (view == null) return;
            // Build print content
            StringBuilder printContent = new StringBuilder();
            printContent.append("ITEM DETAILS REPORT\n");
            printContent.append("======================\n\n");
            printContent.append("Code: ").append(view.code).append("\n");
            printContent.append("Name: ").append(view.name).append("\n");
            printContent.append("Description: ").append(view.description).append("\n");
            printContent.append("Family: ").append(view.family != null ? view.family : "N/A").append("\n");
            printContent.append("Category: ").append(view.category != null ? view.category : "N/A").append("\n");
            printContent.append("Supplier: ").append(view.supplier != null ? view.supplier : "N/A").append("\n");
            printContent.append("Cost Price: €").append(String.format("%.2f", view.costPrice != null ? view.costPrice : 0.0)).append("\n");
            printContent.append("Sale Price: €").append(String.format("%.2f", view.salePrice != null ? view.salePrice : 0.0)).append("\n");
            printContent.append("VAT: ").append(view.vatPercent != null ? view.vatPercent : 0).append("%\n");
            printContent.append("Current Stock: ").append(view.stock != null ? view.stock : 0).append("\n");
            printContent.append("Barcode: ").append(view.barcode).append("\n");
            printContent.append("Active: ").append(view.active != null && view.active ? "Yes" : "No").append("\n");
            printContent.append("Date Added: ").append(view.dateAdded != null ? view.dateAdded.format(dateFormatter) : "N/A").append("\n");
            printContent.append("Observations: ").append(view.observations).append("\n");
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
        String code = view != null ? view.code : null;
        String name = view != null ? view.name : null;
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

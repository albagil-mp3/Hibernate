package com.billing.service;

import com.billing.model.item.Item;
import com.billing.dto.ItemDetailsView;
import com.billing.gui.UIConstants;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Destination;
import java.awt.*;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Service responsible for preparing item details view, building printable text,
 * and handling print / PDF export operations. Keeps business calculations
 * out of the Swing view layer.
 */
public class ItemReportService {

    public ItemDetailsView buildView(Item item) {
        if (item == null) return null;
        ItemDetailsView v = new ItemDetailsView();
        v.code = item.getCode();
        v.name = item.getName();
        v.description = item.getDescription();
        v.family = item.getFamily() != null ? item.getFamily().getName() : "Not assigned";
        v.category = item.getCategory() != null ? item.getCategory().getName() : "Not assigned";
        v.supplier = item.getSupplier() != null ? item.getSupplier().getName() : "Not assigned";
        v.salePrice = item.getSalePrice();
        v.costPrice = item.getCostPrice();
        v.vatPercent = item.getIVAPercent();
        v.priceWithVat = calculatePriceWithIVA(v.salePrice, v.vatPercent).setScale(2, RoundingMode.HALF_UP);

        // margin percent
        BigDecimal marginBD;
        if (v.costPrice == null || v.costPrice.compareTo(BigDecimal.ZERO) == 0) {
            marginBD = BigDecimal.ZERO;
        } else {
            marginBD = v.salePrice.subtract(v.costPrice)
                .divide(v.costPrice, 6, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
        }
        v.marginPercent = marginBD;

        double marginDouble = v.marginPercent.doubleValue();
        if (marginDouble < 0) v.marginStatus = "NEGATIVE";
        else if (marginDouble < 10) v.marginStatus = "LOW";
        else v.marginStatus = "ADEQUATE";

        v.stock = item.getCurrentStock();
        if (item.isLowStock()) v.stockStatus = "LOW";
        else if (v.stock != null && item.getMinimumStock() != null && v.stock <= item.getMinimumStock() * 1.5) v.stockStatus = "APPROACHING MINIMUM";
        else v.stockStatus = "ADEQUATE";

        v.barcode = item.getBarcode();
        v.active = item.isActive();
        v.observations = item.getObservations();
        v.dateAdded = item.getDateAdded();
        v.imagePath = item.getImage();

        return v;
    }

    public String buildReportText(ItemDetailsView v) {
        if (v == null) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("ITEM DETAILS REPORT\n");
        sb.append("======================\n\n");
        sb.append("Code: ").append(v.code).append("\n");
        sb.append("Name: ").append(v.name).append("\n");
        sb.append("Description: ").append(v.description != null ? v.description : "").append("\n");
        sb.append("Family: ").append(v.family).append("\n");
        sb.append("Category: ").append(v.category).append("\n");
        sb.append("Supplier: ").append(v.supplier).append("\n");
        sb.append("Cost Price: €").append(formatAmount(v.costPrice)).append("\n");
        sb.append("Sale Price: €").append(formatAmount(v.salePrice)).append("\n");
        sb.append("VAT: ").append(v.vatPercent).append("%\n");
        sb.append("Price with VAT: €").append(formatAmount(v.priceWithVat)).append("\n");
        sb.append("Profit margin: ").append(formatAmount(v.marginPercent)).append("% (").append(v.marginStatus).append(")\n");
        sb.append("Current Stock: ").append(v.stock).append("\n");
        sb.append("Stock status: ").append(v.stockStatus).append("\n");
        sb.append("Barcode: ").append(v.barcode).append("\n");
        sb.append("Active: ").append(v.active != null && v.active ? "Yes" : "No").append("\n");
        sb.append("Date Added: ").append(v.dateAdded != null ? v.dateAdded.toString() : "N/A").append("\n");
        sb.append("Observations: ").append(v.observations != null ? v.observations : "").append("\n");
        return sb.toString();
    }

    public void printReport(ItemDetailsView view, Component parent) {
        try {
            String text = buildReportText(view);
            JTextArea printArea = new JTextArea(text);
            printArea.setFont(UIConstants.DEFAULT_FONT);

            PrinterJob job = PrinterJob.getPrinterJob();
            job.setJobName("Print - " + (view.name != null ? view.name : (view.code != null ? view.code : "Item")) );
            job.setPrintable(printArea.getPrintable(null, null));
            if (job.printDialog()) {
                job.print();
            }
        } catch (PrinterException e) {
            JOptionPane.showMessageDialog(parent, "Print operation cancelled.", "Print", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void exportPdf(ItemDetailsView view, Component parent) {
        try {
            String text = buildReportText(view);
            JTextArea printArea = new JTextArea(text);
            printArea.setFont(UIConstants.DEFAULT_FONT);

            File targetFile = choosePdfTargetFile(parent, suggestedPdfFileName(view));
            if (targetFile == null) return;

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
                JOptionPane.showMessageDialog(parent, "PDF saved: " + targetFile.getAbsolutePath(), "Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (PrinterException e) {
                JOptionPane.showMessageDialog(parent, "Could not save PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(parent, "Error saving PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String formatAmount(BigDecimal d) {
        if (d == null) return "0.00";
        return d.setScale(2, RoundingMode.HALF_UP).toString();
    }

    private BigDecimal calculatePriceWithIVA(BigDecimal basePrice, Integer ivaPercent) {
        if (basePrice == null || ivaPercent == null) return BigDecimal.ZERO;
        BigDecimal ivaMultiplier = BigDecimal.ONE.add(BigDecimal.valueOf(ivaPercent).divide(BigDecimal.valueOf(100)));
        return basePrice.multiply(ivaMultiplier);
    }

    private File choosePdfTargetFile(Component parent, String suggestedName) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save PDF");
        chooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
        chooser.setSelectedFile(new File(suggestedName));

        int result = chooser.showSaveDialog(parent);
        if (result != JFileChooser.APPROVE_OPTION) return null;
        File selectedFile = chooser.getSelectedFile();
        String lowerName = selectedFile.getName().toLowerCase(Locale.ROOT);
        if (!lowerName.endsWith(".pdf")) selectedFile = new File(selectedFile.getParentFile(), selectedFile.getName() + ".pdf");
        if (selectedFile.exists()) {
            int overwrite = JOptionPane.showConfirmDialog(parent, "The file already exists. Do you want to replace it?", "Confirm Save", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (overwrite != JOptionPane.YES_OPTION) return null;
        }
        return selectedFile;
    }

    private String suggestedPdfFileName(ItemDetailsView v) {
        String safeName = (v.name == null ? "" : v.name).replaceAll("[^A-Za-z0-9_-]+", "_").trim();
        String base;
        if (v.code != null && !v.code.trim().isEmpty()) {
            base = v.code.trim();
            if (!safeName.isEmpty()) base = base + "_" + safeName;
        } else if (!safeName.isEmpty()) base = safeName; else base = "Item_Details";
        return base + ".pdf";
    }

    private PrintService findPdfPrintService() {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
        PrintService fallback = null;
        for (PrintService service : services) {
            String name = service.getName();
            if (name == null) continue;
            String normalized = name.toLowerCase(Locale.ROOT);
            if (normalized.equals("microsoft print to pdf")) return service;
            if (normalized.contains("pdf")) fallback = service;
        }
        return fallback;
    }

    private String stripPdfExtension(String fileName) {
        if (fileName == null) return "Item_Details";
        String lower = fileName.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".pdf")) return fileName.substring(0, fileName.length() - 4);
        return fileName;
    }
}

package com.billing.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.billing.model.document.BusinessDocument;
import com.billing.model.document.DocumentLine;
import com.billing.model.item.Item;

/**
 * Core document rules shared across sales documents.
 */
public class DocumentService {

    /**
     * Normalize lines, attach them to the document, and recalculate totals.
     */
    public void prepareForSave(BusinessDocument doc) {
        if (doc == null) {
            throw new IllegalArgumentException("Document cannot be null");
        }
        if (doc.getLines() == null) {
            doc.setLines(new ArrayList<>());
        }
        for (DocumentLine line : doc.getLines()) {
            if (line == null) continue;
            line.setDocument(doc);
            Item item = line.getItem();
            if (line.getUnitPrice() == null && item != null) {
                line.setUnitPrice(item.getSalePrice());
            }
            if (line.getTaxRate() == null && item != null && item.getIVAPercent() != null) {
                line.setTaxRate(BigDecimal.valueOf(item.getIVAPercent()).divide(BigDecimal.valueOf(100)));
            }
        }
        doc.recalculateTotals();
    }

    public List<DocumentLine> cloneLines(List<DocumentLine> source) {
        List<DocumentLine> copy = new ArrayList<>();
        if (source == null) return copy;
        for (DocumentLine line : source) {
            if (line == null) continue;
            DocumentLine cloned = new DocumentLine();
            cloned.setItem(line.getItem());
            cloned.setQuantity(line.getQuantity());
            cloned.setUnitPrice(line.getUnitPrice());
            cloned.setTaxRate(line.getTaxRate());
            copy.add(cloned);
        }
        return copy;
    }
}

package com.billing.service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.billing.model.document.BusinessDocument;
import com.billing.model.document.DeliveryNote;
import com.billing.model.document.Invoice;
import com.billing.model.party.Party;
import com.billing.repository.RepositoryFactory;
import com.billing.repository.interfaces.DocumentRepository;
import com.billing.tx.HibernateTransactionManager;
import com.billing.tx.TransactionManager;

/**
 * Read-only access to business documents.
 */
public class DocumentQueryService {
    private final DocumentRepository documentRepository;
    private final TransactionManager tx;

    public DocumentQueryService() {
        this(RepositoryFactory.createDocumentRepository(), new HibernateTransactionManager());
    }

    public DocumentQueryService(DocumentRepository documentRepository, TransactionManager tx) {
        this.documentRepository = documentRepository;
        this.tx = tx;
    }

    public List<BusinessDocument> listAll() {
        return tx.runInTransaction(() -> new ArrayList<>(documentRepository.findAll()));
    }

    public BusinessDocument findById(Long id) {
        return tx.runInTransaction(() -> documentRepository.find(id));
    }

    public List<DocumentTrackingRow> listTrackingRows() {
        return tx.runInTransaction(() -> mapRows(documentRepository.findAll()));
    }

    public List<DocumentTrackingRow> listInvoiceTrackingRows() {
        return tx.runInTransaction(() -> {
            List<DocumentTrackingRow> rows = mapRows(documentRepository.findAll());
            List<DocumentTrackingRow> out = new ArrayList<>();
            for (DocumentTrackingRow row : rows) {
                if ("Invoice".equalsIgnoreCase(row.type)) {
                    out.add(row);
                }
            }
            return out;
        });
    }

    public List<DocumentTrackingRow> searchTrackingRows(String term) {
        return tx.runInTransaction(() -> {
            List<DocumentTrackingRow> rows = mapRows(documentRepository.findAll());
            if (term == null || term.isBlank()) return rows;
            String t = term.toLowerCase();
            List<DocumentTrackingRow> out = new ArrayList<>();
            for (DocumentTrackingRow r : rows) {
                if ((r.type != null && r.type.toLowerCase().contains(t)) ||
                    (r.number != null && r.number.toLowerCase().contains(t)) ||
                    (r.party != null && r.party.toLowerCase().contains(t))) {
                    out.add(r);
                }
            }
            return out;
        });
    }

    public List<DocumentTrackingRow> searchInvoiceTrackingRows(String term) {
        return tx.runInTransaction(() -> {
            List<DocumentTrackingRow> rows = new ArrayList<>();
            for (DocumentTrackingRow row : mapRows(documentRepository.findAll())) {
                if ("Invoice".equalsIgnoreCase(row.type)) {
                    rows.add(row);
                }
            }
            if (term == null || term.isBlank()) return rows;
            String t = term.toLowerCase();
            List<DocumentTrackingRow> out = new ArrayList<>();
            for (DocumentTrackingRow r : rows) {
                if ((r.number != null && r.number.toLowerCase().contains(t)) ||
                    (r.party != null && r.party.toLowerCase().contains(t))) {
                    out.add(r);
                }
            }
            return out;
        });
    }

    public List<DocumentTrackingRow> listTrackingRowsSorted(String sortKey) {
        return tx.runInTransaction(() -> {
            List<DocumentTrackingRow> rows = mapRows(documentRepository.findAll());
            if (sortKey == null) return rows;
            switch (sortKey) {
                case "Sort by Code" -> rows.sort(Comparator.comparing(r -> r.number == null ? "" : r.number, String.CASE_INSENSITIVE_ORDER));
                case "Sort by Date" -> rows.sort(Comparator.comparing(r -> r.date == null ? "" : r.date));
                case "Sort by Party" -> rows.sort(Comparator.comparing(r -> r.party == null ? "" : r.party, String.CASE_INSENSITIVE_ORDER));
                case "Sort by Type" -> rows.sort(Comparator.comparing(r -> r.type == null ? "" : r.type, String.CASE_INSENSITIVE_ORDER));
                default -> rows.sort(Comparator.comparing(r -> r.id == null ? Long.MAX_VALUE : r.id));
            }
            return rows;
        });
    }

    public List<DocumentTrackingRow> listInvoiceTrackingRowsSorted(String sortKey) {
        return tx.runInTransaction(() -> {
            List<DocumentTrackingRow> rows = new ArrayList<>();
            for (DocumentTrackingRow row : mapRows(documentRepository.findAll())) {
                if ("Invoice".equalsIgnoreCase(row.type)) {
                    rows.add(row);
                }
            }
            if (sortKey == null) return rows;
            switch (sortKey) {
                case "Sort by Code" -> rows.sort(Comparator.comparing(r -> r.number == null ? "" : r.number, String.CASE_INSENSITIVE_ORDER));
                case "Sort by Date" -> rows.sort(Comparator.comparing(r -> r.date == null ? "" : r.date));
                case "Sort by Party" -> rows.sort(Comparator.comparing(r -> r.party == null ? "" : r.party, String.CASE_INSENSITIVE_ORDER));
                default -> rows.sort(Comparator.comparing(r -> r.id == null ? Long.MAX_VALUE : r.id));
            }
            return rows;
        });
    }

    private List<DocumentTrackingRow> mapRows(List<BusinessDocument> documents) {
        List<DocumentTrackingRow> rows = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (BusinessDocument doc : documents) {
            String type = doc instanceof Invoice ? "Invoice" : (doc instanceof DeliveryNote ? "Delivery Note" : "Document");
            Party party = doc.getParty();
            String partyName = party != null ? party.getName() : "";
            String status = doc instanceof Invoice ? "Invoiced" : "Pending Invoice";
            DocumentTrackingRow row = new DocumentTrackingRow();
            row.id = doc.getId();
            row.type = type;
            row.number = doc.getCode();
            row.date = doc.getDate() != null ? doc.getDate().format(fmt) : "";
            row.party = partyName;
            row.status = status;
            row.subtotal = doc.getSubtotal() != null ? doc.getSubtotal() : BigDecimal.ZERO;
            row.taxes = doc.getTaxes() != null ? doc.getTaxes() : BigDecimal.ZERO;
            row.total = doc.getTotal() != null ? doc.getTotal() : BigDecimal.ZERO;
            rows.add(row);
        }
        return rows;
    }

    public static class DocumentTrackingRow {
        public Long id;
        public String type;
        public String number;
        public String date;
        public String party;
        public String status;
        public BigDecimal subtotal;
        public BigDecimal taxes;
        public BigDecimal total;
    }
}

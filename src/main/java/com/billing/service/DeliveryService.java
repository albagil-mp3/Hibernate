package com.billing.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.billing.model.document.DeliveryNote;
import com.billing.model.document.DocumentLine;
import com.billing.model.document.Invoice;
import com.billing.model.party.Client;
import com.billing.repository.RepositoryFactory;
import com.billing.repository.interfaces.DocumentRepository;
import com.billing.tx.HibernateTransactionManager;
import com.billing.tx.TransactionManager;

/**
 * Delivery workflow service (create from order, mark delivered, generate invoice).
 */
public class DeliveryService {
    private final SalesService salesService;
    private final DocumentRepository documentRepository;
    private final TransactionManager tx;

    public DeliveryService() {
        this(new SalesService(), RepositoryFactory.createDocumentRepository(), new HibernateTransactionManager());
    }

    public DeliveryService(SalesService salesService) {
        this(salesService, RepositoryFactory.createDocumentRepository(), new HibernateTransactionManager());
    }

    public DeliveryService(SalesService salesService, DocumentRepository documentRepository, TransactionManager tx) {
        this.salesService = salesService;
        this.documentRepository = documentRepository;
        this.tx = tx;
    }

    public List<DeliveryNote> listAll() {
        return tx.runInTransaction(() -> {
            List<DeliveryNote> out = new ArrayList<>();
            for (var doc : documentRepository.findAll()) {
                if (doc instanceof DeliveryNote) out.add((DeliveryNote) doc);
            }
            return out;
        });
    }

    public List<DeliveryRow> listTrackingRows() {
        return tx.runInTransaction(() -> {
            List<DeliveryRow> rows = new ArrayList<>();
            for (DeliveryNote dn : listAll()) {
                DeliveryRow r = new DeliveryRow();
                r.id = dn.getId();
                r.number = dn.getCode();
                r.date = dn.getDate() != null ? dn.getDate().toString() : "";
                r.customer = dn.getClient() != null ? dn.getClient().getName() : "";
                r.items = dn.getLines() != null ? dn.getLines().size() : 0;
                r.delivered = Boolean.TRUE.equals(dn.getDelivered());
                r.pendingInvoice = !Boolean.TRUE.equals(dn.getInvoiced());
                r.total = dn.getTotal() != null ? dn.getTotal() : BigDecimal.ZERO;
                rows.add(r);
            }
            return rows;
        });
    }

    public DeliveryNote findById(Long id) {
        return tx.runInTransaction(() -> {
            Object doc = documentRepository.find(id);
            return doc instanceof DeliveryNote ? (DeliveryNote) doc : null;
        });
    }

    public DeliveryNote createFromOrder(Long orderId) {
        return salesService.generateDeliveryNote(orderId);
    }

    public SalesService.Quotation createQuotation(Client customer, List<DocumentLine> lines) {
        return salesService.createQuotation(customer, lines);
    }

    public SalesService.SalesOrder confirmOrder(Long quotationId) {
        return salesService.confirmOrder(quotationId);
    }

    public SalesService.Quotation updateQuotationStatus(Long quotationId, SalesService.QuotationStatus status) {
        return salesService.updateQuotationStatus(quotationId, status);
    }

    public SalesService.SalesOrder acceptQuotation(Long quotationId) {
        return salesService.acceptQuotation(quotationId);
    }

    public List<SalesService.Quotation> listQuotations() {
        return salesService.listQuotations();
    }

    public List<SalesService.SalesOrder> listOrders() {
        return salesService.listOrders();
    }

    public SalesService getSalesService() {
        return salesService;
    }

    public DeliveryNote markDelivered(Long deliveryNoteId) {
        return tx.runInTransaction(() -> {
            DeliveryNote dn = findById(deliveryNoteId);
            if (dn == null) throw new IllegalArgumentException("Delivery note not found");
            dn.setDelivered(true);
            return (DeliveryNote) documentRepository.save(dn);
        });
    }

    public DeliveryNote sendOrder(Long orderId) {
        return salesService.generateDeliveryNote(orderId);
    }

    public Invoice generateInvoice(Long deliveryNoteId) {
        Invoice inv = salesService.generateInvoice(deliveryNoteId);
        tx.runInTransaction(() -> {
            DeliveryNote dn = findById(deliveryNoteId);
            if (dn != null) {
                dn.setInvoiced(true);
                documentRepository.save(dn);
            }
        });
        return inv;
    }

    public static class DeliveryRow {
        public Long id;
        public String number;
        public String date;
        public String customer;
        public Integer items;
        public boolean delivered;
        public boolean pendingInvoice;
        public BigDecimal total;
    }

    public List<DeliveryRow> search(String text) {
        String q = text == null ? "" : text.trim().toLowerCase();
        if (q.isEmpty()) return listTrackingRows();
        List<DeliveryRow> rows = listTrackingRows();
        List<DeliveryRow> out = new ArrayList<>();
        for (DeliveryRow r : rows) {
            String id = r.id != null ? r.id.toString() : "";
            String number = r.number != null ? r.number : "";
            String date = r.date != null ? r.date : "";
            String customer = r.customer != null ? r.customer : "";
            String status = r.delivered ? "delivered" : "pending";
            String pending = r.pendingInvoice ? "pending invoice" : "invoiced";
            String total = r.total != null ? r.total.toString() : "";
            String haystack = String.join(" ",
                id, number, date, customer, status, pending, total).toLowerCase();
            if (haystack.contains(q)) out.add(r);
        }
        return out;
    }

    public List<DeliveryRow> sort(int selectedIndex) {
        List<DeliveryRow> rows = new ArrayList<>(listTrackingRows());
        switch (selectedIndex) {
            case 0 -> rows.sort((a, b) -> {
                String ac = a.number != null ? a.number : "";
                String bc = b.number != null ? b.number : "";
                int cmp = ac.compareToIgnoreCase(bc);
                if (cmp != 0) return cmp;
                long ai = a.id != null ? a.id : 0L;
                long bi = b.id != null ? b.id : 0L;
                return Long.compare(ai, bi);
            });
            case 1 -> rows.sort((a, b) -> {
                String ad = a.date != null ? a.date : "";
                String bd = b.date != null ? b.date : "";
                int cmp = ad.compareTo(bd);
                if (cmp != 0) return cmp;
                long ai = a.id != null ? a.id : 0L;
                long bi = b.id != null ? b.id : 0L;
                return Long.compare(ai, bi);
            });
            case 2 -> rows.sort((a, b) -> {
                String ac = a.customer != null ? a.customer : "";
                String bc = b.customer != null ? b.customer : "";
                int cmp = ac.compareToIgnoreCase(bc);
                if (cmp != 0) return cmp;
                long ai = a.id != null ? a.id : 0L;
                long bi = b.id != null ? b.id : 0L;
                return Long.compare(ai, bi);
            });
            case 3 -> rows.sort((a, b) -> {
                String as = a.delivered ? "delivered" : "pending";
                String bs = b.delivered ? "delivered" : "pending";
                int cmp = as.compareTo(bs);
                if (cmp != 0) return cmp;
                boolean ap = a.pendingInvoice;
                boolean bp = b.pendingInvoice;
                if (ap != bp) return ap ? 1 : -1;
                long ai = a.id != null ? a.id : 0L;
                long bi = b.id != null ? b.id : 0L;
                return Long.compare(ai, bi);
            });
            default -> {
            }
        }
        return rows;
    }
}

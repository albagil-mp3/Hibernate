package com.billing.service;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.UUID;

import com.billing.model.document.DeliveryNote;
import com.billing.model.document.DocumentLine;
import com.billing.model.document.Invoice;
import com.billing.model.party.Client;
import com.billing.repository.RepositoryFactory;
import com.billing.repository.interfaces.DocumentRepository;
import com.billing.tx.HibernateTransactionManager;
import com.billing.tx.TransactionManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Core sales workflow service.
 * - Quotations and Orders are handled as in-memory workflow documents
 * - Delivery notes and invoices are persisted
 */
public class SalesService {

    public enum QuotationStatus {
        DRAFT,
        SENT,
        ACCEPTED,
        REJECTED
    }

    public enum OrderStatus {
        CREATED,
        SENT
    }

    private static final Logger logger = Logger.getLogger(SalesService.class.getName());
    private static final String WORKFLOW_FILE = "data/sales-workflow.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    private final DocumentRepository documentRepository;
    private final DocumentService documentService;
    private final TransactionManager tx;

    private final AtomicLong quotationSeq = new AtomicLong(1);
    private final AtomicLong orderSeq = new AtomicLong(1);
    private final Map<Long, Quotation> quotations = new ConcurrentHashMap<>();
    private final Map<Long, SalesOrder> orders = new ConcurrentHashMap<>();

    public SalesService() {
        this(RepositoryFactory.createDocumentRepository(), new DocumentService(), new HibernateTransactionManager());
    }

    public SalesService(DocumentRepository documentRepository, DocumentService documentService, TransactionManager tx) {
        this.documentRepository = documentRepository;
        this.documentService = documentService;
        this.tx = tx;
        loadFromFile();
    }

    public Quotation createQuotation(Client customer, List<DocumentLine> lines) {
        if (customer == null) throw new IllegalArgumentException("Customer cannot be null");
        Quotation q = new Quotation();
        q.id = quotationSeq.getAndIncrement();
        q.code = String.format("QT%04d", q.id);
        q.date = LocalDate.now();
        q.status = QuotationStatus.DRAFT;
        q.customer = customer;
        q.lines = documentService.cloneLines(lines);
        q.recalculateTotals();
        quotations.put(q.id, q);
        logger.info("Quotation created: " + q.code);
        saveToFile();
        return q;
    }

    public List<Quotation> listQuotations() {
        List<Quotation> out = new ArrayList<>(quotations.values());
        out.sort((a, b) -> Long.compare(a.id == null ? Long.MAX_VALUE : a.id, b.id == null ? Long.MAX_VALUE : b.id));
        return out;
    }

    public Quotation findQuotation(Long id) {
        if (id == null) return null;
        return quotations.get(id);
    }

    public SalesOrder confirmOrder(Long quotationId) {
        Quotation q = quotations.get(quotationId);
        if (q == null) throw new IllegalArgumentException("Quotation not found: " + quotationId);
        SalesOrder order = new SalesOrder();
        order.id = orderSeq.getAndIncrement();
        order.code = String.format("SO%04d", order.id);
        order.date = LocalDate.now();
        order.status = OrderStatus.CREATED;
        order.customer = q.customer;
        order.lines = documentService.cloneLines(q.lines);
        order.recalculateTotals();
        orders.put(order.id, order);
        quotations.remove(quotationId);
        logger.info("Order confirmed: " + order.code);
        saveToFile();
        return order;
    }

    public List<SalesOrder> listOrders() {
        List<SalesOrder> out = new ArrayList<>(orders.values());
        out.sort((a, b) -> Long.compare(a.id == null ? Long.MAX_VALUE : a.id, b.id == null ? Long.MAX_VALUE : b.id));
        return out;
    }

    public SalesOrder findOrder(Long id) {
        if (id == null) return null;
        return orders.get(id);
    }

    public Quotation updateQuotationStatus(Long quotationId, QuotationStatus status) {
        if (quotationId == null) throw new IllegalArgumentException("Quotation ID cannot be null");
        if (status == null) throw new IllegalArgumentException("Status cannot be null");
        if (status == QuotationStatus.ACCEPTED) {
            throw new IllegalArgumentException("Use acceptQuotation() to convert to order");
        }
        Quotation q = quotations.get(quotationId);
        if (q == null) throw new IllegalArgumentException("Quotation not found: " + quotationId);
        validateQuotationTransition(q, status);
        q.status = status;
        logger.info("Quotation status updated: " + q.code + " -> " + status);
        saveToFile();
        return q;
    }

    public SalesOrder acceptQuotation(Long quotationId) {
        if (quotationId == null) throw new IllegalArgumentException("Quotation ID cannot be null");
        Quotation q = quotations.get(quotationId);
        if (q == null) throw new IllegalArgumentException("Quotation not found: " + quotationId);
        QuotationStatus current = q.status != null ? q.status : QuotationStatus.DRAFT;
        if (current != QuotationStatus.SENT) {
            throw new IllegalArgumentException("Quotation must be SENT before it can be accepted");
        }
        q.status = QuotationStatus.ACCEPTED;
        return confirmOrder(quotationId);
    }

    public DeliveryNote generateDeliveryNote(Long orderId) {
        return tx.runInTransaction(() -> {
            SalesOrder order = orders.get(orderId);
            if (order == null) throw new IllegalArgumentException("Order not found: " + orderId);
            order.status = OrderStatus.SENT;
            DeliveryNote dn = new DeliveryNote();
            dn.setDate(order.date);
            dn.setClient(order.customer);
            dn.setLines(documentService.cloneLines(order.lines));
            documentService.prepareForSave(dn);
            // Ensure DB NOT NULL constraint for `code` is satisfied on first insert
            if (dn.getCode() == null || dn.getCode().isBlank()) {
                dn.setCode("DN-TMP-" + UUID.randomUUID().toString());
            }
            DeliveryNote saved = (DeliveryNote) documentRepository.save(dn);
            saved = ensureCode(saved, "DN");
            logger.info("DeliveryNote generated from Order " + order.code + ": " + saved.getCode());
            orders.remove(orderId);
            saveToFile();
            return saved;
        });
    }

    public Invoice generateInvoice(Long deliveryNoteId) {
        return tx.runInTransaction(() -> {
            DeliveryNote dn = findDeliveryNote(deliveryNoteId);
            if (dn == null) throw new IllegalArgumentException("DeliveryNote not found: " + deliveryNoteId);
            Invoice inv = new Invoice();
            inv.setDate(dn.getDate());
            inv.setClient(dn.getClient());
            inv.setLines(documentService.cloneLines(dn.getLines()));
            documentService.prepareForSave(inv);
            if (inv.getCode() == null || inv.getCode().isBlank()) {
                inv.setCode("INV-TMP-" + UUID.randomUUID().toString());
            }
            Invoice saved = (Invoice) documentRepository.save(inv);
            saved = ensureCode(saved, "INV");
            logger.info("Invoice generated from DeliveryNote " + dn.getId() + ": " + saved.getCode());
            return saved;
        });
    }

    /**
     * Exceptional case: create an invoice directly without previous documents.
     */
    public Invoice createDirectInvoice(Client customer, List<DocumentLine> lines) {
        return tx.runInTransaction(() -> {
            if (customer == null) throw new IllegalArgumentException("Customer cannot be null");
            Invoice inv = new Invoice();
            inv.setDate(LocalDate.now());
            inv.setClient(customer);
            inv.setLines(documentService.cloneLines(lines));
            documentService.prepareForSave(inv);
            if (inv.getCode() == null || inv.getCode().isBlank()) {
                inv.setCode("INV-TMP-" + UUID.randomUUID().toString());
            }
            Invoice saved = (Invoice) documentRepository.save(inv);
            saved = ensureCode(saved, "INV");
            logger.info("Direct invoice created: " + saved.getCode());
            return saved;
        });
    }

    /**
     * Generate the next workflow step for a document (DeliveryNote -> Invoice).
     */
    public Invoice generateNextStep(Long documentId) {
        return generateInvoice(documentId);
    }

    private DeliveryNote ensureCode(DeliveryNote dn, String prefix) {
        if (dn == null || dn.getId() == null) return dn;
        String current = dn.getCode();
        String expected = String.format("%s%04d", prefix, dn.getId());
        if (current == null || current.isBlank() || !current.matches("^" + prefix + "\\\\d{4}$")) {
            dn.setCode(expected);
            return (DeliveryNote) documentRepository.save(dn);
        }
        return dn;
    }

    private Invoice ensureCode(Invoice inv, String prefix) {
        if (inv == null || inv.getId() == null) return inv;
        String current = inv.getCode();
        String expected = String.format("%s%04d", prefix, inv.getId());
        if (current == null || current.isBlank() || !current.matches("^" + prefix + "\\\\d{4}$")) {
            inv.setCode(expected);
            return (Invoice) documentRepository.save(inv);
        }
        return inv;
    }

    private DeliveryNote findDeliveryNote(Long id) {
        if (id == null) return null;
        Object doc = documentRepository.find(id);
        if (doc instanceof DeliveryNote) return (DeliveryNote) doc;
        return null;
    }

    private void saveToFile() {
        try {
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            ObjectNode root = mapper.createObjectNode();
            root.put("quotationSeq", quotationSeq.get());
            root.put("orderSeq", orderSeq.get());
            root.set("quotations", mapper.valueToTree(quotations.values()));
            root.set("orders", mapper.valueToTree(orders.values()));
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(WORKFLOW_FILE), root);
            logger.info("Workflow saved to " + WORKFLOW_FILE);
        } catch (Exception ex) {
            logger.warning("Failed to save workflow: " + ex.getMessage());
        }
    }

    private void validateQuotationTransition(Quotation q, QuotationStatus target) {
        QuotationStatus current = q.status != null ? q.status : QuotationStatus.DRAFT;
        if (current == QuotationStatus.ACCEPTED || current == QuotationStatus.REJECTED) {
            throw new IllegalArgumentException("Quotation is already " + current + " and cannot be changed");
        }
        boolean allowed = false;
        if (current == QuotationStatus.DRAFT) {
            allowed = target == QuotationStatus.DRAFT || target == QuotationStatus.SENT;
        } else if (current == QuotationStatus.SENT) {
            allowed = target == QuotationStatus.SENT || target == QuotationStatus.REJECTED;
        }
        if (!allowed) {
            throw new IllegalArgumentException("Invalid transition: " + current + " -> " + target);
        }
    }

    private void loadFromFile() {
        try {
            File file = new File(WORKFLOW_FILE);
            if (!file.exists()) {
                logger.info("Workflow file not found, starting fresh");
                return;
            }
            ObjectNode root = mapper.readValue(file, ObjectNode.class);
            quotationSeq.set(root.get("quotationSeq").asLong(1));
            orderSeq.set(root.get("orderSeq").asLong(1));
            
            if (root.has("quotations") && root.get("quotations").isArray()) {
                for (var node : root.get("quotations")) {
                    Quotation q = mapper.treeToValue(node, Quotation.class);
                    if (q.id != null) {
                        if (q.status == null) {
                            q.status = QuotationStatus.DRAFT;
                        }
                        quotations.put(q.id, q);
                    }
                }
            }
            
            if (root.has("orders") && root.get("orders").isArray()) {
                for (var node : root.get("orders")) {
                    SalesOrder o = mapper.treeToValue(node, SalesOrder.class);
                    if (o.id != null) {
                        if (o.status == null) {
                            o.status = OrderStatus.CREATED;
                        }
                        orders.put(o.id, o);
                    }
                }
            }
            logger.info("Workflow loaded: " + quotations.size() + " quotations, " + orders.size() + " orders");
        } catch (Exception ex) {
            logger.warning("Failed to load workflow: " + ex.getMessage());
        }
    }

    public static class Quotation {
        public Long id;
        public String code;
        public LocalDate date;
        public QuotationStatus status;
        public Client customer;
        public List<DocumentLine> lines;
        public java.math.BigDecimal subtotal = java.math.BigDecimal.ZERO;
        public java.math.BigDecimal taxes = java.math.BigDecimal.ZERO;
        public java.math.BigDecimal total = java.math.BigDecimal.ZERO;

        public void recalculateTotals() {
            subtotal = java.math.BigDecimal.ZERO;
            taxes = java.math.BigDecimal.ZERO;
            if (lines == null) return;
            for (DocumentLine l : lines) {
                java.math.BigDecimal qty = java.math.BigDecimal.valueOf(l.getQuantity() == null ? 0 : l.getQuantity());
                java.math.BigDecimal base = (l.getUnitPrice() == null ? java.math.BigDecimal.ZERO : l.getUnitPrice()).multiply(qty);
                java.math.BigDecimal tax = base.multiply(l.getTaxRate() == null ? java.math.BigDecimal.ZERO : l.getTaxRate());
                subtotal = subtotal.add(base);
                taxes = taxes.add(tax);
            }
            total = subtotal.add(taxes);
        }
    }

    public static class SalesOrder {
        public Long id;
        public String code;
        public LocalDate date;
        public OrderStatus status;
        public Client customer;
        public List<DocumentLine> lines;
        public java.math.BigDecimal subtotal = java.math.BigDecimal.ZERO;
        public java.math.BigDecimal taxes = java.math.BigDecimal.ZERO;
        public java.math.BigDecimal total = java.math.BigDecimal.ZERO;

        public void recalculateTotals() {
            subtotal = java.math.BigDecimal.ZERO;
            taxes = java.math.BigDecimal.ZERO;
            if (lines == null) return;
            for (DocumentLine l : lines) {
                java.math.BigDecimal qty = java.math.BigDecimal.valueOf(l.getQuantity() == null ? 0 : l.getQuantity());
                java.math.BigDecimal base = (l.getUnitPrice() == null ? java.math.BigDecimal.ZERO : l.getUnitPrice()).multiply(qty);
                java.math.BigDecimal tax = base.multiply(l.getTaxRate() == null ? java.math.BigDecimal.ZERO : l.getTaxRate());
                subtotal = subtotal.add(base);
                taxes = taxes.add(tax);
            }
            total = subtotal.add(taxes);
        }
    }
}

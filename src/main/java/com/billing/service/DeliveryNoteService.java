package com.billing.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

import com.billing.model.document.DeliveryNote;
import com.billing.model.document.DocumentLine;
import com.billing.model.document.Invoice;
import com.billing.repository.RepositoryFactory;
import com.billing.repository.interfaces.DocumentRepository;
import com.billing.tx.HibernateTransactionManager;
import com.billing.tx.TransactionManager;

/**
 * Service layer for DeliveryNote operations
 */
public class DeliveryNoteService {

    private static final Logger logger = Logger.getLogger(DeliveryNoteService.class.getName());
    private final DocumentRepository documentRepository;
    private final TransactionManager tx;

    public DeliveryNoteService() {
        this.documentRepository = RepositoryFactory.createDocumentRepository();
        this.tx = new HibernateTransactionManager();
    }

    /**
     * Constructor for explicit dependency injection
     */
    public DeliveryNoteService(DocumentRepository documentRepository, TransactionManager tx) {
        this.documentRepository = documentRepository;
        this.tx = tx;
    }

    public DeliveryNote createDeliveryNote(DeliveryNote dn) {
        return tx.runInTransaction(() -> {
            // Simple business rules can be added here
            DeliveryNote saved = (DeliveryNote) documentRepository.save(dn);
            saved = ensureCode(saved, "DN");
            final DeliveryNote finalSaved = saved;
            logger.info(() -> "DeliveryNote created: " + (finalSaved != null ? finalSaved.getId() : "null"));
            return saved;
        });
    }

    public DeliveryNote findById(Long id) {
        return tx.runInTransaction(() -> {
            Object doc = documentRepository.find(id);
            return doc instanceof DeliveryNote ? (DeliveryNote) doc : null;
        });
    }

    public List<DeliveryNote> findAll() {
        return tx.runInTransaction(() -> {
            List<DeliveryNote> out = new java.util.ArrayList<>();
            for (var doc : documentRepository.findAll()) {
                if (doc instanceof DeliveryNote) out.add((DeliveryNote) doc);
            }
            return out;
        });
    }

    /**
     * Convert a delivery note into an invoice (simple copy of lines and totals).
     * This persists a new Invoice and returns it.
     */
    public Invoice convertToInvoice(DeliveryNote dn) {
        return tx.runInTransaction(() -> {
            if (dn == null) throw new IllegalArgumentException("DeliveryNote cannot be null");
            Invoice inv = new Invoice();
            inv.setClient(dn.getClient());
            inv.setDate(dn.getDate());

            BigDecimal total = BigDecimal.ZERO;
            for (DocumentLine line : dn.getLines()) {
                // move line to the new invoice
                line.setDocument(inv);
                inv.getLines().add(line);
                BigDecimal lineTotal = line.getUnitPrice() == null ? BigDecimal.ZERO : line.getUnitPrice().multiply(BigDecimal.valueOf(line.getQuantity()));
                total = total.add(lineTotal);
            }
            inv.setTotal(total);

            Invoice saved = (Invoice) documentRepository.save(inv);
            saved = ensureInvoiceCode(saved, "INV");
            final Invoice finalSaved = saved;
            logger.info(() -> "Converted DeliveryNote " + dn.getId() + " to Invoice " + (finalSaved != null ? finalSaved.getId() : "null"));
            return saved;
        });
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

    private Invoice ensureInvoiceCode(Invoice inv, String prefix) {
        if (inv == null || inv.getId() == null) return inv;
        String current = inv.getCode();
        String expected = String.format("%s%04d", prefix, inv.getId());
        if (current == null || current.isBlank() || !current.matches("^" + prefix + "\\\\d{4}$")) {
            inv.setCode(expected);
            return (Invoice) documentRepository.save(inv);
        }
        return inv;
    }
}

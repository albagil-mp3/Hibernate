package com.billing.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.billing.model.document.Invoice;
import com.billing.model.party.Client;
import com.billing.repository.RepositoryFactory;
import com.billing.repository.interfaces.DocumentRepository;
import com.billing.tx.HibernateTransactionManager;
import com.billing.tx.TransactionManager;

/**
 * Read-only invoice queries.
 */
public class InvoiceQueryService {

    private static final Logger logger = Logger.getLogger(InvoiceQueryService.class.getName());
    private final DocumentRepository documentRepository;
    private final TransactionManager tx;

    public InvoiceQueryService() {
        this(RepositoryFactory.createDocumentRepository(), new HibernateTransactionManager());
    }

    public InvoiceQueryService(DocumentRepository documentRepository, TransactionManager tx) {
        this.documentRepository = documentRepository;
        this.tx = tx;
    }

    public List<Invoice> listAll() {
        return tx.runInTransaction(() -> {
            List<Invoice> out = new ArrayList<>();
            for (var doc : documentRepository.findAll()) {
                if (doc instanceof Invoice) out.add((Invoice) doc);
            }
            return out;
        });
    }

    public List<Invoice> findByCustomer(Client customer) {
        if (customer == null) return new ArrayList<>();
        List<Invoice> out = new ArrayList<>();
        for (Invoice inv : listAll()) {
            if (inv.getClient() != null && inv.getClient().getId() != null
                && inv.getClient().getId().equals(customer.getId())) {
                out.add(inv);
            }
        }
        return out;
    }

    public List<Invoice> findByDateRange(LocalDate from, LocalDate to) {
        List<Invoice> all = listAll();
        List<Invoice> out = new ArrayList<>();
        for (Invoice inv : all) {
            LocalDate d = inv.getDate();
            if (d == null) continue;
            boolean afterFrom = from == null || !d.isBefore(from);
            boolean beforeTo = to == null || !d.isAfter(to);
            if (afterFrom && beforeTo) out.add(inv);
        }
        return out;
    }

    /**
     * Pending payments are not explicitly tracked in the current model.
     * This returns invoices with a positive total and logs a warning.
     */
    public List<Invoice> findPendingPayments() {
        logger.warning("Pending payment status is not tracked; returning invoices with total > 0");
        List<Invoice> all = listAll();
        List<Invoice> out = new ArrayList<>();
        for (Invoice inv : all) {
            if (inv.getTotal() != null && inv.getTotal().signum() > 0) {
                out.add(inv);
            }
        }
        return out;
    }
}

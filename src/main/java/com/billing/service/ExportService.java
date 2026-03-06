package com.billing.service;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.billing.config.AppConfig;
import com.billing.config.ConfigLoader;
import com.billing.io.InvoiceExporter;
import com.billing.dto.InvoiceExportDTO;
import com.billing.dto.InvoiceLineDTO;
import com.billing.model.document.DocumentLine;
import com.billing.model.document.Invoice;
import com.billing.model.item.Item;
import com.billing.model.party.Client;
import com.billing.repository.RepositoryFactory;
import com.billing.repository.interfaces.DocumentRepository;
import com.billing.tx.HibernateTransactionManager;
import com.billing.tx.TransactionManager;

/**
 * Export operations for invoices.
 */
public class ExportService {

    private final InvoiceExporter invoiceExporter;
    private final AppConfig config;
    private final TransactionManager tx;
    private final DocumentRepository documentRepository;

    public ExportService() {
        this(RepositoryFactory.createDocumentRepository(), ConfigLoader.load(), new HibernateTransactionManager());
    }

    public ExportService(DocumentRepository documentRepository, AppConfig config, TransactionManager tx) {
        this.invoiceExporter = new InvoiceExporter();
        this.config = config != null ? config : ConfigLoader.load();
        this.tx = tx;
        this.documentRepository = documentRepository;
    }

    public Path exportInvoiceJson() throws IOException {
        Path target = Paths.get(config.getExportDir()).resolve("invoices.json");
        exportInvoiceJson(target);
        return target;
    }

    public void exportInvoiceJson(Path target) throws IOException {
        try {
            tx.runInTransaction(() -> {
                try {
                    invoiceExporter.exportAllToJson(loadInvoiceDtos(), target, Paths.get(config.getBackupDir()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            throw e;
        }
    }

    public Path exportInvoiceXml() throws IOException {
        Path target = Paths.get(config.getExportDir()).resolve("invoices.xml");
        exportInvoiceXml(target);
        return target;
    }

    public void exportInvoiceXml(Path target) throws IOException {
        try {
            tx.runInTransaction(() -> {
                try {
                    invoiceExporter.exportAllToXml(loadInvoiceDtos(), target, Paths.get(config.getBackupDir()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            throw e;
        }
    }

    public void exportInvoiceJson(Long invoiceId, Path target) throws IOException {
        try {
            tx.runInTransaction(() -> {
                try {
                    invoiceExporter.exportAllToJson(loadInvoiceDtos(invoiceId), target, Paths.get(config.getBackupDir()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            throw e;
        }
    }

    public void exportInvoiceXml(Long invoiceId, Path target) throws IOException {
        try {
            tx.runInTransaction(() -> {
                try {
                    invoiceExporter.exportAllToXml(loadInvoiceDtos(invoiceId), target, Paths.get(config.getBackupDir()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (RuntimeException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            throw e;
        }
    }

    private List<InvoiceExportDTO> loadInvoiceDtos() {
        List<InvoiceExportDTO> out = new ArrayList<>();
        for (var doc : documentRepository.findAll()) {
            if (doc instanceof Invoice) {
                out.add(mapInvoice((Invoice) doc));
            }
        }
        return out;
    }

    private List<InvoiceExportDTO> loadInvoiceDtos(Long invoiceId) {
        List<InvoiceExportDTO> out = new ArrayList<>();
        Object doc = documentRepository.find(invoiceId);
        if (doc instanceof Invoice) {
            out.add(mapInvoice((Invoice) doc));
        } else {
            throw new IllegalArgumentException("Selected document is not an invoice");
        }
        return out;
    }

    private InvoiceExportDTO mapInvoice(Invoice inv) {
        Client c = inv.getClient();
        InvoiceExportDTO dto = new InvoiceExportDTO();
        dto.id = inv.getId();
        dto.code = inv.getCode();
        dto.date = inv.getDate() != null ? inv.getDate().toString() : null;
        dto.clientCode = c != null ? c.getCode() : null;
        dto.clientName = c != null ? c.getName() : null;
        dto.subtotal = inv.getSubtotal();
        dto.taxes = inv.getTaxes();
        dto.total = inv.getTotal();
        dto.lines = new ArrayList<>();
        if (inv.getLines() != null) {
            for (DocumentLine l : inv.getLines()) {
                Item it = l.getItem();
                InvoiceLineDTO lr = new InvoiceLineDTO();
                lr.itemCode = it != null ? it.getCode() : null;
                lr.itemDescription = it != null ? it.getDescription() : null;
                lr.quantity = l.getQuantity();
                lr.unitPrice = l.getUnitPrice();
                lr.taxRate = l.getTaxRate();
                lr.lineTotal = l.lineTotal();
                dto.lines.add(lr);
            }
        }
        return dto;
    }
}

package com.billing.io;

import java.io.IOException;
import java.util.List;

import java.nio.file.Path;

import com.billing.dto.InvoiceExportDTO;
import com.billing.util.BackupUtil;
import com.billing.util.FilePersistenceUtil;

/**
 * Exports invoices to JSON/XML using DTOs (avoid Hibernate proxies).
 */
public final class InvoiceExporter {
    public void exportAllToJson(List<InvoiceExportDTO> invoices, Path target, Path backupDir) throws IOException {
        BackupUtil.backupIfExists(target, backupDir);
        FilePersistenceUtil.ensureParentExists(target);
        FilePersistenceUtil.json().writerWithDefaultPrettyPrinter().writeValue(target.toFile(), invoices);
    }

    public void exportAllToXml(List<InvoiceExportDTO> invoices, Path target, Path backupDir) throws IOException {
        BackupUtil.backupIfExists(target, backupDir);
        InvoiceExport payload = new InvoiceExport(invoices);
        FilePersistenceUtil.ensureParentExists(target);
        FilePersistenceUtil.xml().writerWithDefaultPrettyPrinter().writeValue(target.toFile(), payload);
    }

    public static class InvoiceExport {
        public List<InvoiceExportDTO> invoices;
        public InvoiceExport() {}
        public InvoiceExport(List<InvoiceExportDTO> invoices) { this.invoices = invoices; }
    }
}

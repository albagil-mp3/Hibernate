package com.billing.io;

import java.nio.file.Path;
import java.util.List;

import com.billing.dto.SupplierImportDTO;
import com.billing.util.BackupUtil;
import com.billing.util.FilePersistenceUtil;

/**
 * Imports suppliers from JSON.
 */
public final class SupplierImporter {
    public List<SupplierImportDTO> importFromJson(Path source, Path backupDir) throws ImportException {
        try {
            BackupUtil.backupIfExists(source, backupDir);
            return FilePersistenceUtil.json().readValue(
                    source.toFile(),
                    FilePersistenceUtil.json().getTypeFactory().constructCollectionType(List.class, SupplierImportDTO.class));
        } catch (Exception e) {
            throw new ImportException("Invalid supplier import file", e);
        }
    }
}

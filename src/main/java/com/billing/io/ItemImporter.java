package com.billing.io;

import java.nio.file.Path;
import java.util.List;

import com.billing.dto.ItemImportDTO;
import com.billing.util.BackupUtil;
import com.billing.util.FilePersistenceUtil;

/**
 * Imports items from JSON using references by code.
 */
public final class ItemImporter {
    public List<ItemImportDTO> importFromJson(Path source, Path backupDir) throws ImportException {
        try {
            BackupUtil.backupIfExists(source, backupDir);
            return FilePersistenceUtil.json().readValue(
                    source.toFile(),
                    FilePersistenceUtil.json().getTypeFactory().constructCollectionType(List.class, ItemImportDTO.class));
        } catch (Exception e) {
            throw new ImportException("Invalid item import file", e);
        }
    }
}

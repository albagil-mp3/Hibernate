package com.billing.io;

import java.nio.file.Path;
import java.util.List;

import com.billing.dto.ClientImportDTO;
import com.billing.util.BackupUtil;
import com.billing.util.FilePersistenceUtil;

/**
 * Imports clients from JSON.
 */
public final class ClientImporter {
    public List<ClientImportDTO> importFromJson(Path source, Path backupDir) throws ImportException {
        try {
            BackupUtil.backupIfExists(source, backupDir);
            return FilePersistenceUtil.json().readValue(
                    source.toFile(),
                    FilePersistenceUtil.json().getTypeFactory().constructCollectionType(List.class, ClientImportDTO.class));
        } catch (Exception e) {
            throw new ImportException("Invalid client import file", e);
        }
    }
}

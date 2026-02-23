package com.billing.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple file backup utility.
 */
public final class BackupUtil {
    private static final DateTimeFormatter TS = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    private BackupUtil() {}

    public static void backupIfExists(Path source, Path backupDir) throws IOException {
        if (source == null || backupDir == null) return;
        if (!Files.exists(source)) return;
        FilePersistenceUtil.ensureParentExists(backupDir.resolve("placeholder"));
        String name = source.getFileName().toString();
        String stamp = LocalDateTime.now().format(TS);
        Path target = backupDir.resolve(name + "." + stamp + ".bak");
        Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    }
}

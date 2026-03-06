package com.billing.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Utilities for JSON/XML file persistence using Jackson
 */
public final class FilePersistenceUtil {

    private static final ObjectMapper jsonMapper = new ObjectMapper();
    private static final XmlMapper xmlMapper = new XmlMapper();

    static {
        jsonMapper.registerModule(new JavaTimeModule());
        xmlMapper.registerModule(new JavaTimeModule());
    }

    private FilePersistenceUtil() {}

    public static ObjectMapper json() {
        return jsonMapper;
    }

    public static XmlMapper xml() {
        return xmlMapper;
    }

    public static void ensureParentExists(Path p) throws IOException {
        Path parent = p.toAbsolutePath().getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }
    }

    public static File ensureFile(Path p) throws IOException {
        ensureParentExists(p);
        File f = p.toFile();
        if (!f.exists()) f.createNewFile();
        return f;
    }
}

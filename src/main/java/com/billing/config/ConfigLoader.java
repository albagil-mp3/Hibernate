package com.billing.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;

import com.billing.util.FilePersistenceUtil;

/**
 * Loads application configuration from JSON.
 * Search order:
 * 1) ./config.json
 * 2) classpath: config.json
 * 3) defaults
 */
public final class ConfigLoader {
    private static final Logger logger = Logger.getLogger(ConfigLoader.class.getName());
    private static final String CONFIG_FILE = "config.json";

    private ConfigLoader() {}

    public static AppConfig load() {
        AppConfig cfg = tryLoadFromWorkingDir();
        if (cfg != null) return cfg;
        cfg = tryLoadFromClasspath();
        return cfg != null ? cfg : new AppConfig();
    }

    private static AppConfig tryLoadFromWorkingDir() {
        Path p = Paths.get(CONFIG_FILE);
        if (!Files.exists(p)) return null;
        try {
            return FilePersistenceUtil.json().readValue(p.toFile(), AppConfig.class);
        } catch (IOException e) {
            logger.warning("Failed to load config.json from working dir: " + e.getMessage());
            return null;
        }
    }

    private static AppConfig tryLoadFromClasspath() {
        try (InputStream in = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (in == null) return null;
            return FilePersistenceUtil.json().readValue(in, AppConfig.class);
        } catch (IOException e) {
            logger.warning("Failed to load config.json from classpath: " + e.getMessage());
            return null;
        }
    }
}

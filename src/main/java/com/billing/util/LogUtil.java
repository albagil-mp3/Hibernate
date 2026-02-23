package com.billing.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.billing.config.AppConfig;

/**
 * Configures logging to file with rotation.
 */
public final class LogUtil {
    private LogUtil() {}

    public static void configure(AppConfig cfg) {
        String dir = cfg != null ? cfg.getLogDir() : "logs";
        Path logDir = Paths.get(dir);
        try {
            Files.createDirectories(logDir);
        } catch (IOException e) {
            // If log dir fails, fallback to console only.
            Logger.getLogger(LogUtil.class.getName()).warning("Failed to create log dir: " + e.getMessage());
            return;
        }

        Logger root = Logger.getLogger("");
        for (Handler h : root.getHandlers()) {
            root.removeHandler(h);
        }

        root.setLevel(Level.INFO);

        ConsoleHandler console = new ConsoleHandler();
        console.setLevel(Level.INFO);
        console.setFormatter(new SimpleFormatter());
        root.addHandler(console);

        try {
            String pattern = logDir.resolve("app-%g.log").toString();
            FileHandler file = new FileHandler(pattern, 1_000_000, 5, true);
            file.setLevel(Level.INFO);
            file.setFormatter(new SimpleFormatter());
            root.addHandler(file);
        } catch (IOException e) {
            Logger.getLogger(LogUtil.class.getName()).warning("Failed to set file logging: " + e.getMessage());
        }
    }
}

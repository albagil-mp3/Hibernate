package com.billing.config;

/**
 * Application configuration loaded from JSON.
 */
public class AppConfig {
    private String exportDir = "data/exports";
    private String importDir = "data/imports";
    private String backupDir = "data/backups";
    private String logDir = "logs";

    public AppConfig() {}

    public String getExportDir() { return exportDir; }
    public void setExportDir(String exportDir) { this.exportDir = exportDir; }

    public String getImportDir() { return importDir; }
    public void setImportDir(String importDir) { this.importDir = importDir; }

    public String getBackupDir() { return backupDir; }
    public void setBackupDir(String backupDir) { this.backupDir = backupDir; }

    public String getLogDir() { return logDir; }
    public void setLogDir(String logDir) { this.logDir = logDir; }
}

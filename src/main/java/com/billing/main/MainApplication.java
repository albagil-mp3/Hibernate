package com.billing.main;

import java.awt.Font;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.billing.gui.MainWindow;
import com.billing.config.AppConfig;
import com.billing.config.ConfigLoader;
import com.billing.util.HibernateUtil;
import com.billing.util.LogUtil;

/**
 * Main application class for the Hibernate Billing System
 * Initializes the GUI and sets up the application
 */
public class MainApplication {
    
    private static final Logger logger = Logger.getLogger(MainApplication.class.getName());
    public static void main(String[] args) {
        try {
            AppConfig cfg = ConfigLoader.load();
            LogUtil.configure(cfg);
            
            // Set Look and Feel to system default
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Set default font to Calibri 12pt
            setDefaultFont();

            // Initialize Hibernate
            logger.info("Initializing Hibernate with MySQL database...");
            HibernateUtil.getSessionFactory();
            
            // Suppress Hibernate logs after initialization
            suppressHibernateLogs();
            
            // Check if initialization was successful
            if (!HibernateUtil.isInitialized()) {
                Exception initError = HibernateUtil.getInitializationError();
                logger.severe(() -> "Hibernate initialization failed: " + 
                    (initError != null ? initError.getMessage() : "Unknown error"));
                if (initError != null) {
                    initError.printStackTrace();
                }
            }
            
            // Start GUI on EDT
            SwingUtilities.invokeLater(() -> {
                try {
                    MainWindow mainWindow = new MainWindow();
                    mainWindow.setVisible(true);
                    if (HibernateUtil.isInitialized()) {
                        logger.info("Application started successfully with MySQL database");
                    } else {
                        logger.warning("Application started in offline mode (MySQL not available)");
                    }
                    
                    // Check database connection status
                    if (!HibernateUtil.isInitialized()) {
                        showDatabaseConnectionError(HibernateUtil.getInitializationError());
                    }
                } catch (Exception e) {
                    logger.severe(() -> "Error starting application: " + e.getMessage());
                    showDatabaseConnectionError(e);
                }
            });
            
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException e) {
            logger.severe(() -> "Failed to set look and feel: " + e.getMessage());
            showDatabaseConnectionError(e);
        } catch (RuntimeException e) {
            logger.severe(() -> "Failed to start application: " + e.getMessage());
            showDatabaseConnectionError(e);
        }
        
        // Add shutdown hook to cleanup resources
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutting down application...");
            HibernateUtil.shutdown();
            logger.info("Application shut down completed");
        }));
    }
    
    /**
     * Shows database connection error message with helpful instructions
     */
    private static void showDatabaseConnectionError(Exception e) {
        String message = "Database Connection Error\n\n";
        String errorMessage = (e != null && e.getMessage() != null) ? e.getMessage() : "Unknown database error";
        
        if (errorMessage.contains("Communications link failure") || 
            errorMessage.contains("Connection refused")) {
            message += """
                       Cannot connect to MySQL database.
                       
                       Please check:
                       \u2022 MySQL server is running
                       \u2022 Database 'facturacio' exists
                       \u2022 Username and password are correct
                       \u2022 MySQL is listening on port 3306
                       
                       The application will start in offline mode.
                       Database features will not be available.""";
        } else if (errorMessage.contains("Table") || errorMessage.contains("schema") || 
                   errorMessage.contains("column") || errorMessage.contains("doesn't exist")) {
            message += """
                       Database schema mismatch detected.
                       
                       Please run the database_schema_jpa.sql script to:
                       \u2022 Recreate all tables with correct structure
                       \u2022 Update column names and types
                       \u2022 Add sample data
                       
                       The application will continue in offline mode.""";
        } else {
            message += "Error: " + errorMessage + "\n\n" +
                      "Please check your database configuration and schema.";
        }
        
        JOptionPane.showMessageDialog(null, message, "Database Error", JOptionPane.WARNING_MESSAGE);
    }

    private static void suppressHibernateLogs() {
        Logger.getLogger("org.hibernate").setLevel(Level.OFF);
        Logger.getLogger("org.hibernate.SQL").setLevel(Level.OFF);
        Logger.getLogger("org.hibernate.engine.jdbc.spi.SqlExceptionHelper").setLevel(Level.OFF);
        Logger.getLogger("org.hibernate.engine.jdbc.connections.internal.DriverManagerConnectionProviderImpl").setLevel(Level.OFF);
    }
    
    /**
     * Sets the default font for all Swing components to Calibri 12pt
     */
    private static void setDefaultFont() {
        Font calibriFont = new Font("Calibri", Font.PLAIN, 12);
        Font calibriBoldFont = new Font("Calibri", Font.BOLD, 12);
        
        // Set font for all UI components
        UIManager.put("Button.font", calibriFont);
        UIManager.put("Label.font", calibriFont);
        UIManager.put("TextField.font", calibriFont);
        UIManager.put("TextArea.font", calibriFont);
        UIManager.put("ComboBox.font", calibriFont);
        UIManager.put("Table.font", calibriFont);
        UIManager.put("TableHeader.font", calibriBoldFont);
        UIManager.put("Menu.font", calibriFont);
        UIManager.put("MenuItem.font", calibriFont);
        UIManager.put("TabbedPane.font", calibriFont);
        UIManager.put("TitledBorder.font", calibriBoldFont);
        UIManager.put("CheckBox.font", calibriFont);
        UIManager.put("RadioButton.font", calibriFont);
        UIManager.put("List.font", calibriFont);
        UIManager.put("Panel.font", calibriFont);
    }
}

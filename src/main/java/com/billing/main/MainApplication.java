package com.billing.main;

import com.billing.gui.MainWindow;
import com.billing.util.HibernateUtil;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

/**
 * Main application class for the Hibernate Billing System
 * Initializes the GUI and sets up the application
 */
public class MainApplication {
    
    private static final Logger logger = Logger.getLogger(MainApplication.class.getName());
    
    public static void main(String[] args) {
        try {
            // Set Look and Feel to system default
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Set default font to Calibri 12pt
            setDefaultFont();
            
            // Ask user to select database type
            String databaseType = selectDatabaseType();
            if (databaseType == null) {
                System.exit(0); // User cancelled
            }
            
            // Set the database type before creating SessionFactory
            HibernateUtil.setDatabaseType(databaseType);
            
            // Initialize Hibernate
            logger.info("Initializing Hibernate with " + databaseType + " database...");
            
            // Start GUI on EDT
            SwingUtilities.invokeLater(() -> {
                try {
                    MainWindow mainWindow = new MainWindow();
                    mainWindow.setVisible(true);
                    logger.info("Application started successfully with " + databaseType + " database");
                    
                    // Check database connection status
                    if (!HibernateUtil.isInitialized()) {
                        showDatabaseConnectionError(HibernateUtil.getInitializationError(), databaseType);
                    }
                } catch (Exception e) {
                    logger.severe("Error starting application: " + e.getMessage());
                    showDatabaseConnectionError(e, databaseType);
                }
            });
            
        } catch (Exception e) {
            logger.severe("Failed to start application: " + e.getMessage());
            showDatabaseConnectionError(e, "unknown");
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
    private static void showDatabaseConnectionError(Exception e, String databaseType) {
        String message = "Database Connection Error\n\n";
        
        if ("oracle".equals(databaseType)) {
            if (e.getMessage().contains("Listener refused the connection") || 
                e.getMessage().contains("Connection refused") ||
                e.getMessage().contains("TNS")) {
                message += "Cannot connect to Oracle database.\n\n" +
                          "Please check:\n" +
                          "• Oracle database server is running\n" +
                          "• TNS Listener is running (lsnrctl status)\n" +
                          "• Database 'XE' is available\n" +
                          "• Username 'facturacion' and password are correct\n" +
                          "• Oracle is listening on port 1521\n" +
                          "• Use SQL*Plus or SQL Developer to verify connection:\n" +
                          "  sqlplus facturacion/facturacion@localhost:1521/XE\n\n" +
                          "The application will start in offline mode.\n" +
                          "Database features will not be available.";
            } else {
                message += "Error: " + e.getMessage() + "\n\n" +
                          "Please check your Oracle database configuration.";
            }
        } else {
            if (e.getMessage().contains("Communications link failure") || 
                e.getMessage().contains("Connection refused")) {
                message += "Cannot connect to MySQL database.\n\n" +
                          "Please check:\n" +
                          "• MySQL server is running\n" +
                          "• Database 'facturacion' exists\n" +
                          "• Username and password are correct\n" +
                          "• MySQL is listening on port 3306\n\n" +
                          "The application will start in offline mode.\n" +
                          "Database features will not be available.";
            } else {
                message += "Error: " + e.getMessage() + "\n\n" +
                          "Please check your database configuration.";
            }
        }
        
        JOptionPane.showMessageDialog(null, message, "Database Error", JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * Shows database selection dialog
     * @return selected database type or null if cancelled
     */
    private static String selectDatabaseType() {
        String[] options = {"MySQL", "Oracle"};
        int choice = JOptionPane.showOptionDialog(
            null,
            "Select the database type to use:\n\n" +
            "• MySQL - for MySQL/MariaDB databases\n" +
            "• Oracle - for Oracle databases (compatible with SQL*Plus and SQL Developer)",
            "Database Selection",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );
        
        if (choice == -1) {
            return null; // User cancelled
        }
        
        return choice == 0 ? "mysql" : "oracle";
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
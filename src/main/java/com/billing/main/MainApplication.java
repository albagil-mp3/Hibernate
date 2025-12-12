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
            
            // Initialize Hibernate
            logger.info("Initializing Hibernate with MySQL database...");
            
            // Start GUI on EDT
            SwingUtilities.invokeLater(() -> {
                try {
                    MainWindow mainWindow = new MainWindow();
                    mainWindow.setVisible(true);
                    logger.info("Application started successfully with MySQL database");
                    
                    // Check database connection status
                    if (!HibernateUtil.isInitialized()) {
                        showDatabaseConnectionError(HibernateUtil.getInitializationError(), "mysql");
                    }
                } catch (Exception e) {
                    logger.severe("Error starting application: " + e.getMessage());
                    showDatabaseConnectionError(e, "mysql");
                }
            });
            
        } catch (Exception e) {
            logger.severe("Failed to start application: " + e.getMessage());
            showDatabaseConnectionError(e, "mysql");
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
        
        if (e.getMessage().contains("Communications link failure") || 
            e.getMessage().contains("Connection refused")) {
            message += """
                      Cannot connect to MySQL database.
                      
                      Please check:
                      • MySQL server is running
                      • Database 'hibernate' exists
                      • Username and password are correct
                      • MySQL is listening on port 3306
                      
                      The application will start in offline mode.
                      Database features will not be available.""";
        } else {
            message += "Error: " + e.getMessage() + "\n\n" +
                      "Please check your database configuration.";
        }
        
        JOptionPane.showMessageDialog(null, message, "Database Error", JOptionPane.WARNING_MESSAGE);
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
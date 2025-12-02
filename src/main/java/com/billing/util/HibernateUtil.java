package com.billing.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.util.logging.Logger;

/**
 * Hibernate utility class for managing SessionFactory
 * Follows Singleton pattern to ensure only one SessionFactory instance
 * Supports both MySQL and Oracle databases
 */
public class HibernateUtil {
    
    private static final Logger logger = Logger.getLogger(HibernateUtil.class.getName());
    private static SessionFactory sessionFactory;
    private static boolean initialized = false;
    private static Exception initializationError = null;
    private static String currentDatabase = "mysql"; // Default to MySQL
    
    /**
     * Sets the database type to use (mysql or oracle)
     * Must be called before first getSessionFactory() call
     * @param databaseType "mysql" or "oracle"
     */
    public static void setDatabaseType(String databaseType) {
        if (initialized) {
            throw new IllegalStateException("Database type cannot be changed after SessionFactory initialization");
        }
        currentDatabase = databaseType;
    }
    
    /**
     * Gets the current database type
     * @return current database type (mysql or oracle)
     */
    public static String getDatabaseType() {
        return currentDatabase;
    }
    
    /**
     * Initializes the SessionFactory lazily
     * @return SessionFactory instance or null if initialization failed
     */
    public static SessionFactory getSessionFactory() {
        if (!initialized) {
            initializeSessionFactory();
        }
        return sessionFactory;
    }
    
    /**
     * Initializes the SessionFactory with proper error handling
     */
    private static synchronized void initializeSessionFactory() {
        if (initialized) {
            return;
        }
        
        try {
            String configFile = getConfigurationFile();
            logger.info("Initializing Hibernate SessionFactory with " + configFile + "...");
            // Create the SessionFactory from appropriate configuration file
            sessionFactory = new Configuration().configure(configFile).buildSessionFactory();
            logger.info("Hibernate SessionFactory initialized successfully for " + currentDatabase + " database");
        } catch (Exception ex) {
            // Log the exception but don't throw it
            logger.severe("SessionFactory creation failed: " + ex.getMessage());
            initializationError = ex;
            sessionFactory = null;
        } finally {
            initialized = true;
        }
    }
    
    /**
     * Gets the appropriate configuration file based on current database setting
     * @return the configuration file name
     */
    private static String getConfigurationFile() {
        if ("oracle".equalsIgnoreCase(currentDatabase)) {
            return "hibernate-oracle.cfg.xml";
        }
        return "hibernate.cfg.xml"; // Default MySQL configuration
    }
    
    /**
     * Checks if Hibernate is properly initialized
     * @return true if SessionFactory is available
     */
    public static boolean isInitialized() {
        return initialized && sessionFactory != null;
    }
    
    /**
     * Gets the initialization error if any
     * @return Exception that occurred during initialization or null
     */
    public static Exception getInitializationError() {
        return initializationError;
    }
    
    /**
     * Closes the SessionFactory and releases resources
     */
    public static void shutdown() {
        if (sessionFactory != null) {
            try {
                sessionFactory.close();
                logger.info("Hibernate SessionFactory shutdown completed");
            } catch (Exception e) {
                logger.warning("Error during SessionFactory shutdown: " + e.getMessage());
            }
        }
    }
}
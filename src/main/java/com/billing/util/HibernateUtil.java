package com.billing.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.util.logging.Logger;

/**
 * Hibernate utility class for managing SessionFactory
 * Follows Singleton pattern to ensure only one SessionFactory instance
 * Configured for MySQL database
 */
public class HibernateUtil {
    
    private static final Logger logger = Logger.getLogger(HibernateUtil.class.getName());
    private static SessionFactory sessionFactory;
    private static boolean initialized = false;
    private static Exception initializationError = null;
    
    
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
            logger.info("Hibernate SessionFactory initialized successfully for MySQL database");
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
     * Gets the appropriate configuration file for MySQL
     * @return the configuration file name
     */
    private static String getConfigurationFile() {
        return "hibernate.cfg.xml"; // MySQL configuration
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
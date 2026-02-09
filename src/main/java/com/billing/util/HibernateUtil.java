package com.billing.util;

import java.net.URL;
import java.util.logging.Logger;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

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
            logger.info(() -> "Initializing Hibernate SessionFactory with " + configFile + "...");
            
            // Try to load the configuration file explicitly
            URL configURL = HibernateUtil.class.getClassLoader().getResource(configFile);
            if (configURL == null) {
                throw new RuntimeException("Configuration file '" + configFile + "' not found in classpath");
            }
            logger.info(() -> "Configuration file found at: " + configURL.toExternalForm());
            
            // Create the SessionFactory from configuration file
            sessionFactory = new Configuration().configure(configFile).buildSessionFactory();
            initializationError = null;
            initialized = true;
            logger.info("Hibernate SessionFactory initialized successfully for MySQL database");
        } catch (HibernateException ex) {
            // Specific Hibernate-related errors
            logger.severe(() -> "Hibernate error during SessionFactory creation: " + ex.getMessage());
            initializationError = ex;
            sessionFactory = null;
            initialized = false;
        } catch (RuntimeException ex) {
            // Other runtime problems (config file missing, classpath issues, etc.)
            logger.severe(() -> "Runtime error during SessionFactory creation: " + ex.getMessage());
            initializationError = ex;
            sessionFactory = null;
            initialized = false;
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
        return sessionFactory != null;
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
                // Mark as not initialized so a new SessionFactory can be created later if needed
                sessionFactory = null;
                initialized = false;
            } catch (HibernateException e) {
                logger.warning(() -> "Hibernate error during SessionFactory shutdown: " + e.getMessage());
            } catch (RuntimeException e) {
                logger.warning(() -> "Runtime error during SessionFactory shutdown: " + e.getMessage());
            }
        }
    }
}
package com.billing.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * Simple JDBC utility to obtain a Connection to MySQL using values from `hibernate.cfg.xml` environment.
 * For demonstration only. In production use a proper DataSource and connection pooling.
 */
public final class JdbcUtil {
    private static final Logger logger = Logger.getLogger(JdbcUtil.class.getName());

    private JdbcUtil() {}

    public static Connection getConnection() throws SQLException {
        // Attempt to read from system properties first, otherwise fall back to common defaults
        String url = System.getProperty("billing.jdbc.url", "jdbc:mysql://localhost:3306/Facturacio?serverTimezone=UTC");
        String user = System.getProperty("billing.jdbc.user", "root");
        String pass = System.getProperty("billing.jdbc.password", "");

        logger.info(() -> "Opening JDBC connection to " + url + " as user=" + user);
        return DriverManager.getConnection(url, user, pass);
    }
}

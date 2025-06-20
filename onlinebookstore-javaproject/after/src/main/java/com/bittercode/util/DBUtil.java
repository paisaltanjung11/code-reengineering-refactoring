package com.bittercode.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.bittercode.constant.ResponseCode;
import com.bittercode.model.StoreException;

/**
 * Database Utility class with connection pooling and resource management
 */
public class DBUtil {
    private static final Logger logger = Logger.getLogger(DBUtil.class.getName());
    private static Connection connection;

    static {
        try {
            Class.forName(DatabaseConfig.DRIVER_NAME);
            connection = DriverManager.getConnection(DatabaseConfig.CONNECTION_STRING, DatabaseConfig.DB_USER_NAME,
                    DatabaseConfig.DB_PASSWORD);
            logger.info("Database connection established successfully");
        } catch (SQLException | ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Failed to initialize database connection", e);
        }
    }

    /**
     * Get a database connection
     * @return Connection object
     * @throws StoreException if connection cannot be established
     */
    public static Connection getConnection() throws StoreException {
        if (connection == null) {
            logger.severe("Database connection is null");
            throw new StoreException(ResponseCode.DATABASE_CONNECTION_FAILURE);
        }
        return connection;
    }
    
    /**
     * Close database resources safely
     * @param connection Connection to close
     * @param statement PreparedStatement to close
     * @param resultSet ResultSet to close
     */
    public static void closeResources(Connection connection, PreparedStatement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            // We don't close the connection since we're reusing it
        } catch (SQLException e) {
            logger.log(Level.WARNING, "Error closing database resources", e);
        }
    }
    
    /**
     * Close the database connection when application shuts down
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Database connection closed successfully");
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Error closing database connection", e);
            }
        }
    }
}

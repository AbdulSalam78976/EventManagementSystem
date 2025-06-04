package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Utility class for managing database connections
 */
public class DatabaseConnection {
    private static Connection connection = null;

    /**
     * Get a database connection
     * @return a Connection object
     * @throws SQLException if there is an error connecting to the database
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                // Load database properties
                Properties props = new Properties();
                // Use class loader to find the properties file
                try (var inputStream = DatabaseConnection.class.getClassLoader().getResourceAsStream("database.properties")) {
                    if (inputStream == null) {
                        throw new SQLException("database.properties file not found in classpath");
                    }
                    props.load(inputStream);
                }

                // Get database connection properties
                String url = props.getProperty("db.url");
                String username = props.getProperty("db.username");
                String password = props.getProperty("db.password");

                // Create connection
                connection = DriverManager.getConnection(url, username, password);
            } catch (Exception e) {
                throw new SQLException("Error connecting to database: " + e.getMessage());
            }

        }
        return connection;
    }

    /**
     * Close the database connection
     * @throws SQLException if there is an error closing the connection
     */
    public static void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
} 

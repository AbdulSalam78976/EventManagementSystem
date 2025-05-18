package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;

public class DatabaseUtil {
    private static final Properties props = new Properties();
    private static String url;
    private static String username;
    private static String password;
    private static String driver;

    static {
        try {
            // Check if properties file exists
            File propFile = new File("database.properties");
            if (!propFile.exists()) {
                throw new IOException("database.properties file not found in: " + propFile.getAbsolutePath());
            }

            // Load database properties
            props.load(new FileInputStream(propFile));
            url = props.getProperty("db.url");
            username = props.getProperty("db.username");
            password = props.getProperty("db.password");
            driver = props.getProperty("db.driver");

            // Validate properties
            if (url == null || username == null || password == null || driver == null) {
                throw new IOException("Missing required database properties. Please check database.properties file.");
            }

            System.out.println("Loading database properties:");
            System.out.println("URL: " + url);
            System.out.println("Username: " + username);
            System.out.println("Driver: " + driver);

            // Load the JDBC driver
            try {
                Class.forName(driver);
                System.out.println("JDBC driver loaded successfully");
            } catch (ClassNotFoundException e) {
                System.out.println("Failed to load JDBC driver. Make sure mysql-connector-java-8.0.xx.jar is in the lib directory");
                throw e;
            }
        } catch (Exception e) {
            System.out.println("Error initializing database connection:");
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database connection", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Database connection established successfully");
            return conn;
        } catch (SQLException e) {
            System.out.println("Failed to establish database connection:");
            System.out.println("Error code: " + e.getErrorCode());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error message: " + e.getMessage());
            throw e;
        }
    }

    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Database connection closed successfully");
            } catch (SQLException e) {
                System.out.println("Error closing database connection:");
                e.printStackTrace();
            }
        }
    }

    // Test database connection
    public static boolean testConnection() {
        System.out.println("\nTesting database connection...");
        try (Connection conn = getConnection()) {
            boolean isConnected = conn != null && !conn.isClosed();
            if (isConnected) {
                System.out.println("Database connection test successful");
            } else {
                System.out.println("Database connection test failed: Connection is null or closed");
            }
            return isConnected;
        } catch (SQLException e) {
            System.out.println("Database connection test failed:");
            System.out.println("Error code: " + e.getErrorCode());
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error message: " + e.getMessage());
            return false;
        }
    }
} 
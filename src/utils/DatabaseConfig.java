package utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Database configuration utility class
 */
public class DatabaseConfig {
    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "database.properties";
    
    static {
        try {
            properties.load(new FileInputStream(CONFIG_FILE));
        } catch (IOException e) {
            // Set default values if config file is not found
            properties.setProperty("db.url", "jdbc:mysql://localhost:3306/event_management");
            properties.setProperty("db.username", "root");
            properties.setProperty("db.password", "");
            properties.setProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        }
    }
    
    public static String getUrl() {
        return properties.getProperty("db.url");
    }
    
    public static String getUsername() {
        return properties.getProperty("db.username");
    }
    
    public static String getPassword() {
        return properties.getProperty("db.password");
    }
    
    public static String getDriver() {
        return properties.getProperty("db.driver");
    }
} 
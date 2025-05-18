package database;

import utils.DatabaseUtil;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class TestDatabase {
    public static void main(String[] args) {
        System.out.println("Testing database connection...");
        
        try {
            // Test database connection
            if (DatabaseUtil.testConnection()) {
                System.out.println("✅ Database connection successful!");

                // Test query for users
                try (Connection conn = DatabaseUtil.getConnection();
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM users")) {
                    
                    if (rs.next()) {
                        System.out.println("✅ Found " + rs.getInt(1) + " users in database");
                    }
                }

                // Test categories
                try (Connection conn = DatabaseUtil.getConnection();
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM categories")) {
                    
                    if (rs.next()) {
                        System.out.println("✅ Found " + rs.getInt(1) + " categories in database");
                    }
                }

                // List all tables
                try (Connection conn = DatabaseUtil.getConnection();
                     Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SHOW TABLES")) {
                    
                    System.out.println("\nDatabase tables:");
                    while (rs.next()) {
                        System.out.println("✅ " + rs.getString(1));
                    }
                }
            } else {
                System.out.println("❌ Database connection failed!");
            }
        } catch (Exception e) {
            System.out.println("\n❌ Error testing database:");
            System.out.println("Error message: " + e.getMessage());
            System.out.println("\nStack trace:");
            e.printStackTrace();
        }
    }
} 
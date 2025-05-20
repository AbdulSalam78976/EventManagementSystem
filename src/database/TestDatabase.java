package database;

import utils.DatabaseUtil;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

public class TestDatabase {
    public static void main(String[] args) {
        System.out.println("Testing database connection and schema...");
        
        try {
            // Test database connection
            if (DatabaseUtil.testConnection()) {
                System.out.println("✅ Database connection successful!");

                // Test users table
                testTable("users", new String[]{
                    "id", "name", "email", "role", "active", "registration_date",
                    "security_question1", "security_question2"
                });

                // Test events table
                testTable("events", new String[]{
                    "id", "title", "category", "venue_name", "event_date",
                    "total_slots", "status"
                });

                // Test registrations table
                testTable("registrations", new String[]{
                    "id", "event_id", "user_id", "registration_date", "status"
                });

                // Test feedback table
                testTable("feedback", new String[]{
                    "id", "event_id", "user_id", "rating", "is_anonymous"
                });

                // Test notifications table
                testTable("notifications", new String[]{
                    "id", "user_id", "title", "type", "is_read"
                });

                // Test default admin user
                testDefaultAdmin();

                // List all tables
                listAllTables();

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

    private static void testTable(String tableName, String[] columns) {
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Test table exists
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
                if (rs.next()) {
                    System.out.println("\n✅ Table '" + tableName + "' exists with " + rs.getInt(1) + " records");
                }
            }

            // Test columns
            try (ResultSet rs = stmt.executeQuery("SHOW COLUMNS FROM " + tableName)) {
                System.out.println("Columns in " + tableName + ":");
                while (rs.next()) {
                    String columnName = rs.getString("Field");
                    String columnType = rs.getString("Type");
                    System.out.println("  ✅ " + columnName + " (" + columnType + ")");
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error testing table " + tableName + ": " + e.getMessage());
        }
    }

    private static void testDefaultAdmin() {
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(
                 "SELECT * FROM users WHERE email = 'admin@example.com' AND role = 'ADMIN'")) {
            
            if (rs.next()) {
                System.out.println("\n✅ Default admin user exists:");
                System.out.println("  Name: " + rs.getString("name"));
                System.out.println("  Email: " + rs.getString("email"));
                System.out.println("  Role: " + rs.getString("role"));
                System.out.println("  Active: " + rs.getBoolean("active"));
            } else {
                System.out.println("\n❌ Default admin user not found!");
            }
        } catch (SQLException e) {
            System.out.println("❌ Error testing default admin: " + e.getMessage());
        }
    }

    private static void listAllTables() {
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW TABLES")) {
            
            System.out.println("\nDatabase tables:");
            while (rs.next()) {
                System.out.println("✅ " + rs.getString(1));
            }
        } catch (SQLException e) {
            System.out.println("❌ Error listing tables: " + e.getMessage());
        }
    }
} 
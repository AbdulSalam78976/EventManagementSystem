package components;

import utils.AppColors;
import utils.EmojiUtils;
import utils.UIUtils;
import utils.UIConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Comprehensive demo showcasing emoji usage throughout the entire Event Management System
 * Demonstrates consistent emoji implementation across all UI components
 */
public class EmojiSystemDemo extends JFrame {
    
    public EmojiSystemDemo() {
        setTitle("🎉 Event Management System - Emoji Implementation Demo");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 900);
        setLocationRelativeTo(null);
        
        setupUI();
        setVisible(true);
    }
    
    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(AppColors.BACKGROUND_LIGHT);
        
        // Create tabbed pane to show different sections
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        
        // Add tabs with emoji icons
        tabbedPane.addTab("🏠 Dashboard", createDashboardDemo());
        tabbedPane.addTab("📅 Events", createEventsDemo());
        tabbedPane.addTab("👥 Users", createUsersDemo());
        tabbedPane.addTab("🔧 Components", createComponentsDemo());
        tabbedPane.addTab("📊 Statistics", createStatisticsDemo());
        
        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        
        // Header with system title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(AppColors.PRIMARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel titleLabel = new JLabel("🎯 Event Management System - Complete Emoji Integration");
        titleLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        JLabel subtitleLabel = new JLabel("✨ Modern UI with emoji icons throughout the entire system");
        subtitleLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(255, 255, 255, 200));
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        setContentPane(mainPanel);
    }
    
    private JPanel createDashboardDemo() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Dashboard header
        JLabel headerLabel = new JLabel("🏠 Dashboard Components");
        headerLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        headerLabel.setForeground(AppColors.TEXT_PRIMARY);
        panel.add(headerLabel, BorderLayout.NORTH);
        
        // Stats cards grid
        JPanel statsGrid = new JPanel(new GridLayout(2, 4, 15, 15));
        statsGrid.setOpaque(false);
        
        // Create various stat cards with different styles
        ModernStatCard totalEvents = new ModernStatCard("📅", "Total Events", "156", "All events in system",
            new Color(74, 144, 226), null, ModernStatCard.CardStyle.DEFAULT, ModernStatCard.CardSize.MEDIUM);
        
        ModernStatCard activeUsers = new ModernStatCard("👥", "Active Users", "1,234", "Currently online",
            new Color(52, 168, 83), new Color(34, 139, 34), ModernStatCard.CardStyle.GRADIENT, ModernStatCard.CardSize.MEDIUM);
        
        ModernStatCard pendingApprovals = new ModernStatCard("⏳", "Pending", "23", "Awaiting approval",
            new Color(251, 188, 5), null, ModernStatCard.CardStyle.OUTLINED, ModernStatCard.CardSize.MEDIUM);
        
        ModernStatCard completedEvents = new ModernStatCard("✅", "Completed", "89", "Successfully finished",
            new Color(234, 67, 53), null, ModernStatCard.CardStyle.ELEVATED, ModernStatCard.CardSize.MEDIUM);
        
        ModernStatCard registrations = new ModernStatCard("📝", "Registrations", "2,567", "Total sign-ups",
            new Color(156, 39, 176), null, ModernStatCard.CardStyle.DEFAULT, ModernStatCard.CardSize.MEDIUM);
        
        ModernStatCard revenue = new ModernStatCard("💰", "Revenue", "$45,678", "Total earnings",
            new Color(0, 150, 136), new Color(0, 121, 107), ModernStatCard.CardStyle.GRADIENT, ModernStatCard.CardSize.MEDIUM);
        
        ModernStatCard feedback = new ModernStatCard("⭐", "Avg Rating", "4.8", "User satisfaction",
            new Color(255, 152, 0), null, ModernStatCard.CardStyle.ELEVATED, ModernStatCard.CardSize.MEDIUM);
        
        ModernStatCard notifications = new ModernStatCard("🔔", "Notifications", "12", "Unread messages",
            new Color(233, 30, 99), null, ModernStatCard.CardStyle.OUTLINED, ModernStatCard.CardSize.MEDIUM);
        
        statsGrid.add(totalEvents);
        statsGrid.add(activeUsers);
        statsGrid.add(pendingApprovals);
        statsGrid.add(completedEvents);
        statsGrid.add(registrations);
        statsGrid.add(revenue);
        statsGrid.add(feedback);
        statsGrid.add(notifications);
        
        panel.add(statsGrid, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createEventsDemo() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("📅 Event Categories & Status");
        headerLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        headerLabel.setForeground(AppColors.TEXT_PRIMARY);
        panel.add(headerLabel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        contentPanel.setOpaque(false);
        
        // Event categories
        JPanel categoriesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        categoriesPanel.setOpaque(false);
        categoriesPanel.setBorder(BorderFactory.createTitledBorder("Event Categories"));
        
        String[] categories = {"academic", "sports", "cultural", "technical", "workshop", "seminar", "conference", "social", "competition", "exhibition"};
        for (String category : categories) {
            String emoji = EmojiUtils.getEventCategoryEmoji(category);
            JButton categoryBtn = new JButton(emoji + " " + category.substring(0, 1).toUpperCase() + category.substring(1));
            categoryBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
            categoryBtn.setBackground(new Color(248, 249, 250));
            categoryBtn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            categoriesPanel.add(categoryBtn);
        }
        
        // Event statuses
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        statusPanel.setOpaque(false);
        statusPanel.setBorder(BorderFactory.createTitledBorder("Event Status"));
        
        String[] statuses = {"APPROVED", "PENDING", "REJECTED", "CANCELLED", "COMPLETED", "ONGOING", "DRAFT"};
        for (String status : statuses) {
            String emoji = EmojiUtils.getEventStatusEmoji(status);
            JLabel statusLabel = EmojiUtils.createStatusLabel(emoji, status, getStatusColor(status));
            statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            statusLabel.setOpaque(true);
            statusLabel.setBackground(new Color(248, 249, 250));
            statusPanel.add(statusLabel);
        }
        
        contentPanel.add(categoriesPanel);
        contentPanel.add(statusPanel);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createUsersDemo() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("👥 User Roles & Registration Status");
        headerLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        headerLabel.setForeground(AppColors.TEXT_PRIMARY);
        panel.add(headerLabel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        contentPanel.setOpaque(false);
        
        // User roles
        JPanel rolesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        rolesPanel.setOpaque(false);
        rolesPanel.setBorder(BorderFactory.createTitledBorder("User Roles"));
        
        String[] roles = {"ADMIN", "ORGANIZER", "ATTENDEE", "STUDENT", "FACULTY", "STAFF"};
        for (String role : roles) {
            String emoji = EmojiUtils.getUserRoleEmoji(role);
            JButton roleBtn = new JButton(emoji + " " + role);
            roleBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
            roleBtn.setBackground(new Color(248, 249, 250));
            roleBtn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            rolesPanel.add(roleBtn);
        }
        
        // Registration statuses
        JPanel regStatusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        regStatusPanel.setOpaque(false);
        regStatusPanel.setBorder(BorderFactory.createTitledBorder("Registration Status"));
        
        String[] regStatuses = {"APPROVED", "PENDING", "CANCELLED", "REJECTED", "CONFIRMED", "WAITLISTED"};
        for (String status : regStatuses) {
            String emoji = EmojiUtils.getRegistrationStatusEmoji(status);
            JLabel statusLabel = EmojiUtils.createStatusLabel(emoji, status, getStatusColor(status));
            statusLabel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            statusLabel.setOpaque(true);
            statusLabel.setBackground(new Color(248, 249, 250));
            regStatusPanel.add(statusLabel);
        }
        
        contentPanel.add(rolesPanel);
        contentPanel.add(regStatusPanel);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createComponentsDemo() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("🔧 UI Components with Emojis");
        headerLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        headerLabel.setForeground(AppColors.TEXT_PRIMARY);
        panel.add(headerLabel, BorderLayout.NORTH);
        
        JPanel contentPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        contentPanel.setOpaque(false);
        
        // Buttons
        JPanel buttonsPanel = EmojiUtils.createEmojiCard("🔘", "Emoji Buttons", createButtonsDemo());
        
        // File types
        JPanel fileTypesPanel = EmojiUtils.createEmojiCard("📁", "File Types", createFileTypesDemo());
        
        // Navigation
        JPanel navPanel = EmojiUtils.createEmojiCard("🧭", "Navigation", createNavigationDemo());
        
        // Actions
        JPanel actionsPanel = EmojiUtils.createEmojiCard("⚡", "Actions", createActionsDemo());
        
        contentPanel.add(buttonsPanel);
        contentPanel.add(fileTypesPanel);
        contentPanel.add(navPanel);
        contentPanel.add(actionsPanel);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createStatisticsDemo() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel headerLabel = new JLabel("📊 Statistics & Analytics");
        headerLabel.setFont(new Font("Segoe UI Emoji", Font.BOLD, 20));
        headerLabel.setForeground(AppColors.TEXT_PRIMARY);
        panel.add(headerLabel, BorderLayout.NORTH);
        
        // Create a grid of different sized stat cards
        JPanel statsPanel = new JPanel(new GridBagLayout());
        statsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Large cards
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        ModernStatCard largeCard1 = new ModernStatCard("🎯", "Event Success Rate", "94.5%", "Based on completion and feedback",
            new Color(52, 168, 83), new Color(34, 139, 34), ModernStatCard.CardStyle.GRADIENT, ModernStatCard.CardSize.LARGE);
        statsPanel.add(largeCard1, gbc);
        
        gbc.gridx = 2; gbc.gridy = 0;
        ModernStatCard largeCard2 = new ModernStatCard("📈", "Growth Rate", "+23%", "Compared to last quarter",
            new Color(74, 144, 226), new Color(63, 81, 181), ModernStatCard.CardStyle.GRADIENT, ModernStatCard.CardSize.LARGE);
        statsPanel.add(largeCard2, gbc);
        
        // Medium cards
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        ModernStatCard medCard1 = new ModernStatCard("⏱️", "Avg Duration", "2.5h", null,
            new Color(251, 188, 5), null, ModernStatCard.CardStyle.ELEVATED, ModernStatCard.CardSize.MEDIUM);
        statsPanel.add(medCard1, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        ModernStatCard medCard2 = new ModernStatCard("🎪", "Categories", "12", null,
            new Color(156, 39, 176), null, ModernStatCard.CardStyle.OUTLINED, ModernStatCard.CardSize.MEDIUM);
        statsPanel.add(medCard2, gbc);
        
        gbc.gridx = 2; gbc.gridy = 1;
        ModernStatCard medCard3 = new ModernStatCard("🌍", "Locations", "8", null,
            new Color(0, 150, 136), null, ModernStatCard.CardStyle.DEFAULT, ModernStatCard.CardSize.MEDIUM);
        statsPanel.add(medCard3, gbc);
        
        gbc.gridx = 3; gbc.gridy = 1;
        ModernStatCard medCard4 = new ModernStatCard("📱", "Mobile Users", "67%", null,
            new Color(233, 30, 99), null, ModernStatCard.CardStyle.ELEVATED, ModernStatCard.CardSize.MEDIUM);
        statsPanel.add(medCard4, gbc);
        
        panel.add(statsPanel, BorderLayout.CENTER);
        return panel;
    }
    
    // Helper methods for creating demo content
    private JPanel createButtonsDemo() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setOpaque(false);
        
        JButton saveBtn = EmojiUtils.createEmojiButton("💾", "Save", 14);
        JButton editBtn = EmojiUtils.createEmojiButton("✏️", "Edit", 14);
        JButton deleteBtn = EmojiUtils.createEmojiButton("🗑️", "Delete", 14);
        JButton shareBtn = EmojiUtils.createEmojiButton("📤", "Share", 14);
        
        panel.add(saveBtn);
        panel.add(editBtn);
        panel.add(deleteBtn);
        panel.add(shareBtn);
        
        return panel;
    }
    
    private JPanel createFileTypesDemo() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setOpaque(false);
        
        String[] fileTypes = {".pdf", ".doc", ".xls", ".ppt", ".jpg", ".mp4", ".mp3", ".zip"};
        for (String fileType : fileTypes) {
            String emoji = EmojiUtils.getFileTypeEmoji(fileType);
            JLabel fileLabel = new JLabel(emoji + " " + fileType.toUpperCase());
            fileLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
            fileLabel.setBorder(BorderFactory.createEmptyBorder(5, 8, 5, 8));
            fileLabel.setOpaque(true);
            fileLabel.setBackground(new Color(248, 249, 250));
            panel.add(fileLabel);
        }
        
        return panel;
    }
    
    private JPanel createNavigationDemo() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setOpaque(false);
        
        String[] navItems = {"🏠 Home", "📅 Events", "👥 Users", "⚙️ Settings", "📊 Reports", "🔔 Notifications"};
        for (String item : navItems) {
            JButton navBtn = new JButton(item);
            navBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
            navBtn.setBackground(new Color(248, 249, 250));
            navBtn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            panel.add(navBtn);
        }
        
        return panel;
    }
    
    private JPanel createActionsDemo() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        panel.setOpaque(false);
        
        String[] actions = {"✅ Approve", "❌ Reject", "⏳ Pending", "🔄 Refresh", "⬇️ Download", "⬆️ Upload"};
        for (String action : actions) {
            JButton actionBtn = new JButton(action);
            actionBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 12));
            actionBtn.setBackground(new Color(248, 249, 250));
            actionBtn.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            panel.add(actionBtn);
        }
        
        return panel;
    }
    
    private Color getStatusColor(String status) {
        switch (status.toUpperCase()) {
            case "APPROVED":
            case "CONFIRMED":
            case "COMPLETED": return new Color(52, 168, 83);
            case "PENDING":
            case "ONGOING":
            case "WAITLISTED": return new Color(251, 188, 5);
            case "REJECTED":
            case "CANCELLED": return new Color(234, 67, 53);
            case "DRAFT": return new Color(156, 39, 176);
            default: return AppColors.TEXT_SECONDARY;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                
                // Enable font smoothing for better emoji rendering
                System.setProperty("awt.useSystemAAFontSettings", "on");
                System.setProperty("swing.aatext", "true");
                
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new EmojiSystemDemo();
        });
    }
}

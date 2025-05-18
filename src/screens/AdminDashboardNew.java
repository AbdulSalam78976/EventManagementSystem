package screens;

import controllers.AuthController;
import controllers.EventController;
import controllers.UserController;
import controllers.SessionManager;
import models.User;
import models.Event;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import utils.*;
import components.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;

/**
 * Dashboard screen for Administrators
 * Implements the design from admin_dashboard.md
 */
@SuppressWarnings("unused")
public class AdminDashboardNew extends JFrame {
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private String username;
    private EventController eventController = EventController.getInstance();
    private UserController userController = UserController.getInstance();
    private boolean isDataLoaded = false;

    /**
     * Creates a new admin dashboard
     * 
     * @throws SQLException if there is an error connecting to the database
     */
    public AdminDashboardNew() throws SQLException {
        System.out.println("Initializing AdminDashboardNew");
        // Get the current user from the session
        User currentUser = SessionManager.getInstance().getCurrentUser();
        System.out.println("Current user: " + (currentUser != null ? currentUser.getName() : "null"));
        
        if (currentUser == null || currentUser.getRole() != User.UserRole.ADMIN) {
            System.out.println("Access denied - User is null or not admin");
            // If no user is logged in or not an admin, redirect to login screen
            SwingUtilities.invokeLater(() -> {
                UIUtils.showError(this, "Access denied. Only administrators can access this dashboard.");
                dispose();
                try {
                    new LoginScreen().setVisible(true);
                } catch (SQLException e) {
                    UIUtils.showError(this, "Error opening login screen: " + e.getMessage());
                }
            });
            return;
        }

        username = currentUser.getName();
        System.out.println("Setting up dashboard for user: " + username);

        setTitle("Event Management System - Admin Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add window listener for cleanup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Clear any sensitive data
                if (isDataLoaded) {
                    isDataLoaded = false;
                }
            }
        });

        System.out.println("Setting up UI components");
        setupUI();
        System.out.println("Setting frame visible");
        setVisible(true);
        System.out.println("AdminDashboardNew initialization complete");
    }

    /**
     * Initializes the user interface
     */
    private void setupUI() {
        // Main panel with border layout
        JPanel mainPanel = UIUtils.createPanel(new BorderLayout(), true);
        mainPanel.setBackground(AppColors.BACKGROUND_LIGHT);

        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create sidebar panel
        JPanel sidebarPanel = createSidebarPanel();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);

        // Create content panel with card layout
        mainContentPanel = UIUtils.createPanel(new CardLayout(), true);
        cardLayout = (CardLayout) mainContentPanel.getLayout();

        // Add content cards
        mainContentPanel.add(createDashboardPanel(), "Dashboard");
        mainContentPanel.add(createEventsPanel(), "Events");
        mainContentPanel.add(createUsersPanel(), "Users");
        mainContentPanel.add(createReportsPanel(), "Reports");
        mainContentPanel.add(createSettingsPanel(), "Settings");

        // Show the dashboard by default
        cardLayout.show(mainContentPanel, "Dashboard");

        mainPanel.add(mainContentPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    /**
     * Creates the header panel
     * 
     * @return the header panel
     */
    private JPanel createHeaderPanel() {
        User currentUser = SessionManager.getInstance().getCurrentUser();
        return new HeaderPanel(
            currentUser.getName(),
            "Administrator",
            true,
            v -> cardLayout.show(mainContentPanel, "Create Event")
        );
    }

    /**
     * Creates the sidebar panel
     * 
     * @return the sidebar panel
     */
    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = UIUtils.createPanel(new BorderLayout(), true);
        sidebarPanel.setBackground(AppColors.PRIMARY_DARK);
        sidebarPanel.setPreferredSize(new Dimension(250, 0));
        sidebarPanel.setBorder(new EmptyBorder(20, 10, 20, 10));

        // Add user profile section
        JPanel profilePanel = UIUtils.createPanel(new FlowLayout(FlowLayout.LEFT), false);
        User currentUser = SessionManager.getInstance().getCurrentUser();
        
        JLabel nameLabel = UIUtils.createLabel(
            currentUser.getName(),
            UIConstants.TITLE_FONT,
            Color.WHITE
        );
        profilePanel.add(nameLabel);
        sidebarPanel.add(profilePanel, BorderLayout.NORTH);

        // Add separator
        JSeparator separator = new JSeparator();
        separator.setForeground(AppColors.BORDER);
        separator.setMaximumSize(new Dimension(220, 1));
        sidebarPanel.add(separator, BorderLayout.NORTH);

        // Add navigation buttons
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBackground(AppColors.PRIMARY_DARK);
        navPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        String[][] navItems = {
            {"📊", "Dashboard"},
            {"📅", "Events"},
            {"👥", "Users"},
            {"👥", "User Management"},
            {"📈", "Reports"},
            {"⚙️", "Settings"}
        };

        for (String[] item : navItems) {
            JButton navButton = UIUtils.createButton(
                item[0] + " " + item[1],
                null,
                UIUtils.ButtonType.SECONDARY,
                UIUtils.ButtonSize.SMALL
            );
            navButton.setMaximumSize(new Dimension(220, 40));
            
            // Special handling for User Management button
            if (item[1].equals("User Management")) {
                navButton.addActionListener(e -> {
                    try {
                        new AdminUserManagementScreen().setVisible(true);
                    } catch (SQLException ex) {
                        UIUtils.showError(this, "Error opening user management: " + ex.getMessage());
                    }
                });
            } else {
            navButton.addActionListener(e -> cardLayout.show(mainContentPanel, item[1]));
            }
            
            navPanel.add(navButton);
            navPanel.add(Box.createVerticalStrut(10));
        }

        sidebarPanel.add(navPanel, BorderLayout.CENTER);

        // Add logout button at the bottom
        JButton logoutButton = UIUtils.createButton(
            "🚪 Logout",
            null,
            UIUtils.ButtonType.ERROR,
            UIUtils.ButtonSize.SMALL
        );
        logoutButton.addActionListener(e -> handleLogout());
        sidebarPanel.add(logoutButton, BorderLayout.SOUTH);

        return sidebarPanel;
    }

    /**
     * Creates the dashboard panel
     * 
     * @return the dashboard panel
     */
    private JPanel createDashboardPanel() {
        JPanel panel = UIUtils.createPanel(new BorderLayout(), true);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = UIUtils.createLabel(
            "Dashboard Overview",
            UIConstants.TITLE_FONT,
            AppColors.TEXT_PRIMARY
        );
        panel.add(titleLabel, BorderLayout.NORTH);

        // Stats panel
        JPanel statsPanel = UIUtils.createPanel(new GridLayout(2, 2, 20, 20), false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Add loading indicator
        LoadingPanel loadingPanel = new LoadingPanel("Loading dashboard data...");
        statsPanel.add(loadingPanel);

        // Load data in background
        new Thread(() -> {
            try {
                // Get total events
                List<Event> events = eventController.getAllEvents();
                int totalEvents = events.size();
                int pendingEvents = (int) events.stream()
                    .filter(e -> e.getStatus() == Event.EventStatus.PENDING)
                    .count();

                // Get total users
                List<User> users = userController.getAllUsers();
                int totalUsers = users.size();
                int activeUsers = (int) users.stream()
                    .filter(User::isActive)
                    .count();

                // Update UI on EDT
                SwingUtilities.invokeLater(() -> {
                // Clear loading panel
                statsPanel.removeAll();

                // Add stat cards with real data
                statsPanel.add(createStatCard("Total Events", String.valueOf(totalEvents), "📅"));
                statsPanel.add(createStatCard("Active Users", String.valueOf(activeUsers), "👥"));
                statsPanel.add(createStatCard("Pending Approvals", String.valueOf(pendingEvents), "⏳"));
                statsPanel.add(createStatCard("Total Revenue", "$0", "💰")); // TODO: Implement revenue tracking

                isDataLoaded = true;
                    statsPanel.revalidate();
                    statsPanel.repaint();
                });
            } catch (SQLException e) {
                SwingUtilities.invokeLater(() -> {
                UIUtils.showError(this, "Error loading dashboard data: " + e.getMessage());
                });
            }
        }).start();

        panel.add(statsPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates a stat card
     * 
     * @param title the card title
     * @param value the card value
     * @param icon the card icon
     * @return the stat card panel
     */
    private JPanel createStatCard(String title, String value, String icon) {
        JPanel card = UIUtils.createPanel(new BorderLayout(), false);
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Icon and title
        JPanel headerPanel = UIUtils.createPanel(new FlowLayout(FlowLayout.LEFT), false);
        JLabel iconLabel = UIUtils.createLabel(icon, UIConstants.TITLE_FONT, AppColors.PRIMARY);
        JLabel titleLabel = UIUtils.createLabel(title, UIConstants.BODY_FONT, AppColors.TEXT_SECONDARY);
        headerPanel.add(iconLabel);
        headerPanel.add(Box.createHorizontalStrut(10));
        headerPanel.add(titleLabel);
        card.add(headerPanel, BorderLayout.NORTH);

        // Value
        JLabel valueLabel = UIUtils.createLabel(value, UIConstants.HEADER_FONT, AppColors.TEXT_PRIMARY);
        valueLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Creates the events panel
     * 
     * @return the events panel
     */
    private JPanel createEventsPanel() {
        JPanel panel = UIUtils.createPanel(new BorderLayout(), true);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title and create button
        JPanel headerPanel = UIUtils.createPanel(new BorderLayout(), false);
        JLabel titleLabel = UIUtils.createLabel(
            "Events Management",
            UIConstants.TITLE_FONT,
            AppColors.TEXT_PRIMARY
        );
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton createButton = UIUtils.createButton(
            "📝 Create Event",
            null,
            UIUtils.ButtonType.PRIMARY,
            UIUtils.ButtonSize.NORMAL
        );
        createButton.addActionListener(e -> showCreateEventDialog());
        headerPanel.add(createButton, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        // TODO: Implement events management UI

        return panel;
    }

    /**
     * Creates the users panel
     * 
     * @return the users panel
     */
    private JPanel createUsersPanel() {
        JPanel panel = UIUtils.createPanel(new BorderLayout(), true);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title and manage button
        JPanel headerPanel = UIUtils.createPanel(new BorderLayout(), false);
        JLabel titleLabel = UIUtils.createLabel(
            "Users Management",
            UIConstants.TITLE_FONT,
            AppColors.TEXT_PRIMARY
        );
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton manageButton = UIUtils.createButton(
            "👥 Manage Users",
            null,
            UIUtils.ButtonType.PRIMARY,
            UIUtils.ButtonSize.NORMAL
        );
        manageButton.addActionListener(e -> {
            try {
                new AdminUserManagementScreen().setVisible(true);
            } catch (SQLException ex) {
                UIUtils.showError(this, "Error opening user management: " + ex.getMessage());
            }
        });
        headerPanel.add(manageButton, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        // Add some statistics
        JPanel statsPanel = UIUtils.createPanel(new GridLayout(1, 3, 20, 0), false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        try {
            List<User> users = userController.getAllUsers();
            int totalUsers = users.size();
            int activeUsers = (int) users.stream().filter(User::isActive).count();
            int adminUsers = (int) users.stream().filter(u -> u.getRole() == User.UserRole.ADMIN).count();

            statsPanel.add(createStatCard("Total Users", String.valueOf(totalUsers), "👥"));
            statsPanel.add(createStatCard("Active Users", String.valueOf(activeUsers), "✅"));
            statsPanel.add(createStatCard("Administrators", String.valueOf(adminUsers), "👑"));
        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading user statistics: " + e.getMessage());
        }

        panel.add(statsPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the reports panel
     * 
     * @return the reports panel
     */
    private JPanel createReportsPanel() {
        JPanel panel = UIUtils.createPanel(new BorderLayout(), true);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = UIUtils.createLabel(
            "Reports and Analytics",
            UIConstants.TITLE_FONT,
            AppColors.TEXT_PRIMARY
        );
        panel.add(titleLabel, BorderLayout.NORTH);

        // TODO: Implement reports and analytics UI

        return panel;
    }

    /**
     * Creates the settings panel
     * 
     * @return the settings panel
     */
    private JPanel createSettingsPanel() {
        JPanel panel = UIUtils.createPanel(new BorderLayout(), true);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = UIUtils.createLabel(
            "Settings",
            UIConstants.TITLE_FONT,
            AppColors.TEXT_PRIMARY
        );
        panel.add(titleLabel, BorderLayout.NORTH);

        // Settings form
        JPanel formPanel = UIUtils.createPanel(new GridBagLayout(), false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add settings fields
        addSettingField(formPanel, gbc, "System Name", "Event Management System");
        addSettingField(formPanel, gbc, "Version", "1.0.0");
        addSettingField(formPanel, gbc, "Database", "MySQL");
        addSettingField(formPanel, gbc, "Last Backup", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Adds a setting field to the settings panel
     * 
     * @param panel the panel to add the field to
     * @param gbc the grid bag constraints
     * @param label the field label
     * @param value the field value
     */
    private void addSettingField(JPanel panel, GridBagConstraints gbc, String label, String value) {
        // Label
        JLabel labelComponent = UIUtils.createLabel(label + ":", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY);
        gbc.gridx = 0;
        panel.add(labelComponent, gbc);

        // Value
        JLabel valueComponent = UIUtils.createLabel(value, UIConstants.BODY_FONT, AppColors.TEXT_SECONDARY);
        gbc.gridx = 1;
        panel.add(valueComponent, gbc);

        gbc.gridy++;
    }

    /**
     * Shows the create event dialog
     */
    private void showCreateEventDialog() {
        // TODO: Implement create event dialog
    }

    /**
     * Handles the logout action
     */
    private void handleLogout() {
        // Clear session
        SessionManager.getInstance().endSession();

        // Show login screen
        dispose();
        try {
            new LoginScreen().setVisible(true);
        } catch (SQLException e) {
            UIUtils.showError(this, "Error opening login screen: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            try {
                new AdminDashboardNew().setVisible(true);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Error initializing admin dashboard: " + e.getMessage(),
                    "Initialization Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

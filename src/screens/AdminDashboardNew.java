package screens;

import controllers.AuthController;
import controllers.RegistrationController;
import controllers.EventController;
import controllers.UserController;
import controllers.SessionManager;
import models.User;
import models.Event;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import utils.*;
import components.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.stream.Collectors;
import java.awt.geom.RoundRectangle2D;
import java.awt.RenderingHints;
import java.util.Arrays;
import javax.swing.Box;
import controllers.NotificationController;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.ArrayList;
import javax.swing.SwingWorker;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.Desktop;

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
    private AuthController authController = AuthController.getInstance();
    private boolean isDataLoaded = false;

    private JTable pendingEventsTable;
    private DefaultTableModel pendingEventsTableModel;

    // Add dashboardStatsPanel as a class-level variable
    private JPanel dashboardStatsPanel;

    // Store references to dynamic panels for updating
    private JPanel statsPanelRef;
    private JPanel pendingListRef;
    private JPanel activityListRef;

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
        JPanel mainPanel = UIUtils.createPanel(new BorderLayout(), true);
        mainPanel.setBackground(AppColors.BACKGROUND_LIGHT);

        mainContentPanel = UIUtils.createPanel(new CardLayout(), true);
        cardLayout = (CardLayout) mainContentPanel.getLayout();

        // Use the shared header and sidebar
        HeaderPanel headerPanel = new HeaderPanel(username, "Admin");

        SidebarPanel sidebarPanel = new SidebarPanel(cardLayout, mainContentPanel, username, "Admin");

        // Add navigation buttons with emoji icons
        sidebarPanel.addNavButton("🏠 Dashboard", null, "Dashboard", true);
        sidebarPanel.addNavButton("⏳ Pending Approvals", null, "Pending Approvals", false);
        sidebarPanel.addNavButton("📅 All Events", null, "All Events", false);
        sidebarPanel.addNavButton("➕ Create Event", null, "Create Event", false);
        sidebarPanel.addNavButton("👥 Registered Users", null, "Registered Users", false);

        // Add refresh actions to buttons
        sidebarPanel.addNavButtonActionListener("Dashboard", e -> loadDashboardData());
        sidebarPanel.addNavButtonActionListener("Pending Approvals", e -> refreshPendingApprovals());
        sidebarPanel.addNavButtonActionListener("All Events", e -> refreshEventsPanel());
        sidebarPanel.addNavButtonActionListener("Registered Users", e -> refreshUsersPanel());

        sidebarPanel.addSectionLabel("SYSTEM");
        sidebarPanel.addNavButton("⚙️ System Settings", null, "System Settings", false);

        // Add profile and logout in ACCOUNT section
        sidebarPanel.addSectionLabel("ACCOUNT");
        sidebarPanel.addNavButton("👤 My Profile", null, "My Profile", false);
        sidebarPanel.addLogoutButton(e -> handleLogout());

        // Add content cards
        mainContentPanel.add(createDashboardPanel(), "Dashboard");
        mainContentPanel.add(createEventsPanel(), "All Events");
        mainContentPanel.add(createUsersPanel(), "Registered Users");
        mainContentPanel.add(createSettingsPanel(), "System Settings");
        mainContentPanel.add(createPendingApprovalsPanel(), "Pending Approvals");
        mainContentPanel.add(new CreateEventForm("Admin", success -> {
            if (success) {
                cardLayout.show(mainContentPanel, "Dashboard");
                loadDashboardData(); // Refresh dashboard after event creation
            }
        }), "Create Event");
        mainContentPanel.add(createProfilePanel(), "My Profile");

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(mainContentPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);

        cardLayout.show(mainContentPanel, "Dashboard");
        loadDashboardData(); // Initial load
    }

    /**
     * Gets the name of the currently visible card
     */
    private String getCurrentCard() {
        for (Component comp : mainContentPanel.getComponents()) {
            if (comp.isVisible()) {
                return comp.getName();
            }
        }
        return null;
    }

    /**
     * Creates the dashboard panel
     *
     * @return the dashboard panel
     */
    private JPanel createDashboardPanel() {
        JPanel panel = UIUtils.createPanel(new BorderLayout(20, 20), true);
        panel.setBackground(AppColors.BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header section (Greeting and Date)
        JPanel headerSection = UIUtils.createPanel(new BorderLayout(), false);
        headerSection.setOpaque(false);

        // Greeting
        User currentUser = SessionManager.getInstance().getCurrentUser();
        JLabel greetingLabel = UIUtils.createLabel(
            "Welcome back, " + (currentUser != null ? currentUser.getName() : "Guest") + "!",
            UIConstants.HEADER_FONT,
            AppColors.TEXT_PRIMARY
        );
        headerSection.add(greetingLabel, BorderLayout.WEST);

        // Date
        JLabel dateLabel = UIUtils.createLabel(
            DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy").format(ZonedDateTime.now()),
            UIConstants.BODY_FONT,
            AppColors.TEXT_SECONDARY
        );
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        headerSection.add(dateLabel, BorderLayout.EAST);

        panel.add(headerSection, BorderLayout.NORTH);

        // Main content area
        JPanel mainContent = UIUtils.createPanel(new BorderLayout(20, 20), false);
        mainContent.setOpaque(false);

        // --- Stats Panel ---
        statsPanelRef = UIUtils.createPanel(new GridLayout(1, 5, 20, 0), false);
        statsPanelRef.setOpaque(false);
        mainContent.add(statsPanelRef, BorderLayout.NORTH);

        // --- Bottom Section: Pending Approvals & Recent Activity ---
        JPanel bottomSection = UIUtils.createPanel(new GridLayout(1, 2, 20, 0), false);
        bottomSection.setOpaque(false);

        // --- Pending Approvals ---
        RoundedPanel pendingPanel = new RoundedPanel(new BorderLayout(), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        pendingPanel.setBorder(UIUtils.createRoundedBorderWithPadding(AppColors.BORDER, UIConstants.CORNER_RADIUS_MEDIUM, 1, 0));

        JLabel pendingTitle = UIUtils.createLabel("PENDING APPROVALS", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY);
        pendingTitle.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        pendingPanel.add(pendingTitle, BorderLayout.NORTH);

        pendingListRef = new JPanel();
        pendingListRef.setLayout(new BoxLayout(pendingListRef, BoxLayout.Y_AXIS));
        pendingListRef.setOpaque(false);
        JScrollPane pendingScrollPane = new JScrollPane(pendingListRef);
        pendingScrollPane.setBorder(BorderFactory.createEmptyBorder());
        pendingScrollPane.setOpaque(false);
        pendingScrollPane.getViewport().setOpaque(false);
        pendingPanel.add(pendingScrollPane, BorderLayout.CENTER);

        JButton viewAllPendingBtn = UIUtils.createButton("VIEW ALL PENDING", null, UIUtils.ButtonType.PRIMARY, UIUtils.ButtonSize.NORMAL);
        viewAllPendingBtn.addActionListener(e -> cardLayout.show(mainContentPanel, "Pending Approvals"));
        JPanel pendingBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pendingBtnPanel.setOpaque(false);
        pendingBtnPanel.add(viewAllPendingBtn);
        pendingBtnPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        pendingPanel.add(pendingBtnPanel, BorderLayout.SOUTH);

        // --- Recent Activity ---
        RoundedPanel activityPanel = new RoundedPanel(new BorderLayout(), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        activityPanel.setBorder(UIUtils.createRoundedBorderWithPadding(AppColors.BORDER, UIConstants.CORNER_RADIUS_MEDIUM, 1, 0));

        JLabel activityTitle = UIUtils.createLabel("RECENT ACTIVITY", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY);
        activityTitle.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        activityPanel.add(activityTitle, BorderLayout.NORTH);

        activityListRef = new JPanel();
        activityListRef.setLayout(new BoxLayout(activityListRef, BoxLayout.Y_AXIS));
        activityListRef.setOpaque(false);
        JScrollPane activityScrollPane = new JScrollPane(activityListRef);
        activityScrollPane.setBorder(BorderFactory.createEmptyBorder());
        activityScrollPane.setOpaque(false);
        activityScrollPane.getViewport().setOpaque(false);
        activityPanel.add(activityScrollPane, BorderLayout.CENTER);

        JButton viewAllActivityBtn = UIUtils.createButton("VIEW ALL ACTIVITY", null, UIUtils.ButtonType.PRIMARY, UIUtils.ButtonSize.NORMAL);
        viewAllActivityBtn.addActionListener(e -> cardLayout.show(mainContentPanel, "Activity Log"));
        JPanel activityBtnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        activityBtnPanel.setOpaque(false);
        activityBtnPanel.add(viewAllActivityBtn);
        activityBtnPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        activityPanel.add(activityBtnPanel, BorderLayout.SOUTH);

        // Add both panels to bottom section
        bottomSection.add(pendingPanel);
        bottomSection.add(activityPanel);
        mainContent.add(bottomSection, BorderLayout.CENTER);

        panel.add(mainContent, BorderLayout.CENTER);

        // Load data initially
        loadDashboardData();

        return panel;
    }

    /**
     * Creates the events panel with enhanced details
     */
    private JPanel createEventsPanel() {
        JPanel panel = UIUtils.createPanel(new BorderLayout(20, 20), true);
        panel.setBackground(AppColors.BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header section
        JPanel headerSection = UIUtils.createPanel(new BorderLayout(), false);
        headerSection.setOpaque(false);

        JLabel titleLabel = UIUtils.createLabel(
            "All Events",
            UIConstants.HEADER_FONT,
            AppColors.TEXT_PRIMARY
        );
        headerSection.add(titleLabel, BorderLayout.WEST);

        JButton createButton = UIUtils.createButton(
            "Create Event",
            null,
            UIUtils.ButtonType.PRIMARY,
            UIUtils.ButtonSize.NORMAL
        );
        createButton.addActionListener(e -> cardLayout.show(mainContentPanel, "Create Event"));
        headerSection.add(createButton, BorderLayout.EAST);

        panel.add(headerSection, BorderLayout.NORTH);

        // Stats Panel
        JPanel statsPanel = UIUtils.createPanel(new GridLayout(1, 4, 20, 0), false);
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        try {
            List<Event> events = eventController.getAllEvents();
            int totalEvents = events.size();
            int activeEvents = (int) events.stream().filter(e -> e.getStatus() == Event.EventStatus.APPROVED).count();
            int totalRegistrations = events.stream().mapToInt(e -> e.getTotalSlots() - e.getAvailableSlots()).sum();
            double avgRegistrationsPerEvent = totalEvents > 0 ? (double) totalRegistrations / totalEvents : 0;

            statsPanel.add(createStatCard("TOTAL EVENTS", String.valueOf(totalEvents), AppColors.PRIMARY));
            statsPanel.add(createStatCard("ACTIVE EVENTS", String.valueOf(activeEvents), AppColors.SUCCESS));
            statsPanel.add(createStatCard("TOTAL REGISTRATIONS", String.valueOf(totalRegistrations), AppColors.SECONDARY));
            statsPanel.add(createStatCard("AVG REGISTRATIONS", String.format("%.1f", avgRegistrationsPerEvent), AppColors.ACCENT));
        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading event statistics: " + e.getMessage());
        }

        panel.add(statsPanel, BorderLayout.NORTH);

        // Main content - Events Table
        RoundedPanel contentPanel = new RoundedPanel(new BorderLayout(), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        contentPanel.setBorder(UIUtils.createRoundedBorder(AppColors.BORDER, UIConstants.CORNER_RADIUS_MEDIUM, 1));

        // Create table model
        String[] columns = {"ID", "Title", "Date", "Venue", "Organizer", "Status", "Registered", "Available", "Actions"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8; // Only actions column is editable
            }
        };

        // Create table
        JTable eventsTable = new JTable(tableModel);
        eventsTable.setRowHeight(40);
        eventsTable.setFont(UIConstants.BODY_FONT);
        eventsTable.getTableHeader().setFont(UIConstants.SMALL_FONT_BOLD);
        eventsTable.getTableHeader().setBackground(AppColors.BACKGROUND_LIGHT);
        eventsTable.setShowGrid(false);
        eventsTable.setIntercellSpacing(new Dimension(0, 0));

        // Set column widths
        eventsTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        eventsTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Title
        eventsTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Date
        eventsTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Venue
        eventsTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Organizer
        eventsTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Status
        eventsTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Registered
        eventsTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Available
        eventsTable.getColumnModel().getColumn(8).setPreferredWidth(100); // Actions

        // Status column renderer
        eventsTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setForeground(getStatusColor((String) value));
                return label;
            }
        });

        // Actions column renderer and editor
        eventsTable.getColumnModel().getColumn(8).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JButton button = UIUtils.createButton("View Details", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.SMALL);
                button.setHorizontalAlignment(SwingConstants.CENTER);
                return button;
            }
        });

        eventsTable.getColumnModel().getColumn(8).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private JButton button;
            {
                button = UIUtils.createButton("View Details", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.SMALL);
                button.setHorizontalAlignment(SwingConstants.CENTER);
                button.addActionListener(e -> {
                    int row = eventsTable.getSelectedRow();
                    if (row >= 0) {
                        try {
                            int eventId = (int) eventsTable.getValueAt(row, 0);
                            Event event = eventController.getEvent(eventId);
                            showEventDetails(event);
                        } catch (SQLException ex) {
                            UIUtils.showError(panel, "Error loading event details: " + ex.getMessage());
                        }
                    }
                    fireEditingStopped();
                });
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value,
                    boolean isSelected, int row, int column) {
                return button;
            }

            @Override
            public Object getCellEditorValue() {
                return "View Details";
            }
        });

        // Load events data
        try {
            List<Event> events = eventController.getAllEvents();
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");

            for (Event event : events) {
                Object[] rowData = {
                    event.getId(),
                    event.getTitle(),
                    event.getEventDate() != null ? event.getEventDate().format(dateFormat) : "N/A",
                    event.getVenueName(),
                    event.getOrganizer() != null ? event.getOrganizer().getName() : "Admin",
                    event.getStatus().toString(),
                    event.getTotalSlots() - event.getAvailableSlots(),
                    event.getAvailableSlots(),
                    "View Details"
                };
                tableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading events: " + e.getMessage());
        }

        // Add search and filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setOpaque(false);

        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setToolTipText("Search by event title");

        JButton searchButton = UIUtils.createButton("Search", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.SMALL);

        JComboBox<String> statusFilter = UIUtils.createRoundedComboBox(new String[]{"All", "APPROVED", "PENDING", "COMPLETED", "CANCELLED", "REJECTED"});
        statusFilter.setPreferredSize(new Dimension(150, 30));

        // Add action listeners for search and filter
        searchButton.addActionListener(e -> {
            String searchText = searchField.getText().toLowerCase();
            String selectedStatus = (String) statusFilter.getSelectedItem();

            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>((DefaultTableModel) eventsTable.getModel());
            eventsTable.setRowSorter(sorter);

            RowFilter<DefaultTableModel, Object> textFilter = RowFilter.regexFilter("(?i)" + searchText, 1); // Search in title column

            if (selectedStatus.equals("All")) {
                sorter.setRowFilter(textFilter);
            } else {
                RowFilter<DefaultTableModel, Object> statusRowFilter = RowFilter.regexFilter("^" + selectedStatus + "$", 5); // Status column
                sorter.setRowFilter(RowFilter.andFilter(List.of(textFilter, statusRowFilter)));
            }
        });

        // Add change listener for status filter
        statusFilter.addActionListener(e -> {
            String searchText = searchField.getText().toLowerCase();
            String selectedStatus = (String) statusFilter.getSelectedItem();

            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>((DefaultTableModel) eventsTable.getModel());
            eventsTable.setRowSorter(sorter);

            if (searchText.isEmpty() && selectedStatus.equals("All")) {
                sorter.setRowFilter(null);
            } else if (selectedStatus.equals("All")) {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText, 1)); // Search in title column
            } else if (searchText.isEmpty()) {
                sorter.setRowFilter(RowFilter.regexFilter("^" + selectedStatus + "$", 5)); // Status column
            } else {
                RowFilter<DefaultTableModel, Object> textFilter = RowFilter.regexFilter("(?i)" + searchText, 1);
                RowFilter<DefaultTableModel, Object> statusRowFilter = RowFilter.regexFilter("^" + selectedStatus + "$", 5);
                sorter.setRowFilter(RowFilter.andFilter(List.of(textFilter, statusRowFilter)));
            }
        });

        // Add organizer filter
        JComboBox<String> organizerFilter = UIUtils.createRoundedComboBox(new String[]{"All Organizers"});
        organizerFilter.setPreferredSize(new Dimension(180, 30));

        // Populate organizer filter with unique organizer names
        try {
            List<Event> events = eventController.getAllEvents();
            Set<String> organizers = new HashSet<>();

            for (Event event : events) {
                if (event.getOrganizer() != null) {
                    organizers.add(event.getOrganizer().getName());
                }
            }

            for (String organizer : organizers) {
                organizerFilter.addItem(organizer);
            }

            // Add action listener for organizer filter
            organizerFilter.addActionListener(e -> {
                String searchText = searchField.getText().toLowerCase();
                String selectedStatus = (String) statusFilter.getSelectedItem();
                String selectedOrganizer = (String) organizerFilter.getSelectedItem();

                TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>((DefaultTableModel) eventsTable.getModel());
                eventsTable.setRowSorter(sorter);

                List<RowFilter<DefaultTableModel, Object>> filters = new ArrayList<>();

                // Add text filter if search text is not empty
                if (!searchText.isEmpty()) {
                    filters.add(RowFilter.regexFilter("(?i)" + searchText, 1)); // Title column
                }

                // Add status filter if not "All"
                if (!selectedStatus.equals("All")) {
                    filters.add(RowFilter.regexFilter("^" + selectedStatus + "$", 5)); // Status column
                }

                // Add organizer filter if not "All Organizers"
                if (!selectedOrganizer.equals("All Organizers")) {
                    filters.add(RowFilter.regexFilter("^" + selectedOrganizer + "$", 4)); // Organizer column
                }

                if (filters.isEmpty()) {
                    sorter.setRowFilter(null);
                } else if (filters.size() == 1) {
                    sorter.setRowFilter(filters.get(0));
                } else {
                    sorter.setRowFilter(RowFilter.andFilter(filters));
                }
            });

        } catch (SQLException ex) {
            UIUtils.showError(this, "Error loading organizers: " + ex.getMessage());
        }

        // Add components to filter panel
        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilter);
        filterPanel.add(new JLabel("Organizer:"));
        filterPanel.add(organizerFilter);

        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(eventsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        // Add components to content panel
        contentPanel.add(filterPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    private Color getStatusColor(String status) {
        switch (status) {
            case "APPROVED":
                return AppColors.SUCCESS;
            case "PENDING":
                return AppColors.WARNING;
            case "REJECTED":
                return AppColors.ERROR;
            case "CANCELLED":
                return AppColors.TEXT_SECONDARY;
            default:
                return AppColors.TEXT_PRIMARY;
        }
    }

    public void showEventDetails(Event event) {
        JDialog dialog = new JDialog(this, "Event Details", true);
        dialog.setSize(1000, 800);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        EventDetailsPanel detailsPanel = new EventDetailsPanel(
            event,
            e -> {
                dialog.dispose();
                showEditEventDialog(event);
            },
            event.getStatus() == Event.EventStatus.PENDING ? e -> { handleEventApproval(event); dialog.dispose(); } : null,
            event.getStatus() == Event.EventStatus.PENDING ? e -> { handleEventRejection(event); dialog.dispose(); } : null,
            e -> dialog.dispose()
        );
        dialog.add(detailsPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    /**
     * Creates a section panel with a title
     */
    private RoundedPanel createSectionPanel(String title) {
        RoundedPanel panel = new RoundedPanel(new BorderLayout(), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        panel.setBorder(UIUtils.createRoundedBorderWithPadding(AppColors.BORDER, UIConstants.CORNER_RADIUS_MEDIUM, 1, 15));

        JLabel titleLabel = UIUtils.createLabel(title, UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        panel.add(titleLabel, BorderLayout.NORTH);

        return panel;
    }

    private void showUserProfile(User user) {
        // Create and show user profile dialog
        JDialog dialog = new JDialog(this, "User Profile", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Create content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Add user details
        addDetailField(contentPanel, gbc, "Name:", user.getName());
        addDetailField(contentPanel, gbc, "Email:", user.getEmail());
        addDetailField(contentPanel, gbc, "Role:", user.getRole().toString());
        addDetailField(contentPanel, gbc, "Status:", user.isActive() ? "Active" : "Inactive");
        addDetailField(contentPanel, gbc, "Registration Date:", user.getRegistrationDate());
        if (user.getLastLoginAt() != null) {
            addDetailField(contentPanel, gbc, "Last Login:",
                new SimpleDateFormat("MMM d, yyyy h:mm a").format(user.getLastLoginAt()));
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        dialog.add(scrollPane, BorderLayout.CENTER);

        // Add action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton editButton = UIUtils.createButton("Edit", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.NORMAL);
        editButton.addActionListener(e -> {
            dialog.dispose();
            showEditUserDialog(user);
        });
        buttonPanel.add(editButton);

        JButton closeButton = UIUtils.createButton("Close", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.NORMAL);
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);

        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showEditEventDialog(Event event) {
        // TODO: Implement event editing dialog
        // This will be similar to CreateEventForm but pre-populated with event data
    }

    private void showEditUserDialog(User user) {
        // TODO: Implement user editing dialog
        // This will be similar to UserEditDialog but pre-populated with user data
    }

    /**
     * Creates the users panel
     *
     * @return the users panel
     */
    private JPanel createUsersPanel() {
        JPanel panel = UIUtils.createPanel(new BorderLayout(), true);
        panel.setBackground(AppColors.BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Stats section
        JPanel statsPanel = UIUtils.createPanel(new GridLayout(1, 3, 20, 0), false);
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        try {
            List<User> users = userController.getAllUsers();
            int totalUsers = users.size();
            int activeUsers = (int) users.stream().filter(User::isActive).count();
            int adminUsers = (int) users.stream().filter(u -> u.getRole() == User.UserRole.ADMIN).count();

            statsPanel.add(createStatCard("TOTAL USERS", String.valueOf(totalUsers), AppColors.PRIMARY));
            statsPanel.add(createStatCard("ACTIVE USERS", String.valueOf(activeUsers), AppColors.SECONDARY));
            statsPanel.add(createStatCard("ADMINISTRATORS", String.valueOf(adminUsers), AppColors.ACCENT));
        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading user statistics: " + e.getMessage());
        }

        panel.add(statsPanel, BorderLayout.NORTH);

        // Add the RegisteredUsersView component
        RegisteredUsersView usersView = new RegisteredUsersView();
        panel.add(usersView, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the settings panel
     *
     * @return the settings panel
     */
    private JPanel createSettingsPanel() {
        JPanel panel = UIUtils.createPanel(new BorderLayout(), true);
        panel.setBackground(AppColors.BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header section
        JPanel headerSection = UIUtils.createPanel(new BorderLayout(), false);
        headerSection.setOpaque(false);

        JLabel titleLabel = UIUtils.createLabel(
            "System Settings",
            UIConstants.HEADER_FONT,
            AppColors.TEXT_PRIMARY
        );
        headerSection.add(titleLabel, BorderLayout.WEST);

        JButton saveButton = UIUtils.createButton(
            "💾 Save Settings",
            null,
            UIUtils.ButtonType.PRIMARY,
            UIUtils.ButtonSize.NORMAL
        );
        headerSection.add(saveButton, BorderLayout.EAST);

        panel.add(headerSection, BorderLayout.NORTH);

        // Main content
        JPanel mainContent = UIUtils.createPanel(new BorderLayout(0, 20), false);
        mainContent.setOpaque(false);

        // Settings card
        RoundedPanel settingsCard = new RoundedPanel(new BorderLayout(), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        settingsCard.setBorder(UIUtils.createRoundedBorderWithPadding(AppColors.BORDER, UIConstants.CORNER_RADIUS_MEDIUM, 1, 15));

        JLabel settingsTitle = UIUtils.createLabel("SYSTEM CONFIGURATION", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY);
        settingsCard.add(settingsTitle, BorderLayout.NORTH);

        // Settings form
        JPanel formPanel = UIUtils.createPanel(new GridBagLayout(), false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Add settings fields
        addSettingField(formPanel, gbc, "System Name", "Event Management System");
        addSettingField(formPanel, gbc, "Version", "1.0.0");
        addSettingField(formPanel, gbc, "Database", "MySQL");
        addSettingField(formPanel, gbc, "Last Backup", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        addSettingField(formPanel, gbc, "Server Status", "Online");
        addSettingField(formPanel, gbc, "API Status", "Active");

        settingsCard.add(formPanel, BorderLayout.CENTER);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);
        JButton backupBtn = UIUtils.createButton("Backup Database", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.NORMAL);
        JButton resetBtn = UIUtils.createButton("Reset Settings", null, UIUtils.ButtonType.ERROR, UIUtils.ButtonSize.NORMAL);
        actionPanel.add(backupBtn);
        actionPanel.add(resetBtn);
        settingsCard.add(actionPanel, BorderLayout.SOUTH);

        mainContent.add(settingsCard, BorderLayout.NORTH);

        panel.add(mainContent, BorderLayout.CENTER);

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
        JLabel labelComponent = UIUtils.createLabel(label, UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY);
        gbc.gridx = 0;
        panel.add(labelComponent, gbc);

        JLabel valueComponent = UIUtils.createLabel(value != null ? value : "N/A", UIConstants.BODY_FONT, AppColors.TEXT_SECONDARY);
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

    // Helper class for rendering buttons in a JTable cell
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "" : value.toString());
            setForeground(AppColors.PRIMARY);
            return this;
        }
    }

    // Helper class for editing buttons in a JTable cell
    private class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int row;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            isPushed = true;
            this.row = row;
            button.setText(label);
            button.setForeground(AppColors.PRIMARY);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Handle button click - open review dialog/screen
                try {
                    DefaultTableModel model = (DefaultTableModel) pendingEventsTable.getModel();
                    String eventTitle = (String) model.getValueAt(row, 0);
                    // Find the event by title (or ideally, by ID if added to table model)
                    List<Event> pendingEvents = eventController.getEventsByStatus(Event.EventStatus.PENDING);
                    Event eventToReview = pendingEvents.stream()
                        .filter(event -> event.getTitle().equals(eventTitle))
                        .findFirst()
                        .orElse(null);

                    if (eventToReview != null) {
                        // TODO: Open a dialog or screen to review the event
                         JOptionPane.showMessageDialog(null, "Reviewing Event: " + eventToReview.getTitle());
                         // Example: new ReviewEventDialog(eventToReview).setVisible(true);
                    } else {
                        UIUtils.showError(null, "Error: Event not found for review.");
                    }
                } catch (SQLException e) {
                    UIUtils.showError(null, "Error retrieving event for review: " + e.getMessage());
                }
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    /**
     * Loads dashboard data and updates the UI.
     */
    public void loadDashboardData() {
        // Clear previous data
        statsPanelRef.removeAll();
        pendingListRef.removeAll();
        activityListRef.removeAll();

        try {
            // Load stats
            List<User> users = userController.getAllUsers();
            List<Event> events = eventController.getAllEvents();
            List<Event> pendingEvents = eventController.getEventsByStatus(Event.EventStatus.PENDING);
            List<Event> approvedEvents = eventController.getEventsByStatus(Event.EventStatus.APPROVED);

            // Add stat cards
            statsPanelRef.add(createStatCard("TOTAL USERS", String.valueOf(users.size()), AppColors.PRIMARY));
            statsPanelRef.add(createStatCard("TOTAL EVENTS", String.valueOf(events.size()), AppColors.SECONDARY));
            statsPanelRef.add(createStatCard("PENDING EVENTS", String.valueOf(pendingEvents.size()), AppColors.WARNING));
            statsPanelRef.add(createStatCard("APPROVED EVENTS", String.valueOf(approvedEvents.size()), AppColors.SUCCESS));

            // Pending approval events
            if (pendingEvents.isEmpty()) {
                JLabel emptyLabel = UIUtils.createLabel("No pending events", UIConstants.BODY_FONT, AppColors.TEXT_SECONDARY);
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                emptyLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
                pendingListRef.add(emptyLabel);
            } else {
                for (Event event : pendingEvents.subList(0, Math.min(5, pendingEvents.size()))) {
                    PendingEventItem item = new PendingEventItem(
                        event,
                        e -> handleEventApproval(event),
                        e -> handleEventRejection(event)
                    );
                    pendingListRef.add(item);
                    pendingListRef.add(Box.createVerticalStrut(10));
                }
            }

            // Recent activity
            List<String[]> activities = NotificationController.getInstance().getRecentAdminNotifications(5);
            if (activities.isEmpty()) {
                JLabel emptyLabel = UIUtils.createLabel("No recent activities", UIConstants.BODY_FONT, AppColors.TEXT_SECONDARY);
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                emptyLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
                activityListRef.add(emptyLabel);
            } else {
                for (String[] activity : activities) {
                    ActivityItem item = new ActivityItem(activity[0], activity[1], activity[2]);
                    activityListRef.add(item);
                    activityListRef.add(Box.createVerticalStrut(10));
                }
            }

            // Refresh the UI
            statsPanelRef.revalidate();
            statsPanelRef.repaint();
            pendingListRef.revalidate();
            pendingListRef.repaint();
            activityListRef.revalidate();
            activityListRef.repaint();

            isDataLoaded = true;

        } catch (SQLException e) {
                UIUtils.showError(this, "Error loading dashboard data: " + e.getMessage());
        }
    }

    /**
     * Updates the event status and refreshes all relevant panels
     */
    private void handleEventApproval(Event event) {
        try {
            event.setStatus(Event.EventStatus.APPROVED);
            eventController.updateEvent(event);

            // Refresh all affected panels
            loadDashboardData();
            refreshPendingApprovals();
            refreshEventsPanel();

            UIUtils.showSuccess(this, "Event approved: " + event.getTitle());
        } catch (SQLException ex) {
            UIUtils.showError(this, "Error approving event: " + ex.getMessage());
        }
    }

    /**
     * Updates the event status and refreshes all relevant panels
     */
    private void handleEventRejection(Event event) {
        try {
            event.setStatus(Event.EventStatus.REJECTED);
            eventController.updateEvent(event);

            // Refresh all affected panels
            loadDashboardData();
            refreshPendingApprovals();
            refreshEventsPanel();

            UIUtils.showSuccess(this, "Event rejected: " + event.getTitle());
        } catch (SQLException ex) {
            UIUtils.showError(this, "Error rejecting event: " + ex.getMessage());
        }
    }

    /**
     * Refreshes the pending approvals panel
     */
    private void refreshPendingApprovals() {
        try {
            // Remove existing content
            Component[] components = mainContentPanel.getComponents();
            for (Component comp : components) {
                if (comp.getName() != null && comp.getName().equals("Pending Approvals")) {
                    mainContentPanel.remove(comp);
                    break;
                }
            }

            // Add updated panel
            JPanel newPanel = createPendingApprovalsPanel();
            newPanel.setName("Pending Approvals");
            mainContentPanel.add(newPanel, "Pending Approvals");
            cardLayout.show(mainContentPanel, "Pending Approvals");

            // Refresh UI
            mainContentPanel.revalidate();
            mainContentPanel.repaint();
        } catch (Exception e) {
            UIUtils.showError(this, "Error refreshing pending approvals: " + e.getMessage());
        }
    }

    /**
     * Refreshes the events panel
     */
    private void refreshEventsPanel() {
        try {
            // Remove existing content
            Component[] components = mainContentPanel.getComponents();
            for (Component comp : components) {
                if (comp.getName() != null && comp.getName().equals("All Events")) {
                    mainContentPanel.remove(comp);
                    break;
                }
            }

            // Add updated panel
            JPanel newPanel = createEventsPanel();
            newPanel.setName("All Events");
            mainContentPanel.add(newPanel, "All Events");
            cardLayout.show(mainContentPanel, "All Events");

            // Refresh UI
            mainContentPanel.revalidate();
            mainContentPanel.repaint();
        } catch (Exception e) {
            UIUtils.showError(this, "Error refreshing events panel: " + e.getMessage());
        }
    }

    /**
     * Refreshes the users panel
     */
    private void refreshUsersPanel() {
        try {
            // Remove existing content
            Component[] components = mainContentPanel.getComponents();
            for (Component comp : components) {
                if (comp.getName() != null && comp.getName().equals("Registered Users")) {
                    mainContentPanel.remove(comp);
                    break;
                }
            }

            // Add updated panel
            JPanel newPanel = createUsersPanel();
            newPanel.setName("Registered Users");
            mainContentPanel.add(newPanel, "Registered Users");
            cardLayout.show(mainContentPanel, "Registered Users");

            // Refresh UI
            mainContentPanel.revalidate();
            mainContentPanel.repaint();
        } catch (Exception e) {
            UIUtils.showError(this, "Error refreshing users panel: " + e.getMessage());
        }
    }

    private JPanel createPendingApprovalsPanel() {
        JPanel panel = UIUtils.createPanel(new BorderLayout(20, 20), true);
        panel.setBackground(AppColors.BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header section
        JPanel headerSection = UIUtils.createPanel(new BorderLayout(), false);
        headerSection.setOpaque(false);

        JLabel titleLabel = UIUtils.createLabel(
            "Pending Approvals",
            UIConstants.HEADER_FONT,
            AppColors.TEXT_PRIMARY
        );
        headerSection.add(titleLabel, BorderLayout.WEST);

        panel.add(headerSection, BorderLayout.NORTH);

        // Main content
        RoundedPanel contentPanel = new RoundedPanel(new BorderLayout(), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        contentPanel.setBorder(UIUtils.createRoundedBorderWithPadding(AppColors.BORDER, UIConstants.CORNER_RADIUS_MEDIUM, 1, 15));

        // Create scrollable list panel
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        try {
            List<Event> pendingEvents = eventController.getEventsByStatus(Event.EventStatus.PENDING);
            if (pendingEvents.isEmpty()) {
                JLabel emptyLabel = UIUtils.createLabel(
                    "No pending events to approve",
                    UIConstants.BODY_FONT,
                    AppColors.TEXT_SECONDARY
                );
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                emptyLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
                listPanel.add(emptyLabel);
            } else {
                for (Event event : pendingEvents) {
                    PendingEventItem item = new PendingEventItem(
                        event,
                        e -> handleEventApproval(event),
                        e -> handleEventRejection(event)
                    );
                    listPanel.add(item);
                    listPanel.add(Box.createVerticalStrut(10));
                }
            }
        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading pending events: " + e.getMessage());
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createActivityLogPanel() {
        JPanel panel = UIUtils.createPanel(new BorderLayout(20, 20), true);
        panel.setBackground(AppColors.BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header section
        JPanel headerSection = UIUtils.createPanel(new BorderLayout(), false);
        headerSection.setOpaque(false);

        JLabel titleLabel = UIUtils.createLabel(
            "Activity Log",
            UIConstants.HEADER_FONT,
            AppColors.TEXT_PRIMARY
        );
        headerSection.add(titleLabel, BorderLayout.WEST);

        panel.add(headerSection, BorderLayout.NORTH);

        // Main content
        RoundedPanel contentPanel = new RoundedPanel(new BorderLayout(), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        contentPanel.setBorder(UIUtils.createRoundedBorderWithPadding(AppColors.BORDER, UIConstants.CORNER_RADIUS_MEDIUM, 1, 15));

        // Create scrollable list panel
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        try {
            List<String[]> activities = NotificationController.getInstance().getRecentAdminNotifications(50); // Get more activities for the full log
            if (activities.isEmpty()) {
                JLabel emptyLabel = UIUtils.createLabel(
                    "No activities to display",
                    UIConstants.BODY_FONT,
                    AppColors.TEXT_SECONDARY
                );
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                emptyLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
                listPanel.add(emptyLabel);
            } else {
                for (String[] activity : activities) {
                    ActivityItem item = new ActivityItem(activity[0], activity[1], activity[2]);
                    listPanel.add(item);
                    listPanel.add(Box.createVerticalStrut(10));
                }
            }
        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading activity log: " + e.getMessage());
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }

    // Stat card for user stats and dashboard stats
    private JPanel createStatCard(String title, String value, Color valueColor) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(0, 5), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        card.setBorder(UIUtils.createRoundedBorderWithPadding(
            AppColors.BORDER,
            UIConstants.CORNER_RADIUS_MEDIUM,
            1,
            15
        ));

        JLabel titleLabel = UIUtils.createLabel(
            title,
            UIConstants.SMALL_FONT,
            AppColors.TEXT_SECONDARY
        );
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(titleLabel, BorderLayout.NORTH);

        JLabel valueLabel = UIUtils.createLabel(
            value,
            UIConstants.DASHBOARD_NUMBER_FONT,
            valueColor
        );
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    /**
     * Shows the profile dialog for the current user
     */
    private void showProfileDialog() {
        try {
            User currentUser = SessionManager.getInstance().getCurrentUser();
            if (currentUser != null) {
                // Create a dialog
                JDialog dialog = new JDialog(this, "My Profile", true);
                dialog.setLayout(new BorderLayout(10, 10));
                dialog.setSize(500, 700);
                dialog.setLocationRelativeTo(this);

                // Create tabbed pane
                JTabbedPane tabbedPane = new JTabbedPane();
                tabbedPane.setFont(UIConstants.BODY_FONT);

                // Basic Info Tab
                JPanel basicInfoPanel = new JPanel(new GridBagLayout());
                basicInfoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                GridBagConstraints gbc = new GridBagConstraints();
                gbc.gridwidth = GridBagConstraints.REMAINDER;
                gbc.anchor = GridBagConstraints.WEST;
                gbc.fill = GridBagConstraints.HORIZONTAL;
                gbc.insets = new Insets(5, 5, 5, 5);

                // User ID (Read-only)
                JTextField idField = UIUtils.createRoundedTextField();
                idField.setText(String.valueOf(currentUser.getId()));
                idField.setEditable(false);
                idField.setBackground(AppColors.BACKGROUND_LIGHT);

                // Basic info fields
                JTextField nameField = UIUtils.createRoundedTextField();
                nameField.setText(currentUser.getName());

                JTextField emailField = UIUtils.createRoundedTextField();
                emailField.setText(currentUser.getEmail());

                JTextField phoneField = UIUtils.createRoundedTextField();
                phoneField.setText(currentUser.getPhone());

                JTextField roleField = UIUtils.createRoundedTextField();
                roleField.setText(currentUser.getRole().toString());
                roleField.setEditable(false);
                roleField.setBackground(AppColors.BACKGROUND_LIGHT);

                // Registration info (Read-only)
                JTextField regDateField = UIUtils.createRoundedTextField();
                regDateField.setText(currentUser.getRegistrationDate());
                regDateField.setEditable(false);
                regDateField.setBackground(AppColors.BACKGROUND_LIGHT);

                JTextField lastLoginField = UIUtils.createRoundedTextField();
                lastLoginField.setText(currentUser.getLastLoginAt() != null ?
                    new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentUser.getLastLoginAt()) : "Never");
                lastLoginField.setEditable(false);
                lastLoginField.setBackground(AppColors.BACKGROUND_LIGHT);

                // Add fields to basic info panel
                basicInfoPanel.add(UIUtils.createLabel("User ID:", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY), gbc);
                basicInfoPanel.add(idField, gbc);
                basicInfoPanel.add(Box.createVerticalStrut(10), gbc);

                basicInfoPanel.add(UIUtils.createLabel("Name:", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY), gbc);
                basicInfoPanel.add(nameField, gbc);
                basicInfoPanel.add(Box.createVerticalStrut(10), gbc);

                basicInfoPanel.add(UIUtils.createLabel("Email:", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY), gbc);
                basicInfoPanel.add(emailField, gbc);
                basicInfoPanel.add(Box.createVerticalStrut(10), gbc);

                basicInfoPanel.add(UIUtils.createLabel("Phone:", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY), gbc);
                basicInfoPanel.add(phoneField, gbc);
                basicInfoPanel.add(Box.createVerticalStrut(10), gbc);

                basicInfoPanel.add(UIUtils.createLabel("Role:", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY), gbc);
                basicInfoPanel.add(roleField, gbc);
                basicInfoPanel.add(Box.createVerticalStrut(20), gbc);

                basicInfoPanel.add(UIUtils.createLabel("Registration Date:", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY), gbc);
                basicInfoPanel.add(regDateField, gbc);
                basicInfoPanel.add(Box.createVerticalStrut(10), gbc);

                basicInfoPanel.add(UIUtils.createLabel("Last Login:", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY), gbc);
                basicInfoPanel.add(lastLoginField, gbc);

                // Password Management Tab
                JPanel passwordPanel = new JPanel(new GridBagLayout());
                passwordPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                GridBagConstraints gbcPass = new GridBagConstraints();
                gbcPass.gridwidth = GridBagConstraints.REMAINDER;
                gbcPass.anchor = GridBagConstraints.WEST;
                gbcPass.fill = GridBagConstraints.HORIZONTAL;
                gbcPass.insets = new Insets(5, 5, 5, 5);

                JPasswordField currentPasswordField = UIUtils.createRoundedPasswordField();
                JPasswordField newPasswordField = UIUtils.createRoundedPasswordField();
                JPasswordField confirmPasswordField = UIUtils.createRoundedPasswordField();
                JLabel passwordMatchLabel = UIUtils.createLabel("", UIConstants.SMALL_FONT, AppColors.ERROR);

                // Add password fields
                passwordPanel.add(UIUtils.createLabel("Current Password:", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY), gbcPass);
                passwordPanel.add(currentPasswordField, gbcPass);
                passwordPanel.add(Box.createVerticalStrut(10), gbcPass);

                passwordPanel.add(UIUtils.createLabel("New Password:", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY), gbcPass);
                passwordPanel.add(newPasswordField, gbcPass);
                passwordPanel.add(Box.createVerticalStrut(10), gbcPass);

                passwordPanel.add(UIUtils.createLabel("Confirm Password:", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY), gbcPass);
                passwordPanel.add(confirmPasswordField, gbcPass);
                passwordPanel.add(passwordMatchLabel, gbcPass);
                passwordPanel.add(Box.createVerticalStrut(10), gbcPass);

                // Password requirements info
                JTextArea requirementsArea = new JTextArea(
                    "Password Requirements:\n" +
                    "• Minimum 8 characters\n" +
                    "• At least one uppercase letter\n" +
                    "• At least one lowercase letter\n" +
                    "• At least one number\n" +
                    "• At least one special character"
                );
                requirementsArea.setEditable(false);
                requirementsArea.setBackground(AppColors.BACKGROUND_LIGHT);
                requirementsArea.setFont(UIConstants.SMALL_FONT);
                requirementsArea.setForeground(AppColors.TEXT_SECONDARY);
                requirementsArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                passwordPanel.add(requirementsArea, gbcPass);

                // Add tabs
                tabbedPane.addTab("Basic Info", basicInfoPanel);
                tabbedPane.addTab("Change Password", passwordPanel);

                // Buttons panel
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton saveButton = UIUtils.createButton("Save Changes", null, UIUtils.ButtonType.PRIMARY, UIUtils.ButtonSize.NORMAL);
                JButton cancelButton = UIUtils.createButton("Cancel", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.NORMAL);

                saveButton.addActionListener(e -> {
                    try {
                        // Validate and update basic info
                        currentUser.setName(nameField.getText().trim());
                        currentUser.setEmail(emailField.getText().trim());
                        currentUser.setPhone(phoneField.getText().trim());

                        // Check if password needs to be updated
                        String currentPassword = new String(currentPasswordField.getPassword());
                        String newPassword = new String(newPasswordField.getPassword());
                        String confirmPassword = new String(confirmPasswordField.getPassword());

                        if (!newPassword.isEmpty() || !confirmPassword.isEmpty()) {
                            // Verify current password
                            SecurityUtils.VerificationResult verificationResult = SecurityUtils.verifyPassword(currentPassword, currentUser.getPassword());
                            if (!verificationResult.isSuccess()) {
                                throw new IllegalArgumentException("Current password is incorrect");
                            }

                            if (!newPassword.equals(confirmPassword)) {
                                throw new IllegalArgumentException("New passwords do not match");
                            }

                            if (!ValidationUtils.isValidPassword(newPassword)) {
                                throw new IllegalArgumentException("New password does not meet requirements");
                            }

                            // Update password with new hash
                            currentUser.setPassword(SecurityUtils.hashPassword(newPassword));
                        }

                        // Save changes
                        authController.updateUser(currentUser);
                        dialog.dispose();

                        JOptionPane.showMessageDialog(this,
                            "Profile updated successfully",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this,
                            "Error updating profile: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                });

                cancelButton.addActionListener(e -> dialog.dispose());

                buttonPanel.add(saveButton);
                buttonPanel.add(cancelButton);

                // Add components to dialog
                dialog.add(tabbedPane, BorderLayout.CENTER);
                dialog.add(buttonPanel, BorderLayout.SOUTH);
                dialog.setVisible(true);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error loading profile: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Creates the profile panel using the ProfilePanel component
     */
    private JPanel createProfilePanel() {
        JPanel container = UIUtils.createPanel(new BorderLayout(20, 20), true);
        container.setBackground(AppColors.BACKGROUND_LIGHT);
        container.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        User currentUser = SessionManager.getInstance().getCurrentUser();
        ProfilePanel profilePanel = new ProfilePanel(currentUser, true);
        container.add(profilePanel, BorderLayout.CENTER);

        return container;
    }

    /**
     * Adds a detail field to a panel with a label and value (GridBag version)
     *
     * @param panel The panel to add the field to
     * @param gbc The grid bag constraints
     * @param label The label text
     * @param value The value text
     */
    private void addDetailField(JPanel panel, GridBagConstraints gbc, String label, String value) {
        JLabel labelComponent = UIUtils.createLabel(label, UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY);
        gbc.gridx = 0;
        panel.add(labelComponent, gbc);

        JLabel valueComponent = UIUtils.createLabel(value != null ? value : "N/A", UIConstants.BODY_FONT, AppColors.TEXT_SECONDARY);
        gbc.gridx = 1;
        panel.add(valueComponent, gbc);

        gbc.gridy++;
    }

    /**
     * Adds a detail field to a panel with a label and value (Grid version)
     *
     * @param panel The panel to add the field to
     * @param label The label text
     * @param value The value text
     */
    private void addDetailField(JPanel panel, String label, String value) {
        JLabel labelComponent = UIUtils.createLabel(label, UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY);
        panel.add(labelComponent);

        JLabel valueComponent = UIUtils.createLabel(value != null ? value : "N/A", UIConstants.BODY_FONT, AppColors.TEXT_SECONDARY);
        panel.add(valueComponent);
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

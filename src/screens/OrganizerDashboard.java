package screens;

import controllers.AuthController;
import controllers.RegistrationController;
import controllers.EventController;
import models.User;
import models.Event;
import models.Registration;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.table.TableColumn;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.RowFilter;
import utils.*;
import components.*;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

/**
 * Organizer Dashboard
 * Implements the design from organizer_dashboard.md
 */
@SuppressWarnings("unused")
public class OrganizerDashboard extends JFrame {
    // UI Components
    private JPanel contentPanel;
    private CardLayout contentLayout;
    private JLabel eventCountLabel;
    private JLabel pendingCountLabel;
    private JLabel approvedCountLabel;
    private JLabel rejectedCountLabel;
    private JLabel participantsCountLabel;
    private JTextField searchField;
    private JTable participantsTable;
    private int currentEventId;
    private final EventController eventController;
    private final RegistrationController registrationController;
    private final AuthController authController;
    private JPanel dashboardStatsPanel;
    private JPanel recentEventsPanel;

    // Participants panel component
    private ParticipantsPanel participantsPanel;

    // Media Upload panel component
    private MediaUploadPanel mediaUploadPanel;

    public OrganizerDashboard() throws SQLException {
        try {
            // Initialize controllers
            eventController = EventController.getInstance();
            registrationController = RegistrationController.getInstance();
            authController = AuthController.getInstance();

            // Check session
            if (!authController.isLoggedIn() || !authController.isOrganizer()) {
                UIUtils.showError(this, "Access denied. Only organizers can access this dashboard.");
                dispose();
                try {
                    new LoginScreen().setVisible(true);
                } catch (SQLException ex) {
                    UIUtils.showError(this, "Error opening login screen: " + ex.getMessage());
                }
                return;
            }

            initializeFrame();
            setupUI();
            loadDashboardData();
            setVisible(true);
        } catch (SQLException e) {
            UIUtils.showError(this, "Error initializing dashboard: " + e.getMessage());
            throw e;
        }
    }

    private void initializeFrame() {
        setTitle("Event Management System - Organizer Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void setupUI() throws SQLException {
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
        contentPanel = UIUtils.createPanel(new CardLayout(), true);
        contentLayout = (CardLayout) contentPanel.getLayout();

        // Add content cards
        contentPanel.add(createDashboardPanel(), "Dashboard");
        contentPanel.add(createMyEventsPanel(), "My Events");
        contentPanel.add(createCreateEventPanel(), "Create Event");
        contentPanel.add(createParticipantsPanel(), "Participants");
        contentPanel.add(createMediaUploadPanel(), "Media Upload");

        // Show the dashboard by default
        contentLayout.show(contentPanel, "Dashboard");

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private JPanel createHeaderPanel() throws SQLException {
        User currentUser = authController.getCurrentUser();
        return new HeaderPanel(
            currentUser.getName(),
            "Event Organizer",
            false,
            null
        );
    }

    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BorderLayout());
        sidebarPanel.setBackground(AppColors.BACKGROUND_DARK);
        sidebarPanel.setPreferredSize(new Dimension(250, Integer.MAX_VALUE));

        // Top panel for user info
        JPanel topPanel = UIUtils.createPanel(new FlowLayout(FlowLayout.CENTER, 0, 15), false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        topPanel.setPreferredSize(new Dimension(250, 150));

        // User info panel (wrapper for BoxLayout components)
        JPanel userInfoWrapper = new JPanel();
        userInfoWrapper.setLayout(new BoxLayout(userInfoWrapper, BoxLayout.Y_AXIS));
        userInfoWrapper.setOpaque(false);
        userInfoWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);

        // User avatar - Keep existing painting logic or use an actual image
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw circle
                g2.setColor(Color.WHITE);
                g2.fillOval(0, 0, getWidth(), getHeight());

                // Draw first letter
                g2.setColor(AppColors.PRIMARY_DARK);
                g2.setFont(UIConstants.BODY_FONT_BOLD); // Use bold font for initial

                // Get current user safely
                String firstLetter = "G"; // Default to Guest
                try {
                    User currentUser = authController.getCurrentUser();
                    if (currentUser != null && currentUser.getName() != null && !currentUser.getName().isEmpty()) {
                        firstLetter = currentUser.getName().substring(0, 1).toUpperCase();
                    } else {
                         System.err.println("Warning: Current user or username is null/empty.");
                    }
                } catch (Exception e) {
                     System.err.println("Error getting current user for avatar: " + e.getMessage());
                }

                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(firstLetter)) / 2;
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();

                g2.drawString(firstLetter, x, y);
                g2.dispose();
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(60, 60); // Larger avatar size
            }
             @Override
            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        avatarPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userInfoWrapper.add(avatarPanel);
        userInfoWrapper.add(Box.createVerticalStrut(10)); // Spacing below avatar

        // User name
        JLabel nameLabel = UIUtils.createLabel(
            "Loading...", // Placeholder
            UIConstants.BODY_FONT_BOLD,
            AppColors.TEXT_LIGHT
        );
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
         userInfoWrapper.add(nameLabel);

        // User role
        JLabel roleLabel = UIUtils.createLabel(
            "Loading...", // Placeholder
            UIConstants.SMALL_FONT,
            AppColors.TEXT_LIGHT_SECONDARY
        );
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
         userInfoWrapper.add(roleLabel);

        topPanel.add(userInfoWrapper); // Add wrapper to top panel
        sidebarPanel.add(topPanel, BorderLayout.NORTH);

        // Center panel for navigation
        JPanel centerPanel = UIUtils.createPanel(new BorderLayout(), false);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Navigation label
        JLabel navLabel = UIUtils.createLabel(
            "NAVIGATION",
            UIConstants.SMALL_FONT_BOLD,
            AppColors.TEXT_LIGHT_SECONDARY
        );
        navLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 5, 20));
        centerPanel.add(navLabel, BorderLayout.NORTH);

        // Navigation buttons panel - Use BoxLayout for stacking buttons
        JPanel navButtonsPanel = new JPanel();
        navButtonsPanel.setLayout(new BoxLayout(navButtonsPanel, BoxLayout.Y_AXIS));
        navButtonsPanel.setOpaque(false);
        navButtonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        navButtonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Dashboard button
        JButton dashboardButton = UIUtils.createSidebarNavButton(
            "Dashboard",
            "dashboard",
            e -> contentLayout.show(contentPanel, "Dashboard")
        );
        navButtonsPanel.add(dashboardButton);
        navButtonsPanel.add(Box.createVerticalStrut(5)); // Spacing between buttons

        // My Events button
        JButton myEventsButton = UIUtils.createSidebarNavButton(
            "My Events",
            "calendar",
            e -> contentLayout.show(contentPanel, "My Events")
        );
        navButtonsPanel.add(myEventsButton);
        navButtonsPanel.add(Box.createVerticalStrut(5)); // Spacing between buttons

        // Create Event button
        JButton createEventButton = UIUtils.createSidebarNavButton(
            "Create Event",
            "create-event",
            e -> contentLayout.show(contentPanel, "Create Event")
        );
        navButtonsPanel.add(createEventButton);
        navButtonsPanel.add(Box.createVerticalStrut(5)); // Spacing between buttons

        // Participants button
        JButton participantsButton = UIUtils.createSidebarNavButton(
            "Participants",
            "users",
            e -> contentLayout.show(contentPanel, "Participants")
        );
        navButtonsPanel.add(participantsButton);
         navButtonsPanel.add(Box.createVerticalStrut(5)); // Spacing between buttons

        // Media Upload button
        JButton mediaUploadButton = UIUtils.createSidebarNavButton(
            "Media Upload",
            "media",
            e -> contentLayout.show(contentPanel, "Media Upload")
        );
        navButtonsPanel.add(mediaUploadButton);

        centerPanel.add(navButtonsPanel, BorderLayout.CENTER);
        sidebarPanel.add(centerPanel, BorderLayout.CENTER);

        // Bottom panel for account section
        JPanel bottomPanel = UIUtils.createPanel(new BorderLayout(), false);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

        // Account label
        JLabel accountLabel = UIUtils.createLabel(
            "ACCOUNT",
            UIConstants.SMALL_FONT_BOLD,
            AppColors.TEXT_LIGHT_SECONDARY
        );
        accountLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 5, 20));
        bottomPanel.add(accountLabel, BorderLayout.NORTH);

        // Account buttons panel - Use BoxLayout for stacking buttons
        JPanel accountButtonsPanel = new JPanel();
        accountButtonsPanel.setLayout(new BoxLayout(accountButtonsPanel, BoxLayout.Y_AXIS));
        accountButtonsPanel.setOpaque(false);
        accountButtonsPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // Logout button
        JButton logoutButton = UIUtils.createSidebarNavButton(
            "Logout",
            "logout",
            e -> handleLogout()
        );
        accountButtonsPanel.add(logoutButton);

        bottomPanel.add(accountButtonsPanel, BorderLayout.CENTER);
        sidebarPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Load user info asynchronously
        SwingUtilities.invokeLater(() -> {
            try {
                 User currentUser = authController.getCurrentUser();
                 if (currentUser != null) {
                     nameLabel.setText(currentUser.getName());
                     roleLabel.setText(currentUser.getRole().toString().replace("_", " "));
                     avatarPanel.repaint();
                 }
            } catch (Exception e) {
                 System.err.println("Error loading user info for sidebar: " + e.getMessage());
                 nameLabel.setText("Error");
                 roleLabel.setText("Error");
            }
        });

        return sidebarPanel;
    }

    private JPanel createDashboardPanel() {
        JPanel panel = UIUtils.createPanel(new BorderLayout(20, 20), true);
        panel.setBackground(AppColors.BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header section (Greeting and Date)
        JPanel headerSection = UIUtils.createPanel(new BorderLayout(), false);
        headerSection.setOpaque(false);

        // Greeting
        User currentUser = authController.getCurrentUser();
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

        // Main content area for dashboard (Stats, Recent Events, Quick Actions)
        JPanel mainContent = UIUtils.createPanel(new BorderLayout(20, 20), false);
        mainContent.setOpaque(false);

        // Stats row
        dashboardStatsPanel = UIUtils.createPanel(new GridLayout(1, 5, 20, 0), false);
        dashboardStatsPanel.setOpaque(false);

        // Recent Events and Quick Actions side by side
        JPanel bottomSection = UIUtils.createPanel(new GridLayout(1, 2, 20, 0), false);
        bottomSection.setOpaque(false);

        // Recent Events Panel
        recentEventsPanel = UIUtils.createPanel(new BorderLayout(), false);
        recentEventsPanel.setOpaque(false);
        recentEventsPanel.setBorder(UIUtils.createRoundedBorder(AppColors.BORDER, UIConstants.CORNER_RADIUS_MEDIUM, 1));
        recentEventsPanel.setBackground(Color.WHITE);

        JLabel recentEventsTitle = UIUtils.createLabel(
            "Recent Events",
            UIConstants.BODY_FONT_BOLD,
            AppColors.TEXT_PRIMARY
        );
        recentEventsTitle.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        recentEventsPanel.add(recentEventsTitle, BorderLayout.NORTH);

        // Recent Events Table (Placeholder/Existing table will be added here)
        String[] columns = {"Event Name", "Date", "Location", "Status", "Participants"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        JTable recentEventsTable = new JTable(tableModel);
        recentEventsTable.setFillsViewportHeight(true);
        recentEventsTable.setRowHeight(30);
        recentEventsTable.setFont(UIConstants.BODY_FONT);
        recentEventsTable.getTableHeader().setFont(UIConstants.SMALL_FONT_BOLD);
        recentEventsTable.getTableHeader().setBackground(AppColors.BACKGROUND_LIGHT);
        recentEventsTable.getTableHeader().setForeground(AppColors.TEXT_SECONDARY);
        recentEventsTable.setGridColor(AppColors.BORDER);
        recentEventsTable.setSelectionBackground(AppColors.PRIMARY_LIGHT);
        recentEventsTable.setSelectionForeground(AppColors.TEXT_PRIMARY);

        JScrollPane tableScrollPane = new JScrollPane(recentEventsTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        recentEventsPanel.add(tableScrollPane, BorderLayout.CENTER);


        // Quick Actions Panel
        JPanel quickActionsPanel = UIUtils.createPanel(new BorderLayout(), false);
        quickActionsPanel.setOpaque(false);
        quickActionsPanel.setBorder(UIUtils.createRoundedBorder(AppColors.BORDER, UIConstants.CORNER_RADIUS_MEDIUM, 1));
        quickActionsPanel.setBackground(Color.WHITE);

        JLabel quickActionsTitle = UIUtils.createLabel(
            "Quick Actions",
            UIConstants.BODY_FONT_BOLD,
            AppColors.TEXT_PRIMARY
        );
        quickActionsTitle.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        quickActionsPanel.add(quickActionsTitle, BorderLayout.NORTH);

        // Quick Actions Buttons Grid
        JPanel actionsGrid = UIUtils.createPanel(new GridLayout(2, 2, 15, 15), false);
        actionsGrid.setOpaque(false);
        actionsGrid.setBorder(BorderFactory.createEmptyBorder(0, 15, 15, 15));

        // Create New Event button
        JButton createEventButton = UIUtils.createButton(
            "Create New Event",
            null,
            UIUtils.ButtonType.PRIMARY,
            UIUtils.ButtonSize.LARGE_RECTANGULAR
        );
        createEventButton.addActionListener(e -> {
            JDialog dialog = new JDialog(this, "Create New Event", true);
            dialog.setLayout(new BorderLayout());
            
            CreateEventForm form = new CreateEventForm("Organizer", success -> {
                dialog.dispose();
                if (success) {
                    loadDashboardData(); // Refresh dashboard data after successful creation
                }
            });
            
            dialog.add(form, BorderLayout.CENTER);
            dialog.pack();
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        });
        actionsGrid.add(createEventButton);

        // Manage Events button (Assuming this goes to My Events)
        JButton manageEventsButton = UIUtils.createButton(
            "Manage Events",
            null,
            UIUtils.ButtonType.PRIMARY,
            UIUtils.ButtonSize.LARGE_RECTANGULAR
        );
        manageEventsButton.addActionListener(e -> contentLayout.show(contentPanel, "My Events"));
        actionsGrid.add(manageEventsButton);

        // View Participants button
        JButton viewParticipantsButton = UIUtils.createButton(
            "View Participants",
            null,
            UIUtils.ButtonType.PRIMARY,
            UIUtils.ButtonSize.LARGE_RECTANGULAR
        );
        viewParticipantsButton.addActionListener(e -> contentLayout.show(contentPanel, "Participants"));
        actionsGrid.add(viewParticipantsButton);

        // Upload Media button
        JButton uploadMediaButton = UIUtils.createButton(
            "Upload Media",
            null,
            UIUtils.ButtonType.PRIMARY,
            UIUtils.ButtonSize.LARGE_RECTANGULAR
        );
        uploadMediaButton.addActionListener(e -> contentLayout.show(contentPanel, "Media Upload"));
        actionsGrid.add(uploadMediaButton);

        quickActionsPanel.add(actionsGrid, BorderLayout.CENTER);

        bottomSection.add(recentEventsPanel);
        bottomSection.add(quickActionsPanel);

        mainContent.add(dashboardStatsPanel, BorderLayout.NORTH);
        mainContent.add(bottomSection, BorderLayout.CENTER);

        panel.add(mainContent, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatCard(String title, String value, Color valueColor) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(0, 10), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
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
            UIConstants.HEADER_FONT,
            valueColor
        );
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createMyEventsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppColors.BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add loading indicator
        JPanel loadingPanel = UIUtils.createLoadingPanel();
        panel.add(loadingPanel, BorderLayout.CENTER);

        // Load data in background
        SwingUtilities.invokeLater(() -> {
            try {
                panel.remove(loadingPanel);
                DefaultTableModel tableModel = new DefaultTableModel(
                    new Object[]{"Event Name", "Date", "Location", "Status", "Actions"},
                    0
                ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                        return column == 4; // Only allow editing of Actions column
                    }
                };
                JTable table = new JTable(tableModel);
                table.setRowHeight(40);
                table.getTableHeader().setReorderingAllowed(false);
                
                // Add table to scroll pane
                JScrollPane scrollPane = new JScrollPane(table);
                panel.add(scrollPane, BorderLayout.CENTER);
                
                // Load data
                loadMyEventsData(tableModel);
            } catch (Exception e) {
                UIUtils.showError(this, "Error loading events: " + e.getMessage());
            }
        });

        return panel;
    }

    private void loadMyEventsData(DefaultTableModel tableModel) {
        try {
            User currentUser = authController.getCurrentUser();
            List<Event> events = eventController.getEventsByOrganizer(currentUser.getId());
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            for (Event event : events) {
                Object[] row = {
                    event.getId(),
                    event.getName(),
                    event.getStartDateTime().format(dateFormatter),
                    event.getVenueName(),
                    event.getStatus().toString(),
                    "View Details"
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading events: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private JPanel createCreateEventPanel() {
        JPanel panel = UIUtils.createPanel(new BorderLayout(20, 20), true);
        panel.setBackground(AppColors.BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Use the CreateEventForm component
        try {
            CreateEventForm createEventForm = new CreateEventForm("Organizer", success -> {
                // Handle form submission callback
                if (success) {
                    // Optionally refresh dashboard data or show a confirmation message
                    loadDashboardData(); // Refresh dashboard stats and recent events
                    UIUtils.showSuccess(this, "Event created successfully!");
                } else {
                    // Handle cancellation or failure
                    UIUtils.showError(this, "Event creation cancelled or failed.");
                }
                // Switch back to dashboard view
                contentLayout.show(contentPanel, "Dashboard");
            });
             panel.add(createEventForm, BorderLayout.CENTER);
        } catch (Exception e) {
            UIUtils.showError(this, "Error loading create event form: " + e.getMessage());
            JLabel errorLabel = UIUtils.createLabel("Error loading form.", UIConstants.BODY_FONT, AppColors.ERROR);
             errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
             panel.add(errorLabel, BorderLayout.CENTER);
            e.printStackTrace();
        }

        return panel;
    }

    private void loadVenueData() {
        // This method is no longer needed here as it's handled in CreateEventForm
    }

    private void loadCategoryData() {
        // This method is no longer needed here as it's handled in CreateEventForm
    }

    private void handleCreateEvent() {
         // This method is no longer needed here as it's handled in CreateEventForm
    }

    private JPanel createParticipantsPanel() {
        JPanel panel = UIUtils.createPanel(new BorderLayout(), true);
        panel.setBackground(AppColors.BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Use the ParticipantsPanel component
        try {
            User currentUser = authController.getCurrentUser();
            if (currentUser != null) {
                participantsPanel = new ParticipantsPanel(currentUser.getId());
                panel.add(participantsPanel, BorderLayout.CENTER);
            } else {
                JLabel errorLabel = UIUtils.createLabel("Error: User not logged in.", UIConstants.BODY_FONT, AppColors.ERROR);
                errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
                panel.add(errorLabel, BorderLayout.CENTER);
            }
        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading participants panel: " + e.getMessage());
            JLabel errorLabel = UIUtils.createLabel("Error loading participants.", UIConstants.BODY_FONT, AppColors.ERROR);
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(errorLabel, BorderLayout.CENTER);
            e.printStackTrace();
        } catch (Exception e) { // Catch any other potential exceptions during panel creation
            UIUtils.showError(this, "An unexpected error occurred loading participants: " + e.getMessage());
             JLabel errorLabel = UIUtils.createLabel("An unexpected error occurred.", UIConstants.BODY_FONT, AppColors.ERROR);
             errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
             panel.add(errorLabel, BorderLayout.CENTER);
             e.printStackTrace();
        }

        return panel;
    }

    private void loadParticipantsData(DefaultTableModel tableModel) {
        try {
            User currentUser = authController.getCurrentUser();
            List<Event> events = eventController.getEventsByOrganizer(currentUser.getId());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            tableModel.setRowCount(0);

            for (Event event : events) {
                List<Registration> registrations = registrationController.getEventRegistrations(event.getId());
                for (Registration registration : registrations) {
                    User participant = registration.getAttendee();
                    Object[] row = {
                        participant.getName(),
                        participant.getEmail(),
                        dateFormat.format(registration.getRegistrationDate()),
                        registration.getStatus().toString(),
                        ""
                    };
                    tableModel.addRow(row);
                }
            }
        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading participants: " + e.getMessage());
        }
    }

    private void filterParticipants() {
        String searchText = searchField.getText().toLowerCase();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>((DefaultTableModel) participantsTable.getModel());
        participantsTable.setRowSorter(sorter);

        if (searchText.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
        }
    }

    private class ButtonRenderer extends DefaultTableCellRenderer {
        private JButton button;

        public ButtonRenderer() {
            button = UIUtils.createButton(
                "View Details",
                null,
                UIUtils.ButtonType.PRIMARY,
                UIUtils.ButtonSize.SMALL
            );
            button.setIconTextGap(10);
            button.setHorizontalTextPosition(SwingConstants.LEFT);
            button.setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }

        @Override
        public void paint(Graphics g) {
            super.paint(g);
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private int row;
        private JTable table;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = UIUtils.createButton(
                "View Details",
                null,
                UIUtils.ButtonType.PRIMARY,
                UIUtils.ButtonSize.SMALL
            );
            button.setIconTextGap(10);
            button.setHorizontalTextPosition(SwingConstants.LEFT);
            button.setHorizontalAlignment(SwingConstants.CENTER);
            button.addActionListener(e -> fireEditingStopped());
            checkBox.setVisible(false);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            this.table = table;
            this.row = row;
            label = (value == null) ? "" : value.toString();
            if (isSelected) {
                button.setForeground(table.getSelectionForeground());
                button.setBackground(table.getSelectionBackground());
            } else {
                button.setForeground(table.getForeground());
                button.setBackground(UIManager.getColor("Button.background"));
            }
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                DefaultTableModel model = (DefaultTableModel) table.getModel();
                String participantName = (String) model.getValueAt(row, 0);
                String participantEmail = (String) model.getValueAt(row, 1);
                
                JOptionPane.showMessageDialog(button,
                    "View details for: " + participantName + " (" + participantEmail + ") coming soon...",
                    "View Details",
                    JOptionPane.INFORMATION_MESSAGE);
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

    private JPanel createMediaUploadPanel() {
        JPanel panel = UIUtils.createPanel(new BorderLayout(), true);
        panel.setBackground(AppColors.BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Use the MediaUploadPanel component
        try {
            mediaUploadPanel = new MediaUploadPanel();
            panel.add(mediaUploadPanel, BorderLayout.CENTER);
        } catch (Exception e) { // Catch any potential exceptions during panel creation
            UIUtils.showError(this, "An error occurred loading media upload panel: " + e.getMessage());
             JLabel errorLabel = UIUtils.createLabel("An unexpected error occurred.", UIConstants.BODY_FONT, AppColors.ERROR);
             errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
             panel.add(errorLabel, BorderLayout.CENTER);
             e.printStackTrace();
        }

        return panel;
    }

    private void loadDashboardData() {
        try {
            // Show loading indicator
            JPanel loadingPanel = UIUtils.createLoadingPanel();
            contentPanel.add(loadingPanel, "Loading");
            contentLayout.show(contentPanel, "Loading");

            // Load data in background
            SwingUtilities.invokeLater(() -> {
                try {
                    loadStatCards();
                    loadRecentEvents();
                    contentLayout.show(contentPanel, "Dashboard");
                } catch (Exception e) {
                    UIUtils.showError(this, "Error loading dashboard data: " + e.getMessage());
                    contentLayout.show(contentPanel, "Dashboard");
                }
            });
        } catch (Exception e) {
            UIUtils.showError(this, "Error initializing dashboard: " + e.getMessage());
        contentLayout.show(contentPanel, "Dashboard");
        }
    }

    private void loadStatCards() {
        try {
            User currentUser = authController.getCurrentUser();
            List<Event> events = eventController.getEventsByOrganizer(currentUser.getId());

            int totalEvents = events.size();
            int pendingEvents = (int) events.stream()
                .filter(e -> e.getStatus() == Event.EventStatus.PENDING)
                .count();
            int approvedEvents = (int) events.stream()
                .filter(e -> e.getStatus() == Event.EventStatus.APPROVED)
                .count();
            int rejectedEvents = (int) events.stream()
                .filter(e -> e.getStatus() == Event.EventStatus.REJECTED)
                .count();

            int totalParticipants = 0;
            for (Event event : events) {
                totalParticipants += registrationController.getEventRegistrations(event.getId()).size();
            }

            dashboardStatsPanel.removeAll();

            dashboardStatsPanel.add(createStatCard("Total Events", String.valueOf(totalEvents), AppColors.PRIMARY));
            dashboardStatsPanel.add(createStatCard("Pending", String.valueOf(pendingEvents), AppColors.ACCENT_YELLOW));
            dashboardStatsPanel.add(createStatCard("Approved", String.valueOf(approvedEvents), AppColors.ACCENT_GREEN));
            dashboardStatsPanel.add(createStatCard("Rejected", String.valueOf(rejectedEvents), AppColors.ACCENT_RED));
            dashboardStatsPanel.add(createStatCard("Participants", String.valueOf(totalParticipants), AppColors.PRIMARY));

            dashboardStatsPanel.revalidate();
            dashboardStatsPanel.repaint();

        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading dashboard data: " + e.getMessage());
        }
    }

    private void loadRecentEvents() {
        try {
            User currentUser = authController.getCurrentUser();
            List<Event> events = eventController.getEventsByOrganizer(currentUser.getId());
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

            // Clear existing events
            recentEventsPanel.removeAll();
            recentEventsPanel.setLayout(new BoxLayout(recentEventsPanel, BoxLayout.Y_AXIS));

            // Add recent events (up to 5)
            int count = 0;
            for (Event event : events) {
                if (count >= 5) break;

                JPanel eventPanel = new JPanel(new BorderLayout(10, 0));
                eventPanel.setBackground(Color.WHITE);
                eventPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppColors.BORDER),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));

                // Event name
                JLabel nameLabel = new JLabel(event.getName());
                nameLabel.setFont(UIConstants.BODY_FONT_BOLD);
                eventPanel.add(nameLabel, BorderLayout.NORTH);

                // Event details
                JPanel detailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
                detailsPanel.setOpaque(false);

                JLabel dateLabel = new JLabel("📅 " + event.getStartDateTime().format(dateFormatter));
                dateLabel.setFont(UIConstants.SMALL_FONT);
                detailsPanel.add(dateLabel);

                JLabel venueLabel = new JLabel("📍 " + event.getVenueName());
                venueLabel.setFont(UIConstants.SMALL_FONT);
                detailsPanel.add(venueLabel);

                eventPanel.add(detailsPanel, BorderLayout.CENTER);
                recentEventsPanel.add(eventPanel);
                recentEventsPanel.add(Box.createVerticalStrut(10));

                count++;
            }

            recentEventsPanel.revalidate();
            recentEventsPanel.repaint();
        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading recent events: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleLogout() {
        try {
            authController.logout();
            dispose();
            new LoginScreen().setVisible(true);
        } catch (SQLException e) {
            UIUtils.showError(this, "Error during logout: " + e.getMessage());
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
                new OrganizerDashboard().setVisible(true);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Error initializing organizer dashboard: " + e.getMessage(),
                    "Initialization Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

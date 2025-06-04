package screens;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

import components.CreateEventForm;
import components.EventDetailsPanel;
import components.GradientButton;
import components.HeaderPanel;
import components.MediaUploadPanel;
import components.ParticipantsPanel;
import components.ProfilePanel;
import components.RoundedPanel;
import components.SidebarPanel;
import components.RecentEventItem;
import controllers.AuthController;
import controllers.EventController;
import controllers.RegistrationController;
import models.Event;
import models.Event.EventStatus;
import models.Registration;
import models.User;
import utils.AppColors;
import utils.UIConstants;
import utils.UIUtils;

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

    private JTable table; // Add this field at the class level

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
        JPanel mainPanel = UIUtils.createPanel(new BorderLayout(), true);
        mainPanel.setBackground(AppColors.BACKGROUND_LIGHT);

        contentPanel = UIUtils.createPanel(new CardLayout(), true);
        contentLayout = (CardLayout) contentPanel.getLayout();

        // Use the shared header and sidebar
        String username = authController.getCurrentUser().getName();
        HeaderPanel headerPanel = new HeaderPanel(username, "Event Organizer");
        SidebarPanel sidebarPanel = new SidebarPanel(contentLayout, contentPanel, username, "Event Organizer");

        // Add navigation buttons with emoji icons
        sidebarPanel.addNavButton("🏠 Dashboard", null, "Dashboard", true);
        sidebarPanel.addNavButton("📅 My Events", null, "My Events", false);
        sidebarPanel.addNavButton("➕ Create Event", null, "Create Event", false);
        sidebarPanel.addNavButton("👥 Participants", null, "Participants", false);
        sidebarPanel.addNavButton("📸 Media Upload", null, "Media Upload", false);
        sidebarPanel.addNavButton("👤 Profile", null, "Profile", false);
        sidebarPanel.addLogoutButton(e -> handleLogout());

        // Add content cards
        contentPanel.add(createDashboardPanel(), "Dashboard");
        contentPanel.add(createMyEventsPanel(), "My Events");
        contentPanel.add(new CreateEventForm("Organizer", success -> {
            if (success) {
                loadDashboardData();
                contentLayout.show(contentPanel, "Dashboard");
            }
        }), "Create Event");
        contentPanel.add(createParticipantsPanel(), "Participants");
        contentPanel.add(createMediaUploadPanel(), "Media Upload");
        contentPanel.add(new ProfilePanel(authController.getCurrentUser(), true), "Profile");

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);

        contentLayout.show(contentPanel, "Dashboard");
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

        // Main content area for dashboard (Stats, Recent Events)
        JPanel mainContent = UIUtils.createPanel(new BorderLayout(20, 20), false);
        mainContent.setOpaque(false);

        // Stats row
        dashboardStatsPanel = UIUtils.createPanel(new GridLayout(1, 5, 20, 0), false);
        dashboardStatsPanel.setOpaque(false);

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

        // Create scrollable list panel for Recent Events
        JPanel recentEventsListPanel = new JPanel();
        recentEventsListPanel.setLayout(new BoxLayout(recentEventsListPanel, BoxLayout.Y_AXIS));
        recentEventsListPanel.setOpaque(false);

        JScrollPane recentEventsScrollPane = new JScrollPane(recentEventsListPanel);
        recentEventsScrollPane.setBorder(BorderFactory.createEmptyBorder());
        recentEventsScrollPane.setOpaque(false);
        recentEventsScrollPane.getViewport().setOpaque(false);
        recentEventsPanel.add(recentEventsScrollPane, BorderLayout.CENTER);

        // Main content layout: stats on top, recent events below
        mainContent.add(dashboardStatsPanel, BorderLayout.NORTH);
        mainContent.add(recentEventsPanel, BorderLayout.CENTER);

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
        JPanel panel = UIUtils.createPanel(new BorderLayout(20, 20), true);
        panel.setBackground(AppColors.BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header section
        JPanel headerSection = UIUtils.createPanel(new BorderLayout(), false);
        headerSection.setOpaque(false);

        JLabel titleLabel = UIUtils.createLabel(
            "My Events",
            UIConstants.HEADER_FONT,
            AppColors.TEXT_PRIMARY
        );
        headerSection.add(titleLabel, BorderLayout.WEST);

        // Stats panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);

        try {
            List<Event> myEvents = eventController.getEventsByOrganizer(authController.getCurrentUser().getId());

            // Count events by status
            long upcomingCount = myEvents.stream().filter(e -> e.isUpcoming() && e.getStatus() == EventStatus.APPROVED).count();
            long ongoingCount = myEvents.stream().filter(e -> e.isOngoing() && e.getStatus() == EventStatus.APPROVED).count();
            long completedCount = myEvents.stream().filter(e -> e.getStatus() == EventStatus.COMPLETED).count();

            // Add stat cards
            statsPanel.add(UIUtils.createStatCard("Total Events", String.valueOf(myEvents.size()), null));
            statsPanel.add(UIUtils.createStatCard("Upcoming", String.valueOf(upcomingCount), null));
            statsPanel.add(UIUtils.createStatCard("Ongoing", String.valueOf(ongoingCount), null));
            statsPanel.add(UIUtils.createStatCard("Completed", String.valueOf(completedCount), null));
        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading event statistics: " + e.getMessage());
        }

        // Add header and stats to top panel
        JPanel topPanel = new JPanel(new BorderLayout(0, 20));
        topPanel.setOpaque(false);
        topPanel.add(headerSection, BorderLayout.NORTH);
        topPanel.add(statsPanel, BorderLayout.CENTER);
        panel.add(topPanel, BorderLayout.NORTH);

        // Main content - Events Table
        RoundedPanel contentPanel = new RoundedPanel(new BorderLayout(), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        contentPanel.setBorder(UIUtils.createRoundedBorder(AppColors.BORDER, UIConstants.CORNER_RADIUS_MEDIUM, 1));

        // Create table model
        String[] columns = {"ID", "Title", "Date", "Venue", "Status", "Registered", "Available", "Actions"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only actions column is editable
            }
        };

        JTable eventsTable = new JTable(tableModel);
        eventsTable.setRowHeight(40);
        eventsTable.setShowGrid(true);
        eventsTable.setGridColor(new Color(230, 230, 230));
        eventsTable.getTableHeader().setReorderingAllowed(false);
        eventsTable.getTableHeader().setFont(UIConstants.SMALL_FONT_BOLD);
        eventsTable.getTableHeader().setBackground(AppColors.BACKGROUND_LIGHT);
        eventsTable.getTableHeader().setForeground(AppColors.TEXT_SECONDARY);

        // Set column widths
        eventsTable.getColumnModel().getColumn(0).setMaxWidth(50); // ID
        eventsTable.getColumnModel().getColumn(3).setPreferredWidth(150); // Venue
        eventsTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Status
        eventsTable.getColumnModel().getColumn(5).setMaxWidth(100); // Registered
        eventsTable.getColumnModel().getColumn(6).setMaxWidth(100); // Available
        eventsTable.getColumnModel().getColumn(7).setMaxWidth(120); // Actions

        // Add button renderer for actions column
        eventsTable.getColumnModel().getColumn(7).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JButton button = UIUtils.createButton("View Details", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.SMALL);
                button.setHorizontalAlignment(SwingConstants.CENTER);
                return button;
            }
        });

        // Add button editor for actions column
        eventsTable.getColumnModel().getColumn(7).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
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
                            OrganizerDashboard.this.showEventDetails(event);
                        } catch (SQLException ex) {
                            UIUtils.showError(panel, "Error loading event details: " + ex.getMessage());
                        }
                    }
                    fireEditingStopped();
                });
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                return button;
            }

            @Override
            public Object getCellEditorValue() {
                return "View Details";
            }
        });

        // Load events data
        try {
            List<Event> events = eventController.getEventsByOrganizer(authController.getCurrentUser().getId());
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");

            for (Event event : events) {
                Object[] rowData = {
                    event.getId(),
                    event.getTitle(),
                    event.getEventDate() != null ? event.getEventDate().format(dateFormat) : "N/A",
                    event.getVenueName(),
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

        JTextField searchField = UIUtils.createRoundedTextField();
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setToolTipText("Search by event title");

        JButton searchButton = UIUtils.createButton("Search", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.SMALL);

        JComboBox<String> statusFilter = UIUtils.createRoundedComboBox(new String[]{"All", "APPROVED", "PENDING", "COMPLETED", "CANCELLED"});
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
                RowFilter<DefaultTableModel, Object> statusRowFilter = RowFilter.regexFilter("^" + selectedStatus + "$", 4); // Status column
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
                sorter.setRowFilter(RowFilter.regexFilter("^" + selectedStatus + "$", 4)); // Status column
            } else {
                RowFilter<DefaultTableModel, Object> textFilter = RowFilter.regexFilter("(?i)" + searchText, 1);
                RowFilter<DefaultTableModel, Object> statusRowFilter = RowFilter.regexFilter("^" + selectedStatus + "$", 4);
                sorter.setRowFilter(RowFilter.andFilter(List.of(textFilter, statusRowFilter)));
            }
        });

        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilter);

        // Add table to scroll pane
        JScrollPane tableScrollPane = new JScrollPane(eventsTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());

        // Add components to content panel
        contentPanel.add(filterPanel, BorderLayout.NORTH);
        contentPanel.add(tableScrollPane, BorderLayout.CENTER);

        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private void loadMyEventsData(DefaultTableModel tableModel, String statusFilter) {
        try {
            User currentUser = authController.getCurrentUser();
            List<Event> events = eventController.getEventsByOrganizer(currentUser.getId());
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

            // Clear existing data
            tableModel.setRowCount(0);

            for (Event event : events) {
                // Apply status filter
                if (!statusFilter.equals("All") && !event.getStatus().name().equals(statusFilter.toUpperCase())) {
                    continue;
                }

                Object[] row = {
                    event.getId(),
                    event.getTitle(),
                    event.getEventDate().format(dateFormatter),
                    event.getVenueName(),
                    event.getStatus().name(),
                    event.getAvailableSlots() + "/" + event.getTotalSlots(),
                    "View Details" // This will be replaced by a button in the renderer
                };
                tableModel.addRow(row);
            }

            // Add button renderer and editor
            table.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
            table.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));

        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading events: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText("View Details");
            setFont(UIConstants.BODY_FONT);
            setForeground(AppColors.PRIMARY);
            setBackground(isSelected ? AppColors.PRIMARY_LIGHT : Color.WHITE);
            return this;
        }
    }

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
            label = "View Details";
            isPushed = true;
            this.row = row;
            button.setText(label);
            button.setFont(UIConstants.BODY_FONT);
            button.setForeground(AppColors.PRIMARY);
            button.setBackground(isSelected ? AppColors.PRIMARY_LIGHT : Color.WHITE);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                try {
                    DefaultTableModel model = (DefaultTableModel) table.getModel();
                    int eventId = (int) model.getValueAt(row, 0);
                    Event event = eventController.getEvent(eventId);
                    if (event != null) {
                        // Create a dialog with options to view or edit
                        JDialog optionsDialog = new JDialog(OrganizerDashboard.this, "Event Options", true);
                        optionsDialog.setLayout(new BorderLayout());
                        optionsDialog.setSize(300, 150);
                        optionsDialog.setLocationRelativeTo(OrganizerDashboard.this);

                        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));
                        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

                        JButton viewButton = new JButton("View Event Details");
                        viewButton.addActionListener(e -> {
                            optionsDialog.dispose();
                            showEventDetails(event);
                        });

                        JButton editButton = new JButton("Edit Event");
                        boolean canEdit = event.getStatus() != Event.EventStatus.COMPLETED &&
                                         event.getStatus() != Event.EventStatus.CANCELLED;
                        editButton.setEnabled(canEdit);
                        editButton.addActionListener(e -> {
                            optionsDialog.dispose();
                            showEditEventDialog(event);
                        });

                        buttonPanel.add(viewButton);
                        buttonPanel.add(editButton);

                        optionsDialog.add(buttonPanel, BorderLayout.CENTER);
                        optionsDialog.setVisible(true);
                    }
                } catch (SQLException e) {
                    UIUtils.showError(OrganizerDashboard.this, "Error opening event details: " + e.getMessage());
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

    private JPanel createCreateEventPanel() {
        JPanel panel = UIUtils.createPanel(new BorderLayout(), true);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = UIUtils.createLabel("Create New Event", UIConstants.HEADER_FONT, AppColors.TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = UIUtils.createPanel(new GridBagLayout(), true);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Title field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(UIUtils.createLabel("Title:", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY), gbc);
        JTextField titleField = UIUtils.createRoundedTextField();
        gbc.gridx = 1;
        formPanel.add(titleField, gbc);

        // Description field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(UIUtils.createLabel("Description:", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY), gbc);
        JTextArea descriptionField = new JTextArea(3, 20);
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
        JScrollPane descriptionScroll = new JScrollPane(descriptionField);
        gbc.gridx = 1;
        formPanel.add(descriptionScroll, gbc);

        // Category field
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(UIUtils.createLabel("Category:", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY), gbc);
        JTextField categoryField = UIUtils.createRoundedTextField();
        gbc.gridx = 1;
        formPanel.add(categoryField, gbc);

        // Venue name field
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(UIUtils.createLabel("Venue Name:", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY), gbc);
        JTextField venueNameField = UIUtils.createRoundedTextField();
        gbc.gridx = 1;
        formPanel.add(venueNameField, gbc);

        // Venue capacity field
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(UIUtils.createLabel("Venue Capacity:", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY), gbc);
        JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        gbc.gridx = 1;
        formPanel.add(capacitySpinner, gbc);

        // Event date field
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(UIUtils.createLabel("Event Date:", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY), gbc);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(new JSpinner(new SpinnerDateModel()), "yyyy-MM-dd HH:mm");
        JSpinner dateSpinner = new JSpinner(new SpinnerDateModel());
        dateSpinner.setEditor(dateEditor);
        gbc.gridx = 1;
        formPanel.add(dateSpinner, gbc);

        // Registration deadline field
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(UIUtils.createLabel("Registration Deadline:", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY), gbc);
        JSpinner.DateEditor deadlineEditor = new JSpinner.DateEditor(new JSpinner(new SpinnerDateModel()), "yyyy-MM-dd HH:mm");
        JSpinner deadlineSpinner = new JSpinner(new SpinnerDateModel());
        deadlineSpinner.setEditor(deadlineEditor);
        gbc.gridx = 1;
        formPanel.add(deadlineSpinner, gbc);

        // Total slots field
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(UIUtils.createLabel("Total Slots:", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY), gbc);
        JSpinner slotsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 1000, 1));
        gbc.gridx = 1;
        formPanel.add(slotsSpinner, gbc);

        // Eligibility criteria field
        gbc.gridx = 0;
        gbc.gridy = 8;
        formPanel.add(UIUtils.createLabel("Eligibility Criteria:", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY), gbc);
        JTextArea eligibilityField = new JTextArea(3, 20);
        eligibilityField.setLineWrap(true);
        eligibilityField.setWrapStyleWord(true);
        JScrollPane eligibilityScroll = new JScrollPane(eligibilityField);
        gbc.gridx = 1;
        formPanel.add(eligibilityScroll, gbc);

        // Schedule field
        gbc.gridx = 0;
        gbc.gridy = 9;
        formPanel.add(UIUtils.createLabel("Schedule:", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY), gbc);
        JTextArea scheduleField = new JTextArea(3, 20);
        scheduleField.setLineWrap(true);
        scheduleField.setWrapStyleWord(true);
        JScrollPane scheduleScroll = new JScrollPane(scheduleField);
        gbc.gridx = 1;
        formPanel.add(scheduleScroll, gbc);

        // Create button
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(20, 5, 5, 5);
        GradientButton createButton = GradientButton.createPrimaryButton("Create Event");
        createButton.addActionListener(e -> {
            try {
                // Validate inputs
                if (titleField.getText().trim().isEmpty()) {
                    UIUtils.showError(this, "Please enter an event title");
                    return;
                }
                if (descriptionField.getText().trim().isEmpty()) {
                    UIUtils.showError(this, "Please enter an event description");
                    return;
                }
                if (categoryField.getText().trim().isEmpty()) {
                    UIUtils.showError(this, "Please enter a category");
                    return;
                }
                if (venueNameField.getText().trim().isEmpty()) {
                    UIUtils.showError(this, "Please enter a venue name");
                    return;
                }

                // Create event
                Event event = new Event(
                    titleField.getText().trim(),
                    descriptionField.getText().trim(),
                    ((Date) dateSpinner.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime(),
                    ((Date) deadlineSpinner.getValue()).toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime(),
                    venueNameField.getText().trim(),
                    (Integer) slotsSpinner.getValue(),
                    authController.getCurrentUser(),
                    categoryField.getText().trim()
                );

                // Set additional fields
                event.setEligibilityCriteria(eligibilityField.getText().trim());


                // Save event
                eventController.createEvent(
                    event.getTitle(),
                    event.getDescription(),
                    event.getEventDate(),
                    event.getRegistrationDeadline(),
                    event.getVenueName(),
                    event.getTotalSlots(),
                    event.getOrganizer(),
                    event.getCategory(),
                    null, // mainImage
                    null, // mainImageType
                    null, // additionalDocuments
                    null  // additionalDocumentsType
                );

                // Show success message
                JOptionPane.showMessageDialog(this,
                    "Event created successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

                // Clear form
                titleField.setText("");
                descriptionField.setText("");
                categoryField.setText("");
                venueNameField.setText("");
                capacitySpinner.setValue(1);
                dateSpinner.setValue(new Date());
                deadlineSpinner.setValue(new Date());
                slotsSpinner.setValue(1);
                eligibilityField.setText("");
                scheduleField.setText("");

                // Refresh dashboard
                loadDashboardData();
                contentLayout.show(contentPanel, "Dashboard");

            } catch (SQLException ex) {
                UIUtils.showError(this, "Error creating event: " + ex.getMessage());
            } catch (IllegalArgumentException ex) {
                UIUtils.showError(this, ex.getMessage());
            }
        });
        formPanel.add(createButton, gbc);

        // Add form panel to scroll pane
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

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
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppColors.BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = UIUtils.createLabel("Event Participants", UIConstants.TITLE_FONT, AppColors.TEXT_PRIMARY);
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"Event Name", "Participant Name", "Email", "Registration Date", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);

        try {
            List<Event> events = eventController.getEventsByOrganizer(authController.getCurrentUser().getId());
            for (Event event : events) {
                List<Registration> regs = registrationController.getEventRegistrations(event.getId());
                for (Registration reg : regs) {
                    tableModel.addRow(new Object[]{
                        event.getTitle(),
                        reg.getAttendee().getName(),
                        reg.getAttendee().getEmail(),
                        reg.getRegistrationDate().toString(),
                        reg.getStatus().name()
                    });
                }
            }
        } catch (Exception e) {
            UIUtils.showError(this, "Error loading participants: " + e.getMessage());
        }

        panel.add(new JScrollPane(new JTable(tableModel)), BorderLayout.CENTER);
        return panel;
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

    /**
     * Shows a dialog with event details using the EventDetailsPanel
     *
     * @param event The event to show details for
     */
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
            null,
            null,
            e -> dialog.dispose()
        );
        dialog.add(detailsPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    /**
     * Shows a dialog to edit an event
     *
     * @param event The event to edit
     */
    private void showEditEventDialog(Event event) {
        JDialog dialog = new JDialog(this, "Edit Event", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(800, 700);
        dialog.setLocationRelativeTo(this);

        CreateEventForm form = new CreateEventForm("Organizer", success -> {
            if (success) {
                // Refresh the dashboard and events table
                loadDashboardData();

                // Show confirmation message
                JOptionPane.showMessageDialog(
                    this,
                    "Event details have been successfully updated!",
                    "Changes Saved",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
            dialog.dispose();
        });

        // Set the form data with the event to edit
        form.setEventData(event);

        dialog.add(form, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private JPanel createMediaUploadPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppColors.BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = UIUtils.createLabel("Upload Media for Completed Events", UIConstants.TITLE_FONT, AppColors.TEXT_PRIMARY);
        panel.add(titleLabel, BorderLayout.NORTH);

        try {
            List<Event> completedEvents = eventController.getEventsByOrganizer(authController.getCurrentUser().getId())
                .stream().filter(e -> e.getStatus() == EventStatus.COMPLETED).collect(Collectors.toList());
            if (completedEvents.isEmpty()) {
                panel.add(UIUtils.createLabel("No completed events available for media upload.", UIConstants.BODY_FONT, AppColors.TEXT_SECONDARY), BorderLayout.CENTER);
            } else {
                JComboBox<Event> eventDropdown = new JComboBox<>(completedEvents.toArray(new Event[0]));
                panel.add(eventDropdown, BorderLayout.NORTH);
                // Add your MediaUploadPanel and logic here
                MediaUploadPanel mediaPanel = new MediaUploadPanel();
                panel.add(mediaPanel, BorderLayout.CENTER);
            }
        } catch (Exception e) {
            UIUtils.showError(this, "Error loading media upload panel: " + e.getMessage());
        }
        return panel;
    }

    /**
     * Loads dashboard data and updates the UI.
     */
    public void loadDashboardData() {
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
                .filter(e -> e.getStatus() == EventStatus.PENDING)
                .count();
            int approvedEvents = (int) events.stream()
                .filter(e -> e.getStatus() == EventStatus.APPROVED)
                .count();
            int rejectedEvents = (int) events.stream()
                .filter(e -> e.getStatus() == EventStatus.REJECTED)
                .count();
            int cancelledEvents = (int) events.stream()
                .filter(e -> e.getStatus() == EventStatus.CANCELLED)
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
            dashboardStatsPanel.add(createStatCard("Cancelled", String.valueOf(cancelledEvents), AppColors.TEXT_SECONDARY));
            dashboardStatsPanel.add(createStatCard("Total Participants", String.valueOf(totalParticipants), AppColors.PRIMARY));

            dashboardStatsPanel.revalidate();
            dashboardStatsPanel.repaint();

        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading dashboard data: " + e.getMessage());
        }
    }

    private void loadRecentEvents() {
        recentEventsPanel.removeAll();
        // Re-add the title and View All link
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel titleLabel = UIUtils.createLabel(
            "Recent Events",
            UIConstants.BODY_FONT_BOLD,
            AppColors.TEXT_PRIMARY
        );
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        titlePanel.add(titleLabel, BorderLayout.WEST);

        JLabel viewAllLabel = UIUtils.createLabel(
            "View All Events →",
            UIConstants.SMALL_FONT,
            AppColors.PRIMARY
        );
        viewAllLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        viewAllLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewAllLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                contentLayout.show(contentPanel, "My Events");
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                viewAllLabel.setText("<html><u>View All Events →</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                viewAllLabel.setText("View All Events →");
            }
        });
        titlePanel.add(viewAllLabel, BorderLayout.EAST);
        recentEventsPanel.add(titlePanel, BorderLayout.NORTH);

        // Create a new list panel to hold RecentEventItem components
        JPanel recentEventsList = new JPanel();
        recentEventsList.setLayout(new BoxLayout(recentEventsList, BoxLayout.Y_AXIS));
        recentEventsList.setOpaque(false);

        // Add scroll pane for the list
        JScrollPane listScrollPane = new JScrollPane(recentEventsList);
        listScrollPane.setBorder(BorderFactory.createEmptyBorder());
        listScrollPane.setOpaque(false);
        listScrollPane.getViewport().setOpaque(false);

        try {
            User currentUser = authController.getCurrentUser();
            if (currentUser != null) {
                List<Event> recentEvents = eventController.getRecentEventsByOrganizer(currentUser.getId(), 5);

                if (recentEvents.isEmpty()) {
                    JLabel noEventsLabel = UIUtils.createLabel(
                        "No recent events to display.",
                        UIConstants.BODY_FONT,
                        AppColors.TEXT_SECONDARY
                    );
                    noEventsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    noEventsLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
                    recentEventsList.add(noEventsLabel);
                } else {
                    for (Event event : recentEvents) {
                        RecentEventItem item = new RecentEventItem(event, e -> showEventDetails(event));
                        recentEventsList.add(item);
                        recentEventsList.add(Box.createVerticalStrut(10));
                    }
                }
            }
        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading recent events: " + e.getMessage());
            e.printStackTrace();
        }

        // Add the scroll pane to the recentEventsPanel CENTER position
        recentEventsPanel.add(listScrollPane, BorderLayout.CENTER);

        recentEventsPanel.revalidate();
        recentEventsPanel.repaint();
    }

    private Color getStatusColor(EventStatus status) {
        switch (status) {
            case PENDING:
                return AppColors.ACCENT_YELLOW;
            case APPROVED:
                return AppColors.ACCENT_GREEN;
            case REJECTED:
                return AppColors.ACCENT_RED;
            case CANCELLED:
                return AppColors.TEXT_SECONDARY;
            case COMPLETED:
                return AppColors.PRIMARY;
            default:
                return AppColors.TEXT_SECONDARY;
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

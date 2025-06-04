package screens;

import controllers.AuthController;
import controllers.EventController;
import controllers.RegistrationController;
import models.User;
import models.Event;
import models.Registration;
import java.awt.*;
import java.awt.event.ActionListener;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import utils.*;
import components.*;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import components.RoundedPanel;
import components.ModernStatCard;

/**
 * Dashboard screen for Attendees (Students)
 * Implements a modern UI with rounded corners and consistent sidebar
 */
@SuppressWarnings("unused")
public class AttendeeDashboardNew extends JFrame {
    private JPanel contentPanel;
    private CardLayout contentLayout;
    private SidebarPanel sidebarPanel;

    // Components that need to be accessed from multiple methods
    private CalendarPanel calendarPanel;
    private SearchResultsPanel searchResultsPanel;
    private EventController eventController;
    private RegistrationController registrationController;

    private enum TabInfo {
        HOME("🏠", "Home"),
        SEARCH("🔍", "Search"),
        CALENDAR("📅", "Calendar"),
        MY_EVENTS("⭐", "My Events"),
        PROFILE("👤", "Profile"),
        SETTINGS("⚙️", "Settings");

        final String icon;
        final String title;

        TabInfo(String icon, String title) {
            this.icon = icon;
            this.title = title;
        }
    }

    public AttendeeDashboardNew() {
        try {
            // Initialize controllers
            this.eventController = EventController.getInstance();
            this.registrationController = RegistrationController.getInstance();

            // Check session
            AuthController authController = AuthController.getInstance();
            if (!authController.isLoggedIn() || !authController.isAttendee()) {
                UIUtils.showError(this, "Access denied. Only attendees can access this dashboard.");
                dispose();
                new LoginScreen();
                return;
            }

            initializeFrame();
            setupUI();
            setVisible(true);
        } catch (SQLException e) {
            UIUtils.showError(this, "Error initializing dashboard: " + e.getMessage());
            dispose();
            try {
                new LoginScreen();
            } catch (SQLException ex) {
                UIUtils.showError(this, "Error opening login screen: " + ex.getMessage());
            }
        }
    }

    private void initializeFrame() {
        setTitle("Event Management System - Attendee Dashboard");
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
        String username = AuthController.getInstance().getCurrentUser().getName();
        HeaderPanel headerPanel = new HeaderPanel(username, "Attendee");
        SidebarPanel sidebarPanel = new SidebarPanel(contentLayout, contentPanel, username, "Attendee");

        // Add navigation buttons (now with emoji icons)
        JButton dashboardBtn = sidebarPanel.addNavButton("🏠 Dashboard", null, "Dashboard", true);
        JButton myEventsBtn = sidebarPanel.addNavButton("📅 My Events", null, "My Events", false);
        JButton profileBtn = sidebarPanel.addNavButton("👤 Profile", null, "Profile", false);
        sidebarPanel.addLogoutButton(e -> handleLogout());

        // Add content cards
        contentPanel.add(createDashboardContent(), "Dashboard");
        contentPanel.add(createMyEventsContent(), "My Events");
        contentPanel.add(new ProfilePanel(AuthController.getInstance().getCurrentUser(), true), "Profile");

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);

        // Use showScreen for navigation
        dashboardBtn.addActionListener(e -> showScreen("Dashboard"));
        myEventsBtn.addActionListener(e -> showScreen("My Events"));
        profileBtn.addActionListener(e -> showScreen("Profile"));

        showScreen("Dashboard");
    }

    private JPanel createSearchContent() {
        JPanel panel = UIUtils.createPanel(new BorderLayout(), true);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = UIUtils.createLabel(
            "Search Events",
            UIConstants.TITLE_FONT,
            AppColors.TEXT_PRIMARY
        );
        panel.add(titleLabel, BorderLayout.NORTH);

        // Search panel
        JPanel searchPanel = UIUtils.createPanel(new BorderLayout(), false);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Search field
        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(300, 40));
        searchField.setFont(UIConstants.BODY_FONT);
        searchPanel.add(searchField, BorderLayout.CENTER);

        // Search button
        JButton searchButton = UIUtils.createButton(
            "Search",
            null,
            UIUtils.ButtonType.PRIMARY,
            UIUtils.ButtonSize.SMALL
        );
        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();
            if (!query.isEmpty()) {
                try {
                    List<Event> results = eventController.searchEvents(query);
                    String[][] eventData = results.stream()
                        .map(event -> new String[]{
                            String.valueOf(event.getId()),
                            event.getTitle(),
                            event.getEventDate() != null ? event.getEventDate().toString() : "N/A",
                            event.getVenueName() != null ? event.getVenueName() : "N/A",
                            event.getDescription() != null ? event.getDescription() : "N/A",
                            String.valueOf(event.getTotalSlots())
                        })
                        .toArray(String[][]::new);
                    searchResultsPanel.setEvents(eventData);
                } catch (SQLException ex) {
                    UIUtils.showError(this, "Error searching events: " + ex.getMessage());
                }
            }
        });
        searchPanel.add(searchButton, BorderLayout.EAST);

        panel.add(searchPanel, BorderLayout.CENTER);

        // Results panel
        searchResultsPanel = new SearchResultsPanel(event -> {
            try {
                Event selectedEvent = eventController.getEvent(Integer.parseInt(event[0]));
                if (selectedEvent != null) {
                    EventDetailsScreen screen = new EventDetailsScreen(
                        selectedEvent.getId(),
                        AuthController.getInstance().getCurrentUser()
                    );
                    screen.setVisible(true);
                }
            } catch (SQLException ex) {
                UIUtils.showError(this, "Error loading event details: " + ex.getMessage());
            }
        });
        panel.add(searchResultsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCalendarContent() {
        JPanel panel = UIUtils.createPanel(new BorderLayout(), true);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = UIUtils.createLabel(
            "Event Calendar",
            UIConstants.TITLE_FONT,
            AppColors.TEXT_PRIMARY
        );
        panel.add(titleLabel, BorderLayout.NORTH);

        // Create calendar panel
        calendarPanel = new CalendarPanel();

        try {
            User currentUser = AuthController.getInstance().getCurrentUser();
            List<Event> allEvents = eventController.getAllEvents();
            List<Registration> myRegistrations = registrationController.getUserRegistrations(currentUser.getId());

            // Convert event data to calendar events
            List<CalendarPanel.CalendarEvent> calendarEvents = new ArrayList<>();

            for (Event event : allEvents) {
                boolean isRegistered = myRegistrations.stream()
                    .anyMatch(reg -> reg.getEvent().getId() == event.getId());

                String eventDetails = event.getTitle() + "\n" + event.getVenueName();

                Date eventDate = event.getEventDate() != null ?
                    java.util.Date.from(event.getEventDate().atZone(java.time.ZoneId.systemDefault()).toInstant()) :
                    null;

                calendarEvents.add(new CalendarPanel.CalendarEvent(
                    String.valueOf(event.getId()),
                    event.getTitle(),
                    eventDate,
                    eventDate, // startTime
                    eventDate != null ? new Date(eventDate.getTime() + 7200000) : null, // endTime (2 hours after start)
                    event.getVenueName(),
                    event.getCategory() != null ? event.getCategory() : "Other",
                    event.getDescription() != null ? event.getDescription() : "",
                    isRegistered
                ));
            }

            calendarPanel.setEvents(calendarEvents);

        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading calendar events: " + e.getMessage());
        }

        JScrollPane scrollPane = new JScrollPane(calendarPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // Remove default border
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createMyEventsContent() {
        JPanel panel = UIUtils.createPanel(new BorderLayout(20, 20), true);
        panel.setBackground(AppColors.BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header section
        JPanel headerSection = UIUtils.createPanel(new BorderLayout(), false);
        headerSection.setOpaque(false);

        JLabel titleLabel = UIUtils.createLabel(
            "My Registered Events",
            UIConstants.HEADER_FONT,
            AppColors.TEXT_PRIMARY
        );
        headerSection.add(titleLabel, BorderLayout.WEST);

        panel.add(headerSection, BorderLayout.NORTH);

        // Stats Panel
        JPanel statsPanel = UIUtils.createPanel(new GridLayout(1, 4, 20, 0), false);
        statsPanel.setOpaque(false);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        try {
            User currentUser = AuthController.getInstance().getCurrentUser();
            List<Registration> myRegistrations = registrationController.getUserRegistrations(currentUser.getId());
            List<Event> allUpcomingEvents = eventController.getUpcomingEvents();

            int totalRegistrations = myRegistrations.size();

            // Filter registrations to find upcoming events that are not cancelled or rejected
            int upcomingEvents = (int) myRegistrations.stream()
                .filter(r -> {
                    try {
                        Event event = eventController.getEvent(r.getEvent().getId());
                        return event != null &&
                                (event.getEventDate().toLocalDate().isAfter(LocalDateTime.now().toLocalDate()) ||
                                 (event.getEventDate().toLocalDate().isEqual(LocalDateTime.now().toLocalDate()) && event.getEventDate().isAfter(LocalDateTime.now()))) &&
                                r.getStatus() != Registration.Status.CANCELLED &&
                                r.getStatus() != Registration.Status.REJECTED;
                    } catch (SQLException e) {
                        return false;
                    }
                }).count();

            int completedEvents = (int) myRegistrations.stream()
                .filter(r -> {
                    try {
                        Event event = eventController.getEvent(r.getEvent().getId());
                        return event != null && event.getEventDate().isBefore(LocalDateTime.now()) &&
                               r.getStatus() != Registration.Status.CANCELLED &&
                               r.getStatus() != Registration.Status.REJECTED;
                    } catch (SQLException e) {
                        return false;
                    }
                }).count();
            int pendingApprovals = (int) myRegistrations.stream()
                .filter(r -> r.getStatus() == Registration.Status.PENDING).count();

            statsPanel.add(createStatCard("TOTAL REGISTERED", String.valueOf(totalRegistrations), AppColors.PRIMARY));
            statsPanel.add(createStatCard("UPCOMING EVENTS", String.valueOf(upcomingEvents), AppColors.ACCENT_GREEN));
            statsPanel.add(createStatCard("COMPLETED EVENTS", String.valueOf(completedEvents), AppColors.ACCENT));
            statsPanel.add(createStatCard("PENDING APPROVALS", String.valueOf(pendingApprovals), AppColors.WARNING));
        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading registration statistics: " + e.getMessage());
        }

        panel.add(statsPanel, BorderLayout.NORTH);

        // Main content - Events Table
        RoundedPanel contentPanel = new RoundedPanel(new BorderLayout(), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        contentPanel.setBorder(UIUtils.createRoundedBorder(AppColors.BORDER, UIConstants.CORNER_RADIUS_MEDIUM, 1));

        // Create table model
        String[] columns = {"Event Title", "Date", "Venue", "Status", "Registration Status", "Registration Date", "Actions"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only actions column is editable
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
        eventsTable.getColumnModel().getColumn(0).setPreferredWidth(200); // Event Title
        eventsTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Date
        eventsTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Venue
        eventsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Status
        eventsTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Registration Status
        eventsTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Registration Date
        eventsTable.getColumnModel().getColumn(6).setPreferredWidth(150); // Actions

        // Status column renderer for Event Status
        eventsTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setForeground(getEventStatusColor((String) value));
                return label;
            }
        });

        // Registration Status column renderer
        eventsTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setForeground(getRegistrationStatusColor((String) value));
                return label;
            }
        });

        // Actions column renderer and editor
        eventsTable.getColumnModel().getColumn(6).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
                buttonPanel.setOpaque(false);

                JButton viewButton = UIUtils.createButton("View", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.SMALL);
                buttonPanel.add(viewButton);

                // Add cancel button only for pending/approved registrations
                String regStatus = (String) table.getValueAt(row, 4);
                if ("PENDING".equals(regStatus) || "APPROVED".equals(regStatus)) {
                    JButton cancelButton = UIUtils.createButton("Cancel", null, UIUtils.ButtonType.ERROR, UIUtils.ButtonSize.SMALL);
                    buttonPanel.add(cancelButton);
                }

                return buttonPanel;
            }
        });

        eventsTable.getColumnModel().getColumn(6).setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private JPanel buttonPanel;
            private JButton viewButton;
            private JButton cancelButton;

            {
                buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
                buttonPanel.setOpaque(false);

                viewButton = UIUtils.createButton("View", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.SMALL);
                viewButton.addActionListener(e -> {
                    int row = eventsTable.getSelectedRow();
                    if (row >= 0) {
                        try {
                            String eventTitle = (String) eventsTable.getValueAt(row, 0);
                            // Find event by title
                            User currentUser = AuthController.getInstance().getCurrentUser();
                            List<Registration> registrations = registrationController.getUserRegistrations(currentUser.getId());
                            for (Registration reg : registrations) {
                                Event event = eventController.getEvent(reg.getEvent().getId());
                                if (event != null && event.getTitle().equals(eventTitle)) {
                                    showEventDetailsDialog(event);
                                    break;
                                }
                            }
                        } catch (SQLException ex) {
                            UIUtils.showError(panel, "Error loading event details: " + ex.getMessage());
                        }
                    }
                    fireEditingStopped();
                });

                cancelButton = UIUtils.createButton("Cancel", null, UIUtils.ButtonType.ERROR, UIUtils.ButtonSize.SMALL);
                cancelButton.addActionListener(e -> {
                    int row = eventsTable.getSelectedRow();
                    if (row >= 0) {
                        try {
                            String eventTitle = (String) eventsTable.getValueAt(row, 0);
                            // Find registration by event title
                            User currentUser = AuthController.getInstance().getCurrentUser();
                            List<Registration> registrations = registrationController.getUserRegistrations(currentUser.getId());
                            for (Registration reg : registrations) {
                                Event event = eventController.getEvent(reg.getEvent().getId());
                                if (event != null && event.getTitle().equals(eventTitle)) {
                                    cancelRegistration(reg.getId());
                                    break;
                                }
                            }
                        } catch (SQLException ex) {
                            UIUtils.showError(panel, "Error cancelling registration: " + ex.getMessage());
                        }
                    }
                    fireEditingStopped();
                });
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value,
                    boolean isSelected, int row, int column) {
                buttonPanel.removeAll();
                buttonPanel.add(viewButton);

                // Add cancel button only for pending/approved registrations
                String regStatus = (String) table.getValueAt(row, 4);
                if ("PENDING".equals(regStatus) || "APPROVED".equals(regStatus)) {
                    buttonPanel.add(cancelButton);
                }

                return buttonPanel;
            }

            @Override
            public Object getCellEditorValue() {
                return "Actions";
            }
        });

        // Load events data
        loadMyEventsData(tableModel);

        // Add search and filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setOpaque(false);

        JTextField searchField = UIUtils.createRoundedTextField();
        searchField.setPreferredSize(new Dimension(200, 30));
        searchField.setToolTipText("Search by event title");

        JButton searchButton = UIUtils.createButton("Search", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.SMALL);

        JComboBox<String> statusFilter = UIUtils.createRoundedComboBox(new String[]{"All", "PENDING", "APPROVED", "CANCELLED"});
        statusFilter.setPreferredSize(new Dimension(150, 30));

        // Add action listeners for search and filter
        searchButton.addActionListener(e -> {
            String searchText = searchField.getText().toLowerCase();
            String selectedStatus = (String) statusFilter.getSelectedItem();
            filterMyEvents(tableModel, searchText, selectedStatus);
        });

        statusFilter.addActionListener(e -> {
            String searchText = searchField.getText().toLowerCase();
            String selectedStatus = (String) statusFilter.getSelectedItem();
            filterMyEvents(tableModel, searchText, selectedStatus);
        });

        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        filterPanel.add(new JLabel("Registration Status:"));
        filterPanel.add(statusFilter);

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

    private JPanel createEventSummaryPanel(Event event, Registration registration) {
        JPanel panel = UIUtils.createPanel(new BorderLayout(10, 0), true);
        panel.setBackground(AppColors.BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));

        // Event Info
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(AppColors.BACKGROUND_LIGHT);

        JLabel titleLabel = UIUtils.createLabel(
            event.getTitle(),
            UIConstants.BODY_FONT.deriveFont(Font.BOLD, 14f),
            AppColors.TEXT_PRIMARY
        );
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String dateStr = "N/A";
        if (event.getEventDate() != null) {
            dateStr = event.getEventDate().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        }
        JLabel dateLabel = UIUtils.createLabel(
            "Date: " + dateStr,
            UIConstants.BODY_FONT,
            AppColors.TEXT_SECONDARY
        );
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel venueLabel = UIUtils.createLabel(
            "Venue: " + (event.getVenueName() != null ? event.getVenueName() : "N/A"),
            UIConstants.BODY_FONT,
            AppColors.TEXT_SECONDARY
        );
        venueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        infoPanel.add(titleLabel);
        infoPanel.add(dateLabel);
        infoPanel.add(venueLabel);

        panel.add(infoPanel, BorderLayout.CENTER);

        // Actions
        JPanel actionsPanel = UIUtils.createPanel(new FlowLayout(FlowLayout.RIGHT), false);
        actionsPanel.setBackground(AppColors.BACKGROUND_LIGHT);

        JButton viewDetailsButton = UIUtils.createButton(
            "View Details",
            null,
            UIUtils.ButtonType.SECONDARY,
            UIUtils.ButtonSize.SMALL
        );
        viewDetailsButton.addActionListener(e -> {
            try {
                Event selectedEvent = eventController.getEvent(event.getId());
                if (selectedEvent != null) {
                    JDialog dialog = new JDialog(this, "Event Details", true);
                    dialog.setSize(700, 600);
                    dialog.setLocationRelativeTo(this);
                    dialog.setLayout(new BorderLayout());
                    dialog.add(new components.EventDetailsPanel(selectedEvent), BorderLayout.CENTER);
                    JButton closeBtn = UIUtils.createButton("Close", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.NORMAL);
                    closeBtn.addActionListener(ev -> dialog.dispose());
                    JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    btnPanel.add(closeBtn);
                    dialog.add(btnPanel, BorderLayout.SOUTH);
                    dialog.setVisible(true);
                }
            } catch (Exception ex) {
                UIUtils.showError(this, "Error loading event details: " + ex.getMessage());
            }
        });
        actionsPanel.add(viewDetailsButton);

        if (registration.getStatus() == Registration.Status.PENDING || registration.getStatus() == Registration.Status.APPROVED) {
            JButton cancelRegistrationButton = UIUtils.createButton(
                "Cancel Registration",
                null,
                UIUtils.ButtonType.SECONDARY,
                UIUtils.ButtonSize.SMALL
            );
            cancelRegistrationButton.addActionListener(e -> cancelRegistration(registration.getId()));
            actionsPanel.add(cancelRegistrationButton);
        }

        panel.add(actionsPanel, BorderLayout.EAST);

        return panel;
    }

    private void cancelRegistration(int registrationId) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel this registration?",
            "Confirm Cancellation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                registrationController.cancelRegistration(registrationId);
                UIUtils.showSuccess(this, "Registration cancelled successfully.");
                // Refresh My Events list
                contentPanel.remove(findComponentByName(contentPanel, "My Events"));
                contentPanel.add(createMyEventsContent(), "My Events");
                contentLayout.show(contentPanel, "My Events");
            } catch (SQLException e) {
                UIUtils.showError(this, "Error cancelling registration: " + e.getMessage());
            }
        }
    }

    // Helper method to load events data into the table
    private void loadMyEventsData(DefaultTableModel tableModel) {
        try {
            User currentUser = AuthController.getInstance().getCurrentUser();
            List<Registration> myRegistrations = registrationController.getUserRegistrations(currentUser.getId());
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            DateTimeFormatter regDateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");

            // Clear existing data
            tableModel.setRowCount(0);

            for (Registration registration : myRegistrations) {
                try {
                    Event event = eventController.getEvent(registration.getEvent().getId());
                    if (event != null) {
                        Object[] rowData = {
                            event.getTitle(),
                            event.getEventDate() != null ? event.getEventDate().format(dateFormat) : "N/A",
                            event.getVenueName() != null ? event.getVenueName() : "N/A",
                            event.getStatus() != null ? event.getStatus().toString() : "N/A",
                            registration.getStatus() != null ? registration.getStatus().toString() : "N/A",
                            registration.getRegistrationDate() != null ?
                                registration.getRegistrationDate().format(regDateFormat) : "N/A",
                            "Actions"
                        };
                        tableModel.addRow(rowData);
                    }
                } catch (SQLException e) {
                    System.err.println("Error loading event for registration: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading registered events: " + e.getMessage());
        }
    }

    // Helper method to filter events in the table
    private void filterMyEvents(DefaultTableModel tableModel, String searchText, String selectedStatus) {
        try {
            User currentUser = AuthController.getInstance().getCurrentUser();
            List<Registration> myRegistrations = registrationController.getUserRegistrations(currentUser.getId());
            DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            DateTimeFormatter regDateFormat = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");

            // Clear existing data
            tableModel.setRowCount(0);

            for (Registration registration : myRegistrations) {
                Event event = eventController.getEvent(registration.getEvent().getId());
                if (event != null) {
                    // Apply filters
                    boolean matchesSearch = searchText.isEmpty() ||
                        event.getTitle().toLowerCase().contains(searchText) ||
                        event.getVenueName().toLowerCase().contains(searchText);

                    boolean matchesStatus = selectedStatus.equals("All") ||
                        registration.getStatus().toString().equals(selectedStatus);

                    if (matchesSearch && matchesStatus) {
                        Object[] rowData = {
                            event.getTitle(),
                            event.getEventDate() != null ? event.getEventDate().format(dateFormat) : "N/A",
                            event.getVenueName(),
                            event.getStatus().toString(),
                            registration.getStatus().toString(),
                            registration.getRegistrationDate() != null ?
                                registration.getRegistrationDate().format(regDateFormat) : "N/A",
                            "Actions"
                        };
                        tableModel.addRow(rowData);
                    }
                }
            }
        } catch (SQLException e) {
            UIUtils.showError(this, "Error filtering events: " + e.getMessage());
        }
    }

    // Helper method to get color for event status
    private Color getEventStatusColor(String status) {
        switch (status) {
            case "APPROVED":
                return AppColors.SUCCESS;
            case "PENDING":
                return AppColors.WARNING;
            case "REJECTED":
                return AppColors.ERROR;
            case "CANCELLED":
                return AppColors.TEXT_SECONDARY;
            case "COMPLETED":
                return AppColors.PRIMARY;
            default:
                return AppColors.TEXT_PRIMARY;
        }
    }

    // Helper method to get color for registration status
    private Color getRegistrationStatusColor(String status) {
        switch (status) {
            case "APPROVED":
                return AppColors.SUCCESS;
            case "PENDING":
                return AppColors.WARNING;
            case "CANCELLED":
                return AppColors.ERROR;
            case "REJECTED":
                return AppColors.ERROR;
            default:
                return AppColors.TEXT_PRIMARY;
        }
    }

    // Helper method to show event details dialog
    private void showEventDetailsDialog(Event event) {
        try {
            // Fetch the latest event data
            Event latestEvent = eventController.getEvent(event.getId());
            if (latestEvent == null) {
                UIUtils.showError(this, "Error loading event details: Event not found.");
                return;
            }

            JDialog dialog = new JDialog(this, "Event Details", true);
            dialog.setSize(700, 600);
            dialog.setLocationRelativeTo(this);
            dialog.setLayout(new BorderLayout());
            dialog.add(new components.EventDetailsPanel(latestEvent), BorderLayout.CENTER);

            JButton closeBtn = UIUtils.createButton("Close", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.NORMAL);
            closeBtn.addActionListener(ev -> dialog.dispose());
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            btnPanel.add(closeBtn);
            dialog.add(btnPanel, BorderLayout.SOUTH);
            dialog.setVisible(true);
        } catch (SQLException ex) {
            UIUtils.showError(this, "Error loading event details: " + ex.getMessage());
        }
    }

    // Helper method to create stat cards
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

     // Helper method to find a component by its name (card identifier)
    private Component findComponentByName(Container container, String name) {
        for (Component comp : container.getComponents()) {
            if (comp.getName() != null && comp.getName().equals(name)) {
                return comp;
            }
        }
        return null;
    }

    private JScrollPane createDashboardContent() {
        JPanel panel = UIUtils.createPanel(new BorderLayout(0, 25), true);
        panel.setBackground(AppColors.BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        // --- Hero Section with Gradient Background ---
        JPanel heroSection = createHeroSection();
        panel.add(heroSection, BorderLayout.NORTH);

        // --- Main Dashboard Content ---
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);

        // --- Quick Stats Section ---
        JPanel quickStatsSection = createQuickStatsSection();
        mainContent.add(quickStatsSection);
        mainContent.add(Box.createVerticalStrut(30));

        // --- Quick Actions Section ---
        JPanel quickActionsSection = createQuickActionsSection();
        mainContent.add(quickActionsSection);
        mainContent.add(Box.createVerticalStrut(30));

        // --- Upcoming Events Section ---
        JPanel upcomingEventsSection = createUpcomingEventsSection();
        mainContent.add(upcomingEventsSection);
        mainContent.add(Box.createVerticalStrut(30));

        // --- Recent Activity Section ---
        JPanel recentActivitySection = createRecentActivitySection();
        mainContent.add(recentActivitySection);

        panel.add(mainContent, BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        return scrollPane;
    }

    private JPanel createHeroSection() {
        // Create gradient panel
        JPanel heroPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Create gradient from primary to secondary color
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(74, 144, 226),
                    getWidth(), getHeight(), new Color(80, 170, 200)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        heroPanel.setPreferredSize(new Dimension(0, 180));
        heroPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Left side - Welcome message
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        String userName = "Guest";
        try {
            User currentUser = AuthController.getInstance().getCurrentUser();
            if (currentUser != null) userName = currentUser.getName();
        } catch (SQLException e) {
            System.err.println("Error getting current user: " + e.getMessage());
        }

        JLabel greetingLabel = new JLabel("Hello, " + userName + "!");
        greetingLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        greetingLabel.setForeground(Color.WHITE);
        greetingLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dateLabel = new JLabel(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy").format(ZonedDateTime.now()));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        dateLabel.setForeground(new Color(255, 255, 255, 200));
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        dateLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 15, 0));

        JLabel motivationLabel = new JLabel("Ready to discover amazing events?");
        motivationLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        motivationLabel.setForeground(new Color(255, 255, 255, 230));
        motivationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(greetingLabel);
        leftPanel.add(dateLabel);
        leftPanel.add(motivationLabel);

        // Right side - Quick action button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        JButton exploreButton = new JButton(" Explore Events");
        exploreButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        exploreButton.setForeground(new Color(74, 144, 226));
        exploreButton.setBackground(Color.WHITE);
        exploreButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        exploreButton.setFocusPainted(false);
        exploreButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exploreButton.addActionListener(e -> showScreen("My Events"));

        // Add hover effect
        exploreButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                exploreButton.setBackground(new Color(245, 245, 245));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                exploreButton.setBackground(Color.WHITE);
            }
        });

        rightPanel.add(exploreButton);

        heroPanel.add(leftPanel, BorderLayout.WEST);
        heroPanel.add(rightPanel, BorderLayout.EAST);

        return heroPanel;
    }

    private JPanel createQuickStatsSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setOpaque(false);

        JLabel sectionTitle = new JLabel("Your Activity");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        sectionTitle.setForeground(AppColors.TEXT_PRIMARY);
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        section.add(sectionTitle, BorderLayout.NORTH);

        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 20, 0));
        statsGrid.setOpaque(false);

        try {
            User currentUser = AuthController.getInstance().getCurrentUser();
            List<Registration> myRegistrations = registrationController.getUserRegistrations(currentUser.getId());
            List<Event> allUpcomingEvents = eventController.getUpcomingEvents();

            int totalRegistrations = myRegistrations.size();

            // Filter registrations to find upcoming events that are not cancelled or rejected
            int upcomingEvents = (int) myRegistrations.stream()
                .filter(r -> {
                    try {
                        Event event = eventController.getEvent(r.getEvent().getId());
                        return event != null &&
                                (event.getEventDate().toLocalDate().isAfter(LocalDateTime.now().toLocalDate()) ||
                                 (event.getEventDate().toLocalDate().isEqual(LocalDateTime.now().toLocalDate()) && event.getEventDate().isAfter(LocalDateTime.now()))) &&
                                r.getStatus() != Registration.Status.CANCELLED &&
                                r.getStatus() != Registration.Status.REJECTED;
                    } catch (SQLException e) {
                        return false;
                    }
                }).count();

            int completedEvents = (int) myRegistrations.stream()
                .filter(r -> {
                    try {
                        Event event = eventController.getEvent(r.getEvent().getId());
                        return event != null && event.getEventDate().isBefore(LocalDateTime.now()) &&
                               r.getStatus() != Registration.Status.CANCELLED &&
                               r.getStatus() != Registration.Status.REJECTED;
                    } catch (SQLException e) {
                        return false;
                    }
                }).count();
            int pendingApprovals = (int) myRegistrations.stream()
                .filter(r -> r.getStatus() == Registration.Status.PENDING).count();

            // Create modern stat cards with different styles
            ModernStatCard totalCard = new ModernStatCard("🎯", "Total Events", String.valueOf(totalRegistrations),
                "All registered events", new Color(74, 144, 226), null, ModernStatCard.CardStyle.DEFAULT, ModernStatCard.CardSize.MEDIUM);

            ModernStatCard upcomingCard = new ModernStatCard("⏰", "Upcoming", String.valueOf(upcomingEvents),
                "Events to attend", new Color(52, 168, 83), new Color(34, 139, 34), ModernStatCard.CardStyle.GRADIENT, ModernStatCard.CardSize.MEDIUM);

            ModernStatCard completedCard = new ModernStatCard("✅", "Completed", String.valueOf(completedEvents),
                "Events attended", new Color(251, 188, 5), new Color(255, 165, 0), ModernStatCard.CardStyle.ELEVATED, ModernStatCard.CardSize.MEDIUM);

            ModernStatCard pendingCard = new ModernStatCard("⏳", "Pending", String.valueOf(pendingApprovals),
                "Awaiting approval", new Color(234, 67, 53), null, ModernStatCard.CardStyle.OUTLINED, ModernStatCard.CardSize.MEDIUM);

            // Make cards clickable for navigation
            totalCard.setClickable(true, () -> showScreen("My Events"));
            upcomingCard.setClickable(true, () -> showScreen("My Events"));
            completedCard.setClickable(true, () -> showScreen("My Events"));
            pendingCard.setClickable(true, () -> showScreen("My Events"));

            statsGrid.add(totalCard);
            statsGrid.add(upcomingCard);
            statsGrid.add(completedCard);
            statsGrid.add(pendingCard);

        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading statistics: " + e.getMessage());
        }

        section.add(statsGrid, BorderLayout.CENTER);
        return section;
    }

    private JPanel createQuickActionsSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setOpaque(false);

        JLabel sectionTitle = new JLabel("Quick Actions");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        sectionTitle.setForeground(AppColors.TEXT_PRIMARY);
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        section.add(sectionTitle, BorderLayout.NORTH);

        JPanel actionsGrid = new JPanel(new GridLayout(1, 3, 20, 0));
        actionsGrid.setOpaque(false);

        // Browse Events Action
        JPanel browseAction = createActionCard(
            "🔍", "Browse Events", "Discover new events to join",
            new Color(74, 144, 226), e -> showScreen("My Events")
        );

        // My Events Action
        JPanel myEventsAction = createActionCard(
            "📅", "My Events", "View your registered events",
            new Color(52, 168, 83), e -> showScreen("My Events")
        );

        // Profile Action
        JPanel profileAction = createActionCard(
            "👤", "My Profile", "Update your profile information",
            new Color(251, 188, 5), e -> showScreen("Profile")
        );

        actionsGrid.add(browseAction);
        actionsGrid.add(myEventsAction);
        actionsGrid.add(profileAction);

        section.add(actionsGrid, BorderLayout.CENTER);
        return section;
    }

    private JPanel createActionCard(String icon, String title, String description, Color color, ActionListener action) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw rounded rectangle background
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Draw subtle border
                g2d.setColor(new Color(230, 230, 230));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));
        card.setPreferredSize(new Dimension(250, 140));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(new Color(248, 249, 250));
                card.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(Color.WHITE);
                card.repaint();
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                action.actionPerformed(null);
            }
        });

        // Content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(color);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(new Color(100, 100, 100));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        descLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        content.add(iconLabel);
        content.add(titleLabel);
        content.add(descLabel);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createUpcomingEventsSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setOpaque(false);

        // Section header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel sectionTitle = new JLabel("Upcoming Events");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        sectionTitle.setForeground(AppColors.TEXT_PRIMARY);
        headerPanel.add(sectionTitle, BorderLayout.WEST);

        JButton viewAllButton = new JButton("View All →");
        viewAllButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        viewAllButton.setForeground(new Color(74, 144, 226));
        viewAllButton.setBackground(Color.WHITE);
        viewAllButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        viewAllButton.setFocusPainted(false);
        viewAllButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewAllButton.addActionListener(e -> showScreen("My Events"));
        headerPanel.add(viewAllButton, BorderLayout.EAST);

        section.add(headerPanel, BorderLayout.NORTH);
        section.add(Box.createVerticalStrut(15), BorderLayout.CENTER);

        // Events grid
        JPanel eventsGrid = new JPanel(new GridLayout(1, 3, 20, 0));
        eventsGrid.setOpaque(false);

        try {
            List<Event> allEvents = eventController.getUpcomingEvents();
            User user = AuthController.getInstance().getCurrentUser();
            List<Registration> regs = registrationController.getUserRegistrations(user.getId());
            Set<Integer> registeredEventIds = regs.stream().map(r -> r.getEvent().getId()).collect(Collectors.toSet());

            List<Event> availableEvents = allEvents.stream()
                .filter(event -> !registeredEventIds.contains(event.getId()))
                .limit(3)
                .collect(Collectors.toList());

            if (availableEvents.isEmpty()) {
                JPanel emptyPanel = new JPanel(new BorderLayout());
                emptyPanel.setOpaque(false);
                JLabel emptyLabel = new JLabel("<html><center>🎉<br><br>No new events available<br>Check back later for updates!</center></html>");
                emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                emptyLabel.setForeground(new Color(150, 150, 150));
                emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
                emptyPanel.add(emptyLabel, BorderLayout.CENTER);
                eventsGrid.add(emptyPanel);
            } else {
                for (Event event : availableEvents) {
                    eventsGrid.add(createCompactEventCard(event));
                }
                // Fill remaining slots with empty panels if needed
                while (eventsGrid.getComponentCount() < 3) {
                    eventsGrid.add(new JPanel());
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading upcoming events: " + e.getMessage());
        }

        section.add(eventsGrid, BorderLayout.SOUTH);
        return section;
    }

    private JPanel createCompactEventCard(Event event) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2d.setColor(new Color(230, 230, 230));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setPreferredSize(new Dimension(280, 180));

        // Content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // Event title
        JLabel titleLabel = new JLabel(event.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(AppColors.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Event date
        String dateStr = event.getEventDate() != null ?
            event.getEventDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) : "TBD";
        JLabel dateLabel = new JLabel("📅 " + dateStr);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(100, 100, 100));
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Event venue
        JLabel venueLabel = new JLabel("📍 " + event.getVenueName());
        venueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        venueLabel.setForeground(new Color(100, 100, 100));
        venueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Register button
        JButton registerBtn = UIUtils.createButton(
            "Register",
            null,
            UIUtils.ButtonType.PRIMARY,
            UIUtils.ButtonSize.SMALL
        );
        registerBtn.addActionListener(e -> {
            try {
                User user = AuthController.getInstance().getCurrentUser();
                registrationController.registerForEvent(user, event.getId());
                UIUtils.showSuccess(this, "Successfully registered for " + event.getTitle() + "!");
                // Refresh dashboard
                contentLayout.show(contentPanel, "Dashboard");
            } catch (Exception ex) {
                UIUtils.showError(this, "Registration failed: " + ex.getMessage());
            }
        });

        // View Details button
        JButton viewDetailsBtn = UIUtils.createButton(
            "View Details",
            null,
            UIUtils.ButtonType.SECONDARY,
            UIUtils.ButtonSize.SMALL
        );
        viewDetailsBtn.addActionListener(e -> {
            showEventDetailsDialog(event);
        });

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(viewDetailsBtn);
        buttonPanel.add(registerBtn);

        content.add(titleLabel);
        content.add(Box.createVerticalStrut(8));
        content.add(dateLabel);
        content.add(Box.createVerticalStrut(4));
        content.add(venueLabel);
        content.add(Box.createVerticalStrut(15));
        content.add(buttonPanel);

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel createRecentActivitySection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setOpaque(false);

        JLabel sectionTitle = new JLabel("Recent Activity");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        sectionTitle.setForeground(AppColors.TEXT_PRIMARY);
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        section.add(sectionTitle, BorderLayout.NORTH);

        // Activity list panel
        JPanel activityPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                g2d.setColor(new Color(230, 230, 230));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };
        activityPanel.setOpaque(false);
        activityPanel.setLayout(new BoxLayout(activityPanel, BoxLayout.Y_AXIS));
        activityPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        try {
            User currentUser = AuthController.getInstance().getCurrentUser();
            List<Registration> recentRegistrations = registrationController.getUserRegistrations(currentUser.getId())
                .stream()
                .sorted((r1, r2) -> r2.getRegistrationDate().compareTo(r1.getRegistrationDate()))
                .limit(5)
                .collect(Collectors.toList());

            if (recentRegistrations.isEmpty()) {
                JLabel emptyLabel = new JLabel("No recent activity");
                emptyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                emptyLabel.setForeground(new Color(150, 150, 150));
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                activityPanel.add(emptyLabel);
            } else {
                for (Registration reg : recentRegistrations) {
                    try {
                        Event event = eventController.getEvent(reg.getEvent().getId());
                        if (event != null) {
                            activityPanel.add(createActivityItem(reg, event));
                            activityPanel.add(Box.createVerticalStrut(10));
                        }
                    } catch (SQLException e) {
                        System.err.println("Error loading event for registration: " + e.getMessage());
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading recent activity: " + e.getMessage());
            JLabel errorLabel = new JLabel("Error loading activity data");
            errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            errorLabel.setForeground(AppColors.ERROR);
            errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            activityPanel.add(errorLabel);
        }

        section.add(activityPanel, BorderLayout.CENTER);
        return section;
    }

    private JPanel createActivityItem(Registration registration, Event event) {
        JPanel item = new JPanel(new BorderLayout());
        item.setOpaque(false);
        item.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        // Status icon
        String statusIcon = getStatusIcon(registration.getStatus());
        JLabel iconLabel = new JLabel(statusIcon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        // Activity text
        String activityText = String.format("Registered for %s", event.getTitle());
        JLabel textLabel = new JLabel(activityText);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        textLabel.setForeground(AppColors.TEXT_PRIMARY);

        // Date
        String dateStr = registration.getRegistrationDate() != null ?
            registration.getRegistrationDate().format(DateTimeFormatter.ofPattern("MMM dd")) : "";
        JLabel dateLabel = new JLabel(dateStr);
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dateLabel.setForeground(new Color(150, 150, 150));

        item.add(iconLabel, BorderLayout.WEST);
        item.add(textLabel, BorderLayout.CENTER);
        item.add(dateLabel, BorderLayout.EAST);

        return item;
    }

    private String getStatusIcon(Registration.Status status) {
        switch (status) {
            case APPROVED: return "✅";
            case PENDING: return "⏳";
            case CANCELLED: return "❌";
            case REJECTED: return "❌";
            default: return "📝";
        }
    }

    private void handleLogout() {
        try {
            controllers.AuthController.getInstance().logout();
            Window window = SwingUtilities.getWindowAncestor(this);
            if (window != null) {
                window.dispose();
            }
            new screens.LoginScreen();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error during logout: " + e.getMessage(),
                "Logout Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            // Use the system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create and show the login screen on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                // For testing purposes, you might want to log in a test user here
                // to directly view the dashboard during development.
                // Example (replace with actual test user credentials):
                // AuthController.getInstance().login("test_attendee@example.com", "password");

                // If logged in, show dashboard, otherwise show login screen
                // Note: This basic check might need more robust session validation
                if (AuthController.getInstance().isLoggedIn() && AuthController.getInstance().isAttendee()) {
                     new AttendeeDashboardNew();
                } else {
                    // Ensure login screen is shown if not logged in or not correct role
                     // Attempt to login admin for quick testing
                     AuthController.LoginResult result = null;
                     try {
                         result = AuthController.getInstance().login("admin@example.com", "admin123");
                          if (result.isSuccess() && result.getUser().getRole() == User.UserRole.ATTENDEE) {
                              new AttendeeDashboardNew();
                         } else {
                               System.out.println("Test admin login failed or is not an Attendee.");
                              new LoginScreen();
                         }
                     } catch (SQLException e) {
                          System.err.println("Database error during test login: " + e.getMessage());
                          e.printStackTrace();
                          new LoginScreen();
                     }


                }

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Error initializing application: " + e.getMessage(),
                    "Initialization Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    // Ensure proper disposal of screens when navigating between screens
    public void showScreen(String cardName) {
        contentLayout.show(contentPanel, cardName);
        if (cardName.equals("My Events")) {
            refreshDashboard();
        }
    }

    private void refreshDashboard() {
        try {
            // Refresh the My Events content
            JPanel myEventsContent = createMyEventsContent();
            contentPanel.removeAll();
            contentPanel.add(createDashboardContent(), "Dashboard");
            contentPanel.add(myEventsContent, "My Events");
            contentPanel.add(new ProfilePanel(AuthController.getInstance().getCurrentUser(), true), "Profile");
            contentLayout.show(contentPanel, "My Events");
        } catch (SQLException e) {
            UIUtils.showError(this, "Error refreshing dashboard: " + e.getMessage());
        }
    }
}

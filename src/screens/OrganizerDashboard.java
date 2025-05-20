package screens;

import controllers.AuthController;
import controllers.RegistrationController;
import controllers.EventController;
import models.User;
import models.Event;
import models.Registration;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
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
import models.Event.EventStatus;

import components.GradientButton;
import components.ProfilePanel;
import components.SettingsPanel;

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

        // Add navigation buttons
        sidebarPanel.addNavButton("Dashboard", "", "Dashboard", true);
        sidebarPanel.addNavButton("My Events", "", "My Events", false);
        sidebarPanel.addNavButton("Create Event", "", "Create Event", false);
        sidebarPanel.addNavButton("Participants", "", "Participants", false);
        sidebarPanel.addNavButton("Media Upload", "", "Media Upload", false);
        sidebarPanel.addNavButton("Profile", "", "Profile", false);
        sidebarPanel.addNavButton("Settings", "", "Settings", false);
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
        contentPanel.add(new SettingsPanel(authController.getCurrentUser()), "Settings");

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

        // Recent Events Table (Placeholder/Existing table will be added here)
        String[] columns = {"Event Name", "Date", "Location", "Status", "Participants"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(30);
        table.setFont(UIConstants.BODY_FONT);
        table.getTableHeader().setFont(UIConstants.SMALL_FONT_BOLD);
        table.getTableHeader().setBackground(AppColors.BACKGROUND_LIGHT);
        table.getTableHeader().setForeground(AppColors.TEXT_SECONDARY);
        table.setGridColor(AppColors.BORDER);
        table.setSelectionBackground(AppColors.PRIMARY_LIGHT);
        table.setSelectionForeground(AppColors.TEXT_PRIMARY);

        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        recentEventsPanel.add(tableScrollPane, BorderLayout.CENTER);

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
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppColors.BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = UIUtils.createLabel("My Events", UIConstants.TITLE_FONT, AppColors.TEXT_PRIMARY);
        panel.add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"Event Name", "Date", "Location", "Status", "Actions"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };

        JTable table = new JTable(tableModel);
        table.setRowHeight(40);
        table.getTableHeader().setReorderingAllowed(false);

        // Load events
        try {
            List<Event> events = eventController.getEventsByOrganizer(authController.getCurrentUser().getId());
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            for (Event event : events) {
                JButton viewButton = new JButton("View Details");
                viewButton.addActionListener(e -> {
                    new EventDetailsScreen(event.getId(), authController.getCurrentUser()).setVisible(true);
                });
                JButton updateButton = new JButton("Update");
                boolean canUpdate = event.getStatus() != EventStatus.COMPLETED && event.getStatus() != EventStatus.CANCELLED;
                updateButton.setEnabled(canUpdate);
                updateButton.addActionListener(e -> {
                    // Show the Create Event form pre-filled for update (implement as needed)
                    // For now, just show a message
                    JOptionPane.showMessageDialog(this, "Update event functionality coming soon!");
                });
                JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                actionsPanel.setOpaque(false);
                actionsPanel.add(viewButton);
                actionsPanel.add(updateButton);
                tableModel.addRow(new Object[]{
                    event.getTitle(),
                    event.getEventDate().format(dateFormatter),
                    event.getVenueName(),
                    event.getStatus().name(),
                    actionsPanel
                });
            }
        } catch (Exception e) {
            UIUtils.showError(this, "Error loading events: " + e.getMessage());
        }

        table.getColumn("Actions").setCellRenderer((tbl, value, isSelected, hasFocus, row, col) -> (JPanel) value);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
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
                        new EventDetailsScreen(event.getId(), authController.getCurrentUser()).setVisible(true);
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
        try {
            User currentUser = authController.getCurrentUser();
            List<Event> events = eventController.getEventsByOrganizer(currentUser.getId());
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

            // Clear existing events
            recentEventsPanel.removeAll();
            recentEventsPanel.setLayout(new BoxLayout(recentEventsPanel, BoxLayout.Y_AXIS));

            // Add title
            JLabel titleLabel = UIUtils.createLabel(
                "Recent Events",
                UIConstants.BODY_FONT_BOLD,
                AppColors.TEXT_PRIMARY
            );
            titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
            recentEventsPanel.add(titleLabel);

            // Add recent events (up to 5)
            int count = 0;
            for (Event event : events) {
                if (count >= 5) break;

                JPanel eventPanel = new JPanel(new BorderLayout(10, 0));
                eventPanel.setBackground(Color.WHITE);
                eventPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(AppColors.BORDER),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
                ));

                // Event name and status
                JPanel headerPanel = new JPanel(new BorderLayout());
                headerPanel.setOpaque(false);

                JLabel nameLabel = new JLabel(event.getTitle());
                nameLabel.setFont(UIConstants.BODY_FONT_BOLD);
                headerPanel.add(nameLabel, BorderLayout.WEST);

                // Status badge
                JLabel statusLabel = new JLabel(event.getStatus().name());
                statusLabel.setFont(UIConstants.SMALL_FONT);
                statusLabel.setForeground(getStatusColor(event.getStatus()));
                headerPanel.add(statusLabel, BorderLayout.EAST);

                eventPanel.add(headerPanel, BorderLayout.NORTH);

                // Event details
                JPanel detailsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
                detailsPanel.setOpaque(false);

                JLabel dateLabel = new JLabel("\uD83D\uDCC5 " + event.getEventDate().format(dateFormatter));
                dateLabel.setFont(UIConstants.SMALL_FONT);
                detailsPanel.add(dateLabel);

                JLabel venueLabel = new JLabel("📍 " + event.getVenueName());
                venueLabel.setFont(UIConstants.SMALL_FONT);
                detailsPanel.add(venueLabel);

                JLabel slotsLabel = new JLabel("👥 " + event.getAvailableSlots() + "/" + event.getTotalSlots() + " slots");
                slotsLabel.setFont(UIConstants.SMALL_FONT);
                detailsPanel.add(slotsLabel);

                eventPanel.add(detailsPanel, BorderLayout.CENTER);

                // Action buttons
                JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
                actionPanel.setOpaque(false);

                JButton viewButton = UIUtils.createButton(
                    "View Details",
                    null,
                    UIUtils.ButtonType.SECONDARY,
                    UIUtils.ButtonSize.SMALL
                );
                viewButton.addActionListener(e -> {
                    new EventDetailsScreen(event.getId(), currentUser).setVisible(true);
                });
                actionPanel.add(viewButton);

                eventPanel.add(actionPanel, BorderLayout.SOUTH);
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

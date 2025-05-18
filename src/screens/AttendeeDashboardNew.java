package screens;

import controllers.AuthController;
import controllers.EventController;
import controllers.RegistrationController;
import models.User;
import models.Event;
import models.Registration;
import java.awt.*;
import java.text.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import utils.*;
import components.*;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dashboard screen for Attendees (Students)
 * Implements a modern UI with rounded corners and consistent sidebar
 */
@SuppressWarnings("unused")
public class AttendeeDashboardNew extends JFrame {
    private JPanel contentPanel;
    private CardLayout contentLayout;

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
        PROFILE("👤", "Profile");

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
        contentPanel.add(createDashboardContent(), TabInfo.HOME.title);
        contentPanel.add(createSearchContent(), TabInfo.SEARCH.title);
        contentPanel.add(createCalendarContent(), TabInfo.CALENDAR.title);
        contentPanel.add(createMyEventsContent(), TabInfo.MY_EVENTS.title);
        contentPanel.add(createProfileContent(), TabInfo.PROFILE.title);

        // Show the dashboard by default
        contentLayout.show(contentPanel, TabInfo.HOME.title);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private JPanel createHeaderPanel() {
        try {
            User currentUser = AuthController.getInstance().getCurrentUser();
            return new HeaderPanel(
                currentUser.getName(),
                "Attendee",
                false,
                null
            );
        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading user data: " + e.getMessage());
            return new HeaderPanel("Guest", "Attendee", false, null);
        }
    }

    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(AppColors.PRIMARY);
        sidebarPanel.setPreferredSize(new Dimension(250, 0));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // User info panel
        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setOpaque(false);
        userInfoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userInfoPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        // User avatar
        JLabel avatarLabel = new JLabel(new ImageIcon("resources/images/avatar.png"));
        avatarLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userInfoPanel.add(avatarLabel);

        // Get current user
        User currentUser = null;
        try {
            currentUser = AuthController.getInstance().getCurrentUser();
        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading user data: " + e.getMessage());
        }

        // User name
        JLabel nameLabel = new JLabel(currentUser != null ? currentUser.getName() : "Guest");
        nameLabel.setFont(UIConstants.BODY_FONT);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userInfoPanel.add(nameLabel);

        // User role
        JLabel roleLabel = new JLabel("Attendee");
        roleLabel.setFont(UIConstants.SMALL_FONT);
        roleLabel.setForeground(Color.WHITE);
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userInfoPanel.add(roleLabel);

        sidebarPanel.add(userInfoPanel);

        // Navigation buttons panel
        JPanel navPanel = new JPanel();
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setOpaque(false);
        navPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Dashboard button
        JButton dashboardButton = UIUtils.createButton(
            "Dashboard",
            null,
            UIUtils.ButtonType.SECONDARY,
            UIUtils.ButtonSize.LARGE
        );
        dashboardButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        dashboardButton.setMaximumSize(new Dimension(200, 40));
        navPanel.add(dashboardButton);

        // Events button
        JButton eventsButton = UIUtils.createButton(
            "Events",
            null,
            UIUtils.ButtonType.SECONDARY,
            UIUtils.ButtonSize.LARGE
        );
        eventsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        eventsButton.setMaximumSize(new Dimension(200, 40));
        navPanel.add(eventsButton);

        // My Registrations button
        JButton registrationsButton = UIUtils.createButton(
            "My Registrations",
            null,
            UIUtils.ButtonType.SECONDARY,
            UIUtils.ButtonSize.LARGE
        );
        registrationsButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registrationsButton.setMaximumSize(new Dimension(200, 40));
        navPanel.add(registrationsButton);

        // Logout button
        JButton logoutButton = UIUtils.createButton(
            "Logout",
            null,
            UIUtils.ButtonType.SECONDARY,
            UIUtils.ButtonSize.LARGE
        );
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(200, 40));
        logoutButton.addActionListener(e -> handleLogout());
        navPanel.add(logoutButton);

        sidebarPanel.add(navPanel);
        sidebarPanel.add(Box.createVerticalGlue());

        return sidebarPanel;
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
                            event.getName(),
                            event.getStartDateTime().toString(),
                            event.getVenue().getName(),
                            event.getDescription(),
                            String.valueOf(event.getCapacity())
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
            List<Registration> myRegistrations = registrationController.getEventRegistrations(currentUser.getId());

            // Convert event data to calendar events
            List<CalendarPanel.CalendarEvent> calendarEvents = new ArrayList<>();

            // Add all events
            for (Event event : allEvents) {
                calendarEvents.add(new CalendarPanel.CalendarEvent(
                    "EVENT" + event.getId(),
                    event.getName(),
                    Date.from(event.getStartDateTime().atZone(java.time.ZoneId.systemDefault()).toInstant()),
                    event.getVenue().getName(),
                    myRegistrations.stream().anyMatch(r -> r.getEvent().getId() == event.getId())
                ));
            }

            // Set events data and click listener
            calendarPanel.setEvents(calendarEvents);
            calendarPanel.setEventClickListener(event -> {
                try {
                    Event selectedEvent = eventController.getEvent(Integer.parseInt(event.getId().substring(5)));
                    if (selectedEvent != null) {
                        showEventDetails(
                            selectedEvent.getName(),
                            selectedEvent.getDescription(),
                            Date.from(selectedEvent.getStartDateTime().atZone(java.time.ZoneId.systemDefault()).toInstant()),
                            selectedEvent.getVenue().getName(),
                            selectedEvent.getCapacity()
                        );
                    }
                } catch (SQLException e) {
                    UIUtils.showError(this, "Error loading event details: " + e.getMessage());
                }
            });
        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading calendar data: " + e.getMessage());
        }

        panel.add(calendarPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createMyEventsContent() {
        JPanel panel = UIUtils.createPanel(new BorderLayout(), true);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = UIUtils.createLabel(
            "My Events",
            UIConstants.TITLE_FONT,
            AppColors.TEXT_PRIMARY
        );
        panel.add(titleLabel, BorderLayout.NORTH);

        try {
            User currentUser = AuthController.getInstance().getCurrentUser();
            List<Registration> myRegistrations = registrationController.getEventRegistrations(currentUser.getId());
            List<Event> myEvents = myRegistrations.stream()
                .map(Registration::getEvent)
                .collect(Collectors.toList());

            String[][] eventData = myEvents.stream()
                .map(event -> new String[]{
                    event.getName(),
                    event.getStartDateTime().toString(),
                    event.getVenue().getName(),
                    event.getDescription(),
                    String.valueOf(event.getCapacity())
                })
                .toArray(String[][]::new);

            panel.add(createEventGrid(eventData), BorderLayout.CENTER);
        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading my events: " + e.getMessage());
            panel.add(UIUtils.createLabel(
                "Error loading events. Please try again later.",
                UIConstants.BODY_FONT,
                AppColors.ERROR
            ), BorderLayout.CENTER);
        }

        return panel;
    }

    private JPanel createProfileContent() {
        JPanel panel = UIUtils.createPanel(new BorderLayout(), true);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = UIUtils.createLabel(
            "My Profile",
            UIConstants.TITLE_FONT,
            AppColors.TEXT_PRIMARY
        );
        panel.add(titleLabel, BorderLayout.NORTH);

        // Profile form
        JPanel formPanel = UIUtils.createPanel(new GridBagLayout(), false);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        try {
            User currentUser = AuthController.getInstance().getCurrentUser();
            // Add profile fields
            addProfileField(formPanel, gbc, "Name:", currentUser.getName());
            addProfileField(formPanel, gbc, "Email:", currentUser.getEmail());
            addProfileField(formPanel, gbc, "Role:", currentUser.getRole().toString());
        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading profile data: " + e.getMessage());
            addProfileField(formPanel, gbc, "Error:", "Failed to load profile data");
        }

        panel.add(formPanel, BorderLayout.CENTER);

        return panel;
    }

    private JScrollPane createDashboardContent() {
        JPanel panel = UIUtils.createPanel(new BorderLayout(), true);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = UIUtils.createLabel(
            "Welcome to Event Management System",
            UIConstants.TITLE_FONT,
            AppColors.TEXT_PRIMARY
        );
        panel.add(titleLabel, BorderLayout.NORTH);

        // Content
        JPanel content = UIUtils.createPanel(new BorderLayout(), false);
        content.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Upcoming events section
        JLabel upcomingLabel = UIUtils.createLabel(
            "Upcoming Events",
            UIConstants.HEADER_FONT,
            AppColors.TEXT_PRIMARY
        );
        content.add(upcomingLabel, BorderLayout.NORTH);

        try {
            List<Event> upcomingEvents = eventController.getAllEvents().stream()
                .filter(e -> !e.isPast())
                .collect(Collectors.toList());

            String[][] eventData = upcomingEvents.stream()
                .map(event -> new String[]{
                    event.getName(),
                    event.getStartDateTime().toString(),
                    event.getVenue().getName(),
                    event.getDescription(),
                    String.valueOf(event.getCapacity())
                })
                .toArray(String[][]::new);

            content.add(createEventGrid(eventData), BorderLayout.CENTER);
        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading upcoming events: " + e.getMessage());
        }

        panel.add(content, BorderLayout.CENTER);

        return new JScrollPane(panel);
    }

    private JPanel centeredPanel(Component comp) {
        JPanel panel = UIUtils.createPanel(new GridBagLayout(), false);
        panel.add(comp);
        return panel;
    }

    private JPanel createEventGrid(String[][] events) {
        JPanel grid = UIUtils.createPanel(new GridLayout(0, 3, 20, 20), false);
        grid.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        for (String[] event : events) {
            grid.add(createEventCard(
                event[0],
                event[1],
                event[2],
                "View Details",
                "view"
            ));
        }

        return grid;
    }

    private JPanel createEventCard(String title, String dateStr, String location, String buttonText, String buttonAction) {
        RoundedPanel card = new RoundedPanel(new BorderLayout(), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        card.setBorder(UIUtils.createRoundedBorderWithPadding(
            AppColors.BORDER,
            UIConstants.CORNER_RADIUS_MEDIUM,
            1,
            20
        ));

        // Title
        JLabel titleLabel = UIUtils.createLabel(
            title,
            UIConstants.HEADER_FONT,
            AppColors.TEXT_PRIMARY
        );
        card.add(titleLabel, BorderLayout.NORTH);

        // Details
        JPanel detailsPanel = UIUtils.createPanel(new GridLayout(2, 1, 0, 10), false);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Format date string
        String formattedDate = dateStr;
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a");
            Date date = inputFormat.parse(dateStr);
            formattedDate = outputFormat.format(date);
        } catch (ParseException e) {
            // If parsing fails, use the original string
        }

        JLabel dateLabel = UIUtils.createLabel(
            "📅 " + formattedDate,
            UIConstants.BODY_FONT,
            AppColors.TEXT_SECONDARY
        );
        detailsPanel.add(dateLabel);

        JLabel locationLabel = UIUtils.createLabel(
            "📍 " + location,
            UIConstants.BODY_FONT,
            AppColors.TEXT_SECONDARY
        );
        detailsPanel.add(locationLabel);

        card.add(detailsPanel, BorderLayout.CENTER);

        // Button
        JButton button = UIUtils.createButton(
            buttonText,
            null,
            UIUtils.ButtonType.PRIMARY,
            UIUtils.ButtonSize.SMALL
        );
        button.addActionListener(e -> {
            if (buttonAction.equals("view")) {
                try {
                    List<Event> events = eventController.getAllEvents();
                    Event selectedEvent = events.stream()
                        .filter(event -> event.getName().equals(title))
                        .findFirst()
                        .orElse(null);

                    if (selectedEvent != null) {
                        EventDetailsScreen screen = new EventDetailsScreen(
                            selectedEvent.getId(),
                            AuthController.getInstance().getCurrentUser()
                        );
                        screen.setVisible(true);
                    } else {
                        UIUtils.showError(this, "Event not found");
                    }
                } catch (SQLException ex) {
                    UIUtils.showError(this, "Error loading event details: " + ex.getMessage());
                }
            }
        });
        card.add(button, BorderLayout.SOUTH);

        return card;
    }

    private void addProfileField(JPanel panel, GridBagConstraints gbc, String label, String value) {
        JLabel labelComponent = UIUtils.createLabel(label, UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY);
        panel.add(labelComponent, gbc);

        gbc.gridx = 1;
        JLabel valueComponent = UIUtils.createLabel(value, UIConstants.BODY_FONT, AppColors.TEXT_SECONDARY);
        panel.add(valueComponent, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
    }

    private void showEventDetails(String name, String desc, Date date, String venue, int slots) {
        try {
            // Find the event by name
            List<Event> events = eventController.getAllEvents();
            Event selectedEvent = events.stream()
                .filter(e -> e.getName().equals(name))
                .findFirst()
                .orElse(null);

            if (selectedEvent != null) {
                EventDetailsScreen screen = new EventDetailsScreen(
                    selectedEvent.getId(),
                    AuthController.getInstance().getCurrentUser()
                );
                screen.setVisible(true);
            } else {
                UIUtils.showError(this, "Event not found");
            }
        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading event details: " + e.getMessage());
        }
    }

    private void handleLogout() {
        try {
            AuthController.getInstance().logout();
            dispose();
            SwingUtilities.invokeLater(() -> {
                try {
                    new LoginScreen();
                } catch (SQLException e) {
                    UIUtils.showError(this, "Error opening login screen: " + e.getMessage());
                }
            });
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
        
        SwingUtilities.invokeLater(AttendeeDashboardNew::new);
    }
}

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
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import components.RoundedPanel;

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

        // Add navigation buttons
        sidebarPanel.addNavButton("Dashboard", "", "Dashboard", true);
        sidebarPanel.addNavButton("Search Events", "", "Search Events", false);
        sidebarPanel.addNavButton("Calendar", "", "Calendar", false);
        sidebarPanel.addNavButton("My Events", "", "My Events", false);
        sidebarPanel.addNavButton("Profile", "", "Profile", false);
        sidebarPanel.addNavButton("Settings", "", "Settings", false);
        sidebarPanel.addLogoutButton(e -> handleLogout());

        // Add content cards
        contentPanel.add(createDashboardContent(), "Dashboard");
        contentPanel.add(createSearchContent(), "Search Events");
        contentPanel.add(createCalendarContent(), "Calendar");
        contentPanel.add(createMyEventsContent(), "My Events");
        contentPanel.add(createProfileContent(), "Profile");
        contentPanel.add(new SettingsPanel(AuthController.getInstance().getCurrentUser()), "Settings");

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        setContentPane(mainPanel);

        contentLayout.show(contentPanel, "Dashboard");
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
            List<Registration> myRegistrations = registrationController.getEventRegistrations(currentUser.getId());

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
        JPanel panel = UIUtils.createPanel(new BorderLayout(), true);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = UIUtils.createLabel(
            "My Registered Events",
            UIConstants.TITLE_FONT,
            AppColors.TEXT_PRIMARY
        );
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel eventsListPanel = new JPanel();
        eventsListPanel.setLayout(new BoxLayout(eventsListPanel, BoxLayout.Y_AXIS));
        eventsListPanel.setBackground(Color.WHITE);

        try {
            User currentUser = AuthController.getInstance().getCurrentUser();
            List<Registration> myRegistrations = registrationController.getEventRegistrations(currentUser.getId());

            if (myRegistrations.isEmpty()) {
                JLabel noEventsLabel = UIUtils.createLabel(
                    "You have not registered for any events yet.",
                    UIConstants.BODY_FONT,
                    AppColors.TEXT_SECONDARY
                );
                eventsListPanel.add(noEventsLabel);
            } else {
                for (Registration registration : myRegistrations) {
                    Event event = eventController.getEvent(registration.getEvent().getId());
                    if (event != null) {
                        JPanel eventPanel = createEventSummaryPanel(event, registration);
                        eventsListPanel.add(eventPanel);
                        eventsListPanel.add(Box.createVerticalStrut(10)); // Spacing between events
                    }
                }
            }

        } catch (SQLException e) {
            UIUtils.showError(this, "Error loading registered events: " + e.getMessage());
        }

        JScrollPane scrollPane = new JScrollPane(eventsListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createEventSummaryPanel(Event event, Registration registration) {
        JPanel panel = UIUtils.createPanel(new BorderLayout(10, 0), true);
        panel.setBackground(AppColors.BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height)); // Prevent horizontal expansion

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

        JLabel dateLabel = UIUtils.createLabel(
            "Date: " + (event.getEventDate() != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm").format(event.getEventDate()) : "N/A"),
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
                contentPanel.remove(findComponentByName(contentPanel, TabInfo.MY_EVENTS.title));
                contentPanel.add(createMyEventsContent(), TabInfo.MY_EVENTS.title);
                contentLayout.show(contentPanel, TabInfo.MY_EVENTS.title);
            } catch (SQLException e) {
                UIUtils.showError(this, "Error cancelling registration: " + e.getMessage());
            }
        }
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

        // Profile details panel
        JPanel profileDetailsPanel = UIUtils.createPanel(new GridBagLayout(), false);
        profileDetailsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        try {
            User currentUser = AuthController.getInstance().getCurrentUser();
            if (currentUser != null) {
                addProfileField(profileDetailsPanel, gbc, "Name:", currentUser.getName());
                addProfileField(profileDetailsPanel, gbc, "Email:", currentUser.getEmail());
                addProfileField(profileDetailsPanel, gbc, "Role:", currentUser.getRole().getDisplayName());
                addProfileField(profileDetailsPanel, gbc, "Phone:", currentUser.getPhone());
                addProfileField(profileDetailsPanel, gbc, "Registration Date:", currentUser.getRegistrationDate());
                addProfileField(profileDetailsPanel, gbc, "Last Login:", currentUser.getLastLoginAt() != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm").format(currentUser.getLastLoginAt()) : "N/A");

                // Add a button to edit profile (placeholder)
                JButton editProfileButton = UIUtils.createButton(
                    "Edit Profile",
                    null,
                    UIUtils.ButtonType.SECONDARY,
                    UIUtils.ButtonSize.SMALL
                );
                 gbc.gridx = 0;
                 gbc.gridy++;
                 gbc.gridwidth = 2;
                 gbc.anchor = GridBagConstraints.CENTER;
                 gbc.insets = new Insets(20, 5, 5, 5);
                 profileDetailsPanel.add(editProfileButton, gbc);
                 editProfileButton.addActionListener(e -> {
                     // TODO: Implement Edit Profile Screen/Dialog
                     JOptionPane.showMessageDialog(this, "Edit Profile functionality coming soon!");
                 });


            } else {
                JLabel errorLabel = UIUtils.createLabel(
                    "Unable to load profile data.",
                    UIConstants.BODY_FONT,
                    AppColors.ERROR
                );
                profileDetailsPanel.add(errorLabel, gbc);
            }
        } catch (SQLException e) {
             UIUtils.showError(this, "Error loading profile data: " + e.getMessage());
        }


        panel.add(profileDetailsPanel, BorderLayout.CENTER);

        return panel;
    }

    private void addProfileField(JPanel panel, GridBagConstraints gbc, String label, String value) {
        JLabel labelComp = UIUtils.createLabel(
            label,
            UIConstants.BODY_FONT.deriveFont(Font.BOLD),
            AppColors.TEXT_PRIMARY
        );
        panel.add(labelComp, gbc);

        gbc.gridx++;
        JLabel valueComp = UIUtils.createLabel(
            value != null ? value : "N/A",
            UIConstants.BODY_FONT,
            AppColors.TEXT_SECONDARY
        );
        panel.add(valueComp, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
    }

    private JScrollPane createDashboardContent() {
        JPanel panel = UIUtils.createPanel(new BorderLayout(20, 20), true);
        panel.setBackground(AppColors.BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header section (Greeting and Date)
        JPanel headerSection = UIUtils.createPanel(new BorderLayout(), false);
        headerSection.setOpaque(false);

        // Greeting
        User currentUser = null;
        try {
            currentUser = AuthController.getInstance().getCurrentUser();
        } catch (SQLException e) {
            e.printStackTrace();
        }

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

        // Main content area for dashboard
        JPanel mainContent = UIUtils.createPanel(new BorderLayout(20, 20), false);
        mainContent.setOpaque(false);

        // Stats row
        JPanel statsPanel = UIUtils.createPanel(new GridLayout(1, 4, 20, 0), false);
        statsPanel.setOpaque(false);

        // Add stats cards
        try {
            User user = AuthController.getInstance().getCurrentUser();
            List<Registration> registrations = registrationController.getEventRegistrations(user.getId());
            int upcomingEvents = 0;
            int completedEvents = 0;
            
            for (Registration reg : registrations) {
                Event event = eventController.getEvent(reg.getEvent().getId());
                if (event != null) {
                    if (event.getEventDate().isAfter(LocalDateTime.now())) {
                        upcomingEvents++;
                    } else {
                        completedEvents++;
                    }
                }
            }
            
            statsPanel.add(createStatCard("Total Registrations", String.valueOf(registrations.size()), AppColors.PRIMARY));
            statsPanel.add(createStatCard("Upcoming Events", String.valueOf(upcomingEvents), AppColors.ACCENT_GREEN));
            statsPanel.add(createStatCard("Completed Events", String.valueOf(completedEvents), AppColors.PRIMARY_LIGHT));
            statsPanel.add(createStatCard("Available Events", String.valueOf(eventController.getTotalEvents() - registrations.size()), AppColors.ACCENT_YELLOW));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Upcoming Events Panel
        RoundedPanel upcomingEventsPanel = new RoundedPanel(new BorderLayout(), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        upcomingEventsPanel.setBorder(UIUtils.createRoundedBorder(AppColors.BORDER, UIConstants.CORNER_RADIUS_MEDIUM, 1));

        JLabel upcomingEventsTitle = UIUtils.createLabel(
            "Your Upcoming Events",
            UIConstants.BODY_FONT_BOLD,
            AppColors.TEXT_PRIMARY
        );
        upcomingEventsTitle.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        upcomingEventsPanel.add(upcomingEventsTitle, BorderLayout.NORTH);

        JPanel upcomingEventsList = new JPanel();
        upcomingEventsList.setLayout(new BoxLayout(upcomingEventsList, BoxLayout.Y_AXIS));
        upcomingEventsList.setBackground(Color.WHITE);
        upcomingEventsList.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        try {
            User user = AuthController.getInstance().getCurrentUser();
            List<Registration> registrations = registrationController.getEventRegistrations(user.getId());
            
            List<Event> upcomingEvents = new ArrayList<>();
            for (Registration reg : registrations) {
                Event event = eventController.getEvent(reg.getEvent().getId());
                if (event != null && event.getEventDate().isAfter(LocalDateTime.now())) {
                    upcomingEvents.add(event);
                }
            }

            if (upcomingEvents.isEmpty()) {
                JLabel noEventsLabel = UIUtils.createLabel(
                    "You have no upcoming events.",
                    UIConstants.BODY_FONT,
                    AppColors.TEXT_SECONDARY
                );
                noEventsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                upcomingEventsList.add(noEventsLabel);
            } else {
                // Sort by date, closest first
                upcomingEvents.sort(Comparator.comparing(Event::getEventDate));
                
                // Show up to 5 upcoming events
                int count = 0;
                for (Event event : upcomingEvents) {
                    if (count >= 5) break;
                    
                    JPanel eventItem = createEventItem(event);
                    upcomingEventsList.add(eventItem);
                    upcomingEventsList.add(Box.createVerticalStrut(10));
                    count++;
                }
            }
            
        } catch (SQLException e) {
            JLabel errorLabel = UIUtils.createLabel(
                "Error loading upcoming events.",
                UIConstants.BODY_FONT,
                AppColors.ERROR
            );
            errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            upcomingEventsList.add(errorLabel);
        }

        JScrollPane eventsScrollPane = new JScrollPane(upcomingEventsList);
        eventsScrollPane.setBorder(null);
        eventsScrollPane.setOpaque(false);
        eventsScrollPane.getViewport().setOpaque(false);
        upcomingEventsPanel.add(eventsScrollPane, BorderLayout.CENTER);

        // Announcements Panel
        RoundedPanel announcementsPanel = new RoundedPanel(new BorderLayout(), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        announcementsPanel.setBorder(UIUtils.createRoundedBorder(AppColors.BORDER, UIConstants.CORNER_RADIUS_MEDIUM, 1));

        JLabel announcementsTitle = UIUtils.createLabel(
            "Announcements",
            UIConstants.BODY_FONT_BOLD,
            AppColors.TEXT_PRIMARY
        );
        announcementsTitle.setBorder(BorderFactory.createEmptyBorder(15, 15, 10, 15));
        announcementsPanel.add(announcementsTitle, BorderLayout.NORTH);

        JPanel announcementsList = new JPanel();
        announcementsList.setLayout(new BoxLayout(announcementsList, BoxLayout.Y_AXIS));
        announcementsList.setBackground(Color.WHITE);
        announcementsList.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Add some sample announcements
        addAnnouncementItem(announcementsList, "System Update", "The system will be down for maintenance on Sunday from 2-4 AM.");
        addAnnouncementItem(announcementsList, "New Features", "Check out our new event recommendation system!");
        addAnnouncementItem(announcementsList, "Holiday Schedule", "Special holiday events are now available for registration.");

        JScrollPane announcementsScrollPane = new JScrollPane(announcementsList);
        announcementsScrollPane.setBorder(null);
        announcementsScrollPane.setOpaque(false);
        announcementsScrollPane.getViewport().setOpaque(false);
        announcementsPanel.add(announcementsScrollPane, BorderLayout.CENTER);

        // Add sections to main content
        mainContent.add(statsPanel, BorderLayout.NORTH);
        
        // Two columns: upcoming events and announcements
        JPanel columnsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        columnsPanel.setOpaque(false);
        columnsPanel.add(upcomingEventsPanel);
        columnsPanel.add(announcementsPanel);
        
        mainContent.add(columnsPanel, BorderLayout.CENTER);
        panel.add(mainContent, BorderLayout.CENTER);

        // Wrap panel in scroll pane and return
        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        return scrollPane;
    }

    private void addAnnouncementItem(JPanel parent, String title, String message) {
        RoundedPanel announcementCard = new RoundedPanel(new BorderLayout(), Color.WHITE, UIConstants.CORNER_RADIUS_SMALL);
        announcementCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER_LIGHT),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        JLabel titleLabel = UIUtils.createLabel(title, UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel messageLabel = UIUtils.createLabel(message, UIConstants.BODY_FONT, AppColors.TEXT_SECONDARY);
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(messageLabel);
        
        announcementCard.add(contentPanel, BorderLayout.CENTER);
        announcementCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        parent.add(announcementCard);
        parent.add(Box.createVerticalStrut(10));
    }
    
    private JPanel createEventItem(Event event) {
        RoundedPanel item = new RoundedPanel(new BorderLayout(10, 0), Color.WHITE, UIConstants.CORNER_RADIUS_SMALL);
        item.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER_LIGHT),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JPanel leftSection = new JPanel(new BorderLayout());
        leftSection.setOpaque(false);
        
        JLabel titleLabel = UIUtils.createLabel(event.getTitle(), UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY);
        leftSection.add(titleLabel, BorderLayout.NORTH);
        
        JLabel dateTimeLabel = UIUtils.createLabel(
            event.getEventDate().format(DateTimeFormatter.ofPattern("MMM d, yyyy - h:mm a")),
            UIConstants.SMALL_FONT,
            AppColors.TEXT_SECONDARY
        );
        leftSection.add(dateTimeLabel, BorderLayout.CENTER);
        
        JLabel venueLabel = UIUtils.createLabel(event.getVenueName(), UIConstants.SMALL_FONT, AppColors.TEXT_SECONDARY);
        leftSection.add(venueLabel, BorderLayout.SOUTH);
        
        item.add(leftSection, BorderLayout.CENTER);
        
        JButton viewButton = UIUtils.createButton("View", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.SMALL);
        viewButton.addActionListener(e -> {
            try {
                new EventDetailsScreen(event.getId(), AuthController.getInstance().getCurrentUser()).setVisible(true);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        });
        item.add(viewButton, BorderLayout.EAST);
        
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        return item;
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
}

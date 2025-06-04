package screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import controllers.EventController;
import controllers.FeedbackController;
import controllers.RegistrationController;
import models.Event;
import models.Registration;
import models.User;
import utils.EmojiUtils;

/**
 * Event Details Screen
 * Implements the design from event_details.md with modern UI components
 */
@SuppressWarnings("unused")
public class EventDetailsScreen extends JFrame {
    private Event event;
    private final User currentUser;
    private FeedbackController feedbackController;
    private EventController eventController;
    private RegistrationController registrationController;
    private boolean isRegistered;

    public EventDetailsScreen(int eventId, User currentUser) {
        this.currentUser = currentUser;

        try {
            this.feedbackController = FeedbackController.getInstance();
            this.eventController = EventController.getInstance();
            this.registrationController = RegistrationController.getInstance();

            Event tempEvent = eventController.getEvent(eventId);
            if (tempEvent == null) {
                throw new IllegalArgumentException("Event not found");
            }
            this.event = tempEvent;
            this.isRegistered = registrationController.isUserRegistered(currentUser.getId(), eventId);

            setTitle("Event Details - " + event.getTitle());
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setMinimumSize(new Dimension(900, 600));
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            initializeComponents();
            setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading event details: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Back button
        JButton backButton = new JButton("← Back");
        backButton.addActionListener(e -> onBack());
        headerPanel.add(backButton, BorderLayout.WEST);

        // Title
        JLabel titleLabel = new JLabel("Event Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Image panel
        JPanel imagePanel = new JPanel(new BorderLayout());
        imagePanel.setBackground(Color.WHITE);
        if (event.getMainImage() != null) {
            ImageIcon icon = new ImageIcon(event.getMainImage());
            Image image = icon.getImage().getScaledInstance(400, 400, Image.SCALE_SMOOTH);
            JLabel imageLabel = new JLabel(new ImageIcon(image));
            imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
            imagePanel.add(imageLabel, BorderLayout.CENTER);
        } else {
            // Use emoji placeholder based on event category
            String categoryEmoji = EmojiUtils.getEventCategoryEmoji(event.getCategory());
            JLabel emojiLabel = new JLabel(categoryEmoji);
            emojiLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 120));
            emojiLabel.setHorizontalAlignment(SwingConstants.CENTER);
            emojiLabel.setVerticalAlignment(SwingConstants.CENTER);
            emojiLabel.setPreferredSize(new Dimension(400, 400));
            emojiLabel.setBackground(new Color(248, 249, 250));
            emojiLabel.setOpaque(true);
            emojiLabel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 2));
            imagePanel.add(emojiLabel, BorderLayout.CENTER);
        }

        // Details panel
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Color.WHITE);

        // Event title
        JLabel eventTitleLabel = new JLabel(event.getTitle());
        eventTitleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        detailsPanel.add(eventTitleLabel);
        detailsPanel.add(Box.createVerticalStrut(20));

        // Event details
        detailsPanel.add(createDetailRow("Date:", event.getEventDate().toLocalDate().toString()));
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(createDetailRow("Time:", event.getEventDate().toLocalTime().toString()));
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(createDetailRow("Venue:", event.getVenueName()));
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(createDetailRow("Location:", ""));
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(createDetailRow("Total Slots:", String.valueOf(event.getTotalSlots())));
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(createDetailRow("Category:", event.getCategory()));
        detailsPanel.add(Box.createVerticalStrut(10));
        detailsPanel.add(createDetailRow("Status:", event.getStatus().getDisplayName()));
        detailsPanel.add(Box.createVerticalStrut(20));

        // Description
        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        detailsPanel.add(descriptionLabel);
        detailsPanel.add(Box.createVerticalStrut(10));

        JTextArea descriptionArea = new JTextArea(event.getDescription());
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setEditable(false);
        descriptionArea.setBackground(Color.WHITE);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        descriptionScrollPane.setPreferredSize(new Dimension(400, 100));
        detailsPanel.add(descriptionScrollPane);
        detailsPanel.add(Box.createVerticalStrut(20));

        // Eligibility criteria
        JLabel eligibilityLabel = new JLabel("Eligibility Criteria:");
        eligibilityLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        detailsPanel.add(eligibilityLabel);
        detailsPanel.add(Box.createVerticalStrut(10));

        JTextArea eligibilityArea = new JTextArea(event.getEligibilityCriteria());
        eligibilityArea.setLineWrap(true);
        eligibilityArea.setWrapStyleWord(true);
        eligibilityArea.setEditable(false);
        eligibilityArea.setBackground(Color.WHITE);
        eligibilityArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JScrollPane eligibilityScrollPane = new JScrollPane(eligibilityArea);
        eligibilityScrollPane.setPreferredSize(new Dimension(400, 100));
        detailsPanel.add(eligibilityScrollPane);

        // Add panels to content panel
        contentPanel.add(imagePanel, BorderLayout.WEST);
        contentPanel.add(detailsPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> onRegister());
        buttonPanel.add(registerButton);

        // Add panels to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createDetailRow(String label, String value) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panel.add(labelComponent, BorderLayout.WEST);

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(valueComponent, BorderLayout.CENTER);

        return panel;
    }

    private void onBack() {
        dispose();
        try {
            switch (currentUser.getRole()) {
                case ADMIN:
                    new AdminDashboardNew().setVisible(true);
                    break;
                case EVENT_ORGANIZER:
                    new OrganizerDashboard().setVisible(true);
                    break;
                case ATTENDEE:
                    new AttendeeDashboardNew().setVisible(true);
                    break;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error returning to dashboard: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onRegister() {
        try {
            // Check if event is still available
            if (!event.isAvailable()) {
                JOptionPane.showMessageDialog(this,
                    "Sorry, this event is no longer available for registration.",
                    "Event Unavailable",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Check if user is already registered
            if (isRegistered) {
                JOptionPane.showMessageDialog(this,
                    "You are already registered for this event.",
                    "Already Registered",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Register the user
            Registration registration = registrationController.registerForEvent(currentUser, event.getId());
            isRegistered = true;

            // Show success message
            JOptionPane.showMessageDialog(this,
                "Successfully registered for the event!",
                "Registration Successful",
                JOptionPane.INFORMATION_MESSAGE);

            // Return to dashboard with refresh
            dispose();
            AttendeeDashboardNew dashboard = new AttendeeDashboardNew();
            dashboard.setVisible(true);
            dashboard.showScreen("My Events"); // This will trigger the refresh

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error registering for event: " + e.getMessage(),
                "Registration Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshUI() {
        getContentPane().removeAll();
        initializeComponents();
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create and show the login screen
        SwingUtilities.invokeLater(() -> {
            try {
                new LoginScreen().setVisible(true);
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Error initializing login screen: " + e.getMessage(),
                    "Initialization Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}

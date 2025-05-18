package screens;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import utils.AppColors;
import utils.UIUtils;
import components.EventRegistrationDialog;
import components.GradientButton;
import components.HeaderPanel;
import components.RoundedPanel;
import models.Feedback;
import models.User;
import models.Event;
import models.Registration;
import controllers.FeedbackController;
import controllers.EventController;
import controllers.RegistrationController;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;

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

            setTitle("Event Details - " + event.getName());
            setExtendedState(JFrame.MAXIMIZED_BOTH);
            setMinimumSize(new Dimension(900, 600));
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            setupUI();
            setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading event details: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            dispose();
        }
    }

    private void setupUI() {
        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(AppColors.BACKGROUND_LIGHT);

        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create content panel
        JPanel contentPanel = createContentPanel();

        // Wrap content in scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Set the main panel as the content pane
        setContentPane(mainPanel);
    }

    private JPanel createHeaderPanel() {
        return new HeaderPanel(currentUser.getName(), "Attendee");
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(AppColors.BACKGROUND_LIGHT);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Navigation panel
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        navPanel.setOpaque(false);
        navPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        GradientButton backButton = GradientButton.createSecondaryButton("< Back to Events");
        backButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backButton.setPreferredSize(new Dimension(150, 40));
        backButton.addActionListener(e -> handleBack());

        navPanel.add(backButton);
        contentPanel.add(navPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Event header panel
        JPanel eventHeaderPanel = createEventHeaderPanel();
        eventHeaderPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(eventHeaderPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Description section
        JPanel descriptionPanel = createSectionPanel("Description");
        JTextArea descriptionText = new JTextArea(event.getDescription());
        descriptionText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionText.setLineWrap(true);
        descriptionText.setWrapStyleWord(true);
        descriptionText.setEditable(false);
        descriptionText.setBackground(null);
        descriptionText.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        descriptionPanel.add(descriptionText, BorderLayout.CENTER);
        descriptionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(descriptionPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Event details section
        JPanel detailsPanel = createSectionPanel("Event Details");
        JPanel detailsContent = new JPanel(new GridLayout(0, 1, 0, 10));
        detailsContent.setOpaque(false);
        detailsContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        detailsContent.add(createDetailRow("Category:", event.getCategory().getName()));
        detailsContent.add(createDetailRow("Organizer:", event.getOrganizer().getName()));
        detailsContent.add(createDetailRow("Contact:", event.getOrganizer().getEmail()));

        detailsPanel.add(detailsContent, BorderLayout.CENTER);
        detailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(detailsPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Eligibility criteria section
        JPanel eligibilityPanel = createSectionPanel("Eligibility Criteria");
        JTextArea eligibilityText = new JTextArea(event.getRequirements());
        eligibilityText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        eligibilityText.setLineWrap(true);
        eligibilityText.setWrapStyleWord(true);
        eligibilityText.setEditable(false);
        eligibilityText.setBackground(null);
        eligibilityText.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        eligibilityPanel.add(eligibilityText, BorderLayout.CENTER);
        eligibilityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(eligibilityPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Feedback section
        JPanel feedbackPanel = createSectionPanel("Feedback");
        JPanel feedbackContent = new JPanel();
        feedbackContent.setLayout(new BoxLayout(feedbackContent, BoxLayout.Y_AXIS));
        feedbackContent.setOpaque(false);
        feedbackContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Add feedback form if user is registered and event is past
        if (isRegistered && event.isPast()) {
            JPanel formPanel = new JPanel(new GridLayout(0, 1, 0, 10));
            formPanel.setOpaque(false);

            // Rating selection
            JPanel ratingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            ratingPanel.setOpaque(false);
            JLabel ratingLabel = new JLabel("Rating:");
            ratingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            ratingPanel.add(ratingLabel);

            ButtonGroup ratingGroup = new ButtonGroup();
            JPanel starsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            starsPanel.setOpaque(false);
            for (int i = 1; i <= 5; i++) {
                JToggleButton starButton = new JToggleButton("★");
                starButton.setFont(new Font("Segoe UI", Font.PLAIN, 20));
                starButton.setForeground(Color.GRAY);
                starButton.setOpaque(false);
                starButton.setBorderPainted(false);
                starButton.setContentAreaFilled(false);
                final int rating = i;
                starButton.addActionListener(e -> {
                    for (int j = 0; j < starsPanel.getComponentCount(); j++) {
                        JToggleButton btn = (JToggleButton) starsPanel.getComponent(j);
                        btn.setForeground(j < rating ? Color.YELLOW : Color.GRAY);
                    }
                });
                ratingGroup.add(starButton);
                starsPanel.add(starButton);
            }
            ratingPanel.add(starsPanel);
            formPanel.add(ratingPanel);

            // Comment field
            JTextArea commentField = new JTextArea(3, 30);
            commentField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            commentField.setLineWrap(true);
            commentField.setWrapStyleWord(true);
            JScrollPane commentScroll = new JScrollPane(commentField);
            commentScroll.setBorder(BorderFactory.createTitledBorder("Comment"));

            // Submit button
            GradientButton submitButton = GradientButton.createPrimaryButton("Submit Feedback");
            submitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            submitButton.addActionListener(e -> {
                try {
                    int rating = 0;
                    for (int i = 0; i < starsPanel.getComponentCount(); i++) {
                        JToggleButton btn = (JToggleButton) starsPanel.getComponent(i);
                        if (btn.getForeground() == Color.YELLOW) {
                            rating = i + 1;
                        }
                    }
                    
                    if (rating == 0) {
                        JOptionPane.showMessageDialog(this,
                            "Please select a rating",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    feedbackController.submitFeedback(
                        currentUser,
                        event.getId(),
                        rating,
                        commentField.getText(),
                        false
                    );

                    JOptionPane.showMessageDialog(this,
                        "Thank you for your feedback!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);

                    refreshFeedbackSection(feedbackContent);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(this,
                        "Error submitting feedback: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            });

            formPanel.add(commentScroll);
            formPanel.add(submitButton);
            feedbackContent.add(formPanel);
        }

        // Display existing feedback
        try {
            List<Feedback> feedbacks = feedbackController.getFeedbackByEvent(event.getId());
            for (Feedback feedback : feedbacks) {
                JPanel feedbackItem = new JPanel(new BorderLayout());
                feedbackItem.setOpaque(false);
                feedbackItem.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

                JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
                header.setOpaque(false);
                header.add(new JLabel(feedback.getUserName()));
                header.add(new JLabel("★".repeat(feedback.getRating())));

                JTextArea comment = new JTextArea(feedback.getComment());
                comment.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                comment.setLineWrap(true);
                comment.setWrapStyleWord(true);
                comment.setEditable(false);
                comment.setBackground(null);

                feedbackItem.add(header, BorderLayout.NORTH);
                feedbackItem.add(comment, BorderLayout.CENTER);
                feedbackContent.add(feedbackItem);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error loading feedback: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }

        feedbackPanel.add(feedbackContent, BorderLayout.CENTER);
        feedbackPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(feedbackPanel);

        return contentPanel;
    }

    private void refreshFeedbackSection(JPanel feedbackContent) {
        feedbackContent.removeAll();
        try {
            List<Feedback> feedbacks = feedbackController.getFeedbackByEvent(event.getId());
            for (Feedback feedback : feedbacks) {
                JPanel feedbackItem = new JPanel(new BorderLayout());
                feedbackItem.setOpaque(false);
                feedbackItem.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));

                JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
                header.setOpaque(false);
                header.add(new JLabel(feedback.getUserName()));
                header.add(new JLabel("★".repeat(feedback.getRating())));

                JTextArea comment = new JTextArea(feedback.getComment());
                comment.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                comment.setLineWrap(true);
                comment.setWrapStyleWord(true);
                comment.setEditable(false);
                comment.setBackground(null);

                feedbackItem.add(header, BorderLayout.NORTH);
                feedbackItem.add(comment, BorderLayout.CENTER);
                feedbackContent.add(feedbackItem);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error refreshing feedback: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
        feedbackContent.revalidate();
        feedbackContent.repaint();
    }

    private JPanel createEventHeaderPanel() {
        JPanel headerPanel = new RoundedPanel(new BorderLayout(), Color.WHITE, 10);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title and date
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);

        JLabel titleLabel = new JLabel(event.getName());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titlePanel.add(titleLabel, BorderLayout.NORTH);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy");
        JLabel dateLabel = new JLabel(dateFormat.format(Date.from(event.getStartDateTime().atZone(ZoneId.systemDefault()).toInstant())));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(Color.GRAY);
        titlePanel.add(dateLabel, BorderLayout.CENTER);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        // Registration button
        if (!isRegistered && event.isAvailable()) {
            GradientButton registerButton = GradientButton.createPrimaryButton("Register Now");
            registerButton.addActionListener(e -> handleRegistration());
            headerPanel.add(registerButton, BorderLayout.EAST);
        } else if (isRegistered) {
            JLabel registeredLabel = new JLabel("✓ Registered");
            registeredLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            registeredLabel.setForeground(AppColors.SUCCESS);
            headerPanel.add(registeredLabel, BorderLayout.EAST);
        }

        return headerPanel;
    }

    private JPanel createSectionPanel(String title) {
        JPanel panel = new RoundedPanel(new BorderLayout(), Color.WHITE, 10);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(titleLabel, BorderLayout.NORTH);

        return panel;
    }

    private JPanel createDetailRow(String label, String value) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        row.add(labelComponent, BorderLayout.WEST);

        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        row.add(valueComponent, BorderLayout.CENTER);

        return row;
    }

    private void handleRegistration() {
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
            if (registrationController.isUserRegistered(currentUser.getId(), event.getId())) {
                JOptionPane.showMessageDialog(this,
                    "You are already registered for this event.",
                    "Already Registered",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Register the user
            Registration registration = registrationController.registerForEvent(currentUser, event.getId());
            
            // Update UI
            isRegistered = true;
            refreshUI();

            // Show success message
            JOptionPane.showMessageDialog(this,
                "You have successfully registered for " + event.getName() + "!",
                "Registration Successful",
                JOptionPane.INFORMATION_MESSAGE);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error during registration: " + e.getMessage(),
                "Registration Failed",
                JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(),
                "Registration Failed",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshUI() {
        // Refresh the content panel
        JPanel contentPanel = createContentPanel();
        
        // Get the scroll pane from the main panel
        JScrollPane scrollPane = (JScrollPane) getContentPane().getComponent(1);
        scrollPane.setViewportView(contentPanel);
        
        // Revalidate and repaint
        revalidate();
        repaint();
    }

    private void handleBack() {
        // Open the attendee dashboard
        new AttendeeDashboardNew().setVisible(true);
        dispose();
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

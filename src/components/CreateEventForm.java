package components;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import utils.AppColors;
import utils.UIUtils;
import java.util.Calendar;
import controllers.AuthController;
import controllers.EventController;
import controllers.NotificationController;
import models.User;
import models.Event;
import models.Venue;
import models.Category;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.sql.SQLException;
import controllers.VenueController;
import controllers.CategoryController;
import utils.FileUtils;
import controllers.UserController;
import models.Notification;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import models.Notification.NotificationType;
import java.util.Arrays;

/**
 * Reusable Create Event Form component
 * Extracted from CreateEventScreenNew to be used in multiple places
 */
public class CreateEventForm extends JPanel {
    private final String userRole; // "Admin" or "Organizer"
    private final Consumer<Boolean> onSubmitCallback; // Callback for when form is submitted (success/failure)

    // Form fields
    private JTextField eventNameField;
    private JTextField categoryField;
    private JTextArea descriptionArea;
    private JTextField dateField;
    private JTextField startTimeField;
    private JTextField endTimeField;
    private JTextField venueField;
    private JSpinner capacitySpinner;
    private JTextField contactField;
    private JTextArea eligibilityArea;
    private JButton imageButton;
    private JButton documentsButton;
    private JLabel imageLabel;
    private JLabel documentsLabel;
    private JLabel errorLabel;
    private File selectedImageFile;
    private File[] selectedDocuments;

    private AuthController authController;
    private EventController eventController;
    private NotificationController notificationController;
    private VenueController venueController;
    private UserController userController;

    /**
     * Create a new event form
     *
     * @param userRole The role of the user ("Admin" or "Organizer")
     * @param onSubmitCallback Callback function that receives a boolean indicating success/failure
     */
    public CreateEventForm(String userRole, Consumer<Boolean> onSubmitCallback) {
        this.userRole = userRole;
        this.onSubmitCallback = onSubmitCallback;

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Initialize controllers
        try {
            authController = AuthController.getInstance();
            eventController = EventController.getInstance();
            notificationController = NotificationController.getInstance();
            venueController = VenueController.getInstance();
            userController = UserController.getInstance();
        } catch (SQLException e) {
            System.err.println("Error initializing controllers: " + e.getMessage());
            // Depending on requirements, you might want to show an error message to the user
            // or disable the form.
        }

        // Create form panel
        JPanel formPanel = createEventForm();

        // Add to scroll pane for better usability
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createEventForm() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(AppColors.BACKGROUND_LIGHT);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Form title
        JLabel titleLabel = new JLabel("Create New Event");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(AppColors.TEXT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Form content - Using a central panel with a BoxLayout for vertical stacking
        JPanel formContent = new JPanel();
        formContent.setLayout(new BoxLayout(formContent, BoxLayout.Y_AXIS));
        formContent.setBackground(AppColors.BACKGROUND_LIGHT);
        formContent.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        // Section 1: Basic Info (Horizontal Panel)
        JPanel basicInfoPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        basicInfoPanel.setBackground(AppColors.BACKGROUND_LIGHT);

        JPanel leftBasicInfo = new JPanel(new GridBagLayout());
        leftBasicInfo.setBackground(AppColors.BACKGROUND_LIGHT);
        JPanel rightBasicInfo = new JPanel(new GridBagLayout());
        rightBasicInfo.setBackground(AppColors.BACKGROUND_LIGHT);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.weightx = 1.0;

        // Left Basic Info Column
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Event Name:");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(AppColors.TEXT_PRIMARY);
        leftBasicInfo.add(nameLabel, gbc);

        gbc.gridy = 1;
        eventNameField = new JTextField();
        eventNameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        leftBasicInfo.add(eventNameField, gbc);

        gbc.gridy = 2;
        JLabel categoryLabel = new JLabel("Category:");
        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        categoryLabel.setForeground(AppColors.TEXT_PRIMARY);
        leftBasicInfo.add(categoryLabel, gbc);

        gbc.gridy = 3;
        categoryField = new JTextField();
        categoryField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        leftBasicInfo.add(categoryField, gbc);

        gbc.gridy = 4;
        JLabel capacityLabel = new JLabel("Available Slots:");
        capacityLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        capacityLabel.setForeground(AppColors.TEXT_PRIMARY);
        leftBasicInfo.add(capacityLabel, gbc);

        gbc.gridy = 5;
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(50, 1, 1000, 1);
        capacitySpinner = new JSpinner(spinnerModel);
        capacitySpinner.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        leftBasicInfo.add(capacitySpinner, gbc);

        // Right Basic Info Column
        gbc.gridy = 0;
        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        dateLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        dateLabel.setForeground(AppColors.TEXT_PRIMARY);
        rightBasicInfo.add(dateLabel, gbc);

        gbc.gridy = 1;
        dateField = new JTextField();
        dateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rightBasicInfo.add(dateField, gbc);

        gbc.gridy = 2;
        JLabel timeLabel = new JLabel("Time:");
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        timeLabel.setForeground(AppColors.TEXT_PRIMARY);
        rightBasicInfo.add(timeLabel, gbc);

        gbc.gridy = 3;
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        timePanel.setOpaque(false);

        JLabel startLabel = new JLabel("Start:");
        startLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        startTimeField = new JTextField(8);
        startTimeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel endLabel = new JLabel("End:");
        endLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        endTimeField = new JTextField(8);
        endTimeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        timePanel.add(startLabel);
        timePanel.add(startTimeField);
        timePanel.add(endLabel);
        timePanel.add(endTimeField);

        rightBasicInfo.add(timePanel, gbc);

        gbc.gridy = 4;
        JLabel venueLabel = new JLabel("Venue:");
        venueLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        venueLabel.setForeground(AppColors.TEXT_PRIMARY);
        rightBasicInfo.add(venueLabel, gbc);

        gbc.gridy = 5;
        venueField = new JTextField();
        venueField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        rightBasicInfo.add(venueField, gbc);


        basicInfoPanel.add(leftBasicInfo);
        basicInfoPanel.add(rightBasicInfo);

        // Section 2: Description and Eligibility (Vertical Stack)
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(AppColors.BACKGROUND_LIGHT);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel descLabel = new JLabel("Description:");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        descLabel.setForeground(AppColors.TEXT_PRIMARY);
        detailsPanel.add(descLabel);
        detailsPanel.add(Box.createVerticalStrut(5));

        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        detailsPanel.add(descScroll);
        detailsPanel.add(Box.createVerticalStrut(15));

        JLabel eligibilityLabel = new JLabel("Eligibility Criteria:");
        eligibilityLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        eligibilityLabel.setForeground(AppColors.TEXT_PRIMARY);
        detailsPanel.add(eligibilityLabel);
        detailsPanel.add(Box.createVerticalStrut(5));

        eligibilityArea = new JTextArea(3, 20);
        eligibilityArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        eligibilityArea.setLineWrap(true);
        eligibilityArea.setWrapStyleWord(true);
        JScrollPane eligibilityScroll = new JScrollPane(eligibilityArea);
        detailsPanel.add(eligibilityScroll);

        // Section 3: Contact and Media (Horizontal Panel)
        JPanel contactMediaPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        contactMediaPanel.setBackground(AppColors.BACKGROUND_LIGHT);

        JPanel leftContactMedia = new JPanel(new GridBagLayout());
        leftContactMedia.setBackground(AppColors.BACKGROUND_LIGHT);
        JPanel rightContactMedia = new JPanel(new GridBagLayout());
        rightContactMedia.setBackground(AppColors.BACKGROUND_LIGHT);

        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.weightx = 1.0;

        // Left Contact Column
        gbc.gridy = 0;
        JLabel contactLabel = new JLabel("Contact Info:");
        contactLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        contactLabel.setForeground(AppColors.TEXT_PRIMARY);
        leftContactMedia.add(contactLabel, gbc);

        gbc.gridy = 1;
        contactField = new JTextField();
        contactField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        leftContactMedia.add(contactField, gbc);

        // Right Media Column
        gbc.gridy = 0;
        JLabel imageFileLabel = new JLabel("Event Image:");
        imageFileLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        imageFileLabel.setForeground(AppColors.TEXT_PRIMARY);
        rightContactMedia.add(imageFileLabel, gbc);

        gbc.gridy = 1;
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        imagePanel.setOpaque(false);

        imageButton = UIUtils.createButton("CHOOSE FILE", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.SMALL);
        imageButton.addActionListener(e -> this.handleImageUpload());

        imageLabel = new JLabel("No file chosen");
        imageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        imageLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        imageLabel.setForeground(AppColors.TEXT_SECONDARY);

        imagePanel.add(imageButton);
        imagePanel.add(imageLabel);
        rightContactMedia.add(imagePanel, gbc);

        gbc.gridy = 2;
        JLabel docsLabel = new JLabel("Additional Documents:");
        docsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        docsLabel.setForeground(AppColors.TEXT_PRIMARY);
        rightContactMedia.add(docsLabel, gbc);

        gbc.gridy = 3;
        JPanel docsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        docsPanel.setOpaque(false);

        documentsButton = UIUtils.createButton("CHOOSE FILES", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.SMALL);
        documentsButton.addActionListener(e -> this.handleDocumentsUpload());

        documentsLabel = new JLabel("No files chosen");
        documentsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        documentsLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        documentsLabel.setForeground(AppColors.TEXT_SECONDARY);

        docsPanel.add(documentsButton);
        docsPanel.add(documentsLabel);
        rightContactMedia.add(docsPanel, gbc);

        contactMediaPanel.add(leftContactMedia);
        contactMediaPanel.add(rightContactMedia);

        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        errorLabel.setForeground(AppColors.ERROR);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        JButton cancelButton = UIUtils.createButton("CANCEL", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.NORMAL);
        cancelButton.addActionListener(e -> this.handleCancel());

        JButton createButton = UIUtils.createButton("CREATE EVENT", null, UIUtils.ButtonType.PRIMARY, UIUtils.ButtonSize.NORMAL);
        createButton.addActionListener(e -> this.handleCreateEvent());

        buttonsPanel.add(cancelButton);
        buttonsPanel.add(createButton);

        // Add sections to the main form content panel
        formContent.add(basicInfoPanel);
        formContent.add(Box.createVerticalStrut(20));
        formContent.add(detailsPanel);
        formContent.add(Box.createVerticalStrut(20));
        formContent.add(contactMediaPanel);
        formContent.add(Box.createVerticalStrut(20));
        formContent.add(errorLabel);
        formContent.add(Box.createVerticalGlue()); // Pushes buttons to the bottom

        mainPanel.add(formContent, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private void handleImageUpload() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".jpeg") ||
                       name.endsWith(".png") || name.endsWith(".gif");
            }
            public String getDescription() {
                return "Image Files (*.jpg, *.jpeg, *.png, *.gif)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedImageFile = fileChooser.getSelectedFile();
            imageLabel.setText(selectedImageFile.getName());
        }
    }

    private void handleDocumentsUpload() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedDocuments = fileChooser.getSelectedFiles();
            documentsLabel.setText(selectedDocuments.length + " file(s) chosen");
        }
    }

    private void handleCancel() {
        // Clear form fields
        clearForm();

        // Notify parent that form was cancelled
        if (onSubmitCallback != null) {
            onSubmitCallback.accept(false);
        }
    }

    private void handleCreateEvent() {
        // Validate inputs
        if (eventNameField.getText().trim().isEmpty()) {
            errorLabel.setText("Please enter an event name.");
            return;
        }
        if (categoryField.getText().trim().isEmpty()) {
            errorLabel.setText("Please enter a category.");
            return;
        }
        if (descriptionArea.getText().trim().isEmpty()) {
            errorLabel.setText("Please enter a description.");
            return;
        }
        if (dateField.getText().trim().isEmpty()) {
            errorLabel.setText("Please enter a date.");
            return;
        }
        if (startTimeField.getText().trim().isEmpty()) {
            errorLabel.setText("Please enter a start time.");
            return;
        }
        if (endTimeField.getText().trim().isEmpty()) {
            errorLabel.setText("Please enter an end time.");
            return;
        }
        if (venueField.getText().trim().isEmpty()) {
            errorLabel.setText("Please enter a venue.");
            return;
        }
        if (contactField.getText().trim().isEmpty()) {
            errorLabel.setText("Please enter contact information.");
            return;
        }
        if (eligibilityArea.getText().trim().isEmpty()) {
            errorLabel.setText("Please enter eligibility criteria.");
            return;
        }

        // Parse date and time
        String dateStr = dateField.getText().trim();
        String startTimeStr = startTimeField.getText().trim();
        String endTimeStr = endTimeField.getText().trim();

        if (!isValidDateFormat(dateStr)) {
            errorLabel.setText("Invalid date format. Please use YYYY-MM-DD.");
            return;
        }
        if (!isValidTimeFormat(startTimeStr) || !isValidTimeFormat(endTimeStr)) {
            errorLabel.setText("Invalid time format. Please use HH:MM.");
            return;
        }
        if (!isEndTimeAfterStartTime(startTimeStr, endTimeStr)) {
            errorLabel.setText("End time must be after start time.");
            return;
        }

        try {
            // Parse date and time
            LocalDateTime startDateTime = LocalDateTime.parse(dateStr + "T" + startTimeStr);
            LocalDateTime endDateTime = LocalDateTime.parse(dateStr + "T" + endTimeStr);

            // Check if date is in past
            if (isEventDateTimeInPast(dateStr, startTimeStr)) {
                errorLabel.setText("Event cannot be in the past.");
                return;
            }

            // Get form values
            String eventName = eventNameField.getText().trim();
            String description = descriptionArea.getText().trim();
            int eventCapacity = (Integer) capacitySpinner.getValue();
            String venueName = venueField.getText().trim();
            String categoryName = categoryField.getText().trim();
            String contactInfo = contactField.getText().trim();
            String eligibilityCriteria = eligibilityArea.getText().trim();

            // Create venue object
            Venue venue = new Venue();
            venue.setName(venueName);

            // Create event
            Event event = eventController.createEvent(
                eventName,
                description,
                startDateTime,
                endDateTime,
                venue,
                eventCapacity,
                categoryName,
                selectedImageFile != null ? selectedImageFile.getPath() : null,
                selectedDocuments != null ? String.join(",", Arrays.stream(selectedDocuments)
                    .map(File::getPath)
                    .toArray(String[]::new)) : null
            );

            // Clear form
            clearForm();

            // Notify success
            onSubmitCallback.accept(true);
        } catch (Exception e) {
            errorLabel.setText("Error creating event: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private boolean isDateInPast(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            Date eventDate = sdf.parse(dateStr);
            Date today = Calendar.getInstance().getTime();
            
            // Reset time part for both dates to compare only dates
            Calendar eventCal = Calendar.getInstance();
            eventCal.setTime(eventDate);
            eventCal.set(Calendar.HOUR_OF_DAY, 0);
            eventCal.set(Calendar.MINUTE, 0);
            eventCal.set(Calendar.SECOND, 0);
            eventCal.set(Calendar.MILLISECOND, 0);
            
            Calendar todayCal = Calendar.getInstance();
            todayCal.setTime(today);
            todayCal.set(Calendar.HOUR_OF_DAY, 0);
            todayCal.set(Calendar.MINUTE, 0);
            todayCal.set(Calendar.SECOND, 0);
            todayCal.set(Calendar.MILLISECOND, 0);
            
            return eventCal.before(todayCal);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isEventDateTimeInPast(String dateStr, String timeStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm a");
            sdf.setLenient(false);
            String dateTimeStr = dateStr + " " + timeStr;
            Date eventDateTime = sdf.parse(dateTimeStr);
            return eventDateTime.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidDateFormat(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            sdf.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isValidTimeFormat(String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            sdf.setLenient(false);
            sdf.parse(time);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isEndTimeAfterStartTime(String startTime, String endTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
            Date start = sdf.parse(startTime);
            Date end = sdf.parse(endTime);
            return end.after(start);
        } catch (Exception e) {
            return false;
        }
    }

    private void clearForm() {
        eventNameField.setText("");
        categoryField.setText("");
        descriptionArea.setText("");
        dateField.setText("");
        startTimeField.setText("");
        endTimeField.setText("");
        venueField.setText("");
        capacitySpinner.setValue(50);
        contactField.setText("");
        eligibilityArea.setText("");
        imageLabel.setText("No file chosen");
        documentsLabel.setText("No files chosen");
        selectedImageFile = null;
        selectedDocuments = null;
        errorLabel.setText(" ");
    }
}

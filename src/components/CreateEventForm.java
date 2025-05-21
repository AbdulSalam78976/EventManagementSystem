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
import models.User.UserRole;
import models.Event;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.sql.SQLException;
import utils.FileUtils;
import controllers.UserController;
import models.Notification;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import models.Notification.NotificationType;
import java.util.Arrays;
import java.io.IOException;
import java.nio.file.Files;
import javax.swing.filechooser.FileNameExtensionFilter;
import utils.UIConstants;
import components.RoundedPanel;
import java.time.LocalDate;
import java.time.LocalTime;
import java.io.ByteArrayOutputStream;
import screens.AdminDashboardNew;
import screens.OrganizerDashboard;

/**
 * Reusable Create Event Form component
 * Extracted from CreateEventScreenNew to be used in multiple places
 */
public class CreateEventForm extends JPanel {
    private final String userRole; // "Admin" or "Organizer"
    private final Consumer<Boolean> onSubmitCallback; // Callback for when form is submitted (success/failure)

    // Form fields
    public JTextField eventNameField;
    public JTextField categoryField;
    public JTextArea descriptionArea;
    public JTextField dateField;
    public JTextField startTimeField;
    public JTextField endTimeField;
    public JTextField registrationDeadlineField;
    public JTextField venueNameField;
    public JSpinner totalSlotsSpinner;
    public JTextArea eligibilityArea;
    public JButton imageButton;
    public JButton documentsButton;
    public JLabel imageLabel;
    public JLabel documentsLabel;
    public JLabel errorLabel;
    private File selectedImageFile;
    private File[] selectedDocuments;
    private byte[] selectedImageData;
    private String selectedImageType;
    private byte[] selectedDocumentsData;
    private String selectedDocumentsType;

    private AuthController authController;
    private EventController eventController;
    private NotificationController notificationController;
    private UserController userController;

    // For tracking if we're editing an existing event
    private boolean isEditMode = false;
    private Event eventBeingEdited;

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
            userController = UserController.getInstance();
        } catch (SQLException e) {
            System.err.println("Error initializing controllers: " + e.getMessage());
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
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title - will be updated if in edit mode
        JLabel titleLabel = UIUtils.createLabel("Create Event", UIConstants.HEADER_FONT, AppColors.TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        formPanel.add(titleLabel, BorderLayout.NORTH);

        // Form fields panel
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        // Event Name
        fieldsPanel.add(UIUtils.createLabel("Event Name *", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY), gbc);
        gbc.gridy++;
        eventNameField = UIUtils.createRoundedTextField();
        eventNameField.setToolTipText("Enter a descriptive name for your event");
        fieldsPanel.add(eventNameField, gbc);

        // Category
        gbc.gridy++;
        fieldsPanel.add(UIUtils.createLabel("Category *", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY), gbc);
        gbc.gridy++;
        categoryField = UIUtils.createRoundedTextField();
        categoryField.setToolTipText("Enter the event category (e.g., Conference, Workshop, Seminar)");
        fieldsPanel.add(categoryField, gbc);

        // Description
        gbc.gridy++;
        fieldsPanel.add(UIUtils.createLabel("Description *", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY), gbc);
        gbc.gridy++;
        descriptionArea = UIUtils.createRoundedTextArea();
        descriptionArea.setRows(4);
        descriptionArea.setToolTipText("Provide a detailed description of the event");
        JScrollPane descScroll = UIUtils.createRoundedScrollPane(descriptionArea);
        fieldsPanel.add(descScroll, gbc);

        // Date and Time
        gbc.gridy++;
        JPanel dateTimePanel = new JPanel(new GridBagLayout());
        dateTimePanel.setBackground(Color.WHITE);
        GridBagConstraints dtGbc = new GridBagConstraints();
        dtGbc.fill = GridBagConstraints.HORIZONTAL;
        dtGbc.insets = new Insets(5, 5, 5, 5);
        dtGbc.gridx = 0;
        dtGbc.gridy = 0;
        dtGbc.weightx = 1.0;

        dateTimePanel.add(UIUtils.createLabel("Event Date * (YYYY-MM-DD)", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY), dtGbc);
        dtGbc.gridy++;
        dateField = UIUtils.createRoundedTextField();
        dateField.setToolTipText("Enter the event date in YYYY-MM-DD format");
        dateTimePanel.add(dateField, dtGbc);

        dtGbc.gridy++;
        dateTimePanel.add(UIUtils.createLabel("Event Time * (24-hour format, HH:mm)", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY), dtGbc);
        dtGbc.gridy++;
        JPanel timePanel = new JPanel(new GridLayout(1, 4, 5, 0));
        timePanel.setBackground(Color.WHITE);

        startTimeField = UIUtils.createRoundedTextField();
        startTimeField.setToolTipText("Start time in 24-hour format (e.g., 14:30)");
        endTimeField = UIUtils.createRoundedTextField();
        endTimeField.setToolTipText("End time in 24-hour format (e.g., 16:30)");

        timePanel.add(UIUtils.createLabel("Start:", UIConstants.BODY_FONT, AppColors.TEXT_SECONDARY));
        timePanel.add(startTimeField);
        timePanel.add(UIUtils.createLabel("End:", UIConstants.BODY_FONT, AppColors.TEXT_SECONDARY));
        timePanel.add(endTimeField);

        dateTimePanel.add(timePanel, dtGbc);

        // Registration Deadline
        dtGbc.gridy++;
        dateTimePanel.add(UIUtils.createLabel("Registration Deadline * (YYYY-MM-DD HH:mm)", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY), dtGbc);
        dtGbc.gridy++;
        registrationDeadlineField = UIUtils.createRoundedTextField();
        registrationDeadlineField.setToolTipText("Enter the registration deadline in YYYY-MM-DD HH:mm format");
        dateTimePanel.add(registrationDeadlineField, dtGbc);

        fieldsPanel.add(dateTimePanel, gbc);

        // Venue
        gbc.gridy++;
        fieldsPanel.add(UIUtils.createLabel("Venue *", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY), gbc);
        gbc.gridy++;
        venueNameField = UIUtils.createRoundedTextField();
        venueNameField.setToolTipText("Enter the venue name or location");
        fieldsPanel.add(venueNameField, gbc);

        // Total Slots
        gbc.gridy++;
        fieldsPanel.add(UIUtils.createLabel("Total Slots *", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY), gbc);
        gbc.gridy++;
        totalSlotsSpinner = new JSpinner(new SpinnerNumberModel(50, 1, 1000, 1));
        totalSlotsSpinner.setToolTipText("Enter the maximum number of participants");
        fieldsPanel.add(totalSlotsSpinner, gbc);

        // Eligibility Criteria
        gbc.gridy++;
        fieldsPanel.add(UIUtils.createLabel("Eligibility Criteria *", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY), gbc);
        gbc.gridy++;
        eligibilityArea = UIUtils.createRoundedTextArea();
        eligibilityArea.setRows(3);
        eligibilityArea.setToolTipText("Specify who can attend this event");
        JScrollPane eligibilityScroll = UIUtils.createRoundedScrollPane(eligibilityArea);
        fieldsPanel.add(eligibilityScroll, gbc);

        // Image Upload
        gbc.gridy++;
        JPanel imagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        imagePanel.setBackground(Color.WHITE);
        imageButton = UIUtils.createButton("Upload Image", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.SMALL);
        imageButton.addActionListener(e -> selectImage());
        imageLabel = UIUtils.createLabel("No image selected", UIConstants.BODY_FONT, AppColors.TEXT_SECONDARY);
        imagePanel.add(imageButton);
        imagePanel.add(imageLabel);
        fieldsPanel.add(imagePanel, gbc);

        // Documents Upload
        gbc.gridy++;
        JPanel docsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        docsPanel.setBackground(Color.WHITE);
        documentsButton = UIUtils.createButton("Upload Documents", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.SMALL);
        documentsButton.addActionListener(e -> handleDocumentsUpload());
        documentsLabel = UIUtils.createLabel("No files chosen", UIConstants.BODY_FONT, AppColors.TEXT_SECONDARY);
        docsPanel.add(documentsButton);
        docsPanel.add(documentsLabel);
        fieldsPanel.add(docsPanel, gbc);

        // Error Label
        gbc.gridy++;
        errorLabel = UIUtils.createLabel(" ", UIConstants.BODY_FONT, AppColors.ERROR);
        fieldsPanel.add(errorLabel, gbc);

        // Buttons
        gbc.gridy++;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton submitButton = UIUtils.createButton("Create Event", null, UIUtils.ButtonType.PRIMARY, UIUtils.ButtonSize.NORMAL);
        submitButton.addActionListener(e -> {
            if (isEditMode && eventBeingEdited != null) {
                handleUpdateEvent();
            } else {
                handleCreateEvent();
            }
        });

        JButton clearButton = UIUtils.createButton("Clear Form", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.NORMAL);
        clearButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear all form fields?",
                "Clear Form",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (choice == JOptionPane.YES_OPTION) {
                clearForm();
            }
        });

        JButton cancelButton = UIUtils.createButton("Cancel", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.NORMAL);
        cancelButton.addActionListener(e -> handleCancel());

        buttonPanel.add(submitButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(cancelButton);
        fieldsPanel.add(buttonPanel, gbc);

        // Add fields panel to a scroll pane
        JScrollPane scrollPane = new JScrollPane(fieldsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        formPanel.add(scrollPane, BorderLayout.CENTER);

        return formPanel;
    }

    private void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image files", "jpg", "jpeg", "png", "gif"));

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                // Read image file into byte array
                selectedImageData = Files.readAllBytes(selectedFile.toPath());
                selectedImageType = Files.probeContentType(selectedFile.toPath());

                // Display image preview
                ImageIcon icon = new ImageIcon(selectedImageData);
                Image image = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                imageLabel.setIcon(new ImageIcon(image));
                imageLabel.setText(selectedFile.getName());
            } catch (IOException ex) {
                errorLabel.setText("Error loading image: " + ex.getMessage());
            }
        }
    }

    private void handleDocumentsUpload() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setMultiSelectionEnabled(true);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedDocuments = fileChooser.getSelectedFiles();
            documentsLabel.setText(selectedDocuments.length + " file(s) chosen");

            try {
                // Combine all documents into a single byte array
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                for (File file : selectedDocuments) {
                    byte[] fileData = Files.readAllBytes(file.toPath());
                    outputStream.write(fileData);
                }
                selectedDocumentsData = outputStream.toByteArray();
                selectedDocumentsType = "application/zip"; // Store as a zip-like format
            } catch (IOException ex) {
                errorLabel.setText("Error loading documents: " + ex.getMessage());
            }
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
        if (venueNameField.getText().trim().isEmpty()) {
            errorLabel.setText("Please enter a venue name.");
            return;
        }
        if (eligibilityArea.getText().trim().isEmpty()) {
            errorLabel.setText("Please enter eligibility criteria.");
            return;
        }
        if (registrationDeadlineField.getText().trim().isEmpty()) {
            errorLabel.setText("Please enter a registration deadline.");
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
            errorLabel.setText("Invalid time format. Please use HH:mm (24-hour format, e.g., 14:30)");
            return;
        }
        if (!isEndTimeAfterStartTime(startTimeStr, endTimeStr)) {
            errorLabel.setText("End time must be after start time.");
            return;
        }

        try {
            // Parse date and time
            LocalDate date = LocalDate.parse(dateStr);
            LocalTime startTime = LocalTime.parse(startTimeStr);
            LocalTime endTime = LocalTime.parse(endTimeStr);

            LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
            LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

            // Parse registration deadline
            LocalDateTime registrationDeadline = LocalDateTime.parse(registrationDeadlineField.getText().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

            // Check if registration deadline is before event start
            if (registrationDeadline.isAfter(startDateTime)) {
                errorLabel.setText("Registration deadline must be before event start time.");
                return;
            }

            // Check if registration deadline is in the future
            if (registrationDeadline.isBefore(LocalDateTime.now())) {
                errorLabel.setText("Registration deadline must be in the future.");
                return;
            }

            // Get form values
            String eventName = eventNameField.getText().trim();
            String description = descriptionArea.getText().trim();
            int totalSlots = (Integer) totalSlotsSpinner.getValue();
            String venueName = venueNameField.getText().trim();
            String categoryName = categoryField.getText().trim();
            String eligibilityCriteria = eligibilityArea.getText().trim();

            // Get current user
            User currentUser = authController.getCurrentUser();
            if (currentUser == null) {
                errorLabel.setText("Error: No user logged in");
                return;
            }

            // Create event with current user as organizer
            Event event = new Event(
                eventName,
                description,
                startDateTime,
                registrationDeadline,
                venueName,
                totalSlots,
                currentUser, // Current user is set as organizer
                categoryName
            );

            // Set additional fields
            event.setEligibilityCriteria(eligibilityCriteria);
            event.setMainImage(selectedImageData);
            event.setMainImageType(selectedImageType);
            event.setAdditionalDocuments(selectedDocumentsData);
            event.setAdditionalDocumentsType(selectedDocumentsType);

            // Set status based on user role
            // If admin creates event -> APPROVED (auto-published)
            // If organizer creates event -> PENDING (needs admin approval)
            event.setStatus(userRole.equals("Admin") ? Event.EventStatus.APPROVED : Event.EventStatus.PENDING);

            // Save event with the current user as organizer
            event = eventController.createEvent(
                event.getTitle(),
                event.getDescription(),
                event.getEventDate(),
                event.getRegistrationDeadline(),
                event.getVenueName(),
                event.getTotalSlots(),
                currentUser, // Current user is set as organizer
                event.getCategory(),
                event.getMainImage(),
                event.getMainImageType(),
                event.getAdditionalDocuments(),
                event.getAdditionalDocumentsType()
            );

            // Show appropriate message based on user role
            if (userRole.equals("Admin")) {
                // For admin, just show success message - no notification needed since event is auto-approved
                JOptionPane.showMessageDialog(this,
                    "Event created successfully and published! The event is now visible to attendees.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                // Only send notification to admins if the creator is an organizer
                notificationController.sendNotificationToRole(
                    "New Event Request",
                    "A new event '" + eventName + "' has been requested by organizer " + currentUser.getName(),
                    NotificationType.ADMIN,
                    User.UserRole.ADMIN
                );

                JOptionPane.showMessageDialog(this,
                    "Event request submitted successfully! Please wait for admin approval.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            }

            // Clear form
            clearForm();

            // Notify success and trigger UI refresh
            if (onSubmitCallback != null) {
                onSubmitCallback.accept(true);
            }

            // Find parent window and refresh if it's a dashboard
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            if (parentWindow instanceof AdminDashboardNew) {
                ((AdminDashboardNew) parentWindow).loadDashboardData();
            } else if (parentWindow instanceof OrganizerDashboard) {
                ((OrganizerDashboard) parentWindow).loadDashboardData();
            }
        } catch (DateTimeParseException e) {
            errorLabel.setText("Invalid date/time format. Please use YYYY-MM-DD for date and HH:mm for time.");
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
            // Parse date and time separately
            LocalDate date = LocalDate.parse(dateStr);
            LocalTime time = LocalTime.parse(timeStr);
            LocalDateTime eventDateTime = LocalDateTime.of(date, time);

            return eventDateTime.isBefore(LocalDateTime.now());
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
            // Use 24-hour format (HH:mm)
            if (!time.matches("^([0-1][0-9]|2[0-3]):[0-5][0-9]$")) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isEndTimeAfterStartTime(String startTime, String endTime) {
        try {
            // Parse times in 24-hour format
            String[] startParts = startTime.split(":");
            String[] endParts = endTime.split(":");

            int startHour = Integer.parseInt(startParts[0]);
            int startMinute = Integer.parseInt(startParts[1]);
            int endHour = Integer.parseInt(endParts[0]);
            int endMinute = Integer.parseInt(endParts[1]);

            if (endHour > startHour) {
                return true;
            }
            if (endHour == startHour) {
                return endMinute > startMinute;
            }
            return false;
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
        registrationDeadlineField.setText("");
        venueNameField.setText("");
        totalSlotsSpinner.setValue(50);
        eligibilityArea.setText("");
        imageLabel.setText("No image selected");
        documentsLabel.setText("No files chosen");
        selectedImageData = null;
        selectedImageType = null;
        selectedImageFile = null;
        selectedDocuments = null;
        selectedDocumentsData = null;
        selectedDocumentsType = null;
        errorLabel.setText(" ");
    }

    /**
     * Set the form data from an existing event
     * @param event The event to load data from
     */
    public void setEventData(Event event) {
        // Set edit mode flag and store the event being edited
        isEditMode = true;
        eventBeingEdited = event;

        // Update form title
        Container parent = getParent();
        while (parent != null && !(parent instanceof JDialog)) {
            parent = parent.getParent();
        }
        if (parent instanceof JDialog) {
            ((JDialog) parent).setTitle("Edit Event");
        }

        // Update the form fields
        Component[] components = ((JPanel)getComponent(0)).getComponents();
        for (Component c : components) {
            if (c instanceof JLabel && "Create Event".equals(((JLabel)c).getText())) {
                ((JLabel)c).setText("Edit Event");
                break;
            }
        }

        // Update the submit button text
        Component[] formComponents = ((JPanel)((JScrollPane)getComponent(0)).getViewport().getView()).getComponents();
        for (Component c : formComponents) {
            if (c instanceof JPanel) {
                Component[] panelComponents = ((JPanel)c).getComponents();
                for (Component pc : panelComponents) {
                    if (pc instanceof JButton && "Create Event".equals(((JButton)pc).getText())) {
                        ((JButton)pc).setText("Update Event");
                        break;
                    }
                }
            }
        }

        // Fill in the form fields
        eventNameField.setText(event.getTitle());
        categoryField.setText(event.getCategory());
        descriptionArea.setText(event.getDescription());
        dateField.setText(event.getEventDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

        // Format times in 24-hour format for proper parsing
        startTimeField.setText(event.getEventDate().format(DateTimeFormatter.ofPattern("HH:mm")));

        // Calculate end time as 1 hour after start time
        LocalDateTime endTime = event.getEventDate().plusHours(1);
        endTimeField.setText(endTime.format(DateTimeFormatter.ofPattern("HH:mm")));

        // Format registration deadline
        registrationDeadlineField.setText(event.getRegistrationDeadline().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

        venueNameField.setText(event.getVenueName());
        totalSlotsSpinner.setValue(event.getTotalSlots());
        eligibilityArea.setText(event.getEligibilityCriteria());

        // Set image data
        if (event.getMainImage() != null) {
            selectedImageData = event.getMainImage();
            selectedImageType = event.getMainImageType();

            // Display image preview
            ImageIcon icon = new ImageIcon(selectedImageData);
            Image image = icon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(image));
            imageLabel.setText("Current image");
        }

        // Set documents data
        if (event.getAdditionalDocuments() != null) {
            selectedDocumentsData = event.getAdditionalDocuments();
            selectedDocumentsType = event.getAdditionalDocumentsType();
            documentsLabel.setText("Current documents");
        }
    }

    /**
     * Handle updating an existing event
     */
    private void handleUpdateEvent() {
        if (eventBeingEdited == null) {
            errorLabel.setText("Error: No event to update");
            return;
        }

        // Validate inputs (same validation as create event)
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
        if (venueNameField.getText().trim().isEmpty()) {
            errorLabel.setText("Please enter a venue name.");
            return;
        }
        if (eligibilityArea.getText().trim().isEmpty()) {
            errorLabel.setText("Please enter eligibility criteria.");
            return;
        }
        if (registrationDeadlineField.getText().trim().isEmpty()) {
            errorLabel.setText("Please enter a registration deadline.");
            return;
        }

        try {
            // Parse date and time
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            LocalTime startTime = LocalTime.parse(startTimeField.getText().trim());

            LocalDateTime startDateTime = LocalDateTime.of(date, startTime);

            // Parse registration deadline
            LocalDateTime registrationDeadline = LocalDateTime.parse(
                registrationDeadlineField.getText().trim(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            );

            // Check if registration deadline is before event start
            if (registrationDeadline.isAfter(startDateTime)) {
                errorLabel.setText("Registration deadline must be before event start time.");
                return;
            }

            // Update the event object
            eventBeingEdited.setTitle(eventNameField.getText().trim());
            eventBeingEdited.setDescription(descriptionArea.getText().trim());
            eventBeingEdited.setCategory(categoryField.getText().trim());
            eventBeingEdited.setVenueName(venueNameField.getText().trim());
            eventBeingEdited.setTotalSlots((Integer) totalSlotsSpinner.getValue());
            eventBeingEdited.setEligibilityCriteria(eligibilityArea.getText().trim());
            eventBeingEdited.setEventDate(startDateTime);
            eventBeingEdited.setRegistrationDeadline(registrationDeadline);

            // Update image and documents if changed
            if (selectedImageData != null) {
                eventBeingEdited.setMainImage(selectedImageData);
                eventBeingEdited.setMainImageType(selectedImageType);
            }

            if (selectedDocumentsData != null) {
                eventBeingEdited.setAdditionalDocuments(selectedDocumentsData);
                eventBeingEdited.setAdditionalDocumentsType(selectedDocumentsType);
            }

            // Save the updated event
            eventController.updateEvent(eventBeingEdited);

            // Notify success and trigger UI refresh
            if (onSubmitCallback != null) {
                onSubmitCallback.accept(true);
            }

        } catch (DateTimeParseException e) {
            errorLabel.setText("Invalid date/time format. Please use YYYY-MM-DD for date and HH:mm for time.");
        } catch (Exception e) {
            errorLabel.setText("Error updating event: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public Event getEventData() {
        // Parse date and time
        LocalDateTime eventDate = null;
        LocalDateTime registrationDeadline = null;
        try {
            String dateStr = dateField.getText();
            String startTimeStr = startTimeField.getText();
            String endTimeStr = endTimeField.getText();

            eventDate = LocalDateTime.parse(dateStr + "T" + startTimeStr);
            registrationDeadline = LocalDateTime.parse(dateStr + "T" + endTimeStr);
        } catch (DateTimeParseException e) {
            errorLabel.setText("Invalid date/time format");
            return null;
        }

        // Get organizer
        User organizer = authController.getCurrentUser();
        if (organizer == null) {
            errorLabel.setText("No user logged in");
            return null;
        }

        Event event = new Event(
            eventNameField.getText(),
            descriptionArea.getText(),
            eventDate,
            registrationDeadline,
            venueNameField.getText(),
            (Integer) totalSlotsSpinner.getValue(),
            organizer,
            categoryField.getText()
        );

        // Set image data
        event.setMainImage(selectedImageData);
        event.setMainImageType(selectedImageType);

        // Set additional fields
        event.setEligibilityCriteria(eligibilityArea.getText());

        return event;
    }
}

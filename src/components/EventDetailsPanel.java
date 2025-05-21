package components;

import models.Event;
import utils.UIUtils;
import utils.AppColors;
import utils.UIConstants;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.time.format.DateTimeFormatter;
import controllers.EventController;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class EventDetailsPanel extends JPanel {
    private final Event event;
    private final ActionListener onEdit;
    private final ActionListener onApprove;
    private final ActionListener onReject;
    private final ActionListener onClose;
    private boolean isEditing = false;

    // Editable fields
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JTextField categoryField;
    private JTextField venueField;
    private JSpinner eventDateSpinner;
    private JSpinner regDeadlineSpinner;
    private JSpinner slotsSpinner;
    private JTextArea eligibilityArea;

    public EventDetailsPanel(Event event, ActionListener onEdit, ActionListener onApprove, ActionListener onReject, ActionListener onClose) {
        this.event = event;
        this.onEdit = onEdit;
        this.onApprove = onApprove;
        this.onReject = onReject;
        this.onClose = onClose;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        initializeUI();
    }

    public EventDetailsPanel(Event event) {
        this(event, null, null, null, null);
    }

    private void initializeUI() {
        removeAll();
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);

        // Header Panel
        RoundedPanel headerPanel = new RoundedPanel(new BorderLayout(10, 0), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM, true);
        headerPanel.setBorder(UIUtils.createRoundedBorderWithPadding(AppColors.BORDER, UIConstants.CORNER_RADIUS_MEDIUM, 1, 15));

        JPanel titleStatusPanel = new JPanel(new BorderLayout(10, 5));
        titleStatusPanel.setOpaque(false);
        if (isEditing) {
            titleField = new JTextField(event.getTitle());
            titleField.setFont(UIConstants.HEADER_FONT);
            titleStatusPanel.add(titleField, BorderLayout.CENTER);
        } else {
            JLabel titleLabel = UIUtils.createLabel(event.getTitle(), UIConstants.HEADER_FONT, AppColors.TEXT_PRIMARY);
            titleStatusPanel.add(titleLabel, BorderLayout.CENTER);
        }
        JLabel statusLabel = UIUtils.createLabel(event.getStatus().getDisplayName(), UIConstants.BODY_FONT_BOLD, getStatusColor(event.getStatus().name()));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(new Color(getStatusColor(event.getStatus().name()).getRGB() & 0xFFFFFF | 0x33000000, true));
        titleStatusPanel.add(statusLabel, BorderLayout.EAST);
        headerPanel.add(titleStatusPanel, BorderLayout.CENTER);

        JPanel metaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        metaPanel.setOpaque(false);
        if (isEditing) {
            categoryField = new JTextField(event.getCategory());
            categoryField.setFont(UIConstants.SMALL_FONT);
            metaPanel.add(UIUtils.createLabel("Category:", UIConstants.SMALL_FONT, AppColors.TEXT_SECONDARY));
            metaPanel.add(categoryField);
        } else {
            JLabel categoryLabel = UIUtils.createLabel("Category: " + event.getCategory(), UIConstants.SMALL_FONT, AppColors.TEXT_SECONDARY);
            metaPanel.add(categoryLabel);
        }
        JLabel createdLabel = UIUtils.createLabel("Created: " + event.getCreatedAt().format(DateTimeFormatter.ofPattern("MMM d, yyyy")), UIConstants.SMALL_FONT, AppColors.TEXT_SECONDARY);
        metaPanel.add(new JSeparator(JSeparator.VERTICAL) {{ setPreferredSize(new Dimension(1, 12)); setForeground(AppColors.BORDER); }});
        metaPanel.add(createdLabel);
        headerPanel.add(metaPanel, BorderLayout.SOUTH);
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Event Details Section
        RoundedPanel detailsPanel = createSectionPanel("Event Details");
        JPanel detailsGrid = new JPanel(new GridLayout(0, 2, 20, 10));
        detailsGrid.setOpaque(false);
        detailsGrid.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        if (isEditing) {
            // Date
            eventDateSpinner = new JSpinner(new SpinnerDateModel(Date.from(event.getEventDate().atZone(ZoneId.systemDefault()).toInstant()), null, null, java.util.Calendar.MINUTE));
            eventDateSpinner.setEditor(new JSpinner.DateEditor(eventDateSpinner, "yyyy-MM-dd HH:mm"));
            // Registration Deadline
            regDeadlineSpinner = new JSpinner(new SpinnerDateModel(Date.from(event.getRegistrationDeadline().atZone(ZoneId.systemDefault()).toInstant()), null, null, java.util.Calendar.MINUTE));
            regDeadlineSpinner.setEditor(new JSpinner.DateEditor(regDeadlineSpinner, "yyyy-MM-dd HH:mm"));
            // Venue
            venueField = new JTextField(event.getVenueName());
            // Slots
            slotsSpinner = new JSpinner(new SpinnerNumberModel(event.getTotalSlots(), 1, 10000, 1));
            addDetailField(detailsGrid, "Date:", eventDateSpinner);
            addDetailField(detailsGrid, "Registration Deadline:", regDeadlineSpinner);
            addDetailField(detailsGrid, "Venue:", venueField);
            addDetailField(detailsGrid, "Total Slots:", slotsSpinner);
        } else {
            addDetailField(detailsGrid, "Date:", event.getEventDate().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
            addDetailField(detailsGrid, "Time:", event.getEventDate().format(DateTimeFormatter.ofPattern("h:mm a")) + " - " + event.getEventDate().plusHours(2).format(DateTimeFormatter.ofPattern("h:mm a")));
            addDetailField(detailsGrid, "Venue:", event.getVenueName());
            addDetailField(detailsGrid, "Total Slots:", String.valueOf(event.getTotalSlots()));
            addDetailField(detailsGrid, "Available Slots:", String.valueOf(event.getAvailableSlots()));
            addDetailField(detailsGrid, "Registration Deadline:", event.getRegistrationDeadline().format(DateTimeFormatter.ofPattern("MMM d, yyyy h:mm a")));
        }
        detailsPanel.add(detailsGrid, BorderLayout.CENTER);
        mainPanel.add(detailsPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Description Section
        RoundedPanel descriptionPanel = createSectionPanel("Description");
        if (isEditing) {
            descriptionArea = new JTextArea(event.getDescription());
            descriptionArea.setWrapStyleWord(true);
            descriptionArea.setLineWrap(true);
            descriptionArea.setFont(UIConstants.BODY_FONT);
            descriptionArea.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
            descriptionPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);
        } else {
            JTextArea descArea = new JTextArea(event.getDescription());
            descArea.setWrapStyleWord(true);
            descArea.setLineWrap(true);
            descArea.setEditable(false);
            descArea.setBackground(Color.WHITE);
            descArea.setFont(UIConstants.BODY_FONT);
            descArea.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
            descriptionPanel.add(descArea, BorderLayout.CENTER);
        }
        mainPanel.add(descriptionPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Eligibility Criteria Section (if available)
        if (isEditing || (event.getEligibilityCriteria() != null && !event.getEligibilityCriteria().trim().isEmpty())) {
            RoundedPanel eligibilityPanel = createSectionPanel("Eligibility Criteria");
            if (isEditing) {
                eligibilityArea = new JTextArea(event.getEligibilityCriteria());
                eligibilityArea.setWrapStyleWord(true);
                eligibilityArea.setLineWrap(true);
                eligibilityArea.setFont(UIConstants.BODY_FONT);
                eligibilityArea.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
                eligibilityPanel.add(new JScrollPane(eligibilityArea), BorderLayout.CENTER);
            } else {
                JTextArea eligArea = new JTextArea(event.getEligibilityCriteria());
                eligArea.setWrapStyleWord(true);
                eligArea.setLineWrap(true);
                eligArea.setEditable(false);
                eligArea.setBackground(Color.WHITE);
                eligArea.setFont(UIConstants.BODY_FONT);
                eligArea.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
                eligibilityPanel.add(eligArea, BorderLayout.CENTER);
            }
            mainPanel.add(eligibilityPanel);
            mainPanel.add(Box.createVerticalStrut(15));
        }

        // Organizer Information Section
        RoundedPanel organizerPanel = createSectionPanel("Organizer Information");
        JPanel organizerGrid = new JPanel(new GridLayout(0, 2, 20, 10));
        organizerGrid.setOpaque(false);
        organizerGrid.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        // Only show organizer information in view mode, not in edit mode
        if (!isEditing) {
            if (event.getOrganizer() != null) {
                addDetailField(organizerGrid, "Name:", event.getOrganizer().getName());
                addDetailField(organizerGrid, "Email:", event.getOrganizer().getEmail());
                addDetailField(organizerGrid, "Phone:", event.getOrganizer().getPhone());
            }
            addDetailField(organizerGrid, "Contact Info:", event.getContactInfo());
            organizerPanel.add(organizerGrid, BorderLayout.CENTER);
            mainPanel.add(organizerPanel);
            mainPanel.add(Box.createVerticalStrut(15));
        }

        // Media Section (if available)
        if (event.getMainImage() != null || event.getAdditionalDocuments() != null) {
            RoundedPanel mediaPanel = createSectionPanel("Media & Documents");
            JPanel mediaContent = new JPanel();
            mediaContent.setLayout(new BoxLayout(mediaContent, BoxLayout.Y_AXIS));
            mediaContent.setOpaque(false);
            mediaContent.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
            if (event.getMainImage() != null) {
                try {
                    ImageIcon originalIcon = new ImageIcon(event.getMainImage());
                    Image image = originalIcon.getImage().getScaledInstance(400, 300, Image.SCALE_SMOOTH);
                    JLabel imageLabel = new JLabel(new ImageIcon(image));
                    imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                    imageLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
                    mediaContent.add(imageLabel);
                } catch (Exception e) {
                    System.err.println("Error loading event image: " + e.getMessage());
                }
            }
            if (event.getAdditionalDocuments() != null) {
                JPanel docPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                docPanel.setOpaque(false);
                JButton viewDocsButton = UIUtils.createButton(
                    "View Documents",
                    null,
                    UIUtils.ButtonType.SECONDARY,
                    UIUtils.ButtonSize.NORMAL
                );
                viewDocsButton.addActionListener(e -> {
                    try {
                        File tempFile = File.createTempFile("event_doc_", ".pdf");
                        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                            fos.write(event.getAdditionalDocuments());
                        }
                        Desktop.getDesktop().open(tempFile);
                        tempFile.deleteOnExit();
                    } catch (IOException ex) {
                        UIUtils.showError(this, "Error opening document: " + ex.getMessage());
                    }
                });
                docPanel.add(viewDocsButton);
                mediaContent.add(docPanel);
            }
            mediaPanel.add(mediaContent, BorderLayout.CENTER);
            mainPanel.add(mediaPanel);
            mainPanel.add(Box.createVerticalStrut(15));
        }

        // Registration Status Section
        RoundedPanel registrationPanel = createSectionPanel("Registration Status");
        JPanel registrationGrid = new JPanel(new GridLayout(0, 2, 20, 10));
        registrationGrid.setOpaque(false);
        registrationGrid.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        int registeredUsers = event.getRegisteredUsers();
        double registrationRate = event.getTotalSlots() > 0 ? (registeredUsers * 100.0) / event.getTotalSlots() : 0;
        addDetailField(registrationGrid, "Registered Users:", String.format("%d of %d (%d%%)", registeredUsers, event.getTotalSlots(), (int)registrationRate));
        addDetailField(registrationGrid, "Registration Status:", event.isAvailable() ? "Open" : "Closed");
        addDetailField(registrationGrid, "Event Status:", event.isUpcoming() ? "Upcoming" : event.isOngoing() ? "Ongoing" : event.isPast() ? "Past" : "Unknown");
        registrationPanel.add(registrationGrid, BorderLayout.CENTER);
        mainPanel.add(registrationPanel);

        // Add the mainPanel to a scroll pane
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Action buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        if (isEditing) {
            JButton saveButton = UIUtils.createButton("Save", null, UIUtils.ButtonType.PRIMARY, UIUtils.ButtonSize.NORMAL);
            JButton cancelButton = UIUtils.createButton("Cancel", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.NORMAL);
            saveButton.addActionListener(e -> saveChanges());
            cancelButton.addActionListener(e -> { isEditing = false; initializeUI(); });
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
        } else {
            if (onEdit != null) {
                JButton editButton = UIUtils.createButton("Edit", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.NORMAL);
                editButton.addActionListener(e -> { isEditing = true; initializeUI(); });
                buttonPanel.add(editButton);
            }
            if (event.getStatus() == Event.EventStatus.PENDING && onApprove != null && onReject != null) {
                JButton approveButton = UIUtils.createButton("Approve", null, UIUtils.ButtonType.PRIMARY, UIUtils.ButtonSize.NORMAL);
                approveButton.addActionListener(onApprove);
                buttonPanel.add(approveButton);
                JButton rejectButton = UIUtils.createButton("Reject", null, UIUtils.ButtonType.ERROR, UIUtils.ButtonSize.NORMAL);
                rejectButton.addActionListener(onReject);
                buttonPanel.add(rejectButton);
            }
            if (onClose != null) {
                JButton closeButton = UIUtils.createButton("Close", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.NORMAL);
                closeButton.addActionListener(onClose);
                buttonPanel.add(closeButton);
            }
        }
        add(buttonPanel, BorderLayout.SOUTH);
        revalidate();
        repaint();
    }

    private void saveChanges() {
        try {
            String newTitle = titleField.getText().trim();
            String newDescription = descriptionArea.getText().trim();
            String newCategory = categoryField.getText().trim();
            String newVenue = venueField.getText().trim();
            int newSlots = (int) slotsSpinner.getValue();
            String newEligibility = eligibilityArea != null ? eligibilityArea.getText().trim() : "";
            Date newEventDate = (Date) eventDateSpinner.getValue();
            Date newRegDeadline = (Date) regDeadlineSpinner.getValue();
            LocalDateTime eventDateLdt = newEventDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
            LocalDateTime regDeadlineLdt = newRegDeadline.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

            // Validation (basic)
            if (newTitle.isEmpty() || newDescription.isEmpty() || newCategory.isEmpty() || newVenue.isEmpty()) {
                UIUtils.showError(this, "All fields are required.");
                return;
            }
            if (regDeadlineLdt.isAfter(eventDateLdt)) {
                UIUtils.showError(this, "Registration deadline must be before event date.");
                return;
            }

            // Update event object
            event.setTitle(newTitle);
            event.setDescription(newDescription);
            event.setCategory(newCategory);
            event.setVenueName(newVenue);
            event.setTotalSlots(newSlots);
            event.setEligibilityCriteria(newEligibility);
            event.setEventDate(eventDateLdt);
            event.setRegistrationDeadline(regDeadlineLdt);
            // Keep the original organizer - don't change it
            // Save to DB
            EventController.getInstance().updateEvent(event);

            // Show a clear success message that changes have been made
            JOptionPane.showMessageDialog(
                this,
                "Event details have been successfully updated!",
                "Changes Saved",
                JOptionPane.INFORMATION_MESSAGE
            );

            UIUtils.showSuccess(this, "Event details updated successfully.");
            isEditing = false;
            initializeUI();
        } catch (Exception ex) {
            UIUtils.showError(this, "Error updating event: " + ex.getMessage());
        }
    }

    private RoundedPanel createSectionPanel(String title) {
        RoundedPanel panel = new RoundedPanel(new BorderLayout(), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM, true);
        panel.setBorder(UIUtils.createRoundedBorderWithPadding(AppColors.BORDER, UIConstants.CORNER_RADIUS_MEDIUM, 1, 15));
        JLabel titleLabel = UIUtils.createLabel(title, UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        panel.add(titleLabel, BorderLayout.NORTH);
        return panel;
    }

    private void addDetailField(JPanel panel, String label, String value) {
        JLabel labelComponent = UIUtils.createLabel(label, UIConstants.BODY_FONT_BOLD, AppColors.TEXT_SECONDARY);
        JLabel valueComponent = UIUtils.createLabel(value, UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY);
        panel.add(labelComponent);
        panel.add(valueComponent);
    }

    // Overload for editable fields
    private void addDetailField(JPanel panel, String label, JComponent field) {
        JLabel labelComponent = UIUtils.createLabel(label, UIConstants.BODY_FONT_BOLD, AppColors.TEXT_SECONDARY);
        panel.add(labelComponent);
        panel.add(field);
    }

    private Color getStatusColor(String status) {
        switch (status) {
            case "APPROVED":
                return AppColors.SUCCESS;
            case "PENDING":
                return AppColors.WARNING;
            case "REJECTED":
                return AppColors.ERROR;
            case "CANCELLED":
                return AppColors.TEXT_SECONDARY;
            default:
                return AppColors.TEXT_PRIMARY;
        }
    }
}
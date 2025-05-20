package components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Consumer;
import utils.AppColors;
import utils.UIUtils;
import utils.UIConstants;

/**
 * A dialog for event registration
 */
public class EventRegistrationDialog extends JDialog {
    private final String eventId;
    private final String eventName;
    private final Date eventDate;
    private final String venue;
    private final int availableSlots;
    private final String eligibilityCriteria;
    private final Consumer<Boolean> onRegistrationComplete;
    
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea specialRequirementsArea;
    private JCheckBox eligibilityCheckbox;
    private JLabel errorLabel;
    private boolean confirmed = false;
    
    /**
     * Creates a new event registration dialog
     * 
     * @param parent The parent frame
     * @param eventId The event ID
     * @param eventName The event name
     * @param eventDate The event date
     * @param venue The venue
     * @param availableSlots The available slots
     * @param eligibilityCriteria The eligibility criteria
     * @param onRegistrationComplete Callback for when registration is complete
     */
    public EventRegistrationDialog(
            JFrame parent,
            String eventId,
            String eventName,
            Date eventDate,
            String venue,
            int availableSlots,
            String eligibilityCriteria,
            Consumer<Boolean> onRegistrationComplete) {
        
        super(parent, "Register for " + eventName, ModalityType.APPLICATION_MODAL);
        
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.venue = venue;
        this.availableSlots = availableSlots;
        this.eligibilityCriteria = eligibilityCriteria;
        this.onRegistrationComplete = onRegistrationComplete;
        
        setupUI();
        
        // Set dialog properties
        setSize(800, 700);
        setLocationRelativeTo(parent);
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setMinimumSize(new Dimension(400, 500));
        setLocationRelativeTo(getOwner());

        // Create content panel
        JPanel contentPanel = UIUtils.createPanel(new BoxLayout(null, BoxLayout.Y_AXIS), true);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add form fields
        contentPanel.add(createFormPanel());
        contentPanel.add(Box.createVerticalStrut(20));

        // Add error label
        errorLabel = UIUtils.createLabel(
            " ",
            UIConstants.SMALL_FONT,
            AppColors.ERROR
        );
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(errorLabel);
        contentPanel.add(Box.createVerticalStrut(20));

        // Add buttons
        contentPanel.add(createButtonPanel());

        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createFormPanel() {
        JPanel formPanel = UIUtils.createPanel(new BoxLayout(null, BoxLayout.Y_AXIS), false);
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Event details
        JPanel detailsPanel = UIUtils.createPanel(new BoxLayout(null, BoxLayout.Y_AXIS), false);
        detailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Event name
        JLabel eventLabel = UIUtils.createLabel(
            "Event: " + eventName,
            UIConstants.BODY_FONT,
            AppColors.TEXT_PRIMARY
        );
        eventLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(eventLabel);
        detailsPanel.add(Box.createVerticalStrut(10));

        // Event date
        JLabel dateLabel = UIUtils.createLabel(
            "Date: " + eventDate.toString(),
            UIConstants.BODY_FONT,
            AppColors.TEXT_PRIMARY
        );
        dateLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(dateLabel);
        detailsPanel.add(Box.createVerticalStrut(10));

        // Event venue
        JLabel venueLabel = UIUtils.createLabel(
            "Venue: " + venue,
            UIConstants.BODY_FONT,
            AppColors.TEXT_PRIMARY
        );
        venueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(venueLabel);
        detailsPanel.add(Box.createVerticalStrut(10));

        // Available slots
        JLabel slotsLabel = UIUtils.createLabel(
            "Available Slots: " + availableSlots,
            UIConstants.BODY_FONT,
            AppColors.TEXT_PRIMARY
        );
        slotsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(slotsLabel);
        detailsPanel.add(Box.createVerticalStrut(10));

        // Eligibility criteria
        JLabel eligibilityLabel = UIUtils.createLabel(
            "Eligibility Criteria:",
            UIConstants.BODY_FONT,
            AppColors.TEXT_PRIMARY
        );
        eligibilityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.add(eligibilityLabel);
        detailsPanel.add(Box.createVerticalStrut(5));

        JTextArea eligibilityText = new JTextArea(eligibilityCriteria);
        eligibilityText.setFont(UIConstants.BODY_FONT);
        eligibilityText.setLineWrap(true);
        eligibilityText.setWrapStyleWord(true);
        eligibilityText.setEditable(false);
        eligibilityText.setBackground(null);
        eligibilityText.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        detailsPanel.add(eligibilityText);

        formPanel.add(detailsPanel);
        formPanel.add(Box.createVerticalStrut(20));

        return formPanel;
    }
    
    private JPanel createButtonPanel() {
        JPanel buttonPanel = UIUtils.createPanel(new FlowLayout(FlowLayout.RIGHT), false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton cancelButton = UIUtils.createButton(
            "Cancel",
            null,
            UIUtils.ButtonType.SECONDARY,
            UIUtils.ButtonSize.NORMAL
        );
        cancelButton.addActionListener(e -> {
            if (onRegistrationComplete != null) {
                onRegistrationComplete.accept(false);
            }
            dispose();
        });

        JButton registerButton = UIUtils.createButton(
            "Register",
            null,
            UIUtils.ButtonType.PRIMARY,
            UIUtils.ButtonSize.NORMAL
        );
        registerButton.addActionListener(e -> {
            if (onRegistrationComplete != null) {
                onRegistrationComplete.accept(true);
            }
            dispose();
        });

        buttonPanel.add(cancelButton);
        buttonPanel.add(registerButton);

        return buttonPanel;
    }
    
    private void handleRegistration() {
        // Reset error message
        errorLabel.setText(" ");
        
        // Validate name
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            errorLabel.setText("Please enter your full name.");
            return;
        }
        
        // Validate email
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            errorLabel.setText("Please enter your email address.");
            return;
        }
        if (!isValidEmail(email)) {
            errorLabel.setText("Please enter a valid email address.");
            return;
        }
        
        // Validate phone number
        String phoneNumber = phoneField.getText().trim();
        if (phoneNumber.isEmpty()) {
            errorLabel.setText("Please enter your phone number.");
            return;
        }
        if (!isValidPhoneNumber(phoneNumber)) {
            errorLabel.setText("Please enter a valid phone number (10 digits).");
            return;
        }
        
        // Validate eligibility
        if (!eligibilityCheckbox.isSelected()) {
            errorLabel.setText("You must confirm that you meet the eligibility criteria.");
            return;
        }
        
        // Check available slots
        if (availableSlots <= 0) {
            errorLabel.setText("Sorry, this event is full.");
            return;
        }
        
        // Disable form while processing
        setFormEnabled(false);
        
        try {
            // Call the callback
            if (onRegistrationComplete != null) {
                onRegistrationComplete.accept(true);
            }
            
            // Show success message
            JOptionPane.showMessageDialog(this,
                "You have successfully registered for " + eventName + "!",
                "Registration Successful",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Reset form and close dialog
            resetForm();
            confirmed = true;
            dispose();
        } catch (Exception e) {
            // Show error message
            JOptionPane.showMessageDialog(this,
                "Error during registration: " + e.getMessage(),
                "Registration Failed",
                JOptionPane.ERROR_MESSAGE);
            
            // Re-enable form
            setFormEnabled(true);
        }
    }
    
    private void setFormEnabled(boolean enabled) {
        nameField.setEnabled(enabled);
        emailField.setEnabled(enabled);
        phoneField.setEnabled(enabled);
        specialRequirementsArea.setEnabled(enabled);
        eligibilityCheckbox.setEnabled(enabled);
    }
    
    private void resetForm() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        specialRequirementsArea.setText("");
        eligibilityCheckbox.setSelected(false);
        errorLabel.setText(" ");
    }
    
    private boolean isValidEmail(String email) {
        // Basic email validation
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    private boolean isValidPhoneNumber(String phoneNumber) {
        // Remove any non-digit characters
        String digitsOnly = phoneNumber.replaceAll("\\D", "");
        // Check if it has exactly 10 digits
        return digitsOnly.length() == 10;
    }

    /**
     * Shows the dialog and returns whether the registration was confirmed
     *
     * @return true if the registration was confirmed, false otherwise
     */
    public boolean showDialog() {
        setVisible(true);
        return confirmed;
    }

    /**
     * Gets the registration data
     *
     * @return An array containing [name, email, phone, notes]
     */
    public String[] getRegistrationData() {
        return new String[] {
            nameField.getText().trim(),
            emailField.getText().trim(),
            phoneField.getText().trim(),
            specialRequirementsArea.getText().trim()
        };
    }
}

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
    private JTextField studentIdField;
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

        // Name field
        JLabel nameLabel = UIUtils.createLabel(
            "Full Name:",
            UIConstants.BODY_FONT,
            AppColors.TEXT_PRIMARY
        );
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(nameLabel);
        formPanel.add(Box.createVerticalStrut(5));

        nameField = UIUtils.createRoundedTextField();
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        formPanel.add(nameField);
        formPanel.add(Box.createVerticalStrut(15));

        // Email field
        JLabel emailLabel = UIUtils.createLabel(
            "Email:",
            UIConstants.BODY_FONT,
            AppColors.TEXT_PRIMARY
        );
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(emailLabel);
        formPanel.add(Box.createVerticalStrut(5));

        emailField = UIUtils.createRoundedTextField();
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        formPanel.add(emailField);
        formPanel.add(Box.createVerticalStrut(15));

        // Phone field
        JLabel phoneLabel = UIUtils.createLabel(
            "Phone:",
            UIConstants.BODY_FONT,
            AppColors.TEXT_PRIMARY
        );
        phoneLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(phoneLabel);
        formPanel.add(Box.createVerticalStrut(5));

        phoneField = UIUtils.createRoundedTextField();
        phoneField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        formPanel.add(phoneField);
        formPanel.add(Box.createVerticalStrut(15));

        // Notes field
        JLabel notesLabel = UIUtils.createLabel(
            "Additional Notes:",
            UIConstants.BODY_FONT,
            AppColors.TEXT_PRIMARY
        );
        notesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(notesLabel);
        formPanel.add(Box.createVerticalStrut(5));

        specialRequirementsArea = new JTextArea(3, 20);
        specialRequirementsArea.setFont(UIConstants.BODY_FONT);
        specialRequirementsArea.setLineWrap(true);
        specialRequirementsArea.setWrapStyleWord(true);
        specialRequirementsArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        JScrollPane notesScroll = new JScrollPane(specialRequirementsArea);
        notesScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        formPanel.add(notesScroll);

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
            confirmed = false;
            dispose();
        });

        JButton registerButton = UIUtils.createButton(
            "Register",
            null,
            UIUtils.ButtonType.PRIMARY,
            UIUtils.ButtonSize.NORMAL
        );
        registerButton.addActionListener(e -> handleRegistration());

        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
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
        
        // Validate student ID
        String studentId = studentIdField.getText().trim();
        if (studentId.isEmpty()) {
            errorLabel.setText("Please enter your student ID.");
            return;
        }
        if (!isValidStudentId(studentId)) {
            errorLabel.setText("Please enter a valid student ID (alphanumeric, at least 5 characters).");
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
        studentIdField.setEnabled(enabled);
        specialRequirementsArea.setEnabled(enabled);
        eligibilityCheckbox.setEnabled(enabled);
    }
    
    private void resetForm() {
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        studentIdField.setText("");
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
    
    private boolean isValidStudentId(String studentId) {
        // Check if it's alphanumeric and at least 5 characters
        return studentId.matches("^[a-zA-Z0-9]{5,}$");
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

package components;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import utils.AppColors;
import utils.UIUtils;
import utils.ValidationUtils;
import models.User;
import utils.UIConstants;
import utils.SecurityUtils;
import controllers.AuthController;
import java.awt.event.ActionListener;

/**
 * A reusable dialog for editing user information
 */
public class UserEditDialog extends JDialog {
    private final User user;
    private final boolean isCurrentUser;
    private final ActionListener onSaveCallback;
    
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JLabel errorLabel;
    private JLabel successLabel;
    private JTabbedPane tabbedPane;
    
    /**
     * Creates a new user edit dialog
     * 
     * @param parent The parent frame
     * @param user The user to edit
     * @param isCurrentUser Whether this is the current logged-in user
     * @param onSaveCallback Callback to be called when changes are saved successfully
     */
    public UserEditDialog(JFrame parent, User user, boolean isCurrentUser, ActionListener onSaveCallback) {
        super(parent, "Edit User", true);
        this.user = user;
        this.isCurrentUser = isCurrentUser;
        this.onSaveCallback = onSaveCallback;
        
        setupUI();
        pack();
        setLocationRelativeTo(parent);
    }

    private void setupUI() {
        setMinimumSize(new Dimension(500, 600));
        setLayout(new BorderLayout(20, 20));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        
        JLabel titleLabel = UIUtils.createLabel(
            isCurrentUser ? "Edit Profile" : "Edit User",
            UIConstants.HEADER_FONT,
            AppColors.TEXT_PRIMARY
        );
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        add(headerPanel, BorderLayout.NORTH);

        // Main content with tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.BODY_FONT);
        tabbedPane.addTab("Basic Information", createBasicInfoPanel());
        tabbedPane.addTab("Change Password", createPasswordPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createBasicInfoPanel() {
        RoundedPanel panel = new RoundedPanel(new BorderLayout(20, 20), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        // Name field
        nameField = UIUtils.createRoundedTextField();
        nameField.setText(user.getName());
        addFormField(formPanel, gbc, "Full Name", nameField);

        // Email field
        emailField = UIUtils.createRoundedTextField();
        emailField.setText(user.getEmail());
        addFormField(formPanel, gbc, "Email Address", emailField);

        // Phone field
        phoneField = UIUtils.createRoundedTextField();
        phoneField.setText(user.getPhone());
        addFormField(formPanel, gbc, "Phone Number", phoneField);

        // Status indicators
        errorLabel = UIUtils.createLabel("", UIConstants.SMALL_FONT, AppColors.ERROR);
        successLabel = UIUtils.createLabel("", UIConstants.SMALL_FONT, AppColors.SUCCESS);
        
        gbc.insets = new Insets(10, 0, 0, 0);
        formPanel.add(errorLabel, gbc);
        formPanel.add(successLabel, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        JButton cancelButton = UIUtils.createButton("Cancel", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.NORMAL);
        cancelButton.addActionListener(e -> dispose());
        
        JButton saveButton = UIUtils.createButton("Save Changes", null, UIUtils.ButtonType.PRIMARY, UIUtils.ButtonSize.NORMAL);
        saveButton.addActionListener(e -> saveBasicInfo());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);
        
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private JPanel createPasswordPanel() {
        RoundedPanel panel = new RoundedPanel(new BorderLayout(20, 20), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 5, 0);

        // Password fields
        if (isCurrentUser) {
            currentPasswordField = UIUtils.createRoundedPasswordField();
            addFormField(formPanel, gbc, "Current Password", currentPasswordField);
        }

        newPasswordField = UIUtils.createRoundedPasswordField();
        addFormField(formPanel, gbc, "New Password", newPasswordField);

        confirmPasswordField = UIUtils.createRoundedPasswordField();
        addFormField(formPanel, gbc, "Confirm New Password", confirmPasswordField);

        // Password requirements
        JTextArea requirementsArea = new JTextArea(
            "Password Requirements:\n" +
            "• Minimum 8 characters\n" +
            "• At least one uppercase letter\n" +
            "• At least one lowercase letter\n" +
            "• At least one number\n" +
            "• At least one special character"
        );
        requirementsArea.setEditable(false);
        requirementsArea.setBackground(AppColors.BACKGROUND_LIGHT);
        requirementsArea.setFont(UIConstants.SMALL_FONT);
        requirementsArea.setForeground(AppColors.TEXT_SECONDARY);
        requirementsArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        gbc.insets = new Insets(20, 0, 20, 0);
        formPanel.add(requirementsArea, gbc);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        
        JButton cancelButton = UIUtils.createButton("Cancel", null, UIUtils.ButtonType.SECONDARY, UIUtils.ButtonSize.NORMAL);
        cancelButton.addActionListener(e -> dispose());
        
        JButton changePasswordButton = UIUtils.createButton("Change Password", null, UIUtils.ButtonType.PRIMARY, UIUtils.ButtonSize.NORMAL);
        changePasswordButton.addActionListener(e -> changePassword());
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(changePasswordButton);
        
        panel.add(formPanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field) {
        JLabel label = UIUtils.createLabel(labelText + ":", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY);
        gbc.insets = new Insets(5, 0, 2, 0);
        panel.add(label, gbc);

        gbc.insets = new Insets(0, 0, 15, 0);
        panel.add(field, gbc);
    }

    private void saveBasicInfo() {
        try {
            // Validate inputs
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                showError("All fields are required");
                return;
            }

            if (!ValidationUtils.isValidEmail(email)) {
                showError("Please enter a valid email address");
                return;
            }

            if (!ValidationUtils.isValidPhoneNumber(phone)) {
                showError("Please enter a valid phone number");
                return;
            }

            // Update user object
            user.setName(name);
            user.setEmail(email);
            user.setPhone(phone);

            // Save to database
            AuthController.getInstance().updateUser(user);
            showSuccess("Profile updated successfully");
            
            // Notify callback
            if (onSaveCallback != null) {
                onSaveCallback.actionPerformed(null);
            }
            
            dispose();
        } catch (Exception e) {
            showError("Error updating profile: " + e.getMessage());
        }
    }

    private void changePassword() {
        try {
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (isCurrentUser) {
                String currentPassword = new String(currentPasswordField.getPassword());
                if (currentPassword.isEmpty()) {
                    showError("Current password is required");
                    return;
                }

                // Verify current password
                SecurityUtils.VerificationResult verificationResult = SecurityUtils.verifyPassword(currentPassword, user.getPassword());
                if (!verificationResult.isSuccess()) {
                    showError("Current password is incorrect");
                    return;
                }
            }

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                showError("New password and confirmation are required");
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                showError("New passwords do not match");
                return;
            }

            if (!ValidationUtils.isValidPassword(newPassword)) {
                showError("New password does not meet requirements");
                return;
            }

            // Update password
            user.setPassword(SecurityUtils.hashPassword(newPassword));
            AuthController.getInstance().updateUser(user);
            
            showSuccess("Password changed successfully");
            
            // Notify callback
            if (onSaveCallback != null) {
                onSaveCallback.actionPerformed(null);
            }
            
            dispose();
        } catch (Exception e) {
            showError("Error changing password: " + e.getMessage());
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        successLabel.setText("");
    }

    private void showSuccess(String message) {
        successLabel.setText(message);
        errorLabel.setText("");
    }
} 
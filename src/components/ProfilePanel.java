package components;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;
import utils.AppColors;
import utils.UIUtils;
import utils.ValidationUtils;
import models.User;
import utils.UIConstants;
import utils.SecurityUtils;
import utils.IconUtils;
import controllers.AuthController;
import controllers.SessionManager;

/**
 * A panel for displaying user profile information with inline editing capabilities
 */
public class ProfilePanel extends JPanel {
    private final User currentUser;
    private final boolean editable;
    
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JLabel errorLabel;
    private JLabel successLabel;
    private boolean isEditing = false;
    private JPanel formPanel;
    private JTabbedPane tabbedPane;
    private JButton editButton;
    
    /**
     * Creates a new profile panel
     * 
     * @param user The user object
     * @param editable Whether the panel is editable
     */
    public ProfilePanel(User user, boolean editable) {
        this.currentUser = user;
        this.editable = editable;
        
        setLayout(new BorderLayout(20, 20));
        setBackground(AppColors.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        setupUI();
    }

    private void setupUI() {
        // Header Panel with gradient background
        RoundedPanel headerPanel = new RoundedPanel(new BorderLayout(15, 15), AppColors.PRIMARY_LIGHT, UIConstants.CORNER_RADIUS_LARGE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));
        
        // User info in header
        JPanel userInfoPanel = new JPanel(new BorderLayout(10, 5));
        userInfoPanel.setOpaque(false);
        
        // User name and role
        JLabel nameLabel = UIUtils.createLabel(currentUser.getName(), UIConstants.HEADER_FONT, AppColors.TEXT_PRIMARY);
        JLabel roleLabel = UIUtils.createLabel(currentUser.getRole().getDisplayName(), UIConstants.BODY_FONT, AppColors.TEXT_SECONDARY);
        
        userInfoPanel.add(nameLabel, BorderLayout.NORTH);
        userInfoPanel.add(roleLabel, BorderLayout.CENTER);
        
        headerPanel.add(userInfoPanel, BorderLayout.WEST);
        
        if (editable) {
            editButton = UIUtils.createButton(isEditing ? "Save Changes" : "Edit Profile", 
                "edit", UIUtils.ButtonType.PRIMARY, UIUtils.ButtonSize.LARGE);
            editButton.setFont(UIConstants.BODY_FONT_BOLD);
            editButton.addActionListener(e -> toggleEditMode(editButton));
            headerPanel.add(editButton, BorderLayout.EAST);
        }
        
        add(headerPanel, BorderLayout.NORTH);

        // Main content with tabs
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UIConstants.BODY_FONT_BOLD);
        tabbedPane.setBackground(Color.WHITE);
        tabbedPane.setBorder(null);
        
        // Custom tab styling
        UIManager.put("TabbedPane.selected", AppColors.PRIMARY_LIGHT);
        UIManager.put("TabbedPane.contentAreaColor", Color.WHITE);
        UIManager.put("TabbedPane.shadow", Color.WHITE);
        
        tabbedPane.addTab("Basic Information", createBasicInfoPanel());
        tabbedPane.addTab("Security", createSecurityPanel());
        
        // Add some padding around the tabbed pane
        JPanel tabbedPaneWrapper = new JPanel(new BorderLayout());
        tabbedPaneWrapper.setBackground(AppColors.BACKGROUND);
        tabbedPaneWrapper.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        tabbedPaneWrapper.add(tabbedPane, BorderLayout.CENTER);
        
        add(tabbedPaneWrapper, BorderLayout.CENTER);

        // Status Panel
        JPanel statusPanel = new JPanel(new BorderLayout(10, 0));
        statusPanel.setBackground(AppColors.BACKGROUND);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        
        errorLabel = UIUtils.createLabel("", UIConstants.SMALL_FONT_BOLD, AppColors.ERROR);
        successLabel = UIUtils.createLabel("", UIConstants.SMALL_FONT_BOLD, AppColors.SUCCESS);
        
        JPanel messagePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        messagePanel.setOpaque(false);
        messagePanel.add(errorLabel);
        messagePanel.add(successLabel);
        
        statusPanel.add(messagePanel, BorderLayout.WEST);
        
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JPanel createBasicInfoPanel() {
        RoundedPanel panel = new RoundedPanel(new BorderLayout(20, 20), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 15, 0);

        // Create fields
        nameField = UIUtils.createRoundedTextField();
        emailField = UIUtils.createRoundedTextField();
        phoneField = UIUtils.createRoundedTextField();

        // Set initial values
        nameField.setText(currentUser.getName());
        emailField.setText(currentUser.getEmail());
        phoneField.setText(currentUser.getPhone());

        // Add fields to panel
        if (isEditing) {
            addEditableFieldWithIcon(formPanel, gbc, "Full Name", nameField, "user");
            addEditableFieldWithIcon(formPanel, gbc, "Email Address", emailField, "email");
            addEditableFieldWithIcon(formPanel, gbc, "Phone Number", phoneField, "phone");
        } else {
            addDetailFieldWithIcon(formPanel, gbc, "Full Name", currentUser.getName(), "user");
            addDetailFieldWithIcon(formPanel, gbc, "Email Address", currentUser.getEmail(), "email");
            addDetailFieldWithIcon(formPanel, gbc, "Phone Number", currentUser.getPhone(), "phone");
        }

        // Add non-editable fields with icons
        addDetailFieldWithIcon(formPanel, gbc, "Role", currentUser.getRole().getDisplayName(), "role");
        addDetailFieldWithIcon(formPanel, gbc, "Registration Date", currentUser.getRegistrationDate(), "calendar");
        addDetailFieldWithIcon(formPanel, gbc, "Last Login", 
            currentUser.getLastLoginAt() != null ? 
            new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(currentUser.getLastLoginAt()) : "Never",
            "clock");

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createSecurityPanel() {
        RoundedPanel panel = new RoundedPanel(new BorderLayout(20, 20), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel securityForm = new JPanel(new GridBagLayout());
        securityForm.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 15, 0);

        if (isEditing) {
            // Password fields
            currentPasswordField = UIUtils.createRoundedPasswordField();
            newPasswordField = UIUtils.createRoundedPasswordField();
            confirmPasswordField = UIUtils.createRoundedPasswordField();

            addEditableFieldWithIcon(securityForm, gbc, "Current Password", currentPasswordField, "lock");
            addEditableFieldWithIcon(securityForm, gbc, "New Password", newPasswordField, "lock");
            addEditableFieldWithIcon(securityForm, gbc, "Confirm New Password", confirmPasswordField, "lock");
        
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
            securityForm.add(requirementsArea, gbc);

            // Change password button
            JButton changePasswordButton = UIUtils.createButton("Change Password", null, UIUtils.ButtonType.PRIMARY, UIUtils.ButtonSize.NORMAL);
            changePasswordButton.addActionListener(e -> changePassword());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
            buttonPanel.add(changePasswordButton);
            
        panel.add(buttonPanel, BorderLayout.SOUTH);
        } else {
            addDetailFieldWithIcon(securityForm, gbc, "Password", "••••••••", "lock");
        }
        
        panel.add(securityForm, BorderLayout.CENTER);
        return panel;
    }
    
    private void addDetailFieldWithIcon(JPanel panel, GridBagConstraints gbc, String labelText, String value, String iconName) {
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 5));
        fieldPanel.setOpaque(false);
        
        // Label with icon
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        labelPanel.setOpaque(false);
        
        ImageIcon icon = IconUtils.loadIcon(iconName, IconUtils.ICON_SIZE_SMALL);
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setForeground(AppColors.TEXT_PRIMARY);
        JLabel textLabel = UIUtils.createLabel(labelText + ":", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY);
        
        labelPanel.add(iconLabel);
        labelPanel.add(textLabel);
        
        // Value
        JLabel valueLabel = UIUtils.createLabel(value != null ? value : "N/A", UIConstants.BODY_FONT, AppColors.TEXT_SECONDARY);
        valueLabel.setBorder(BorderFactory.createEmptyBorder(5, 30, 0, 0));
        
        fieldPanel.add(labelPanel, BorderLayout.NORTH);
        fieldPanel.add(valueLabel, BorderLayout.CENTER);
        
        panel.add(fieldPanel, gbc);
    }

    private void addEditableFieldWithIcon(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field, String iconName) {
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 5));
        fieldPanel.setOpaque(false);
        
        // Label with icon
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        labelPanel.setOpaque(false);
        
        ImageIcon icon = IconUtils.loadIcon(iconName, IconUtils.ICON_SIZE_SMALL);
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setForeground(AppColors.TEXT_PRIMARY);
        JLabel textLabel = UIUtils.createLabel(labelText + ":", UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY);
        
        labelPanel.add(iconLabel);
        labelPanel.add(textLabel);
        
        fieldPanel.add(labelPanel, BorderLayout.NORTH);
        fieldPanel.add(field, BorderLayout.CENTER);
        
        panel.add(fieldPanel, gbc);
    }

    private JTextField createStyledField(String placeholder, String initialValue) {
        JTextField field = UIUtils.createRoundedTextField();
        field.setFont(UIConstants.BODY_FONT);
        field.setText(initialValue);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 40));
        return field;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = UIUtils.createRoundedPasswordField();
        field.setFont(UIConstants.BODY_FONT);
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 40));
        return field;
    }

    private void toggleEditMode(JButton editButton) {
        if (isEditing) {
            // Save changes
            saveChanges();
            if (errorLabel.getText().isEmpty()) {
                editButton.setText("Edit Profile");
            } else {
                return; // Don't exit edit mode if there are errors
            }
        } else {
            isEditing = true;
            editButton.setText("Save Changes");
            refreshUI();
        }
    }

    private void saveChanges() {
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
        
            // Check if password fields are filled
            String currentPassword = currentPasswordField != null ? new String(currentPasswordField.getPassword()) : "";
            String newPassword = newPasswordField != null ? new String(newPasswordField.getPassword()) : "";
            String confirmPassword = confirmPasswordField != null ? new String(confirmPasswordField.getPassword()) : "";

            // If password fields are filled, validate and update password
            if (!currentPassword.isEmpty() || !newPassword.isEmpty() || !confirmPassword.isEmpty()) {
                if (!updatePassword(currentPassword, newPassword, confirmPassword)) {
                    return; // Password update failed
                }
            }

            // Update user object with basic info
            currentUser.setName(name);
            currentUser.setEmail(email);
            currentUser.setPhone(phone);

            // Save to database
            try {
                AuthController.getInstance().updateUser(currentUser);
                showSuccess("Profile updated successfully");
                isEditing = false;
                refreshUI();
            } catch (SQLException e) {
                showError("Failed to update profile in database: " + e.getMessage());
                return;
            }
        } catch (Exception e) {
            showError("Error updating profile: " + e.getMessage());
        }
    }

    private boolean updatePassword(String currentPassword, String newPassword, String confirmPassword) {
        try {
            if (currentPassword.isEmpty()) {
                showError("Current password is required to change password");
                return false;
            }

            // Verify current password
            SecurityUtils.VerificationResult verificationResult = SecurityUtils.verifyPassword(currentPassword, currentUser.getPassword());
            if (!verificationResult.isSuccess()) {
                showError("Current password is incorrect");
                return false;
            }

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                showError("New password and confirmation are required");
                return false;
            }

            if (!newPassword.equals(confirmPassword)) {
                showError("New passwords do not match");
                return false;
            }

            if (!ValidationUtils.isValidPassword(newPassword)) {
                showError("New password does not meet requirements");
                return false;
            }

            // Update password in user object
            currentUser.setPassword(SecurityUtils.hashPassword(newPassword));
            
            // Clear password fields
            currentPasswordField.setText("");
            newPasswordField.setText("");
            confirmPasswordField.setText("");
            
            return true;
        } catch (Exception e) {
            showError("Error changing password: " + e.getMessage());
            return false;
        }
    }

    private void changePassword() {
        try {
            String currentPassword = new String(currentPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());

            if (currentPassword.isEmpty()) {
                showError("Current password is required");
                return;
            }

            // Verify current password
            SecurityUtils.VerificationResult verificationResult = SecurityUtils.verifyPassword(currentPassword, currentUser.getPassword());
            if (!verificationResult.isSuccess()) {
                showError("Current password is incorrect");
                return;
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
            currentUser.setPassword(SecurityUtils.hashPassword(newPassword));
            AuthController.getInstance().updateUser(currentUser);
            
            showSuccess("Password changed successfully");
        
        // Clear password fields
        currentPasswordField.setText("");
        newPasswordField.setText("");
            confirmPasswordField.setText("");
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

    private void refreshUI() {
        removeAll();
        setupUI();
        revalidate();
        repaint();
    }
}

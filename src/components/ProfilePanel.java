package components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.sql.SQLException;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import controllers.AuthController;
import models.User;
import utils.AppColors;
import utils.EmojiUtils;
import utils.SecurityUtils;
import utils.UIConstants;
import utils.UIUtils;
import utils.ValidationUtils;

/**
 * A reusable panel for displaying and editing user profile information
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
    private Consumer<User> onProfileUpdate;

    /**
     * Creates a new profile panel
     *
     * @param user The user object
     * @param editable Whether the panel is editable
     * @param onProfileUpdate Callback when profile is updated
     */
    public ProfilePanel(User user, boolean editable, Consumer<User> onProfileUpdate) {
        this.currentUser = user;
        this.editable = editable;
        this.onProfileUpdate = onProfileUpdate;

        setLayout(new BorderLayout(20, 20));
        setBackground(AppColors.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        setupUI();
    }

    /**
     * Creates a new profile panel without update callback
     */
    public ProfilePanel(User user, boolean editable) {
        this(user, editable, null);
    }

    private void setupUI() {
        // Header Panel with gradient background
        RoundedPanel headerPanel = new RoundedPanel(new BorderLayout(15, 15), AppColors.PRIMARY_LIGHT, UIConstants.CORNER_RADIUS_LARGE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // User info in header
        JPanel userInfoPanel = new JPanel(new BorderLayout(10, 5));
        userInfoPanel.setOpaque(false);

        // --- Avatar with emoji ---
        JPanel avatarPanel = new JPanel(new BorderLayout());
        avatarPanel.setPreferredSize(new Dimension(60, 60));
        avatarPanel.setBackground(AppColors.PRIMARY_DARK);
        avatarPanel.setOpaque(true);

        // Add emoji avatar based on user role
        String roleEmoji = EmojiUtils.getUserRoleEmoji(currentUser.getRole().name());
        JLabel avatarEmoji = new JLabel(roleEmoji);
        avatarEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        avatarEmoji.setHorizontalAlignment(SwingConstants.CENTER);
        avatarEmoji.setVerticalAlignment(SwingConstants.CENTER);
        avatarEmoji.setForeground(Color.WHITE);
        avatarPanel.add(avatarEmoji, BorderLayout.CENTER);
        userInfoPanel.add(avatarPanel, BorderLayout.WEST);

        // User name and role
        JPanel nameRolePanel = new JPanel();
        nameRolePanel.setLayout(new BoxLayout(nameRolePanel, BoxLayout.Y_AXIS));
        nameRolePanel.setOpaque(false);
        JLabel nameLabel = UIUtils.createLabel(currentUser.getName(), UIConstants.HEADER_FONT, AppColors.TEXT_PRIMARY);
        JLabel roleLabel = UIUtils.createLabel(currentUser.getRole().getDisplayName(), UIConstants.BODY_FONT, AppColors.TEXT_SECONDARY);
        nameRolePanel.add(nameLabel);
        nameRolePanel.add(roleLabel);
        userInfoPanel.add(nameRolePanel, BorderLayout.CENTER);

        headerPanel.add(userInfoPanel, BorderLayout.WEST);

        if (editable) {
            editButton = UIUtils.createButton(isEditing ? "💾 Save Changes" : "✏️ Edit Profile",
                null, UIUtils.ButtonType.PRIMARY, UIUtils.ButtonSize.LARGE);
            editButton.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
            editButton.addActionListener(e -> toggleEditMode(editButton));
            headerPanel.add(editButton, BorderLayout.EAST);
        }

        add(headerPanel, BorderLayout.NORTH);

        // Main content with tabs
        tabbedPane = new JTabbedPane() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Highlight active tab
                int idx = getSelectedIndex();
                if (idx >= 0) {
                    Rectangle rect = getBoundsAt(idx);
                    g.setColor(AppColors.PRIMARY_DARK);
                    g.fillRect(rect.x, rect.y, rect.width, rect.height);
                    // Draw the tab text in white
                    String title = getTitleAt(idx);
                    FontMetrics fm = g.getFontMetrics(getFont());
                    int textX = rect.x + (rect.width - fm.stringWidth(title)) / 2;
                    int textY = rect.y + ((rect.height - fm.getHeight()) / 2) + fm.getAscent();
                    g.setColor(Color.WHITE);
                    g.setFont(getFont());
                    g.drawString(title, textX, textY);
                }
            }
        };
        tabbedPane.setFont(UIConstants.BODY_FONT_BOLD);
        tabbedPane.setBackground(new Color(250, 250, 252));
        tabbedPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        tabbedPane.addTab("Basic Information", createBasicInfoPanel());
        if (editable) {
            tabbedPane.addTab("Change Password", createPasswordPanel());
        }

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createBasicInfoPanel() {
        RoundedPanel panel = new RoundedPanel(new BorderLayout(20, 20), new Color(248, 250, 255), UIConstants.CORNER_RADIUS_MEDIUM);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 15, 0);

        // --- Section Heading ---
        JLabel sectionHeading = UIUtils.createLabel("Personal Information", UIConstants.BODY_FONT_BOLD, AppColors.PRIMARY_DARK);
        sectionHeading.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        formPanel.add(sectionHeading, gbc);

        // Create fields
        nameField = UIUtils.createRoundedTextField();
        emailField = UIUtils.createRoundedTextField();
        phoneField = UIUtils.createRoundedTextField();
        nameField.setToolTipText("Enter your full name");
        emailField.setToolTipText("Enter your email address");
        phoneField.setToolTipText("Enter your phone number");

        // Set initial values
        nameField.setText(currentUser.getName());
        emailField.setText(currentUser.getEmail());
        phoneField.setText(currentUser.getPhone());

        // Add fields to panel with emoji icons
        if (isEditing) {
            addEditableFieldWithIcon(formPanel, gbc, "Full Name", nameField, "👤");
            addEditableFieldWithIcon(formPanel, gbc, "Email Address", emailField, "📧");
            addEditableFieldWithIcon(formPanel, gbc, "Phone Number", phoneField, "📱");
        } else {
            addDetailFieldWithIcon(formPanel, gbc, "Full Name", currentUser.getName(), "👤");
            addDetailFieldWithIcon(formPanel, gbc, "Email Address", currentUser.getEmail(), "📧");
            addDetailFieldWithIcon(formPanel, gbc, "Phone Number", currentUser.getPhone(), "📱");
        }

        // Add non-editable fields with emoji icons
        String roleEmoji = EmojiUtils.getUserRoleEmoji(currentUser.getRole().name());
        addDetailFieldWithIcon(formPanel, gbc, "Role", currentUser.getRole().getDisplayName(), roleEmoji);
        addDetailFieldWithIcon(formPanel, gbc, "Registration Date", currentUser.getRegistrationDate(), "📅");

        // Add status labels
        errorLabel = UIUtils.createLabel("", UIConstants.SMALL_FONT, AppColors.ERROR);
        successLabel = UIUtils.createLabel("", UIConstants.SMALL_FONT, AppColors.SUCCESS);
        gbc.insets = new Insets(10, 0, 0, 0);
        formPanel.add(errorLabel, gbc);
        formPanel.add(successLabel, gbc);

        // Wrap formPanel in a scroll pane
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPasswordPanel() {
        RoundedPanel panel = new RoundedPanel(new BorderLayout(20, 20), new Color(248, 250, 255), UIConstants.CORNER_RADIUS_MEDIUM);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 15, 0);

        // --- Section Heading ---
        JLabel sectionHeading = UIUtils.createLabel("Change Password", UIConstants.BODY_FONT_BOLD, AppColors.PRIMARY_DARK);
        sectionHeading.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        formPanel.add(sectionHeading, gbc);

        // Create password fields
        currentPasswordField = UIUtils.createRoundedPasswordField();
        newPasswordField = UIUtils.createRoundedPasswordField();
        confirmPasswordField = UIUtils.createRoundedPasswordField();
        currentPasswordField.setToolTipText("Enter your current password");
        newPasswordField.setToolTipText("Enter a new password");
        confirmPasswordField.setToolTipText("Re-enter the new password");

        // Add fields to panel with emoji icons
        addEditableFieldWithIcon(formPanel, gbc, "Current Password", currentPasswordField, "🔒");
        addEditableFieldWithIcon(formPanel, gbc, "New Password", newPasswordField, "🔑");
        addEditableFieldWithIcon(formPanel, gbc, "Confirm New Password", confirmPasswordField, "🔑");

        // Password requirements
        JTextArea requirementsArea = new JTextArea(
            "Password Requirements:\n" +
            "• Minimum 8 characters.\n" +
            "• At least one letter.\n" +
            "• At least one digit."
        );
        requirementsArea.setEditable(false);
        requirementsArea.setBackground(new Color(0,0,0,0));
        requirementsArea.setFont(UIConstants.SMALL_FONT);
        requirementsArea.setForeground(AppColors.TEXT_SECONDARY);
        requirementsArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        requirementsArea.setToolTipText("Your password must meet these requirements.");
        gbc.insets = new Insets(10, 0, 10, 0);
        formPanel.add(requirementsArea, gbc);

        // Change Password button with emoji
        JButton changePasswordButton = UIUtils.createButton("🔑 Change Password", null, UIUtils.ButtonType.PRIMARY, UIUtils.ButtonSize.NORMAL);
        changePasswordButton.setFont(new Font("Segoe UI Emoji", Font.BOLD, 14));
        changePasswordButton.addActionListener(e -> {
            // Only update password, not other fields
            String currentPassword = new String(currentPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            if (updatePassword(currentPassword, newPassword, confirmPassword)) {
                try {
                    // Save to database
                    AuthController.getInstance().updateUser(currentUser);

                    // Show success message in dialog box
                    JOptionPane.showMessageDialog(
                        this,
                        "Your password has been successfully changed!",
                        "Password Changed",
                        JOptionPane.INFORMATION_MESSAGE
                    );

                    showSuccess("Password changed successfully");
                } catch (SQLException ex) {
                    showError("Failed to update password in database: " + ex.getMessage());
                }
            }
        });
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(changePasswordButton);
        gbc.insets = new Insets(10, 0, 0, 0);
        formPanel.add(buttonPanel, gbc);

        // Add scroll pane for the form
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void addEditableFieldWithIcon(JPanel panel, GridBagConstraints gbc, String label, JComponent field, String emoji) {
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 0));
        fieldPanel.setOpaque(false);

        // Add emoji icon
        if (emoji != null) {
            JLabel iconLabel = new JLabel(emoji);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            fieldPanel.add(iconLabel, BorderLayout.WEST);
        }

        // Add field
        fieldPanel.add(field, BorderLayout.CENTER);

        // Add to form
        panel.add(UIUtils.createLabel(label, UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY), gbc);
        panel.add(fieldPanel, gbc);
    }

    private void addDetailFieldWithIcon(JPanel panel, GridBagConstraints gbc, String label, String value, String emoji) {
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 0));
        fieldPanel.setOpaque(false);

        // Add emoji icon
        if (emoji != null) {
            JLabel iconLabel = new JLabel(emoji);
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
            fieldPanel.add(iconLabel, BorderLayout.WEST);
        }

        // Add value label
        JLabel valueLabel = UIUtils.createLabel(value != null ? value : "N/A", UIConstants.BODY_FONT, AppColors.TEXT_SECONDARY);
        fieldPanel.add(valueLabel, BorderLayout.CENTER);

        // Add to form
        panel.add(UIUtils.createLabel(label, UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY), gbc);
        panel.add(fieldPanel, gbc);
    }

    private void toggleEditMode(JButton editButton) {
        if (isEditing) {
            // Save changes
            saveChanges();
            if (errorLabel.getText().isEmpty()) {
                editButton.setText("✏️ Edit Profile");
            } else {
                return; // Don't exit edit mode if there are errors
            }
        } else {
            isEditing = true;
            editButton.setText("💾 Save Changes");
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
                JOptionPane.showMessageDialog(
                    this,
                    "Please fill in all required fields.",
                    "Required Fields",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (!ValidationUtils.isValidEmail(email)) {
                showError("Please enter a valid email address");
                JOptionPane.showMessageDialog(
                    this,
                    "The email address you entered is not valid. Please enter a valid email address.",
                    "Invalid Email",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (!ValidationUtils.isValidPhoneNumber(phone)) {
                showError("Please enter a valid phone number");
                JOptionPane.showMessageDialog(
                    this,
                    "The phone number you entered is not valid. Please enter a valid phone number.",
                    "Invalid Phone Number",
                    JOptionPane.ERROR_MESSAGE
                );
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

                // Show success message in dialog box
                JOptionPane.showMessageDialog(
                    this,
                    "Your profile has been successfully updated!",
                    "Profile Updated",
                    JOptionPane.INFORMATION_MESSAGE
                );

                showSuccess("Profile updated successfully");
                isEditing = false;
                refreshUI();

                // Notify callback if provided
                if (onProfileUpdate != null) {
                    onProfileUpdate.accept(currentUser);
                }
            } catch (SQLException e) {
                showError("Failed to update profile in database: " + e.getMessage());
                JOptionPane.showMessageDialog(
                    this,
                    "Failed to update your profile: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
        } catch (Exception e) {
            showError("Error updating profile: " + e.getMessage());
            JOptionPane.showMessageDialog(
                this,
                "An error occurred while updating your profile: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private boolean updatePassword(String currentPassword, String newPassword, String confirmPassword) {
        try {
            if (currentPassword.isEmpty()) {
                showError("Current password is required to change password");
                JOptionPane.showMessageDialog(
                    this,
                    "Please enter your current password.",
                    "Password Required",
                    JOptionPane.ERROR_MESSAGE
                );
                return false;
            }

            // Verify current password
            SecurityUtils.VerificationResult verificationResult = SecurityUtils.verifyPassword(currentPassword, currentUser.getPassword());
            if (!verificationResult.isSuccess()) {
                showError("Current password is incorrect");
                JOptionPane.showMessageDialog(
                    this,
                    "The current password you entered is incorrect. Please try again.",
                    "Incorrect Password",
                    JOptionPane.ERROR_MESSAGE
                );
                return false;
            }

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                showError("New password and confirmation are required");
                JOptionPane.showMessageDialog(
                    this,
                    "Please enter both new password and confirmation.",
                    "Password Required",
                    JOptionPane.ERROR_MESSAGE
                );
                return false;
            }

            if (!newPassword.equals(confirmPassword)) {
                showError("New passwords do not match");
                JOptionPane.showMessageDialog(
                    this,
                    "The new passwords you entered do not match. Please make sure both fields contain the same password.",
                    "Passwords Don't Match",
                    JOptionPane.ERROR_MESSAGE
                );
                return false;
            }

            if (!ValidationUtils.isValidPassword(newPassword)) {
                showError("New password does not meet requirements");
                JOptionPane.showMessageDialog(
                    this,
                    "Your new password does not meet the requirements.\n" +
                    "• Minimum 8 characters\n" +
                    "• At least one uppercase letter\n" +
                    "• At least one lowercase letter",
                    "Invalid Password",
                    JOptionPane.ERROR_MESSAGE
                );
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
            JOptionPane.showMessageDialog(
                this,
                "An error occurred while changing your password: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            return false;
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
        // Remove all components
        removeAll();

        // Rebuild UI
        setupUI();

        // Refresh the panel
        revalidate();
        repaint();
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split(" ");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) sb.append(Character.toUpperCase(part.charAt(0)));
            if (sb.length() == 2) break;
        }
        return sb.toString();
    }
}

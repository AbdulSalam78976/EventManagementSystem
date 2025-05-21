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
import java.util.function.Consumer;
import java.awt.geom.Ellipse2D;

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
        
        // --- Avatar with initials ---
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int size = 60;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(AppColors.PRIMARY_DARK);
                g2.fill(new Ellipse2D.Double(0, 0, size, size));
                g2.setColor(Color.WHITE);
                String initials = getInitials(currentUser.getName());
                Font font = UIConstants.HEADER_FONT.deriveFont(Font.BOLD, 28f);
                g2.setFont(font);
                FontMetrics fm = g2.getFontMetrics();
                int x = (size - fm.stringWidth(initials)) / 2;
                int y = (size - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(initials, x, y);
                g2.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(60, 60));
        avatarPanel.setOpaque(false);
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
            editButton = UIUtils.createButton(isEditing ? "Save Changes" : "Edit Profile", 
                "edit", UIUtils.ButtonType.PRIMARY, UIUtils.ButtonSize.LARGE);
            editButton.setFont(UIConstants.BODY_FONT_BOLD);
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

        // Add fields to panel
        addEditableFieldWithIcon(formPanel, gbc, "Current Password", currentPasswordField, "lock");
        addEditableFieldWithIcon(formPanel, gbc, "New Password", newPasswordField, "key");
        addEditableFieldWithIcon(formPanel, gbc, "Confirm New Password", confirmPasswordField, "key");

        // Password requirements
        JTextArea requirementsArea = new JTextArea(
            "Password Requirements:\n" +
            "• Minimum 8 characters\n" +
            "• At least one uppercase letter\n" +
            "• At least one lowercase letter"
        );
        requirementsArea.setEditable(false);
        requirementsArea.setBackground(new Color(0,0,0,0));
        requirementsArea.setFont(UIConstants.SMALL_FONT);
        requirementsArea.setForeground(AppColors.TEXT_SECONDARY);
        requirementsArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        requirementsArea.setToolTipText("Your password must meet these requirements.");
        gbc.insets = new Insets(10, 0, 10, 0);
        formPanel.add(requirementsArea, gbc);

        // Change Password button
        JButton changePasswordButton = UIUtils.createButton("Change Password", null, UIUtils.ButtonType.PRIMARY, UIUtils.ButtonSize.NORMAL);
        changePasswordButton.addActionListener(e -> {
            // Only update password, not other fields
            String currentPassword = new String(currentPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            updatePassword(currentPassword, newPassword, confirmPassword);
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

    private void addEditableFieldWithIcon(JPanel panel, GridBagConstraints gbc, String label, JComponent field, String iconName) {
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 0));
        fieldPanel.setOpaque(false);

        // Add icon
        ImageIcon icon = IconUtils.loadIcon(iconName + ".png", IconUtils.ICON_SIZE_SMALL);
        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
            fieldPanel.add(iconLabel, BorderLayout.WEST);
        }

        // Add field
        fieldPanel.add(field, BorderLayout.CENTER);

        // Add to form
        panel.add(UIUtils.createLabel(label, UIConstants.BODY_FONT_BOLD, AppColors.TEXT_PRIMARY), gbc);
        panel.add(fieldPanel, gbc);
    }

    private void addDetailFieldWithIcon(JPanel panel, GridBagConstraints gbc, String label, String value, String iconName) {
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 0));
        fieldPanel.setOpaque(false);

        // Add icon
        ImageIcon icon = IconUtils.loadIcon(iconName + ".png", IconUtils.ICON_SIZE_SMALL);
        if (icon != null) {
            JLabel iconLabel = new JLabel(icon);
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
                
                // Notify callback if provided
                if (onProfileUpdate != null) {
                    onProfileUpdate.accept(currentUser);
                }
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

package components;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import utils.AppColors;
import utils.UIUtils;
import utils.ValidationUtils;

/**
 * A panel for displaying and editing user profile information
 */
public class ProfilePanel extends JPanel {
    private String username;
    private String email;
    private String phone;
    private String studentId;
    
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField studentIdField;
    private JPasswordField currentPasswordField;
    private JPasswordField newPasswordField;
    private JCheckBox emailNotificationsCheckbox;
    private JLabel errorLabel;
    private JLabel successLabel;
    
    /**
     * Creates a new profile panel
     * 
     * @param username The username
     * @param email The email
     * @param phone The phone number
     * @param studentId The student ID
     */
    public ProfilePanel(String username, String email, String phone, String studentId) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.studentId = studentId;
        
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Create content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add profile sections
        contentPanel.add(createPersonalInfoSection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createPasswordSection());
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(createNotificationsSection());
        
        // Add content to scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);
    }
    
    private RoundedPanel createPersonalInfoSection() {
        RoundedPanel panel = new RoundedPanel(new BorderLayout(), Color.WHITE, UIUtils.CORNER_RADIUS);
        panel.setShadow(3);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Section title
        JLabel titleLabel = new JLabel("Personal Information");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(AppColors.TEXT_PRIMARY);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 10, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        
        // Name field
        JLabel nameLabel = new JLabel("Full Name");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(nameLabel, gbc);
        
        nameField = UIUtils.createRoundedTextField();
        nameField.setText(username);
        formPanel.add(nameField, gbc);
        
        // Email field
        JLabel emailLabel = new JLabel("Email");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(emailLabel, gbc);
        
        emailField = UIUtils.createRoundedTextField();
        emailField.setText(email);
        formPanel.add(emailField, gbc);
        
        // Phone field
        JLabel phoneLabel = new JLabel("Phone");
        phoneLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(phoneLabel, gbc);
        
        phoneField = UIUtils.createRoundedTextField();
        phoneField.setText(phone);
        formPanel.add(phoneField, gbc);
        
        // Student ID field
        JLabel studentIdLabel = new JLabel("Student ID");
        studentIdLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(studentIdLabel, gbc);
        
        studentIdField = UIUtils.createRoundedTextField();
        studentIdField.setText(studentId);
        studentIdField.setEditable(false); // Student ID cannot be changed
        studentIdField.setBackground(new Color(240, 240, 240));
        formPanel.add(studentIdField, gbc);
        
        // Error and success labels
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        errorLabel.setForeground(Color.RED);
        formPanel.add(errorLabel, gbc);
        
        successLabel = new JLabel(" ");
        successLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        successLabel.setForeground(new Color(0, 150, 0));
        formPanel.add(successLabel, gbc);
        
        // Save button
        GradientButton saveButton = GradientButton.createPrimaryButton("Save Changes");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.addActionListener(e -> savePersonalInfo());
        
        // Add components to panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(saveButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private RoundedPanel createPasswordSection() {
        RoundedPanel panel = new RoundedPanel(new BorderLayout(), Color.WHITE, UIUtils.CORNER_RADIUS);
        panel.setShadow(3);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Section title
        JLabel titleLabel = new JLabel("Change Password");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(AppColors.TEXT_PRIMARY);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 10, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        
        // Current password field
        JLabel currentPasswordLabel = new JLabel("Current Password");
        currentPasswordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(currentPasswordLabel, gbc);
        
        currentPasswordField = new JPasswordField();
        currentPasswordField.setBorder(UIUtils.createRoundedBorderWithPadding(
            AppColors.BORDER_LIGHT, UIUtils.CORNER_RADIUS, 1, UIUtils.PADDING_MEDIUM));
        formPanel.add(currentPasswordField, gbc);
        
        // New password field
        JLabel newPasswordLabel = new JLabel("New Password");
        newPasswordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        formPanel.add(newPasswordLabel, gbc);
        
        newPasswordField = new JPasswordField();
        newPasswordField.setBorder(UIUtils.createRoundedBorderWithPadding(
            AppColors.BORDER_LIGHT, UIUtils.CORNER_RADIUS, 1, UIUtils.PADDING_MEDIUM));
        formPanel.add(newPasswordField, gbc);
        
        // Password requirements
        JLabel requirementsLabel = new JLabel("Password must be at least 8 characters long");
        requirementsLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        requirementsLabel.setForeground(AppColors.TEXT_SECONDARY);
        formPanel.add(requirementsLabel, gbc);
        
        // Error label for password
        JLabel passwordErrorLabel = new JLabel(" ");
        passwordErrorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordErrorLabel.setForeground(Color.RED);
        formPanel.add(passwordErrorLabel, gbc);
        
        // Save button
        GradientButton saveButton = GradientButton.createPrimaryButton("Change Password");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.addActionListener(e -> changePassword(passwordErrorLabel));
        
        // Add components to panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(saveButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private RoundedPanel createNotificationsSection() {
        RoundedPanel panel = new RoundedPanel(new BorderLayout(), Color.WHITE, UIUtils.CORNER_RADIUS);
        panel.setShadow(3);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Section title
        JLabel titleLabel = new JLabel("Notification Preferences");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(AppColors.TEXT_PRIMARY);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 0, 10, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1.0;
        
        // Email notifications checkbox
        emailNotificationsCheckbox = new JCheckBox("Receive email notifications for event updates");
        emailNotificationsCheckbox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailNotificationsCheckbox.setSelected(true);
        emailNotificationsCheckbox.setOpaque(false);
        formPanel.add(emailNotificationsCheckbox, gbc);
        
        // Save button
        GradientButton saveButton = GradientButton.createPrimaryButton("Save Preferences");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.addActionListener(e -> saveNotificationPreferences());
        
        // Add components to panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(saveButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void savePersonalInfo() {
        // Validate inputs
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
            errorLabel.setText("All fields are required");
            successLabel.setText(" ");
            return;
        }
        
        if (!isValidEmail(email)) {
            errorLabel.setText("Please enter a valid email address");
            successLabel.setText(" ");
            return;
        }
        
        if (!isValidPhoneNumber(phone)) {
            errorLabel.setText("Please enter a valid phone number (10 digits)");
            successLabel.setText(" ");
            return;
        }
        
        // In a real application, this would save to the database
        this.username = name;
        this.email = email;
        this.phone = phone;
        
        errorLabel.setText(" ");
        successLabel.setText("Personal information updated successfully");
    }
    
    private void changePassword(JLabel errorLabel) {
        // Validate inputs
        char[] currentPassword = currentPasswordField.getPassword();
        char[] newPassword = newPasswordField.getPassword();
        
        if (currentPassword.length == 0 || newPassword.length == 0) {
            errorLabel.setText("Both password fields are required");
            return;
        }
        
        if (newPassword.length < 8) {
            errorLabel.setText("New password must be at least 8 characters long");
            return;
        }
        
        // In a real application, this would verify the current password and update it
        
        // Clear password fields
        currentPasswordField.setText("");
        newPasswordField.setText("");
        
        errorLabel.setText(" ");
        JOptionPane.showMessageDialog(this,
            "Password changed successfully",
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void saveNotificationPreferences() {
        // In a real application, this would save to the database
        boolean receiveEmails = emailNotificationsCheckbox.isSelected();
        
        JOptionPane.showMessageDialog(this,
            "Notification preferences saved successfully",
            "Success",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private boolean isValidEmail(String email) {
        return ValidationUtils.isValidEmail(email);
    }
    
    private boolean isValidPhoneNumber(String phoneNumber) {
        // Remove any non-digit characters
        String digitsOnly = phoneNumber.replaceAll("\\D", "");
        // Check if it has exactly 10 digits
        return digitsOnly.length() == 10;
    }
}

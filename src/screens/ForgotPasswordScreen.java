package screens;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import controllers.AuthController;
import controllers.PasswordResetController;
import models.User;
import utils.*;
import components.*;
import java.sql.SQLException;

/**
 * Screen for handling password reset functionality
 */
public class ForgotPasswordScreen extends JFrame {
    private JTextField emailField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JTextField securityAnswer1Field;
    private JTextField securityAnswer2Field;
    private JButton verifyButton;
    private JButton resetButton;
    private JButton backButton;
    private AuthController authController;
    private PasswordResetController passwordResetController;
    private User user;
    private boolean isVerified = false;
    private JLabel errorLabel;
    private JLabel resetErrorLabel;
    private JButton submitBtn;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel emailPanel;
    private JPanel securityQuestionsPanel;
    private JPanel resetPasswordPanel;

    /**
     * Creates a new forgot password screen
     * 
     * @throws SQLException if there is an error connecting to the database
     */
    public ForgotPasswordScreen() throws SQLException {
        authController = AuthController.getInstance();
        try {
            passwordResetController = PasswordResetController.getInstance();
        } catch (SQLException e) {
            throw new SQLException("Failed to initialize password reset controller: " + e.getMessage());
        }
        setTitle("Event Management System - Forgot Password");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(700, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main panel with card layout
        mainPanel = UIUtils.createPanel(new CardLayout(), true);
        mainPanel.setBackground(AppColors.BACKGROUND_LIGHT);
        cardLayout = (CardLayout) mainPanel.getLayout();

        // Create panels for each step
        emailPanel = createEmailPanel();
        securityQuestionsPanel = createSecurityQuestionsPanel();
        resetPasswordPanel = createResetPasswordPanel();

        // Add panels to card layout
        mainPanel.add(emailPanel, "EMAIL");
        mainPanel.add(securityQuestionsPanel, "SECURITY");
        mainPanel.add(resetPasswordPanel, "RESET");

        // Show email panel by default
        cardLayout.show(mainPanel, "EMAIL");

        setContentPane(mainPanel);
    }

    private JPanel createEmailPanel() {
        JPanel panel = UIUtils.createPanel(new GridBagLayout(), true);
        panel.setBackground(AppColors.BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Center panel for content
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // System name label
        JLabel systemName = UIUtils.createLabel(
            "Event Management System",
            UIConstants.HEADER_FONT,
            AppColors.PRIMARY
        );
        systemName.setAlignmentX(Component.CENTER_ALIGNMENT);
        systemName.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Title
        JLabel titleLabel = UIUtils.createLabel(
            "<html><div style='text-align: center;'>"
            + "<h2>Forgot Password</h2>"
            + "<p style='width: 300px;'>Enter your email to reset your password</p>"
            + "</div></html>",
            UIConstants.BODY_FONT,
            AppColors.TEXT_SECONDARY
        );
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Form panel
        RoundedPanel formPanel = new RoundedPanel(new GridBagLayout(), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        formPanel.setBorder(UIUtils.createRoundedBorderWithPadding(
            AppColors.BORDER,
            UIConstants.CORNER_RADIUS_MEDIUM,
            1,
            20
        ));
        formPanel.setMaximumSize(new Dimension(500, Integer.MAX_VALUE));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Email field
        JLabel emailLabel = UIUtils.createLabel("Email:", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY);
        gbc.gridy = 0;
        formPanel.add(emailLabel, gbc);

        emailField = UIUtils.createRoundedTextField();
        emailField.setColumns(20);
        gbc.gridy++;
        formPanel.add(emailField, gbc);

        // Error label
        errorLabel = UIUtils.createLabel(" ", UIConstants.SMALL_FONT, AppColors.ERROR);
        gbc.gridy++;
        formPanel.add(errorLabel, gbc);

        // Submit button
        submitBtn = UIUtils.createButton(
            "Continue",
            null,
            UIUtils.ButtonType.PRIMARY,
            UIUtils.ButtonSize.LARGE
        );
        submitBtn.addActionListener(e -> handleEmailSubmit());
        gbc.gridy++;
        gbc.insets = new Insets(20, 10, 10, 10);
        formPanel.add(submitBtn, gbc);

        // Back to login link
        JLabel backLabel = UIUtils.createLabel("<html><u>Back to Login</u></html>", UIConstants.SMALL_FONT, AppColors.PRIMARY);
        backLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                try {
                    new LoginScreen().setVisible(true);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(ForgotPasswordScreen.this,
                        "Error opening login screen: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        gbc.gridy++;
        gbc.insets = new Insets(10, 10, 0, 10);
        formPanel.add(backLabel, gbc);

        // Add components to center panel
        centerPanel.add(systemName);
        centerPanel.add(titleLabel);
        centerPanel.add(formPanel);

        // Add center panel to main panel
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.weightx = 1.0;
        gbcMain.weighty = 1.0;
        panel.add(centerPanel, gbcMain);

        return panel;
    }

    private JPanel createSecurityQuestionsPanel() {
        JPanel panel = UIUtils.createPanel(new GridBagLayout(), true);
        panel.setBackground(AppColors.BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Center panel for content
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // System name label
        JLabel systemName = UIUtils.createLabel(
            "Event Management System",
            UIConstants.HEADER_FONT,
            AppColors.PRIMARY
        );
        systemName.setAlignmentX(Component.CENTER_ALIGNMENT);
        systemName.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Title
        JLabel titleLabel = UIUtils.createLabel(
            "<html><div style='text-align: center;'>"
            + "<h2>Security Questions</h2>"
            + "<p style='width: 300px;'>Please answer your security questions to reset your password</p>"
            + "</div></html>",
            UIConstants.BODY_FONT,
            AppColors.TEXT_SECONDARY
        );
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Form panel
        RoundedPanel formPanel = new RoundedPanel(new GridBagLayout(), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        formPanel.setBorder(UIUtils.createRoundedBorderWithPadding(
            AppColors.BORDER,
            UIConstants.CORNER_RADIUS_MEDIUM,
            1,
            20
        ));
        formPanel.setMaximumSize(new Dimension(500, Integer.MAX_VALUE));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // First security question
        JLabel question1Label = UIUtils.createLabel("Security Question 1:", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY);
        gbc.gridy = 0;
        formPanel.add(question1Label, gbc);

        JLabel question1Text = UIUtils.createLabel("", UIConstants.BODY_FONT, AppColors.TEXT_SECONDARY);
        gbc.gridy++;
        formPanel.add(question1Text, gbc);

        securityAnswer1Field = UIUtils.createRoundedTextField();
        securityAnswer1Field.setColumns(20);
        JPanel answer1FieldPanel = EmojiUtils.createEmojiTextField("💬", securityAnswer1Field);
        gbc.gridy++;
        formPanel.add(answer1FieldPanel, gbc);

        // Second security question
        JLabel question2Label = UIUtils.createLabel("Security Question 2:", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY);
        gbc.gridy++;
        formPanel.add(question2Label, gbc);

        JLabel question2Text = UIUtils.createLabel("", UIConstants.BODY_FONT, AppColors.TEXT_SECONDARY);
        gbc.gridy++;
        formPanel.add(question2Text, gbc);

        securityAnswer2Field = UIUtils.createRoundedTextField();
        securityAnswer2Field.setColumns(20);
        JPanel answer2FieldPanel = EmojiUtils.createEmojiTextField("💬", securityAnswer2Field);
        gbc.gridy++;
        formPanel.add(answer2FieldPanel, gbc);

        // Error label
        errorLabel = UIUtils.createLabel(" ", UIConstants.SMALL_FONT, AppColors.ERROR);
        gbc.gridy++;
        formPanel.add(errorLabel, gbc);

        // Submit button
        submitBtn = UIUtils.createButton(
            "Verify Answers",
            null,
            UIUtils.ButtonType.PRIMARY,
            UIUtils.ButtonSize.LARGE
        );
        submitBtn.addActionListener(e -> verifyAnswers());
        gbc.gridy++;
        gbc.insets = new Insets(20, 10, 10, 10);
        formPanel.add(submitBtn, gbc);

        // Back button
        JButton backBtn = UIUtils.createButton(
            "Back",
            null,
            UIUtils.ButtonType.SECONDARY,
            UIUtils.ButtonSize.LARGE
        );
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "EMAIL"));
        gbc.gridy++;
        gbc.insets = new Insets(10, 10, 10, 10);
        formPanel.add(backBtn, gbc);

        // Add components to center panel
        centerPanel.add(systemName);
        centerPanel.add(titleLabel);
        centerPanel.add(formPanel);

        // Add center panel to main panel
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.weightx = 1.0;
        gbcMain.weighty = 1.0;
        panel.add(centerPanel, gbcMain);

        // Store references to question labels for updating
        panel.putClientProperty("question1Label", question1Text);
        panel.putClientProperty("question2Label", question2Text);

        return panel;
    }

    private JPanel createResetPasswordPanel() {
        JPanel panel = UIUtils.createPanel(new GridBagLayout(), true);
        panel.setBackground(AppColors.BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Center panel for content
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // System name label
        JLabel systemName = UIUtils.createLabel(
            "Event Management System",
            UIConstants.HEADER_FONT,
            AppColors.PRIMARY
        );
        systemName.setAlignmentX(Component.CENTER_ALIGNMENT);
        systemName.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Title
        JLabel titleLabel = UIUtils.createLabel(
            "<html><div style='text-align: center;'>"
            + "<h2>Reset Password</h2>"
            + "<p style='width: 300px;'>Enter your new password</p>"
            + "</div></html>",
            UIConstants.BODY_FONT,
            AppColors.TEXT_SECONDARY
        );
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Form panel
        RoundedPanel formPanel = new RoundedPanel(new GridBagLayout(), Color.WHITE, UIConstants.CORNER_RADIUS_MEDIUM);
        formPanel.setBorder(UIUtils.createRoundedBorderWithPadding(
            AppColors.BORDER,
            UIConstants.CORNER_RADIUS_MEDIUM,
            1,
            20
        ));
        formPanel.setMaximumSize(new Dimension(500, Integer.MAX_VALUE));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // New password field
        JLabel newPasswordLabel = UIUtils.createLabel("New Password:", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY);
        gbc.gridy = 0;
        formPanel.add(newPasswordLabel, gbc);

        newPasswordField = UIUtils.createRoundedPasswordField();
        newPasswordField.setColumns(20);
        gbc.gridy++;
        formPanel.add(newPasswordField, gbc);

        // Confirm password field
        JLabel confirmPasswordLabel = UIUtils.createLabel("Confirm Password:", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY);
        gbc.gridy++;
        formPanel.add(confirmPasswordLabel, gbc);

        confirmPasswordField = UIUtils.createRoundedPasswordField();
        confirmPasswordField.setColumns(20);
        gbc.gridy++;
        formPanel.add(confirmPasswordField, gbc);

        // Error label
        resetErrorLabel = UIUtils.createLabel(" ", UIConstants.SMALL_FONT, AppColors.ERROR);
        gbc.gridy++;
        formPanel.add(resetErrorLabel, gbc);

        // Reset button
        resetButton = UIUtils.createButton(
            "Reset Password",
            null,
            UIUtils.ButtonType.PRIMARY,
            UIUtils.ButtonSize.LARGE
        );
        resetButton.addActionListener(e -> {
            try {
                handlePasswordReset();
            } catch (SQLException ex) {
                resetErrorLabel.setText("Error: " + ex.getMessage());
            }
        });
        gbc.gridy++;
        gbc.insets = new Insets(20, 10, 10, 10);
        formPanel.add(resetButton, gbc);

        // Back button
        backButton = UIUtils.createButton(
            "Back",
            null,
            UIUtils.ButtonType.SECONDARY,
            UIUtils.ButtonSize.LARGE
        );
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "SECURITY"));
        gbc.gridy++;
        gbc.insets = new Insets(10, 10, 10, 10);
        formPanel.add(backButton, gbc);

        // Add components to center panel
        centerPanel.add(systemName);
        centerPanel.add(titleLabel);
        centerPanel.add(formPanel);

        // Add center panel to main panel
        GridBagConstraints gbcMain = new GridBagConstraints();
        gbcMain.gridx = 0;
        gbcMain.gridy = 0;
        gbcMain.weightx = 1.0;
        gbcMain.weighty = 1.0;
        panel.add(centerPanel, gbcMain);

        return panel;
    }

    private void handleEmailSubmit() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            errorLabel.setText("Please enter your email address");
            return;
        }

        try {
            // Verify email exists and get security questions
            user = authController.findUserByEmail(email);
            if (user != null) {
                // Update security questions in the panel
                JLabel question1Label = (JLabel) securityQuestionsPanel.getClientProperty("question1Label");
                JLabel question2Label = (JLabel) securityQuestionsPanel.getClientProperty("question2Label");
                question1Label.setText(user.getSecurityQuestion1());
                question2Label.setText(user.getSecurityQuestion2());
                
                // Show security questions panel
                cardLayout.show(mainPanel, "SECURITY");
            } else {
                errorLabel.setText("Email not found. Please try again.");
            }
        } catch (SQLException e) {
            errorLabel.setText("Error: " + e.getMessage());
        }
    }

    private void verifyAnswers() {
        String answer1 = securityAnswer1Field.getText().trim();
        String answer2 = securityAnswer2Field.getText().trim();

        if (answer1.isEmpty() || answer2.isEmpty()) {
            errorLabel.setText("Please answer both security questions");
            return;
        }

        // Verify answers using PasswordResetController
        if (passwordResetController.verifySecurityAnswers(user, answer1, answer2)) {
            isVerified = true;
            // Show password reset panel
            cardLayout.show(mainPanel, "RESET");
        } else {
            errorLabel.setText("Incorrect answers. Please try again.");
        }
    }

    /**
     * Handles the password reset process
     * 
     * @throws SQLException if there is an error connecting to the database
     */
    private void handlePasswordReset() throws SQLException {
        if (!isVerified) {
            resetErrorLabel.setText("Please verify your identity first");
            return;
        }

        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            resetErrorLabel.setText("Please enter both password fields");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            resetErrorLabel.setText("Passwords do not match");
            return;
        }

        if (!ValidationUtils.isValidPassword(newPassword)) {
            resetErrorLabel.setText("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character");
            return;
        }

        try {
            // Disable reset button while processing
            resetButton.setEnabled(false);

            PasswordResetController.ResetResult result = passwordResetController.resetPassword(user.getEmail(), newPassword);
            if (result.isSuccess()) {
                JOptionPane.showMessageDialog(this,
                    "Password has been reset successfully. Please login with your new password.",
                    "Password Reset Successful",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new LoginScreen().setVisible(true);
            } else {
                resetErrorLabel.setText(result.getMessage());
            }
        } catch (SQLException e) {
            resetErrorLabel.setText("Error resetting password: " + e.getMessage());
        } finally {
            // Re-enable reset button
            resetButton.setEnabled(true);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.invokeLater(() -> {
                try {
                    new ForgotPasswordScreen().setVisible(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
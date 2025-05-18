package screens;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.SQLException;

import controllers.AuthController;
import models.User;
import utils.*;
import components.*;

/**
 * Login screen for the Event Management System
 */
public class LoginScreen extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel errorLabel;
    private AuthController authController;

    /**
     * Creates a new login screen
     *
     * @throws SQLException if there is an error connecting to the database
     */
    public LoginScreen() throws SQLException {
        authController = AuthController.getInstance();
        setTitle("Event Management System - Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(700, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setupUI();

        setVisible(true);
    }

    private void setupUI() {
        // Main panel
        JPanel mainPanel = UIUtils.createPanel(new GridBagLayout(), true);
        mainPanel.setBackground(AppColors.BACKGROUND_LIGHT);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

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
            + "<h2>Login</h2>"
            + "<p style='width: 300px;'>Enter your credentials to access your account</p>"
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
        gbc.anchor = GridBagConstraints.WEST; // Align components to the left

        // Email field
        JLabel emailLabel = UIUtils.createLabel("Email:", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY);
        gbc.gridy = 0;
        formPanel.add(emailLabel, gbc);

        emailField = UIUtils.createRoundedTextField();
        emailField.setColumns(20);
        gbc.gridy++;
        formPanel.add(emailField, gbc);

        // Password field
        JLabel passwordLabel = UIUtils.createLabel("Password:", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY);
        gbc.gridy++;
        formPanel.add(passwordLabel, gbc);

        passwordField = UIUtils.createRoundedPasswordField();
        passwordField.setColumns(20);
        gbc.gridy++;
        formPanel.add(passwordField, gbc);

        // Error label
        errorLabel = UIUtils.createLabel(" ", UIConstants.SMALL_FONT, AppColors.ERROR);
        gbc.gridy++;
        formPanel.add(errorLabel, gbc);

        // Login button
        loginButton = UIUtils.createButton(
            "Sign In",
            null,
            UIUtils.ButtonType.PRIMARY,
            UIUtils.ButtonSize.LARGE_RECTANGULAR
        );
        loginButton.addActionListener(e -> handleLogin());
        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Center button
        gbc.anchor = GridBagConstraints.CENTER; // Center button
        gbc.insets = new Insets(20, 10, 10, 10);
        formPanel.add(loginButton, gbc);

        // Forgot password link
        JLabel forgotPasswordLabel = UIUtils.createLabel("<html><u>Forgot Password?</u></html>", UIConstants.SMALL_FONT, AppColors.PRIMARY);
        forgotPasswordLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        forgotPasswordLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                try {
                    new ForgotPasswordScreen().setVisible(true);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(LoginScreen.this,
                        "Error opening forgot password screen: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        gbc.gridy++;
        gbc.insets = new Insets(10, 10, 0, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(forgotPasswordLabel, gbc);

        // Sign up link
        JLabel signupLabel = UIUtils.createLabel("<html><u>Don't have an account? Sign up</u></html>", UIConstants.SMALL_FONT, AppColors.PRIMARY);
        signupLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signupLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                try {
                    new SignupScreen().setVisible(true);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(LoginScreen.this,
                        "Error opening signup screen: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        gbc.gridy++;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        formPanel.add(signupLabel, gbc);

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
        mainPanel.add(centerPanel, gbcMain);

        setContentPane(mainPanel);
    }

    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both email and password");
            return;
        }

        try {
            System.out.println("Attempting login for email: " + email);
            AuthController.LoginResult result = authController.login(email, password);
            System.out.println("Login result success: " + result.isSuccess());

            if (result.isSuccess()) {
                User user = result.getUser();
                System.out.println("User role: " + (user != null ? user.getRole() : "null"));

                if (user != null) {
                    // Close login screen first
                    dispose();

                    // Create and show appropriate dashboard based on role
                    SwingUtilities.invokeLater(() -> {
                        try {
                            switch (user.getRole()) {
                                case ADMIN:
                                    System.out.println("User is admin, showing admin dashboard");
                                    new AdminDashboardNew().setVisible(true);
                                    break;
                                case EVENT_ORGANIZER:
                                    System.out.println("User is organizer, showing organizer dashboard");
                                    new OrganizerDashboard().setVisible(true);
                                    break;
                                case ATTENDEE:
                                    System.out.println("User is attendee, showing attendee dashboard");
                                    new AttendeeDashboardNew().setVisible(true);
                                    break;
                                default:
                                    System.out.println("Unknown role, showing error");
                                    UIUtils.showError(this, "Unknown user role: " + user.getRole());
                                    new LoginScreen().setVisible(true);
                                    break;
                            }
                        } catch (SQLException e) {
                            System.err.println("Error creating dashboard: " + e.getMessage());
                            e.printStackTrace();
                            UIUtils.showError(this, "Error opening dashboard: " + e.getMessage());
                            // If dashboard fails to open, show login screen again
                            try {
                                new LoginScreen().setVisible(true);
                            } catch (SQLException ex) {
                                UIUtils.showError(this, "Error reopening login screen: " + ex.getMessage());
                            }
                        }
                    });
                } else {
                    System.out.println("User is null after successful login");
                    errorLabel.setText("Error: User data not found");
                }
            } else {
                System.out.println("Login failed: " + result.getMessage());
                errorLabel.setText(result.getMessage());
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception during login: " + e.getMessage());
            e.printStackTrace();
            errorLabel.setText("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            try {
                new LoginScreen();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Error initializing login screen: " + e.getMessage(),
                    "Initialization Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
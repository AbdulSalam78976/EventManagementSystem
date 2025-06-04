package screens;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import components.RoundedPanel;
import controllers.AuthController;
import controllers.UserController;
import models.User;
import utils.AppColors;
import utils.EmojiUtils;
import utils.UIConstants;
import utils.UIUtils;
import utils.ValidationUtils;

public class SignupScreen extends JFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JLabel errorLabel;
    private JButton signupButton;
    private AuthController authController;
    private UserController userController;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JPanel signupPanel;
    private JPanel securityQuestionsPanel;
    private JTextField securityAnswer1Field;
    private JTextField securityAnswer2Field;
    private JComboBox<String> securityQuestion1Combo;
    private JComboBox<String> securityQuestion2Combo;
    private User user;

    private static final String[] SECURITY_QUESTIONS = {
        "What was your first pet's name?",
        "In which city were you born?",
        "What is your mother's maiden name?",
        "What was your childhood nickname?",
        "What is the name of your favorite childhood teacher?",
        "What was the make of your first car?",
        "What is your favorite movie?",
        "What is the name of the street you grew up on?"
    };

    public SignupScreen() throws SQLException {
        authController = AuthController.getInstance();
        userController = UserController.getInstance();
        setTitle("Event Management System - Sign Up");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(700, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Main panel with card layout
        mainPanel = UIUtils.createPanel(new CardLayout(), true);
        mainPanel.setBackground(AppColors.BACKGROUND_LIGHT);
        cardLayout = (CardLayout) mainPanel.getLayout();

        // Create panels
        signupPanel = createSignupPanel();
        securityQuestionsPanel = createSecurityQuestionsPanel();

        // Add panels to card layout
        mainPanel.add(signupPanel, "SIGNUP");
        mainPanel.add(securityQuestionsPanel, "SECURITY");

        // Show signup panel by default
        cardLayout.show(mainPanel, "SIGNUP");

        setContentPane(mainPanel);
    }

    private JPanel createSignupPanel() {
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
            + "<h2>Create Account</h2>"
            + "<p style='width: 300px;'>Fill in your details to create a new account</p>"
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

        // Name field with emoji
        JLabel nameLabel = UIUtils.createLabel("Full Name:", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY);
        gbc.gridy = 0;
        formPanel.add(nameLabel, gbc);

        nameField = new JTextField();
        nameField.setColumns(20);
        JPanel nameFieldPanel = EmojiUtils.createEmojiTextField("👤", nameField);
        gbc.gridy++;
        formPanel.add(nameFieldPanel, gbc);

        // Email field with emoji
        JLabel emailLabel = UIUtils.createLabel("Email:", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY);
        gbc.gridy++;
        formPanel.add(emailLabel, gbc);

        emailField = new JTextField();
        emailField.setColumns(20);
        JPanel emailFieldPanel = EmojiUtils.createEmojiTextField("📧", emailField);
        gbc.gridy++;
        formPanel.add(emailFieldPanel, gbc);

        // Password field with emoji
        JLabel passwordLabel = UIUtils.createLabel("Password:", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY);
        gbc.gridy++;
        formPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        passwordField.setColumns(20);
        JPanel passwordFieldPanel = EmojiUtils.createEmojiPasswordField("🔒", passwordField);
        gbc.gridy++;
        formPanel.add(passwordFieldPanel, gbc);

        // Role selection with emoji
        JLabel roleLabel = UIUtils.createLabel("Role:", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY);
        gbc.gridy++;
        formPanel.add(roleLabel, gbc);

        roleComboBox = new JComboBox<>(new String[]{
            "Event Organizer",
            "Attendee"
        });
        roleComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        roleComboBox.setBackground(Color.WHITE);
        roleComboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.BORDER),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        JPanel roleFieldPanel = EmojiUtils.createEmojiComboBox("👑", roleComboBox);
        gbc.gridy++;
        formPanel.add(roleFieldPanel, gbc);

        // Error label
        errorLabel = UIUtils.createLabel(" ", UIConstants.SMALL_FONT, AppColors.ERROR);
        gbc.gridy++;
        formPanel.add(errorLabel, gbc);

        // Sign up button with emoji
        signupButton = UIUtils.createButton(
            "✨ Create Account",
            null,
            UIUtils.ButtonType.PRIMARY,
            UIUtils.ButtonSize.LARGE
        );
        signupButton.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        signupButton.addActionListener(e -> handleSignup());
        gbc.gridy++;
        gbc.insets = new Insets(20, 10, 10, 10);
        formPanel.add(signupButton, gbc);

        // Back to login link
        JLabel backLabel = UIUtils.createLabel("<html><u>Already have an account? Login</u></html>", UIConstants.SMALL_FONT, AppColors.PRIMARY);
        backLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                try {
                    new LoginScreen().setVisible(true);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(SignupScreen.this,
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
            + "<p style='width: 300px;'>Please select and answer two security questions</p>"
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

        securityQuestion1Combo = new JComboBox<>(SECURITY_QUESTIONS);
        securityQuestion1Combo.setFont(UIConstants.BODY_FONT);
        JPanel question1ComboPanel = EmojiUtils.createEmojiComboBox("❓", securityQuestion1Combo);
        gbc.gridy++;
        formPanel.add(question1ComboPanel, gbc);

        securityAnswer1Field = new JTextField();
        securityAnswer1Field.setColumns(20);
        JPanel answer1FieldPanel = EmojiUtils.createEmojiTextField("💬", securityAnswer1Field);
        gbc.gridy++;
        formPanel.add(answer1FieldPanel, gbc);

        // Second security question
        JLabel question2Label = UIUtils.createLabel("Security Question 2:", UIConstants.BODY_FONT, AppColors.TEXT_PRIMARY);
        gbc.gridy++;
        formPanel.add(question2Label, gbc);

        securityQuestion2Combo = new JComboBox<>(SECURITY_QUESTIONS);
        securityQuestion2Combo.setFont(UIConstants.BODY_FONT);
        JPanel question2ComboPanel = EmojiUtils.createEmojiComboBox("❓", securityQuestion2Combo);
        gbc.gridy++;
        formPanel.add(question2ComboPanel, gbc);

        securityAnswer2Field = new JTextField();
        securityAnswer2Field.setColumns(20);
        JPanel answer2FieldPanel = EmojiUtils.createEmojiTextField("💬", securityAnswer2Field);
        gbc.gridy++;
        formPanel.add(answer2FieldPanel, gbc);

        // Error label
        errorLabel = UIUtils.createLabel(" ", UIConstants.SMALL_FONT, AppColors.ERROR);
        gbc.gridy++;
        formPanel.add(errorLabel, gbc);

        // Submit button with emoji
        JButton submitButton = UIUtils.createButton(
            "🎉 Complete Registration",
            null,
            UIUtils.ButtonType.PRIMARY,
            UIUtils.ButtonSize.LARGE
        );
        submitButton.setFont(new Font("Segoe UI Emoji", Font.BOLD, 16));
        submitButton.addActionListener(e -> handleSecurityQuestions());
        gbc.gridy++;
        gbc.insets = new Insets(20, 10, 10, 10);
        formPanel.add(submitButton, gbc);

        // Back button
        JButton backButton = UIUtils.createButton(
            "Back",
            null,
            UIUtils.ButtonType.SECONDARY,
            UIUtils.ButtonSize.LARGE
        );
        backButton.addActionListener(e -> cardLayout.show(mainPanel, "SIGNUP"));
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

    private void handleSignup() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();

        // Clear previous error
        errorLabel.setText(" ");

        // Debug logging
        System.out.println("Starting registration process...");
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Role: " + role);

        // Validate input fields
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields");
            return;
        }

        if (!ValidationUtils.isValidEmail(email)) {
            errorLabel.setText("Please enter a valid email address");
            return;
        }

        if (!ValidationUtils.isValidPassword(password)) {
            errorLabel.setText("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character");
            return;
        }

        try {
            // Check if email already exists
            if (authController.verifyEmail(email)) {
                errorLabel.setText("Email already registered");
                return;
            }

            // Create temporary user object to store in class field
            user = new User(name, email, password, User.UserRole.fromString(role));

            // Show security questions panel
            cardLayout.show(mainPanel, "SECURITY");

        } catch (SQLException e) {
            System.out.println("SQL Exception during registration: " + e.getMessage());
            e.printStackTrace();
            errorLabel.setText("Error: " + e.getMessage());
        }
    }

    private void handleSecurityQuestions() {
        String question1 = (String) securityQuestion1Combo.getSelectedItem();
        String question2 = (String) securityQuestion2Combo.getSelectedItem();
        String answer1 = securityAnswer1Field.getText().trim();
        String answer2 = securityAnswer2Field.getText().trim();

        if (answer1.isEmpty() || answer2.isEmpty()) {
            errorLabel.setText("Please answer both security questions");
            return;
        }

        if (question1.equals(question2)) {
            errorLabel.setText("Please select different security questions");
            return;
        }

        JButton submitButton = null;
        try {
            // Find and disable submit button while processing
            for (Component comp : securityQuestionsPanel.getComponents()) {
                if (comp instanceof JButton && ((JButton) comp).getText().equals("Complete Registration")) {
                    submitButton = (JButton) comp;
                    break;
                }
            }
            if (submitButton != null) {
                submitButton.setEnabled(false);
            }
            errorLabel.setText("Creating account...");

            // Get the role from the user object
            String roleStr = user.getRole().toString();

            // Register user with security questions
            AuthController.RegisterResult result = authController.register(
                user.getEmail(),
                user.getPassword(),
                user.getName(),
                roleStr,
                question1,
                answer1,
                question2,
                answer2
            );

            if (result.isSuccess()) {
                // Show success message and redirect to login
                JOptionPane.showMessageDialog(this,
                    "Registration successful! Please login with your new account.",
                    "Registration Complete",
                    JOptionPane.INFORMATION_MESSAGE);

                dispose();
                new LoginScreen().setVisible(true);
            } else {
                errorLabel.setText(result.getMessage());
                if (submitButton != null) {
                    submitButton.setEnabled(true);
                }
            }
        } catch (SQLException e) {
            errorLabel.setText("Error: " + e.getMessage());
            if (submitButton != null) {
                submitButton.setEnabled(true);
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.invokeLater(() -> {
                try {
                    new SignupScreen().setVisible(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
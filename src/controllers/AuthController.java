package controllers;

import dao.UserDAO;
import dao.SQLUserDAO;
import models.User;
import models.User.UserRole;
import java.util.regex.Pattern;
import utils.ValidationUtils;
import utils.SecurityUtils;
import java.util.Date;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller class for handling authentication-related operations
 */
public class AuthController {
    
    private static AuthController instance;
    private final UserDAO userDAO;
    private final SessionManager sessionManager;
    
    // Private constructor for singleton pattern
    private AuthController() throws SQLException {
        this.userDAO = new SQLUserDAO();
        this.sessionManager = SessionManager.getInstance();
    }
    
    /**
     * Get the singleton instance of the AuthController
     * 
     * @return The AuthController instance
     */
    public static synchronized AuthController getInstance() throws SQLException {
        if (instance == null) {
            instance = new AuthController();
        }
        return instance;
    }
    
    /**
     * Attempt to log in a user with the provided credentials
     * 
     * @param email The user's email
     * @param password The user's password
     * @return A LoginResult object containing the result of the login attempt
     */
    public LoginResult login(String email, String password) throws SQLException {
        if (!ValidationUtils.isValidEmail(email)) {
            return new LoginResult(false, "Invalid email format", null);
        }

        if (!ValidationUtils.isValidPassword(password)) {
            return new LoginResult(false, "Invalid password format", null);
        }

        User user = userDAO.findByEmail(email);
        if (user == null) {
            return new LoginResult(false, "User not found", null);
        }

        if (!user.isActive()) {
            return new LoginResult(false, "Account is inactive", null);
        }

        // Use verifyPassword to compare the input password with the stored hash
        SecurityUtils.VerificationResult verificationResult = SecurityUtils.verifyPassword(password, user.getPassword());
        
        if (!verificationResult.isSuccess()) {
            return new LoginResult(false, "Invalid password", null);
        }

        // If password verification succeeded but needs migration, update the hash
        if (verificationResult.needsMigration()) {
            user.setPassword(verificationResult.getNewHash());
            userDAO.update(user);
        }

        // Update last login timestamp
        userDAO.updateLastLogin(user.getId());
        user.setLastLoginAt(new Date());

        sessionManager.startSession(user);
        return new LoginResult(true, "Login successful", user);
    }
    
    /**
     * Register a new user in the system
     * 
     * @param email The user's email
     * @param password The user's password
     * @param name The user's full name
     * @param roleStr The user's role as a string
     * @param securityQuestion1 The first security question
     * @param securityAnswer1 The answer to the first security question
     * @param securityQuestion2 The second security question
     * @param securityAnswer2 The answer to the second security question
     * @return A RegisterResult object containing the result of the registration attempt
     * @throws SQLException if a database error occurs
     */
    public RegisterResult register(String email, String password, String name, String roleStr, String securityQuestion1, String securityAnswer1, String securityQuestion2, String securityAnswer2) {
        System.out.println("AuthController.register called with:");
        System.out.println("Email: " + email);
        System.out.println("Name: " + name);
        System.out.println("Role: " + roleStr);

        // Validate input fields
        if (!ValidationUtils.isValidEmail(email)) {
            return new RegisterResult(false, "Invalid email format");
        }

        if (!ValidationUtils.isValidPassword(password)) {
            return new RegisterResult(false, "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character");
        }

        if (!ValidationUtils.isNotEmpty(name)) {
            return new RegisterResult(false, "Name is required");
        }

        if (!ValidationUtils.isNotEmpty(roleStr)) {
            return new RegisterResult(false, "Role is required");
        }

        try {
            // Check if email already exists
            if (userDAO.findByEmail(email) != null) {
                System.out.println("Email already registered");
                return new RegisterResult(false, "Email already registered");
            }

            // Convert role string to enum
            User.UserRole role;
            try {
                // First try to convert from display name
                role = User.UserRole.fromString(roleStr);
            } catch (IllegalArgumentException e) {
                // If that fails, try to convert directly from enum name
                try {
                    role = User.UserRole.valueOf(roleStr);
                } catch (IllegalArgumentException ex) {
                    System.out.println("Invalid role: " + ex.getMessage());
                    return new RegisterResult(false, "Invalid role");
                }
            }
            System.out.println("Role converted successfully: " + role);

            try {
                System.out.println("Creating user in database...");
                // Use createUser with provided security questions
                userDAO.createUser(
                    name,
                    email,
                    password,
                    role,
                    securityQuestion1,
                    securityAnswer1,
                    securityQuestion2,
                    securityAnswer2
                );
                System.out.println("User created successfully");
                return new RegisterResult(true, "Registration successful");
            } catch (SQLException e) {
                System.out.println("SQL Exception during user creation: " + e.getMessage());
                e.printStackTrace();
                return new RegisterResult(false, "Registration failed: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception during email check: " + e.getMessage());
            e.printStackTrace();
            return new RegisterResult(false, "Registration failed: " + e.getMessage());
        }
    }
    
    /**
     * Log out the current user
     */
    public void logout() {
        sessionManager.endSession();
    }
    
    /**
     * Get the currently logged-in user
     * 
     * @return The current user, or null if no user is logged in
     */
    public User getCurrentUser() {
        return sessionManager.getCurrentUser();
    }
    
    /**
     * Check if a user is currently logged in
     * 
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return sessionManager.getCurrentUser() != null;
    }
    
    /**
     * Check if the current user has the required role
     * 
     * @param requiredRole The role required for the operation
     * @return true if the current user has the required role, false otherwise
     */
    private boolean hasRequiredRole(User.UserRole requiredRole) {
        User currentUser = getCurrentUser();
        return currentUser != null && currentUser.getRole() == requiredRole;
    }
    
    /**
     * Check if the current user is an admin
     * 
     * @return true if the current user is an admin, false otherwise
     */
    public boolean isAdmin() {
        return hasRequiredRole(User.UserRole.ADMIN);
    }
    
    /**
     * Check if the current user is an organizer
     * 
     * @return true if the current user is an organizer, false otherwise
     */
    public boolean isOrganizer() {
        return hasRequiredRole(User.UserRole.EVENT_ORGANIZER);
    }
    
    /**
     * Check if the current user is an attendee
     * 
     * @return true if the current user is an attendee, false otherwise
     */
    public boolean isAttendee() {
        return hasRequiredRole(User.UserRole.ATTENDEE);
    }
    
    /**
     * Find a user by their email address
     * 
     * @param email The email address to search for
     * @return The user if found, null otherwise
     */
    public User findUserByEmail(String email) throws SQLException {
        return userDAO.findByEmail(email);
    }
    
    /**
     * Verify if an email exists in the system
     * 
     * @param email The email to verify
     * @return true if the email exists, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean verifyEmail(String email) throws SQLException {
        return userDAO.findByEmail(email) != null;
    }
    
    /**
     * Reset a user's password
     * 
     * @param email The user's email
     * @param newPassword The new password
     * @return A ResetResult object containing the result of the reset attempt
     * @throws SQLException if a database error occurs
     */
    public ResetResult resetPassword(String email, String newPassword) throws SQLException {
        if (!ValidationUtils.isValidEmail(email)) {
            return new ResetResult(false, "Invalid email format");
        }

        if (!ValidationUtils.isValidPassword(newPassword)) {
            return new ResetResult(false, "Invalid password format");
        }

        User user = userDAO.findByEmail(email);
        if (user == null) {
            return new ResetResult(false, "User not found");
        }

        try {
            // Hash the new password before storing
            String hashedPassword = SecurityUtils.hashPassword(newPassword);
            user.setPassword(hashedPassword);
            userDAO.update(user);
            return new ResetResult(true, "Password reset successful");
        } catch (SQLException e) {
            return new ResetResult(false, "Failed to reset password: " + e.getMessage());
        }
    }
    
    /**
     * Get all users in the system
     * 
     * @return List of all users
     * @throws SQLException if a database error occurs
     */
    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }

    /**
     * Get a user by their ID
     * 
     * @param userId The ID of the user to get
     * @return The user if found, null otherwise
     * @throws SQLException if a database error occurs
     */
    public User getUserById(int userId) throws SQLException {
        return userDAO.getUserById(userId);
    }

    /**
     * Update a user's information
     * 
     * @param user The user to update
     * @throws SQLException if a database error occurs
     */
    public void updateUser(User user) throws SQLException {
        userDAO.update(user);
    }
    
    /**
     * Class representing the result of a login attempt
     */
    public static class LoginResult {
        private final boolean success;
        private final String message;
        private final User user;
        
        public LoginResult(boolean success, String message, User user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public User getUser() {
            return user;
        }
    }
    
    /**
     * Class representing the result of a registration attempt
     */
    public static class RegisterResult {
        private final boolean success;
        private final String message;
        
        public RegisterResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    /**
     * Class representing the result of a password reset attempt
     */
    public static class ResetResult {
        private final boolean success;
        private final String message;
        
        public ResetResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
        
        public boolean isSuccess() {
            return success;
        }
        
        public String getMessage() {
            return message;
        }
    }
    
    
}

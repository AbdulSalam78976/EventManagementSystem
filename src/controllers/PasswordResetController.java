package controllers;

import models.User;
import utils.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import utils.SecurityUtils;

/**
 * Controller for handling password reset functionality
 */
public class PasswordResetController {
    private static PasswordResetController instance;
    private final AuthController authController;

    private PasswordResetController() throws SQLException {
        this.authController = AuthController.getInstance();
    }

    /**
     * Gets the singleton instance of PasswordResetController
     * 
     * @return the PasswordResetController instance
     * @throws SQLException if there is an error connecting to the database
     */
    public static synchronized PasswordResetController getInstance() throws SQLException {
        if (instance == null) {
            instance = new PasswordResetController();
        }
        return instance;
    }

    /**
     * Verifies the user's security question answers
     * 
     * @param user the user to verify
     * @param answer1 the answer to the first security question
     * @param answer2 the answer to the second security question
     * @return true if the answers are correct, false otherwise
     */
    public boolean verifySecurityAnswers(User user, String answer1, String answer2) {
        if (user == null || answer1 == null || answer2 == null) {
            return false;
        }

        return answer1.equalsIgnoreCase(user.getSecurityAnswer1()) && 
               answer2.equalsIgnoreCase(user.getSecurityAnswer2());
    }

    /**
     * Resets the user's password
     * 
     * @param email the email of the user
     * @param newPassword the new password
     * @return a ResetResult object containing the result of the operation
     */
    public ResetResult resetPassword(String email, String newPassword) {
        if (email == null || newPassword == null) {
            return new ResetResult(false, "Invalid input parameters");
        }

        try {
            // Find user by email
            User user = authController.findUserByEmail(email);
            if (user == null) {
                return new ResetResult(false, "User not found");
            }

            // Hash the new password before storing
            String hashedPassword = SecurityUtils.hashPassword(newPassword);

            // Update password in database with hashed password
            try (Connection conn = DatabaseUtil.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                     "UPDATE users SET password = ? WHERE email = ?")) {
                
                stmt.setString(1, hashedPassword);  // Store hashed password
                stmt.setString(2, email);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    return new ResetResult(true, "Password reset successful");
                } else {
                    return new ResetResult(false, "Failed to update password");
                }
            }
        } catch (SQLException e) {
            return new ResetResult(false, "Error resetting password: " + e.getMessage());
        }
    }

    /**
     * Result of a password reset operation
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
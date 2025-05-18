package dao;

import models.User;
import models.User.UserRole;
import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object interface for User model
 */
public interface UserDAO {
    /**
     * Get all users from the database
     * @return List of all users
     * @throws SQLException if there is an error accessing the database
     */
    List<User> getAllUsers() throws SQLException;

    /**
     * Get a user by their ID
     * @param userId the ID of the user to get
     * @return the user with the specified ID, or null if not found
     * @throws SQLException if there is an error accessing the database
     */
    User getUserById(int userId) throws SQLException;

    /**
     * Get a user by their email
     * @param email the email of the user to get
     * @return the user with the specified email, or null if not found
     * @throws SQLException if there is an error accessing the database
     */
    User findByEmail(String email) throws SQLException;

    /**
     * Create a new user
     * @param name the user's name
     * @param email the user's email
     * @param password the user's password
     * @param role the user's role
     * @param securityQuestion1 the first security question
     * @param securityAnswer1 the answer to the first security question
     * @param securityQuestion2 the second security question
     * @param securityAnswer2 the answer to the second security question
     * @return the newly created user
     * @throws SQLException if there is an error accessing the database
     */
    User createUser(String name, String email, String password, UserRole role, String securityQuestion1, String securityAnswer1, String securityQuestion2, String securityAnswer2) throws SQLException;

    /**
     * Update an existing user
     * @param user the user to update
     * @return the updated user
     * @throws SQLException if there is an error accessing the database
     */
    User update(User user) throws SQLException;

    /**
     * Save a user (create if new, update if existing)
     * @param user the user to save
     * @return the saved user
     * @throws SQLException if there is an error accessing the database
     */
    User save(User user) throws SQLException;

    /**
     * Delete a user
     * @param userId the ID of the user to delete
     * @return true if the user was deleted successfully, false otherwise
     * @throws SQLException if there is an error accessing the database
     */
    boolean deleteUser(int userId) throws SQLException;

    /**
     * Activate a user
     * @param userId the ID of the user to activate
     * @return true if the user was activated successfully, false otherwise
     * @throws SQLException if there is an error accessing the database
     */
    boolean activateUser(int userId) throws SQLException;

    /**
     * Deactivate a user
     * @param userId the ID of the user to deactivate
     * @return true if the user was deactivated successfully, false otherwise
     * @throws SQLException if there is an error accessing the database
     */
    boolean deactivateUser(int userId) throws SQLException;
}

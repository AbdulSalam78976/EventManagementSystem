package controllers;

import models.User;
import models.User.UserRole;
import dao.UserDAO;
import dao.SQLUserDAO;
import utils.ValidationUtils;
import utils.SecurityUtils;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller class for managing user-related operations
 */
public class UserController {
    private static UserController instance;
    private final UserDAO userDAO;

    /**
     * Private constructor for singleton pattern
     * 
     * @throws SQLException if a database error occurs during initialization
     */
    private UserController() throws SQLException {
        this.userDAO = new SQLUserDAO();
    }

    /**
     * Get the singleton instance of the UserController
     * 
     * @return The UserController instance
     * @throws SQLException if a database error occurs during initialization
     */
    public static synchronized UserController getInstance() throws SQLException {
        if (instance == null) {
            instance = new UserController();
        }
        return instance;
    }

    /**
     * Get all users from the database
     * 
     * @return List of all users
     * @throws SQLException if there is an error accessing the database
     */
    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }

    /**
     * Get a user by their ID
     * 
     * @param userId the ID of the user to get
     * @return the user with the specified ID, or null if not found
     * @throws SQLException if there is an error accessing the database
     * @throws IllegalArgumentException if userId is invalid
     */
    public User getUserById(int userId) throws SQLException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        return userDAO.getUserById(userId);
    }

    /**
     * Get a user by their email
     * 
     * @param email the email of the user to get
     * @return the user with the specified email, or null if not found
     * @throws SQLException if there is an error accessing the database
     * @throws IllegalArgumentException if email is invalid
     */
    public User getUserByEmail(String email) throws SQLException {
        if (!ValidationUtils.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        return userDAO.findByEmail(email);
    }

    /**
     * Create a new user
     * 
     * @param name the user's name
     * @param email the user's email
     * @param password the user's password
     * @param role the user's role
     * @return the newly created user
     * @throws SQLException if there is an error accessing the database
     * @throws IllegalArgumentException if any input is invalid
     */
    public User createUser(String name, String email, String password, UserRole role) throws SQLException {
        // Validate input
        if (!ValidationUtils.isNotEmpty(name)) {
            throw new IllegalArgumentException("Name is required");
        }
        if (!ValidationUtils.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (!ValidationUtils.isValidPassword(password)) {
            throw new IllegalArgumentException("Invalid password format");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role is required");
        }

        // Check if email already exists
        if (userDAO.findByEmail(email) != null) {
            throw new IllegalArgumentException("Email already registered");
        }

        // Create user with default security questions
        return userDAO.createUser(
            name,
            email,
            password,
            role,
            "What is your mother's maiden name?",
            "default",
            "What was your first pet's name?",
            "default"
        );
    }

    /**
     * Update an existing user
     * 
     * @param user the user to update
     * @throws SQLException if there is an error accessing the database
     * @throws IllegalArgumentException if user is null or invalid
     */
    public void updateUser(User user) throws SQLException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        if (!ValidationUtils.isNotEmpty(user.getName())) {
            throw new IllegalArgumentException("Name is required");
        }
        if (!ValidationUtils.isValidEmail(user.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }
        if (user.getRole() == null) {
            throw new IllegalArgumentException("Role is required");
        }

        // Check if email is already used by another user
        User existingUser = userDAO.findByEmail(user.getEmail());
        if (existingUser != null && existingUser.getId() != user.getId()) {
            throw new IllegalArgumentException("Email already registered");
        }

        userDAO.update(user);
    }

    /**
     * Delete a user
     * 
     * @param userId the ID of the user to delete
     * @throws SQLException if there is an error accessing the database
     * @throws IllegalArgumentException if userId is invalid
     */
    public void deleteUser(int userId) throws SQLException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        if (userDAO.getUserById(userId) == null) {
            throw new IllegalArgumentException("User not found");
        }
        userDAO.deleteUser(userId);
    }

    /**
     * Activate a user
     * 
     * @param userId the ID of the user to activate
     * @throws SQLException if there is an error accessing the database
     * @throws IllegalArgumentException if userId is invalid or user not found
     */
    public void activateUser(int userId) throws SQLException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (user.isActive()) {
            throw new IllegalStateException("User is already active");
        }
        userDAO.activateUser(userId);
    }

    /**
     * Deactivate a user
     * 
     * @param userId the ID of the user to deactivate
     * @throws SQLException if there is an error accessing the database
     * @throws IllegalArgumentException if userId is invalid or user not found
     */
    public void deactivateUser(int userId) throws SQLException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        if (!user.isActive()) {
            throw new IllegalStateException("User is already inactive");
        }
        userDAO.deactivateUser(userId);
    }

    /**
     * Get all users with a specific role.
     * @param role The role to filter by.
     * @return A list of users with the specified role.
     * @throws SQLException if a database error occurs.
     * @throws IllegalArgumentException if the role is null.
     */
    public List<User> getUsersByRole(UserRole role) throws SQLException {
        if (role == null) {
            throw new IllegalArgumentException("User role cannot be null.");
        }
        // Assuming UserDAO has a getAllUsers method
        return userDAO.getAllUsers().stream()
            .filter(user -> user.getRole() == role)
            .collect(Collectors.toList());
    }
} 
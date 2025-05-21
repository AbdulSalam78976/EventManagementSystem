package dao;

import models.User;
import models.User.UserRole;
import utils.DatabaseConnection;
import utils.SecurityUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for User model using SQL database
 */
public class SQLUserDAO implements UserDAO {
    private final Connection connection;

    public SQLUserDAO() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Get all users from the database
     * @return List of all users
     * @throws SQLException if there is an error accessing the database
     */
    @Override
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                users.add(createUserFromResultSet(rs));
            }
        }
        
        return users;
    }

    /**
     * Get a user by their ID
     * @param userId the ID of the user to get
     * @return the user with the specified ID, or null if not found
     * @throws SQLException if there is an error accessing the database
     */
    @Override
    public User getUserById(int userId) throws SQLException {
        String query = "SELECT * FROM users WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createUserFromResultSet(rs);
                }
            }
        }
        
        return null;
    }

    /**
     * Get a user by their email
     * @param email the email of the user to get
     * @return the user with the specified email, or null if not found
     * @throws SQLException if there is an error accessing the database
     */
    @Override
    public User findByEmail(String email) throws SQLException {
        String query = "SELECT * FROM users WHERE email = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return createUserFromResultSet(rs);
                }
            }
        }
        
        return null;
    }

    /**
     * Create a new user
     * @param name the user's name
     * @param email the user's email
     * @param password the user's password
     * @param role the user's role
     * @param securityQuestion1 the first security question
     * @param securityAnswer1 the first security answer
     * @param securityQuestion2 the second security question
     * @param securityAnswer2 the second security answer
     * @return the newly created user
     * @throws SQLException if there is an error accessing the database
     */
    @Override
    public User createUser(String name, String email, String password, UserRole role, String securityQuestion1, String securityAnswer1, String securityQuestion2, String securityAnswer2) throws SQLException {
        System.out.println("SQLUserDAO.createUser called with:");
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Role: " + role);

        String query = "INSERT INTO users (name, email, password, role, active, registration_date, security_question1, security_answer1, security_question2, security_answer2) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, SecurityUtils.hashPassword(password));
            stmt.setString(4, role.toString());
            stmt.setBoolean(5, true);
            stmt.setDate(6, Date.valueOf(java.time.LocalDate.now().toString()));
            stmt.setString(7, securityQuestion1);
            stmt.setString(8, securityAnswer1);
            stmt.setString(9, securityQuestion2);
            stmt.setString(10, securityAnswer2);

            System.out.println("Executing SQL query...");
            int affectedRows = stmt.executeUpdate();
            System.out.println("Affected rows: " + affectedRows);

            if (affectedRows == 0) {
                System.out.println("No rows affected during user creation");
                throw new SQLException("Creating user failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    System.out.println("User created with ID: " + userId);
                    User user = new User(name, email, password, role);
                    user.setId(userId);
                    return user;
                }
                System.out.println("No ID obtained after user creation");
                throw new SQLException("Creating user failed, no ID obtained.");
            }
        } catch (SQLException e) {
            System.out.println("SQL Exception in createUser: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Update an existing user
     * @param user the user to update
     * @throws SQLException if there is an error accessing the database
     */
    @Override
    public User update(User user) throws SQLException {
        String query = "UPDATE users SET name = ?, email = ?, password = ?, role = ?, active = ?, phone = ?, security_question1 = ?, security_answer1 = ?, security_question2 = ?, security_answer2 = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            // Only hash if the password is not already hashed (e.g., check length or pattern)
            String password = user.getPassword();
            if (password.length() < 60) { // Assuming BCrypt hashed passwords are 60 chars
                password = SecurityUtils.hashPassword(password);
            }
            stmt.setString(3, password);
            stmt.setString(4, user.getRole().toString());
            stmt.setBoolean(5, user.isActive());
            stmt.setString(6, user.getPhone());
            stmt.setString(7, user.getSecurityQuestion1());
            stmt.setString(8, user.getSecurityAnswer1());
            stmt.setString(9, user.getSecurityQuestion2());
            stmt.setString(10, user.getSecurityAnswer2());
            stmt.setInt(11, user.getId());
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating user failed, no rows affected.");
            }
            return user;
        }
    }

    /**
     * Delete a user
     * @param userId the ID of the user to delete
     * @throws SQLException if there is an error accessing the database
     */
    @Override
    public boolean deleteUser(int userId) throws SQLException {
        String query = "DELETE FROM users WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Activate a user
     * @param userId the ID of the user to activate
     * @throws SQLException if there is an error accessing the database
     */
    @Override
    public boolean activateUser(int userId) throws SQLException {
        String query = "UPDATE users SET active = true WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Deactivate a user
     * @param userId the ID of the user to deactivate
     * @throws SQLException if there is an error accessing the database
     */
    @Override
    public boolean deactivateUser(int userId) throws SQLException {
        String query = "UPDATE users SET active = false WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    /**
     * Helper method to create a User object from a ResultSet
     * @param rs the ResultSet containing user data
     * @return a User object
     * @throws SQLException if there is an error accessing the database
     */
    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User(
            rs.getString("name"),
            rs.getString("email"),
            rs.getString("password"),
            UserRole.valueOf(rs.getString("role"))
        );
        user.setId(rs.getInt("id"));
        user.setPhone(rs.getString("phone"));
        user.setActive(rs.getBoolean("active"));
        user.setRegistrationDate(rs.getDate("registration_date").toString());
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setLastLoginAt(rs.getTimestamp("last_login_at"));
        user.setSecurityQuestion1(rs.getString("security_question1"));
        user.setSecurityAnswer1(rs.getString("security_answer1"));
        user.setSecurityQuestion2(rs.getString("security_question2"));
        user.setSecurityAnswer2(rs.getString("security_answer2"));
        return user;
    }

    public void updateLastLogin(int userId) throws SQLException {
        String query = "UPDATE users SET last_login_at = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    @Override
    public User save(User user) throws SQLException {
        if (user.getId() == 0) {
            return createUser(user.getName(), user.getEmail(), user.getPassword(), user.getRole(), user.getSecurityQuestion1(), user.getSecurityAnswer1(), user.getSecurityQuestion2(), user.getSecurityAnswer2());
        } else {
            return update(user);
        }
    }

    @Override
    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
                return null;
            }
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setPhone(rs.getString("phone"));
        user.setRole(User.UserRole.valueOf(rs.getString("role")));
        user.setActive(rs.getBoolean("active"));
        user.setRegistrationDate(rs.getString("registration_date"));
        
        Timestamp lastLoginAt = rs.getTimestamp("last_login_at");
        if (lastLoginAt != null) {
            user.setLastLoginAt(new Date(lastLoginAt.getTime()));
        }
        
        return user;
    }

    @Override
    public List<User> findAll() throws SQLException {
        String sql = "SELECT * FROM users";
        List<User> users = new ArrayList<>();
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        }
        return users;
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public User getUserByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
                return null;
            }
        }
    }
} 
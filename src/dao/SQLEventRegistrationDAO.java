package dao;

import models.Registration;
import models.Registration.Status;
import models.Event;
import models.User;
import utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * SQL implementation of the RegistrationDAO interface
 */
public class SQLEventRegistrationDAO implements RegistrationDAO {
    private final Connection connection;
    private final EventDAO eventDAO;
    private final UserDAO userDAO;
    
    public SQLEventRegistrationDAO() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
        this.eventDAO = new SQLEventDAO();
        this.userDAO = new SQLUserDAO();
    }
    
    @Override
    public Registration findById(int id) throws SQLException {
        String sql = "SELECT * FROM registrations WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRegistration(rs);
                }
            }
        }
        return null;
    }
    
    @Override
    public List<Registration> findAll() throws SQLException {
        String sql = "SELECT * FROM registrations ORDER BY registration_date DESC";
        List<Registration> registrations = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                registrations.add(mapResultSetToRegistration(rs));
            }
        }
        return registrations;
    }
    
    @Override
    public List<Registration> findByUser(int userId) throws SQLException {
        String sql = "SELECT * FROM registrations WHERE user_id = ? ORDER BY registration_date DESC";
        List<Registration> registrations = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registrations.add(mapResultSetToRegistration(rs));
                }
            }
        }
        return registrations;
    }
    
    @Override
    public List<Registration> findByEvent(int eventId) throws SQLException {
        String sql = "SELECT * FROM registrations WHERE event_id = ? ORDER BY registration_date DESC";
        List<Registration> registrations = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registrations.add(mapResultSetToRegistration(rs));
                }
            }
        }
        return registrations;
    }
    
    @Override
    public List<Registration> findByStatus(Status status) throws SQLException {
        String sql = "SELECT * FROM registrations WHERE status = ? ORDER BY registration_date DESC";
        List<Registration> registrations = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registrations.add(mapResultSetToRegistration(rs));
                }
            }
        }
        return registrations;
    }
    
    @Override
    public List<Registration> findByUserAndEvent(int userId, int eventId) throws SQLException {
        String sql = "SELECT * FROM registrations WHERE user_id = ? AND event_id = ? ORDER BY registration_date DESC";
        List<Registration> registrations = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    registrations.add(mapResultSetToRegistration(rs));
                }
            }
        }
        return registrations;
    }
    
    @Override
    public boolean existsByUserAndEvent(int userId, int eventId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM registrations WHERE user_id = ? AND event_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
    
    @Override
    public Registration findOldestWaitlisted(int eventId) throws SQLException {
        String sql = "SELECT * FROM registrations WHERE event_id = ? AND status = 'WAITLISTED' " +
                    "ORDER BY registration_date ASC LIMIT 1";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToRegistration(rs);
                }
            }
        }
        return null;
    }
    
    @Override
    public Registration save(Registration registration) throws SQLException {
        String sql = "INSERT INTO registrations (event_id, user_id, status, registration_date, checked_in) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, registration.getEvent().getId());
            stmt.setInt(2, registration.getAttendee().getId());
            stmt.setString(3, registration.getStatus().name());
            stmt.setTimestamp(4, Timestamp.valueOf(registration.getRegistrationDate()));
            stmt.setBoolean(5, registration.isCheckedIn());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating registration failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    registration.setId(generatedKeys.getInt(1));
                    return registration;
                } else {
                    throw new SQLException("Creating registration failed, no ID obtained.");
                }
            }
        }
    }
    
    @Override
    public Registration update(Registration registration) throws SQLException {
        String sql = "UPDATE registrations SET status = ?, checked_in = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, registration.getStatus().name());
            stmt.setBoolean(2, registration.isCheckedIn());
            stmt.setInt(3, registration.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating registration failed, no rows affected.");
            }
            return registration;
        }
    }
    
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM registrations WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    @Override
    public int countByEvent(int eventId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM registrations WHERE event_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    @Override
    public int countByEventAndStatus(int eventId, Status status) throws SQLException {
        String sql = "SELECT COUNT(*) FROM registrations WHERE event_id = ? AND status = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            stmt.setString(2, status.name());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
    
    private Registration mapResultSetToRegistration(ResultSet rs) throws SQLException {
        Registration registration = new Registration();
        registration.setId(rs.getInt("id"));
        registration.setStatus(Status.valueOf(rs.getString("status")));
        registration.setRegistrationDate(rs.getTimestamp("registration_date").toLocalDateTime());
        registration.setCheckedIn(rs.getBoolean("checked_in"));
        
        // Load related entities
        int eventId = rs.getInt("event_id");
        int userId = rs.getInt("user_id");
        
        // Load event
        Event event = eventDAO.findById(eventId);
        if (event == null) {
            throw new SQLException("Event not found for ID: " + eventId);
        }
        registration.setEvent(event);
        
        // Load user
        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new SQLException("User not found for ID: " + userId);
        }
        registration.setAttendee(user);
        
        return registration;
    }
} 
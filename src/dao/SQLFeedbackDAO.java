package dao;

import models.Feedback;
import models.Event;
import models.User;
import utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * SQL implementation of the FeedbackDAO interface
 */
public class SQLFeedbackDAO implements FeedbackDAO {
    private final Connection connection;
    private final EventDAO eventDAO;
    private final UserDAO userDAO;
    
    public SQLFeedbackDAO() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
        this.eventDAO = new SQLEventDAO();
        this.userDAO = new SQLUserDAO();
    }
    
    @Override
    public List<Feedback> getAllFeedback() throws SQLException {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT * FROM feedback ORDER BY submitted_at DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                feedbackList.add(mapResultSetToFeedback(rs));
            }
        }
        return feedbackList;
    }
    
    @Override
    public Feedback findById(int feedbackId) throws SQLException {
        String sql = "SELECT * FROM feedback WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, feedbackId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFeedback(rs);
                }
            }
        }
        return null;
    }
    
    @Override
    public List<Feedback> findByEvent(int eventId) throws SQLException {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT * FROM feedback WHERE event_id = ? ORDER BY submitted_at DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    feedbackList.add(mapResultSetToFeedback(rs));
                }
            }
        }
        return feedbackList;
    }
    
    @Override
    public List<Feedback> findByUser(int userId) throws SQLException {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT * FROM feedback WHERE user_id = ? ORDER BY submitted_at DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    feedbackList.add(mapResultSetToFeedback(rs));
                }
            }
        }
        return feedbackList;
    }
    
    @Override
    public Feedback save(Feedback feedback) throws SQLException {
        String sql = "INSERT INTO feedback (event_id, user_id, rating, comment, submitted_at, is_anonymous) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, feedback.getEvent().getId());
            stmt.setInt(2, feedback.getUser().getId());
            stmt.setInt(3, feedback.getRating());
            stmt.setString(4, feedback.getComment());
            stmt.setTimestamp(5, Timestamp.valueOf(feedback.getSubmittedAt()));
            stmt.setBoolean(6, feedback.isAnonymous());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating feedback failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    feedback.setId(generatedKeys.getInt(1));
                    return feedback;
                } else {
                    throw new SQLException("Creating feedback failed, no ID obtained.");
                }
            }
        }
    }
    
    @Override
    public Feedback update(Feedback feedback) throws SQLException {
        String sql = "UPDATE feedback SET rating = ?, comment = ?, is_anonymous = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, feedback.getRating());
            stmt.setString(2, feedback.getComment());
            stmt.setBoolean(3, feedback.isAnonymous());
            stmt.setInt(4, feedback.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating feedback failed, no rows affected.");
            }
            return feedback;
        }
    }
    
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM feedback WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    @Override
    public boolean existsByUserAndEvent(int userId, int eventId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM feedback WHERE user_id = ? AND event_id = ?";
        
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
    public List<Feedback> getAnonymousFeedback() throws SQLException {
        List<Feedback> feedbackList = new ArrayList<>();
        String sql = "SELECT * FROM feedback WHERE is_anonymous = true ORDER BY submitted_at DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                feedbackList.add(mapResultSetToFeedback(rs));
            }
        }
        return feedbackList;
    }
    
    @Override
    public double getAverageRatingForEvent(int eventId) throws SQLException {
        String sql = "SELECT AVG(rating) FROM feedback WHERE event_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        }
        return 0.0;
    }
    
    @Override
    public int getRatingCountForEvent(int eventId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM feedback WHERE event_id = ?";
        
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
    
    private Feedback mapResultSetToFeedback(ResultSet rs) throws SQLException {
        Feedback feedback = new Feedback();
        feedback.setId(rs.getInt("id"));
        feedback.setRating(rs.getInt("rating"));
        feedback.setComment(rs.getString("comment"));
        feedback.setSubmittedAt(rs.getTimestamp("submitted_at").toLocalDateTime());
        feedback.setAnonymous(rs.getBoolean("is_anonymous"));
        
        // Load related entities
        int eventId = rs.getInt("event_id");
        int userId = rs.getInt("user_id");
        
        // Load event
        Event event = eventDAO.findById(eventId);
        if (event == null) {
            throw new SQLException("Event not found for ID: " + eventId);
        }
        feedback.setEvent(event);
        
        // Load user
        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new SQLException("User not found for ID: " + userId);
        }
        feedback.setUser(user);
        
        return feedback;
    }
} 
package dao;

import models.Notification;
import models.User;
import models.Event;
import models.Notification.NotificationType;
import utils.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * SQL implementation of the NotificationDAO interface
 */
public class SQLNotificationDAO implements NotificationDAO {
    private final Connection connection;
    private final UserDAO userDAO;
    private final EventDAO eventDAO;
    
    public SQLNotificationDAO() throws SQLException {
        this.connection = DatabaseConnection.getConnection();
        this.userDAO = new SQLUserDAO();
        this.eventDAO = new SQLEventDAO();
    }
    
    @Override
    public Notification save(Notification notification) throws SQLException {
        String sql = "INSERT INTO notifications (recipient_id, title, message, type, event_id, is_read, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
                    
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, notification.getRecipient().getId());
            stmt.setString(2, notification.getTitle());
            stmt.setString(3, notification.getMessage());
            stmt.setString(4, notification.getType().toString());
            stmt.setObject(5, notification.getEvent() != null ? notification.getEvent().getId() : null);
            stmt.setBoolean(6, notification.isRead());
            stmt.setTimestamp(7, Timestamp.valueOf(notification.getCreatedAt()));
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating notification failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    notification.setId(generatedKeys.getInt(1));
                    return notification;
                } else {
                    throw new SQLException("Creating notification failed, no ID obtained.");
                }
            }
        }
    }
    
    @Override
    public Notification update(Notification notification) throws SQLException {
        String sql = "UPDATE notifications SET title = ?, message = ?, type = ?, event_id = ?, " +
                    "is_read = ?, created_at = ? WHERE id = ?";
                    
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, notification.getTitle());
            stmt.setString(2, notification.getMessage());
            stmt.setString(3, notification.getType().toString());
            stmt.setObject(4, notification.getEvent() != null ? notification.getEvent().getId() : null);
            stmt.setBoolean(5, notification.isRead());
            stmt.setTimestamp(6, Timestamp.valueOf(notification.getCreatedAt()));
            stmt.setInt(7, notification.getId());
            
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Updating notification failed, no rows affected.");
            }
            return notification;
        }
    }
    
    @Override
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM notifications WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }
    
    @Override
    public Notification findById(int id) throws SQLException {
        String sql = "SELECT * FROM notifications WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToNotification(rs);
                }
            }
        }
        return null;
    }
    
    @Override
    public List<Notification> findAll() throws SQLException {
        String sql = "SELECT * FROM notifications ORDER BY created_at DESC";
        List<Notification> notifications = new ArrayList<>();
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                notifications.add(mapResultSetToNotification(rs));
            }
        }
        return notifications;
    }
    
    @Override
    public List<Notification> findByRecipient(int userId) throws SQLException {
        String sql = "SELECT * FROM notifications WHERE recipient_id = ? ORDER BY created_at DESC";
        List<Notification> notifications = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        }
        return notifications;
    }
    
    @Override
    public List<Notification> findUnreadByRecipient(int userId) throws SQLException {
        String sql = "SELECT * FROM notifications WHERE recipient_id = ? AND is_read = false ORDER BY created_at DESC";
        List<Notification> notifications = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        }
        return notifications;
    }
    
    @Override
    public List<Notification> findByType(NotificationType type) throws SQLException {
        String sql = "SELECT * FROM notifications WHERE type = ? ORDER BY created_at DESC";
        List<Notification> notifications = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, type.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        }
        return notifications;
    }
    
    @Override
    public List<Notification> findByEvent(int eventId) throws SQLException {
        String sql = "SELECT * FROM notifications WHERE event_id = ? ORDER BY created_at DESC";
        List<Notification> notifications = new ArrayList<>();
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
        }
        return notifications;
    }
    
    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setId(rs.getInt("id"));
        
        // Get recipient
        int recipientId = rs.getInt("recipient_id");
        User recipient = userDAO.getUserById(recipientId);
        if (recipient == null) {
            throw new SQLException("User not found for ID: " + recipientId);
        }
        notification.setRecipient(recipient);
        
        notification.setTitle(rs.getString("title"));
        notification.setMessage(rs.getString("message"));
        notification.setType(NotificationType.valueOf(rs.getString("type")));
        
        // Get event if exists
        int eventId = rs.getInt("event_id");
        if (eventId > 0) {
            Event event = eventDAO.findById(eventId);
            if (event == null) {
                throw new SQLException("Event not found for ID: " + eventId);
            }
            notification.setEvent(event);
        }
        
        notification.setRead(rs.getBoolean("is_read"));
        notification.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        
        return notification;
    }
} 
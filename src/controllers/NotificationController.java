package controllers;

import models.Notification;
import models.User;
import models.Event;
import models.Registration;
import models.Notification.NotificationType;
import dao.NotificationDAO;
import dao.SQLNotificationDAO;
import dao.UserDAO;
import dao.SQLUserDAO;
import utils.ValidationUtils;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller class for handling notification-related operations
 */
public class NotificationController {
    
    private static NotificationController instance;
    private final NotificationDAO notificationDAO;
    private final UserDAO userDAO;
    
    /**
     * Private constructor for singleton pattern
     * 
     * @throws SQLException if a database error occurs during initialization
     */
    private NotificationController() throws SQLException {
        this.notificationDAO = new SQLNotificationDAO();
        this.userDAO = new SQLUserDAO();
    }
    
    /**
     * Get the singleton instance of the NotificationController
     * 
     * @return The NotificationController instance
     * @throws SQLException if a database error occurs during initialization
     */
    public static synchronized NotificationController getInstance() throws SQLException {
        if (instance == null) {
            instance = new NotificationController();
        }
        return instance;
    }
    
    /**
     * Send a notification to a user
     * 
     * @param userId The ID of the recipient
     * @param title The notification title
     * @param message The notification message
     * @param type The notification type
     * @return The created notification
     * @throws IllegalArgumentException if the input is invalid
     * @throws SQLException if a database error occurs
     */
    public Notification sendNotification(int userId, String title, String message, NotificationType type) 
            throws SQLException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        
        if (!ValidationUtils.isNotEmpty(title)) {
            throw new IllegalArgumentException("Notification title cannot be empty");
        }
        
        if (!ValidationUtils.isNotEmpty(message)) {
            throw new IllegalArgumentException("Notification message cannot be empty");
        }
        
        if (type == null) {
            throw new IllegalArgumentException("Notification type cannot be null");
        }
        
        User recipient = userDAO.getUserById(userId);
        if (recipient == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        Notification notification = new Notification(recipient, title.trim(), message.trim(), type);
        return notificationDAO.save(notification);
    }
    
    /**
     * Send a notification to a user about an event
     * 
     * @param userId The ID of the recipient
     * @param title The notification title
     * @param message The notification message
     * @param type The notification type
     * @param event The event
     * @return The created notification
     * @throws IllegalArgumentException if the input is invalid
     * @throws SQLException if a database error occurs
     */
    public Notification sendEventNotification(int userId, String title, String message, 
                                             NotificationType type, Event event) 
            throws SQLException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        
        if (!ValidationUtils.isNotEmpty(title)) {
            throw new IllegalArgumentException("Notification title cannot be empty");
        }
        
        if (!ValidationUtils.isNotEmpty(message)) {
            throw new IllegalArgumentException("Notification message cannot be empty");
        }
        
        if (type == null) {
            throw new IllegalArgumentException("Notification type cannot be null");
        }
        
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        
        User recipient = userDAO.getUserById(userId);
        if (recipient == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        Notification notification = new Notification(recipient, title.trim(), message.trim(), type, event);
        return notificationDAO.save(notification);
    }
    
    /**
     * Send a notification to all users with a specific role
     * 
     * @param title The notification title
     * @param message The notification message
     * @param type The notification type
     * @param role The role to filter recipients by
     * @return The number of notifications sent
     * @throws IllegalArgumentException if the input is invalid
     * @throws SQLException if a database error occurs
     */
    public int sendNotificationToRole(String title, String message, NotificationType type, User.UserRole role) 
            throws SQLException {
        if (!ValidationUtils.isNotEmpty(title)) {
            throw new IllegalArgumentException("Notification title cannot be empty");
        }
        
        if (!ValidationUtils.isNotEmpty(message)) {
            throw new IllegalArgumentException("Notification message cannot be empty");
        }
        
        if (type == null) {
            throw new IllegalArgumentException("Notification type cannot be null");
        }
        
        if (role == null) {
            throw new IllegalArgumentException("User role cannot be null");
        }
        
        List<User> recipients = userDAO.getAllUsers().stream()
            .filter(user -> user.getRole() == role)
            .collect(Collectors.toList());
            
        int count = 0;
        for (User recipient : recipients) {
            try {
                sendNotification(recipient.getId(), title, message, type);
                count++;
            } catch (SQLException e) {
                // Log error but continue with other recipients
                System.err.println("Failed to send notification to user " + recipient.getId() + ": " + e.getMessage());
            }
        }
        return count;
    }
    
    /**
     * Send a notification to all participants of an event
     * 
     * @param event The event
     * @param title The notification title
     * @param message The notification message
     * @param type The notification type
     * @return The number of notifications sent
     * @throws IllegalArgumentException if the input is invalid
     * @throws SQLException if a database error occurs
     */
    public int sendNotificationToEventParticipants(Event event, String title, String message, NotificationType type) 
            throws SQLException {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        
        if (!ValidationUtils.isNotEmpty(title)) {
            throw new IllegalArgumentException("Notification title cannot be empty");
        }
        
        if (!ValidationUtils.isNotEmpty(message)) {
            throw new IllegalArgumentException("Notification message cannot be empty");
        }
        
        if (type == null) {
            throw new IllegalArgumentException("Notification type cannot be null");
        }
        
        int count = 0;
        for (Registration registration : event.getRegistrations()) {
            try {
                sendEventNotification(registration.getAttendee().getId(), title, message, type, event);
                count++;
            } catch (SQLException e) {
                // Log error but continue with other recipients
                System.err.println("Failed to send notification to user " + registration.getAttendee().getId() + ": " + e.getMessage());
            }
        }
        return count;
    }
    
    /**
     * Mark a notification as read
     * 
     * @param notificationId The ID of the notification to mark as read
     * @return The updated notification
     * @throws IllegalArgumentException if the notification ID is invalid or notification not found
     * @throws SQLException if a database error occurs
     */
    public Notification markAsRead(int notificationId) throws SQLException {
        if (notificationId <= 0) {
            throw new IllegalArgumentException("Invalid notification ID");
        }
        
        Notification notification = notificationDAO.findById(notificationId);
        if (notification == null) {
            throw new IllegalArgumentException("Notification not found");
        }
        
        if (notification.isRead()) {
            throw new IllegalStateException("Notification is already marked as read");
        }
        
        notification.markAsRead();
        return notificationDAO.update(notification);
    }
    
    /**
     * Mark all notifications for a user as read
     * 
     * @param userId The ID of the user
     * @return The number of notifications marked as read
     * @throws IllegalArgumentException if the user ID is invalid
     * @throws SQLException if a database error occurs
     */
    public int markAllAsRead(int userId) throws SQLException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        
        if (userDAO.getUserById(userId) == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        List<Notification> notifications = notificationDAO.findUnreadByRecipient(userId);
        int count = 0;
        for (Notification notification : notifications) {
            try {
                notification.markAsRead();
                notificationDAO.update(notification);
                count++;
            } catch (SQLException e) {
                // Log error but continue with other notifications
                System.err.println("Failed to mark notification " + notification.getId() + " as read: " + e.getMessage());
            }
        }
        return count;
    }
    
    /**
     * Delete a notification
     * 
     * @param notificationId The ID of the notification to delete
     * @return true if the notification was deleted, false otherwise
     * @throws IllegalArgumentException if the notification ID is invalid or notification not found
     * @throws SQLException if a database error occurs
     */
    public boolean deleteNotification(int notificationId) throws SQLException {
        if (notificationId <= 0) {
            throw new IllegalArgumentException("Invalid notification ID");
        }
        
        Notification notification = notificationDAO.findById(notificationId);
        if (notification == null) {
            throw new IllegalArgumentException("Notification not found");
        }
        
        return notificationDAO.delete(notificationId);
    }
    
    /**
     * Get a notification by ID
     * 
     * @param notificationId The ID of the notification to get
     * @return The notification, or null if not found
     * @throws IllegalArgumentException if the notification ID is invalid
     * @throws SQLException if a database error occurs
     */
    public Notification getNotificationById(int notificationId) throws SQLException {
        if (notificationId <= 0) {
            throw new IllegalArgumentException("Invalid notification ID");
        }
        
        return notificationDAO.findById(notificationId);
    }
    
    /**
     * Get all notifications for a user
     * 
     * @param userId The ID of the user
     * @return A list of notifications for the user
     * @throws IllegalArgumentException if the user ID is invalid
     * @throws SQLException if a database error occurs
     */
    public List<Notification> getNotificationsByUser(int userId) throws SQLException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        
        if (userDAO.getUserById(userId) == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        return notificationDAO.findByRecipient(userId);
    }
    
    /**
     * Get all unread notifications for a user
     * 
     * @param userId The ID of the user
     * @return A list of unread notifications for the user
     * @throws IllegalArgumentException if the user ID is invalid
     * @throws SQLException if a database error occurs
     */
    public List<Notification> getUnreadNotificationsByUser(int userId) throws SQLException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        
        if (userDAO.getUserById(userId) == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        return notificationDAO.findUnreadByRecipient(userId);
    }
    
    /**
     * Get all notifications of a specific type
     * 
     * @param type The notification type
     * @return A list of notifications of the specified type
     * @throws IllegalArgumentException if the type is null
     * @throws SQLException if a database error occurs
     */
    public List<Notification> getNotificationsByType(NotificationType type) throws SQLException {
        if (type == null) {
            throw new IllegalArgumentException("Notification type cannot be null");
        }
        
        return notificationDAO.findByType(type);
    }
    
    /**
     * Get all notifications for an event
     * 
     * @param eventId The ID of the event
     * @return A list of notifications for the event
     * @throws IllegalArgumentException if the event ID is invalid
     * @throws SQLException if a database error occurs
     */
    public List<Notification> getNotificationsByEvent(int eventId) throws SQLException {
        if (eventId <= 0) {
            throw new IllegalArgumentException("Invalid event ID");
        }
        
        return notificationDAO.findByEvent(eventId);
    }
    
    /**
     * Send a reminder notification for an event
     * 
     * @param event The event to send a reminder for
     * @param hoursBeforeEvent The number of hours before the event to send the reminder
     * @throws IllegalArgumentException if the input is invalid
     * @throws SQLException if a database error occurs
     */
    public void sendEventReminder(Event event, int hoursBeforeEvent) throws SQLException {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }
        
        if (hoursBeforeEvent <= 0) {
            throw new IllegalArgumentException("Hours before event must be positive");
        }
        
        LocalDateTime reminderTime = event.getStartDateTime().minusHours(hoursBeforeEvent);
        if (reminderTime.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Reminder time has already passed");
        }
        
        String title = "Event Reminder: " + event.getName();
        String message = String.format("This is a reminder that %s starts in %d hours.", 
                                     event.getName(), hoursBeforeEvent);
        
        sendNotificationToEventParticipants(event, title, message, NotificationType.EVENT_REMINDER);
    }
}

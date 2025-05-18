package dao;

import java.util.List;
import models.Notification;
import models.User;
import models.Event;
import java.sql.SQLException;
import models.Notification.NotificationType;

/**
 * Data Access Object interface for Notification entities
 */
public interface NotificationDAO {
    /**
     * Get all notifications
     *
     * @return List of all notifications
     * @throws SQLException if a database error occurs
     */
    List<Notification> findAll() throws SQLException;

    /**
     * Get a notification by its ID
     *
     * @param notificationId The ID of the notification to retrieve
     * @return The notification with the specified ID, or null if not found
     * @throws SQLException if a database error occurs
     */
    Notification findById(int notificationId) throws SQLException;

    /**
     * Get all notifications for a specific user
     *
     * @param userId The ID of the user
     * @return List of notifications for the specified user
     * @throws SQLException if a database error occurs
     */
    List<Notification> findByRecipient(int userId) throws SQLException;

    /**
     * Get all unread notifications for a specific user
     *
     * @param userId The ID of the user
     * @return List of unread notifications for the specified user
     * @throws SQLException if a database error occurs
     */
    List<Notification> findUnreadByRecipient(int userId) throws SQLException;

    /**
     * Save a new notification
     *
     * @param notification The notification to save
     * @return The saved notification with ID
     * @throws SQLException if a database error occurs
     */
    Notification save(Notification notification) throws SQLException;

    /**
     * Update an existing notification
     *
     * @param notification The notification to update
     * @return The updated notification
     * @throws SQLException if a database error occurs
     */
    Notification update(Notification notification) throws SQLException;

    /**
     * Delete a notification
     *
     * @param id The ID of the notification to delete
     * @return true if the notification was deleted, false otherwise
     * @throws SQLException if a database error occurs
     */
    boolean delete(int id) throws SQLException;

    /**
     * Get notifications by type
     *
     * @param type The notification type
     * @return List of notifications of the specified type
     * @throws SQLException if a database error occurs
     */
    List<Notification> findByType(NotificationType type) throws SQLException;

    /**
     * Get notifications for an event
     *
     * @param eventId The ID of the event
     * @return List of notifications for the specified event
     * @throws SQLException if a database error occurs
     */
    List<Notification> findByEvent(int eventId) throws SQLException;
}

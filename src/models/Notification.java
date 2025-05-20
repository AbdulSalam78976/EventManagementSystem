package models;

import java.time.LocalDateTime;

/**
 * Model class representing a notification in the system
 */
public class Notification {
    private int id;
    private User recipient;
    private String title;
    private String message;
    private NotificationType type;
    private Event event;
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private String actionUrl;

    /**
     * Enum representing the different types of notifications
     */
    public enum NotificationType {
        EVENT_CREATED,
        EVENT_UPDATED,
        EVENT_CANCELLED,
        REGISTRATION_APPROVED,
        REGISTRATION_REJECTED,
        EVENT_REMINDER,
        SYSTEM,
        ADMIN
    }

    // Default constructor
    public Notification() {
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }

    // Constructor with essential fields
    public Notification(User recipient, String title, String message, NotificationType type) {
        this();
        this.recipient = recipient;
        this.title = title;
        this.message = message;
        this.type = type;
    }

    // Constructor with event
    public Notification(User recipient, String title, String message, NotificationType type, Event event) {
        this(recipient, title, message, type);
        this.event = event;
    }

    // Full constructor
    public Notification(int id, User recipient, String title, String message, NotificationType type,
                       boolean isRead, LocalDateTime createdAt, LocalDateTime readAt, String actionUrl,
                       Event event) {
        this.id = id;
        this.recipient = recipient;
        this.title = title;
        this.message = message;
        this.type = type;
        this.isRead = isRead;
        this.createdAt = createdAt;
        this.readAt = readAt;
        this.actionUrl = actionUrl;
        this.event = event;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getRecipient() {
        return recipient;
    }

    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setActionUrl(String actionUrl) {
        this.actionUrl = actionUrl;
    }

    /**
     * Mark the notification as read
     */
    public void markAsRead() {
        this.isRead = true;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "id=" + id +
                ", recipient=" + recipient +
                ", title='" + title + '\'' +
                ", message='" + message + '\'' +
                ", type=" + type +
                ", event=" + event +
                ", isRead=" + isRead +
                ", createdAt=" + createdAt +
                '}';
    }
}

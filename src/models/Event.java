package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing an event in the system
 */
public class Event {
    private int id;
    private String name;
    private String description;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private String venueName;
    private int capacity;
    private int registeredCount;
    private User organizer;
    private String categoryName;
    private EventStatus status;
    private String requirements;
    private String mainImagePath;
    private String additionalDocumentPaths;
    private boolean featured;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Registration> registrations;
    private List<Feedback> feedbacks;

    /**
     * Enum representing the different statuses an event can have
     */
    public enum EventStatus {
        PENDING("Pending"),
        APPROVED("Approved"),
        REJECTED("Rejected"),
        CANCELLED("Cancelled"),
        COMPLETED("Completed");

        private final String displayName;

        EventStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static EventStatus fromString(String text) {
            for (EventStatus status : EventStatus.values()) {
                if (status.displayName.equalsIgnoreCase(text)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("No status with display name: " + text);
        }
    }

    // Default constructor
    public Event() {
        this.registrations = new ArrayList<>();
        this.feedbacks = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor with essential fields
    public Event(String name, String description, LocalDateTime startDateTime, 
                LocalDateTime endDateTime, String venueName, int capacity, User organizer, 
                String categoryName) {
        this();
        this.name = name;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.venueName = venueName;
        this.capacity = capacity;
        this.organizer = organizer;
        this.categoryName = categoryName;
        this.status = organizer.getRole() == User.UserRole.ADMIN ? 
                      EventStatus.APPROVED : EventStatus.PENDING;
    }

    // Full constructor
    public Event(int id, String name, String description, LocalDateTime startDateTime,
                LocalDateTime endDateTime, String venueName, int capacity, int registeredCount,
                User organizer, String categoryName, EventStatus status, String requirements,
                String mainImagePath, String additionalDocumentPaths, boolean featured, LocalDateTime createdAt,
                LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.venueName = venueName;
        this.capacity = capacity;
        this.registeredCount = registeredCount;
        this.organizer = organizer;
        this.categoryName = categoryName;
        this.status = status;
        this.requirements = requirements;
        this.mainImagePath = mainImagePath;
        this.additionalDocumentPaths = additionalDocumentPaths;
        this.featured = featured;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.registrations = new ArrayList<>();
        this.feedbacks = new ArrayList<>();
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public LocalDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(LocalDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getRegisteredCount() {
        return registeredCount;
    }

    public void setRegisteredCount(int registeredCount) {
        this.registeredCount = registeredCount;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    public String getMainImagePath() {
        return mainImagePath;
    }

    public void setMainImagePath(String mainImagePath) {
        this.mainImagePath = mainImagePath;
    }

    public String getAdditionalDocumentPaths() {
        return additionalDocumentPaths;
    }

    public void setAdditionalDocumentPaths(String additionalDocumentPaths) {
        this.additionalDocumentPaths = additionalDocumentPaths;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Registration> getRegistrations() {
        return registrations;
    }

    public void setRegistrations(List<Registration> registrations) {
        this.registrations = registrations;
        this.registeredCount = registrations.size();
    }

    public void addRegistration(Registration registration) {
        this.registrations.add(registration);
        this.registeredCount = this.registrations.size();
    }

    public List<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(List<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    public void addFeedback(Feedback feedback) {
        this.feedbacks.add(feedback);
    }

    public boolean isAvailable() {
        return status == EventStatus.APPROVED && registeredCount < capacity;
    }

    public boolean isUpcoming() {
        return startDateTime.isAfter(LocalDateTime.now());
    }

    public boolean isOngoing() {
        LocalDateTime now = LocalDateTime.now();
        return startDateTime.isBefore(now) && endDateTime.isAfter(now);
    }

    public boolean isPast() {
        return endDateTime.isBefore(LocalDateTime.now());
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", startDateTime=" + startDateTime +
                ", venueName='" + venueName + '\'' +
                ", status=" + status +
                '}';
    }
}

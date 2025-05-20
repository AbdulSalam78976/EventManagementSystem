package models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class representing an event in the system
 */
public class Event {
    private int id;
    private String title;
    private String description;
    private String category;
    private String venueName;
    private User organizer;
    private LocalDateTime eventDate;
    private LocalDateTime registrationDeadline;
    private int totalSlots;
    private int availableSlots;
    private String eligibilityCriteria;
    private String contactInfo;
    private EventStatus status;
    private byte[] mainImage;
    private String mainImageType;
    private byte[] additionalDocuments;
    private String additionalDocumentsType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Registration> registrations;
    private List<Feedback> feedbacks;

    /**
     * Enum representing the different statuses an event can have
     */
    public enum EventStatus {
        DRAFT("Draft"),
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
        this.status = EventStatus.DRAFT;
    }

    // Constructor with essential fields
    public Event(String title, String description, LocalDateTime eventDate, 
                LocalDateTime registrationDeadline, String venueName,
                int totalSlots, User organizer, String category) {
        this();
        this.title = title;
        this.description = description;
        this.eventDate = eventDate;
        this.registrationDeadline = registrationDeadline;
        this.venueName = venueName;
        this.totalSlots = totalSlots;
        this.availableSlots = totalSlots;
        this.organizer = organizer;
        this.category = category;
        
        // Set status based on organizer's role
        if (organizer.getRole() == User.UserRole.ADMIN) {
            this.status = EventStatus.APPROVED;
        } else {
            this.status = EventStatus.PENDING;
        }
        
        // Set contact info from organizer's email
        this.contactInfo = organizer.getEmail();
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public LocalDateTime getRegistrationDeadline() {
        return registrationDeadline;
    }

    public void setRegistrationDeadline(LocalDateTime registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }

    public int getTotalSlots() {
        return totalSlots;
    }

    public void setTotalSlots(int totalSlots) {
        this.totalSlots = totalSlots;
    }

    public int getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(int availableSlots) {
        this.availableSlots = availableSlots;
    }

    public String getEligibilityCriteria() {
        return eligibilityCriteria;
    }

    public void setEligibilityCriteria(String eligibilityCriteria) {
        this.eligibilityCriteria = eligibilityCriteria;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public byte[] getMainImage() {
        return mainImage;
    }

    public void setMainImage(byte[] mainImage) {
        this.mainImage = mainImage;
    }

    public String getMainImageType() {
        return mainImageType;
    }

    public void setMainImageType(String mainImageType) {
        this.mainImageType = mainImageType;
    }

    public byte[] getAdditionalDocuments() {
        return additionalDocuments;
    }

    public void setAdditionalDocuments(byte[] additionalDocuments) {
        this.additionalDocuments = additionalDocuments;
    }

    public String getAdditionalDocumentsType() {
        return additionalDocumentsType;
    }

    public void setAdditionalDocumentsType(String additionalDocumentsType) {
        this.additionalDocumentsType = additionalDocumentsType;
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
    }

    public void addRegistration(Registration registration) {
        this.registrations.add(registration);
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

    public int getRegisteredUsers() {
        return registrations != null ? registrations.size() : totalSlots - availableSlots;
    }

    public boolean isAvailable() {
        return availableSlots > 0 && 
               LocalDateTime.now().isBefore(registrationDeadline) &&
               status == EventStatus.APPROVED;
    }

    public boolean isUpcoming() {
        return eventDate.isAfter(LocalDateTime.now());
    }

    public boolean isOngoing() {
        LocalDateTime now = LocalDateTime.now();
        return !eventDate.isAfter(now) && 
               eventDate.plusHours(4).isAfter(now); // Assuming events last 4 hours
    }

    public boolean isPast() {
        return eventDate.plusHours(4).isBefore(LocalDateTime.now());
    }

    @Override
    public String toString() {
        return title;
    }
}

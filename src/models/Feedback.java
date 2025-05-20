package models;

import java.time.LocalDateTime;

/**
 * Model class representing feedback for an event
 */
public class Feedback {
    private int id;
    private User user;
    private Event event;
    private int rating;
    private String comment;
    private LocalDateTime submittedAt;
    private boolean anonymous;

    // Default constructor
    public Feedback() {
        this.submittedAt = LocalDateTime.now();
        this.anonymous = false;
    }

    // Constructor with essential fields
    public Feedback(User user, Event event, int rating, String comment) {
        this();
        this.user = user;
        this.event = event;
        this.rating = rating;
        this.comment = comment;
    }

    // Full constructor
    public Feedback(int id, User user, Event event, int rating, String comment,
                   LocalDateTime submittedAt, boolean anonymous) {
        this.id = id;
        this.user = user;
        this.event = event;
        this.rating = rating;
        this.comment = comment;
        this.submittedAt = submittedAt;
        this.anonymous = anonymous;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    /**
     * Get the name of the user who submitted the feedback
     * If the feedback is anonymous, returns "Anonymous"
     * 
     * @return The name of the user or "Anonymous"
     */
    public String getUserName() {
        if (anonymous) {
            return "Anonymous";
        }
        return user.getName();
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "id=" + id +
                ", user=" + getUserName() +
                ", event=" + event.getTitle() +
                ", rating=" + rating +
                ", submittedAt=" + submittedAt +
                '}';
    }
}

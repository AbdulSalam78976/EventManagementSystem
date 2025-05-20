package models;

import java.time.LocalDateTime;

/**
 * Model class representing a registration for an event
 */
public class Registration {
    private int id;
    private Event event;
    private User attendee;
    private LocalDateTime registrationDate;
    private Status status;
    private boolean checkedIn;
    
    /**
     * Enum representing the different statuses a registration can have
     */
    public enum Status {
        PENDING,
        APPROVED,
        REJECTED,
        CANCELLED,
        REGISTERED,
        WAITLISTED,
        ATTENDED,
        NO_SHOW
    }

    // Default constructor
    public Registration() {
        this.registrationDate = LocalDateTime.now();
        this.status = Status.PENDING;
        this.checkedIn = false;
    }

    // Constructor with user and event
    public Registration(User attendee, Event event) {
        this();
        this.attendee = attendee;
        this.event = event;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public Event getEvent() {
        return event;
    }
    
    public void setEvent(Event event) {
        this.event = event;
    }
    
    public User getAttendee() {
        return attendee;
    }
    
    public void setAttendee(User attendee) {
        this.attendee = attendee;
    }
    
    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(boolean checkedIn) {
        this.checkedIn = checkedIn;
    }
    
    @Override
    public String toString() {
        return "Registration{" +
                "id=" + id +
                ", event=" + event.getTitle() +
                ", attendee=" + attendee.getName() +
                ", registrationDate=" + registrationDate +
                ", status=" + status +
                ", checkedIn=" + checkedIn +
                '}';
    }
}

package controllers;

import models.Event;
import models.User;
import models.Event.EventStatus;
import dao.EventDAO;
import dao.SQLEventDAO;
import dao.UserDAO;
import dao.SQLUserDAO;
import utils.ValidationUtils;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller class for handling event-related operations
 */
public class EventController {

    private static EventController instance;
    private final EventDAO eventDAO;
    private final UserDAO userDAO;

    // Private constructor for singleton pattern
    private EventController() throws SQLException {
        this.eventDAO = new SQLEventDAO();
        this.userDAO = new SQLUserDAO();
    }

    /**
     * Get the singleton instance of the EventController
     *
     * @return The EventController instance
     * @throws SQLException if a database error occurs during initialization
     */
    public static synchronized EventController getInstance() throws SQLException {
        if (instance == null) {
            instance = new EventController();
        }
        return instance;
    }

    /**
     * Create a new event
     *
     * @param title Event title
     * @param description Event description
     * @param eventDate Event date and time
     * @param registrationDeadline Registration deadline
     * @param venueName Event venue name
     * @param totalSlots Maximum capacity
     * @param organizer Event organizer
     * @param category Event category
     * @param mainImage Main event image data
     * @param mainImageType MIME type of the image
     * @param additionalDocuments Additional event documents data
     * @param additionalDocumentsType MIME type of the additional documents
     * @return The created event
     * @throws IllegalArgumentException if the input is invalid
     */
    public Event createEvent(String title, String description, LocalDateTime eventDate,
                           LocalDateTime registrationDeadline, String venueName,
                           int totalSlots, User organizer, String category,
                           byte[] mainImage, String mainImageType,
                           byte[] additionalDocuments, String additionalDocumentsType) throws SQLException {
        // Validate input
        if (!ValidationUtils.isNotEmpty(title)) {
            throw new IllegalArgumentException("Event title is required");
        }
        if (eventDate == null) {
            throw new IllegalArgumentException("Event date is required");
        }
        if (registrationDeadline == null) {
            throw new IllegalArgumentException("Registration deadline is required");
        }
        if (eventDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Event date cannot be in the past");
        }
        if (registrationDeadline.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Registration deadline cannot be in the past");
        }
        if (registrationDeadline.isAfter(eventDate)) {
            throw new IllegalArgumentException("Registration deadline must be before event date");
        }
        if (registrationDeadline.isBefore(LocalDateTime.now().plusHours(1))) {
            throw new IllegalArgumentException("Registration deadline must be at least 1 hour in the future");
        }
        if (!ValidationUtils.isNotEmpty(venueName)) {
            throw new IllegalArgumentException("Event venue is required");
        }
        if (totalSlots <= 0) {
            throw new IllegalArgumentException("Total slots must be greater than 0");
        }
        if (organizer == null) {
            throw new IllegalArgumentException("Event organizer is required");
        }
        if (!ValidationUtils.isNotEmpty(category)) {
            throw new IllegalArgumentException("Event category is required");
        }

        // Create event
        Event event = new Event(title, description, eventDate, registrationDeadline,
                              venueName, totalSlots, organizer, category);

        // Set additional fields
        event.setMainImage(mainImage);
        event.setMainImageType(mainImageType);
        event.setAdditionalDocuments(additionalDocuments);
        event.setAdditionalDocumentsType(additionalDocumentsType);

        return eventDAO.save(event);
    }

    /**
     * Update an existing event
     *
     * @param id Event ID
     * @param title Event title
     * @param description Event description
     * @param eventDate Event date and time
     * @param registrationDeadline Registration deadline
     * @param venueName Event venue name
     * @param totalSlots Maximum capacity
     * @param organizer Event organizer
     * @param category Event category
     * @param mainImage Main event image data
     * @param mainImageType MIME type of the image
     * @param additionalDocuments Additional event documents data
     * @param additionalDocumentsType MIME type of the additional documents
     * @return The updated event
     * @throws IllegalArgumentException if the input is invalid
     */
    public Event updateEvent(int id, String title, String description, LocalDateTime eventDate,
                           LocalDateTime registrationDeadline, String venueName,
                           int totalSlots, User organizer, String category,
                           byte[] mainImage, String mainImageType,
                           byte[] additionalDocuments, String additionalDocumentsType) throws SQLException {
        // Validate input
        if (!ValidationUtils.isNotEmpty(title)) {
            throw new IllegalArgumentException("Event title is required");
        }
        if (eventDate == null) {
            throw new IllegalArgumentException("Event date is required");
        }
        if (registrationDeadline == null) {
            throw new IllegalArgumentException("Registration deadline is required");
        }
        if (eventDate.isBefore(registrationDeadline)) {
            throw new IllegalArgumentException("Event date must be after registration deadline");
        }
        if (!ValidationUtils.isNotEmpty(venueName)) {
            throw new IllegalArgumentException("Event venue is required");
        }
        if (totalSlots <= 0) {
            throw new IllegalArgumentException("Total slots must be greater than 0");
        }
        if (organizer == null) {
            throw new IllegalArgumentException("Event organizer is required");
        }
        if (!ValidationUtils.isNotEmpty(category)) {
            throw new IllegalArgumentException("Event category is required");
        }

        // Get existing event
        Event event = eventDAO.findById(id);
        if (event == null) {
            throw new IllegalArgumentException("Event not found");
        }

        // Update event
        event.setTitle(title);
        event.setDescription(description);
        event.setEventDate(eventDate);
        event.setRegistrationDeadline(registrationDeadline);
        event.setVenueName(venueName);
        event.setTotalSlots(totalSlots);
        event.setOrganizer(organizer);
        event.setCategory(category);
        event.setMainImage(mainImage);
        event.setMainImageType(mainImageType);
        event.setAdditionalDocuments(additionalDocuments);
        event.setAdditionalDocumentsType(additionalDocumentsType);

        return eventDAO.update(event);
    }

    /**
     * Delete an event
     *
     * @param id The ID of the event to delete
     * @return true if the event was deleted, false otherwise
     */
    public boolean deleteEvent(int id) throws SQLException {
        return eventDAO.delete(id);
    }

    /**
     * Get an event by ID
     *
     * @param id The ID of the event to get
     * @return The event, or null if not found
     */
    public Event getEvent(int id) throws SQLException {
        return eventDAO.findById(id);
    }

    /**
     * Get all events
     *
     * @return A list of all events
     */
    public List<Event> getAllEvents() throws SQLException {
        return eventDAO.findAll();
    }

    /**
     * Get events by category
     *
     * @param category Category name
     * @return List of events in the specified category
     */
    public List<Event> getEventsByCategory(String category) throws SQLException {
        return eventDAO.findAll().stream()
                .filter(event -> event.getCategory().equals(category))
            .collect(Collectors.toList());
    }

    /**
     * Get events by venue name
     *
     * @param venueName The name of the venue
     * @return A list of events at the specified venue
     */
    public List<Event> getEventsByVenue(String venueName) throws SQLException {
        if (!ValidationUtils.isNotEmpty(venueName)) {
            throw new IllegalArgumentException("Venue name cannot be empty");
        }
        return eventDAO.findAll().stream()
                .filter(event -> event.getVenueName().equalsIgnoreCase(venueName.trim()))
            .collect(Collectors.toList());
    }

    /**
     * Get events by organizer
     *
     * @param organizerId Organizer ID
     * @return List of events organized by the specified user
     */
    public List<Event> getEventsByOrganizer(int organizerId) throws SQLException {
        return eventDAO.findAll().stream()
                .filter(event -> event.getOrganizer() != null && event.getOrganizer().getId() == organizerId)
                .collect(Collectors.toList());
    }

    /**
     * Get a specified number of most recent events by organizer.
     *
     * @param organizerId Organizer ID
     * @param limit The maximum number of recent events to retrieve
     * @return List of recent events organized by the specified user
     */
    public List<Event> getRecentEventsByOrganizer(int organizerId, int limit) throws SQLException {
        return eventDAO.findAll().stream()
                .filter(event -> event.getOrganizer() != null && event.getOrganizer().getId() == organizerId)
                .sorted((e1, e2) -> e2.getEventDate().compareTo(e1.getEventDate())) // Sort by date descending
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Get events by status
     *
     * @param status The status to filter by
     * @return A list of events with the specified status
     */
    public List<Event> getEventsByStatus(EventStatus status) throws SQLException {
        return eventDAO.findByStatus(status);
    }

    /**
     * Get upcoming events
     *
     * @return A list of upcoming events
     */
    public List<Event> getUpcomingEvents() throws SQLException {
        return eventDAO.findUpcoming();
    }

    /**
     * Get past events
     *
     * @return A list of past events
     */
    public List<Event> getPastEvents() throws SQLException {
        return eventDAO.findPast();
    }

    /**
     * Approve an event
     *
     * @param eventId The ID of the event to approve
     * @return The approved event
     */
    public Event approveEvent(int eventId) throws SQLException {
        Event event = eventDAO.findById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found");
        }
        if (event.getStatus() != EventStatus.PENDING) {
            throw new IllegalStateException("Only pending events can be approved");
        }
        event.setStatus(EventStatus.APPROVED);
        return eventDAO.update(event);
    }

    /**
     * Reject an event
     *
     * @param eventId The ID of the event to reject
     * @return The rejected event
     */
    public Event rejectEvent(int eventId) throws SQLException {
        Event event = eventDAO.findById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found");
        }
        if (event.getStatus() != EventStatus.PENDING) {
            throw new IllegalStateException("Only pending events can be rejected");
        }
        event.setStatus(EventStatus.REJECTED);
        return eventDAO.update(event);
    }

    /**
     * Cancel an event
     *
     * @param eventId The ID of the event to cancel
     * @return The cancelled event
     */
    public Event cancelEvent(int eventId) throws SQLException {
        Event event = eventDAO.findById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found");
        }
        if (event.getStatus() == EventStatus.CANCELLED || event.getStatus() == EventStatus.COMPLETED) {
            throw new IllegalStateException("Event is already cancelled or completed");
        }
        event.setStatus(EventStatus.CANCELLED);
        return eventDAO.update(event);
    }

    /**
     * Search events by title or description
     *
     * @param query The search query
     * @return A list of matching events
     */
    public List<Event> searchEvents(String query) throws SQLException {
        return eventDAO.search(query);
    }

    /**
     * Get total number of events
     *
     * @return The total number of events
     */
    public int getTotalEvents() throws SQLException {
        return eventDAO.findAll().size();
    }

    /**
     * Get active events (approved and upcoming)
     *
     * @return A list of active events
     */
    public List<Event> getActiveEvents() throws SQLException {
        return eventDAO.findAll().stream()
                .filter(event -> event.getStatus() == EventStatus.APPROVED && event.isUpcoming())
                .collect(Collectors.toList());
    }

    /**
     * Get total number of registrations across all events
     *
     * @return The total number of registrations
     */
    public int getTotalRegistrations() throws SQLException {
        return eventDAO.getTotalRegistrations();
    }

    /**
     * Get today's events
     *
     * @return A list of today's events
     */
    public List<Event> getTodaysEvents() throws SQLException {
        return eventDAO.findToday();
    }

    /**
     * Update event status
     *
     * @param event The event to update
     * @return The updated event
     * @throws SQLException if a database error occurs
     */
    public Event updateEvent(Event event) throws SQLException {
        if (event == null) {
            throw new IllegalArgumentException("Event cannot be null");
        }

        Event existingEvent = eventDAO.findById(event.getId());
        if (existingEvent == null) {
            throw new IllegalArgumentException("Event not found");
        }

        // Preserve the original organizer
        event.setOrganizer(existingEvent.getOrganizer());

        return eventDAO.update(event);
    }
}

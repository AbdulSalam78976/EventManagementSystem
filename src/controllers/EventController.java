package controllers;

import models.Event;
import models.User;
import models.Venue;
import models.Category;
import models.Event.EventStatus;
import dao.EventDAO;
import dao.SQLEventDAO;
import dao.UserDAO;
import dao.SQLUserDAO;
import dao.CategoryDAO;
import dao.SQLCategoryDAO;
import dao.VenueDAO;
import dao.SQLVenueDAO;
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
    private final CategoryDAO categoryDAO;
    private final VenueDAO venueDAO;
    
    // Private constructor for singleton pattern
    private EventController() throws SQLException {
        this.eventDAO = new SQLEventDAO();
        this.userDAO = new SQLUserDAO();
        this.categoryDAO = new SQLCategoryDAO();
        this.venueDAO = new SQLVenueDAO();
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
     * @param name Event name
     * @param description Event description
     * @param startDateTime Event start date and time
     * @param endDateTime Event end date and time
     * @param venue Event venue
     * @param capacity Maximum capacity
     * @param category Event category
     * @param mainImagePath Path to the main event image
     * @param additionalDocumentPaths Paths to additional event documents (comma-separated)
     * @return The created event
     * @throws IllegalArgumentException if the input is invalid
     */
    public Event createEvent(String name, String description, LocalDateTime startDateTime,
                           LocalDateTime endDateTime, Venue venue, int capacity, 
                           String categoryName, String mainImagePath, String additionalDocumentPaths) throws SQLException {
        // Validate input
        if (!ValidationUtils.isNotEmpty(name)) {
            throw new IllegalArgumentException("Event name is required");
        }
        if (startDateTime == null) {
            throw new IllegalArgumentException("Start date and time is required");
        }
        if (endDateTime == null) {
            throw new IllegalArgumentException("End date and time is required");
        }
        if (startDateTime.isAfter(endDateTime)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        if (venue == null) {
            throw new IllegalArgumentException("Event venue is required");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        if (!ValidationUtils.isNotEmpty(categoryName)) {
            throw new IllegalArgumentException("Event category is required");
        }
        
        // Create event
        Event event = new Event(name, description, startDateTime, endDateTime, 
                              venue.getName(), capacity, null, categoryName);
        event.setStatus(EventStatus.PENDING);
        event.setMainImagePath(mainImagePath);
        event.setAdditionalDocumentPaths(additionalDocumentPaths);
        
        return eventDAO.save(event);
    }
    
    /**
     * Update an existing event
     * 
     * @param id Event ID
     * @param name Event name
     * @param description Event description
     * @param startDateTime Event start date and time
     * @param endDateTime Event end date and time
     * @param venue Event venue
     * @param capacity Maximum capacity
     * @param category Event category
     * @param mainImagePath Path to the main event image
     * @param additionalDocumentPaths Paths to additional event documents (comma-separated)
     * @return The updated event
     * @throws IllegalArgumentException if the input is invalid
     */
    public Event updateEvent(int id, String name, String description, LocalDateTime startDateTime,
                           LocalDateTime endDateTime, Venue venue, int capacity, 
                           String categoryName, String mainImagePath, String additionalDocumentPaths) throws SQLException {
        // Validate input
        if (!ValidationUtils.isNotEmpty(name)) {
            throw new IllegalArgumentException("Event name is required");
        }
        if (startDateTime == null) {
            throw new IllegalArgumentException("Start date and time is required");
        }
        if (endDateTime == null) {
            throw new IllegalArgumentException("End date and time is required");
        }
        if (startDateTime.isAfter(endDateTime)) {
            throw new IllegalArgumentException("Start date must be before end date");
        }
        if (venue == null) {
            throw new IllegalArgumentException("Event venue is required");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        if (!ValidationUtils.isNotEmpty(categoryName)) {
            throw new IllegalArgumentException("Event category is required");
        }
        
        // Get existing event
        Event event = eventDAO.findById(id);
        if (event == null) {
            throw new IllegalArgumentException("Event not found");
        }
        
        // Update event
        event.setName(name);
        event.setDescription(description);
        event.setStartDateTime(startDateTime);
        event.setEndDateTime(endDateTime);
        event.setVenue(venue);
        event.setCapacity(capacity);
        event.setCategoryName(categoryName);
        event.setMainImagePath(mainImagePath);
        event.setAdditionalDocumentPaths(additionalDocumentPaths);
        
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
     * @param categoryName The name of the category
     * @return A list of events with the specified category name
     * @throws SQLException if a database error occurs
     */
    public List<Event> getEventsByCategory(String categoryName) throws SQLException {
        // Assuming EventDAO has a findByCategoryName method or filter all events
        // For simplicity, filtering all events for now.
        List<Event> allEvents = eventDAO.findAll();
        return allEvents.stream()
            .filter(event -> event.getCategoryName() != null && event.getCategoryName().equalsIgnoreCase(categoryName))
            .collect(Collectors.toList());
    }
    
    /**
     * Get events by venue name.
     * @param venueName The name of the venue.
     * @return A list of events at the specified venue name.
     * @throws SQLException if a database error occurs.
     * @throws IllegalArgumentException if the venue name is empty.
     */
    public List<Event> getEventsByVenue(String venueName) throws SQLException {
        if (!ValidationUtils.isNotEmpty(venueName)) {
            throw new IllegalArgumentException("Venue name cannot be empty.");
        }
        // Assuming EventDAO has a findByVenueName method or filter all events
        // For simplicity, filtering all events for now.
        List<Event> allEvents = eventDAO.findAll();
        return allEvents.stream()
            .filter(event -> event.getVenueName() != null && event.getVenueName().equalsIgnoreCase(venueName.trim()))
            .collect(Collectors.toList());
    }
    
    /**
     * Get events by organizer
     * 
     * @param organizerId The ID of the organizer
     * @return A list of events organized by the specified user
     * @throws SQLException if a database error occurs
     */
    public List<Event> getEventsByOrganizer(int organizerId) throws SQLException {
        return eventDAO.findByOrganizer(organizerId);
    }
    
    /**
     * Get events by status
     * 
     * @param status The status to filter by
     * @return A list of events with the specified status
     * @throws SQLException if a database error occurs
     */
    public List<Event> getEventsByStatus(EventStatus status) throws SQLException {
        return eventDAO.findByStatus(status);
    }
    
    /**
     * Get upcoming events
     * 
     * @return A list of upcoming events
     * @throws SQLException if a database error occurs
     */
    public List<Event> getUpcomingEvents() throws SQLException {
        return eventDAO.findUpcoming();
    }
    
    /**
     * Get past events
     * 
     * @return A list of past events
     * @throws SQLException if a database error occurs
     */
    public List<Event> getPastEvents() throws SQLException {
        return eventDAO.findPast();
    }
    
    /**
     * Approve an event
     * 
     * @param eventId The ID of the event to approve
     * @return The approved event
     * @throws SQLException if a database error occurs
     */
    public Event approveEvent(int eventId) throws SQLException {
        Event event = eventDAO.findById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found");
        }
        
        event.setStatus(EventStatus.APPROVED);
        return eventDAO.update(event);
    }
    
    /**
     * Reject an event
     * 
     * @param eventId The ID of the event to reject
     * @return The rejected event
     * @throws SQLException if a database error occurs
     */
    public Event rejectEvent(int eventId) throws SQLException {
        Event event = eventDAO.findById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found");
        }
        
        event.setStatus(EventStatus.REJECTED);
        return eventDAO.update(event);
    }
    
    /**
     * Cancel an event
     * 
     * @param eventId The ID of the event to cancel
     * @return The cancelled event
     * @throws SQLException if a database error occurs
     */
    public Event cancelEvent(int eventId) throws SQLException {
        Event event = eventDAO.findById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found");
        }
        
        event.setStatus(EventStatus.CANCELLED);
        return eventDAO.update(event);
    }
    
    /**
     * Search for events by name
     * 
     * @param query The search query
     * @return A list of events matching the query
     */
    public List<Event> searchEvents(String query) throws SQLException {
        return eventDAO.search(query);
    }
}

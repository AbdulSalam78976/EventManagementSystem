package dao;

import models.Event;
import models.Event.EventStatus;
import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object interface for Event entities
 */
public interface EventDAO {
    
    /**
     * Find an event by its ID
     * 
     * @param id The event ID to search for
     * @return The event with the given ID, or null if not found
     * @throws SQLException if a database error occurs
     */
    Event findById(int id) throws SQLException;
    
    /**
     * Get all events in the system
     * 
     * @return A list of all events
     * @throws SQLException if a database error occurs
     */
    List<Event> findAll() throws SQLException;
    
    /**
     * Find events by organizer
     * 
     * @param organizerId The ID of the organizer
     * @return A list of events organized by the specified user
     * @throws SQLException if a database error occurs
     */
    List<Event> findByOrganizer(int organizerId) throws SQLException;
    
    /**
     * Find events by status
     * 
     * @param status The status to filter by
     * @return A list of events with the specified status
     * @throws SQLException if a database error occurs
     */
    List<Event> findByStatus(EventStatus status) throws SQLException;
    
    /**
     * Find upcoming events (events that haven't started yet)
     * 
     * @return A list of upcoming events
     * @throws SQLException if a database error occurs
     */
    List<Event> findUpcoming() throws SQLException;
    
    /**
     * Find past events (events that have ended)
     * 
     * @return A list of past events
     * @throws SQLException if a database error occurs
     */
    List<Event> findPast() throws SQLException;
    
    /**
     * Find events by category
     * 
     * @param categoryId The ID of the category
     * @return A list of events in the specified category
     * @throws SQLException if a database error occurs
     */
    List<Event> findByCategory(int categoryId) throws SQLException;
    
    /**
     * Find events by venue
     * 
     * @param venueId The ID of the venue
     * @return A list of events at the specified venue
     * @throws SQLException if a database error occurs
     */
    List<Event> findByVenue(int venueId) throws SQLException;
    
    /**
     * Search for events by name or description
     * 
     * @param query The search query
     * @return A list of events matching the query
     * @throws SQLException if a database error occurs
     */
    List<Event> search(String query) throws SQLException;
    
    /**
     * Save a new event to the system
     * 
     * @param event The event to save
     * @return The saved event with ID populated
     * @throws SQLException if a database error occurs
     */
    Event save(Event event) throws SQLException;
    
    /**
     * Update an existing event
     * 
     * @param event The event to update
     * @return The updated event
     * @throws SQLException if a database error occurs
     */
    Event update(Event event) throws SQLException;
    
    /**
     * Delete an event by its ID
     * 
     * @param id The ID of the event to delete
     * @return true if the event was deleted, false otherwise
     * @throws SQLException if a database error occurs
     */
    boolean delete(int id) throws SQLException;
    
    /**
     * Find featured events
     * 
     * @return A list of featured events
     * @throws SQLException if a database error occurs
     */
    List<Event> findFeatured() throws SQLException;
    
    /**
     * Find events happening today
     * 
     * @return A list of events happening today
     * @throws SQLException if a database error occurs
     */
    List<Event> findToday() throws SQLException;
}

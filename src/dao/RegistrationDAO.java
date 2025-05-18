package dao;

import models.Registration;
import models.Registration.Status;
import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object interface for Registration entities
 */
public interface RegistrationDAO {
    
    /**
     * Find a registration by its ID
     * 
     * @param id The registration ID to search for
     * @return The registration with the given ID, or null if not found
     * @throws SQLException if a database error occurs
     */
    Registration findById(int id) throws SQLException;
    
    /**
     * Get all registrations in the system
     * 
     * @return A list of all registrations
     * @throws SQLException if a database error occurs
     */
    List<Registration> findAll() throws SQLException;
    
    /**
     * Find registrations by user
     * 
     * @param userId The ID of the user
     * @return A list of registrations for the specified user
     * @throws SQLException if a database error occurs
     */
    List<Registration> findByUser(int userId) throws SQLException;
    
    /**
     * Find registrations by event
     * 
     * @param eventId The ID of the event
     * @return A list of registrations for the specified event
     * @throws SQLException if a database error occurs
     */
    List<Registration> findByEvent(int eventId) throws SQLException;
    
    /**
     * Find registrations by status
     * 
     * @param status The status to filter by
     * @return A list of registrations with the specified status
     * @throws SQLException if a database error occurs
     */
    List<Registration> findByStatus(Status status) throws SQLException;
    
    /**
     * Find registrations by user and event
     * 
     * @param userId The ID of the user
     * @param eventId The ID of the event
     * @return A list of registrations for the specified user and event
     * @throws SQLException if a database error occurs
     */
    List<Registration> findByUserAndEvent(int userId, int eventId) throws SQLException;
    
    /**
     * Check if a registration exists for a user and event
     * 
     * @param userId The ID of the user
     * @param eventId The ID of the event
     * @return true if a registration exists, false otherwise
     * @throws SQLException if a database error occurs
     */
    boolean existsByUserAndEvent(int userId, int eventId) throws SQLException;
    
    /**
     * Find the oldest waitlisted registration for an event
     * 
     * @param eventId The ID of the event
     * @return The oldest waitlisted registration, or null if none exists
     * @throws SQLException if a database error occurs
     */
    Registration findOldestWaitlisted(int eventId) throws SQLException;
    
    /**
     * Save a new registration to the system
     * 
     * @param registration The registration to save
     * @return The saved registration with ID populated
     * @throws SQLException if a database error occurs
     */
    Registration save(Registration registration) throws SQLException;
    
    /**
     * Update an existing registration
     * 
     * @param registration The registration to update
     * @return The updated registration
     * @throws SQLException if a database error occurs
     */
    Registration update(Registration registration) throws SQLException;
    
    /**
     * Delete a registration by its ID
     * 
     * @param id The ID of the registration to delete
     * @return true if the registration was deleted, false otherwise
     * @throws SQLException if a database error occurs
     */
    boolean delete(int id) throws SQLException;
    
    /**
     * Count registrations by event
     * 
     * @param eventId The ID of the event
     * @return The number of registrations for the specified event
     * @throws SQLException if a database error occurs
     */
    int countByEvent(int eventId) throws SQLException;
    
    /**
     * Count registrations by event and status
     * 
     * @param eventId The ID of the event
     * @param status The status to filter by
     * @return The number of registrations for the specified event with the specified status
     * @throws SQLException if a database error occurs
     */
    int countByEventAndStatus(int eventId, Status status) throws SQLException;
}

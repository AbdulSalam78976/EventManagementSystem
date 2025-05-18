package controllers;

import models.Registration;
import models.Event;
import models.User;
import models.Registration.Status;
import dao.RegistrationDAO;
import dao.EventDAO;
import dao.SQLEventDAO;
import dao.SQLEventRegistrationDAO;
import dao.UserDAO;
import dao.SQLUserDAO;

import java.time.LocalDateTime;
import java.util.List;
import java.sql.SQLException;

/**
 * Controller class for handling registration-related operations
 */
public class RegistrationController {
    
    private static RegistrationController instance;
    private final SQLEventRegistrationDAO registrationDAO;
    private final EventDAO eventDAO;
    private final UserDAO userDAO;
    
    // Private constructor for singleton pattern
    private RegistrationController() throws SQLException {
        this.registrationDAO = new SQLEventRegistrationDAO();
        this.eventDAO = new SQLEventDAO();
        this.userDAO = new SQLUserDAO();
    }
    
    /**
     * Get the singleton instance of the RegistrationController
     * 
     * @return The RegistrationController instance
     * @throws SQLException if a database error occurs during initialization
     */
    public static synchronized RegistrationController getInstance() throws SQLException {
        if (instance == null) {
            instance = new RegistrationController();
        }
        return instance;
    }
    
    /**
     * Register a user for an event
     * 
     * @param user The user to register
     * @param eventId The ID of the event to register for
     * @return The created registration
     * @throws SQLException if a database error occurs
     * @throws IllegalArgumentException if user is null or event is not found
     * @throws IllegalStateException if event is not available or user is already registered
     */
    public Registration registerForEvent(User user, int eventId) throws SQLException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        // Get the event
        Event event = eventDAO.findById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found");
        }
        
        // Check if the event is available for registration
        if (!event.isAvailable()) {
            throw new IllegalStateException("Event is not available for registration");
        }
        
        // Check if the user is already registered
        if (registrationDAO.existsByUserAndEvent(user.getId(), eventId)) {
            throw new IllegalStateException("User is already registered for this event");
        }
        
        // Create the registration
        Registration registration = new Registration(user, event);
        
        // Check if the event is at capacity
        if (event.getRegisteredCount() >= event.getCapacity()) {
            registration.setStatus(Status.WAITLISTED);
        } else {
            registration.setStatus(Status.REGISTERED);
            
            // Update the event's registered count
            event.setRegisteredCount(event.getRegisteredCount() + 1);
            eventDAO.update(event);
        }
        
        // Save the registration
        return registrationDAO.save(registration);
    }
    
    /**
     * Register a user for an event by user ID
     * 
     * @param eventId The ID of the event
     * @param userId The ID of the user
     * @return The created registration
     * @throws SQLException if a database error occurs
     * @throws IllegalArgumentException if user or event is not found
     * @throws IllegalStateException if event is not available or user is already registered
     */
    public Registration registerUser(int eventId, int userId) throws SQLException {
        // Get the user
        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        return registerForEvent(user, eventId);
    }
    
    /**
     * Cancel a registration
     * 
     * @param registrationId The ID of the registration to cancel
     * @return The cancelled registration
     * @throws SQLException if a database error occurs
     * @throws IllegalArgumentException if registration is not found
     * @throws IllegalStateException if registration is already cancelled
     */
    public Registration cancelRegistration(int registrationId) throws SQLException {
        Registration registration = registrationDAO.findById(registrationId);
        if (registration == null) {
            throw new IllegalArgumentException("Registration not found");
        }
        
        // Check if the registration is already cancelled
        if (registration.getStatus() == Status.CANCELLED) {
            throw new IllegalStateException("Registration is already cancelled");
        }
        
        // Cancel the registration
        registration.setStatus(Status.CANCELLED);
        
        // Update the event's registered count if the registration was active
        if (registration.getStatus() == Status.REGISTERED) {
            Event event = registration.getEvent();
            event.setRegisteredCount(event.getRegisteredCount() - 1);
            eventDAO.update(event);
            
            // Check if there are waitlisted registrations that can be promoted
            promoteWaitlistedRegistration(event.getId());
        }
        
        // Save the registration
        return registrationDAO.update(registration);
    }
    
    /**
     * Unregister a user from an event
     * 
     * @param eventId The ID of the event
     * @param userId The ID of the user
     * @return The cancelled registration
     * @throws SQLException if a database error occurs
     * @throws IllegalArgumentException if registration is not found
     */
    public Registration unregisterUser(int eventId, int userId) throws SQLException {
        List<Registration> registrations = registrationDAO.findByUserAndEvent(userId, eventId);
        if (registrations.isEmpty()) {
            throw new IllegalArgumentException("No registration found for user and event");
        }
        
        // Get the first registration (there should only be one)
        Registration registration = registrations.get(0);
        return cancelRegistration(registration.getId());
    }
    
    /**
     * Promote a waitlisted registration to registered status
     * 
     * @param eventId The ID of the event
     * @throws SQLException if a database error occurs
     */
    private void promoteWaitlistedRegistration(int eventId) throws SQLException {
        // Get the event
        Event event = eventDAO.findById(eventId);
        if (event == null) {
            return;
        }
        
        // Check if there's room for more registrations
        if (event.getRegisteredCount() >= event.getCapacity()) {
            return;
        }
        
        // Get the oldest waitlisted registration
        Registration waitlisted = registrationDAO.findOldestWaitlisted(eventId);
        if (waitlisted == null) {
            return;
        }
        
        // Promote the registration
        waitlisted.setStatus(Status.REGISTERED);
        registrationDAO.update(waitlisted);
        
        // Update the event's registered count
        event.setRegisteredCount(event.getRegisteredCount() + 1);
        eventDAO.update(event);
    }
    
    /**
     * Check in a participant
     * 
     * @param registrationId The ID of the registration to check in
     * @return The updated registration
     * @throws SQLException if a database error occurs
     * @throws IllegalArgumentException if registration is not found
     * @throws IllegalStateException if registration is not in a valid state for check-in
     */
    public Registration checkInParticipant(int registrationId) throws SQLException {
        Registration registration = registrationDAO.findById(registrationId);
        if (registration == null) {
            throw new IllegalArgumentException("Registration not found");
        }
        
        // Check if the registration is valid
        if (registration.getStatus() != Status.REGISTERED) {
            throw new IllegalStateException("Registration is not in a valid state for check-in");
        }
        
        // Check in the participant
        registration.setCheckedIn(true);
        registration.setStatus(Status.ATTENDED);
        
        // Save the registration
        return registrationDAO.update(registration);
    }
    
    /**
     * Mark a participant as a no-show
     * 
     * @param registrationId The ID of the registration to mark as no-show
     * @return The updated registration
     * @throws SQLException if a database error occurs
     * @throws IllegalArgumentException if registration is not found
     * @throws IllegalStateException if registration is not in a valid state to mark as no-show
     */
    public Registration markNoShow(int registrationId) throws SQLException {
        Registration registration = registrationDAO.findById(registrationId);
        if (registration == null) {
            throw new IllegalArgumentException("Registration not found");
        }
        
        // Check if the registration is valid
        if (registration.getStatus() != Status.REGISTERED) {
            throw new IllegalStateException("Registration is not in a valid state to mark as no-show");
        }
        
        // Mark as no-show
        registration.setStatus(Status.NO_SHOW);
        
        // Save the registration
        return registrationDAO.update(registration);
    }
    
    /**
     * Update the status of a registration
     * 
     * @param registrationId The ID of the registration
     * @param status The new status
     * @return The updated registration
     * @throws SQLException if a database error occurs
     * @throws IllegalArgumentException if registration is not found
     */
    public Registration updateRegistrationStatus(int registrationId, Status status) throws SQLException {
        Registration registration = registrationDAO.findById(registrationId);
        if (registration == null) {
            throw new IllegalArgumentException("Registration not found");
        }
        
        registration.setStatus(status);
        return registrationDAO.update(registration);
    }
    
    /**
     * Get a registration by ID
     * 
     * @param registrationId The ID of the registration
     * @return The registration, or null if not found
     * @throws SQLException if a database error occurs
     */
    public Registration getRegistration(int registrationId) throws SQLException {
        return registrationDAO.findById(registrationId);
    }
    
    /**
     * Get all registrations for an event
     * 
     * @param eventId The ID of the event
     * @return A list of registrations for the event
     * @throws SQLException if a database error occurs
     */
    public List<Registration> getEventRegistrations(int eventId) throws SQLException {
        return registrationDAO.findByEvent(eventId);
    }
    
    /**
     * Get all registrations for a user
     * 
     * @param userId The ID of the user
     * @return A list of registrations for the user
     * @throws SQLException if a database error occurs
     */
    public List<Registration> getUserRegistrations(int userId) throws SQLException {
        return registrationDAO.findByUser(userId);
    }
    
    /**
     * Get registrations by status
     * 
     * @param status The status to filter by
     * @return A list of registrations with the specified status
     * @throws SQLException if a database error occurs
     */
    public List<Registration> getRegistrationsByStatus(Status status) throws SQLException {
        return registrationDAO.findByStatus(status);
    }
    
    /**
     * Get the number of registrations for an event
     * 
     * @param eventId The ID of the event
     * @return The number of registrations
     * @throws SQLException if a database error occurs
     */
    public int getRegistrationCount(int eventId) throws SQLException {
        return registrationDAO.countByEvent(eventId);
    }
    
    /**
     * Get the number of available slots for an event
     * 
     * @param eventId The ID of the event
     * @return The number of available slots
     * @throws SQLException if a database error occurs
     * @throws IllegalArgumentException if event is not found
     */
    public int getAvailableSlots(int eventId) throws SQLException {
        Event event = eventDAO.findById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found");
        }
        
        return event.getCapacity() - getRegistrationCount(eventId);
    }
    
    /**
     * Check if a user is registered for an event
     * 
     * @param eventId The ID of the event
     * @param userId The ID of the user
     * @return true if the user is registered, false otherwise
     * @throws SQLException if a database error occurs
     */
    public boolean isUserRegistered(int eventId, int userId) throws SQLException {
        return registrationDAO.existsByUserAndEvent(userId, eventId);
    }
}

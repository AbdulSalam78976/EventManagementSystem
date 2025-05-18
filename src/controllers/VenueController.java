package controllers;

import models.Venue;
import dao.VenueDAO;
import dao.SQLVenueDAO;
import utils.ValidationUtils;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller class for handling venue-related operations
 */
public class VenueController {
    private static VenueController instance;
    private final VenueDAO venueDAO;
    
    private VenueController() throws SQLException {
        this.venueDAO = new SQLVenueDAO();
    }
    
    /**
     * Get the singleton instance of the VenueController
     * 
     * @return The VenueController instance
     */
    public static synchronized VenueController getInstance() throws SQLException {
        if (instance == null) {
            instance = new VenueController();
        }
        return instance;
    }
    
    /**
     * Create a new venue
     * 
     * @param name Venue name
     * @param address Venue address
     * @param city Venue city
     * @param state Venue state
     * @param zipCode Venue zip code
     * @param capacity Venue capacity
     * @param description Venue description
     * @param contactInfo Venue contact information
     * @param type Venue type
     * @return The created venue
     * @throws IllegalArgumentException if the input is invalid
     * @throws SQLException if a database error occurs
     */
    public Venue createVenue(String name, String address, String city, String state, 
                           String zipCode, int capacity, String description, 
                           String contactInfo, String type) throws SQLException {
        // Validate input
        if (!ValidationUtils.isNotEmpty(name)) {
            throw new IllegalArgumentException("Venue name is required");
        }
        if (!ValidationUtils.isNotEmpty(address)) {
            throw new IllegalArgumentException("Venue address is required");
        }
        if (!ValidationUtils.isNotEmpty(city)) {
            throw new IllegalArgumentException("Venue city is required");
        }
        if (!ValidationUtils.isNotEmpty(state)) {
            throw new IllegalArgumentException("Venue state is required");
        }
        if (!ValidationUtils.isValidZipCode(zipCode)) {
            throw new IllegalArgumentException("Invalid zip code format");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        
        // Create venue
        Venue venue = new Venue();
        venue.setName(name);
        venue.setAddress(address);
        venue.setCity(city);
        venue.setState(state);
        venue.setZipCode(zipCode);
        venue.setCapacity(capacity);
        venue.setDescription(description);
        venue.setContactInfo(contactInfo);
        venue.setType(type);
        
        return venueDAO.save(venue);
    }
    
    /**
     * Update an existing venue
     * 
     * @param id The ID of the venue to update
     * @param name Venue name
     * @param address Venue address
     * @param city Venue city
     * @param state Venue state
     * @param zipCode Venue zip code
     * @param capacity Venue capacity
     * @param description Venue description
     * @param contactInfo Venue contact information
     * @param type Venue type
     * @return The updated venue
     * @throws IllegalArgumentException if the input is invalid
     * @throws SQLException if a database error occurs
     */
    public Venue updateVenue(int id, String name, String address, String city, 
                           String state, String zipCode, int capacity, 
                           String description, String contactInfo, String type) throws SQLException {
        // Validate input
        if (!ValidationUtils.isNotEmpty(name)) {
            throw new IllegalArgumentException("Venue name is required");
        }
        if (!ValidationUtils.isNotEmpty(address)) {
            throw new IllegalArgumentException("Venue address is required");
        }
        if (!ValidationUtils.isNotEmpty(city)) {
            throw new IllegalArgumentException("Venue city is required");
        }
        if (!ValidationUtils.isNotEmpty(state)) {
            throw new IllegalArgumentException("Venue state is required");
        }
        if (!ValidationUtils.isValidZipCode(zipCode)) {
            throw new IllegalArgumentException("Invalid zip code format");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
        
        // Get existing venue
        Venue venue = venueDAO.findById(id);
        if (venue == null) {
            throw new IllegalArgumentException("Venue not found");
        }
        
        // Update venue
        venue.setName(name);
        venue.setAddress(address);
        venue.setCity(city);
        venue.setState(state);
        venue.setZipCode(zipCode);
        venue.setCapacity(capacity);
        venue.setDescription(description);
        venue.setContactInfo(contactInfo);
        venue.setType(type);
        
        return venueDAO.update(venue);
    }
    
    /**
     * Delete a venue
     * 
     * @param id The ID of the venue to delete
     * @return true if the venue was deleted, false otherwise
     * @throws IllegalArgumentException if the venue ID is invalid
     * @throws SQLException if a database error occurs
     */
    public boolean deleteVenue(int id) throws SQLException {
        return venueDAO.delete(id);
    }
    
    /**
     * Get a venue by ID
     * 
     * @param id The ID of the venue to get
     * @return The venue, or null if not found
     * @throws IllegalArgumentException if the venue ID is invalid
     * @throws SQLException if a database error occurs
     */
    public Venue getVenue(int id) throws SQLException {
        return venueDAO.findById(id);
    }
    
    /**
     * Get all venues
     * 
     * @return A list of all venues
     * @throws SQLException if a database error occurs
     */
    public List<Venue> getAllVenues() throws SQLException {
        return venueDAO.findAll();
    }
    
    /**
     * Get active venues
     * 
     * @return A list of active venues
     * @throws SQLException if a database error occurs
     */
    public List<Venue> getActiveVenues() throws SQLException {
        return venueDAO.findByActive(true);
    }
    
    /**
     * Get venues by type
     * 
     * @param type The type to filter by
     * @return A list of venues with the specified type
     * @throws IllegalArgumentException if the type is null or empty
     * @throws SQLException if a database error occurs
     */
    public List<Venue> getVenuesByType(String type) throws SQLException {
        if (!ValidationUtils.isNotEmpty(type)) {
            throw new IllegalArgumentException("Venue type cannot be empty");
        }
        return venueDAO.findByType(type);
    }
    
    /**
     * Get venues by capacity
     * 
     * @param minCapacity The minimum capacity
     * @param maxCapacity The maximum capacity
     * @return A list of venues with capacity in the specified range
     * @throws IllegalArgumentException if the capacity range is invalid
     * @throws SQLException if a database error occurs
     */
    public List<Venue> getVenuesByCapacity(int minCapacity, int maxCapacity) throws SQLException {
        if (minCapacity < 0) {
            throw new IllegalArgumentException("Minimum capacity cannot be negative");
        }
        
        if (maxCapacity < minCapacity) {
            throw new IllegalArgumentException("Maximum capacity must be greater than or equal to minimum capacity");
        }
        
        return venueDAO.findByCapacityRange(minCapacity, maxCapacity);
    }
    
    /**
     * Search for venues by name or address
     * 
     * @param query The search query
     * @return A list of venues matching the query
     * @throws IllegalArgumentException if the query is empty
     * @throws SQLException if a database error occurs
     */
    public List<Venue> searchVenues(String query) throws SQLException {
        return venueDAO.search(query);
    }
    
    /**
     * Activate a venue
     * 
     * @param venueId The ID of the venue to activate
     * @return The activated venue
     * @throws IllegalArgumentException if the venue ID is invalid or venue not found
     * @throws SQLException if a database error occurs
     */
    public Venue activateVenue(int venueId) throws SQLException {
        if (venueId <= 0) {
            throw new IllegalArgumentException("Invalid venue ID");
        }
        
        Venue venue = venueDAO.findById(venueId);
        if (venue == null) {
            throw new IllegalArgumentException("Venue not found");
        }
        
        venue.setActive(true);
        return venueDAO.update(venue);
    }
    
    /**
     * Deactivate a venue
     * 
     * @param venueId The ID of the venue to deactivate
     * @return The deactivated venue
     * @throws IllegalArgumentException if the venue ID is invalid or venue not found
     * @throws SQLException if a database error occurs
     */
    public Venue deactivateVenue(int venueId) throws SQLException {
        if (venueId <= 0) {
            throw new IllegalArgumentException("Invalid venue ID");
        }
        
        Venue venue = venueDAO.findById(venueId);
        if (venue == null) {
            throw new IllegalArgumentException("Venue not found");
        }
        
        venue.setActive(false);
        return venueDAO.update(venue);
    }
    
    /**
     * Get a venue by its name.
     * @param name The name of the venue.
     * @return The Venue object if found, otherwise null.
     * @throws SQLException if a database error occurs.
     */
    public Venue getVenueByName(String name) throws SQLException {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Venue name cannot be empty.");
        }
        // Assuming VenueDAO has a findByName method or iterate through all
        // For simplicity, iterating through all for now.
        List<Venue> allVenues = venueDAO.findAll();
        for (Venue venue : allVenues) {
            if (venue.getName().equalsIgnoreCase(name.trim())) {
                return venue;
            }
        }
        return null;
    }
}

package dao;

import java.time.LocalDate;
import java.util.List;
import models.Venue;

/**
 * Data Access Object interface for Venue entities
 */
public interface VenueDAO {
    /**
     * Get all venues
     * @return List of all venues
     */
    List<Venue> findAll();

    /**
     * Get venue by ID
     * @param venueId The venue ID
     * @return The venue, or null if not found
     */
    Venue findById(int venueId);

    /**
     * Get venue by name
     * @param name The venue name
     * @return The venue, or null if not found
     */
    Venue findByName(String name);

    /**
     * Save a new venue
     * @param venue The venue to save
     * @return The saved venue, or null if save failed
     */
    Venue save(Venue venue);

    /**
     * Update an existing venue
     * @param venue The venue to update
     * @return The updated venue, or null if update failed
     */
    Venue update(Venue venue);

    /**
     * Delete a venue
     * @param venueId The ID of the venue to delete
     * @return true if deleted successfully, false otherwise
     */
    boolean delete(int venueId);

    /**
     * Check if a venue with the given name exists
     * @param name The venue name to check
     * @return true if a venue with the name exists, false otherwise
     */
    boolean existsByName(String name);

    /**
     * Get venues by active status
     * @param active The active status to filter by
     * @return List of venues with the specified active status
     */
    List<Venue> findByActive(boolean active);

    /**
     * Get venues by type
     * @param type The venue type to filter by
     * @return List of venues of the specified type
     */
    List<Venue> findByType(String type);

    /**
     * Get venues by city
     * @param city The city to filter by
     * @return List of venues in the specified city
     */
    List<Venue> findByCity(String city);

    /**
     * Get venues by capacity
     * @param minCapacity The minimum capacity to filter by
     * @return List of venues with capacity greater than or equal to minCapacity
     */
    List<Venue> findByCapacity(int minCapacity);

    /**
     * Get venues by capacity range
     * @param minCapacity The minimum capacity to filter by
     * @param maxCapacity The maximum capacity to filter by
     * @return List of venues with capacity in the specified range
     */
    List<Venue> findByCapacityRange(int minCapacity, int maxCapacity);

    /**
     * Search venues by name or address
     * @param query The search query
     * @return List of venues matching the query
     */
    List<Venue> search(String query);

    /**
     * Get available venues for a specific date
     * @param date The date to check availability for
     * @return List of available venues for the date
     */
    List<Venue> getAvailableVenuesForDate(LocalDate date);

    /**
     * Get active venues
     * @return List of active venues
     */
    List<Venue> getActiveVenues();
}

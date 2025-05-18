package dao;

import java.util.List;
import models.Event;
import models.Feedback;
import models.User;
import java.sql.SQLException;

/**
 * Data Access Object interface for Feedback entities
 */
public interface FeedbackDAO {
    /**
     * Get all feedback
     *
     * @return List of all feedback
     * @throws SQLException if a database error occurs
     */
    List<Feedback> getAllFeedback() throws SQLException;

    /**
     * Get feedback by ID
     *
     * @param feedbackId The ID of the feedback to retrieve
     * @return The feedback with the specified ID, or null if not found
     * @throws SQLException if a database error occurs
     */
    Feedback findById(int feedbackId) throws SQLException;

    /**
     * Get all feedback for a specific event
     *
     * @param eventId The ID of the event
     * @return List of feedback for the specified event
     * @throws SQLException if a database error occurs
     */
    List<Feedback> findByEvent(int eventId) throws SQLException;

    /**
     * Get all feedback submitted by a specific user
     *
     * @param userId The ID of the user
     * @return List of feedback submitted by the specified user
     * @throws SQLException if a database error occurs
     */
    List<Feedback> findByUser(int userId) throws SQLException;

    /**
     * Save new feedback
     *
     * @param feedback The feedback to save
     * @return The saved feedback
     * @throws SQLException if a database error occurs
     */
    Feedback save(Feedback feedback) throws SQLException;

    /**
     * Update existing feedback
     *
     * @param feedback The feedback with updated information
     * @return The updated feedback
     * @throws SQLException if a database error occurs
     */
    Feedback update(Feedback feedback) throws SQLException;

    /**
     * Delete feedback by ID
     *
     * @param feedbackId The ID of the feedback to delete
     * @return true if successful, false otherwise
     * @throws SQLException if a database error occurs
     */
    boolean delete(int feedbackId) throws SQLException;

    /**
     * Check if feedback exists for a user and event
     *
     * @param userId The ID of the user
     * @param eventId The ID of the event
     * @return true if feedback exists, false otherwise
     * @throws SQLException if a database error occurs
     */
    boolean existsByUserAndEvent(int userId, int eventId) throws SQLException;

    /**
     * Get all anonymous feedback
     *
     * @return List of all anonymous feedback
     * @throws SQLException if a database error occurs
     */
    List<Feedback> getAnonymousFeedback() throws SQLException;

    /**
     * Get average rating for an event
     *
     * @param eventId The ID of the event
     * @return The average rating for the event
     * @throws SQLException if a database error occurs
     */
    double getAverageRatingForEvent(int eventId) throws SQLException;

    /**
     * Get rating count for an event
     *
     * @param eventId The ID of the event
     * @return The number of ratings for the event
     * @throws SQLException if a database error occurs
     */
    int getRatingCountForEvent(int eventId) throws SQLException;
}

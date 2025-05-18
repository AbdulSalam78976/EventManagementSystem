package controllers;

import models.Feedback;
import models.User;
import models.Event;
import models.Registration;
import dao.FeedbackDAO;
import dao.SQLFeedbackDAO;
import dao.RegistrationDAO;
import dao.SQLEventRegistrationDAO;
import dao.EventDAO;
import dao.SQLEventDAO;
import dao.UserDAO;
import dao.SQLUserDAO;
import utils.ValidationUtils;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller class for handling feedback-related operations
 */
public class FeedbackController {
    
    private static FeedbackController instance;
    private final FeedbackDAO feedbackDAO;
    private final RegistrationDAO registrationDAO;
    private final EventDAO eventDAO;
    private final UserDAO userDAO;
    
    /**
     * Private constructor for singleton pattern
     * 
     * @throws SQLException if a database error occurs during initialization
     */
    private FeedbackController() throws SQLException {
        this.feedbackDAO = new SQLFeedbackDAO();
        this.registrationDAO = new SQLEventRegistrationDAO();
        this.eventDAO = new SQLEventDAO();
        this.userDAO = new SQLUserDAO();
    }
    
    /**
     * Get the singleton instance of the FeedbackController
     * 
     * @return The FeedbackController instance
     * @throws SQLException if a database error occurs during initialization
     */
    public static synchronized FeedbackController getInstance() throws SQLException {
        if (instance == null) {
            instance = new FeedbackController();
        }
        return instance;
    }
    
    /**
     * Submit feedback for an event
     * 
     * @param user The user submitting the feedback
     * @param eventId The ID of the event
     * @param rating The rating (1-5)
     * @param comment The comment
     * @param anonymous Whether the feedback should be anonymous
     * @return The created feedback
     * @throws IllegalArgumentException if the input is invalid
     * @throws IllegalStateException if the feedback cannot be submitted
     * @throws SQLException if a database error occurs
     */
    public Feedback submitFeedback(User user, int eventId, int rating, String comment, boolean anonymous) 
            throws SQLException {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }
        
        if (eventId <= 0) {
            throw new IllegalArgumentException("Invalid event ID");
        }
        
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        
        if (!ValidationUtils.isNotEmpty(comment)) {
            throw new IllegalArgumentException("Comment cannot be empty");
        }
        
        // Get the event
        Event event = eventDAO.findById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found");
        }
        
        // Check if the event has ended
        if (!event.isPast()) {
            throw new IllegalStateException("Cannot submit feedback for an event that hasn't ended yet");
        }
        
        // Check if the user attended the event
        boolean attended = registrationDAO.findByUserAndEvent(user.getId(), eventId)
            .stream()
            .anyMatch(r -> r.getStatus() == Registration.Status.ATTENDED);
        
        if (!attended) {
            throw new IllegalStateException("User did not attend this event");
        }
        
        // Check if the user has already submitted feedback
        if (feedbackDAO.existsByUserAndEvent(user.getId(), eventId)) {
            throw new IllegalStateException("User has already submitted feedback for this event");
        }
        
        // Create the feedback
        Feedback feedback = new Feedback(user, event, rating, comment.trim());
        feedback.setAnonymous(anonymous);
        
        // Save the feedback
        return feedbackDAO.save(feedback);
    }
    
    /**
     * Update existing feedback
     * 
     * @param feedbackId The ID of the feedback to update
     * @param rating The new rating
     * @param comment The new comment
     * @param anonymous Whether the feedback should be anonymous
     * @return The updated feedback
     * @throws IllegalArgumentException if the input is invalid
     * @throws SQLException if a database error occurs
     */
    public Feedback updateFeedback(int feedbackId, int rating, String comment, boolean anonymous) 
            throws SQLException {
        if (feedbackId <= 0) {
            throw new IllegalArgumentException("Invalid feedback ID");
        }
        
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        
        if (!ValidationUtils.isNotEmpty(comment)) {
            throw new IllegalArgumentException("Comment cannot be empty");
        }
        
        Feedback feedback = feedbackDAO.findById(feedbackId);
        if (feedback == null) {
            throw new IllegalArgumentException("Feedback not found");
        }
        
        // Update the feedback
        feedback.setRating(rating);
        feedback.setComment(comment.trim());
        feedback.setAnonymous(anonymous);
        
        // Save the feedback
        return feedbackDAO.update(feedback);
    }
    
    /**
     * Delete feedback
     * 
     * @param feedbackId The ID of the feedback to delete
     * @return true if the feedback was deleted, false otherwise
     * @throws IllegalArgumentException if the feedback ID is invalid
     * @throws SQLException if a database error occurs
     */
    public boolean deleteFeedback(int feedbackId) throws SQLException {
        if (feedbackId <= 0) {
            throw new IllegalArgumentException("Invalid feedback ID");
        }
        
        Feedback feedback = feedbackDAO.findById(feedbackId);
        if (feedback == null) {
            throw new IllegalArgumentException("Feedback not found");
        }
        
        return feedbackDAO.delete(feedbackId);
    }
    
    /**
     * Get feedback by ID
     * 
     * @param feedbackId The ID of the feedback to get
     * @return The feedback, or null if not found
     * @throws IllegalArgumentException if the feedback ID is invalid
     * @throws SQLException if a database error occurs
     */
    public Feedback getFeedbackById(int feedbackId) throws SQLException {
        if (feedbackId <= 0) {
            throw new IllegalArgumentException("Invalid feedback ID");
        }
        return feedbackDAO.findById(feedbackId);
    }
    
    /**
     * Get feedback by user
     * 
     * @param userId The ID of the user
     * @return A list of feedback submitted by the specified user
     * @throws IllegalArgumentException if the user ID is invalid
     * @throws SQLException if a database error occurs
     */
    public List<Feedback> getFeedbackByUser(int userId) throws SQLException {
        if (userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID");
        }
        
        User user = userDAO.getUserById(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }
        
        return feedbackDAO.findByUser(userId);
    }
    
    /**
     * Get feedback by event
     * 
     * @param eventId The ID of the event
     * @return A list of feedback for the specified event
     * @throws IllegalArgumentException if the event ID is invalid
     * @throws SQLException if a database error occurs
     */
    public List<Feedback> getFeedbackByEvent(int eventId) throws SQLException {
        if (eventId <= 0) {
            throw new IllegalArgumentException("Invalid event ID");
        }
        
        Event event = eventDAO.findById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found");
        }
        
        return feedbackDAO.findByEvent(eventId);
    }
    
    /**
     * Calculate the average rating for an event
     * 
     * @param eventId The ID of the event
     * @return The average rating, or 0 if no feedback exists
     * @throws IllegalArgumentException if the event ID is invalid
     * @throws SQLException if a database error occurs
     */
    public double getAverageRating(int eventId) throws SQLException {
        if (eventId <= 0) {
            throw new IllegalArgumentException("Invalid event ID");
        }
        
        Event event = eventDAO.findById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found");
        }
        
        List<Feedback> feedbacks = feedbackDAO.findByEvent(eventId);
        if (feedbacks.isEmpty()) {
            return 0.0;
        }
        
        double sum = feedbacks.stream()
            .mapToInt(Feedback::getRating)
            .sum();
            
        return sum / feedbacks.size();
    }
    
    /**
     * Get the distribution of ratings for an event
     * 
     * @param eventId The ID of the event
     * @return An array of 5 integers representing the count of each rating (1-5)
     * @throws IllegalArgumentException if the event ID is invalid
     * @throws SQLException if a database error occurs
     */
    public int[] getRatingDistribution(int eventId) throws SQLException {
        if (eventId <= 0) {
            throw new IllegalArgumentException("Invalid event ID");
        }
        
        Event event = eventDAO.findById(eventId);
        if (event == null) {
            throw new IllegalArgumentException("Event not found");
        }
        
        List<Feedback> feedbacks = feedbackDAO.findByEvent(eventId);
        int[] distribution = new int[5];
        
        for (Feedback feedback : feedbacks) {
            int rating = feedback.getRating();
            if (rating >= 1 && rating <= 5) {
                distribution[rating - 1]++;
            }
        }
        
        return distribution;
    }
}

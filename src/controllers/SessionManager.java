package controllers;

import models.User;

/**
 * Manages user sessions throughout the application
 */
public class SessionManager {
    
    private static SessionManager instance;
    private User currentUser;
    
    // Private constructor for singleton pattern
    private SessionManager() {
    }
    
    /**
     * Get the singleton instance of the SessionManager
     * 
     * @return The SessionManager instance
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Start a new session for a user
     * 
     * @param user The user to start a session for
     */
    public void startSession(User user) {
        this.currentUser = user;
    }
    
    /**
     * End the current session
     */
    public void endSession() {
        this.currentUser = null;
    }
    
    /**
     * Get the currently logged-in user
     * 
     * @return The current user, or null if no user is logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Check if a user is currently logged in
     * 
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Check if the current user has a specific role
     * 
     * @param role The role to check for
     * @return true if the current user has the specified role, false otherwise
     */
    public boolean hasRole(User.UserRole role) {
        return isLoggedIn() && currentUser.getRole() == role;
    }
    
    /**
     * Check if the current user is an administrator
     * 
     * @return true if the current user is an administrator, false otherwise
     */
    public boolean isAdmin() {
        return hasRole(User.UserRole.ADMIN);
    }
    
    /**
     * Check if the current user is an organizer
     * 
     * @return true if the current user is an organizer, false otherwise
     */
    public boolean isOrganizer() {
        return hasRole(User.UserRole.EVENT_ORGANIZER);
    }
    
    /**
     * Check if the current user is an attendee
     * 
     * @return true if the current user is an attendee, false otherwise
     */
    public boolean isAttendee() {
        return hasRole(User.UserRole.ATTENDEE);
    }
}

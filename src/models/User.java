package models;

import java.util.Date;

/**
 * Model class representing a user in the system
 */
public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private UserRole role;
    private String phone;
    private String studentId; // Only applicable for Attendees
    private boolean active;
    private String registrationDate;
    private Date createdAt;
    private Date lastLoginAt;
    private String securityQuestion1;
    private String securityAnswer1;
    private String securityQuestion2;
    private String securityAnswer2;

    /**
     * Enum representing user roles in the system
     */
    public enum UserRole {
        ADMIN("Administrator"),
        EVENT_ORGANIZER("Event Organizer"),
        ATTENDEE("Attendee");

        private final String displayName;

        UserRole(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        public static UserRole fromString(String text) {
            for (UserRole role : UserRole.values()) {
                if (role.displayName.equalsIgnoreCase(text)) {
                    return role;
                }
            }
            throw new IllegalArgumentException("No role with display name: " + text);
        }
    }

    /**
     * Constructor for creating a new user
     * @param name the user's name
     * @param email the user's email
     * @param password the user's password
     * @param role the user's role
     */
    public User(String name, String email, String password, UserRole role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.active = true;
        this.createdAt = new Date();
        // Current date as registration date in format YYYY-MM-DD
        this.registrationDate = java.time.LocalDate.now().toString();
    }

    /**
     * Constructor for creating a user with all fields
     * @param id the user's ID
     * @param name the user's name
     * @param email the user's email
     * @param password the user's password
     * @param role the user's role
     * @param active whether the user is active
     */
    public User(int id, String name, String email, String password, UserRole role, boolean active) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.active = active;
        this.createdAt = new Date();
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Date lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public String getSecurityQuestion1() {
        return securityQuestion1;
    }

    public void setSecurityQuestion1(String securityQuestion1) {
        this.securityQuestion1 = securityQuestion1;
    }

    public String getSecurityAnswer1() {
        return securityAnswer1;
    }

    public void setSecurityAnswer1(String securityAnswer1) {
        this.securityAnswer1 = securityAnswer1;
    }

    public String getSecurityQuestion2() {
        return securityQuestion2;
    }

    public void setSecurityQuestion2(String securityQuestion2) {
        this.securityQuestion2 = securityQuestion2;
    }

    public String getSecurityAnswer2() {
        return securityAnswer2;
    }

    public void setSecurityAnswer2(String securityAnswer2) {
        this.securityAnswer2 = securityAnswer2;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", active=" + active +
                '}';
    }
}

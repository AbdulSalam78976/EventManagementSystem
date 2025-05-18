package utils;

import org.mindrot.jbcrypt.BCrypt;
import java.security.SecureRandom;

/**
 * Utility class for security-related operations
 */
public class SecurityUtils {
    
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int BCRYPT_ROUNDS = 12;
    
    /**
     * Hash a password using BCrypt
     * 
     * @param password The password to hash
     * @return The hashed password
     */
    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(BCRYPT_ROUNDS));
    }
    
    /**
     * Verify a password against a hashed password
     * 
     * @param password The password to verify
     * @param hashedPassword The hashed password to compare against
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
    
    /**
     * Generate a random salt using BCrypt's built-in generator
     * 
     * @return A random salt
     */
    public static String generateSalt() {
        return BCrypt.gensalt(BCRYPT_ROUNDS);
    }
} 
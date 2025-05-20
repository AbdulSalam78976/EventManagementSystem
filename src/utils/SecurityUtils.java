package utils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

/**
 * Utility class for security-related operations
 */
public class SecurityUtils {
    
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int SALT_LENGTH = 16; // 128 bits
    private static final int HASH_LENGTH = 32; // 256 bits
    private static final int ITERATIONS = 10000; // Number of iterations for PBKDF2
    
    /**
     * Hashes a password using PBKDF2 with SHA-256
     * 
     * @param password the password to hash
     * @return the hashed password with salt (format: iterations:salt:hash)
     */
    public static String hashPassword(String password) {
        try {
            // Generate a random salt
            byte[] salt = new byte[SALT_LENGTH];
            RANDOM.nextBytes(salt);

            // Create PBEKeySpec for PBKDF2
            PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                ITERATIONS,
                HASH_LENGTH * 8 // Convert bytes to bits
            );

            // Get SecretKeyFactory instance for PBKDF2WithHmacSHA256
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            
            // Generate the hash
            byte[] hash = factory.generateSecret(spec).getEncoded();
            spec.clearPassword(); // Clear the password from memory

            // Convert salt and hash to Base64 for storage
            String saltStr = Base64.getEncoder().encodeToString(salt);
            String hashStr = Base64.getEncoder().encodeToString(hash);

            // Return iterations:salt:hash
            return ITERATIONS + ":" + saltStr + ":" + hashStr;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Verifies a password against a stored hash, handling both new PBKDF2 and legacy SHA-256 formats.
     * If a legacy hash is encountered and verification succeeds, returns a new hash in the PBKDF2 format.
     *
     * @param password the password to verify
     * @param storedHash the stored hash (format: iterations:salt:hash for PBKDF2 or salt:hash for legacy)
     * @return VerificationResult containing success status and optionally a new hash if migration is needed
     */
    public static VerificationResult verifyPassword(String password, String storedHash) {
        try {
            System.out.println("Verifying password. Hash format: " + (storedHash.split(":").length == 3 ? "PBKDF2" : "Legacy"));
            
            // Split stored string
            String[] parts = storedHash.split(":");
            
            // Check if this is a legacy hash (2 parts) or new PBKDF2 hash (3 parts)
            if (parts.length == 2) {
                System.out.println("Using legacy verification");
                // Legacy SHA-256 format
                if (verifyLegacyPassword(password, storedHash)) {
                    System.out.println("Legacy verification successful, returning new hash format");
                    // If legacy verification succeeds, return success with new hash format
                    return new VerificationResult(true, hashPassword(password));
                }
                System.out.println("Legacy verification failed");
                return new VerificationResult(false, null);
            } else if (parts.length == 3) {
                System.out.println("Using PBKDF2 verification");
                // New PBKDF2 format
                if (verifyPBKDF2Password(password, storedHash)) {
                    System.out.println("PBKDF2 verification successful");
                    return new VerificationResult(true, null);
                }
                System.out.println("PBKDF2 verification failed");
                return new VerificationResult(false, null);
            }
            
            System.out.println("Invalid hash format");
            return new VerificationResult(false, null);
        } catch (Exception e) {
            System.out.println("Error during password verification: " + e.getMessage());
            e.printStackTrace();
            return new VerificationResult(false, null);
        }
    }

    /**
     * Verifies a password against a legacy SHA-256 hash
     */
    private static boolean verifyLegacyPassword(String password, String storedHash) {
        try {
            String[] parts = storedHash.split(":");
            if (parts.length != 2) return false;

            byte[] salt = Base64.getDecoder().decode(parts[0]);
            byte[] hash = Base64.getDecoder().decode(parts[1]);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] computedHash = md.digest(password.getBytes());

            // Debug output for hash comparison
            System.out.println("Stored hash length: " + hash.length);
            System.out.println("Computed hash length: " + computedHash.length);
            System.out.println("Stored hash: " + Base64.getEncoder().encodeToString(hash));
            System.out.println("Computed hash: " + Base64.getEncoder().encodeToString(computedHash));

            return MessageDigest.isEqual(computedHash, hash);
        } catch (Exception e) {
            System.out.println("Error in legacy password verification: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Verifies a password against a PBKDF2 hash
     */
    private static boolean verifyPBKDF2Password(String password, String storedHash) {
        try {
            String[] parts = storedHash.split(":");
            if (parts.length != 3) return false;

            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] hash = Base64.getDecoder().decode(parts[2]);

            PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                salt,
                iterations,
                hash.length * 8
            );

            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] testHash = factory.generateSecret(spec).getEncoded();
            spec.clearPassword();

            // Debug output for hash comparison
            System.out.println("Stored hash length: " + hash.length);
            System.out.println("Computed hash length: " + testHash.length);
            System.out.println("Stored hash: " + Base64.getEncoder().encodeToString(hash));
            System.out.println("Computed hash: " + Base64.getEncoder().encodeToString(testHash));

            return MessageDigest.isEqual(hash, testHash);
        } catch (Exception e) {
            System.out.println("Error in PBKDF2 password verification: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Result class for password verification that includes migration information
     */
    public static class VerificationResult {
        private final boolean success;
        private final String newHash;

        public VerificationResult(boolean success, String newHash) {
            this.success = success;
            this.newHash = newHash;
        }

        public boolean isSuccess() {
            return success;
        }

        public boolean needsMigration() {
            return newHash != null;
        }

        public String getNewHash() {
            return newHash;
        }
    }
} 
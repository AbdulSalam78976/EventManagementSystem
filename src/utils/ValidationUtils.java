package utils;

import javax.swing.*;
import java.awt.Component;
import java.util.regex.Pattern;

public class ValidationUtils {
    private ValidationUtils() {}

    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final Pattern HAS_UPPERCASE = Pattern.compile("[A-Z]");
    private static final Pattern HAS_LOWERCASE = Pattern.compile("[a-z]");
    private static final Pattern HAS_NUMBER = Pattern.compile("\\d");
    private static final Pattern HAS_SPECIAL = Pattern.compile("[!@#$%^&*(),.?\":{}|<>]");

    // Email validation pattern (RFC 5322 compliant)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
        Pattern.CASE_INSENSITIVE
    );

    // Password validation pattern (min 8 chars, at least one number, one letter)
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[0-9])(?=.*[a-zA-Z]).{8,}$"
    );

    // Phone number validation pattern (international format)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^\\+?[1-9]\\d{1,14}$"
    );

    // Hex color validation pattern
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile(
        "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$"
    );

    // Zip code validation pattern
    private static final Pattern ZIP_CODE_PATTERN = Pattern.compile(
        "^\\d{5}(-\\d{4})?$"
    );

    /**
     * Validates an email address
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        // Trim the email and convert to lowercase for consistent validation
        String trimmedEmail = email.trim().toLowerCase();
        return EMAIL_PATTERN.matcher(trimmedEmail).matches();
    }

    /**
     * Validates a password
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    /**
     * Validates a phone number
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Validates that a text field is not empty
     */
    public static boolean isNotEmpty(String text) {
        return text != null && !text.trim().isEmpty();
    }

    /**
     * Validates a date string (YYYY-MM-DD format)
     */
    public static boolean isValidDate(String date) {
        if (date == null || !date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return false;
        }
        try {
            java.time.LocalDate.parse(date);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validates a time string (HH:mm format)
     */
    public static boolean isValidTime(String time) {
        if (time == null || !time.matches("\\d{2}:\\d{2}")) {
            return false;
        }
        try {
            java.time.LocalTime.parse(time);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Validates a number is within a range
     */
    public static boolean isInRange(int number, int min, int max) {
        return number >= min && number <= max;
    }

    /**
     * Validates a number is positive
     */
    public static boolean isPositive(double number) {
        return number > 0;
    }

    /**
     * Shows validation error message
     */
    public static void showValidationError(Component parent, String message) {
        UIUtils.showError(parent, "Validation Error: " + message);
    }

    /**
     * Validates a JTextField and shows error if invalid
     */
    public static boolean validateTextField(JTextField field, String fieldName, boolean required) {
        String text = field.getText().trim();
        
        if (required && !isNotEmpty(text)) {
            showValidationError(field, fieldName + " is required");
            field.requestFocus();
            return false;
        }
        
        return true;
    }

    /**
     * Validates a JPasswordField and shows error if invalid
     */
    public static boolean validatePasswordField(JPasswordField field, String fieldName, boolean required) {
        String password = new String(field.getPassword());
        
        if (required && !isNotEmpty(password)) {
            showValidationError(field, fieldName + " is required");
            field.requestFocus();
            return false;
        }
        
        if (isNotEmpty(password) && !isValidPassword(password)) {
            showValidationError(field, "Password must be at least 8 characters long and contain at least one number and one letter");
            field.requestFocus();
            return false;
        }
        
        return true;
    }

    /**
     * Validates an email field and shows error if invalid
     */
    public static boolean validateEmailField(JTextField field, String fieldName, boolean required) {
        String email = field.getText().trim();
        
        if (required && !isNotEmpty(email)) {
            showValidationError(field, fieldName + " is required");
            field.requestFocus();
            return false;
        }
        
        if (isNotEmpty(email) && !isValidEmail(email)) {
            showValidationError(field, "Invalid email format");
            field.requestFocus();
            return false;
        }
        
        return true;
    }

    /**
     * Validates a phone field and shows error if invalid
     */
    public static boolean validatePhoneField(JTextField field, String fieldName, boolean required) {
        String phone = field.getText().trim();
        
        if (required && !isNotEmpty(phone)) {
            showValidationError(field, fieldName + " is required");
            field.requestFocus();
            return false;
        }
        
        if (isNotEmpty(phone) && !isValidPhone(phone)) {
            showValidationError(field, "Invalid phone number format. Please use international format (e.g., +1234567890)");
            field.requestFocus();
            return false;
        }
        
        return true;
    }

    /**
     * Check if a string is a valid hex color code
     */
    public static boolean isValidHexColor(String color) {
        return color != null && HEX_COLOR_PATTERN.matcher(color).matches();
    }

    /**
     * Check if a string is a valid US zip code
     */
    public static boolean isValidZipCode(String zipCode) {
        return zipCode != null && ZIP_CODE_PATTERN.matcher(zipCode).matches();
    }

    /**
     * Validates a phone number
     * 
     * @param phoneNumber The phone number to validate
     * @return true if the phone number is valid, false otherwise
     */
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        // Remove any non-digit characters
        String digitsOnly = phoneNumber.replaceAll("\\D", "");
        // Check if it has exactly 10 digits
        return digitsOnly.length() == 10;
    }
} 
package com.hotel.util;

import com.hotel.exception.InvalidInputException;
import java.util.regex.Pattern;

/**
 * Utility class for validating user input throughout the system.
 * All methods throw InvalidInputException on validation failure.
 */
public class InputValidator {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private static final Pattern PHONE_PATTERN =
        Pattern.compile("^[6-9]\\d{9}$");   // Indian mobile number format

    private static final Pattern UPI_PATTERN =
        Pattern.compile("^[\\w.]+@[\\w]+$");

    private static final Pattern CARD_PATTERN =
        Pattern.compile("^\\d{16}$");

    // Private constructor — utility class should not be instantiated
    private InputValidator() {}

    /**
     * Validates that a string is not null or blank.
     *
     * @param value     The string to validate
     * @param fieldName Name of the field (used in the error message)
     * @throws InvalidInputException if the value is null or blank
     */
    public static void validateNotBlank(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new InvalidInputException(fieldName + " cannot be empty.");
        }
    }

    /**
     * Validates an email address format.
     *
     * @param email The email to validate
     * @throws InvalidInputException if the email format is invalid
     */
    public static void validateEmail(String email) {
        validateNotBlank(email, "Email");
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidInputException("Invalid email format: " + email);
        }
    }

    /**
     * Validates a 10-digit Indian mobile phone number.
     *
     * @param phone The phone number to validate
     * @throws InvalidInputException if the phone number format is invalid
     */
    public static void validatePhone(String phone) {
        validateNotBlank(phone, "Phone number");
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new InvalidInputException(
                "Invalid phone number. Must be a 10-digit Indian mobile number starting with 6-9."
            );
        }
    }

    /**
     * Validates that a number of nights is within an acceptable range.
     *
     * @param nights Number of nights
     * @throws InvalidInputException if nights is less than 1 or more than 365
     */
    public static void validateNights(int nights) {
        if (nights < 1 || nights > 365) {
            throw new InvalidInputException(
                "Number of nights must be between 1 and 365."
            );
        }
    }

    /**
     * Validates a UPI ID format.
     *
     * @param upiId UPI ID to validate
     * @throws InvalidInputException if format is invalid
     */
    public static void validateUpiId(String upiId) {
        validateNotBlank(upiId, "UPI ID");
        if (!UPI_PATTERN.matcher(upiId).matches()) {
            throw new InvalidInputException(
                "Invalid UPI ID format. Expected format: username@bank"
            );
        }
    }

    /**
     * Validates a 16-digit credit card number.
     *
     * @param cardNumber Credit card number
     * @throws InvalidInputException if format is invalid
     */
    public static void validateCardNumber(String cardNumber) {
        validateNotBlank(cardNumber, "Card number");
        String digitsOnly = cardNumber.replaceAll("\\s+", "");
        if (!CARD_PATTERN.matcher(digitsOnly).matches()) {
            throw new InvalidInputException(
                "Invalid credit card number. Must be exactly 16 digits."
            );
        }
    }

    /**
     * Validates that a price is a positive number.
     *
     * @param price The price to validate
     * @throws InvalidInputException if price is not positive
     */
    public static void validatePrice(double price) {
        if (price <= 0) {
            throw new InvalidInputException("Price must be a positive number.");
        }
    }

    /**
     * Validates a menu choice is within the allowed range.
     *
     * @param choice   User's menu choice
     * @param min      Minimum valid value (inclusive)
     * @param max      Maximum valid value (inclusive)
     * @throws InvalidInputException if choice is out of range
     */
    public static void validateMenuChoice(int choice, int min, int max) {
        if (choice < min || choice > max) {
            throw new InvalidInputException(
                String.format("Please enter a number between %d and %d.", min, max)
            );
        }
    }

    /**
     * Returns a masked version of a credit card number (shows last 4 digits only).
     *
     * @param cardNumber Full 16-digit card number
     * @return Masked card number like **** **** **** 1234
     */
    public static String maskCardNumber(String cardNumber) {
        String digitsOnly = cardNumber.replaceAll("\\s+", "");
        return "**** **** **** " + digitsOnly.substring(12);
    }
}

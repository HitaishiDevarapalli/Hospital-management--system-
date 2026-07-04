package util;

public class Validator {
    
    public static boolean validateAge(int age) {
        return age >= 0;
    }

    public static boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return false;
        // Accept phone number of digits (typically 10-15 digits, optionally prefixed with +)
        String trimmed = phoneNumber.trim();
        return trimmed.matches("^\\+?[0-9]{10,15}$");
    }

    public static boolean validateEmail(String email) {
        if (email == null) return false;
        return email.trim().matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}

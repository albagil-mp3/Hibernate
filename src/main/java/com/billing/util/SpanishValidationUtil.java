package com.billing.util;

import com.billing.entity.SpanishProvince;

/**
 * Utility class for validating Spanish business rules like DNI, postal codes, etc.
 */
public class SpanishValidationUtil {
    
    private static final String DNI_LETTERS = "TRWAGMYFPDXBNJZSQVHLCKE";
    
    /**
     * Validates Spanish DNI (Documento Nacional de Identidad)
     * @param dni DNI string (8 digits + 1 letter)
     * @return true if DNI is valid, false otherwise
     */
    public static boolean isValidDNI(String dni) {
        if (dni == null || dni.length() != 9) {
            return false;
        }
        
        try {
            // Extract numbers and letter
            String numbers = dni.substring(0, 8);
            char letter = dni.charAt(8);
            
            // Calculate expected letter
            int dniNumber = Integer.parseInt(numbers);
            char expectedLetter = DNI_LETTERS.charAt(dniNumber % 23);
            
            return letter == expectedLetter;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validates if postal code matches the given province
     * @param postalCode 5-digit postal code
     * @param province Spanish province
     * @return true if postal code is valid for the province
     */
    public static boolean isValidPostalCodeForProvince(String postalCode, SpanishProvince province) {
        if (postalCode == null || province == null) {
            return false;
        }
        return province.isValidPostalCode(postalCode);
    }
    
    /**
     * Validates Spanish IBAN format
     * @param iban Bank account number in IBAN format
     * @return true if IBAN is valid Spanish format
     */
    public static boolean isValidSpanishIBAN(String iban) {
        if (iban == null || iban.length() != 24) {
            return false;
        }
        
        if (!iban.startsWith("ES")) {
            return false;
        }
        
        try {
            // Extract the numeric part (22 digits after ES)
            String numericPart = iban.substring(2);
            Long.valueOf(numericPart);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validates Spanish phone number (9 digits)
     * @param phone Phone number string
     * @return true if phone number is valid
     */
    public static boolean isValidSpanishPhone(String phone) {
        if (phone == null || phone.length() != 9) {
            return false;
        }
        
        try {
            Long.valueOf(phone);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Formats DNI with proper letter case
     * @param dni DNI string
     * @return formatted DNI or null if invalid
     */
    public static String formatDNI(String dni) {
        if (dni == null || dni.length() != 9) {
            return null;
        }
        
        String numbers = dni.substring(0, 8);
        String letter = dni.substring(8).toUpperCase();
        
        return numbers + letter;
    }
}
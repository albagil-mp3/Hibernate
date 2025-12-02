package com.billing.entity;

/**
 * Enumeration for payment methods supported by the billing system
 */
public enum PaymentMethod {
    CREDIT("Crèdit", "Credit"),
    CASH("Contat", "Cash");
    
    private final String catalanName;
    private final String englishName;
    
    PaymentMethod(String catalanName, String englishName) {
        this.catalanName = catalanName;
        this.englishName = englishName;
    }
    
    public String getCatalanName() {
        return catalanName;
    }
    
    public String getEnglishName() {
        return englishName;
    }
    
    @Override
    public String toString() {
        return englishName;
    }
}
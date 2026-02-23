package com.billing.model;

/**
 * Enumeration for payment methods supported by the billing system
 */
public enum PaymentMethod {
    CASH("Cash"),
    CARD("Credit Card"),
    TRANSFER("Bank Transfer"),
    CHECK("Check");
    
    private final String name;
    
    PaymentMethod(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return name;
    }
}

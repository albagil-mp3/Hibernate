package com.billing.entity;

/**
 * Enumeration for payment methods supported by the billing system
 */
public enum PaymentMethod {
    EFECTIVO("Cash"),
    TRANSFERENCIA_BANCARIA("Bank Transfer"),
    TARJETA_CREDITO("Credit Card"),
    DOMICILIACION_BANCARIA("Direct Debit"),
    CHEQUE("Check");
    
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
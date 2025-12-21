package com.billing.entity;

/**
 * Enumeration for payment methods supported by the billing system
 */
public enum PaymentMethod {
    TRANSFERENCIA_BANCARIA("Transferència Bancària", "Bank Transfer"),
    DOMICILIACION_BANCARIA("Domiciliació Bancària", "Direct Debit"),
    TARJETA_CREDITO("Targeta de Crèdit", "Credit Card"),
    TARJETA_DEBITO("Targeta de Dèbit", "Debit Card"),
    CHEQUE("Xec", "Check"),
    EFECTIVO("Efectiu", "Cash");
    
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
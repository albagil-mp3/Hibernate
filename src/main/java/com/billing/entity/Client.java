package com.billing.entity;

import javax.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * Client entity representing customers in the billing system
 * Corresponds to 'clientes' table in 'facturacion' database
 */
@Entity
@Table(name = "clientes")
public class Client {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @NotNull(message = "Name cannot be null")
    @Size(max = 50, message = "Name must not exceed 50 characters")
    @Column(name = "nom", nullable = false, length = 50)
    private String name;
    
    @NotNull(message = "DNI cannot be null")
    @Size(max = 9, message = "DNI must not exceed 9 characters")
    @Pattern(regexp = "^[0-9]{8}[A-Z]$", message = "DNI must have 8 digits followed by a letter")
    @Column(name = "dni", nullable = false, length = 9, unique = true)
    private String dni;
    
    @NotNull(message = "Address cannot be null")
    @Size(max = 50, message = "Address must not exceed 50 characters")
    @Column(name = "direccio", nullable = false, length = 50)
    private String address;
    
    @NotNull(message = "City cannot be null")
    @Size(max = 30, message = "City must not exceed 30 characters")
    @Column(name = "poblacio", nullable = false, length = 30)
    private String city;
    
    @NotNull(message = "Province cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "provincia", nullable = false)
    private SpanishProvince province;
    
    @Pattern(regexp = "^[0-9]{5}$", message = "Postal code must be 5 digits")
    @Column(name = "codi_postal", length = 5)
    private String postalCode;
    
    @Pattern(regexp = "^[0-9]{9}$", message = "Fixed phone must be 9 digits")
    @Column(name = "telefon_fixe", length = 9)
    private String fixedPhone;
    
    @Pattern(regexp = "^[0-9]{9}$", message = "Mobile phone must be 9 digits")
    @Column(name = "telefon_mobil", length = 9)
    private String mobilePhone;
    
    @Email(message = "Invalid email format")
    @Size(max = 80, message = "Email must not exceed 80 characters")
    @Column(name = "correu_electronic", length = 80)
    private String email;
    
    @Size(max = 50, message = "Website must not exceed 50 characters")
    @Column(name = "plana_web", length = 50)
    private String website;
    
    @NotNull(message = "Payment method cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "forma_pagament", nullable = false)
    private PaymentMethod paymentMethod;
    
    @DecimalMin(value = "0.00", message = "Credit limit must be at least 0.00")
    @DecimalMax(value = "1000000.00", message = "Credit limit must not exceed 1,000,000.00")
    @Column(name = "limit_credit", precision = 10, scale = 2)
    private BigDecimal creditLimit;
    
    @Size(max = 34, message = "Bank account number must not exceed 34 characters (IBAN format)")
    @Pattern(regexp = "^ES[0-9]{22}$", message = "Bank account must be a valid Spanish IBAN (ES followed by 22 digits)")
    @Column(name = "numero_conta_bancari", length = 34)
    private String bankAccountNumber;
    
    @Column(name = "actiu", nullable = false)
    private Boolean active = true;
    
    @Size(max = 500, message = "Observations must not exceed 500 characters")
    @Column(name = "observacions", length = 500)
    private String observations;
    
    @Lob
    @Column(name = "imatge")
    private byte[] image;
    
    // Constructors
    public Client() {
        this.active = true;
        this.creditLimit = BigDecimal.ZERO;
    }
    
    public Client(String name, String dni, String address, String city, 
                  SpanishProvince province, PaymentMethod paymentMethod) {
        this();
        this.name = name;
        this.dni = dni;
        this.address = address;
        this.city = city;
        this.province = province;
        this.paymentMethod = paymentMethod;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDni() {
        return dni;
    }
    
    public void setDni(String dni) {
        this.dni = dni;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public SpanishProvince getProvince() {
        return province;
    }
    
    public void setProvince(SpanishProvince province) {
        this.province = province;
    }
    
    public String getPostalCode() {
        return postalCode;
    }
    
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
    
    public String getFixedPhone() {
        return fixedPhone;
    }
    
    public void setFixedPhone(String fixedPhone) {
        this.fixedPhone = fixedPhone;
    }
    
    public String getMobilePhone() {
        return mobilePhone;
    }
    
    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getWebsite() {
        return website;
    }
    
    public void setWebsite(String website) {
        this.website = website;
    }
    
    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }
    
    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
    
    public BigDecimal getCreditLimit() {
        return creditLimit;
    }
    
    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }
    
    public String getBankAccountNumber() {
        return bankAccountNumber;
    }
    
    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public String getObservations() {
        return observations;
    }
    
    public void setObservations(String observations) {
        this.observations = observations;
    }
    
    public byte[] getImage() {
        return image;
    }
    
    public void setImage(byte[] image) {
        this.image = image;
    }
    
    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", dni='" + dni + '\'' +
                ", city='" + city + '\'' +
                ", province=" + province +
                ", active=" + active +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Client client = (Client) o;
        
        return dni != null ? dni.equals(client.dni) : client.dni == null;
    }
    
    @Override
    public int hashCode() {
        return dni != null ? dni.hashCode() : 0;
    }
}
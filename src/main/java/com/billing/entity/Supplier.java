package com.billing.entity;

import javax.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Supplier entity representing suppliers in the billing system
 * Corresponds to 'suppliers' table in 'facturacio' database
 */
@Entity
@Table(name = "suppliers")
public class Supplier implements Serializable {
    
    @Id
    @NotNull(message = "Supplier name cannot be null")
    @Size(max = 200, message = "Supplier name must not exceed 200 characters")
    @Column(name = "name", length = 200)
    private String name;
    
    @Size(max = 500, message = "Address must not exceed 500 characters")
    @Column(name = "address", length = 500)
    private String address;
    
    @Size(max = 20, message = "Phone must not exceed 20 characters")
    @Pattern(regexp = "^[0-9+\\s-()]*$", message = "Invalid phone format")
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Email(message = "Invalid email format")
    @Size(max = 100, message = "Email must not exceed 100 characters")
    @Column(name = "email", length = 100)
    private String email;
    
    @Size(max = 65535, message = "Observations cannot be too long")
    @Column(name = "observations", columnDefinition = "TEXT")
    private String observations;
    
    @NotNull(message = "Active status cannot be null")
    @Column(name = "active", nullable = false)
    private Boolean active = true;
    
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;
    
    // Constructors
    public Supplier() {
        this.active = true;
        this.createdDate = LocalDateTime.now();
    }
    
    public Supplier(String name) {
        this();
        this.name = name;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getObservations() {
        return observations;
    }
    
    public void setObservations(String observations) {
        this.observations = observations;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Supplier supplier = (Supplier) o;
        return Objects.equals(name, supplier.name);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
    
    @Override
    public String toString() {
        return name;
    }
}
package com.billing.entity;

import javax.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * Unit entity representing units of measurement in the billing system
 * Corresponds to 'units' table in 'facturacio' database
 */
@Entity
@Table(name = "units")
public class Unit implements Serializable {
    
    @Id
    @NotNull(message = "Unit symbol cannot be null")
    @Size(max = 10, message = "Unit symbol must not exceed 10 characters")
    @Column(name = "symbol", length = 10)
    private String symbol;
    
    @NotNull(message = "Unit name cannot be null")
    @Size(max = 100, message = "Unit name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;
    
    // Constructors
    public Unit() {}
    
    public Unit(String symbol, String name) {
        this.symbol = symbol;
        this.name = name;
    }
    
    // Getters and Setters
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Unit unit = (Unit) o;
        return Objects.equals(symbol, unit.symbol);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(symbol);
    }
    
    @Override
    public String toString() {
        return name != null ? name : symbol;
    }
}
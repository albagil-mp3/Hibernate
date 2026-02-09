package com.billing.entity;

import javax.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * ArticleFamily entity representing article families in the billing system
 * Corresponds to 'article_families' table in 'facturacio' database
 */
@Entity
@Table(name = "article_families")
public class ArticleFamily implements Serializable {
    
    @Id
    @NotNull(message = "Family code cannot be null")
    @Size(max = 10, message = "Family code must not exceed 10 characters")
    @Column(name = "code", length = 10)
    private String code;
    
    @NotNull(message = "Family name cannot be null")
    @Size(max = 100, message = "Family name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;
    
    // Constructors
    public ArticleFamily() {}
    
    public ArticleFamily(String code, String name) {
        this.code = code;
        this.name = name;
    }
    
    // Getters and Setters
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
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
        ArticleFamily family = (ArticleFamily) o;
        return Objects.equals(code, family.code);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
    
    @Override
    public String toString() {
        return name != null ? name : code;
    }
}
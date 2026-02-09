package com.billing.entity;

import javax.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * ArticleCategory entity representing article categories in the billing system
 * Corresponds to 'article_categories' table in 'facturacio' database
 */
@Entity
@Table(name = "article_categories")
public class ArticleCategory implements Serializable {
    
    @Id
    @NotNull(message = "Category code cannot be null")
    @Size(max = 10, message = "Category code must not exceed 10 characters")
    @Column(name = "code", length = 10)
    private String code;
    
    @NotNull(message = "Category name cannot be null")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;
    
    // Constructors
    public ArticleCategory() {}
    
    public ArticleCategory(String code, String name) {
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
        ArticleCategory category = (ArticleCategory) o;
        return Objects.equals(code, category.code);
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
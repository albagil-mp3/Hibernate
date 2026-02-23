package com.billing.model.item;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "item_categories")
public class ItemCategory implements Serializable {

    @Id
    @NotNull(message = "Category code cannot be null")
    @Size(max = 10, message = "Category code must not exceed 10 characters")
    @Column(name = "code", length = 10)
    private String code;

    @NotNull(message = "Category name cannot be null")
    @Size(max = 100, message = "Category name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;

    public ItemCategory() {}

    public ItemCategory(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemCategory category = (ItemCategory) o;
        return Objects.equals(code, category.code);
    }

    @Override
    public int hashCode() { return Objects.hash(code); }

    @Override
    public String toString() { return name != null ? name : code; }
}

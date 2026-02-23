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
@Table(name = "item_families")
public class ItemFamily implements Serializable {

    @Id
    @NotNull(message = "Family code cannot be null")
    @Size(max = 10, message = "Family code must not exceed 10 characters")
    @Column(name = "code", length = 10)
    private String code;

    @NotNull(message = "Family name cannot be null")
    @Size(max = 100, message = "Family name must not exceed 100 characters")
    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;

    public ItemFamily() {}

    public ItemFamily(String code, String name) {
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
        ItemFamily family = (ItemFamily) o;
        return Objects.equals(code, family.code);
    }

    @Override
    public int hashCode() { return Objects.hash(code); }

    @Override
    public String toString() { return name != null ? name : code; }
}

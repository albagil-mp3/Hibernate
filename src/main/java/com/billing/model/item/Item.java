package com.billing.model.item;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import com.billing.model.party.Supplier;

/**
 * Item / Product model
 */
@Entity
@Table(name = "item")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 64)
    private String code;

    @ManyToOne
    @JoinColumn(name = "family_code")
    private ItemFamily family;

    @ManyToOne
    @JoinColumn(name = "category_code")
    private ItemCategory category;

    @ManyToOne
    @JoinColumn(name = "unit_symbol")
    private Unit unit;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    private String description;

    @Column(name = "cost_price")
    private BigDecimal costPrice = BigDecimal.ZERO;

    @Column(name = "sale_price")
    private BigDecimal salePrice = BigDecimal.ZERO;

    @Column(name = "iva_percent")
    private Integer IVAPercent = 21;

    @Column(name = "current_stock")
    private Integer currentStock = 0;

    @Column(name = "minimum_stock")
    private Integer minimumStock = 0;

    @Column(length = 32)
    private String barcode;

    @Column(length = 500)
    private String observations;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(length = 500)
    private String image;

    @Column(name = "date_added")
    private LocalDateTime dateAdded;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public ItemFamily getFamily() { return family; }
    public void setFamily(ItemFamily family) { this.family = family; }

    public ItemCategory getCategory() { return category; }
    public void setCategory(ItemCategory category) { this.category = category; }

    public Unit getUnit() { return unit; }
    public void setUnit(Unit unit) { this.unit = unit; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }

    public BigDecimal getSalePrice() { return salePrice; }
    public void setSalePrice(BigDecimal salePrice) { this.salePrice = salePrice; }

    public Integer getIVAPercent() { return IVAPercent; }
    public void setIVAPercent(Integer IVAPercent) { this.IVAPercent = IVAPercent; }

    public Integer getCurrentStock() { return currentStock; }
    public void setCurrentStock(Integer currentStock) { this.currentStock = currentStock; }

    public Integer getMinimumStock() { return minimumStock; }
    public void setMinimumStock(Integer minimumStock) { this.minimumStock = minimumStock; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }

    public boolean isActive() { return active != null && active; }
    public void setActive(boolean active) { this.active = active; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public LocalDateTime getDateAdded() { return dateAdded; }
    public void setDateAdded(LocalDateTime dateAdded) { this.dateAdded = dateAdded; }

    public String getName() { return this.description; }
    public void setName(String name) { this.description = name; }

    public boolean isLowStock() {
        int current = currentStock == null ? 0 : currentStock;
        int minimum = minimumStock == null ? 0 : minimumStock;
        return current <= minimum;
    }

    @Override public String toString() { return description == null ? "Item#"+id : description; }
}

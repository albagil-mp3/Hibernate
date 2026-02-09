package com.billing.entity;

import javax.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Article entity representing products/articles in the billing system
 * Corresponds to 'articles' table in 'facturacio' database
 */
@Entity
@Table(name = "articles")
public class Article implements Serializable {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "code", insertable = false, updatable = false)
    private String code;
    
    @NotNull(message = "Article name cannot be null")
    @Size(max = 80, message = "Article name must not exceed 80 characters")
    @Column(name = "name", nullable = false, length = 80)
    private String name;
    
    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Column(name = "description", length = 500)
    private String description;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family", referencedColumnName = "code", nullable = false)
    @NotNull(message = "Family cannot be null")
    private ArticleFamily family;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category", referencedColumnName = "code", nullable = false)
    @NotNull(message = "Category cannot be null")
    private ArticleCategory category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unit", referencedColumnName = "symbol", nullable = false)
    @NotNull(message = "Unit cannot be null")
    private Unit unit;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier", referencedColumnName = "name", nullable = false)
    @NotNull(message = "Supplier cannot be null")
    private Supplier supplier;
    
    @NotNull(message = "Cost price cannot be null")
    @DecimalMin(value = "0.00", message = "Cost price must be greater than or equal to 0.00")
    @Column(name = "cost_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal costPrice;
    
    @NotNull(message = "Sale price cannot be null")
    @DecimalMin(value = "0.00", message = "Sale price must be greater than or equal to 0.00")
    @Column(name = "sale_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal salePrice;
    
    @NotNull(message = "IVA percentage cannot be null")
    @Min(value = 0, message = "IVA percentage must be 0, 4, 10, or 21")
    @Max(value = 21, message = "IVA percentage must be 0, 4, 10, or 21")
    @Column(name = "vat_percent", nullable = false)
    private Integer ivaPercent;
    
    @NotNull(message = "Current stock cannot be null")
    @Min(value = 0, message = "Current stock must be greater than or equal to 0")
    @Column(name = "current_stock", nullable = false)
    private Integer currentStock;
    
    @NotNull(message = "Minimum stock cannot be null")
    @Min(value = 0, message = "Minimum stock must be greater than or equal to 0")
    @Column(name = "minimum_stock", nullable = false)
    private Integer minimumStock;
    
    @Size(max = 13, message = "Barcode must not exceed 13 characters")
    @Pattern(regexp = "^[0-9]{13}$|^$", message = "Barcode must be 13 digits or empty")
    @Column(name = "barcode", length = 13)
    private String barcode;
    
    @NotNull(message = "Active status cannot be null")
    @Column(name = "active", nullable = false)
    private Boolean active = true;
    
    @Size(max = 500, message = "Image path must not exceed 500 characters")
    @Column(name = "image", length = 500)
    private String image;
    
    @NotNull(message = "Date added cannot be null")
    @Column(name = "date_added", nullable = false)
    private LocalDateTime dateAdded;
    
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    @Column(name = "observations", length = 500)
    private String observations;
    
    // Constructors
    public Article() {
        this.dateAdded = LocalDateTime.now();
        this.active = true;
        this.currentStock = 0;
        this.minimumStock = 0;
        this.ivaPercent = 21; // Default IVA
        this.costPrice = BigDecimal.ZERO;
        this.salePrice = BigDecimal.ZERO;
    }
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public ArticleFamily getFamily() {
        return family;
    }
    
    public void setFamily(ArticleFamily family) {
        this.family = family;
    }
    
    public ArticleCategory getCategory() {
        return category;
    }
    
    public void setCategory(ArticleCategory category) {
        this.category = category;
    }
    
    public Unit getUnit() {
        return unit;
    }
    
    public void setUnit(Unit unit) {
        this.unit = unit;
    }
    
    public Supplier getSupplier() {
        return supplier;
    }
    
    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
    
    public BigDecimal getCostPrice() {
        return costPrice;
    }
    
    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }
    
    public BigDecimal getSalePrice() {
        return salePrice;
    }
    
    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }
    
    public Integer getIVAPercent() {
        return ivaPercent;
    }
    
    public void setIVAPercent(Integer ivaPercent) {
        this.ivaPercent = ivaPercent;
    }
    
    public Integer getCurrentStock() {
        return currentStock;
    }
    
    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }
    
    public Integer getMinimumStock() {
        return minimumStock;
    }
    
    public void setMinimumStock(Integer minimumStock) {
        this.minimumStock = minimumStock;
    }
    
    public String getBarcode() {
        return barcode;
    }
    
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public boolean isActive() {
        return active != null && active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public String getImage() {
        return image;
    }
    
    public void setImage(String image) {
        this.image = image;
    }
    
    public LocalDateTime getDateAdded() {
        return dateAdded;
    }
    
    public void setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }
    
    public String getObservations() {
        return observations;
    }
    
    public void setObservations(String observations) {
        this.observations = observations;
    }
    
    // Business methods
    public BigDecimal getPriceWithIVA() {
        if (salePrice == null || ivaPercent == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal ivaMultiplier = BigDecimal.ONE.add(BigDecimal.valueOf(ivaPercent).divide(BigDecimal.valueOf(100)));
        return salePrice.multiply(ivaMultiplier);
    }
    
    public BigDecimal calculateProfitMargin() {
        if (costPrice == null || salePrice == null || costPrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        // Calculate margin as percentage: ((salePrice - costPrice) / costPrice) * 100
        BigDecimal profit = salePrice.subtract(costPrice);
        return profit.divide(costPrice, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
    }
    
    public boolean isLowStock() {
        return currentStock != null && minimumStock != null && currentStock <= minimumStock;
    }
    
    // Validation method
    public boolean isValidPricing() {
        return costPrice != null && salePrice != null && salePrice.compareTo(costPrice) >= 0;
    }
    
    // equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return Objects.equals(id, article.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", active=" + active +
                '}';
    }
}
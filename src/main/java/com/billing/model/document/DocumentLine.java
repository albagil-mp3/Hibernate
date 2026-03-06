package com.billing.model.document;

import java.math.BigDecimal;

import com.billing.model.item.Item;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Reusable document line for invoices, delivery notes, orders, etc.
 */
@Entity
@Table(name = "document_line")
public class DocumentLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    private Integer quantity = 1;

    @Column(name = "unit_price")
    private BigDecimal unitPrice = BigDecimal.ZERO;
    
    @Column(name = "tax_rate")
    private BigDecimal taxRate = BigDecimal.ZERO;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "document_id")
    private BusinessDocument document;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Item getItem() { return item; }
    public void setItem(Item item) { this.item = item; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }

    public BusinessDocument getDocument() { return document; }
    public void setDocument(BusinessDocument document) { this.document = document; }

    public java.math.BigDecimal lineTotal() {
        java.math.BigDecimal qty = BigDecimal.valueOf(quantity == null ? 0 : quantity);
        java.math.BigDecimal base = (unitPrice == null ? BigDecimal.ZERO : unitPrice).multiply(qty);
        java.math.BigDecimal tax = base.multiply(taxRate == null ? BigDecimal.ZERO : taxRate);
        return base.add(tax);
    }
}

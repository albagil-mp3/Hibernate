package com.billing.model.document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.billing.model.party.Party;

/**
 * Generic business document base class
 */
@Entity
@Table(name = "business_document")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class BusinessDocument {
    @jakarta.persistence.PrePersist
    @jakarta.persistence.PreUpdate
    private void onSave() {
        recalculateTotals();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "doc_date")
    protected LocalDate date = LocalDate.now();

    @Column(length = 64)
    protected String code;

    @ManyToOne
    @JoinColumn(name = "party_id")
    protected Party party;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true)
    protected List<DocumentLine> lines = new ArrayList<>();

    protected BigDecimal subtotal = BigDecimal.ZERO;
    protected BigDecimal taxes = BigDecimal.ZERO;
    protected BigDecimal total = BigDecimal.ZERO;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Party getParty() { return party; }
    public void setParty(Party party) { this.party = party; }
    public List<DocumentLine> getLines() { return lines; }
    
    public void setLines(List<DocumentLine> newLines) {
    this.lines.clear();

    if (newLines != null) {
        for (DocumentLine l : newLines) {
            addLine(l);
            }
        }
        recalculateTotals();
    }

    public void addLine(DocumentLine line) {
        if (line == null) return;

        if (this.lines == null)
            this.lines = new java.util.ArrayList<>();

        this.lines.add(line);
        line.setDocument(this);
        recalculateTotals();
    }

    public void removeLine(DocumentLine line) {
        if (line == null || lines == null) return;

        this.lines.remove(line);
        line.setDocument(null);
        recalculateTotals();
    }


    public void recalculateTotals() {

        if (lines == null || lines.isEmpty()) {
            subtotal = BigDecimal.ZERO;
            taxes = BigDecimal.ZERO;
            total = BigDecimal.ZERO;
            return;
        }

        BigDecimal newSubtotal = BigDecimal.ZERO;
        BigDecimal newTaxes = BigDecimal.ZERO;

        for (DocumentLine l : lines) {
            if (l == null) continue;

            BigDecimal qty = BigDecimal.valueOf(l.getQuantity() == null ? 0 : l.getQuantity());
            BigDecimal price = l.getUnitPrice() == null ? BigDecimal.ZERO : l.getUnitPrice();
            BigDecimal taxRate = l.getTaxRate() == null ? BigDecimal.ZERO : l.getTaxRate();

            BigDecimal base = price.multiply(qty);
            BigDecimal tax = base.multiply(taxRate);

            newSubtotal = newSubtotal.add(base);
            newTaxes = newTaxes.add(tax);
        }

        this.subtotal = newSubtotal.setScale(2, java.math.RoundingMode.HALF_UP);
        this.taxes = newTaxes.setScale(2, java.math.RoundingMode.HALF_UP);
        this.total = subtotal.add(taxes).setScale(2, java.math.RoundingMode.HALF_UP);
    }

    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getTaxes() { return taxes; }
    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }
}

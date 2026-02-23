package com.billing.model.party;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/**
 * Supplier model (inherits Party).
 */
@Entity
@Table(name = "supplier")
@PrimaryKeyJoinColumn(name = "id")
public class Supplier extends Party {
    @Column(name = "tax_id", length = 64)
    protected String taxId;

    @Column(length = 32, unique = true)
    protected String code;

    @Column(nullable = false)
    protected Boolean active = true;

    public Supplier() {}
    public Supplier(String name) { this.name = name; }

    public String getTaxId() { return taxId; }
    public void setTaxId(String taxId) { this.taxId = taxId; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    @Override public String toString() { return "Supplier{" + "id=" + id + ", name='" + name + '\'' + '}'; }
}

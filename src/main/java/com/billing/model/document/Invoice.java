package com.billing.model.document;

import com.billing.model.party.Client;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

/**
 * Sales invoice document
 */
@Entity
@Table(name = "invoice")
@PrimaryKeyJoinColumn(name = "id")
public class Invoice extends SalesDocument {
    public Invoice() {}
    public Invoice(SalesDocument other) {
        this.date = other.date;
        this.party = other.party;
        this.lines = other.lines;
        recalculateTotals();
    }
    @Override public String toString() { return "Invoice#" + id; }
    public Client getCustomer() { return getClient(); }
}

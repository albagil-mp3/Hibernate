package com.billing.model.document;

import com.billing.model.party.Client;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Column;

/**
 * Delivery note for sales
 */
@Entity
@Table(name = "delivery_note")
@PrimaryKeyJoinColumn(name = "id")
public class DeliveryNote extends SalesDocument {
    @Column(name = "delivered")
    private Boolean delivered = false;

    @Column(name = "invoiced")
    private Boolean invoiced = false;

    public DeliveryNote() {}
    public DeliveryNote(SalesDocument other) {
        this.date = other.date;
        this.party = other.party;
        this.lines = other.lines;
        recalculateTotals();
    }
    @Override public String toString() { return "DeliveryNote#" + id; }
    public Client getCustomer() { return getClient(); }
    public Boolean getDelivered() { return delivered; }
    public void setDelivered(Boolean delivered) { this.delivered = delivered; }
    public Boolean getInvoiced() { return invoiced; }
    public void setInvoiced(Boolean invoiced) { this.invoiced = invoiced; }
}

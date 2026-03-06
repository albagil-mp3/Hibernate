package com.billing.model.document;

import com.billing.model.party.Client;
import com.billing.model.party.Party;

/**
 * Convenience methods for sales documents to access client as party
 */
public abstract class SalesDocument extends BusinessDocument {
	public Client getClient() {
        Party p = this.party;
        return p instanceof Client ? (Client) p : null;
    }
	public void setClient(Client client) { this.party = client; }
}

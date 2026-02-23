package com.billing.service;

import java.util.List;

import com.billing.model.party.Client;
import com.billing.model.party.Supplier;
import com.billing.repository.interfaces.PartyRepository;

public class CodeGenerator {

    private final PartyRepository partyRepository;

    public CodeGenerator(PartyRepository partyRepository) {
        this.partyRepository = partyRepository;
    }

    public String nextClientCode() {
        return nextCode("CLI", partyRepository.findAllClients());
    }

    public String nextSupplierCode() {
        return nextCode("SUP", partyRepository.findAllSuppliers());
    }

    public int resequenceClients() {
        List<Client> clients = partyRepository.findAllClientsOrderedByName();
        int n = 1;
        for (Client c : clients) {
            partyRepository.updateClientCode(c.getId(), format("CLI", n++));
        }
        return clients.size();
    }

    public int resequenceSuppliers() {
        List<Supplier> suppliers = partyRepository.findAllSuppliersOrderedByName();
        int n = 1;
        for (Supplier s : suppliers) {
            partyRepository.updateSupplierCode(s.getId(), format("SUP", n++));
        }
        return suppliers.size();
    }

    private String nextCode(String prefix, List<? extends Object> parties) {
        int max = 0;
        for (Object p : parties) {
            String code = null;
            if (p instanceof Client) code = ((Client) p).getCode();
            if (p instanceof Supplier) code = ((Supplier) p).getCode();
            int n = parseCode(prefix, code);
            if (n > max) max = n;
        }
        return format(prefix, max + 1);
    }

    private int parseCode(String prefix, String code) {
        if (code == null) return 0;
        if (!code.startsWith(prefix)) return 0;
        String num = code.substring(prefix.length());
        if (!num.matches("\\d+")) return 0;
        try {
            return Integer.parseInt(num);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String format(String prefix, int n) {
        return String.format("%s%04d", prefix, n);
    }
}

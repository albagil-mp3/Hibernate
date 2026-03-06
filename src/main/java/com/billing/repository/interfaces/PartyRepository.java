package com.billing.repository.interfaces;

import java.util.List;

import com.billing.model.party.Client;
import com.billing.model.party.Supplier;

public interface PartyRepository {
    Client saveClient(Client client);
    Client updateClient(Client client);
    void deleteClient(Client client);
    Client findClientById(Long id);
    Client findClientByDni(String dni);
    List<Client> findAllClients();
    List<Client> findAllClientsOrderedByCode();
    List<Client> findAllClientsOrderedByDni();
    List<Client> findAllClientsOrderedByName();
    List<Client> findActiveClients();
    List<Client> findInactiveClients();
    List<Client> searchClients(String searchTerm);
    boolean existsClientByDni(String dni);
    long countClients();
    void updateClientCode(Long id, String code);

    Supplier saveSupplier(Supplier supplier);
    Supplier updateSupplier(Supplier supplier);
    void deleteSupplier(Supplier supplier);
    Supplier findSupplierById(Long id);
    Supplier findSupplierByCode(String code);
    List<Supplier> findAllSuppliers();
    List<Supplier> findSuppliersByName(String name);
    List<Supplier> findAllSuppliersOrderedByName();
    List<Supplier> findAllActiveSuppliers();
    List<Supplier> findAllInactiveSuppliers();
    long countSuppliers();
    void updateSupplierCode(Long id, String code);
}

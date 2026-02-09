package com.billing.dao;

import com.billing.entity.Client;
import java.util.List;

/**
 * Data Access Object interface for Client entity
 * Extends GenericDAO with additional client-specific operations
 */
public interface ClientDAO extends GenericDAO<Client, Integer> {
    
    /**
     * Finds a client by DNI
     * @param dni DNI to search for
     * @return Client with the specified DNI or null if not found
     */
    Client findByDni(String dni);
    
    /**
     * Finds clients by name (case-insensitive partial match)
     * @param name Name to search for
     * @return List of clients matching the name
     */
    List<Client> findByName(String name);
    
    /**
     * Finds clients by phone number (fixed or mobile)
     * @param phone Phone number to search for
     * @return List of clients with the specified phone
     */
    List<Client> findByPhone(String phone);
    
    /**
     * Finds clients by website
     * @param website Website to search for
     * @return List of clients with the specified website
     */
    List<Client> findByWebsite(String website);
    
    /**
     * Finds all active clients
     * @return List of active clients
     */
    List<Client> findActiveClients();
    
    /**
     * Finds all inactive clients
     * @return List of inactive clients
     */
    List<Client> findInactiveClients();
    
    /**
     * Finds clients ordered by ID
     * @return List of clients ordered by ID
     */
    List<Client> findAllOrderedById();
    
    /**
     * Finds clients ordered by DNI
     * @return List of clients ordered by DNI
     */
    List<Client> findAllOrderedByDni();
    
    /**
     * Finds clients ordered by name
     * @return List of clients ordered by name
     */
    List<Client> findAllOrderedByName();
    
    /**
     * Checks if DNI already exists in database
     * @param dni DNI to check
     * @return true if DNI exists
     */
    boolean existsByDni(String dni);
    
    /**
     * Searches clients by multiple criteria
     * @param searchTerm Search term to match against name, DNI, phone, or website
     * @return List of clients matching the search criteria
     */
    List<Client> searchClients(String searchTerm);

    /**
     * Update the code field for a client by id. Used when the code is generated after insert.
     * @param id Client id
     * @param code Generated code to set
     */
    void updateCode(Integer id, String code);
}
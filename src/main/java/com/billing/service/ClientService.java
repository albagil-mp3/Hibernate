package com.billing.service;

import com.billing.dao.ClientDAO;
import com.billing.dao.ClientDAOImpl;
import com.billing.entity.Client;
import com.billing.entity.SpanishProvince;
import com.billing.util.SpanishValidationUtil;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Service layer for Client operations
 * Handles business logic and validation before delegating to DAO
 */
public class ClientService {
    
    private static final Logger logger = Logger.getLogger(ClientService.class.getName());
    private final ClientDAO clientDAO;
    private final Validator validator;
    
    public ClientService() {
        this.clientDAO = new ClientDAOImpl();
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
        
        // TODO: Implement Hibernate availability check if needed
        // logger.warning("Database is not available. Some features will be disabled.");
    }
    
    /**
     * Creates a new client with validation
     * @param client Client to create
     * @return Created client
     * @throws IllegalArgumentException if validation fails
     */
    public Client createClient(Client client) {
        validateClient(client);
        
        // Additional business validations
        if (clientDAO.existsByDni(client.getDni())) {
            throw new IllegalArgumentException("Client with DNI " + client.getDni() + " already exists");
        }
        
        // Validate DNI format and letter
        if (!SpanishValidationUtil.isValidDNI(client.getDni())) {
            throw new IllegalArgumentException("Invalid DNI: letter does not match number");
        }
        
        // Validate postal code matches province
        if (client.getPostalCode() != null && client.getProvince() != null) {
            if (!SpanishValidationUtil.isValidPostalCodeForProvince(client.getPostalCode(), client.getProvince())) {
                throw new IllegalArgumentException("Postal code does not match the selected province");
            }
        }
        
        // Format DNI properly
        client.setDni(SpanishValidationUtil.formatDNI(client.getDni()));
        
        logger.info("Creating new client: " + client.getName());
        return clientDAO.save(client);
    }
    
    /**
     * Updates an existing client with validation
     * @param client Client to update
     * @return Updated client
     * @throws IllegalArgumentException if validation fails
     */
    public Client updateClient(Client client) {
        validateClient(client);
        
        if (client.getId() == null) {
            throw new IllegalArgumentException("Client ID cannot be null for update operation");
        }
        
        // Check if client exists
        Client existingClient = clientDAO.findById(client.getId());
        if (existingClient == null) {
            throw new IllegalArgumentException("Client with ID " + client.getId() + " not found");
        }
        
        // Check if DNI is being changed and if new DNI already exists
        if (!existingClient.getDni().equals(client.getDni()) && clientDAO.existsByDni(client.getDni())) {
            throw new IllegalArgumentException("Client with DNI " + client.getDni() + " already exists");
        }
        
        // Validate DNI format and letter
        if (!SpanishValidationUtil.isValidDNI(client.getDni())) {
            throw new IllegalArgumentException("Invalid DNI: letter does not match number");
        }
        
        // Validate postal code matches province
        if (client.getPostalCode() != null && client.getProvince() != null) {
            if (!SpanishValidationUtil.isValidPostalCodeForProvince(client.getPostalCode(), client.getProvince())) {
                throw new IllegalArgumentException("Postal code does not match the selected province");
            }
        }
        
        // Format DNI properly
        client.setDni(SpanishValidationUtil.formatDNI(client.getDni()));
        
        logger.info("Updating client: " + client.getName());
        return clientDAO.update(client);
    }
    
    /**
     * Deletes a client by ID
     * @param id Client ID to delete
     * @throws IllegalArgumentException if client not found
     */
    public void deleteClient(Integer id) {
        Client client = clientDAO.findById(id);
        if (client == null) {
            throw new IllegalArgumentException("Client with ID " + id + " not found");
        }
        
        logger.info("Deleting client: " + client.getName());
        clientDAO.delete(client);
    }
    
    /**
     * Finds a client by ID
     * @param id Client ID
     * @return Client or null if not found
     */
    public Client findClientById(Integer id) {
        return clientDAO.findById(id);
    }
    
    /**
     * Finds a client by DNI
     * @param dni DNI to search for
     * @return Client or null if not found
     */
    public Client findClientByDni(String dni) {
        return clientDAO.findByDni(dni);
    }
    
    /**
     * Gets all clients
     * @return List of all clients
     */
    public List<Client> getAllClients() {
        return clientDAO.findAll();
    }
    
    /**
     * Gets all clients ordered by ID
     * @return List of clients ordered by ID
     */
    public List<Client> getAllClientsOrderedById() {
        return clientDAO.findAllOrderedById();
    }
    
    /**
     * Gets all clients ordered by DNI
     * @return List of clients ordered by DNI
     */
    public List<Client> getAllClientsOrderedByDni() {
        return clientDAO.findAllOrderedByDni();
    }
    
    /**
     * Gets all clients ordered by name
     * @return List of clients ordered by name
     */
    public List<Client> getAllClientsOrderedByName() {
        return clientDAO.findAllOrderedByName();
    }
    
    /**
     * Searches clients by name, DNI, phone, or website
     * @param searchTerm Term to search for
     * @return List of matching clients
     */
    public List<Client> searchClients(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllClients();
        }
        return clientDAO.searchClients(searchTerm.trim());
    }
    
    /**
     * Gets all active clients
     * @return List of active clients
     */
    public List<Client> getActiveClients() {
        return clientDAO.findActiveClients();
    }
    
    /**
     * Gets all inactive clients
     * @return List of inactive clients
     */
    public List<Client> getInactiveClients() {
        return clientDAO.findInactiveClients();
    }
    
    /**
     * Activates a client
     * @param id Client ID to activate
     * @return Updated client
     */
    public Client activateClient(Integer id) {
        Client client = findClientById(id);
        if (client == null) {
            throw new IllegalArgumentException("Client with ID " + id + " not found");
        }
        
        client.setActive(true);
        return updateClient(client);
    }
    
    /**
     * Deactivates a client
     * @param id Client ID to deactivate
     * @return Updated client
     */
    public Client deactivateClient(Integer id) {
        Client client = findClientById(id);
        if (client == null) {
            throw new IllegalArgumentException("Client with ID " + id + " not found");
        }
        
        client.setActive(false);
        return updateClient(client);
    }
    
    /**
     * Gets total number of clients
     * @return Total count
     */
    public long getTotalClientCount() {
        return clientDAO.count();
    }
    
    /**
     * Validates client using bean validation
     * @param client Client to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateClient(Client client) {
        Set<ConstraintViolation<Client>> violations = validator.validate(client);
        if (!violations.isEmpty()) {
            StringBuilder sb = new StringBuilder("Validation errors:\n");
            for (ConstraintViolation<Client> violation : violations) {
                sb.append("- ").append(violation.getPropertyPath()).append(": ")
                  .append(violation.getMessage()).append("\n");
            }
            throw new IllegalArgumentException(sb.toString());
        }
    }
    
    /**
     * Gets province by postal code
     * @param postalCode Postal code to check
     * @return SpanishProvince that matches or null
     */
    public SpanishProvince getProvinceByPostalCode(String postalCode) {
        return SpanishProvince.getByPostalCode(postalCode);
    }
}
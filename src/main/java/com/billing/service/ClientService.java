package com.billing.service;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.billing.model.SpanishProvince;
import com.billing.model.party.Client;
import com.billing.dto.ClientImportDTO;
import com.billing.repository.RepositoryFactory;
import com.billing.repository.interfaces.PartyRepository;
import com.billing.service.validation.SpanishValidationUtil;
import com.billing.tx.HibernateTransactionManager;
import com.billing.tx.TransactionManager;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

/**
 * Service layer for Client operations
 * Handles business logic and validation before delegating to DAO
 */
public class ClientService {
    
    private static final Logger logger = Logger.getLogger(ClientService.class.getName());
    private final PartyRepository partyRepository;
    private final TransactionManager tx;
    private final CodeGenerator codeGenerator;
    private final Validator validator;

    /**
     * Constructor: obtains DAO implementation exclusively from RepositoryFactory.
     */
    public ClientService() {
        this.partyRepository = RepositoryFactory.createPartyRepository();
        this.tx = new HibernateTransactionManager();
        this.codeGenerator = new CodeGenerator(partyRepository);
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
        logger.info("ClientService initialized with PartyRepository");
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
        if (partyRepository.existsClientByDni(client.getDni())) {
            throw new IllegalArgumentException("Client with DNI " + client.getDni() + " already exists");
        }
        
        // Validate DNI format and letter
        if (!SpanishValidationUtil.isValidDNI(client.getDni())) {
            throw new IllegalArgumentException("Invalid DNI: letter does not match number");
        }
        
        // Validate postal code and infer/verify province
        if (client.getPostalCode() != null) {
            SpanishProvince inferred = SpanishProvince.getByPostalCode(client.getPostalCode());
            if (inferred == null) {
                throw new IllegalArgumentException("Postal code is invalid or does not match any province");
            }
            if (client.getProvince() == null) {
                client.setProvince(inferred);
            } else if (!inferred.equals(client.getProvince())) {
                logger.warning(() -> "Postal code disagrees with provided province; overriding with " + inferred);
                client.setProvince(inferred);
            }
        }
        
        // Format DNI properly
        client.setDni(SpanishValidationUtil.formatDNI(client.getDni()));
        
        logger.info(() -> "Creating new client: " + client.getName());
        return tx.runInTransaction(() -> {
            client.setCode(codeGenerator.nextClientCode());
            return partyRepository.saveClient(client);
        });
    }

    /**
     * Regenerate codes for all clients (useful after bulk import).
     * Returns the number of updated rows.
     */
    public int regenerateAllClientCodes() {
        return tx.runInTransaction(() -> {
            int updated = codeGenerator.resequenceClients();
            logger.info("Regenerated codes for clients: " + updated);
            return updated;
        });
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
        Client existingClient = partyRepository.findClientById(client.getId());
        if (existingClient == null) {
            throw new IllegalArgumentException("Client with ID " + client.getId() + " not found");
        }
        
        // Check if DNI is being changed and if new DNI already exists
        if (!existingClient.getDni().equals(client.getDni()) && partyRepository.existsClientByDni(client.getDni())) {
            throw new IllegalArgumentException("Client with DNI " + client.getDni() + " already exists");
        }
        
        // Validate DNI format and letter
        if (!SpanishValidationUtil.isValidDNI(client.getDni())) {
            throw new IllegalArgumentException("Invalid DNI: letter does not match number");
        }
        
        // Validate postal code and infer/verify province
        if (client.getPostalCode() != null) {
            SpanishProvince inferred = SpanishProvince.getByPostalCode(client.getPostalCode());
            if (inferred == null) {
                throw new IllegalArgumentException("Postal code is invalid or does not match any province");
            }
            if (client.getProvince() == null) {
                client.setProvince(inferred);
            } else if (!inferred.equals(client.getProvince())) {
                logger.warning(() -> "Postal code disagrees with provided province; overriding with " + inferred);
                client.setProvince(inferred);
            }
        }
        
        // Format DNI properly
        client.setDni(SpanishValidationUtil.formatDNI(client.getDni()));
        
        logger.info(() -> "Updating client: " + client.getName());
        return tx.runInTransaction(() -> partyRepository.updateClient(client));
    }
    
    /**
     * Deletes a client by ID
     * @param id Client ID to delete
     * @throws IllegalArgumentException if client not found
     */
    public void deleteClient(Long id) {
        Client client = partyRepository.findClientById(id);
        if (client == null) {
            throw new IllegalArgumentException("Client with ID " + id + " not found");
        }
        
        logger.info(() -> "Deleting client: " + client.getName());
        tx.runInTransaction(() -> partyRepository.deleteClient(client));
    }
    
    /**
     * Finds a client by ID
     * @param id Client ID
     * @return Client or null if not found
     */
    public Client findClientById(Long id) {
        return tx.runInTransaction(() -> partyRepository.findClientById(id));
    }
    
    /**
     * Finds a client by DNI
     * @param dni DNI to search for
     * @return Client or null if not found
     */
    public Client findClientByDni(String dni) {
        return tx.runInTransaction(() -> partyRepository.findClientByDni(dni));
    }
    
    /**
     * Gets all clients
     * @return List of all clients
     */
    public List<Client> getAllClients() {
        return tx.runInTransaction(() -> partyRepository.findAllClients());
    }
    
    /**
     * Gets all clients ordered by CODE
     * @return List of clients ordered by CODE
     */
    public List<Client> getAllClientsOrderedByCode() {
        return tx.runInTransaction(() -> partyRepository.findAllClientsOrderedByCode());
    }
    
    /**
     * Gets all clients ordered by DNI
     * @return List of clients ordered by DNI
     */
    public List<Client> getAllClientsOrderedByDni() {
        return tx.runInTransaction(() -> partyRepository.findAllClientsOrderedByDni());
    }
    
    /**
     * Gets all clients ordered by name
     * @return List of clients ordered by name
     */
    public List<Client> getAllClientsOrderedByName() {
        return tx.runInTransaction(() -> partyRepository.findAllClientsOrderedByName());
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
        return tx.runInTransaction(() -> partyRepository.searchClients(searchTerm.trim()));
    }
    
    /**
     * Gets all active clients
     * @return List of active clients
     */
    public List<Client> getActiveClients() {
        return tx.runInTransaction(() -> partyRepository.findActiveClients());
    }
    
    /**
     * Gets all inactive clients
     * @return List of inactive clients
     */
    public List<Client> getInactiveClients() {
        return tx.runInTransaction(() -> partyRepository.findInactiveClients());
    }
    
    /**
     * Activates a client
     * @param id Client ID to activate
     * @return Updated client
     */
    public Client activateClient(Long id) {
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
    public Client deactivateClient(Long id) {
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
        return tx.runInTransaction(() -> partyRepository.countClients());
    }

    public ImportResult importClients(List<ClientImportDTO> input) {
        if (input == null) return new ImportResult(0, 0, 0);
        return tx.runInTransaction(() -> {
            int imported = 0;
            int skipped = 0;
            int failed = 0;
            for (ClientImportDTO dto : input) {
                try {
                    Client client = mapFromDto(dto);
                    if (client.getDni() != null && partyRepository.existsClientByDni(client.getDni())) {
                        skipped++;
                        continue;
                    }
                    createClient(client);
                    imported++;
                } catch (Exception e) {
                    failed++;
                }
            }
            return new ImportResult(imported, skipped, failed);
        });
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

    private Client mapFromDto(ClientImportDTO dto) {
        if (dto == null) throw new IllegalArgumentException("Client data cannot be null");
        Client c = new Client();
        c.setDni(dto.dni);
        c.setName(dto.name);
        c.setAddress(dto.address);
        c.setEmail(dto.email);
        c.setCity(dto.city);
        if (dto.province != null) {
            try {
                c.setProvince(SpanishProvince.valueOf(dto.province));
            } catch (IllegalArgumentException ignored) {
                // validation will handle invalid province
            }
        }
        c.setPostalCode(dto.postalCode);
        c.setFixedPhone(dto.fixedPhone);
        c.setMobilePhone(dto.mobilePhone);
        c.setWebsite(dto.website);
        if (dto.paymentMethod != null) {
            try {
                c.setPaymentMethod(com.billing.model.PaymentMethod.valueOf(dto.paymentMethod));
            } catch (IllegalArgumentException ignored) {
                // validation will handle invalid payment method
            }
        }
        c.setCreditLimit(dto.creditLimit);
        c.setBankAccountNumber(dto.bankAccountNumber);
        c.setActive(dto.active);
        c.setObservations(dto.observations);
        return c;
    }
}

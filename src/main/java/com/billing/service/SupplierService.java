package com.billing.service;

import java.util.List;
import java.util.logging.Logger;

import com.billing.dto.SupplierImportDTO;
import com.billing.model.party.Supplier;
import com.billing.repository.RepositoryFactory;
import com.billing.repository.interfaces.PartyRepository;
import com.billing.tx.HibernateTransactionManager;
import com.billing.tx.TransactionManager;

/**
 * Service layer for Supplier operations.
 */
public class SupplierService {

    private static final Logger logger = Logger.getLogger(SupplierService.class.getName());
    private final PartyRepository partyRepository;
    private final TransactionManager tx;
    private final CodeGenerator codeGenerator;

    public SupplierService() {
        this.partyRepository = RepositoryFactory.createPartyRepository();
        this.tx = new HibernateTransactionManager();
        this.codeGenerator = new CodeGenerator(partyRepository);
        logger.info("SupplierService initialized with PartyRepository");
    }

    public Supplier createSupplier(Supplier supplier) {
        validateSupplier(supplier);
        return tx.runInTransaction(() -> {
            supplier.setCode(codeGenerator.nextSupplierCode());
            return partyRepository.saveSupplier(supplier);
        });
    }

    public Supplier updateSupplier(Supplier supplier) {
        validateSupplier(supplier);
        if (supplier.getId() == null) {
            throw new IllegalArgumentException("Supplier ID cannot be null for update operation");
        }
        return tx.runInTransaction(() -> partyRepository.updateSupplier(supplier));
    }

    public void deleteSupplier(Long id) {
        Supplier supplier = findSupplierById(id);
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier with ID " + id + " not found");
        }
        tx.runInTransaction(() -> partyRepository.deleteSupplier(supplier));
    }

    public Supplier findSupplierById(Long id) {
        return tx.runInTransaction(() -> partyRepository.findSupplierById(id));
    }

    public Supplier findSupplierByCode(String code) {
        return tx.runInTransaction(() -> partyRepository.findSupplierByCode(code));
    }

    public List<Supplier> getAllSuppliers() {
        return tx.runInTransaction(() -> partyRepository.findAllSuppliers());
    }

    public List<Supplier> getAllActiveSuppliers() {
        return tx.runInTransaction(() -> partyRepository.findAllActiveSuppliers());
    }

    public List<Supplier> getAllInactiveSuppliers() {
        return tx.runInTransaction(() -> partyRepository.findAllInactiveSuppliers());
    }

    public int regenerateAllSupplierCodes() {
        return tx.runInTransaction(() -> {
            int updated = codeGenerator.resequenceSuppliers();
            logger.info("Regenerated codes for suppliers: " + updated);
            return updated;
        });
    }

    public ImportResult importSuppliers(List<SupplierImportDTO> input) {
        if (input == null) return new ImportResult(0, 0, 0);
        return tx.runInTransaction(() -> {
            int imported = 0;
            int skipped = 0;
            int failed = 0;
            for (SupplierImportDTO dto : input) {
                try {
                    Supplier supplier = mapFromDto(dto);
                    createSupplier(supplier);
                    imported++;
                } catch (Exception e) {
                    failed++;
                }
            }
            return new ImportResult(imported, skipped, failed);
        });
    }

    private void validateSupplier(Supplier supplier) {
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier cannot be null");
        }
        if (supplier.getName() == null || supplier.getName().isBlank()) {
            throw new IllegalArgumentException("Supplier name cannot be null or empty");
        }
    }

    private Supplier mapFromDto(SupplierImportDTO dto) {
        if (dto == null) throw new IllegalArgumentException("Supplier data cannot be null");
        Supplier s = new Supplier();
        s.setName(dto.name);
        s.setTaxId(dto.taxId);
        s.setAddress(dto.address);
        s.setEmail(dto.email);
        s.setCity(dto.city);
        if (dto.province != null) {
            try {
                s.setProvince(com.billing.model.SpanishProvince.valueOf(dto.province));
            } catch (IllegalArgumentException ignored) {
                // validation will handle invalid province
            }
        }
        s.setPostalCode(dto.postalCode);
        s.setFixedPhone(dto.fixedPhone);
        s.setMobilePhone(dto.mobilePhone);
        s.setWebsite(dto.website);
        if (dto.active != null) {
            s.setActive(dto.active);
        }
        return s;
    }
}

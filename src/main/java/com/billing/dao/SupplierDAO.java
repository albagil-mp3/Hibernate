package com.billing.dao;

import com.billing.entity.Supplier;
import java.util.List;

/**
 * Data Access Object interface for Supplier operations
 * Extends GenericDAO to provide standard CRUD operations
 */
public interface SupplierDAO extends GenericDAO<Supplier, String> {
    
    /**
     * Find suppliers by name (partial match, case insensitive)
     * @param name The name to search for
     * @return List of suppliers matching the name criteria
     */
    List<Supplier> findByName(String name);
    
    /**
     * Get all active suppliers
     * @return List of active suppliers
     */
    List<Supplier> findAllActive();
    
    /**
     * Get all inactive suppliers
     * @return List of inactive suppliers
     */
    List<Supplier> findAllInactive();
}
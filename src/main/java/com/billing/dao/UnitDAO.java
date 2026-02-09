package com.billing.dao;

import com.billing.entity.Unit;
import java.util.List;

/**
 * Data Access Object interface for Unit operations
 * Extends GenericDAO to provide standard CRUD operations
 */
public interface UnitDAO extends GenericDAO<Unit, String> {
    
    /**
     * Find units by name (partial match, case insensitive)
     * @param name The name to search for
     * @return List of units matching the name criteria
     */
    List<Unit> findByName(String name);
}
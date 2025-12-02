package com.billing.dao;

import java.util.List;

/**
 * Generic DAO interface for basic CRUD operations
 * @param <T> Entity type
 * @param <ID> Primary key type
 */
public interface GenericDAO<T, ID> {
    
    /**
     * Saves or updates an entity
     * @param entity Entity to save
     * @return Saved entity
     */
    T save(T entity);
    
    /**
     * Finds an entity by its ID
     * @param id Primary key
     * @return Entity or null if not found
     */
    T findById(ID id);
    
    /**
     * Retrieves all entities
     * @return List of all entities
     */
    List<T> findAll();
    
    /**
     * Updates an existing entity
     * @param entity Entity to update
     * @return Updated entity
     */
    T update(T entity);
    
    /**
     * Deletes an entity
     * @param entity Entity to delete
     */
    void delete(T entity);
    
    /**
     * Deletes an entity by its ID
     * @param id Primary key of entity to delete
     */
    void deleteById(ID id);
    
    /**
     * Checks if an entity exists by ID
     * @param id Primary key
     * @return true if entity exists
     */
    boolean existsById(ID id);
    
    /**
     * Counts total number of entities
     * @return Total count
     */
    long count();
}
package com.billing.repository;

import java.util.List;

/**
 * Generic repository interface
 */
public interface Repository<T, ID> {
    T find(ID id);
    List<T> findAll();
    T save(T entity);
    T update(T entity);
    void delete(T entity);
    long count();
}

package com.billing.dao;

import com.billing.entity.ArticleFamily;
import java.util.List;

/**
 * Data Access Object interface for ArticleFamily operations
 * Extends GenericDAO to provide standard CRUD operations
 */
public interface ArticleFamilyDAO extends GenericDAO<ArticleFamily, String> {
    
    /**
     * Find families by name (partial match, case insensitive)
     * @param name The name to search for
     * @return List of families matching the name criteria
     */
    List<ArticleFamily> findByName(String name);
}
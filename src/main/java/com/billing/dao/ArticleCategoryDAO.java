package com.billing.dao;

import com.billing.entity.ArticleCategory;
import java.util.List;

/**
 * Data Access Object interface for ArticleCategory operations
 * Extends GenericDAO to provide standard CRUD operations
 */
public interface ArticleCategoryDAO extends GenericDAO<ArticleCategory, String> {
    
    /**
     * Find categories by name (partial match, case insensitive)
     * @param name The name to search for
     * @return List of categories matching the name criteria
     */
    List<ArticleCategory> findByName(String name);
}
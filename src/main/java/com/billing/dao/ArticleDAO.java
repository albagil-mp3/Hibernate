package com.billing.dao;

import com.billing.entity.Article;
import java.util.List;

/**
 * Data Access Object interface for Article operations
 * Extends GenericDAO to provide standard CRUD operations
 */
public interface ArticleDAO extends GenericDAO<Article, Integer> {
    
    /**
     * Find articles by name (partial match, case insensitive)
     * @param name The name to search for
     * @return List of articles matching the name criteria
     */
    List<Article> findByName(String name);
    
    /**
     * Find an article by barcode
     * @param barcode The barcode to search for
     * @return The article with the specified barcode, or null if not found
     */
    Article findByBarcode(String barcode);
    
    /**
     * Get all active articles
     * @return List of active articles
     */
    List<Article> findAllActive();
    
    /**
     * Get all inactive articles
     * @return List of inactive articles
     */
    List<Article> findAllInactive();
    
    /**
     * Find articles with low stock (current stock <= minimum stock)
     * @return List of articles with low stock
     */
    List<Article> findLowStockArticles();
    
    /**
     * Find articles by supplier ID
     * @param supplierId The supplier ID to search for
     * @return List of articles from the specified supplier
     */
    List<Article> findBySupplier(Integer supplierId);
    
    /**
     * Find articles by family ID
     * @param familyId The family ID to search for
     * @return List of articles from the specified family
     */
    List<Article> findByFamily(Integer familyId);
    
    /**
     * Find articles by category ID
     * @param categoryId The category ID to search for
     * @return List of articles from the specified category
     */
    List<Article> findByCategory(Integer categoryId);
    
    /**
     * Find articles within a price range
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @return List of articles within the specified price range
     */
    List<Article> findByPriceRange(java.math.BigDecimal minPrice, java.math.BigDecimal maxPrice);

    /**
     * Update the code field for an article by id. Used when the code is generated after insert.
     * @param id Article id
     * @param code Generated code to set
     */
    void updateCode(Integer id, String code);
}
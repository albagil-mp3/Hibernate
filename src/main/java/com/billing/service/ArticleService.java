package com.billing.service;

import com.billing.dao.*;
import com.billing.entity.*;
import com.billing.util.HibernateUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Service class for Article business logic operations
 * Handles validation, business rules, and coordinates with DAOs
 */
public class ArticleService {
    
    private static final Logger logger = Logger.getLogger(ArticleService.class.getName());
    private static final List<Integer> VALID_IVA_PERCENTAGES = Arrays.asList(0, 4, 10, 21);
    
    private final ArticleDAO articleDAO;
    private final SupplierDAO supplierDAO;
    private final ArticleFamilyDAO familyDAO;
    private final ArticleCategoryDAO categoryDAO;
    private final UnitDAO unitDAO;
    
    public ArticleService() {
        if (!HibernateUtil.isInitialized()) {
            logger.warning("Hibernate not initialized. Running in offline mode; database operations may be limited.");
        }
        
        this.articleDAO = new ArticleDAOImpl();
        this.supplierDAO = new SupplierDAOImpl();
        this.familyDAO = new ArticleFamilyDAOImpl();
        this.categoryDAO = new ArticleCategoryDAOImpl();
        this.unitDAO = new UnitDAOImpl();
    }
    
    // === ARTICLE OPERATIONS ===
    
    /**
     * Create a new article with validation
     */
    public Article createArticle(Article article) {
        logger.info("Creating new article: " + article.getName());
        
        validateArticle(article);
        validateUniqueConstraints(article);
        
        // Save first to obtain generated ID
        Article saved = articleDAO.save(article);
        if (saved != null && saved.getId() != null) {
            Integer id = saved.getId();
            String generatedCode = String.format("ART%03d", id);
            articleDAO.updateCode(id, generatedCode);
            saved = articleDAO.findById(id);
        }
        return saved;
    }
    
    /**
     * Update an existing article with validation
     */
    public Article updateArticle(Article article) {
        logger.info("Updating article: " + article.getName());
        
        validateArticle(article);
        validateUniqueConstraintsForUpdate(article);
        
        return articleDAO.save(article);
    }
    
    /**
     * Get all articles
     */
    public List<Article> getAllArticles() {
        try {
            return articleDAO.findAll();
        } catch (Exception e) {
            logger.severe("Error retrieving all articles: " + e.getMessage());
            throw new RuntimeException("Error retrieving all articles", e);
        }
    }
    
    /**
     * Get all active articles
     */
    public List<Article> getAllActiveArticles() {
        try {
            return articleDAO.findAllActive();
        } catch (Exception e) {
            logger.severe("Error retrieving active articles: " + e.getMessage());
            throw new RuntimeException("Error retrieving active articles", e);
        }
    }
    
    /**
     * Get all inactive articles
     */
    public List<Article> getAllInactiveArticles() {
        try {
            return articleDAO.findAllInactive();
        } catch (Exception e) {
            logger.severe("Error retrieving inactive articles: " + e.getMessage());
            throw new RuntimeException("Error retrieving inactive articles", e);
        }
    }
    
    /**
     * Get articles with low stock
     */
    public List<Article> getLowStockArticles() {
        try {
            return articleDAO.findLowStockArticles();
        } catch (Exception e) {
            logger.severe("Error retrieving low stock articles: " + e.getMessage());
            throw new RuntimeException("Error retrieving low stock articles", e);
        }
    }
    
    /**
     * Get article by ID
     */
    public Article getArticleById(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Article ID cannot be null");
        }
        return articleDAO.findById(id);
    }
    
    /**
     * Get article by barcode
     */
    public Article getArticleByBarcode(String barcode) {
        if (barcode == null || barcode.trim().isEmpty()) {
            return null;
        }
        return articleDAO.findByBarcode(barcode.trim());
    }
    
    /**
     * Search articles by name
     */
    public List<Article> searchArticlesByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return getAllArticles();
        }
        return articleDAO.findByName(name.trim());
    }
    
    /**
     * Delete article by ID
     */
    public void deleteArticle(Integer id) {
        Article article = getArticleById(id);
        if (article != null) {
            logger.info("Deleting article: " + article.getName());
            articleDAO.delete(article);
        } else {
            throw new IllegalArgumentException("Article not found with ID: " + id);
        }
    }
    
    // === SUPPLIER OPERATIONS ===
    
    public List<Supplier> getAllSuppliers() {
        try {
            return supplierDAO.findAll();
        } catch (Exception e) {
            logger.severe("Error retrieving suppliers: " + e.getMessage());
            throw new RuntimeException("Error retrieving suppliers", e);
        }
    }
    
    public List<Supplier> getAllActiveSuppliers() {
        try {
            return supplierDAO.findAllActive();
        } catch (Exception e) {
            logger.severe("Error retrieving active suppliers: " + e.getMessage());
            throw new RuntimeException("Error retrieving active suppliers", e);
        }
    }
    
    // === FAMILY OPERATIONS ===
    
    public List<ArticleFamily> getAllFamilies() {
        try {
            return familyDAO.findAll();
        } catch (Exception e) {
            logger.severe("Error retrieving families: " + e.getMessage());
            throw new RuntimeException("Error retrieving families", e);
        }
    }
    
    // === CATEGORY OPERATIONS ===
    
    public List<ArticleCategory> getAllCategories() {
        try {
            return categoryDAO.findAll();
        } catch (Exception e) {
            logger.severe("Error retrieving categories: " + e.getMessage());
            throw new RuntimeException("Error retrieving categories", e);
        }
    }
    
    // === UNIT OPERATIONS ===
    
    public List<Unit> getAllUnits() {
        try {
            return unitDAO.findAll();
        } catch (Exception e) {
            logger.severe("Error retrieving units: " + e.getMessage());
            throw new RuntimeException("Error retrieving units", e);
        }
    }
    
    // === VALIDATION METHODS ===
    
    private void validateArticle(Article article) {
        List<String> errors = new ArrayList<>();
        
        if (article == null) {
            throw new IllegalArgumentException("Article cannot be null");
        }
        
        // Name validation
        if (article.getName() == null || article.getName().trim().isEmpty()) {
            errors.add("Article name cannot be null or empty");
        } else if (article.getName().length() > 80) {
            errors.add("Article name must not exceed 80 characters");
        }
        
        // Description validation
        if (article.getDescription() != null && article.getDescription().length() > 500) {
            errors.add("Description must not exceed 500 characters");
        }
        
        // Required relationships validation
        if (article.getFamily() == null) {
            errors.add("Family cannot be null");
        }
        if (article.getCategory() == null) {
            errors.add("Category cannot be null");
        }
        if (article.getUnit() == null) {
            errors.add("Unit cannot be null");
        }
        if (article.getSupplier() == null) {
            errors.add("Supplier cannot be null");
        }
        
        // Price validation
        if (article.getCostPrice() == null) {
            errors.add("Cost price cannot be null");
        } else if (article.getCostPrice().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Cost price must be greater than or equal to 0.00");
        }
        
        if (article.getSalePrice() == null) {
            errors.add("Sale price cannot be null");
        } else if (article.getSalePrice().compareTo(BigDecimal.ZERO) < 0) {
            errors.add("Sale price must be greater than or equal to 0.00");
        }
        
        // Price relationship validation
        if (article.getCostPrice() != null && article.getSalePrice() != null) {
            if (article.getSalePrice().compareTo(article.getCostPrice()) < 0) {
                errors.add("Sale price must be greater than or equal to cost price");
            }
        }
        
        // IVA validation
        if (article.getIVAPercent() == null) {
            errors.add("IVA percentage cannot be null");
        } else if (!VALID_IVA_PERCENTAGES.contains(article.getIVAPercent())) {
            errors.add("IVA percentage must be 0, 4, 10, or 21");
        }
        
        // Stock validation
        if (article.getCurrentStock() == null) {
            errors.add("Current stock cannot be null");
        } else if (article.getCurrentStock() < 0) {
            errors.add("Current stock must be greater than or equal to 0");
        }
        
        if (article.getMinimumStock() == null) {
            errors.add("Minimum stock cannot be null");
        } else if (article.getMinimumStock() < 0) {
            errors.add("Minimum stock must be greater than or equal to 0");
        }
        
        // Barcode validation
        if (article.getBarcode() != null && !article.getBarcode().trim().isEmpty()) {
            String barcode = article.getBarcode().trim();
            if (!barcode.matches("^[0-9]{13}$")) {
                errors.add("Barcode must be exactly 13 digits");
            }
        }
        
        // Notes validation
        if (article.getObservations() != null && article.getObservations().length() > 500) {
            errors.add("Notes must not exceed 500 characters");
        }
        
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException("Validation errors: " + String.join(", ", errors));
        }
    }
    
    private void validateUniqueConstraints(Article article) {
        // Check barcode uniqueness (if provided)
        if (article.getBarcode() != null && !article.getBarcode().trim().isEmpty()) {
            Article existingByBarcode = articleDAO.findByBarcode(article.getBarcode());
            if (existingByBarcode != null) {
                throw new IllegalArgumentException("Barcode already exists: " + article.getBarcode());
            }
        }
    }
    
    private void validateUniqueConstraintsForUpdate(Article article) {
        // Check barcode uniqueness (if provided, excluding current article)
        if (article.getBarcode() != null && !article.getBarcode().trim().isEmpty()) {
            Article existingByBarcode = articleDAO.findByBarcode(article.getBarcode());
            if (existingByBarcode != null && !existingByBarcode.getId().equals(article.getId())) {
                throw new IllegalArgumentException("Barcode already exists: " + article.getBarcode());
            }
        }
    }
    
    // === UTILITY METHODS ===
    
    /**
     * Get valid IVA percentages
     */
    public List<Integer> getValidIvaPercentages() {
        return new ArrayList<>(VALID_IVA_PERCENTAGES);
    }
    
    /**
     * Calculate price with IVA
     */
    public BigDecimal calculatePriceWithIVA(BigDecimal basePrice, Integer ivaPercent) {
        if (basePrice == null || ivaPercent == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal ivaMultiplier = BigDecimal.ONE.add(BigDecimal.valueOf(ivaPercent).divide(BigDecimal.valueOf(100)));
        return basePrice.multiply(ivaMultiplier);
    }
}
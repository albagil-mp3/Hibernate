package com.billing.dao;

import com.billing.entity.Article;
import com.billing.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Implementation of ArticleDAO using Hibernate
 */
public class ArticleDAOImpl implements ArticleDAO {
    
    private static final Logger logger = Logger.getLogger(ArticleDAOImpl.class.getName());
    private final SessionFactory sessionFactory;
    
    public ArticleDAOImpl() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    private SessionFactory getSessionFactoryOrThrow() {
        SessionFactory factory = sessionFactory != null ? sessionFactory : HibernateUtil.getSessionFactory();
        if (factory == null || factory.isClosed()) {
            throw new RuntimeException("Database is not available. Please check your MySQL connection.");
        }
        return factory;
    }
    
    @Override
    public Article save(Article article) {
        Transaction transaction = null;
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            transaction = session.beginTransaction();
            
            if (article.getId() == null) {
                session.save(article);
                logger.info("Article created successfully: " + article.getName());
            } else {
                session.update(article);
                logger.info("Article updated successfully: " + article.getName());
            }
            
            transaction.commit();
            return article;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.severe("Error saving article: " + e.getMessage());
            throw new RuntimeException("Failed to save article: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Article findById(Integer id) {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            String jpql = "SELECT a FROM Article a " +
                         "LEFT JOIN FETCH a.family " +
                         "LEFT JOIN FETCH a.category " +
                         "LEFT JOIN FETCH a.unit " +
                         "LEFT JOIN FETCH a.supplier " +
                         "WHERE a.id = :id";
            Query<Article> query = session.createQuery(jpql, Article.class);
            query.setParameter("id", id);
            return query.uniqueResult();
        } catch (Exception e) {
            logger.severe("Error finding article by ID: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public List<Article> findAll() {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<Article> query = session.createQuery(
                "SELECT a FROM Article a " +
                "LEFT JOIN FETCH a.family " +
                "LEFT JOIN FETCH a.category " +
                "LEFT JOIN FETCH a.unit " +
                "LEFT JOIN FETCH a.supplier " +
                "ORDER BY a.id", Article.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.severe("Error finding all articles: " + e.getMessage());
            throw new RuntimeException("Error retrieving articles", e);
        }
    }
    
    @Override
    public void delete(Article article) {
        Transaction transaction = null;
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            transaction = session.beginTransaction();
            session.delete(article);
            transaction.commit();
            logger.info("Article deleted successfully: " + article.getName());
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.severe("Error deleting article: " + e.getMessage());
            throw new RuntimeException("Failed to delete article: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteById(Integer id) {
        Article article = findById(id);
        if (article != null) {
            delete(article);
        }
    }
    
    @Override
    public boolean existsById(Integer id) {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(a.id) FROM Article a WHERE a.id = :id", Long.class);
            query.setParameter("id", id);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            logger.severe("Error checking if article exists: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public long count() {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(a) FROM Article a", Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            logger.severe("Error counting articles: " + e.getMessage());
            return 0;
        }
    }
    
    @Override
    public Article update(Article article) {
        Transaction transaction = null;
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            transaction = session.beginTransaction();
            Article updated = (Article) session.merge(article);
            transaction.commit();
            return updated;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Failed to update article: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Article> findByName(String name) {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<Article> query = session.createQuery(
                "SELECT a FROM Article a " +
                "LEFT JOIN FETCH a.family " +
                "LEFT JOIN FETCH a.category " +
                "LEFT JOIN FETCH a.unit " +
                "LEFT JOIN FETCH a.supplier " +
                "WHERE LOWER(a.name) LIKE LOWER(:name) " +
                "ORDER BY a.name", Article.class);
            query.setParameter("name", "%" + name + "%");
            return query.getResultList();
        } catch (Exception e) {
            logger.severe("Error finding articles by name: " + e.getMessage());
            throw new RuntimeException("Error retrieving articles", e);
        }
    }
    
    @Override
    public Article findByBarcode(String barcode) {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<Article> query = session.createQuery(
                "SELECT a FROM Article a " +
                "LEFT JOIN FETCH a.family " +
                "LEFT JOIN FETCH a.category " +
                "LEFT JOIN FETCH a.unit " +
                "LEFT JOIN FETCH a.supplier " +
                "WHERE a.barcode = :barcode", Article.class);
            query.setParameter("barcode", barcode);
            return query.uniqueResult();
        } catch (Exception e) {
            logger.severe("Error finding article by barcode: " + e.getMessage());
            return null;
        }
    }
    
    @Override
    public List<Article> findAllActive() {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<Article> query = session.createQuery(
                "SELECT a FROM Article a " +
                "LEFT JOIN FETCH a.family " +
                "LEFT JOIN FETCH a.category " +
                "LEFT JOIN FETCH a.unit " +
                "LEFT JOIN FETCH a.supplier " +
                "WHERE a.active = true " +
                "ORDER BY a.name", Article.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.severe("Error finding active articles: " + e.getMessage());
            throw new RuntimeException("Error retrieving articles", e);
        }
    }
    
    @Override
    public List<Article> findAllInactive() {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<Article> query = session.createQuery(
                "SELECT a FROM Article a " +
                "LEFT JOIN FETCH a.family " +
                "LEFT JOIN FETCH a.category " +
                "LEFT JOIN FETCH a.unit " +
                "LEFT JOIN FETCH a.supplier " +
                "WHERE a.active = false " +
                "ORDER BY a.name", Article.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.severe("Error finding inactive articles: " + e.getMessage());
            throw new RuntimeException("Error retrieving articles", e);
        }
    }
    
    @Override
    public List<Article> findLowStockArticles() {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<Article> query = session.createQuery(
                "SELECT a FROM Article a " +
                "LEFT JOIN FETCH a.family " +
                "LEFT JOIN FETCH a.category " +
                "LEFT JOIN FETCH a.unit " +
                "LEFT JOIN FETCH a.supplier " +
                "WHERE a.stockActual <= a.stockMinim " +
                "AND a.active = true " +
                "ORDER BY a.stockActual ASC", Article.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.severe("Error finding low stock articles: " + e.getMessage());
            throw new RuntimeException("Error retrieving articles", e);
        }
    }
    
    @Override
    public List<Article> findBySupplier(Integer supplierId) {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<Article> query = session.createQuery(
                "SELECT a FROM Article a " +
                "LEFT JOIN FETCH a.family " +
                "LEFT JOIN FETCH a.category " +
                "LEFT JOIN FETCH a.unit " +
                "LEFT JOIN FETCH a.supplier " +
                "WHERE a.supplier.id = :supplierId " +
                "ORDER BY a.name", Article.class);
            query.setParameter("supplierId", supplierId);
            return query.getResultList();
        } catch (Exception e) {
            logger.severe("Error finding articles by supplier: " + e.getMessage());
            throw new RuntimeException("Error retrieving articles", e);
        }
    }
    
    @Override
    public List<Article> findByFamily(Integer familyId) {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<Article> query = session.createQuery(
                "SELECT a FROM Article a " +
                "LEFT JOIN FETCH a.family " +
                "LEFT JOIN FETCH a.category " +
                "LEFT JOIN FETCH a.unit " +
                "LEFT JOIN FETCH a.supplier " +
                "WHERE a.family.id = :familyId " +
                "ORDER BY a.name", Article.class);
            query.setParameter("familyId", familyId);
            return query.getResultList();
        } catch (Exception e) {
            logger.severe("Error finding articles by family: " + e.getMessage());
            throw new RuntimeException("Error retrieving articles", e);
        }
    }
    
    @Override
    public List<Article> findByCategory(Integer categoryId) {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<Article> query = session.createQuery(
                "SELECT a FROM Article a " +
                "LEFT JOIN FETCH a.family " +
                "LEFT JOIN FETCH a.category " +
                "LEFT JOIN FETCH a.unit " +
                "LEFT JOIN FETCH a.supplier " +
                "WHERE a.category.id = :categoryId " +
                "ORDER BY a.name", Article.class);
            query.setParameter("categoryId", categoryId);
            return query.getResultList();
        } catch (Exception e) {
            logger.severe("Error finding articles by category: " + e.getMessage());
            throw new RuntimeException("Error retrieving articles", e);
        }
    }
    
    @Override
    public List<Article> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<Article> query = session.createQuery(
                "SELECT a FROM Article a " +
                "LEFT JOIN FETCH a.family " +
                "LEFT JOIN FETCH a.category " +
                "LEFT JOIN FETCH a.unit " +
                "LEFT JOIN FETCH a.supplier " +
                "WHERE a.salePrice >= :minPrice " +
                "AND a.salePrice <= :maxPrice " +
                "ORDER BY a.salePrice ASC", Article.class);
            query.setParameter("minPrice", minPrice);
            query.setParameter("maxPrice", maxPrice);
            return query.getResultList();
        } catch (Exception e) {
            logger.severe("Error finding articles by price range: " + e.getMessage());
            throw new RuntimeException("Error retrieving articles", e);
        }
    }

    @Override
    public void updateCode(Integer id, String code) {
        Transaction transaction = null;
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            transaction = session.beginTransaction();
            Query<?> query = session.createNativeQuery("UPDATE articles SET code = :code WHERE id = :id");
            query.setParameter("code", code);
            query.setParameter("id", id);
            query.executeUpdate();
            transaction.commit();
            logger.info(() -> "Updated article code for ID: " + id + " -> " + code);
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.severe("Error updating article code for ID: " + id + ": " + e.getMessage());
            throw new RuntimeException("Failed to update article code: " + e.getMessage(), e);
        }
    }
}
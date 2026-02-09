package com.billing.dao;

import com.billing.entity.ArticleCategory;
import com.billing.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ArticleCategoryDAOImpl implements ArticleCategoryDAO {
    
    private static final Logger logger = Logger.getLogger(ArticleCategoryDAOImpl.class.getName());
    private final SessionFactory sessionFactory;
    
    public ArticleCategoryDAOImpl() {
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
    public ArticleCategory save(ArticleCategory category) {
        Transaction transaction = null;
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            transaction = session.beginTransaction();
            if (category.getCode() == null) {
                throw new RuntimeException("Category code cannot be null");
            }
            session.save(category);
            transaction.commit();
            return category;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Failed to save category: " + e.getMessage(), e);
        }
    }
    
    @Override
    public ArticleCategory findById(String code) {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            return session.get(ArticleCategory.class, code);
        } catch (Exception e) {
            logger.severe("Error finding category by code: " + e.getMessage());
            throw new RuntimeException("Error finding category by code", e);
        }
    }
    
    @Override
    public List<ArticleCategory> findAll() {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<ArticleCategory> query = session.createQuery("FROM ArticleCategory ORDER BY name", ArticleCategory.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.severe("Error finding all categories: " + e.getMessage());
            throw new RuntimeException("Error finding all categories", e);
        }
    }
    
    @Override
    public void delete(ArticleCategory category) {
        Transaction transaction = null;
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            transaction = session.beginTransaction();
            session.delete(category);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Failed to delete category: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteById(String code) {
        ArticleCategory category = findById(code);
        if (category != null) {
            delete(category);
        }
    }
    
    @Override
    public boolean existsById(String code) {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(c.code) FROM ArticleCategory c WHERE c.code = :code", Long.class);
            query.setParameter("code", code);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public long count() {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(c) FROM ArticleCategory c", Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }
    
    @Override
    public ArticleCategory update(ArticleCategory category) {
        Transaction transaction = null;
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            transaction = session.beginTransaction();
            ArticleCategory updated = (ArticleCategory) session.merge(category);
            transaction.commit();
            return updated;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Failed to update category: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<ArticleCategory> findByName(String name) {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<ArticleCategory> query = session.createQuery(
                "FROM ArticleCategory WHERE LOWER(name) LIKE LOWER(:name) ORDER BY name", ArticleCategory.class);
            query.setParameter("name", "%" + name + "%");
            return query.getResultList();
        } catch (Exception e) {
            logger.severe("Error finding categories by name: " + e.getMessage());
            throw new RuntimeException("Error finding categories by name", e);
        }
    }
}
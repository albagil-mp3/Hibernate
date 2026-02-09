package com.billing.dao;

import com.billing.entity.ArticleFamily;
import com.billing.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class ArticleFamilyDAOImpl implements ArticleFamilyDAO {
    
    private static final Logger logger = Logger.getLogger(ArticleFamilyDAOImpl.class.getName());
    private final SessionFactory sessionFactory;
    
    public ArticleFamilyDAOImpl() {
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
    public ArticleFamily save(ArticleFamily family) {
        Transaction transaction = null;
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            transaction = session.beginTransaction();
            if (family.getCode() == null) {
                throw new RuntimeException("Family code cannot be null");
            }
            session.save(family);
            transaction.commit();
            return family;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Failed to save family: " + e.getMessage(), e);
        }
    }
    
    @Override
    public ArticleFamily findById(String code) {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            return session.get(ArticleFamily.class, code);
        } catch (Exception e) {
            logger.severe("Error finding family by code: " + e.getMessage());
            throw new RuntimeException("Error finding family by code", e);
        }
    }
    
    @Override
    public List<ArticleFamily> findAll() {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<ArticleFamily> query = session.createQuery("FROM ArticleFamily ORDER BY name", ArticleFamily.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.severe("Error finding all families: " + e.getMessage());
            throw new RuntimeException("Error finding all families", e);
        }
    }
    
    @Override
    public void delete(ArticleFamily family) {
        Transaction transaction = null;
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            transaction = session.beginTransaction();
            session.delete(family);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Failed to delete family: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteById(String code) {
        ArticleFamily family = findById(code);
        if (family != null) {
            delete(family);
        }
    }
    
    @Override
    public boolean existsById(String code) {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(f.code) FROM ArticleFamily f WHERE f.code = :code", Long.class);
            query.setParameter("code", code);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public long count() {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(f) FROM ArticleFamily f", Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }
    
    @Override
    public ArticleFamily update(ArticleFamily family) {
        Transaction transaction = null;
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            transaction = session.beginTransaction();
            ArticleFamily updated = (ArticleFamily) session.merge(family);
            transaction.commit();
            return updated;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Failed to update family: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<ArticleFamily> findByName(String name) {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<ArticleFamily> query = session.createQuery(
                "FROM ArticleFamily WHERE LOWER(name) LIKE LOWER(:name) ORDER BY name", ArticleFamily.class);
            query.setParameter("name", "%" + name + "%");
            return query.getResultList();
        } catch (Exception e) {
            logger.severe("Error finding families by name: " + e.getMessage());
            throw new RuntimeException("Error finding families by name", e);
        }
    }
}
package com.billing.dao;

import com.billing.entity.Supplier;
import com.billing.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Implementation of SupplierDAO using Hibernate
 */
public class SupplierDAOImpl implements SupplierDAO {
    
    private static final Logger logger = Logger.getLogger(SupplierDAOImpl.class.getName());
    private final SessionFactory sessionFactory;
    
    public SupplierDAOImpl() {
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
    public Supplier save(Supplier supplier) {
        Transaction transaction = null;
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            transaction = session.beginTransaction();
            
            if (supplier.getName() == null) {
                throw new RuntimeException("Supplier name cannot be null");
            }
            
            session.save(supplier);
            logger.info("Supplier created successfully: " + supplier.getName());
            
            transaction.commit();
            return supplier;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.severe("Error saving supplier: " + e.getMessage());
            throw new RuntimeException("Failed to save supplier: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Supplier findById(String name) {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            return session.get(Supplier.class, name);
        } catch (Exception e) {
            logger.severe("Error finding supplier by name: " + e.getMessage());
            throw new RuntimeException("Error finding supplier by name", e);
        }
    }
    
    @Override
    public List<Supplier> findAll() {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<Supplier> query = session.createQuery("FROM Supplier ORDER BY name", Supplier.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.severe("Error finding all suppliers: " + e.getMessage());
            throw new RuntimeException("Error finding all suppliers", e);
        }
    }
    
    @Override
    public void delete(Supplier supplier) {
        Transaction transaction = null;
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            transaction = session.beginTransaction();
            session.delete(supplier);
            transaction.commit();
            logger.info("Supplier deleted successfully: " + supplier.getName());
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            logger.severe("Error deleting supplier: " + e.getMessage());
            throw new RuntimeException("Failed to delete supplier: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteById(String name) {
        Supplier supplier = findById(name);
        if (supplier != null) {
            delete(supplier);
        }
    }
    
    @Override
    public boolean existsById(String name) {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(s.name) FROM Supplier s WHERE s.name = :name", Long.class);
            query.setParameter("name", name);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            logger.severe("Error checking if supplier exists: " + e.getMessage());
            return false;
        }
    }
    
    @Override
    public long count() {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(s) FROM Supplier s", Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            logger.severe("Error counting suppliers: " + e.getMessage());
            return 0;
        }
    }
    
    @Override
    public Supplier update(Supplier supplier) {
        Transaction transaction = null;
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            transaction = session.beginTransaction();
            Supplier updated = (Supplier) session.merge(supplier);
            transaction.commit();
            return updated;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Failed to update supplier: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Supplier> findByName(String name) {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<Supplier> query = session.createQuery(
                "FROM Supplier WHERE LOWER(name) LIKE LOWER(:name) ORDER BY name", Supplier.class);
            query.setParameter("name", "%" + name + "%");
            return query.getResultList();
        } catch (Exception e) {
            logger.severe("Error finding suppliers by name: " + e.getMessage());
            throw new RuntimeException("Error finding suppliers by name", e);
        }
    }
    
    @Override
    public List<Supplier> findAllActive() {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<Supplier> query = session.createQuery("FROM Supplier WHERE active = true ORDER BY name", Supplier.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.severe("Error finding active suppliers: " + e.getMessage());
            throw new RuntimeException("Error finding active suppliers", e);
        }
    }
    
    @Override
    public List<Supplier> findAllInactive() {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<Supplier> query = session.createQuery("FROM Supplier WHERE active = false ORDER BY name", Supplier.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.severe("Error finding inactive suppliers: " + e.getMessage());
            throw new RuntimeException("Error finding inactive suppliers", e);
        }
    }
}
package com.billing.dao;

import com.billing.entity.Unit;
import com.billing.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UnitDAOImpl implements UnitDAO {
    
    private static final Logger logger = Logger.getLogger(UnitDAOImpl.class.getName());
    private final SessionFactory sessionFactory;
    
    public UnitDAOImpl() {
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
    public Unit save(Unit unit) {
        Transaction transaction = null;
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            transaction = session.beginTransaction();
            if (unit.getSymbol() == null) {
                throw new RuntimeException("Unit symbol cannot be null");
            }
            session.save(unit);
            transaction.commit();
            return unit;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Failed to save unit: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Unit findById(String symbol) {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            return session.get(Unit.class, symbol);
        } catch (Exception e) {
            logger.severe("Error finding unit by symbol: " + e.getMessage());
            throw new RuntimeException("Error finding unit by symbol", e);
        }
    }
    
    @Override
    public List<Unit> findAll() {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<Unit> query = session.createQuery("FROM Unit ORDER BY name", Unit.class);
            return query.getResultList();
        } catch (Exception e) {
            logger.severe("Error finding all units: " + e.getMessage());
            throw new RuntimeException("Error finding all units", e);
        }
    }
    
    @Override
    public void delete(Unit unit) {
        Transaction transaction = null;
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            transaction = session.beginTransaction();
            session.delete(unit);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Failed to delete unit: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteById(String symbol) {
        Unit unit = findById(symbol);
        if (unit != null) {
            delete(unit);
        }
    }
    
    @Override
    public boolean existsById(String symbol) {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(u.symbol) FROM Unit u WHERE u.symbol = :symbol", Long.class);
            query.setParameter("symbol", symbol);
            return query.getSingleResult() > 0;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public long count() {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(u) FROM Unit u", Long.class);
            return query.getSingleResult();
        } catch (Exception e) {
            return 0;
        }
    }
    
    @Override
    public Unit update(Unit unit) {
        Transaction transaction = null;
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            transaction = session.beginTransaction();
            Unit updated = (Unit) session.merge(unit);
            transaction.commit();
            return updated;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw new RuntimeException("Failed to update unit: " + e.getMessage(), e);
        }
    }
    
    @Override
    public List<Unit> findByName(String name) {
        try (Session session = getSessionFactoryOrThrow().openSession()) {
            Query<Unit> query = session.createQuery(
                "FROM Unit WHERE LOWER(name) LIKE LOWER(:name) ORDER BY name", Unit.class);
            query.setParameter("name", "%" + name + "%");
            return query.getResultList();
        } catch (Exception e) {
            logger.severe("Error finding units by name: " + e.getMessage());
            throw new RuntimeException("Error finding units by name", e);
        }
    }
}
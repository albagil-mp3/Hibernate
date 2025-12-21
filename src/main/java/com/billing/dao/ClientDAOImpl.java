package com.billing.dao;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import com.billing.entity.Client;
import com.billing.util.HibernateUtil;

/**
 * Implementation of ClientDAO using Hibernate
 */
public class ClientDAOImpl implements ClientDAO {
    
    private static final Logger logger = Logger.getLogger(ClientDAOImpl.class.getName());
    private final SessionFactory sessionFactory;
    
    public ClientDAOImpl() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }
    
    @Override
    public Client save(Client client) {
        if (!HibernateUtil.isInitialized()) {
            throw new RuntimeException("Database is not available. Please check your MySQL connection.");
        }
        if (sessionFactory == null || sessionFactory.isClosed()) {
            throw new RuntimeException("Hibernate SessionFactory is closed or unavailable. Restart the application to reinitialize the database connection.");
        }
        
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.save(client);
            transaction.commit();
            logger.info("Client saved successfully with ID: {0}");
            return client;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.log(Level.SEVERE, "Error saving client", e);
            throw new RuntimeException("Error saving client: " + e.getMessage(), e);
        }
    }
    
    @Override
    public Client findById(Integer id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(Client.class, id);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding client by ID: " + id, e);
            throw new RuntimeException("Error finding client by ID", e);
        }
    }
    
    @Override
    public List<Client> findAll() {
        try (Session session = sessionFactory.openSession()) {
            Query<Client> query = session.createQuery("FROM Client", Client.class);
            return query.list();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding all clients", e);
            throw new RuntimeException("Error finding all clients", e);
        }
    }
    
    @Override
    public Client update(Client client) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(client);
            transaction.commit();
            logger.info("Client updated successfully with ID: {0}");
            return client;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.log(Level.SEVERE, "Error updating client", e);
            throw new RuntimeException("Error updating client", e);
        }
    }
    
    @Override
    public void delete(Client client) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.delete(client);
            transaction.commit();
            logger.info(() -> "Client deleted successfully with ID: " + client.getId());
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.log(Level.SEVERE, "Error deleting client", e);
            throw new RuntimeException("Error deleting client", e);
        }
    }
    
    @Override
    public void deleteById(Integer id) {
        Client client = findById(id);
        if (client != null) {
            delete(client);
        }
    }
    
    @Override
    public boolean existsById(Integer id) {
        return findById(id) != null;
    }
    
    @Override
    public long count() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> query = session.createQuery("SELECT COUNT(c) FROM Client c", Long.class);
            return query.uniqueResult();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error counting clients", e);
            throw new RuntimeException("Error counting clients", e);
        }
    }
    
    @Override
    public Client findByDni(String dni) {
        try (Session session = sessionFactory.openSession()) {
            Query<Client> query = session.createQuery("FROM Client WHERE dni = :dni", Client.class);
            query.setParameter("dni", dni);
            return query.uniqueResult();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding client by DNI: " + dni, e);
            throw new RuntimeException("Error finding client by DNI", e);
        }
    }
    
    @Override
    public List<Client> findByName(String name) {
        try (Session session = sessionFactory.openSession()) {
            Query<Client> query = session.createQuery(
                "FROM Client WHERE LOWER(name) LIKE LOWER(:name)", Client.class);
            query.setParameter("name", "%" + name + "%");
            return query.list();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding clients by name: " + name, e);
            throw new RuntimeException("Error finding clients by name", e);
        }
    }
    
    @Override
    public List<Client> findByPhone(String phone) {
        try (Session session = sessionFactory.openSession()) {
            Query<Client> query = session.createQuery(
                "FROM Client WHERE fixedPhone = :phone OR mobilePhone = :phone", Client.class);
            query.setParameter("phone", phone);
            return query.list();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding clients by phone: " + phone, e);
            throw new RuntimeException("Error finding clients by phone", e);
        }
    }
    
    @Override
    public List<Client> findByWebsite(String website) {
        try (Session session = sessionFactory.openSession()) {
            Query<Client> query = session.createQuery(
                "FROM Client WHERE LOWER(website) LIKE LOWER(:website)", Client.class);
            query.setParameter("website", "%" + website + "%");
            return query.list();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding clients by website: " + website, e);
            throw new RuntimeException("Error finding clients by website", e);
        }
    }
    
    @Override
    public List<Client> findActiveClients() {
        try (Session session = sessionFactory.openSession()) {
            Query<Client> query = session.createQuery("FROM Client WHERE active = true", Client.class);
            return query.list();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding active clients", e);
            throw new RuntimeException("Error finding active clients", e);
        }
    }
    
    @Override
    public List<Client> findInactiveClients() {
        try (Session session = sessionFactory.openSession()) {
            Query<Client> query = session.createQuery("FROM Client WHERE active = false", Client.class);
            return query.list();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding inactive clients", e);
            throw new RuntimeException("Error finding inactive clients", e);
        }
    }
    
    @Override
    public List<Client> findAllOrderedById() {
        try (Session session = sessionFactory.openSession()) {
            Query<Client> query = session.createQuery("FROM Client ORDER BY id", Client.class);
            return query.list();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding clients ordered by ID", e);
            throw new RuntimeException("Error finding clients ordered by ID", e);
        }
    }
    
    @Override
    public List<Client> findAllOrderedByDni() {
        try (Session session = sessionFactory.openSession()) {
            Query<Client> query = session.createQuery("FROM Client ORDER BY dni", Client.class);
            return query.list();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding clients ordered by DNI", e);
            throw new RuntimeException("Error finding clients ordered by DNI", e);
        }
    }
    
    @Override
    public List<Client> findAllOrderedByName() {
        try (Session session = sessionFactory.openSession()) {
            Query<Client> query = session.createQuery("FROM Client ORDER BY name", Client.class);
            return query.list();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error finding clients ordered by name", e);
            throw new RuntimeException("Error finding clients ordered by name", e);
        }
    }
    
    @Override
    public boolean existsByDni(String dni) {
        return findByDni(dni) != null;
    }
    
    @Override
    public List<Client> searchClients(String searchTerm) {
        try (Session session = sessionFactory.openSession()) {
            Query<Client> query = session.createQuery(
                "FROM Client WHERE " +
                "LOWER(name) LIKE LOWER(:term) OR " +
                "LOWER(dni) LIKE LOWER(:term) OR " +
                "fixedPhone LIKE :term OR " +
                "mobilePhone LIKE :term OR " +
                "LOWER(website) LIKE LOWER(:term)", Client.class);
            query.setParameter("term", "%" + searchTerm + "%");
            return query.list();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error searching clients with term: " + searchTerm, e);
            throw new RuntimeException("Error searching clients", e);
        }
    }
}
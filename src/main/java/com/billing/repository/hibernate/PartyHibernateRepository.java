package com.billing.repository.hibernate;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.billing.model.party.Client;
import com.billing.model.party.Party;
import com.billing.model.party.Supplier;
import com.billing.repository.interfaces.PartyRepository;
import com.billing.util.HibernateUtil;

public class PartyHibernateRepository extends HibernateRepository<Party, Long> implements PartyRepository {

    private static final Logger logger = Logger.getLogger(PartyHibernateRepository.class.getName());

    public PartyHibernateRepository() {
        super(Party.class);
    }

    private Session currentSession() {
        return HibernateUtil.getSessionFactory().getCurrentSession();
    }

    @Override
    public Client saveClient(Client client) {
        currentSession().persist(client);
        return client;
    }

    @Override
    public Client updateClient(Client client) {
        return (Client) currentSession().merge(client);
    }

    @Override
    public void deleteClient(Client client) {
        currentSession().remove(client);
    }

    @Override
    public Client findClientById(Long id) {
        return currentSession().get(Client.class, id);
    }

    @Override
    public Client findClientByDni(String dni) {
        Query<Client> query = currentSession().createQuery("FROM Client WHERE dni = :dni", Client.class);
        query.setParameter("dni", dni);
        return query.uniqueResult();
    }

    @Override
    public List<Client> findAllClients() {
        Query<Client> query = currentSession().createQuery("FROM Client", Client.class);
        return query.list();
    }

    @Override
    public List<Client> findAllClientsOrderedByCode() {
        Query<Client> query = currentSession().createQuery("FROM Client ORDER BY code", Client.class);
        return query.list();
    }

    @Override
    public List<Client> findAllClientsOrderedByDni() {
        Query<Client> query = currentSession().createQuery("FROM Client ORDER BY dni", Client.class);
        return query.list();
    }

    @Override
    public List<Client> findAllClientsOrderedByName() {
        Query<Client> query = currentSession().createQuery("FROM Client ORDER BY name", Client.class);
        return query.list();
    }

    @Override
    public List<Client> findActiveClients() {
        Query<Client> query = currentSession().createQuery("FROM Client WHERE active = true", Client.class);
        return query.list();
    }

    @Override
    public List<Client> findInactiveClients() {
        Query<Client> query = currentSession().createQuery("FROM Client WHERE active = false", Client.class);
        return query.list();
    }

    @Override
    public List<Client> searchClients(String searchTerm) {
        Query<Client> query = currentSession().createQuery(
            "FROM Client WHERE " +
            "LOWER(name) LIKE LOWER(:term) OR " +
            "LOWER(dni) LIKE LOWER(:term) OR " +
            "fixedPhone LIKE :term OR " +
            "mobilePhone LIKE :term OR " +
            "LOWER(website) LIKE LOWER(:term)", Client.class);
        query.setParameter("term", "%" + searchTerm + "%");
        return query.list();
    }

    @Override
    public boolean existsClientByDni(String dni) {
        return findClientByDni(dni) != null;
    }

    @Override
    public long countClients() {
        Query<Long> query = currentSession().createQuery("SELECT COUNT(c) FROM Client c", Long.class);
        return query.uniqueResult();
    }

    @Override
    public void updateClientCode(Long id, String code) {
        try {
            Query<?> query = currentSession().createNativeQuery("UPDATE client SET code = :code WHERE id = :id", Void.class);
            query.setParameter("code", code);
            query.setParameter("id", id);
            query.executeUpdate();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating client code for ID: " + id, e);
            throw new RuntimeException("Error updating client code", e);
        }
    }

    @Override
    public Supplier saveSupplier(Supplier supplier) {
        currentSession().persist(supplier);
        return supplier;
    }

    @Override
    public Supplier updateSupplier(Supplier supplier) {
        return (Supplier) currentSession().merge(supplier);
    }

    @Override
    public void deleteSupplier(Supplier supplier) {
        currentSession().remove(supplier);
    }

    @Override
    public Supplier findSupplierById(Long id) {
        return currentSession().get(Supplier.class, id);
    }

    @Override
    public Supplier findSupplierByCode(String code) {
        Query<Supplier> query = currentSession().createQuery("FROM Supplier WHERE code = :code", Supplier.class);
        query.setParameter("code", code);
        return query.uniqueResult();
    }

    @Override
    public List<Supplier> findAllSuppliers() {
        Query<Supplier> query = currentSession().createQuery("FROM Supplier ORDER BY name", Supplier.class);
        return query.list();
    }

    @Override
    public List<Supplier> findSuppliersByName(String name) {
        Query<Supplier> query = currentSession().createQuery(
            "FROM Supplier WHERE LOWER(name) LIKE LOWER(:name) ORDER BY name", Supplier.class);
        query.setParameter("name", "%" + name + "%");
        return query.list();
    }

    @Override
    public List<Supplier> findAllSuppliersOrderedByName() {
        Query<Supplier> query = currentSession().createQuery("FROM Supplier ORDER BY name", Supplier.class);
        return query.list();
    }

    @Override
    public List<Supplier> findAllActiveSuppliers() {
        Query<Supplier> query = currentSession().createQuery("FROM Supplier WHERE active = true ORDER BY name", Supplier.class);
        return query.list();
    }

    @Override
    public List<Supplier> findAllInactiveSuppliers() {
        Query<Supplier> query = currentSession().createQuery("FROM Supplier WHERE active = false ORDER BY name", Supplier.class);
        return query.list();
    }

    @Override
    public long countSuppliers() {
        Query<Long> query = currentSession().createQuery("SELECT COUNT(s) FROM Supplier s", Long.class);
        return query.uniqueResult();
    }

    @Override
    public void updateSupplierCode(Long id, String code) {
        try {
            Query<?> query = currentSession().createNativeQuery("UPDATE supplier SET code = :code WHERE id = :id", Void.class);
            query.setParameter("code", code);
            query.setParameter("id", id);
            query.executeUpdate();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating supplier code for ID: " + id, e);
            throw new RuntimeException("Error updating supplier code", e);
        }
    }
}

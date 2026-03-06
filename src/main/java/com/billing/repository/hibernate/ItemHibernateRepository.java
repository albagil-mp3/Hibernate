package com.billing.repository.hibernate;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.billing.model.item.Item;
import com.billing.model.item.ItemCategory;
import com.billing.model.item.ItemFamily;
import com.billing.model.item.Unit;
import com.billing.repository.interfaces.ItemRepository;
import com.billing.util.HibernateUtil;

public class ItemHibernateRepository extends HibernateRepository<Item, Long> implements ItemRepository {

    private static final Logger logger = Logger.getLogger(ItemHibernateRepository.class.getName());

    public ItemHibernateRepository() {
        super(Item.class);
    }

    private Session currentSession() {
        return HibernateUtil.getSessionFactory().getCurrentSession();
    }

    @Override
    public Item findById(Long id) {
        String jpql = "SELECT i FROM Item i " +
                      "LEFT JOIN FETCH i.family " +
                      "LEFT JOIN FETCH i.category " +
                      "LEFT JOIN FETCH i.unit " +
                      "LEFT JOIN FETCH i.supplier " +
                      "WHERE i.id = :id";
        Query<Item> query = currentSession().createQuery(jpql, Item.class);
        query.setParameter("id", id);
        return query.uniqueResult();
    }

    public Item findById(Integer id) {
        return findById(id.longValue());
    }

    @Override
    public List<Item> findAll() {
        Query<Item> query = currentSession().createQuery(
            "SELECT i FROM Item i " +
            "LEFT JOIN FETCH i.family " +
            "LEFT JOIN FETCH i.category " +
            "LEFT JOIN FETCH i.unit " +
            "LEFT JOIN FETCH i.supplier " +
            "ORDER BY i.id", Item.class);
        return query.getResultList();
    }

    @Override
    public Item findByBarcode(String barcode) {
        Query<Item> query = currentSession().createQuery(
            "SELECT i FROM Item i " +
            "LEFT JOIN FETCH i.family " +
            "LEFT JOIN FETCH i.category " +
            "LEFT JOIN FETCH i.unit " +
            "LEFT JOIN FETCH i.supplier " +
            "WHERE i.barcode = :barcode", Item.class);
        query.setParameter("barcode", barcode);
        return query.uniqueResult();
    }

    @Override
    public List<Item> findByName(String name) {
        Query<Item> query = currentSession().createQuery(
            "SELECT i FROM Item i " +
            "LEFT JOIN FETCH i.family " +
            "LEFT JOIN FETCH i.category " +
            "LEFT JOIN FETCH i.unit " +
            "LEFT JOIN FETCH i.supplier " +
            "WHERE LOWER(i.description) LIKE LOWER(:name) " +
            "ORDER BY i.description", Item.class);
        query.setParameter("name", "%" + name + "%");
        return query.getResultList();
    }

    @Override
    public List<Item> findAllActive() {
        Query<Item> query = currentSession().createQuery(
            "SELECT i FROM Item i " +
            "LEFT JOIN FETCH i.family " +
            "LEFT JOIN FETCH i.category " +
            "LEFT JOIN FETCH i.unit " +
            "LEFT JOIN FETCH i.supplier " +
            "WHERE i.active = true " +
            "ORDER BY i.description", Item.class);
        return query.getResultList();
    }

    @Override
    public List<Item> findAllInactive() {
        Query<Item> query = currentSession().createQuery(
            "SELECT i FROM Item i " +
            "LEFT JOIN FETCH i.family " +
            "LEFT JOIN FETCH i.category " +
            "LEFT JOIN FETCH i.unit " +
            "LEFT JOIN FETCH i.supplier " +
            "WHERE i.active = false " +
            "ORDER BY i.description", Item.class);
        return query.getResultList();
    }

    @Override
    public List<Item> findLowStockItems() {
        Query<Item> query = currentSession().createQuery(
            "SELECT i FROM Item i " +
            "LEFT JOIN FETCH i.family " +
            "LEFT JOIN FETCH i.category " +
            "LEFT JOIN FETCH i.unit " +
            "LEFT JOIN FETCH i.supplier " +
            "WHERE i.currentStock <= i.minimumStock " +
            "AND i.active = true " +
            "ORDER BY i.currentStock ASC", Item.class);
        return query.getResultList();
    }

    @Override
    public List<Item> findBySupplier(Long supplierId) {
        Query<Item> query = currentSession().createQuery(
            "SELECT i FROM Item i " +
            "LEFT JOIN FETCH i.family " +
            "LEFT JOIN FETCH i.category " +
            "LEFT JOIN FETCH i.unit " +
            "LEFT JOIN FETCH i.supplier " +
            "WHERE i.supplier.id = :supplierId " +
            "ORDER BY i.description", Item.class);
        query.setParameter("supplierId", supplierId);
        return query.getResultList();
    }

    public List<Item> findBySupplier(Integer supplierId) {
        return findBySupplier(supplierId.longValue());
    }

    @Override
    public List<Item> findByFamilyCode(String familyCode) {
        Query<Item> query = currentSession().createQuery(
            "SELECT i FROM Item i " +
            "LEFT JOIN FETCH i.family f " +
            "LEFT JOIN FETCH i.category " +
            "LEFT JOIN FETCH i.unit " +
            "LEFT JOIN FETCH i.supplier " +
            "WHERE f.code = :familyCode " +
            "ORDER BY i.description", Item.class);
        query.setParameter("familyCode", familyCode);
        return query.getResultList();
    }

    @Override
    public List<Item> findByCategoryCode(String categoryCode) {
        Query<Item> query = currentSession().createQuery(
            "SELECT i FROM Item i " +
            "LEFT JOIN FETCH i.family " +
            "LEFT JOIN FETCH i.category c " +
            "LEFT JOIN FETCH i.unit " +
            "LEFT JOIN FETCH i.supplier " +
            "WHERE c.code = :categoryCode " +
            "ORDER BY i.description", Item.class);
        query.setParameter("categoryCode", categoryCode);
        return query.getResultList();
    }

    @Override
    public List<Item> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        Query<Item> query = currentSession().createQuery(
            "SELECT i FROM Item i " +
            "LEFT JOIN FETCH i.family " +
            "LEFT JOIN FETCH i.category " +
            "LEFT JOIN FETCH i.unit " +
            "LEFT JOIN FETCH i.supplier " +
            "WHERE i.salePrice >= :minPrice " +
            "AND i.salePrice <= :maxPrice " +
            "ORDER BY i.salePrice ASC", Item.class);
        query.setParameter("minPrice", minPrice);
        query.setParameter("maxPrice", maxPrice);
        return query.getResultList();
    }

    @Override
    public void updateCode(Long id, String code) {
        try {
            Query<?> query = currentSession().createNativeQuery("UPDATE item SET code = :code WHERE id = :id", Void.class);
            query.setParameter("code", code);
            query.setParameter("id", id);
            query.executeUpdate();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating item code for ID: " + id, e);
            throw new RuntimeException("Failed to update item code: " + e.getMessage(), e);
        }
    }

    public void updateCode(Integer id, String code) {
        updateCode(id.longValue(), code);
    }

    @Override
    public ItemFamily findFamilyByCode(String code) {
        Query<ItemFamily> query = currentSession().createQuery("FROM ItemFamily WHERE code = :code", ItemFamily.class);
        query.setParameter("code", code);
        return query.uniqueResult();
    }

    @Override
    public ItemCategory findCategoryByCode(String code) {
        Query<ItemCategory> query = currentSession().createQuery("FROM ItemCategory WHERE code = :code", ItemCategory.class);
        query.setParameter("code", code);
        return query.uniqueResult();
    }

    @Override
    public Unit findUnitBySymbol(String symbol) {
        Query<Unit> query = currentSession().createQuery("FROM Unit WHERE symbol = :symbol", Unit.class);
        query.setParameter("symbol", symbol);
        return query.uniqueResult();
    }

    @Override
    public List<ItemFamily> findAllFamilies() {
        Query<ItemFamily> query = currentSession().createQuery("FROM ItemFamily ORDER BY name", ItemFamily.class);
        return query.list();
    }

    @Override
    public List<ItemCategory> findAllCategories() {
        Query<ItemCategory> query = currentSession().createQuery("FROM ItemCategory ORDER BY name", ItemCategory.class);
        return query.list();
    }

    @Override
    public List<Unit> findAllUnits() {
        Query<Unit> query = currentSession().createQuery("FROM Unit ORDER BY name", Unit.class);
        return query.list();
    }
}

package com.billing.repository.hibernate;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

import com.billing.repository.Repository;
import com.billing.util.HibernateUtil;

/**
 * Basic Hibernate repository implementation for common CRUD operations
 */
public abstract class HibernateRepository<T, ID extends Serializable> implements Repository<T, ID> {
    protected final Class<T> entityClass;

    protected HibernateRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public T find(ID id) {
        Session s = HibernateUtil.getSessionFactory().getCurrentSession();
        return s.get(entityClass, id);
    }

    @Override
    public List<T> findAll() {
        Session s = HibernateUtil.getSessionFactory().getCurrentSession();
        return s.createQuery("from " + entityClass.getName(), entityClass).list();
    }

    @Override
    public T save(T entity) {
        Session s = HibernateUtil.getSessionFactory().getCurrentSession();
        return s.merge(entity);
    }

    @Override
    public T update(T entity) {
        Session s = HibernateUtil.getSessionFactory().getCurrentSession();
        return s.merge(entity);
    }

    @Override
    public void delete(T entity) {
        Session s = HibernateUtil.getSessionFactory().getCurrentSession();
        s.remove(entity);
    }

    @Override
    public long count() {
        Session s = HibernateUtil.getSessionFactory().getCurrentSession();
        String ql = "SELECT COUNT(e) FROM " + entityClass.getName() + " e";
        return s.createQuery(ql, Long.class).getSingleResult();
    }
}

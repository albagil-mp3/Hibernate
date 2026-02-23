package com.billing.repository.hibernate;

import java.util.List;

import org.hibernate.Session;

import com.billing.model.document.BusinessDocument;
import com.billing.repository.interfaces.DocumentRepository;
import com.billing.util.HibernateUtil;

public class DocumentHibernateRepository
    extends HibernateRepository<BusinessDocument, Long>
    implements DocumentRepository {

    public DocumentHibernateRepository() {
        super(BusinessDocument.class);
    }

    @Override
    public BusinessDocument find(Long id) {
        Session s = HibernateUtil.getSessionFactory().getCurrentSession();
        return s.createQuery(
                "select distinct d from BusinessDocument d " +
                "left join fetch d.lines l " +
                "left join fetch l.item " +
                "left join fetch d.party " +
                "where d.id = :id", BusinessDocument.class)
                .setParameter("id", id)
            .uniqueResult();
    }

    @Override
    public List<BusinessDocument> findAll() {
        Session s = HibernateUtil.getSessionFactory().getCurrentSession();
        return s.createQuery(
                "select distinct d from BusinessDocument d " +
                "left join fetch d.lines l " +
                "left join fetch l.item " +
                "left join fetch d.party", BusinessDocument.class)
            .list();
    }
}

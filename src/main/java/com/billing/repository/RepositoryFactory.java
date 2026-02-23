package com.billing.repository;

import java.util.logging.Logger;

import com.billing.repository.hibernate.DocumentHibernateRepository;
import com.billing.repository.hibernate.PartyHibernateRepository;
import com.billing.repository.hibernate.ItemHibernateRepository;
import com.billing.repository.interfaces.DocumentRepository;
import com.billing.repository.interfaces.ItemRepository;
import com.billing.repository.interfaces.PartyRepository;
import com.billing.util.HibernateUtil;

/**
 * Central factory for creating repository/DAO instances with fixed responsibilities.
 * Hibernate is always the primary transactional storage.
 * JSON/XML are used for import/export and configuration (not runtime switching).
 * JDBC DAO exists only for comparative/educational purposes and is not selected here.
 */
public final class RepositoryFactory {

    private static final Logger logger = Logger.getLogger(RepositoryFactory.class.getName());

    private RepositoryFactory() {}

    public static PartyRepository createPartyRepository() {
        if (!HibernateUtil.isInitialized()) {
            logger.warning("Party repository requires Hibernate but SessionFactory is not initialized");
        }
        return new PartyHibernateRepository();
    }

    public static ItemRepository createItemRepository() {
        if (!HibernateUtil.isInitialized()) {
            logger.warning("Item repository requires Hibernate but SessionFactory is not initialized");
        }
        return new ItemHibernateRepository();
    }

    public static DocumentRepository createDocumentRepository() {
        return new DocumentHibernateRepository();
    }

}

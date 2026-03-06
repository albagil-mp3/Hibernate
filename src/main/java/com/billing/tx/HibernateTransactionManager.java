package com.billing.tx;

import java.util.function.Supplier;

import com.billing.util.HibernateUtil;

public class HibernateTransactionManager implements TransactionManager {
    @Override
    public <T> T runInTransaction(Supplier<T> work) {
        return HibernateUtil.runInTransaction(work);
    }

    @Override
    public void runInTransaction(Runnable work) {
        HibernateUtil.runInTransaction(work);
    }
}

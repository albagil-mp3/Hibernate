package com.billing.tx;

import java.util.function.Supplier;

public interface TransactionManager {
    <T> T runInTransaction(Supplier<T> work);
    void runInTransaction(Runnable work);
}

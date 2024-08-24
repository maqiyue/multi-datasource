package com.qyma.db.manager;



import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;

import javax.sql.DataSource;

public class TenantTransactionManager extends DataSourceTransactionManager {

    public TenantTransactionManager() {
        super();
    }

    public TenantTransactionManager(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
        MultiRouterManager.dataSourceContext.set("tenant");
        super.doBegin(transaction, definition);
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) {
        super.doCommit(status);
        MultiRouterManager.dataSourceContext.remove();
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) {
        super.doRollback(status);
        MultiRouterManager.dataSourceContext.remove();
    }
}


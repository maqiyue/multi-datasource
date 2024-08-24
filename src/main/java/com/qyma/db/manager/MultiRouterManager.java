package com.qyma.db.manager;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class MultiRouterManager extends AbstractRoutingDataSource {
    public static ThreadLocal<String> dataSourceContext = new ThreadLocal<>();
    public static ThreadLocal<String> tenantContext = new ThreadLocal<>();

    @Override
    protected Object determineCurrentLookupKey() {
        if (dataSourceContext.get() != null && tenantContext.get() != null){
            return tenantContext.get();
        }
        return "common";
    }


}

package com.qyma.mi.work;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.meiyunji.crm.model.entity.common", 
        entityManagerFactoryRef = "commonEntityManagerFactory"
)
public class DynamicDataSourceConfig2 {

    @Autowired
    DataSourceManger dataSourceManger;


    @Bean(name = "commonDataSource")
    public DataSource commonDataSource() {
        return dataSourceManger.getDataSource();
    }

    @Bean
    @Primary
    public DataSource dataSource(@Qualifier("commonDataSource") DataSource commonDataSource) {
        AbstractRoutingDataSource routingDataSource = new TenantRoutingDataSource();
        Map<Object, Object> targetDataSources = new HashMap<>();
        // 配置公共数据源
        targetDataSources.put("common", commonDataSource);
        Map<String,String> dataSourceUrlMap = null;//从common数据库获取配置
        dataSourceUrlMap.forEach((tenant, url) -> {
            DataSource tenantDataSource = dataSourceManger.getDataSource(url);
            targetDataSources.put(tenant, tenantDataSource);
        });
        routingDataSource.setTargetDataSources(targetDataSources);

        return routingDataSource;
    }

    @Bean(name = "commonEntityManagerFactory")
    @Primary
    public LocalContainerEntityManagerFactoryBean commonEntityManagerFactory(@Qualifier("commonDataSource") DataSource commonDataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(commonDataSource);
        em.setPackagesToScan("com.meiyunji.crm.model.entity.common");
        return em;
    }

    @Bean
    public Map<String, LocalContainerEntityManagerFactoryBean> tenantEntityManagerFactories() {
        Map<String, LocalContainerEntityManagerFactoryBean> factories = new HashMap<>();
        Map<String, String> dataSourceUrlMap = null; // 获取租户数据源配置
        for (Map.Entry<String, String> entry : dataSourceUrlMap.entrySet()) {
            String tenant = entry.getKey();
            String url = entry.getValue();
            DataSource tenantDataSource = dataSourceManger.getDataSource(url); // 获取租户数据源
            LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
            em.setDataSource(tenantDataSource);
            em.setPackagesToScan("com.meiyunji.crm.model.entity.tenant"); // 扫描租户实体类包
            factories.put(tenant, em);
        }

        return factories;
    }

    private LocalContainerEntityManagerFactoryBean createEntityManagerFactory(DataSource dataSource,String type) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan(type.equals("common")?"com.meiyunji.crm.model.entity.common":"com.meiyunji.crm.model.entity.tenant"); // Path to tenant entities
        return em;
    }
}

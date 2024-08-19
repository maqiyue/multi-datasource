//package com.qyma.mi.work;
//
//
//import jakarta.persistence.EntityManagerFactory;
//import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
//import org.springframework.orm.jpa.JpaTransactionManager;
//import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
//import org.springframework.transaction.PlatformTransactionManager;
//
//
//import javax.sql.DataSource;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//
//import org.springframework.context.annotation.Primary;
//
//@Configuration
//public class DynamicDataSourceConfig {
//
//    @Bean
//    @Primary
//    public DataSource dataSource() {
//        AbstractRoutingDataSource routingDataSource = new TenantRoutingDataSource();
//        Map<Object, Object> targetDataSources = new HashMap<>();
//        // 配置公共数据源
//        DataSource commonDataSource = Utils.getDataResource();
//        targetDataSources.put("common", commonDataSource);
//        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = createEntityManagerFactory(commonDataSource, "common");
//        entityManagerFactoryBean.afterPropertiesSet();
//        List<DataSourceProperties> dataSourceProperties = null;//从common数据库获取配置
//        for (DataSourceProperties properties : dataSourceProperties) {
//            DataSource tenantDataSource = Utils.getDataResource(properties);
//            targetDataSources.put(properties.getUrl(), tenantDataSource);
//            LocalContainerEntityManagerFactoryBean entityManagerFactoryBean2 = createEntityManagerFactory(tenantDataSource, properties.getUrl());
//            entityManagerFactoryBean2.afterPropertiesSet();
//        }
//        routingDataSource.setTargetDataSources(targetDataSources);
//
//        return routingDataSource;
//    }
//
//
//    private LocalContainerEntityManagerFactoryBean createEntityManagerFactory(DataSource dataSource,String type) {
//        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//        em.setDataSource(dataSource);
//        em.setPackagesToScan(type.equals("common")?"com.meiyunji.crm.model.entity.common":"com.meiyunji.crm.model.entity.tenant"); // Path to tenant entities
//        return em;
//    }
//
//}

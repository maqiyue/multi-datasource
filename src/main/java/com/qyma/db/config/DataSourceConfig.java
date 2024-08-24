package com.qyma.db.config;

import com.qyma.db.manager.TenantTransactionManager;
import com.qyma.db.manager.MultiRouterManager;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Bean(name = "commonDataSource")
    public DataSource dataSourceCommon() {
        return createDataSource("jdbc:mysql://localhost:3306/common", "root", "Yuege2018....");
    }

    @Bean(name = "multiDataSource")
    public DataSource dataSource(@Qualifier("commonDataSource") DataSource dataSource) {
        MultiRouterManager multiRouterManager = new MultiRouterManager();

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("common", dataSource);

        dataSourceMap.put("1", createDataSource("jdbc:mysql://localhost:3306/tenant_01", "root", "Yuege2018...."));
        dataSourceMap.put("2", createDataSource("jdbc:mysql://localhost:3306/tenant_02", "root", "Yuege2018...."));

        multiRouterManager.setTargetDataSources(dataSourceMap);
        return multiRouterManager;
    }

    private DataSource createDataSource(String url, String username, String password) {
        com.zaxxer.hikari.HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }

    @Bean(name = "common")
    public PlatformTransactionManager transactionManager(@Qualifier("multiDataSource") DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

//    @Bean(name = "tenant")
    @Bean
    @Primary
    public PlatformTransactionManager tenantTransactionManager(@Qualifier("multiDataSource") DataSource dataSource) {
        return new TenantTransactionManager(dataSource);
    }


    @Bean(name = "commonTransactionTemplate")
    public TransactionTemplate commonTransactionTemplate(@Qualifier("common") PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }

    @Bean
    @Primary
    public TransactionTemplate tenantTransactionTemplate(PlatformTransactionManager transactionManager) {
        return new TransactionTemplate(transactionManager);
    }


    @Bean
    public SqlSessionFactory sqlSessionFactory(@Qualifier("multiDataSource") DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
        factoryBean.setDataSource(dataSource);
        return factoryBean.getObject();
    }
}

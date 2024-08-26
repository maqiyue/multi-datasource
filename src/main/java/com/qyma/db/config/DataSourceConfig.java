package com.qyma.db.config;



import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import com.qyma.db.manager.MultiRouterManager;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.TransactionIsolationLevel;
import org.apache.ibatis.transaction.Transaction;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jta.atomikos.AtomikosDataSourceBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;


import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class DataSourceConfig {



    @Bean
    @Primary
    public DataSource dataSource() {
        MultiRouterManager multiRouterManager = new MultiRouterManager();

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put("common", createDataSource("jdbc:mysql://localhost:3306/common", "root", "Yuege2018....","common"));

        dataSourceMap.put("1", createDataSource("jdbc:mysql://localhost:3306/tenant_01", "root", "Yuege2018....","tenant_01"));
        dataSourceMap.put("2", createDataSource("jdbc:mysql://localhost:3306/tenant_02", "root", "Yuege2018....","tenant_02"));

        multiRouterManager.setTargetDataSources(dataSourceMap);
        return multiRouterManager;
    }

    private DataSource createDataSource(String url, String username, String password,String sourceName) {
        Properties prop = new Properties();
        prop.put("url", url);
        prop.put("username", username);
        prop.put("password", password);
        prop.put("driverClassName", "com.mysql.cj.jdbc.Driver");
        AtomikosDataSourceBean xaDataSource = new AtomikosDataSourceBean();
        xaDataSource.setXaDataSourceClassName("com.alibaba.druid.pool.xa.DruidXADataSource");
        xaDataSource.setUniqueResourceName(sourceName);
        xaDataSource.setPoolSize(5);
        xaDataSource.setXaProperties(prop);
        return xaDataSource;
    }
//    private DataSource createDataSource(String url, String username, String password,String sourceName) {
//        com.zaxxer.hikari.HikariDataSource dataSource = new HikariDataSource();
//        dataSource.setJdbcUrl(url);
//        dataSource.setUsername(username);
//        dataSource.setPassword(password);
//        return dataSource;
//    }

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);

        bean.setTransactionFactory(new SpringManagedTransactionFactory(){
            @Override
            public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
                return new MultiDataSourceTransaction2(dataSource);
            }
        });
        bean.setMapperLocations(new PathMatchingResourcePatternResolver().getResources("classpath*:mybatis/**/*.xml"));// 扫描指定目录的xml
        return bean.getObject();
    }

    @Bean
    public UserTransaction userTransaction() throws Throwable {
        UserTransactionImp userTransactionImp = new UserTransactionImp();
        userTransactionImp.setTransactionTimeout(10000);
        return userTransactionImp;
    }

    @Bean
    public TransactionManager atomikosTransactionManager() throws Throwable {
        UserTransactionManager userTransactionManager = new UserTransactionManager();
        userTransactionManager.setForceShutdown(false);
        return userTransactionManager;
    }

    @Bean
    public PlatformTransactionManager transactionManager() throws Throwable {
        return new JtaTransactionManager(userTransaction(), atomikosTransactionManager());
    }


}

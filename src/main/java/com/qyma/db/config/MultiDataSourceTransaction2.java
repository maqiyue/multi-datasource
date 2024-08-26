package com.qyma.db.config;



import com.qyma.db.manager.MultiRouterManager;
import org.apache.ibatis.transaction.Transaction;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MultiDataSourceTransaction2 implements Transaction {

    private final DataSource dataSource;
    private ConcurrentMap<String, Connection> connectionMap;
    private boolean isConnectionTransactional;
    private boolean autoCommit;

    public MultiDataSourceTransaction2(DataSource dataSource) {
        this.dataSource = dataSource;
        this.connectionMap = new ConcurrentHashMap<>();
    }

    @Override
    public Connection getConnection() throws SQLException {
        String databaseIdentification = MultiRouterManager.getDataSourceKey();

        return connectionMap.computeIfAbsent(databaseIdentification, key -> {
            try {
                Connection conn = dataSource.getConnection();
                this.autoCommit = conn.getAutoCommit();
                this.isConnectionTransactional = DataSourceUtils.isConnectionTransactional(conn, dataSource);
                return conn;
            } catch (SQLException ex) {
                throw new CannotGetJdbcConnectionException("Could not get JDBC Connection", ex);
            }
        });
    }

    @Override
    public void commit() throws SQLException {
        if (!isConnectionTransactional && !autoCommit) {
            for (Connection connection : connectionMap.values()) {
                connection.commit();
            }
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (!isConnectionTransactional && !autoCommit) {
            for (Connection connection : connectionMap.values()) {
                connection.rollback();
            }
        }
    }

    @Override
    public void close() throws SQLException {
        for (Connection connection : connectionMap.values()) {
            try {
                DataSourceUtils.releaseConnection(connection, dataSource);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        connectionMap.clear();
    }

    @Override
    public Integer getTimeout() throws SQLException {
        return null;
    }
}


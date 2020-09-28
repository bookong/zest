package com.github.bookong.zest.mock;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * @author Jiang Xu
 */
public class MockConnection implements Connection {

    private Connection conn = null;

    public void setConnection(Connection conn) {
        this.conn = conn;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        if (conn != null) {
            return conn.getMetaData();
        }
        return new MockMetaData();
    }

    @Override
    public Statement createStatement() throws SQLException {
        if (conn != null) {
            return conn.createStatement();
        }
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        if (conn != null) {
            return conn.prepareStatement(sql);
        }
        return null;
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        if (conn != null) {
            return conn.prepareCall(sql);
        }
        return null;
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        if (conn != null) {
            return conn.nativeSQL(sql);
        }
        return null;
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        if (conn != null) {
            return conn.getAutoCommit();

        }
        return false;
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        if (conn != null) {
            conn.setAutoCommit(autoCommit);
        }
    }

    @Override
    public void commit() throws SQLException {
        if (conn != null) {
            conn.commit();
        }
    }

    @Override
    public void rollback() throws SQLException {
        if (conn != null) {
            conn.rollback();
        }
    }

    @Override
    public void close() throws SQLException {
    }

    @Override
    public boolean isClosed() throws SQLException {
        if (conn != null) {
            return conn.isClosed();
        }
        return false;
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        if (conn != null) {
            return conn.isReadOnly();
        }
        return false;
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        if (conn != null) {
            conn.setReadOnly(readOnly);
        }
    }

    @Override
    public String getCatalog() throws SQLException {
        if (conn != null) {
            return conn.getCatalog();
        }
        return null;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        if (conn != null) {
            conn.setCatalog(catalog);
        }
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        if (conn != null) {
            return conn.getTransactionIsolation();
        }
        return 0;
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        if (conn != null) {
            conn.setTransactionIsolation(level);
        }
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        if (conn != null) {
            return conn.getWarnings();
        }
        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        if (conn != null) {
            conn.clearWarnings();
        }
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        if (conn != null) {
            return conn.createStatement(resultSetType, resultSetConcurrency);
        }
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType,
                                              int resultSetConcurrency) throws SQLException {
        if (conn != null) {
            return conn.prepareStatement(sql, resultSetType, resultSetConcurrency);
        }
        return null;
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        if (conn != null) {
            return conn.prepareCall(sql, resultSetType, resultSetConcurrency);
        }
        return null;
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        if (conn != null) {
            return conn.getTypeMap();
        }
        return null;
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        if (conn != null) {
            conn.setTypeMap(map);
        }
    }

    @Override
    public int getHoldability() throws SQLException {
        if (conn != null) {
            return conn.getHoldability();
        }
        return 0;
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        if (conn != null) {
            conn.setHoldability(holdability);
        }
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        if (conn != null) {
            return conn.setSavepoint();
        }
        return null;
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        if (conn != null) {
            return conn.setSavepoint(name);
        }
        return null;
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        if (conn != null) {
            conn.rollback(savepoint);
        }
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        if (conn != null) {
            conn.releaseSavepoint(savepoint);
        }
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency,
                                     int resultSetHoldability) throws SQLException {
        if (conn != null) {
            return conn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        }
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
                                              int resultSetHoldability) throws SQLException {
        if (conn != null) {
            return conn.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }
        return null;
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
                                         int resultSetHoldability) throws SQLException {
        if (conn != null) {
            return conn.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        if (conn != null) {
            return conn.prepareStatement(sql, autoGeneratedKeys);
        }
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        if (conn != null) {
            return conn.prepareStatement(sql, columnIndexes);
        }
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        if (conn != null) {
            return conn.prepareStatement(sql, columnNames);
        }
        return null;
    }

    @Override
    public Clob createClob() throws SQLException {
        if (conn != null) {
            return conn.createClob();
        }
        return null;
    }

    @Override
    public Blob createBlob() throws SQLException {
        if (conn != null) {
            return conn.createBlob();
        }
        return null;
    }

    @Override
    public NClob createNClob() throws SQLException {
        if (conn != null) {
            return conn.createNClob();
        }
        return null;
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        if (conn != null) {
            return conn.createSQLXML();
        }
        return null;
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        if (conn != null) {
            return conn.isValid(timeout);
        }
        return false;
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        if (conn != null) {
            conn.setClientInfo(name, value);
        }
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        if (conn != null) {
            return conn.getClientInfo(name);
        }
        return null;
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        if (conn != null) {
            return conn.getClientInfo();
        }
        return null;
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        if (conn != null) {
            conn.setClientInfo(properties);
        }
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        if (conn != null) {
            return conn.createArrayOf(typeName, elements);
        }
        return null;
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        if (conn != null) {
            return conn.createStruct(typeName, attributes);
        }
        return null;
    }

    @Override
    public String getSchema() throws SQLException {
        if (conn != null) {
            return getSchema();
        }
        return null;
    }

    @Override
    public void setSchema(String schema) throws SQLException {
        if (conn != null) {
            conn.setSchema(schema);
        }
    }

    @Override
    public void abort(Executor executor) throws SQLException {
        if (conn != null) {
            conn.abort(executor);
        }
    }

    @Override
    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        if (conn != null) {
            conn.setNetworkTimeout(executor, milliseconds);
        }
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        if (conn != null) {
            return conn.getNetworkTimeout();
        }
        return 0;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (conn != null) {
            return conn.unwrap(iface);
        }
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (conn != null) {
            return conn.isWrapperFor(iface);
        }
        return false;
    }
}

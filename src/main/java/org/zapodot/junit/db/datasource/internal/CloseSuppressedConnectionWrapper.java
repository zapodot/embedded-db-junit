package org.zapodot.junit.db.datasource.internal;

import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class CloseSuppressedConnectionWrapper implements Connection {

    private final Connection underlyingConnection;

    private CloseSuppressedConnectionWrapper(final Connection underlyingConnection) {
        this.underlyingConnection = underlyingConnection;
    }

    public static Connection forConnection(final Connection connection) {
        return new CloseSuppressedConnectionWrapper(connection);
    }

    @Override
    public Statement createStatement() throws SQLException {
        return underlyingConnection.createStatement();
    }

    @Override
    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        return underlyingConnection.isWrapperFor(iface);
    }

    @Override
    public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
        underlyingConnection.setClientInfo(name, value);
    }

    @Override
    public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
        return underlyingConnection.createArrayOf(typeName, elements);
    }

    @Override
    public void setTypeMap(final Map<String, Class<?>> map) throws SQLException {
        underlyingConnection.setTypeMap(map);
    }

    @Override
    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        underlyingConnection.releaseSavepoint(savepoint);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
        return underlyingConnection.prepareStatement(sql, columnIndexes);
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return underlyingConnection.isReadOnly();
    }

    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return underlyingConnection.prepareCall(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return underlyingConnection.getWarnings();
    }

    @Override
    public void clearWarnings() throws SQLException {
        underlyingConnection.clearWarnings();
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
        return underlyingConnection.prepareStatement(sql, columnNames);
    }

    @Override
    public void close() throws SQLException {

    }

    @Override
    public void rollback(final Savepoint savepoint) throws SQLException {
        underlyingConnection.rollback(savepoint);
    }

    @Override
    public boolean isClosed() throws SQLException {
        return underlyingConnection.isClosed();
    }

    @Override
    public int getHoldability() throws SQLException {
        return underlyingConnection.getHoldability();
    }

    @Override
    public String getClientInfo(final String name) throws SQLException {
        return underlyingConnection.getClientInfo(name);
    }

    @Override
    public void setCatalog(final String catalog) throws SQLException {
        underlyingConnection.setCatalog(catalog);
    }

    @Override
    public void commit() throws SQLException {
        underlyingConnection.commit();
    }

    @Override
    public CallableStatement prepareCall(final String sql) throws SQLException {
        return underlyingConnection.prepareCall(sql);
    }

    @Override
    public void setTransactionIsolation(final int level) throws SQLException {
        underlyingConnection.setTransactionIsolation(level);
    }

    @Override
    public NClob createNClob() throws SQLException {
        return underlyingConnection.createNClob();
    }

    @Override
    public String getCatalog() throws SQLException {
        return underlyingConnection.getCatalog();
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return underlyingConnection.prepareStatement(sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public <T> T unwrap(final Class<T> iface) throws SQLException {
        return underlyingConnection.unwrap(iface);
    }

    @Override
    public Clob createClob() throws SQLException {
        return underlyingConnection.createClob();
    }

    @Override
    public void setReadOnly(final boolean readOnly) throws SQLException {
        underlyingConnection.setReadOnly(readOnly);
    }

    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        return underlyingConnection.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        underlyingConnection.setAutoCommit(autoCommit);
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        return underlyingConnection.getNetworkTimeout();
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        return underlyingConnection.setSavepoint();
    }

    @Override
    public String nativeSQL(final String sql) throws SQLException {
        return underlyingConnection.nativeSQL(sql);
    }

    @Override
    public void setSchema(final String schema) throws SQLException {
        underlyingConnection.setSchema(schema);
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return underlyingConnection.getAutoCommit();
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return underlyingConnection.getTransactionIsolation();
    }

    @Override
    public void setHoldability(final int holdability) throws SQLException {
        underlyingConnection.setHoldability(holdability);
    }

    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        return underlyingConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
        return underlyingConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return underlyingConnection.getClientInfo();
    }

    @Override
    public void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException {
        underlyingConnection.setNetworkTimeout(executor, milliseconds);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        return underlyingConnection.prepareStatement(sql);
    }

    @Override
    public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException {
        return underlyingConnection.createStruct(typeName, attributes);
    }

    @Override
    public boolean isValid(final int timeout) throws SQLException {
        return underlyingConnection.isValid(timeout);
    }

    @Override
    public void setClientInfo(final Properties properties) throws SQLClientInfoException {
        underlyingConnection.setClientInfo(properties);
    }

    @Override
    public String getSchema() throws SQLException {
        return underlyingConnection.getSchema();
    }

    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
        return underlyingConnection.createStatement(resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
        return underlyingConnection.prepareStatement(sql, autoGeneratedKeys);
    }

    @Override
    public void abort(final Executor executor) throws SQLException {
        underlyingConnection.abort(executor);
    }

    @Override
    public Blob createBlob() throws SQLException {
        return underlyingConnection.createBlob();
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return underlyingConnection.getTypeMap();
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return underlyingConnection.getMetaData();
    }

    @Override
    public Savepoint setSavepoint(final String name) throws SQLException {
        return underlyingConnection.setSavepoint(name);
    }

    @Override
    public void rollback() throws SQLException {
        underlyingConnection.rollback();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        return underlyingConnection.createSQLXML();
    }
}

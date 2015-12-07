package org.zapodot.junit.db.internal;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class ConnectionInterceptorTest {

    static class FrenchConnection implements ConnectionProxy, Connection {

        private Connection connection;

        public FrenchConnection(final Connection connection) {
            this.connection = connection;
        }
        @Override
        public Connection getDelegatedConnection() {
            return connection;
        }

        @Override
        public void setDelegatedConnection(final Connection delegatedConnection) {
            this.connection = delegatedConnection;
        }

        @Override
        public Statement createStatement() throws SQLException {
            return null;
        }

        @Override
        public PreparedStatement prepareStatement(final String sql) throws SQLException {
            return null;
        }

        @Override
        public CallableStatement prepareCall(final String sql) throws SQLException {
            return null;
        }

        @Override
        public String nativeSQL(final String sql) throws SQLException {
            return null;
        }

        @Override
        public void setAutoCommit(final boolean autoCommit) throws SQLException {

        }

        @Override
        public boolean getAutoCommit() throws SQLException {
            return false;
        }

        @Override
        public void commit() throws SQLException {

        }

        @Override
        public void rollback() throws SQLException {

        }

        @Override
        public void close() throws SQLException {

        }

        @Override
        public boolean isClosed() throws SQLException {
            return false;
        }

        @Override
        public DatabaseMetaData getMetaData() throws SQLException {
            return null;
        }

        @Override
        public void setReadOnly(final boolean readOnly) throws SQLException {

        }

        @Override
        public boolean isReadOnly() throws SQLException {
            return false;
        }

        @Override
        public void setCatalog(final String catalog) throws SQLException {

        }

        @Override
        public String getCatalog() throws SQLException {
            return null;
        }

        @Override
        public void setTransactionIsolation(final int level) throws SQLException {

        }

        @Override
        public int getTransactionIsolation() throws SQLException {
            return 0;
        }

        @Override
        public SQLWarning getWarnings() throws SQLException {
            return null;
        }

        @Override
        public void clearWarnings() throws SQLException {

        }

        @Override
        public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
            return null;
        }

        @Override
        public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
            return null;
        }

        @Override
        public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency) throws SQLException {
            return null;
        }

        @Override
        public Map<String, Class<?>> getTypeMap() throws SQLException {
            return null;
        }

        @Override
        public void setTypeMap(final Map<String, Class<?>> map) throws SQLException {

        }

        @Override
        public void setHoldability(final int holdability) throws SQLException {

        }

        @Override
        public int getHoldability() throws SQLException {
            return 0;
        }

        @Override
        public Savepoint setSavepoint() throws SQLException {
            return null;
        }

        @Override
        public Savepoint setSavepoint(final String name) throws SQLException {
            return null;
        }

        @Override
        public void rollback(final Savepoint savepoint) throws SQLException {

        }

        @Override
        public void releaseSavepoint(final Savepoint savepoint) throws SQLException {

        }

        @Override
        public Statement createStatement(final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
            return null;
        }

        @Override
        public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
            return null;
        }

        @Override
        public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency, final int resultSetHoldability) throws SQLException {
            return null;
        }

        @Override
        public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
            return null;
        }

        @Override
        public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
            return null;
        }

        @Override
        public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
            return null;
        }

        @Override
        public Clob createClob() throws SQLException {
            return null;
        }

        @Override
        public Blob createBlob() throws SQLException {
            return null;
        }

        @Override
        public NClob createNClob() throws SQLException {
            return null;
        }

        @Override
        public SQLXML createSQLXML() throws SQLException {
            return null;
        }

        @Override
        public boolean isValid(final int timeout) throws SQLException {
            return false;
        }

        @Override
        public void setClientInfo(final String name, final String value) throws SQLClientInfoException {

        }

        @Override
        public void setClientInfo(final Properties properties) throws SQLClientInfoException {

        }

        @Override
        public String getClientInfo(final String name) throws SQLException {
            return null;
        }

        @Override
        public Properties getClientInfo() throws SQLException {
            return null;
        }

        @Override
        public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
            return null;
        }

        @Override
        public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException {
            return null;
        }

        @Override
        public void setSchema(final String schema) throws SQLException {

        }

        @Override
        public String getSchema() throws SQLException {
            return null;
        }

        @Override
        public void abort(final Executor executor) throws SQLException {

        }

        @Override
        public void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException {

        }

        @Override
        public int getNetworkTimeout() throws SQLException {
            return 0;
        }

        @Override
        public <T> T unwrap(final Class<T> iface) throws SQLException {
            return null;
        }

        @Override
        public boolean isWrapperFor(final Class<?> iface) throws SQLException {
            return false;
        }
    }

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Connection connection;


    @Test
    public void testIntercept() throws Exception {

        final FrenchConnection frenchConnection = spy(new FrenchConnection(connection));
        final Method createStatement = Connection.class.getMethod("createStatement");
        ConnectionInterceptor.intercept(createStatement, frenchConnection, new Object[]{});
        verify(connection).createStatement();
        verifyNoMoreInteractions(connection);
    }

    @Test
    public void testInstantiation() throws Exception {
        final Constructor<ConnectionInterceptor> constructor = ConnectionInterceptor.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        assertNotNull(constructor.newInstance());
        constructor.setAccessible(false);
    }

    @Test(expected = IllegalAccessException.class)
    public void testNoInstantiation() throws Exception {
        final Constructor<ConnectionInterceptor> constructor = ConnectionInterceptor.class.getDeclaredConstructor();
        constructor.newInstance();

    }
}
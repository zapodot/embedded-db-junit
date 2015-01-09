package org.zapodot.junit.db.datasource.internal;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class CloseSuppressedConnectionWrapperTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Connection underlyingConnection;

    private Connection connection;

    @Before
    public void setUp() throws Exception {
        this.connection = CloseSuppressedConnectionWrapper.forConnection(underlyingConnection);

    }

    @Test
    public void testCreateStatement() throws Exception {
        connection.createStatement();
        verify(underlyingConnection).createStatement();
    }

    @Test
    public void testIsWrapperFor() throws Exception {
        connection.isWrapperFor(String.class);
        verify(underlyingConnection).isWrapperFor(eq(String.class));
    }

    @Test
    public void testSetClientInfo() throws Exception {
        final String name = "name";
        final String value = "value";
        connection.setClientInfo(name, value);
        verify(underlyingConnection).setClientInfo(eq(name), eq(value));
    }

    @Test
    public void testCreateArrayOf() throws Exception {
        final String typeName = "VARCHAR";
        connection.createArrayOf(typeName, new Object[]{});
        verify(underlyingConnection).createArrayOf(anyString(), any(Object[].class));
    }

    @Test
    public void testSetTypeMap() throws Exception {
        connection.setTypeMap(new HashMap<String, Class<?>>());
        verify(underlyingConnection).setTypeMap(anyMap());
    }

    @Test
    public void testReleaseSavepoint() throws Exception {
        connection.releaseSavepoint(null);
        verify(underlyingConnection).releaseSavepoint(null);
    }

    @Test
    public void testPrepareStatement() throws Exception {
        connection.prepareStatement("");
        verify(underlyingConnection).prepareStatement(anyString());
        reset(underlyingConnection);

        connection.prepareStatement("", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
        verify(underlyingConnection).prepareStatement(anyString(), anyInt(), anyInt());
        reset(underlyingConnection);

    }

    @Test
    public void testIsReadOnly() throws Exception {

    }

    @Test
    public void testPrepareCall() throws Exception {

    }

    @Test
    public void testGetWarnings() throws Exception {

    }

    @Test
    public void testClearWarnings() throws Exception {

    }

    @Test
    public void testPrepareStatement1() throws Exception {

    }

    @Test
    public void testClose() throws Exception {
        connection.close();
        verifyZeroInteractions(underlyingConnection);
    }

    @Test
    public void testRollback() throws Exception {

    }

    @Test
    public void testIsClosed() throws Exception {

    }

    @Test
    public void testGetHoldability() throws Exception {

    }

    @Test
    public void testGetClientInfo() throws Exception {

    }

    @Test
    public void testSetCatalog() throws Exception {

    }

    @Test
    public void testCommit() throws Exception {

    }

    @Test
    public void testPrepareCall1() throws Exception {

    }

    @Test
    public void testSetTransactionIsolation() throws Exception {

    }

    @Test
    public void testCreateNClob() throws Exception {

    }

    @Test
    public void testGetCatalog() throws Exception {

    }

    @Test
    public void testPrepareStatement2() throws Exception {

    }

    @Test
    public void testUnwrap() throws Exception {

    }

    @Test
    public void testCreateClob() throws Exception {

    }

    @Test
    public void testSetReadOnly() throws Exception {

    }

    @Test
    public void testCreateStatement1() throws Exception {

    }

    @Test
    public void testSetAutoCommit() throws Exception {

    }

    @Test
    public void testGetNetworkTimeout() throws Exception {

    }

    @Test
    public void testSetSavepoint() throws Exception {

    }

    @Test
    public void testNativeSQL() throws Exception {

    }

    @Test
    public void testSetSchema() throws Exception {

    }

    @Test
    public void testGetAutoCommit() throws Exception {

    }

    @Test
    public void testGetTransactionIsolation() throws Exception {

    }

    @Test
    public void testSetHoldability() throws Exception {

    }

    @Test
    public void testPrepareCall2() throws Exception {

    }

    @Test
    public void testPrepareStatement3() throws Exception {

    }

    @Test
    public void testGetClientInfo1() throws Exception {

    }

    @Test
    public void testSetNetworkTimeout() throws Exception {

    }

    @Test
    public void testPrepareStatement4() throws Exception {

    }

    @Test
    public void testCreateStruct() throws Exception {

    }

    @Test
    public void testIsValid() throws Exception {

    }

    @Test
    public void testSetClientInfo1() throws Exception {

    }

    @Test
    public void testGetSchema() throws Exception {

    }

    @Test
    public void testCreateStatement2() throws Exception {

    }

    @Test
    public void testPrepareStatement5() throws Exception {

    }

    @Test
    public void testAbort() throws Exception {

    }

    @Test
    public void testCreateBlob() throws Exception {

    }

    @Test
    public void testGetTypeMap() throws Exception {

    }

    @Test
    public void testGetMetaData() throws Exception {

    }

    @Test
    public void testSetSavepoint1() throws Exception {

    }

    @Test
    public void testRollback1() throws Exception {

    }

    @Test
    public void testCreateSQLXML() throws Exception {

    }
}
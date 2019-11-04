package org.zapodot.junit.db.internal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.ResultSet;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
public class CloseSuppressedConnectionTest {

    @Mock
    private Connection underlyingConnection;

    private Connection connection;

    @BeforeEach
    public void setUp() throws Exception {
        this.connection = CloseSuppressedConnectionFactory.createProxy(underlyingConnection);

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
        final String name = "illegalSqlFromResource";
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
        connection.setTypeMap(new HashMap<>());
        verify(underlyingConnection).setTypeMap(ArgumentMatchers.anyMap());
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
    public void testClose() throws Exception {
        connection.close();
        verifyNoInteractions(underlyingConnection);
    }


}
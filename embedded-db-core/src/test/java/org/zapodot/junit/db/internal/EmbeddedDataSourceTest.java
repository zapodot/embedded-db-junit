package org.zapodot.junit.db.internal;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.io.PrintWriter;
import java.sql.Connection;

import static org.junit.Assert.*;

public class EmbeddedDataSourceTest {

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Connection connection;

    @Test
    public void getConnection() {
        assertNotSame(connection, EmbeddedDataSource.create(connection).getConnection());
    }

    @Test
    public void getConnectionWithUserNameAndPassword() {
        assertNotSame(connection, EmbeddedDataSource.create(connection).getConnection("userName", "password"));
    }

    @Test
    public void getLogWriter() {
        final PrintWriter logWriter = EmbeddedDataSource.create(connection).getLogWriter();
        assertNotNull(logWriter);
    }

    @Test
    public void setLogWriter() {
        final EmbeddedDataSource embeddedDataSource = EmbeddedDataSource.create(connection);
        assertNotNull(embeddedDataSource);
        embeddedDataSource.setLogWriter(null);
    }

    @Test
    public void setLoginTimeout() {
        final EmbeddedDataSource embeddedDataSource = EmbeddedDataSource.create(connection);
        assertNotNull(embeddedDataSource);
        final int loginTimeout = 22;
        embeddedDataSource.setLoginTimeout(loginTimeout);
        assertNotEquals(loginTimeout, embeddedDataSource.getLoginTimeout());
    }

    @Test
    public void getLoginTimeout() {
        assertEquals(0, EmbeddedDataSource.create(connection).getLoginTimeout());
    }

    @Test
    public void getParentLogger() {
        assertNotNull(EmbeddedDataSource.create(connection).getParentLogger());
    }

    @Test
    public void isWrapperClass() {
        assertFalse(EmbeddedDataSource.create(connection).isWrapperFor(getClass()));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void unwrap() {
        assertNotNull(EmbeddedDataSource.create(connection).unwrap(getClass()));
    }
}
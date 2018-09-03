package org.zapodot.junit.db.internal;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class SQLInitializationPluginTest {

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Connection connection;

    @Mock
    private Statement statement;

    @Test(expected = IllegalArgumentException.class)
    public void constructionNull() {
        assertNull(new SQLInitializationPlugin(null));
    }

    @Test(expected = IllegalStateException.class)
    public void connectionMadeSQLFails() throws SQLException {
        final SQLInitializationPlugin sqlInitializationPlugin = new SQLInitializationPlugin(
                "INSERT INTO MyTables values(1, 'User');");
        assertNotNull(sqlInitializationPlugin);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.execute(anyString())).thenThrow(new SQLException("Reason"));

        sqlInitializationPlugin.connectionMade("name", connection);

    }
}
package org.zapodot.junit.db.internal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SQLInitializationPluginTest {

    @Mock
    private Connection connection;

    @Mock
    private Statement statement;

    @Test
    public void constructionNull() {
        assertThrows(IllegalArgumentException.class, () -> new SQLInitializationPlugin(null));
    }

    @Test
    public void connectionMadeSQLFails() throws SQLException {
        final SQLInitializationPlugin sqlInitializationPlugin = new SQLInitializationPlugin(
                "INSERT INTO MyTables values(1, 'User');");
        assertNotNull(sqlInitializationPlugin);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.execute(anyString())).thenThrow(new SQLException("Reason"));

        assertThrows(IllegalStateException.class, () -> sqlInitializationPlugin.connectionMade("name", connection));

    }
}
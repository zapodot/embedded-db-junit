package org.zapodot.junit.db.internal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FilePathInitializationPluginTest {

    @Mock
    private Connection connection;

    @Mock
    private Statement statement;

    @Test
    public void resourceMissing() {
        assertThrows(IllegalArgumentException.class, () -> new FilePathInitializationPlugin(null, null));
    }

    @Test
    public void charsetMissing() {
        assertThrows(IllegalArgumentException.class, () -> new FilePathInitializationPlugin("resource", null));
    }

    @Test
    public void connectionMadeDone() throws SQLException {
        final FilePathInitializationPlugin filePathInitializationPlugin = new FilePathInitializationPlugin(
                "classpath:initial.sql",
                StandardCharsets.UTF_8);
        assertNotNull(filePathInitializationPlugin);

        when(connection.createStatement()).thenReturn(statement);
        filePathInitializationPlugin.connectionMade("name", connection);
        verify(connection).createStatement();
        verify(statement).execute(anyString());
        verify(statement).close();
        verifyNoMoreInteractions(connection, statement);
    }

    @Test
    public void connectionSQLExecutionFails() throws SQLException {
        final FilePathInitializationPlugin filePathInitializationPlugin = new FilePathInitializationPlugin(
                "classpath:initial.sql",
                StandardCharsets.UTF_8);
        assertNotNull(filePathInitializationPlugin);

        when(connection.createStatement()).thenReturn(statement);
        when(statement.execute(anyString())).thenThrow(new SQLException("Error"));
        assertThrows(IllegalArgumentException.class, () -> filePathInitializationPlugin.connectionMade("name", connection));
        verify(connection).createStatement();
        verify(statement).execute(anyString());
        verify(statement).close();
        verifyNoMoreInteractions(connection, statement);
    }

    @Test
    public void connectionMadeIllegalPath() {
        final FilePathInitializationPlugin filePathInitializationPlugin = new FilePathInitializationPlugin(
                "classpath:nonexisting.sql",
                StandardCharsets.UTF_8);
        assertNotNull(filePathInitializationPlugin);
        assertThrows(IllegalArgumentException.class, () -> filePathInitializationPlugin.connectionMade("name", connection));
        verifyNoMoreInteractions(connection, statement);
    }
}
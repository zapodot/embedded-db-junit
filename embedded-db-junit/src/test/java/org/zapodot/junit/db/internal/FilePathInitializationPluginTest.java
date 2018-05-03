package org.zapodot.junit.db.internal;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class FilePathInitializationPluginTest {

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Connection connection;

    @Mock
    private Statement statement;

    @Test(expected = IllegalArgumentException.class)
    public void resourceMissing() {
        new FilePathInitializationPlugin(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void charsetMissing() {
        new FilePathInitializationPlugin("resource", null);
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

    @Test(expected = IllegalArgumentException.class)
    public void connectionSQLExecutionFails() throws SQLException {
        final FilePathInitializationPlugin filePathInitializationPlugin = new FilePathInitializationPlugin(
                "classpath:initial.sql",
                StandardCharsets.UTF_8);
        assertNotNull(filePathInitializationPlugin);

        when(connection.createStatement()).thenReturn(statement);
        when(statement.execute(anyString())).thenThrow(new SQLException("Error"));
        try {
            filePathInitializationPlugin.connectionMade("name", connection);
        } finally {
            verify(connection).createStatement();
            verify(statement).execute(anyString());
            verify(statement).close();
            verifyNoMoreInteractions(connection, statement);
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void connectionMadeIllegalPath() {
        final FilePathInitializationPlugin filePathInitializationPlugin = new FilePathInitializationPlugin(
                "classpath:nonexisting.sql",
                StandardCharsets.UTF_8);
        assertNotNull(filePathInitializationPlugin);
        try {
            filePathInitializationPlugin.connectionMade("name", connection);
        } finally {
            verifyNoMoreInteractions(connection, statement);
        }
    }
}
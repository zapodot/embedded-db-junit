package org.zapodot.junit.db;

import org.junit.Rule;
import org.junit.Test;
import org.zapodot.junit.db.plugin.InitializationPlugin;

import java.sql.*;

import static org.junit.Assert.assertFalse;

/**
 * @author zapodot
 */
public class EmbeddedDatabaseRulePluginCloseConnectionTest {

    public static class InitDbPlugin implements InitializationPlugin {
        @Override
        public void connectionMade(final String name, final Connection connection) {
            try (final Statement statement = connection.createStatement()) {
                statement.execute("CREATE TABLE TEST(ID INT PRIMARY KEY, NAME VARCHAR(255) NULL )");
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to init db", e);
            }
        }
    }

    @Rule
    public final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.builder().initializedByPlugin(new InitDbPlugin()).build();

    @Test
    public void connectionShouldNotBeClosed() throws Exception {
        try (final Connection connection = DriverManager.getConnection(embeddedDatabaseRule.getConnectionJdbcUrl());
             final Statement statement = connection.createStatement();
             final ResultSet resultSet = statement.executeQuery("SELECT * FROM TEST")
        ) {
            assertFalse(resultSet.next());
        }

    }
}
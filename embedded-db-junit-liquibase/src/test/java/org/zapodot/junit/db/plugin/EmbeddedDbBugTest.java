package org.zapodot.junit.db.plugin;

import org.junit.Rule;
import org.junit.Test;
import org.zapodot.junit.db.EmbeddedDatabaseRule;

import java.sql.*;

import static org.junit.Assert.*;

/**
 * Adapted by @zapodot from GIST https://gist.github.com/victornoel/bef5ac4e037949594210b8ccf5779d6d
 * @author victornoel
 */
public class EmbeddedDbBugTest {
    @Rule
    public final EmbeddedDatabaseRule dbRule = EmbeddedDatabaseRule.builder()
            .initializedByPlugin(LiquibaseInitializer.builder().withChangelogResource("migrations.xml").build())
            .build();

    private void insert(final Connection conn) throws SQLException {
        try (final Connection connection = conn;
             final Statement statement = connection.createStatement()) {
            final int result = statement.executeUpdate("INSERT INTO TEST(id) VALUES (1)");
            assertEquals(1, result);
            connection.commit();
        }
    }

    private void select(final Connection conn) throws SQLException {
        try (final Connection connection = conn;
             final Statement statement = connection.createStatement();
             final ResultSet resultSet = statement.executeQuery("SELECT * from TEST")) {
            // one and only one result
            assertTrue(resultSet.next());
            assertFalse(resultSet.next());
        }
    }

    @Test
    public void failsButShouldnt() throws Exception {
        insert(dbRule.getConnection());

        select(DriverManager.getConnection(dbRule.getConnectionJdbcUrl()));
    }

    @Test
    public void doesntFailAsExpected() throws Exception {
        insert(dbRule.getConnection());

        select(dbRule.getConnection());
    }

    @Test
    public void doesntFailAsExpected2() throws Exception {
        insert(DriverManager.getConnection(dbRule.getConnectionJdbcUrl()));

        select(DriverManager.getConnection(dbRule.getConnectionJdbcUrl()));
    }

    @Test
    public void doesntFailAsExpected3() throws Exception {
        insert(DriverManager.getConnection(dbRule.getConnectionJdbcUrl()));

        select(dbRule.getConnection());
    }
}

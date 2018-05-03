package org.zapodot.junit.db.plugin;

import org.junit.Rule;
import org.junit.Test;
import org.zapodot.junit.db.EmbeddedDatabaseRule;

import java.sql.Connection;
import java.sql.SQLException;

public class LiquibaseInitializerIllegalMigrationTest {

    @Rule
    public final EmbeddedDatabaseRule databaseRule = EmbeddedDatabaseRule.hsqldb().build();

    @Test(expected = IllegalArgumentException.class)
    public void connectionMade() throws SQLException {
        final LiquibaseInitializer liquibaseInitializer = LiquibaseInitializer.builder()
                                                                              .withChangelogResource(
                                                                                      "example-illegal.xml")
                                                                              .build();
        try (final Connection connection = databaseRule.getConnection()) {
            liquibaseInitializer.connectionMade("name", connection);
        }
    }
}
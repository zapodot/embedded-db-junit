package org.zapodot.junit.db.plugin;

import org.junit.Rule;
import org.junit.Test;
import org.zapodot.junit.db.EmbeddedDatabaseRule;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;

/**
 * Test that shows that when Flyway initializes the schema it quotes the name. This allows reserved words to be used as schema names
 */
public class FlywayInitializerIllegalSchemaNameTest {

    private static final String SCHEMA_NAME = "CREATE";

    @Rule
    public final EmbeddedDatabaseRule embeddedDatabaseRule =
            EmbeddedDatabaseRule.h2()
                                .withName(
                                        "FlywayInitializerIllegalSchemaNameTest")
                                .initializedByPlugin(
                                        new FlywayInitializer.Builder()
                                                .withSchemas(SCHEMA_NAME)
                                                .withLocations(
                                                        "classpath:migrations/")
                                                .build()).build();

    @Test
    public void illegalSchema() throws SQLException {
        try (final Statement statement = embeddedDatabaseRule.getConnection().createStatement();
             final ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM \"" + SCHEMA_NAME + "\".USER")) {
            assertNotNull(resultSet);
            assertTrue(resultSet.next());
        }
    }
}
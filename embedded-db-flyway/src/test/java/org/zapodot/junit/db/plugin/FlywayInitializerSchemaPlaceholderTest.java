package org.zapodot.junit.db.plugin;

import com.google.common.collect.ImmutableMap;
import org.junit.Rule;
import org.junit.Test;
import org.zapodot.junit.db.EmbeddedDatabaseRule;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class FlywayInitializerSchemaPlaceholderTest {

    public static final String SCHEMA_NAME = "myschema";
    public static final String INSTALLED_BY = "someone";
    public static final String HISTORY_TABLE = "schemahistory";
    @Rule
    public final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.builder()
                                                                                 .withMode(
                                                                                   EmbeddedDatabaseRule.CompatibilityMode.Oracle)
                                                                                 .initializedByPlugin(
                                                                                   new FlywayInitializer.Builder()
                                                                                           .withSchemas(SCHEMA_NAME)
                                                                                           .withPlaceholders(
                                                                                                   ImmutableMap.of("schema", SCHEMA_NAME))
                                                                                           .withLocations(
                                                                                                   "classpath:placeholder_migrations/")
                                                                                           .withInstalledBy(
                                                                                                   INSTALLED_BY)
                                                                                           .withTable(HISTORY_TABLE)
                                                                                           .build()).build();

    @Test
    public void checkNumberOfUsers() throws Exception {
        try (final Connection connection = embeddedDatabaseRule.getConnection()) {
            try (final Statement statement = connection.createStatement();
                 final ResultSet resultSet = statement.executeQuery("SELECT count(*) FROM " + SCHEMA_NAME + ".USER")) {
                assertTrue(resultSet.next());
                assertEquals(3, resultSet.getInt(1));
            }

        }
    }
}
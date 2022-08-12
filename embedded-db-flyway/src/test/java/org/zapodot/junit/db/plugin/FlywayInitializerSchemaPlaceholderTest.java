package org.zapodot.junit.db.plugin;

import com.google.common.collect.ImmutableMap;
import org.junit.Rule;
import org.junit.Test;
import org.zapodot.junit.db.common.CompatibilityMode;
import org.zapodot.junit.db.EmbeddedDatabaseRule;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.*;

public class FlywayInitializerSchemaPlaceholderTest {

    private static final String SCHEMA_NAME = "myschema";

    private static final String INSTALLED_BY = "someone";

    private static final String HISTORY_TABLE = "schemahistory";

    @Rule
    public final EmbeddedDatabaseRule embeddedDatabaseRule =
            EmbeddedDatabaseRule.hsqldb()
                                .withMode(
                                        CompatibilityMode.Oracle)
                                .withoutAutoCommit()
                                .initializedByPlugin(
                                        new FlywayInitializer.Builder()
                                                .withSchemas(SCHEMA_NAME)
                                                .withPlaceholders(
                                                        ImmutableMap.of("schema", "\"" + SCHEMA_NAME + "\""))
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
                 final ResultSet resultSet = statement
                         .executeQuery("SELECT count(*) FROM \"" + SCHEMA_NAME + "\".USERS")) {
                assertTrue(resultSet.next());
                assertEquals(3, resultSet.getInt(1));
            }

        }
    }
}
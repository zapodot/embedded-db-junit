package org.zapodot.junit.db.plugin;

import org.junit.Rule;
import org.junit.Test;
import org.zapodot.junit.db.common.CompatibilityMode;
import org.zapodot.junit.db.EmbeddedDatabaseRule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.junit.Assert.*;

/**
 * @author zapodot
 */
public class LiquibaseInitializerWithSchemaNameHyperSQLTest {

    @Rule
    public final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule
            .hsqldb()
            .withMode(CompatibilityMode.MSSQLServer)
            .initializedByPlugin(LiquibaseInitializer.builder()
                                                     .withChangelogResource("example-changelog.xml")
                                                     .withDefaultSchemaName("some_schema_name")
                                                     .build())
            .build();

    @Test
    public void testFindUserTable() throws Exception {
        try (final Connection connection = embeddedDatabaseRule.getConnection();
             final PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM some_schema_name.USER");
             final ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            assertFalse(resultSet.next());
        }

    }
}
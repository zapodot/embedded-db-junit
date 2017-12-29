package org.zapodot.junit.db.plugin;

import org.junit.Rule;
import org.junit.Test;
import org.zapodot.junit.db.EmbeddedDatabaseRule;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author zapodot
 */
public class LiquibaseInitializerLimitTest {

    @Rule
    public final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule
            .builder()
            .withMode(EmbeddedDatabaseRule.CompatibilityMode.MSSQLServer)
            .initializedByPlugin(LiquibaseInitializer.builder()
                    .withChangelogResource("example-changelog.sql")
                    .limitChanges(2)
                    .build())
            .build();

    @Test (expected = SQLException.class)
    public void testShouldNotFindAnything() throws Exception {
        try(final Connection connection = embeddedDatabaseRule.getConnection()) {
            connection.prepareStatement("SELECT * FROM USERROLE");
        }

    }
}
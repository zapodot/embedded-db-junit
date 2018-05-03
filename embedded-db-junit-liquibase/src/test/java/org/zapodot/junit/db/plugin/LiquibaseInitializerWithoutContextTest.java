package org.zapodot.junit.db.plugin;

import org.junit.Rule;
import org.junit.Test;
import org.zapodot.junit.db.CompatibilityMode;
import org.zapodot.junit.db.EmbeddedDatabaseRule;
import org.zapodot.junit.db.plugin.dao.RoleDao;

import java.sql.Connection;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author zapodot
 */
public class LiquibaseInitializerWithoutContextTest {

    @Rule
    public final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule
            .builder()
            .withMode(CompatibilityMode.MSSQLServer)
            .initializedByPlugin(LiquibaseInitializer.builder()
                    .withChangelogResource("example-changelog.sql")
                    .build())
            .build();

    @Test
    public void testFindRolesInsertedByLiquibase() throws Exception {
        try(final Connection connection = embeddedDatabaseRule.getConnection()) {
            final List<String> roles = new RoleDao(connection).rolesForUser("Ada");
            assertEquals(2, roles.size());
        }

    }
}
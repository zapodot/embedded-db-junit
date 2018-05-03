package org.zapodot.junit.db.plugin;

import org.junit.Rule;
import org.junit.Test;
import org.zapodot.junit.db.EmbeddedDatabaseRule;
import org.zapodot.junit.db.plugin.dao.RoleDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author zapodot
 */
public class LiquibaseInitializerWithDbNameAddedToContextsH2Test {

    private static final String DATABASE_NAME = "myDb";

    @Rule
    public final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule
            .h2()
            .withMode(EmbeddedDatabaseRule.CompatibilityMode.MSSQLServer)
            .withName(DATABASE_NAME)
            .initializedByPlugin(LiquibaseInitializer.builder()
                    .withChangelogResource("example-changelog.sql")
                    .addDatabaseNameToContext()
                    .build())
            .build();

    @Test
    public void testFindRolesInsertedByLiquibase() throws Exception {
        try(final Connection connection = embeddedDatabaseRule.getConnection()) {
            final List<String> rolesForUser = new RoleDao(connection).rolesForUser("Ada");
            assertEquals(0, rolesForUser.size());
        }

    }

}
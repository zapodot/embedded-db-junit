package org.zapodot.junit.db.plugin;

import org.junit.Rule;
import org.junit.Test;
import org.zapodot.junit.db.common.CompatibilityMode;
import org.zapodot.junit.db.EmbeddedDatabaseRule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author zapodot
 */
public class LiquibaseInitializerWithContextExcludeTest {

    @Rule
    public final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule
            .builder()
            .withMode(CompatibilityMode.MSSQLServer)
            .initializedByPlugin(LiquibaseInitializer.builder()
                    .withChangelogResource("example-changelog.sql")
                    .withContexts("!addUsersAndRoles")
                    .build())
            .build();

    @Test
    public void testFindRolesInsertedByLiquibase() throws Exception {
        try(final Connection connection = embeddedDatabaseRule.getConnection()) {
            try(final PreparedStatement statement = connection.prepareStatement("Select * FROM ROLE r INNER JOIN USERROLE ur on r.ID = ur.ROLE_ID INNER JOIN USER u on ur.USER_ID = u.ID where u.NAME = ?")) {
                statement.setString(1, "Ada");
                try(final ResultSet resultSet = statement.executeQuery()) {
                    final List<String> roles = new LinkedList<>();
                    while(resultSet.next()) {
                        roles.add(resultSet.getString("illegalSqlFromResource"));
                    }
                    assertEquals(0, roles.size());
                }
            }
        }

    }
}
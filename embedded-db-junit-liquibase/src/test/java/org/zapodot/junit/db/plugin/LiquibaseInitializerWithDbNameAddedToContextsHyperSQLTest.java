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
public class LiquibaseInitializerWithDbNameAddedToContextsHyperSQLTest {

    private static final String DATABASE_NAME = "myDb";

    @Rule
    public final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule
            .hsqldb()
            .withMode(CompatibilityMode.MSSQLServer)
            .withName(DATABASE_NAME)
            .initializedByPlugin(LiquibaseInitializer.builder()
                                                     .withChangelogResource("example-changelog.xml")
                                                     .addDatabaseNameToContext()
                                                     .build())
            .build();

    @Test
    public void testFindRolesInsertedByLiquibase() throws Exception {
        try (final Connection connection = embeddedDatabaseRule.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT r.name from role r inner join userrole ur on r.id = ur.role_id inner join user u on u.id=ur.user_id where u.username = ?")) {
            preparedStatement.setString(1, "Ada");
            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                assertFalse(resultSet.next());
            }
        }

    }

}
package org.zapodot.junit.db;

import org.junit.Rule;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.assertTrue;

/**
 * Created by zapodot on 03.12.2015.
 */
public class EmbeddedDatabaseRuleWithInitSqlFromFileTest {

    @Rule
    public EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.builder()
                                                                           .withInitialSqlFromResource(
                                                                                   "classpath:initial.sql")
                                                                           .build();

    @Test
    public void testWithInitialSQL() throws Exception {
        try (final Connection connection = embeddedDatabaseRule.getConnection()) {

            try (final Statement statement = connection.createStatement()) {
                try (final ResultSet resultSet = statement.executeQuery("SELECT * from PEOPLE")) {
                    assertTrue(resultSet.next());
                }
            }

        }

    }
}

package org.zapodot.junit.db;

import org.junit.Rule;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EmbeddedDatabaseRuleMultipleResourcesTest {

    @Rule
    public final EmbeddedDatabaseRule databaseRule = EmbeddedDatabaseRule.builder().withMode(CompatibilityMode.MySQL)
            .withInitialSqlFromResource("classpath:initial.sql")
            .withInitialSqlFromResource("classpath:initial-pets.sql")
            .build();

    @Test
    public void testMultiple() throws SQLException {
        try (Connection connection = databaseRule.getConnection();
             final Statement peopleStatement = connection.createStatement();
             final Statement petStatement = connection.createStatement();
             final ResultSet peopleResult = peopleStatement.executeQuery("SELECT COUNT(*) FROM PEOPLE");
             final ResultSet petResults = petStatement.executeQuery("SELECT COUNT(*) FROM PETS")) {
            assertTrue(peopleResult.next());
            assertTrue(petResults.next());
            assertEquals(2, peopleResult.getInt(1));
            assertEquals(2, petResults.getInt(1));
        }
    }
}

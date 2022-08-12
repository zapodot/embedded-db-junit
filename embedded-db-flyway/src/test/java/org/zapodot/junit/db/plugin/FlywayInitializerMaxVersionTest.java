package org.zapodot.junit.db.plugin;

import org.junit.Rule;
import org.junit.Test;
import org.zapodot.junit.db.EmbeddedDatabaseRule;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.*;

public class FlywayInitializerMaxVersionTest {

    public static final String INSTALLED_BY = "zapodot";
    @Rule
    public final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.builder()
            .initializedByPlugin(
                    new FlywayInitializer.Builder()
                            .withLocations(
                                    "classpath:migrations/")
                            .withTarget("1.0.1")
                            .withInstalledBy(
                                    INSTALLED_BY)
                            .build()).build();

    @Test
    public void checkNumberOfUsers() throws Exception {
        try (final Connection connection = embeddedDatabaseRule.getConnection()) {
            try (final Statement statement = connection.createStatement();
                 final ResultSet resultSet = statement.executeQuery("SELECT count(*) FROM USERS")) {
                assertTrue(resultSet.next());
                assertEquals(0, resultSet.getInt(1));
            }
        }
    }
}
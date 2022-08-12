package org.zapodot.junit.db.plugin;

import org.junit.Rule;
import org.junit.Test;
import org.zapodot.junit.db.EmbeddedDatabaseRule;
import org.zapodot.junit.db.common.CompatibilityMode;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.Assert.*;

public class FlywayInitializerDefaultSettingsHypersqlTest {

    @Rule
    public final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.hsqldb()
            .withMode(CompatibilityMode.HSQLDB)
            .initializedByPlugin(
                    new FlywayInitializer.Builder()
                            .withLocations("classpath:migrations/")
                            .build()).build();


    @Test
    public void checkMigrationsHasRun() throws Exception {
        try (final Connection connection = embeddedDatabaseRule.getConnection();
             final Statement statement = connection.createStatement();
             final ResultSet resultSet = statement.executeQuery("SELECT * FROM USERS")) {
            assertTrue(resultSet.next());
        }
    }
}
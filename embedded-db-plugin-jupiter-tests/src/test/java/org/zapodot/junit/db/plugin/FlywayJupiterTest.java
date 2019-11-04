package org.zapodot.junit.db.plugin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.zapodot.junit.db.EmbeddedDatabaseExtension;
import org.zapodot.junit.db.common.CompatibilityMode;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.assertTrue;

public class FlywayJupiterTest {

    @RegisterExtension
    public static EmbeddedDatabaseExtension databaseExtension = EmbeddedDatabaseExtension.Builder.hsqldb().withMode(CompatibilityMode.MSSQLServer)
            .initializedByPlugin(FlywayInitializer.builder().withLocations("classpath:migrations/").build())
            .build();

    @DisplayName("RegisterExtension with Flyway")
    @Test
    void testJupiterRegisterWithFlyway() throws SQLException {
        try (final Connection connection = databaseExtension.getConnection();
             final Statement statement = connection.createStatement();
             final ResultSet resultSet = statement.executeQuery("SELECT * FROM USER")) {
            assertTrue(resultSet.next());
        }
    }
}

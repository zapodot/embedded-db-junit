package org.zapodot.junit.db.plugin;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.zapodot.junit.db.EmbeddedDatabaseExtension;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;


public class LiquibaseJupiterTest {

    @RegisterExtension
    public static EmbeddedDatabaseExtension databaseExtension = EmbeddedDatabaseExtension.Builder.hsqldb().initializedByPlugin(
            LiquibaseInitializer.builder()
                    .withDefaultSchemaName("some_schema_name")
                    .withChangelogResource("example-changelog.xml")
                    .build())
            .build();

    @DisplayName("RegisterExtension with Liquibase")
    @Test
    void selectFromDatabase() throws SQLException {
        try (final Connection connection = databaseExtension.getConnection();
             final PreparedStatement preparedStatement = connection
                     .prepareStatement("SELECT * FROM some_schema_name.USER");
             final ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            assertFalse(resultSet.next());
        }
    }
}

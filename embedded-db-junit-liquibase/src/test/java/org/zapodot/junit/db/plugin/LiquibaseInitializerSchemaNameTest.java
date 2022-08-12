package org.zapodot.junit.db.plugin;

import org.junit.Rule;
import org.junit.Test;
import org.zapodot.junit.db.EmbeddedDatabaseRule;
import org.zapodot.junit.db.common.CompatibilityMode;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class LiquibaseInitializerSchemaNameTest {

  private static final String SCHEMA_NAME = "SOME_SCHEMA_NAME";
  @Rule
  public final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule
      .builder()
      .withMode(CompatibilityMode.MSSQLServer)
      .initializedByPlugin(LiquibaseInitializer.builder()
                               .withChangelogResource("example-changelog.xml")
                               .withDefaultSchemaName(SCHEMA_NAME)
                               .build())
      .build();


  @Test
  public void testSchemaDefaultSchemaNameUsed() throws SQLException {
    try (final Connection connection = embeddedDatabaseRule.getConnection();
         final Statement statement = connection.createStatement()
         ) {
      statement.executeQuery(String.format("SELECT * FROM %s.user", SCHEMA_NAME));
    }
  }
}

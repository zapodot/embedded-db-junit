package org.zapodot.junit.db.plugin;

import org.junit.Rule;
import org.junit.Test;
import org.zapodot.junit.db.common.CompatibilityMode;
import org.zapodot.junit.db.EmbeddedDatabaseRule;

import java.sql.Connection;
import java.sql.SQLException;

public class LiquibaseInitializerSchemaNameTest {

  @Rule
  public final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule
      .builder()
      .withMode(CompatibilityMode.MSSQLServer)
      .initializedByPlugin(LiquibaseInitializer.builder()
                               .withChangelogResource("example-changelog.xml")
                               .withDefaultSchemaName("some_schema_name")
                               .build())
      .build();


  @Test
  public void testSchemaDefaultSchemaNameUsed() throws SQLException {
    try (final Connection connection = embeddedDatabaseRule.getConnection()) {
      connection.prepareStatement("SELECT * FROM some_schema_name.USER");
    }
  }
}

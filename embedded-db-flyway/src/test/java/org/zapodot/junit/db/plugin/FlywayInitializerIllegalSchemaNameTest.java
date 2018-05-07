package org.zapodot.junit.db.plugin;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.zapodot.junit.db.EmbeddedDatabaseRule;

import static org.junit.Assert.*;

public class FlywayInitializerIllegalSchemaNameTest {

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Statement statement;

    @Test(expected = IllegalStateException.class)
    public void illegalSchema() throws Throwable {
        final Description testDescription = Description.createTestDescription(getClass(), "Flyway");
        final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.h2()
                                                                              .initializedByPlugin(
                                                                                      new FlywayInitializer.Builder()
                                                                                              .withSchemas("SELECT",
                                                                                                           "SCHEMA")
                                                                                              .withLocations(
                                                                                                      "classpath:migrations/")
                                                                                              .build()).build();
        final Statement appliedStatement = embeddedDatabaseRule.apply(statement, testDescription);
        assertNotNull(appliedStatement);
        appliedStatement.evaluate();
    }
}
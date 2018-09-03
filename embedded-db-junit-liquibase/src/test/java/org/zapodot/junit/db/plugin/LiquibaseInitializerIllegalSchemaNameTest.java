package org.zapodot.junit.db.plugin;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.zapodot.junit.db.common.CompatibilityMode;
import org.zapodot.junit.db.EmbeddedDatabaseRule;

import static org.junit.Assert.*;

public class LiquibaseInitializerIllegalSchemaNameTest {

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Statement statement;

    @Test(expected = IllegalStateException.class)
    public void illegalSchemaName() throws Throwable {
        final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule
                .h2()
                .withMode(CompatibilityMode.MSSQLServer)
                .initializedByPlugin(LiquibaseInitializer.builder()
                                                         .withChangelogResource("example-changelog.xml")
                                                         .withDefaultSchemaName("SELECT")
                                                         .build())
                .build();
        assertNotNull(embeddedDatabaseRule);
        final Statement appliedStatement = embeddedDatabaseRule
                .apply(statement, Description.createTestDescription(getClass(), "Liquibase"));
        assertNotNull(appliedStatement);
        appliedStatement.evaluate();
    }
}
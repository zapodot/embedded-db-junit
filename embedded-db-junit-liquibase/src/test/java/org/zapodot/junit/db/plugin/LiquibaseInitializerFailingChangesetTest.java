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

public class LiquibaseInitializerFailingChangesetTest {

    @Mock
    private Statement statement;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test(expected = IllegalArgumentException.class)
    public void stuff() throws Throwable {
        final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.h2().initializedByPlugin(
                LiquibaseInitializer.builder().withChangelogResource("example-fails.xml").build()).build();
        final Description testDescription = Description.createTestDescription(getClass(), "Test");
        final Statement appliedStatement = embeddedDatabaseRule.apply(statement, testDescription);
        assertNotNull(appliedStatement);
        appliedStatement.evaluate();
    }
}
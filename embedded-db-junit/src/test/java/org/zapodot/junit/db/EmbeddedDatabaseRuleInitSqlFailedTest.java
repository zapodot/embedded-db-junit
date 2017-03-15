package org.zapodot.junit.db;

import org.h2.jdbc.JdbcSQLException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

/**
 * @author zapodot at gmail dot com
 */
public class EmbeddedDatabaseRuleInitSqlFailedTest {

    @Mock
    private Statement statement;

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Test(expected = JdbcSQLException.class)
    public void name() throws Throwable {
        final EmbeddedDatabaseRule rule = EmbeddedDatabaseRule.builder().withInitialSqlFromResource("classpath:illegal.sql").build();
        final Description testDescription = Description.createTestDescription(getClass(), "Test");
        final Statement testStatement = rule.apply(statement, testDescription);
        testStatement.evaluate();

    }
}
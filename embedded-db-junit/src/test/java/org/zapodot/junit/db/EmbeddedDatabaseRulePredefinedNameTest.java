package org.zapodot.junit.db;

import org.hamcrest.CoreMatchers;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Note: Having tests that are dependent on other tests is an anti-pattern and should be avoided. As these two tests is
 * run sequentially the datasource is still reset properly between the tests. If we had invoked JDBC calls from
 * different threads the results may have been different. This is however hard to write tests with consistent
 * behaviour to prove this.
 *
 * @author zapodot
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EmbeddedDatabaseRulePredefinedNameTest {

    @Rule
    public final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.builder()
                                                                                 .withName("predefined")
                                                                                 .withInitialSql("CREATE TABLE A (id INT PRIMARY KEY, NAME VARCHAR(255) NOT NULL)")
                                                                                 .build();

    @Test
    public void testA() throws Exception {
        try (final Connection connection = embeddedDatabaseRule.getConnection();
             final PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO A VALUES (?, ?)")) {
            insertStatement.setInt(1, 1);
            insertStatement.setString(2, "NAME");
            final int updatedRows = insertStatement.executeUpdate();
            assertThat(updatedRows, equalTo(1));
        }

    }

    @Test
    public void testB() throws Exception {
        try (final Connection connection = embeddedDatabaseRule.getConnection();
             final PreparedStatement selectStatement = connection.prepareStatement("SELECT COUNT(*) FROM A")) {
            final ResultSet result = selectStatement.executeQuery();
            assertThat(result.next(), CoreMatchers.equalTo(true));
            assertThat(result.getInt(1), equalTo(0));
        }

    }
}
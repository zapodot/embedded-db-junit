package org.zapodot.junit.db;

import org.junit.Rule;
import org.junit.Test;
import org.zapodot.junit.db.common.CompatibilityMode;
import org.zapodot.junit.db.internal.HyperSqlJdbcUrlFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.Assert.*;

public class EmbeddedDatabaseRuleHsqlTest {

    @Rule
    public final EmbeddedDatabaseRule databaseRule = EmbeddedDatabaseRule.hsqldb()
                                                                         .withMode(CompatibilityMode.MSSQLServer)
                                                                         .build();

    @Test
    public void hsqldbConnection() {
        assertEquals(HyperSqlJdbcUrlFactory.HSQLDB_MEM_URL + getClass().getSimpleName() + ";sql.syntax_mss=true",
                     databaseRule.getConnectionJdbcUrl());
    }

    @Test
    public void mssqlCompatibility() {
        final String testString = "A test string";
        try (final Connection connection = databaseRule.getConnection();
             final Statement statement = connection.createStatement();
             final ResultSet resultSet = statement.executeQuery("Select '" + testString + "'")) {
            assertTrue(resultSet.next());
            assertEquals(testString, resultSet.getString(1));
        } catch (SQLException e) {
            fail("Could not execute SQL due to an exception \"" + e.getMessage() + "\"");
        }
    }
}
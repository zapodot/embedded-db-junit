package org.zapodot.junit.db;

import org.junit.Rule;
import org.junit.Test;

import java.sql.Connection;

import static org.junit.Assert.assertTrue;

public class EmbeddedDatabaseRuleConnectionClose {

    @Rule
    public final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.builder().withMode("ORACLE").withInitialSql(
            "CREATE TABLE Customer(id INTEGER PRIMARY KEY, name VARCHAR(512)); "
            + "INSERT INTO CUSTOMER(id, name) VALUES (1, 'John Doe')").build();

    @Test
    public void testBlah() throws Exception {
        final Connection connection = embeddedDatabaseRule.getConnection();
        connection.close();
        assertTrue(connection.createStatement().executeQuery("SELECT * from CUSTOMER").isBeforeFirst());

    }
}

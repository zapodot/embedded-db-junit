package org.zapodot.junit.db;

import org.junit.Rule;
import org.junit.Test;

import java.sql.Connection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class EmbeddedDatabaseRuleSimpleTest {

    @Rule
    public EmbeddedDatabaseRule embeddedDatabaseRule = new EmbeddedDatabaseRule();

    @Test
    public void testCanConnect() throws Exception {

        try(final Connection connection = embeddedDatabaseRule.getConnection()) {
            assertFalse(connection.isClosed());
            assertEquals("H2", connection.getMetaData().getDatabaseProductName());

        }

    }
}
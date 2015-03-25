package org.zapodot.junit.db;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class EmbeddedDatabaseRuleTest {

    @Test
    public void testFilterNullProperties() throws Exception {
        final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.builder().withMode(null).build();

        assertEquals("jdbc:h2:mem:test", embeddedDatabaseRule.generateJdbcUrl("test"));

    }

    @Test
    public void testModeSet() throws Exception {
        final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.builder().withMode("ORACLE").build();

        assertEquals("jdbc:h2:mem:test;MODE=ORACLE", embeddedDatabaseRule.generateJdbcUrl("test"));
    }

    @Test
    public void testProperty() throws Exception {

        final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.builder().withProperty("MODE", "ORACLE").build();
        assertEquals("jdbc:h2:mem:test;MODE=ORACLE", embeddedDatabaseRule.generateJdbcUrl("test"));

    }
}
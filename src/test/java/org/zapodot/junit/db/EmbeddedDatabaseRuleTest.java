package org.zapodot.junit.db;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class EmbeddedDatabaseRuleTest {

    public static final String TEST_NAME = "test";

    @Test
    public void testFilterNullProperties() throws Exception {
        final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.builder().withName(TEST_NAME).withMode(null).build();

        assertEquals("jdbc:h2:mem:test", embeddedDatabaseRule.getConnectionJdbcUrl());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitialSqlNull() throws Exception {
        EmbeddedDatabaseRule.builder().withName(TEST_NAME).withInitialSql(null);
    }

    @Test
    public void testInitialSqlNotNull() throws Exception {

        final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.builder().withName(TEST_NAME).withInitialSql("CREATE TABLE foo(bar int primary key)").build();
        assertFalse(embeddedDatabaseRule.getConnectionJdbcUrl().contains("INIT"));

    }

    @Test
    public void testPropertyNull() throws Exception {
        final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.builder().withName(TEST_NAME).withProperty("property", null).build();
        assertEquals("jdbc:h2:mem:test", embeddedDatabaseRule.getConnectionJdbcUrl());

    }

    @Test
    public void testModeSet() throws Exception {
        final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.builder().withName("test").withMode("ORACLE").build();

        assertEquals("jdbc:h2:mem:test;MODE=ORACLE", embeddedDatabaseRule.getConnectionJdbcUrl());
    }

    @Test
    public void testProperty() throws Exception {

        final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.builder().withName(TEST_NAME).withProperty("MODE", "ORACLE").build();
        assertEquals("jdbc:h2:mem:test;MODE=ORACLE", embeddedDatabaseRule.getConnectionJdbcUrl());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitResourcesNull() throws Exception {
        EmbeddedDatabaseRule.builder().withInitialSqlFromResource(null);
    }
}
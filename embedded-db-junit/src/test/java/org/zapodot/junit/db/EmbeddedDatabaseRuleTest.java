package org.zapodot.junit.db;

import org.junit.Test;

import static org.junit.Assert.*;

public class EmbeddedDatabaseRuleTest {

    public static final String TEST_NAME = "test";

    @Test (expected = IllegalArgumentException.class)
    public void testFilterNullProperties() {
        final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.builder().withName(TEST_NAME).withMode((String) null).build();

        assertEquals("jdbc:h2:mem:test", embeddedDatabaseRule.getConnectionJdbcUrl());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitialSqlNull() {
        EmbeddedDatabaseRule.builder().withName(TEST_NAME).withInitialSql(null);
    }

    @Test
    public void testInitialSqlNotNull() {

        final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.builder().withName(TEST_NAME).withInitialSql("CREATE TABLE foo(bar int primary key)").build();
        assertFalse(embeddedDatabaseRule.getConnectionJdbcUrl().contains("INIT"));

    }

    @Test
    public void testPropertyNull() {
        final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.builder().withName(TEST_NAME).withProperty("property", null).build();
        assertEquals("jdbc:h2:mem:test", embeddedDatabaseRule.getConnectionJdbcUrl());

    }

    @Test(expected = IllegalArgumentException.class)
    public void withInitialSqlFromResourceNullBoth() {
        final EmbeddedDatabaseRule.Builder builder = EmbeddedDatabaseRule.h2();
        assertNotNull(builder);
        builder.withInitialSqlFromResource(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void withInitialSqlFromResourceNullCharset() {
        final EmbeddedDatabaseRule.Builder builder = EmbeddedDatabaseRule.h2();
        assertNotNull(builder);
        builder.withInitialSqlFromResource("filename.sql", null);
    }

    @Test
    public void testModeSet() {
        final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.builder().withName("test").withMode(CompatibilityMode.Oracle).build();

        assertEquals("jdbc:h2:mem:test;MODE=Oracle", embeddedDatabaseRule.getConnectionJdbcUrl());
    }

    @Test
    public void testProperty() {

        final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.builder().withName(TEST_NAME).withProperty("MODE", "ORACLE").build();
        assertEquals("jdbc:h2:mem:test;MODE=ORACLE", embeddedDatabaseRule.getConnectionJdbcUrl());

    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitResourcesNull() {
        EmbeddedDatabaseRule.builder().withInitialSqlFromResource(null);
    }
}
package org.zapodot.junit.db;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.*;

public class EmbeddedDatabaseRuleBuilderTest {

    @SuppressWarnings("deprecation")
    @Test
    public void instance() {
        assertNotNull(EmbeddedDatabaseRule.Builder.instance());
    }

    @Test
    public void h2() {
        assertNotNull(EmbeddedDatabaseRule.Builder.h2());
    }

    @Test
    public void hsqldb() {
        assertNotNull(EmbeddedDatabaseRule.Builder.hsqldb());
    }

    @Test
    public void withPropertyNull() {
        final String name = "name";
        assertEquals("jdbc:h2:mem:" + name, EmbeddedDatabaseRule.h2().withName(name).withProperty("prop", null).build().getConnectionJdbcUrl());
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullStringCompatibilityMode() {
        final EmbeddedDatabaseRule.Builder builder = EmbeddedDatabaseRule.hsqldb();
        assertNotNull(builder);
        builder.withMode((String)null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullCompatibilityMode() {
        final EmbeddedDatabaseRule.Builder builder = EmbeddedDatabaseRule.hsqldb();
        assertNotNull(builder);
        builder.withMode((CompatibilityMode) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void illegalCompatibilityMode() {
        final EmbeddedDatabaseRule.Builder builder = EmbeddedDatabaseRule.h2();
        assertNotNull(builder);
        builder.withMode("something").build();
    }

    @Test
    public void emptyCompatibilityMode() {
        final EmbeddedDatabaseRule.Builder builder = EmbeddedDatabaseRule.h2();
        assertNotNull(builder);
        final EmbeddedDatabaseRule embeddedDatabaseRule = builder.withMode("").withName("name").build();
        assertNotNull(embeddedDatabaseRule);
        final String connectionJdbcUrl = embeddedDatabaseRule.getConnectionJdbcUrl();
        assertNotNull(connectionJdbcUrl);
        assertThat(connectionJdbcUrl, not(containsString("MODE")));
    }

    @Test
    public void validCompatibilityMode() {
        final EmbeddedDatabaseRule h2DatabaseRule = EmbeddedDatabaseRule.h2().withMode("Oracle").withName("name").build();
        final String connectionJdbcUrl = h2DatabaseRule.getConnectionJdbcUrl();
        assertNotNull(connectionJdbcUrl);
        assertThat(connectionJdbcUrl, containsString("MODE"));
    }
}
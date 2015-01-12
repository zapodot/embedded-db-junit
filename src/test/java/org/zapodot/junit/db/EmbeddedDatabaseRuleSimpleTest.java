package org.zapodot.junit.db;

import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class EmbeddedDatabaseRuleSimpleTest {

    @Rule
    public EmbeddedDatabaseRule embeddedDatabaseRule = new EmbeddedDatabaseRule();

    @Test
    public void testCanConnect() throws Exception {
        assertNotNull(embeddedDatabaseRule.getConnection());

    }
}
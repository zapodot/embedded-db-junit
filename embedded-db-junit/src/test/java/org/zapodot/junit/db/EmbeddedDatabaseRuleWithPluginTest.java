package org.zapodot.junit.db;

import org.junit.Rule;
import org.junit.Test;
import org.zapodot.junit.db.plugin.InitializationPlugin;

import java.sql.Connection;

import static org.junit.Assert.assertEquals;

/**
 * @author zapodot
 */
public class EmbeddedDatabaseRuleWithPluginTest {

    private static final AssertPlugin assertPlugin = new AssertPlugin();

    @Rule
    public EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.builder().initializedByPlugin(assertPlugin).build();

    private static class AssertPlugin implements InitializationPlugin {

        public boolean connectionIsMade = true;

        @Override
        public void connectionMade(final String name, final Connection connection) {
            connectionIsMade = connection != null;
        }
    }

    @Test
    public void testWhetherPluginIsRun() throws Exception {
        assertEquals(true, assertPlugin.connectionIsMade);

    }
}
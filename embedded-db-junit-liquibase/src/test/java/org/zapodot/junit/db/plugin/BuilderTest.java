package org.zapodot.junit.db.plugin;

import org.junit.Test;

/**
 * @author zapodot
 */
public class BuilderTest {

    @Test(expected = IllegalArgumentException.class)
    public void testNoChangelogProvided() throws Exception {
        LiquibaseInitializer.builder().build();

    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonExistingChangelogProvided() throws Exception {
        LiquibaseInitializer.builder().withChangelogResource("nonexisting.yml").build();

    }


}
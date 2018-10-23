package org.zapodot.junit.db;

import org.junit.Test;

import static org.junit.Assert.*;

public class VersionTest {

    @Test
    public void props() {
        final Version version = new Version();
        assertNotNull(version);
        assertEquals("org.zapodot", version.getGroupId());
        assertEquals("embedded-db-core", version.getArtifactId());
        assertNotNull(version.getVersion());
        assertNotEquals("", version.getVersion());
        assertNotNull(version.getGitBranch());
        assertNotEquals("", version.getGitBranch());
        assertNotNull(version.getGitCommit());
        assertNotEquals("", version.getGitCommit());
    }

    @Test(expected = IllegalStateException.class)
    public void loadPropsNotFound() {
        assertNotNull(Version.loadPropertiesFile("tsss"));
    }
}
package org.zapodot.junit.db;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    public void loadPropsNotFound() {
        assertThrows(IllegalStateException.class, () -> Version.loadPropertiesFile("tsss"));
    }
}
package org.zapodot.junit.db;

import org.junit.Test;

import static org.junit.Assert.*;

public class VersionTest {

    @Test
    public void versionsPropertiesLoadedFromCoreModule() {
        assertNotNull(new Version());
    }
}

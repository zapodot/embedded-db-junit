package org.zapodot.junit.db.common;

import org.junit.Test;

import java.util.EnumSet;

import static org.junit.Assert.*;

public class EngineTest {

    @Test
    public void hasTwoItems() {
        assertEquals(2, EnumSet.allOf(Engine.class).size());
    }

    @Test
    public void hasH2() {
        assertNotNull(Engine.H2);
    }

    @Test
    public void hasHSQLDB() {
        assertNotNull(Engine.HSQLDB);
    }
}
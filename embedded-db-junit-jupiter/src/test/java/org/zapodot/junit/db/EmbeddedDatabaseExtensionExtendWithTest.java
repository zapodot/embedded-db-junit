package org.zapodot.junit.db;

import org.junit.jupiter.api.Test;
import org.zapodot.junit.db.annotations.EmbeddedDatabase;
import org.zapodot.junit.db.annotations.EmbeddedDatabaseTest;
import org.zapodot.junit.db.common.Engine;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

@EmbeddedDatabaseTest(engine = Engine.HSQLDB)
class EmbeddedDatabaseExtensionExtendWithTest {


    @EmbeddedDatabase
    private DataSource dataSource;

    @Test
    void databaseCall() {
        assertNotNull(dataSource);
    }
}
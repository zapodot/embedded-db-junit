package org.zapodot.junit.db;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class EmbeddedDatabaseExtensionRegisterExtensionHSQLDBTest {

    @RegisterExtension
    static EmbeddedDatabaseExtension embeddedDatabaseExtension = EmbeddedDatabaseExtension.Builder.hsqldb().build();


    @Test
    void doDatabaseCall() throws SQLException {
        assertEquals("HSQL Database Engine", embeddedDatabaseExtension.getConnection().getMetaData().getDatabaseProductName());
    }

}
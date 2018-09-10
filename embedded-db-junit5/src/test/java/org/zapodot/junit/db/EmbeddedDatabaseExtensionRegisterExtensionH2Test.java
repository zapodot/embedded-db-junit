package org.zapodot.junit.db;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.zapodot.junit.db.annotations.EmbeddedDatabase;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class EmbeddedDatabaseExtensionRegisterExtensionH2Test {

    @RegisterExtension
    static EmbeddedDatabaseExtension embeddedDatabaseExtension = EmbeddedDatabaseExtension.Builder.h2().build();

    @EmbeddedDatabase
    private DataSource injectableDataSource;

    @Test
    void doDatabaseCall() throws SQLException {
        assertEquals("H2", embeddedDatabaseExtension.getConnection().getMetaData().getDatabaseProductName());
    }

    @Test
    void injectedDataSource() {
        assertNotNull(injectableDataSource);
    }
}
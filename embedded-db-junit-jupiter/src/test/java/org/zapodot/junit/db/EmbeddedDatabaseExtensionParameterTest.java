package org.zapodot.junit.db;

import org.junit.jupiter.api.Test;
import org.zapodot.junit.db.annotations.EmbeddedDatabase;
import org.zapodot.junit.db.annotations.EmbeddedDatabaseTest;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EmbeddedDatabaseTest
class EmbeddedDatabaseExtensionParameterTest {
    @Test
    void dataSourceAsParameter(@EmbeddedDatabase final DataSource dataSource) {
        assertNotNull(dataSource);
    }

    @Test
    void connectionAsParameter(@EmbeddedDatabase final Connection connection) {
        assertNotNull(connection);
    }

    @Test
    void connectionAsString(@EmbeddedDatabase final String connectionUri) {
        assertNotNull(connectionUri);
        assertTrue(connectionUri.startsWith("jdbc:"));
    }
}
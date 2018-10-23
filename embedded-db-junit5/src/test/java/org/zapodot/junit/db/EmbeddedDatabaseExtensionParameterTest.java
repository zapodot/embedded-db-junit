package org.zapodot.junit.db;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.zapodot.junit.db.annotations.DataSourceConfig;
import org.zapodot.junit.db.annotations.EmbeddedDatabase;
import org.zapodot.junit.db.common.Engine;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(EmbeddedDatabaseExtension.class)
@DataSourceConfig(engine = Engine.HSQLDB)
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
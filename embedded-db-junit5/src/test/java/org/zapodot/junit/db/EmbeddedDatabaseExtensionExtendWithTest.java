package org.zapodot.junit.db;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.zapodot.junit.db.annotations.DataSourceConfig;
import org.zapodot.junit.db.common.Engine;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(EmbeddedDatabaseExtension.class)
@DataSourceConfig(
        engine = Engine.HSQLDB
)
class EmbeddedDatabaseExtensionExtendWithTest {


    private DataSource dataSource;

    @Test
    void databaseCall() {
        assertNotNull(dataSource);
    }
}
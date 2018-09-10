package org.zapodot.junit.db.internal;

import org.junit.Test;
import org.zapodot.junit.db.common.CompatibilityMode;
import org.zapodot.junit.db.plugin.InitializationPlugin;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.*;

public class EmbeddedDatabaseCreatorImplTest {

    @Test(expected = NullPointerException.class)
    public void getConnection() {
        final RealEmbeddedDatabaseCreatorImpl realEmbeddedDatabaseCreator = new RealEmbeddedDatabaseCreatorImpl(false,
                                                                                                                "name",
                                                                                                                Collections
                                                                                                                        .emptyMap(),
                                                                                                                Collections
                                                                                                                        .emptyMap(),
                                                                                                                new H2JdbcUrlFactory(),
                                                                                                                CompatibilityMode.DB2);
        assertNotNull(realEmbeddedDatabaseCreator);
        realEmbeddedDatabaseCreator.getConnection();
    }

    @Test
    public void getDataSource() {
        final RealEmbeddedDatabaseCreatorImpl realEmbeddedDatabaseCreator = new RealEmbeddedDatabaseCreatorImpl(false,
                                                                                                                "name",
                                                                                                                Collections
                                                                                                                        .emptyMap(),
                                                                                                                Collections
                                                                                                                        .emptyMap(),
                                                                                                                new H2JdbcUrlFactory(),
                                                                                                                CompatibilityMode.DB2);
        assertNotNull(realEmbeddedDatabaseCreator);
        assertNotNull(realEmbeddedDatabaseCreator.getDataSource());
    }

    @Test
    public void isAutoCommit() {
        final RealEmbeddedDatabaseCreatorImpl databaseCreator = new RealEmbeddedDatabaseCreatorImpl(false,
                                                                                                    "name",
                                                                                                    Collections
                                                                                                            .emptyMap(),
                                                                                                    Collections
                                                                                                            .emptyMap(),
                                                                                                    new H2JdbcUrlFactory(),
                                                                                                    CompatibilityMode.DB2);
        assertFalse(databaseCreator.isAutoCommit());
    }

    @Test
    public void getConnectionJdbcUrl() {
        final String name = "name";
        final RealEmbeddedDatabaseCreatorImpl databaseCreator = new RealEmbeddedDatabaseCreatorImpl(false,
                                                                                                    name,
                                                                                                    Collections
                                                                                                            .emptyMap(),
                                                                                                    Collections
                                                                                                            .emptyMap(),
                                                                                                    new H2JdbcUrlFactory(),
                                                                                                    CompatibilityMode.DB2);
        assertEquals(H2JdbcUrlFactory.H2_IN_MEMORY_JDBC_URL_PREFIX + name + ";MODE=DB2",
                     databaseCreator.getConnectionJdbcUrl());
    }

    @Test
    public void setupConnection() throws SQLException {
        final RealEmbeddedDatabaseCreatorImpl databaseCreator = new RealEmbeddedDatabaseCreatorImpl(false,
                                                                                                    "name",
                                                                                                    Collections
                                                                                                            .emptyMap(),
                                                                                                    Collections
                                                                                                            .emptyMap(),
                                                                                                    new H2JdbcUrlFactory(),
                                                                                                    CompatibilityMode.DB2);
        databaseCreator.setupConnection("name");
        assertNotNull(databaseCreator.getConnection());
        takeDownConnection();
    }

    @Test
    public void takeDownConnection() {
    }

    private static class RealEmbeddedDatabaseCreatorImpl extends EmbeddedDatabaseCreatorImpl {
        public RealEmbeddedDatabaseCreatorImpl(final boolean autoCommit,
                                               final String name,
                                               final Map<String, String> jdbcUrlProperties,
                                               final Map<Class<? extends InitializationPlugin>, InitializationPlugin> initializationPlugins,
                                               final JdbcUrlFactory jdbcUrlFactory,
                                               final CompatibilityMode compatibilityMode) {
            super(autoCommit, name, jdbcUrlProperties, initializationPlugins, jdbcUrlFactory, compatibilityMode);
        }
    }
}
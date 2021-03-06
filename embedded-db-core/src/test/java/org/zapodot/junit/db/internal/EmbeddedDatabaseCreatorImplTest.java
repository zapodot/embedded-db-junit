package org.zapodot.junit.db.internal;

import org.junit.jupiter.api.Test;
import org.zapodot.junit.db.common.CompatibilityMode;
import org.zapodot.junit.db.plugin.InitializationPlugin;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class EmbeddedDatabaseCreatorImplTest {

    @Test
    public void getConnection() {
        final RealEmbeddedDatabaseCreatorImpl realEmbeddedDatabaseCreator = new RealEmbeddedDatabaseCreatorImpl(false,
                                                                                                                "name",
                                                                                                                Collections
                                                                                                                        .emptyMap(),
                                                                                                                Collections
                                                                                                                        .emptyList(),
                                                                                                                new H2JdbcUrlFactory(),
                                                                                                                CompatibilityMode.DB2);
        assertNotNull(realEmbeddedDatabaseCreator);
        assertThrows(NullPointerException.class, () -> realEmbeddedDatabaseCreator.getConnection());
    }

    @Test
    public void getDataSource() {
        final RealEmbeddedDatabaseCreatorImpl realEmbeddedDatabaseCreator = new RealEmbeddedDatabaseCreatorImpl(false,
                                                                                                                "name",
                                                                                                                Collections
                                                                                                                        .emptyMap(),
                                                                                                                Collections
                                                                                                                        .emptyList(),
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
                                                                                                            .emptyList(),
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
                                                                                                            .emptyList(),
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
                                                                                                            .emptyList(),
                                                                                                    new H2JdbcUrlFactory(),
                                                                                                    CompatibilityMode.DB2);
        databaseCreator.setupConnection("name");
        assertNotNull(databaseCreator.getConnection());
        databaseCreator.takeDownConnection();
    }

    @Test
    public void illegalJdbcUrl() {
        final RealEmbeddedDatabaseCreatorImpl databaseCreator = new RealEmbeddedDatabaseCreatorImpl(false,
                                                                                                    "name",
                                                                                                    Collections
                                                                                                            .emptyMap(),
                                                                                                    Collections
                                                                                                            .emptyList(),
                                                                                                    new IllegalJdbcUrlFactory(),
                                                                                                    CompatibilityMode.DB2);
        assertThrows(SQLException.class, () -> databaseCreator.setupConnection("name"));
    }

    private static class IllegalJdbcUrlFactory implements JdbcUrlFactory {
        @Override
        public String connectionUrlForInitialization(final String name, final Map<String, String> properties) {
            return "jdbc:stuff:notworks";
        }

        @Override
        public String connectionUrl(final String name, final Map<String, String> properties) {
            return connectionUrlForInitialization(name, properties);
        }

        @Override
        public Map<String, String> compatibilityModeParam(final CompatibilityMode compatibilityMode) {
            return Collections.emptyMap();
        }
    }
    private static class RealEmbeddedDatabaseCreatorImpl extends EmbeddedDatabaseCreatorImpl {
        public RealEmbeddedDatabaseCreatorImpl(final boolean autoCommit,
                                               final String name,
                                               final Map<String, String> jdbcUrlProperties,
                                               final List<InitializationPlugin> initializationPlugins,
                                               final JdbcUrlFactory jdbcUrlFactory,
                                               final CompatibilityMode compatibilityMode) {
            super(autoCommit, name, jdbcUrlProperties, initializationPlugins, jdbcUrlFactory, compatibilityMode);
        }
    }
}
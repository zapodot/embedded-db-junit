package org.zapodot.junit.db.internal;

import org.h2.jdbc.JdbcSQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zapodot.junit.db.common.CompatibilityMode;
import org.zapodot.junit.db.common.EmbeddedDatabaseCreator;
import org.zapodot.junit.db.plugin.InitializationPlugin;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class EmbeddedDatabaseCreatorImpl implements EmbeddedDatabaseCreator {

    private final boolean autoCommit;

    protected final String predefinedName;

    private String testName;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedDatabaseCreatorImpl.class);

    private final Map<String, String> jdbcUrlProperties;

    private final Map<Class<? extends InitializationPlugin>, InitializationPlugin> initializationPlugins;

    private final JdbcUrlFactory jdbcUrlFactory;

    private final CompatibilityMode compatibilityMode;

    private Connection connection;

    protected EmbeddedDatabaseCreatorImpl(final boolean autoCommit,
                                        final String name,
                                        final Map<String, String> jdbcUrlProperties,
                                        final Map<Class<? extends InitializationPlugin>, InitializationPlugin> initializationPlugins,
                                        final JdbcUrlFactory jdbcUrlFactory,
                                        final CompatibilityMode compatibilityMode) {
        this.autoCommit = autoCommit;
        this.predefinedName = name;
        this.jdbcUrlProperties = jdbcUrlProperties == null ? Collections.emptyMap() : jdbcUrlProperties;
        this.initializationPlugins = initializationPlugins == null ? Collections.emptyMap() : initializationPlugins;
        this.jdbcUrlFactory = jdbcUrlFactory == null ? new H2JdbcUrlFactory() : jdbcUrlFactory;
        this.compatibilityMode = compatibilityMode;
    }

    /**
     * Gives access to the current H2 JDBC connection. The connection returned by this method will suppress all "close" calls
     *
     * @return the current JDBC connection to be used internally in your test, or null if has not been set yet
     */
    @Override
    public Connection getConnection() {
        return CloseSuppressedConnectionFactory.createProxy(connection);
    }

    /**
     * To be used when you actually need is a DataSource
     *
     * @return a DataSource instance wrapping a single connection
     */
    @Override
    public DataSource getDataSource() {
        return EmbeddedDataSource.create(connection);
    }

    @Override
    public boolean isAutoCommit() {
        return autoCommit;
    }


    /**
     * Returns a JDBC url for connecting to the in-memory database created by this rule with all INIT params stripped
     *
     * @return a JDBC url string
     */
    @Override
    public String getConnectionJdbcUrl() {
        return jdbcUrlFactory.connectionUrl(getInMemoryDatabaseName(), getJdbcUrlProperties());
    }

    private String getInMemoryDatabaseName() {
        return predefinedName == null ? testName : predefinedName;
    }

    private Map<String, String> getJdbcUrlProperties() {
        final LinkedHashMap<String, String> properties = new LinkedHashMap<>();
        properties.putAll(jdbcUrlFactory.compatibilityModeParam(compatibilityMode));
        properties.putAll(jdbcUrlProperties);
        return properties;
    }

    protected void setupConnection(final String name) throws SQLException {

        testName = name;
        final String url = generateJdbcUrl();
        try {
            connection = DriverManager.getConnection(url);
        } catch (JdbcSQLException e) {
            if (url.contains("RUNSCRIPT")) {
                LOGGER.error("Failed to initialize the H2 database. Please check your init script for errors", e);
            }
            throw e;
        }
        connection.setAutoCommit(isAutoCommit());
        for (final Map.Entry<Class<? extends InitializationPlugin>, InitializationPlugin> entry : initializationPlugins
                .entrySet()) {
            entry.getValue().connectionMade(name, getConnection());
        }
    }

    protected void takeDownConnection() throws SQLException {
        this.connection.close();
    }

    private String generateJdbcUrl() {
        return jdbcUrlFactory.connectionUrlForInitialization(getInMemoryDatabaseName(), getJdbcUrlProperties());
    }
}

package org.zapodot.junit.db;

import org.h2.jdbc.JdbcSQLException;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zapodot.junit.db.internal.CloseSuppressedConnectionFactory;
import org.zapodot.junit.db.internal.EmbeddedDataSource;
import org.zapodot.junit.db.internal.FilePathInitializationPlugin;
import org.zapodot.junit.db.internal.H2JdbcUrlFactory;
import org.zapodot.junit.db.internal.SQLInitializationPlugin;
import org.zapodot.junit.db.plugin.InitializationPlugin;

import javax.sql.DataSource;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A JUnit Rule implementation that makes it easy to stub JDBC integrations from your tests
 *
 * @author zapodot
 */
public class EmbeddedDatabaseRule implements TestRule {

    public static final String PROP_INIT_SQL = "INIT";

    public static final String PROP_MODE = "MODE";

    private final boolean autoCommit;

    private final String predefinedName;

    private String testName;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedDatabaseRule.class);

    private final Map<String, String> jdbcUrlProperties;

    private final Map<Class<? extends InitializationPlugin>, InitializationPlugin> initializationPlugins;

    private Connection connection;

    /**
     * Standard constructor that is suitable if you don't need to do anything special
     */
    public EmbeddedDatabaseRule() {
        this(true, null, null, null);
    }


    private EmbeddedDatabaseRule(final boolean autoCommit,
                                 final String name,
                                 final Map<String, String> jdbcUrlProperties,
                                 final Map<Class<? extends InitializationPlugin>, InitializationPlugin> initializationPlugins) {
        this.autoCommit = autoCommit;
        this.predefinedName = name;
        this.jdbcUrlProperties = jdbcUrlProperties == null ? Collections.emptyMap() : jdbcUrlProperties;
        this.initializationPlugins = initializationPlugins == null ? Collections.emptyMap() : initializationPlugins;
    }

    /**
     * Creates a builder that enables you to use the fluent API when construction an EmbeddedDatabaseRule instance
     *
     * @return a Builder
     */
    public static Builder builder() {
        return h2();
    }

    /**
     * Creates a builder that enables you to use the fluent API to construct an {@link EmbeddedDatabaseRule} using the H2 engine
     *
     * @return a {@link Builder} instance
     */
    public static Builder h2() {
        return Builder.h2();
    }

    /**
     * Creates a builder that enables you to use the fluent PAI to consturct an {@link EmbeddedDatabaseRule} using the HSQLDB engine
     *
     * @return a {@link Builder} instance
     */
    public static Builder hsqldb() {
        return Builder.hsqldb();
    }

    /**
     * Gives access to the current H2 JDBC connection. The connection returned by this method will suppress all "close" calls
     *
     * @return the current JDBC connection to be used internally in your test, or null if has not been set yet
     */
    public Connection getConnection() {
        return CloseSuppressedConnectionFactory.createProxy(connection);
    }

    /**
     * To be used when you actually need is a DataSource
     *
     * @return a DataSource instance wrapping a single connection
     */
    public DataSource getDataSource() {
        return EmbeddedDataSource.create(connection);
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }

    /**
     * Returns a JDBC url for connecting to the in-memory database created by this rule with all INIT params stripped
     *
     * @return a JDBC url string
     */
    public String getConnectionJdbcUrl() {
        return H2JdbcUrlFactory.buildFilteringInitProperties(getInMemoryDatabaseName(), jdbcUrlProperties);
    }

    /**
     * Will generate a JDBC url for an in-memory H2 named database
     *
     * @return a JDBC URL string
     */
    private String generateJdbcUrl() {
        return H2JdbcUrlFactory.buildWithNameAndProperties(getInMemoryDatabaseName(), jdbcUrlProperties);
    }

    private String getInMemoryDatabaseName() {
        return predefinedName == null ? testName : predefinedName;
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        warnIfNameIsPredifinedAndTheRuleIsMethodBased(description);
        return statement(base, predefinedName != null ? predefinedName : extractNameFromDescription(description));
    }

    private void warnIfNameIsPredifinedAndTheRuleIsMethodBased(final Description description) {
        if (description.getMethodName() != null && predefinedName != null) {
            LOGGER.warn(
                    "You have set a name for your datasource and are running the EmbeddedDatabaseRule as a method @Rule. " +
                            "This may lead to the datasource not being reset between tests especially of your tests uses runs with " +
                            "multiple threads");
        }
    }

    private String extractNameFromDescription(Description description) {
        return description.getTestClass() == null ? description.getClassName() : description.getTestClass()
                                                                                            .getSimpleName();
    }


    private Statement statement(final Statement base, final String name) {
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                setupConnection(name);
                try {
                    base.evaluate();
                } finally {
                    takeDownConnection();
                }
            }
        };
    }

    private void takeDownConnection() throws SQLException {
        this.connection.close();
    }

    private void setupConnection(final String name) throws SQLException {

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

    /**
     * A builder class that provides a fluent api for building DB rules
     */
    public static class Builder {

        private final Map<String, String> properties = new LinkedHashMap<>();

        private final Map<Class<? extends InitializationPlugin>, InitializationPlugin> initializationPlugins = new LinkedHashMap<>();

        private String name;

        private boolean autoCommit = true;

        private final Engine engine;

        private Builder(final Engine engine) {
            if (engine == null) {
                throw new IllegalArgumentException("The \"engine\" argument can not be null");
            }
            this.engine = engine;
        }

        /**
         * Creates a builder for the H2 engine
         *
         * @return a builder for creating an {@link EmbeddedDatabaseRule} that will use the H2 engine
         * @deprecated use {@link Builder#h2()} instead. Will be removed in the 2.0 release
         */
        @Deprecated
        public static Builder instance() {
            return new Builder(Engine.H2);
        }

        /**
         * Creates a builder for the H2 engine
         *
         * @return a builder for creating an {@link EmbeddedDatabaseRule} that will use the H2 engine
         */
        public static Builder h2() {
            return new Builder(Engine.H2);
        }


        /**
         * Creates a builder for the H2 engine
         *
         * @return a builder for creating an {@link EmbeddedDatabaseRule} that will use the HSQLDB engine
         */
        public static Builder hsqldb() {
            return new Builder(Engine.HSQLDB);
        }

        public Builder withName(final String name) {
            this.name = name;
            return this;
        }

        private String normalizeString(final String input) {
            if (input == null) {
                return null;
            } else {
                return input.replaceAll("\n", "").replaceAll(";", "\\\\;").trim();
            }
        }

        public Builder withInitialSql(final String sql) {
            if (sql == null) {
                throw new IllegalArgumentException("The value of the \"sql\" argument can not be null");
            }
            return initializedByPlugin(new SQLInitializationPlugin(sql));
        }

        public Builder withInitialSqlFromResource(final String resource) {

            if (null == resource) {
                throw new IllegalArgumentException("The value of the \"resource\" argument can not be null");
            }
            return withInitialSqlFromResource(resource, StandardCharsets.UTF_8);
        }

        public Builder withInitialSqlFromResource(final String resource, final Charset charset) {
            if (null == resource) {
                throw new IllegalArgumentException("The value of the \"resource\" argument can not be null");
            }
            if (null == charset) {
                throw new IllegalArgumentException("The value of the \"charset\" argument can not be null");
            }
            return initializedByPlugin(new FilePathInitializationPlugin(resource, charset));
        }


        public Builder withMode(final String mode) {
            if (mode == null) {
                throw new IllegalArgumentException("The \"mode\" argument can not be null");
            }
            return withProperty(PROP_MODE, mode);
        }

        public Builder withMode(final CompatibilityMode compatibilityMode) {

            if (compatibilityMode == null) {
                throw new IllegalArgumentException("The \"compatibilityMode\" argument can not be null");
            }
            return withMode(compatibilityMode.name());
        }

        public <P extends InitializationPlugin> Builder initializedByPlugin(final P plugin) {
            if (plugin != null) {
                initializationPlugins.put(plugin.getClass(), plugin);
            }
            return this;
        }

        public Builder withProperty(final String property, final String value) {

            if (property != null && value != null) {
                properties.put(property, normalizeString(value));
            }
            return this;
        }

        public Builder withoutAutoCommit() {
            autoCommit = false;
            return this;
        }

        private Map<String, String> propertiesMap() {
            return new LinkedHashMap<>(properties);
        }

        public EmbeddedDatabaseRule build() {
            return new EmbeddedDatabaseRule(autoCommit, name, propertiesMap(), initializationPlugins);
        }
    }

    public enum CompatibilityMode {
        REGULAR, DB2, Derby, HSQLDB, MSSQLServer, MySQL, Oracle, PostgreSQL
    }

    public enum Engine {
        H2, HSQLDB
    }


}

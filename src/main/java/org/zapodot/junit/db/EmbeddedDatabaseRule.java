package org.zapodot.junit.db;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.zapodot.junit.db.internal.CloseSuppressedConnectionFactory;
import org.zapodot.junit.db.internal.EmbeddedDataSource;

import javax.sql.DataSource;
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
    private final String _predefinedName;
    private String _testName;

    private final Map<String, String> _jdbcUrlProperties;
    private Connection connection;

    /**
     * Standard constructor that is suitable if you don't need to do anything special
     */
    public EmbeddedDatabaseRule() {
        this(true, null, null);
    }


    private EmbeddedDatabaseRule(final boolean autoCommit, final String name, final Map<String, String> jdbcUrlProperties) {
        this.autoCommit = autoCommit;
        this._predefinedName = name;
        this._jdbcUrlProperties = jdbcUrlProperties == null ? Collections.<String, String>emptyMap() : jdbcUrlProperties;
    }

    /**
     * Creates a builder that enables you to use the fluent API when construction an EmbeddedDatabaseRule instance
     *
     * @return a Builder
     */
    public static Builder builder() {
        return Builder.instance();
    }

    private static Map<String, String> filterInitProperties(final Map<String, String> jdbcUrlProperties) {
        if (jdbcUrlProperties == null) {
            return null;
        } else {
            final Map<String, String> propertiesCopy = new LinkedHashMap<>();
            for (final Map.Entry<String, String> property : jdbcUrlProperties.entrySet()) {
                if (!PROP_INIT_SQL.equalsIgnoreCase(property.getKey())) {
                    propertiesCopy.put(property.getKey(), property.getValue());
                }
            }
            return propertiesCopy;
        }
    }

    private static String createJdbcUrlParameterString(final Map<String, String> properties) {
        if (properties == null) {
            return "";
        }
        final StringBuilder paramStringBuilder = new StringBuilder("");
        for (final Map.Entry<String, String> property : properties.entrySet()) {
            if (property.getValue() != null) {
                paramStringBuilder.append(';')
                        .append(property.getKey())
                        .append('=')
                        .append(property.getValue());
            }
        }
        return paramStringBuilder.toString();
    }

    private static String createH2InMemoryCreateUrl(final String name, final Map<String, String> properties) {
        if (name == null) {
            throw new NullPointerException("The value of the \"name\" parameter can not be null");
        }
        return new StringBuilder("jdbc:h2:mem:")
                .append(name)
                .append(createJdbcUrlParameterString(properties))
                .toString();
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
     * @return a JDBC url string
     */
    public String getConnectionJdbcUrl() {
        return createH2InMemoryCreateUrl(getInMemoryDatabaseName(), filterInitProperties(_jdbcUrlProperties));
    }

    /**
     * Will generate a JDBC url for an in-memory H2 named database
     *
     * @return a JDBC URL string
     */
    private String generateJdbcUrl() {
        return createH2InMemoryCreateUrl(getInMemoryDatabaseName(), _jdbcUrlProperties);
    }

    private String getInMemoryDatabaseName() {
        return _predefinedName == null ? _testName : _predefinedName;
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return statement(base, _predefinedName != null ? _predefinedName : extractNameFromDescription(description));
    }

    private String extractNameFromDescription(Description description) {
        return description.getTestClass() == null ? description.getClassName() : description.getTestClass().getSimpleName();
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

        _testName = name;
        connection = DriverManager.getConnection(generateJdbcUrl());
        connection.setAutoCommit(isAutoCommit());
    }

    /**
     * A builder class that provides a fluent api for building DB rules
     */
    public static class Builder {

        private final Map<String, String> properties = new LinkedHashMap<>();

        private String name;

        private boolean autoCommit = true;

        public static Builder instance() {
            return new Builder();
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

            return withProperty(PROP_INIT_SQL, sql);
        }

        public Builder withMode(final String mode) {

            return withProperty(PROP_MODE, mode);
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
            return new EmbeddedDatabaseRule(autoCommit, name, propertiesMap());
        }
    }


}

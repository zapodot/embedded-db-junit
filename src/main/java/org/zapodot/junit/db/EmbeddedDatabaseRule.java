package org.zapodot.junit.db;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.zapodot.junit.db.datasource.EmbeddedDataSource;
import org.zapodot.junit.db.datasource.internal.CloseSuppressedConnectionFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class EmbeddedDatabaseRule implements TestRule {

    public static class Builder {

        private Map<String, String> properties = new LinkedHashMap<>();

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
            if(input == null) {
                return null;
            } else {
                return input.replaceAll("\n", "").replaceAll(";", "\\\\;").trim();
            }
        }

        public Builder withInitialSql(final String sql) {

            return withProperty("INIT", sql);
        }

        public Builder withMode(final String mode) {

            return withProperty("MODE", mode);
        }

        public Builder withProperty(final String property, final String value) {

            if(property != null && value != null) {
                properties.put(property, normalizeString(value));
            }
            return this;
        }

        private Map<String, String> propertiesMap() {
            return new LinkedHashMap<>(properties);
        }

        public EmbeddedDatabaseRule build() {
            return new EmbeddedDatabaseRule(autoCommit, name, propertiesMap());
        }
    }

    private final boolean autoCommit;

    private final String _predefinedName;

    private final Map<String, String> _jdbcUrlProperties;

    private Connection connection;


    /**
     * Standard constructor that is suitable if you don't need to do anything special
     */
    public EmbeddedDatabaseRule() {
        this(true, null, null);
    }

    EmbeddedDatabaseRule(final boolean autoCommit, final String name, final Map<String, String> jdbcUrlProperties) {
        this.autoCommit = autoCommit;
        this._predefinedName = name;
        this._jdbcUrlProperties = jdbcUrlProperties == null ? Collections.<String, String>emptyMap() : jdbcUrlProperties;
    }

    public static Builder builder() {
        return Builder.instance();
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
        return new EmbeddedDataSource(getConnection());
    }

    public boolean isAutoCommit() {
        return autoCommit;
    }

    /**
     * Will generate a JDBC url for an in-memory H2 named database
     * @param name the name of the currently running test
     * @return a JDBC URL string
     */
    public String generateJdbcUrl(final String name) {
        StringBuilder jdbcUrlBuilder = new StringBuilder("jdbc:h2:mem:");
        if(_predefinedName != null) {
            jdbcUrlBuilder.append(_predefinedName);
        } else {
            jdbcUrlBuilder.append(name);
        }
        for (String property: _jdbcUrlProperties.keySet()) {
            jdbcUrlBuilder.append(';').append(property).append('=').append(_jdbcUrlProperties.get(property));
        }
        return jdbcUrlBuilder.toString();
    }



    @Override
    public Statement apply(final Statement base, final Description description) {
        return statement(base, description.getTestClass().getSimpleName());
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
        connection = DriverManager.getConnection(generateJdbcUrl(name));
        connection.setAutoCommit(isAutoCommit());
    }


}

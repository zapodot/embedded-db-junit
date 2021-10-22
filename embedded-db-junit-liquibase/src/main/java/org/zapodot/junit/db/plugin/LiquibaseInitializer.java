package org.zapodot.junit.db.plugin;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * A custom InitializationPlugin for Embedded DB Junit supporting database evolutions based on
 * <a href="https://www.liquibase.org">Liquibase</a>.
 *
 * @author zapodot
 */
public class LiquibaseInitializer implements InitializationPlugin {

    private final Contexts contexts;

    private final LabelExpression labelExpression;

    private final String changeLog;

    private final ResourceAccessor resourceAccessor;

    private final Integer changesLimit;

    private final boolean addDbNameToContext;

    private final String defaultSchemaName;

    public static class Builder {
        private String databaseChangeLog;

        private ResourceAccessor resourceAccessor;

        private List<String> contexts = new LinkedList<>();

        private List<String> labels = new LinkedList<>();

        private Integer changesToApply;

        private boolean addNameToContext = false;

        private String defaultSchemaName;

        /**
         * A reference to the changelog file. Can be in any format supported by liquibase (currently SQL,Yaml,XML or JSON)
         *
         * @param resource a resource that will be read from classpath using Class.getResource(String)
         * @return the same builder
         */
        public Builder withChangelogResource(final String resource) {
            this.databaseChangeLog = resource;
            this.resourceAccessor = new ClassLoaderResourceAccessor();
            return this;
        }

        /**
         * Limit the number of changes in the changelog to be applied
         *
         * @param limit the number of changes
         * @return the same builder
         */
        public Builder limitChanges(Integer limit) {
            this.changesToApply = limit;
            return this;
        }

        /**
         * Adds contexts to be used for Liquibase. As default no contexts are specified, which is interpreted be
         * Liquibase as meaning 'all contexts'.
         * To have the H2 database name added to the context list use {@link #addDatabaseNameToContext()}.
         *
         * @param contexts a variable length list of contexts
         * @return the same builder instance
         * @see <a href="http://www.liquibase.org/documentation/contexts.html">Liquibase Contexts documentation</a>
         */
        public Builder withContexts(final String... contexts) {
            if (contexts != null) {
                this.contexts.addAll(Arrays.asList(contexts));
            }
            return this;
        }

        /**
         * Avoids the current H2 database name to be added to the list of contexts.
         *
         * @return the same builder instance
         */
        public Builder addDatabaseNameToContext() {
            this.addNameToContext = true;
            return this;
        }

        /**
         * If you need parameter support, use this method to add labels to
         *
         * @param labels a variable length array of labels to be added
         * @return the same builder instance
         */
        public Builder withLabels(final String... labels) {
            if (labels != null) {
                this.labels.addAll(Arrays.asList(labels));
            }
            return this;
        }

        /**
         * Overrides default H2 schema name (which is PUBLIC) with given one. Will handle schema
         * creation (if not exists) on startup.
         */
        public Builder withDefaultSchemaName(String schemaName) {
            this.defaultSchemaName = schemaName;
            return this;
        }

        /**
         * Builds the LiquibaseInitializer based on the settings provided earlier by calls to the various builder methods
         *
         * @return a LiqubaseInitializer instance to be used with the EmbeddedDatabaseRule
         */
        public LiquibaseInitializer build() {
            if (databaseChangeLog == null) {
                throw new IllegalArgumentException(
                        "You must provide a changelog file to the LiquibaseIntitializer Plugin builder");
            }
            try {
                if (resourceAccessor.openStream(null, databaseChangeLog) == null) {
                    throw new IllegalArgumentException(String.format(
                            "Can not load changelog from resource \"%s\". Does it exist?",
                            databaseChangeLog));
                }
            } catch (IOException e) {
                throw new IllegalArgumentException(String.format(
                        "An IO exception occurred while loading changelog from resource \"%s\"",
                        databaseChangeLog), e);
            }
            return new LiquibaseInitializer(createContexts(), createLabels(), databaseChangeLog,
                                            resourceAccessor, changesToApply, addNameToContext,
                                            defaultSchemaName);
        }

        private LabelExpression createLabels() {
            return new LabelExpression(labels);
        }

        private Contexts createContexts() {

            return new Contexts(contexts);
        }
    }

    private LiquibaseInitializer(final Contexts contexts,
                                 final LabelExpression labelExpression,
                                 final String changeLog,
                                 final ResourceAccessor resourceAccessor,
                                 final Integer changesLimit,
                                 final boolean addDbNameToContext,
                                 final String defaultSchemaName) {
        this.contexts = contexts;
        this.labelExpression = labelExpression;
        this.changeLog = changeLog;
        this.resourceAccessor = resourceAccessor;
        this.changesLimit = changesLimit;
        this.addDbNameToContext = addDbNameToContext;
        this.defaultSchemaName = defaultSchemaName;
    }

    @Override
    public void connectionMade(final String name, final Connection connection) {
        if (defaultSchemaName != null) {
            try (final Statement statement = connection.createStatement()) {
                statement.execute(String.format("CREATE SCHEMA IF NOT EXISTS %s", defaultSchemaName));
            } catch (SQLException e) {
                throw new IllegalStateException("Could not create schema \"" + defaultSchemaName + "\"", e);
            }
        }

        final Liquibase liquibase = createLiquibase(connection);
        if (addDbNameToContext) {
            contexts.add(name);
        }
        try {
            if (changesLimit == null) {
                liquibase.update(contexts, labelExpression);
            } else {
                liquibase.update(changesLimit, contexts, labelExpression);
            }
        } catch (LiquibaseException e) {
            throw new IllegalArgumentException("An exception occurred while applying Liquibase changesets", e);
        }
    }

    private Liquibase createLiquibase(final Connection connection) {
        try {
            JdbcConnection conn = new JdbcConnection(connection);
            Database database = resolveDatabase(connection);
            database.setConnection(conn);
            if (defaultSchemaName != null) {
                database.setDefaultSchemaName(defaultSchemaName);
            }
            return new Liquibase(changeLog, resourceAccessor, database);
        } catch (LiquibaseException e) {
            throw new IllegalStateException("Could not initialize Liquibase", e);
        }
    }

    private final Database resolveDatabase(final Connection connection) throws DatabaseException {
        return DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
    }

    /**
     * Creates a builder for providing parameters for Liquibase to be run
     *
     * @return a new {@link Builder}
     */
    public static Builder builder() {
        return new Builder();
    }


}


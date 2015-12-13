package org.zapodot.junit.db.plugin;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.resource.ResourceAccessor;

import java.io.IOException;
import java.sql.Connection;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * A custom InitializationPlugin for
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

    public static class Builder {
        private String databaseChangeLog;
        private ResourceAccessor resourceAccessor;
        private List<String> contexts = new LinkedList<>();
        private List<String> labels = new LinkedList<>();
        private Integer changesToApply;
        private boolean addNameToContext = false;

        public Builder() {
        }

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
            this.addNameToContext = false;
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
         * Builds the LiquibaseInitializer based on the settings provided earlier by calls to the various builder methods
         *
         * @return a LiqubaseInitializer instance to be used with the EmbeddedDatabaseRule
         */
        public LiquibaseInitializer build() {
            if (databaseChangeLog == null) {
                throw new IllegalArgumentException("You must provide a changelog file to the LiquibaseIntitializer Plugin builder");
            }
            try {
                if(resourceAccessor.getResourcesAsStream(databaseChangeLog) == null) {
                    throw new IllegalArgumentException(String.format("Can not load changelog from resource \"%s\". Does it exist?", databaseChangeLog));
                }
            } catch (IOException e) {
                throw new IllegalArgumentException(String.format("An IO exception occurred while loading changelog from resource \"%s\"", databaseChangeLog), e);
            }
            return new LiquibaseInitializer(createContexts(), createLabels(), databaseChangeLog, resourceAccessor, changesToApply, addNameToContext);
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
                                 final boolean addDbNameToContext) {
        this.contexts = contexts;
        this.labelExpression = labelExpression;
        this.changeLog = changeLog;
        this.resourceAccessor = resourceAccessor;
        this.changesLimit = changesLimit;
        this.addDbNameToContext = addDbNameToContext;
    }

    @Override
    public void connectionMade(final String name, final Connection connection) {
        final Liquibase liquibase = createLiquibase(connection);
        if (addDbNameToContext) {
            contexts.add(name);
        }
        try {
            if (changesLimit == null) {
                liquibase.update(contexts, labelExpression);
            } else {
                liquibase.update(changesLimit.intValue(), contexts, labelExpression);
            }
        } catch (LiquibaseException e) {
            throw new RuntimeException("An exception occurred while applying Liquibase changesets", e);
        }
    }

    private Liquibase createLiquibase(final Connection connection) {
        try {
            return new Liquibase(changeLog, resourceAccessor, new JdbcConnection(connection));
        } catch (LiquibaseException e) {
            throw new RuntimeException("Could not initialize Liquibase", e);
        }
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


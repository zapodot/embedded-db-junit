package org.zapodot.junit.db;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zapodot.junit.db.common.CompatibilityMode;
import org.zapodot.junit.db.common.Engine;
import org.zapodot.junit.db.internal.AbstractEmbeddedDatabaseCreatorBuilder;
import org.zapodot.junit.db.internal.EmbeddedDatabaseCreatorImpl;
import org.zapodot.junit.db.internal.JdbcUrlFactory;
import org.zapodot.junit.db.plugin.InitializationPlugin;

import java.util.List;
import java.util.Map;

/**
 * A JUnit4 Rule implementation that makes it easy to stub JDBC integrations from your tests
 *
 * @author zapodot
 */
public class EmbeddedDatabaseRule extends EmbeddedDatabaseCreatorImpl implements TestRule {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedDatabaseRule.class);



    /**
     * Standard constructor that is suitable if you don't need to do anything special
     */
    public EmbeddedDatabaseRule() {
        this(true, null, null, null, null, CompatibilityMode.REGULAR);
    }


    private EmbeddedDatabaseRule(final boolean autoCommit,
                                 final String name,
                                 final Map<String, String> jdbcUrlProperties,
                                 final List<InitializationPlugin> initializationPlugins,
                                 final JdbcUrlFactory jdbcUrlFactory,
                                 final CompatibilityMode compatibilityMode) {
        super(autoCommit, name, jdbcUrlProperties, initializationPlugins, jdbcUrlFactory, compatibilityMode);
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


    /**
     * A builder class that provides a fluent api for building DB rules
     */
    public static class Builder extends AbstractEmbeddedDatabaseCreatorBuilder<EmbeddedDatabaseRule> {


        private Builder(final Engine engine) {
            super(engine);
        }

        /**
         * Creates a builder for the H2 engine
         *
         * @return a builder for creating an {@link EmbeddedDatabaseRule} that will use the H2 engine
         * @deprecated use {@link Builder#h2()} instead. Will be removed in the 2.0 release
         */
        @Deprecated
        public static Builder instance() {
            return h2();
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

        @Override
        public EmbeddedDatabaseRule build() {
            return new EmbeddedDatabaseRule(autoCommit,
                                            name,
                                            propertiesMap(),
                                            initializationPlugins,
                                            createJdbcUrlFactory(),
                                            compatibilityMode);
        }
    }


}

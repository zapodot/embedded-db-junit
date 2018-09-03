package org.zapodot.junit.db;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.util.AnnotationUtils;
import org.junit.platform.commons.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zapodot.junit.db.annotations.ConfigurationProperty;
import org.zapodot.junit.db.annotations.DataSourceConfig;
import org.zapodot.junit.db.common.CompatibilityMode;
import org.zapodot.junit.db.common.EmbeddedDatabaseCreator;
import org.zapodot.junit.db.common.Engine;
import org.zapodot.junit.db.internal.AbstractEmbeddedDatabaseCreatorBuilder;
import org.zapodot.junit.db.internal.InternalEmbeddedDatabaseCreator;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * A JUnit5 Extension that makes it easy to test JDBC integration code
 */
public class EmbeddedDatabaseExtension implements EmbeddedDatabaseCreator, ExecutionCondition, BeforeTestExecutionCallback, AfterTestExecutionCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedDatabaseExtension.class);

    private InternalEmbeddedDatabaseCreator embeddedDatabaseCreator;

    public EmbeddedDatabaseExtension() {

        this(null);
    }

    private EmbeddedDatabaseExtension(final InternalEmbeddedDatabaseCreator embeddedDatabaseCreator) {
        this.embeddedDatabaseCreator = embeddedDatabaseCreator;
    }


    @Override
    public void afterTestExecution(final ExtensionContext extensionContext) throws Exception {
        embeddedDatabaseCreator.takeDownConnection();
    }

    @Override
    public void beforeTestExecution(final ExtensionContext extensionContext) throws Exception {
        embeddedDatabaseCreator
                .setupConnection(embeddedDatabaseCreator.getPredefinedName() != null ? embeddedDatabaseCreator
                        .getPredefinedName() : extractNameFromExtensionContext(extensionContext));

        tryToInjectDataSourceOrConnection(extensionContext);
    }

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(final ExtensionContext context) {
        if (embeddedDatabaseCreator != null) {
            return ConditionEvaluationResult.enabled("Configuration was provided");
        }
        final InternalEmbeddedDatabaseCreator embeddedDatabaseCreatorFromConfiguration = tryToCreateFromExtensionContext(
                context);
        if (embeddedDatabaseCreatorFromConfiguration == null) {
            return ConditionEvaluationResult.disabled(
                    "No database configuration found. Disabling test. Use @RegisterExtension with builder or the @DataSourceConfig annotation to configure");
        } else {
            this.embeddedDatabaseCreator = embeddedDatabaseCreatorFromConfiguration;
            return ConditionEvaluationResult.enabled("Configuration provided using annotations");
        }
    }

    @Override
    public Connection getConnection() {
        return embeddedDatabaseCreator.getConnection();
    }

    @Override
    public DataSource getDataSource() {
        return embeddedDatabaseCreator.getDataSource();
    }

    @Override
    public boolean isAutoCommit() {
        return embeddedDatabaseCreator.isAutoCommit();
    }

    @Override
    public String getConnectionJdbcUrl() {
        return embeddedDatabaseCreator.getConnectionJdbcUrl();
    }

    private void tryToInjectDataSourceOrConnection(final ExtensionContext extensionContext) {
        extensionContext.getTestInstance()
                        .ifPresent(testInstance -> {
                            findInjectCandidateFields(extensionContext).stream()
                                                                       .filter(field -> getFieldValueFromInstance(
                                                                               testInstance,
                                                                               field) == null)
                                                                       .forEach(field ->
                                                                                        injectDataSourceOrConnection(
                                                                                                testInstance,
                                                                                                field));
                        });


    }

    private List<Field> findInjectCandidateFields(final ExtensionContext extensionContext) {
        return extensionContext.getTestClass()
                               .map(clazz -> ReflectionUtils.findFields(clazz,
                                                                        f -> DataSource.class
                                                                                .isAssignableFrom(f.getType()) || Connection.class
                                                                                .isAssignableFrom(f.getType()),
                                                                        ReflectionUtils.HierarchyTraversalMode.TOP_DOWN))
                               .orElse(Collections.emptyList());

    }

    private Object getFieldValueFromInstance(final Object testInstance,
                                             final Field field) {
        boolean accessibleOriginal = field.isAccessible();
        try {
            field.setAccessible(true);
            return field.get(testInstance);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(String.format("Could not access field \"%s\" on instance %s",
                                                          field,
                                                          testInstance));
        } finally {
            field.setAccessible(accessibleOriginal);
        }
    }

    private void injectDataSourceOrConnection(final Object testInstance,
                                              final Field field) {
        boolean accessibleOriginal = field.isAccessible();
        field.setAccessible(true);
        try {

            if (DataSource.class.isAssignableFrom(field.getType())) {
                LOGGER.debug("Will inject javax.sql.DataSource to field {}", field.getName());
                field.set(testInstance, getDataSource());
            } else if (Connection.class.isAssignableFrom(field.getType())) {
                LOGGER.debug("Will inject java.sql.Connection to field {}", field.getName());
                field.set(testInstance, getConnection());
            }
        } catch (IllegalAccessException | IllegalArgumentException e) {
            throw new IllegalStateException("Could not inject embedded DataSource/Connection to field", e);
        } finally {
            field.setAccessible(accessibleOriginal);
        }
    }

    private String extractNameFromExtensionContext(final ExtensionContext extensionContext) {
        return extensionContext.getTestClass().map(Class::getSimpleName).orElse(extensionContext.getUniqueId());
    }

    private static InternalEmbeddedDatabaseCreator tryToCreateFromExtensionContext(final ExtensionContext extensionContext) {

        LOGGER.debug("Constructing DataSource configuration using annotations");
        final Optional<DataSourceConfig> dataSourceConfig = findAnnotation(extensionContext.getElement(),
                                                                           DataSourceConfig.class);
        if (!dataSourceConfig.isPresent()) {
            LOGGER.warn(
                    "No configuration found. There should be an @DataSourceConfig annotation on either the test class or the method");
            return null;
        } else {
            final DataSourceConfig dataSourceConfigValue = dataSourceConfig.get();
            final Builder builder;
            if (Engine.HSQLDB.equals(dataSourceConfigValue.engine())) {
                builder = Builder.hsqldb();
            } else {
                builder = Builder.h2();
            }
            if (dataSourceConfigValue.name() == null && !dataSourceConfigValue.name().equals("")) {
                builder.withName(dataSourceConfigValue.name());
            }
            if (dataSourceConfigValue.compatibilityMode() == null) {
                builder.withMode(CompatibilityMode.REGULAR);
            } else {
                builder.withMode(dataSourceConfigValue.compatibilityMode());
            }
            final ConfigurationProperty[] properties = dataSourceConfigValue.properties();
            if (properties != null) {
                Arrays.stream(properties).forEach(cp -> builder.withProperty(cp.name(), cp.value()));
            }

            return builder.buildInternalEmbeddedDatabaseCreator();
        }
    }

    public static <A extends Annotation> Optional<A> findAnnotation(Optional<? extends AnnotatedElement> element,
                                                                    Class<A> annotationType) {

        if (element == null || !element.isPresent()) {
            return Optional.empty();
        }

        return AnnotationUtils.findAnnotation(element.get(), annotationType);
    }

    public static class Builder extends AbstractEmbeddedDatabaseCreatorBuilder<EmbeddedDatabaseExtension> {

        private Builder(final Engine engine) {
            super(engine);
        }

        /**
         * Creates a builder for the H2 engine
         *
         * @return a builder for creating an {@link EmbeddedDatabaseExtension} that will use the H2 engine
         */
        public static Builder h2() {
            return new Builder(Engine.H2);
        }


        /**
         * Creates a builder for the H2 engine
         *
         * @return a builder for creating an {@link EmbeddedDatabaseExtension} that will use the HSQLDB engine
         */
        public static Builder hsqldb() {
            return new Builder(Engine.HSQLDB);
        }

        InternalEmbeddedDatabaseCreator buildInternalEmbeddedDatabaseCreator() {
            return new InternalEmbeddedDatabaseCreator(autoCommit,
                                                       name,
                                                       propertiesMap(),
                                                       initializationPlugins,
                                                       createJdbcUrlFactory(),
                                                       compatibilityMode);
        }

        @Override
        public EmbeddedDatabaseExtension build() {
            return new EmbeddedDatabaseExtension(buildInternalEmbeddedDatabaseCreator());
        }
    }
}

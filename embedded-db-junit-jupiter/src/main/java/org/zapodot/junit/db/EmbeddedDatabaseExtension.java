package org.zapodot.junit.db;

import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.util.AnnotationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zapodot.junit.db.annotations.ConfigurationProperty;
import org.zapodot.junit.db.annotations.EmbeddedDatabase;
import org.zapodot.junit.db.annotations.EmbeddedDatabaseTest;
import org.zapodot.junit.db.common.CompatibilityMode;
import org.zapodot.junit.db.common.EmbeddedDatabaseCreator;
import org.zapodot.junit.db.common.Engine;
import org.zapodot.junit.db.internal.AbstractEmbeddedDatabaseCreatorBuilder;
import org.zapodot.junit.db.internal.InternalEmbeddedDatabaseCreator;

import javax.sql.DataSource;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * A JUnit5 Extension that makes it easy to test JDBC integration code
 */
public class EmbeddedDatabaseExtension implements EmbeddedDatabaseCreator, BeforeEachCallback, AfterEachCallback, TestInstancePostProcessor, ParameterResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedDatabaseExtension.class);

    private static final ExtensionContext.Namespace EMBEDDED_DB_EXT = ExtensionContext.Namespace
            .create("org.zapodot.junit.db");

    private static final String STORE_PROPERTY_DATABASE_CREATOR = "embeddedDatabaseCreator";

    private static final String TEST_INSTANCE = "testInstance";

    private final InternalEmbeddedDatabaseCreator embeddedDatabaseCreator;

    public EmbeddedDatabaseExtension() {

        this(null);
    }

    private EmbeddedDatabaseExtension(final InternalEmbeddedDatabaseCreator embeddedDatabaseCreator) {
        this.embeddedDatabaseCreator = embeddedDatabaseCreator;
    }

    @Override
    public void afterEach(final ExtensionContext context) throws Exception {
        final InternalEmbeddedDatabaseCreator internalEmbeddedDatabaseCreator = context.getStore(EMBEDDED_DB_EXT).get(
                STORE_PROPERTY_DATABASE_CREATOR,
                InternalEmbeddedDatabaseCreator.class);
        if (internalEmbeddedDatabaseCreator != null) {
            internalEmbeddedDatabaseCreator.takeDownConnection();
        }
        context.getStore(EMBEDDED_DB_EXT).remove(TEST_INSTANCE);
        context.getStore(EMBEDDED_DB_EXT).remove(STORE_PROPERTY_DATABASE_CREATOR);
    }

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {

        final InternalEmbeddedDatabaseCreator embeddedDatabaseCreatorFromConfiguration = Optional
                .ofNullable(embeddedDatabaseCreator)
                .orElseGet(() -> Optional.ofNullable(context.getStore(EMBEDDED_DB_EXT).get(
                        STORE_PROPERTY_DATABASE_CREATOR,
                        InternalEmbeddedDatabaseCreator.class))
                                         .orElseGet(() -> tryToCreateFromExtensionContext(context).orElse(null)));
        if (embeddedDatabaseCreatorFromConfiguration != null) {
            embeddedDatabaseCreatorFromConfiguration
                    .setupConnection(embeddedDatabaseCreatorFromConfiguration
                                             .getPredefinedName() != null ? embeddedDatabaseCreatorFromConfiguration
                            .getPredefinedName() : extractNameFromExtensionContext(context));
            tryToInjectDataSourceOrConnection(context.getStore(EMBEDDED_DB_EXT).get(TEST_INSTANCE),
                                              embeddedDatabaseCreatorFromConfiguration);
            context.getStore(EMBEDDED_DB_EXT)
                   .put(STORE_PROPERTY_DATABASE_CREATOR, embeddedDatabaseCreatorFromConfiguration);
        }

    }

    @Override
    public void postProcessTestInstance(final Object testInstance, final ExtensionContext context) {
        tryToCreateFromExtensionContext(context)
                .ifPresent(dc -> context.getStore(EMBEDDED_DB_EXT).put(STORE_PROPERTY_DATABASE_CREATOR, dc));
        context.getStore(EMBEDDED_DB_EXT).put(TEST_INSTANCE, testInstance);
    }

    @Override
    public boolean supportsParameter(final ParameterContext parameterContext,
                                     final ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.isAnnotated(EmbeddedDatabase.class);
    }

    @Override
    public Object resolveParameter(final ParameterContext parameterContext,
                                   final ExtensionContext extensionContext) throws ParameterResolutionException {
        final Parameter parameter = parameterContext.getParameter();
        final InternalEmbeddedDatabaseCreator databaseCreator = extensionContext.getStore(
                EMBEDDED_DB_EXT).get(STORE_PROPERTY_DATABASE_CREATOR, InternalEmbeddedDatabaseCreator.class);
        if (DataSource.class.isAssignableFrom(parameter.getType())) {
            return databaseCreator.getDataSource();
        } else if (Connection.class.isAssignableFrom(parameter.getType())) {
            return databaseCreator.getConnection();
        } else if (String.class.isAssignableFrom(parameter.getType())) {
            return databaseCreator.getConnectionJdbcUrl();
        }
        return null;
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

    private void tryToInjectDataSourceOrConnection(final Object testInstance,
                                                   final EmbeddedDatabaseCreator embeddedDatabaseCreator) {
        findInjectCandidateFields(testInstance.getClass()).stream()
                                                          .forEach(field -> injectDataSourceOrConnection(testInstance,
                                                                                                         field,
                                                                                                         embeddedDatabaseCreator));
    }

    private List<Field> findInjectCandidateFields(final Class type) {
        return AnnotationUtils.findAnnotatedFields(type, EmbeddedDatabase.class, f -> DataSource.class
                .isAssignableFrom(f.getType()) || Connection.class
                .isAssignableFrom(f.getType()));

    }


    private void injectDataSourceOrConnection(final Object testInstance,
                                              final Field field,
                                              final EmbeddedDatabaseCreator embeddedDatabaseCreator) {
        boolean accessibleOriginal = field.isAccessible();
        field.setAccessible(true);
        try {

            if (DataSource.class.isAssignableFrom(field.getType())) {
                LOGGER.debug("Will inject javax.sql.DataSource to field {}", field.getName());
                field.set(testInstance, embeddedDatabaseCreator.getDataSource());
            } else if (Connection.class.isAssignableFrom(field.getType())) {
                LOGGER.debug("Will inject java.sql.Connection to field {}", field.getName());
                field.set(testInstance, embeddedDatabaseCreator.getConnection());
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

    private static Optional<InternalEmbeddedDatabaseCreator> tryToCreateFromExtensionContext(final ExtensionContext extensionContext) {

        LOGGER.debug("Constructing DataSource configuration using annotations");
        final Optional<EmbeddedDatabaseTest> dataSourceConfig = findAnnotation(extensionContext.getElement(),
                                                                           EmbeddedDatabaseTest.class);
        if (!dataSourceConfig.isPresent()) {
            LOGGER.warn(
                    "No configuration found. There should be an @DataSourceConfig annotation on either the test class or the method");
            return Optional.empty();
        } else {
            final EmbeddedDatabaseTest dataSourceConfigValue = dataSourceConfig.get();
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
            if (!dataSourceConfigValue.autoCommit()) {
                builder.withoutAutoCommit();
            }
            if (dataSourceConfigValue.initialSqls() != null) {
                Arrays.stream(dataSourceConfigValue.initialSqls())
                      .forEach(sql -> builder.withInitialSql(sql));
            }
            if (dataSourceConfigValue.initialSqlResources() != null) {
                Arrays.stream(dataSourceConfigValue.initialSqlResources())
                      .forEach(sqlResource -> builder.withInitialSqlFromResource(sqlResource));
            }
            return Optional.of(builder.buildInternalEmbeddedDatabaseCreator());
        }
    }

    private static <A extends Annotation> Optional<A> findAnnotation(Optional<? extends AnnotatedElement> element,
                                                                     Class<A> annotationType) {

        return element.flatMap(e -> findAnnotationForElement(annotationType, e));
    }

    private static <A extends Annotation> Optional<A> findAnnotationForElement(final Class<A> annotationType,
                                                                               final AnnotatedElement e) {
        return AnnotationUtils.findAnnotation(e, annotationType);
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

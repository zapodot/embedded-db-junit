package org.zapodot.junit.db.internal;

import org.zapodot.junit.db.common.CompatibilityMode;
import org.zapodot.junit.db.common.EmbeddedDatabaseCreator;
import org.zapodot.junit.db.common.EmbeddedDatabaseCreatorBuilder;
import org.zapodot.junit.db.common.Engine;
import org.zapodot.junit.db.plugin.InitializationPlugin;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public abstract class AbstractEmbeddedDatabaseCreatorBuilder<C extends EmbeddedDatabaseCreator> implements EmbeddedDatabaseCreatorBuilder<C> {

    protected final Map<String, String> properties = new LinkedHashMap<>();

    protected final List<InitializationPlugin> initializationPlugins = new LinkedList<>();

    protected String name;

    protected boolean autoCommit = true;

    protected final Engine engine;

    protected CompatibilityMode compatibilityMode = CompatibilityMode.REGULAR;


    public AbstractEmbeddedDatabaseCreatorBuilder(final Engine engine) {
        if (engine == null) {
            throw new IllegalArgumentException("The \"engine\" argument can not be null");
        }
        this.engine = engine;
    }

    @Override
    public AbstractEmbeddedDatabaseCreatorBuilder<C> withName(final String name) {
        this.name = name;
        return this;
    }

    private String normalizeString(final String input) {
        return Optional.ofNullable(input)
                       .map(i -> i.replaceAll("\n", "").replaceAll(";", "\\\\;").trim())
                       .orElse(null);
    }

    @Override
    public AbstractEmbeddedDatabaseCreatorBuilder<C> withInitialSql(final String sql) {
        if (sql == null) {
            throw new IllegalArgumentException("The value of the \"sql\" argument can not be null");
        }
        return initializedByPlugin(new SQLInitializationPlugin(sql));
    }

    @Override
    public AbstractEmbeddedDatabaseCreatorBuilder<C> withInitialSqlFromResource(final String resource) {

        if (null == resource) {
            throw new IllegalArgumentException("The value of the \"resource\" argument can not be null");
        }
        return withInitialSqlFromResource(resource, StandardCharsets.UTF_8);
    }

    @Override
    public AbstractEmbeddedDatabaseCreatorBuilder<C> withInitialSqlFromResource(final String resource, final Charset charset) {
        if (null == resource) {
            throw new IllegalArgumentException("The value of the \"resource\" argument can not be null");
        }
        if (null == charset) {
            throw new IllegalArgumentException("The value of the \"charset\" argument can not be null");
        }
        return initializedByPlugin(new FilePathInitializationPlugin(resource, charset));
    }


    public AbstractEmbeddedDatabaseCreatorBuilder<C> withMode(final String mode) {
        if (!"".equals(mode)) {
            return withMode(mapToCompatibilityMode(mode));
        } else {
            return this;
        }
    }

    private CompatibilityMode mapToCompatibilityMode(final String mode) {
        if (mode == null) {
            throw new IllegalArgumentException("The \"mode\" argument can not be null");
        }
        return Arrays.stream(CompatibilityMode.values())
                     .filter(c -> c.name().equalsIgnoreCase(mode))
                     .findAny()
                     .orElseThrow(() -> new IllegalArgumentException("Could not map mode \"" + mode + "\" to a valid Compatibility mode"));
    }

    @Override
    public AbstractEmbeddedDatabaseCreatorBuilder<C> withMode(final CompatibilityMode compatibilityMode) {

        if (compatibilityMode == null) {
            throw new IllegalArgumentException("The \"compatibilityMode\" argument can not be null");
        }
        this.compatibilityMode = compatibilityMode;
        return this;
    }

    @Override
    public <P extends InitializationPlugin> AbstractEmbeddedDatabaseCreatorBuilder<C> initializedByPlugin(final P plugin) {
        if (plugin != null) {
            initializationPlugins.add(plugin);
        }
        return this;
    }

    @Override
    public AbstractEmbeddedDatabaseCreatorBuilder<C> withProperty(final String property, final String value) {

        if (property != null && value != null) {
            properties.put(property, normalizeString(value));
        }
        return this;
    }

    @Override
    public AbstractEmbeddedDatabaseCreatorBuilder<C> withoutAutoCommit() {
        autoCommit = false;
        return this;
    }

    protected Map<String, String> propertiesMap() {
        return new LinkedHashMap<>(properties);
    }



    protected JdbcUrlFactory createJdbcUrlFactory() {
        if (engine == Engine.HSQLDB) {
            return new HyperSqlJdbcUrlFactory();
        } else {
            return new H2JdbcUrlFactory();
        }
    }


}

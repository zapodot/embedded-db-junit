package org.zapodot.junit.db.common;

import org.zapodot.junit.db.plugin.InitializationPlugin;

import java.nio.charset.Charset;

public interface EmbeddedDatabaseCreatorBuilder<C extends EmbeddedDatabaseCreator> {

    /**
     * Used to set name for dataSource. Will be auto-generated if not specified
     *
     * @param name the name to be used
     * @return the same instance of {@link EmbeddedDatabaseCreatorBuilder}
     */
    EmbeddedDatabaseCreatorBuilder<C> withName(final String name);

    /**
     * Sets an initial SQL to be run after the database has been created
     *
     * @param sql a string containing a SQL command
     * @return the same instance of {@link EmbeddedDatabaseCreatorBuilder}
     */
    EmbeddedDatabaseCreatorBuilder<C> withInitialSql(final String sql);

    /**
     * Sets a resource from which to load SQL to be run after the database has been created
     *
     * @param resource a valid resource reference
     * @return the same instance of {@link EmbeddedDatabaseCreatorBuilder}
     */
    EmbeddedDatabaseCreatorBuilder<C> withInitialSqlFromResource(final String resource);

    /**
     * Sets a resource from which to load SQL to be run after the database has been created
     *
     * @param resource a valid resource reference
     * @param charset  the {@link Charset} to be used to decode the SQL
     * @return the same instance of {@link EmbeddedDatabaseCreatorBuilder}
     */
    EmbeddedDatabaseCreatorBuilder<C> withInitialSqlFromResource(final String resource, final Charset charset);

    /**
     * Sets a {@link CompatibilityMode}
     *
     * @param compatibilityMode the {@link CompatibilityMode} to use for the embedded database
     * @return the same instance of {@link EmbeddedDatabaseCreatorBuilder}
     */
    EmbeddedDatabaseCreatorBuilder<C> withMode(final CompatibilityMode compatibilityMode);

    /**
     * Indicates an {@link InitializationPlugin} to be invoked after the database has been created
     *
     * @param plugin An {@link InitializationPlugin} implementation to be used
     * @param <P>    The type of the {@link InitializationPlugin}
     * @return the same instance of {@link EmbeddedDatabaseCreatorBuilder}
     */
    <P extends InitializationPlugin> EmbeddedDatabaseCreatorBuilder<C> initializedByPlugin(final P plugin);

    /**
     * An configuration property to be added when the embedded database is set up
     *
     * @param property the name of the configuration property
     * @param value    the value to set
     * @return the same instance of {@link EmbeddedDatabaseCreatorBuilder}
     */
    EmbeddedDatabaseCreatorBuilder<C> withProperty(final String property, final String value);

    /**
     * Disables autoCommit for connections to the embedded database
     *
     * @return the same instance of {@link EmbeddedDatabaseCreatorBuilder}
     */
    EmbeddedDatabaseCreatorBuilder<C> withoutAutoCommit();

    /**
     * Create an instance of {@link EmbeddedDatabaseCreator}
     *
     * @return an {@link EmbeddedDatabaseCreator} instance
     */
    C build();
}

package org.zapodot.junit.db.internal;

import org.zapodot.junit.db.common.CompatibilityMode;
import org.zapodot.junit.db.plugin.InitializationPlugin;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class InternalEmbeddedDatabaseCreator extends EmbeddedDatabaseCreatorImpl {
    public InternalEmbeddedDatabaseCreator(final boolean autoCommit,
                                           final String name,
                                           final Map<String, String> jdbcUrlProperties,
                                           final List<InitializationPlugin> initializationPlugins,
                                           final JdbcUrlFactory jdbcUrlFactory,
                                           final CompatibilityMode compatibilityMode) {
        super(autoCommit, name, jdbcUrlProperties, initializationPlugins, jdbcUrlFactory, compatibilityMode);
    }

    @Override
    public void setupConnection(final String name) throws SQLException {
        super.setupConnection(name);
    }

    @Override
    public void takeDownConnection() throws SQLException {
        super.takeDownConnection();
    }

    public String getPredefinedName() {
        return predefinedName;
    }
}

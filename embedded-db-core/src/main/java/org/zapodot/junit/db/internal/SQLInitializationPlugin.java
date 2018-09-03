package org.zapodot.junit.db.internal;

import org.zapodot.junit.db.plugin.InitializationPlugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLInitializationPlugin implements InitializationPlugin {

    private final String sql;

    public SQLInitializationPlugin(final String sql) {
        if(null == sql || "".equals(sql)) {
            throw new IllegalArgumentException("No value was provided for parameter \"sql\"");
        }
        this.sql = sql;
    }

    @Override
    public void connectionMade(final String name, final Connection connection) {
        try (final Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            throw new IllegalStateException(String.format("Could not init database using SQL script: \"%s\"", sql));
        }
    }
}

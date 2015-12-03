package org.zapodot.junit.db.plugin;

import java.sql.Connection;

/**
 * @author zapodot
 */
public interface InitializationPlugin {

    void connectionMade(final String name, final Connection connection);
}

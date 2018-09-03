package org.zapodot.junit.db.plugin;

import java.sql.Connection;

/**
 * An interface for creating custom initialization code for instance using Liquibase, Flyway or similar.
 *
 * @author zapodot
 */
public interface InitializationPlugin {

    /**
     * Invoked by the Junit plugin after the in-memory database has been created
     *
     * @param name       the name of the in-memory H2 databaase
     * @param connection a connection to the database
     */
    void connectionMade(final String name, final Connection connection);
}

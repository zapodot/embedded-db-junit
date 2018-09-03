package org.zapodot.junit.db.common;

import javax.sql.DataSource;
import java.sql.Connection;

public interface EmbeddedDatabaseCreator {
    /**
     * Returns a connection to the embedded database
     *
     * @return a {@link Connection}
     */
    Connection getConnection();

    /**
     * Returns a {@link DataSource} that may be used to create connections to the embedded database
     *
     * @return a {@link DataSource}
     */
    DataSource getDataSource();

    /**
     * Convenience method that may be used to check if AutoCommit has been set
     *
     * @return
     */
    boolean isAutoCommit();

    /**
     * Returns a JDBC URL that may be used to connect to the Embedded database
     *
     * @return a JDBC URL string
     */
    String getConnectionJdbcUrl();
}

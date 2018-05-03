package org.zapodot.junit.db;

/**
 * Describe what compatibility mode to use. The support for each mode may vary depending on the database engine.
 * Check the documentation for your chosen engine for details.
 */
public enum CompatibilityMode {
    /**
     * REGULAR means that compatibility mode is not enforced
     */
    REGULAR, DB2, Derby, HSQLDB, MSSQLServer, MySQL, Oracle, PostgreSQL
}

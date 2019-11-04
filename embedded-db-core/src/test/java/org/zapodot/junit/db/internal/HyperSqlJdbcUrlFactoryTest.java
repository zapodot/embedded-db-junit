package org.zapodot.junit.db.internal;

import org.junit.jupiter.api.Test;
import org.zapodot.junit.db.common.CompatibilityMode;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HyperSqlJdbcUrlFactoryTest {

    private static final HyperSqlJdbcUrlFactory URL_FACTORY = new HyperSqlJdbcUrlFactory();

    @Test
    public void compatibilityModeParamNull() {
        assertEquals(Collections.emptyMap(), URL_FACTORY.compatibilityModeParam(null));
    }

    @Test
    public void compatibilityModeParamRegular() {
        assertEquals(Collections.emptyMap(),
                     URL_FACTORY.compatibilityModeParam(CompatibilityMode.REGULAR));
    }

    @Test
    public void compatibilityModeDerby() {
        assertEquals(Collections.emptyMap(),
                     URL_FACTORY.compatibilityModeParam(CompatibilityMode.Derby));
    }

    @Test
    public void compatibilityModeMssql() {
        assertEquals(HyperSqlJdbcUrlFactory.SQL_SYNTAX_MSSQLSERVER,
                     URL_FACTORY.compatibilityModeParam(CompatibilityMode.MSSQLServer)
                                .entrySet()
                                .iterator()
                                .next()
                                .getKey());
    }

    @Test
    public void compatibilityModeOracle() {
        assertEquals(HyperSqlJdbcUrlFactory.SQL_SYNTAX_ORACLE,
                     URL_FACTORY.compatibilityModeParam(CompatibilityMode.Oracle)
                                .entrySet()
                                .iterator()
                                .next()
                                .getKey());
    }

    @Test
    public void compatibilityModeMysql() {
        assertEquals(HyperSqlJdbcUrlFactory.SQL_SYNTAX_MYSQL,
                     URL_FACTORY.compatibilityModeParam(CompatibilityMode.MySQL)
                                .entrySet()
                                .iterator()
                                .next()
                                .getKey());
    }

    @Test
    public void compatibilityModeDB2() {
        assertEquals(HyperSqlJdbcUrlFactory.SQL_SYNTAX_DB2,
                     URL_FACTORY.compatibilityModeParam(CompatibilityMode.DB2)
                                .entrySet()
                                .iterator()
                                .next()
                                .getKey());
    }

    @Test
    public void compatibilityModePostgresql() {
        assertEquals(HyperSqlJdbcUrlFactory.SQL_SYNTAX_POSTGRESQL,
                     URL_FACTORY.compatibilityModeParam(CompatibilityMode.PostgreSQL)
                                .entrySet()
                                .iterator()
                                .next()
                                .getKey());
    }

    @Test
    public void compatibilityModeHsqldb() {
        assertEquals(Collections.emptyMap(),
                     URL_FACTORY.compatibilityModeParam(CompatibilityMode.HSQLDB));
    }

    @Test
    public void createForInitialization() {
        final String name = "name";
        final String urlForInitialization = new HyperSqlJdbcUrlFactory()
                .connectionUrlForInitialization(name, Collections.emptyMap());
        final String[] elements = urlForInitialization.split(";");
        assertEquals(3, elements.length);
        assertEquals(HyperSqlJdbcUrlFactory.HSQLDB_MEM_URL + name, elements[0]);
        assertEquals("create=true", elements[1]);
        assertEquals("shutdown=true", elements[2]);
    }

    @Test
    public void createForReuse() {
        final String name = "name";
        final String connectionUrl = new HyperSqlJdbcUrlFactory().connectionUrl(name, Collections.emptyMap());
        assertEquals(HyperSqlJdbcUrlFactory.HSQLDB_MEM_URL + name, connectionUrl);
    }
}
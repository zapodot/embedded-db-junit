package org.zapodot.junit.db.internal;

import org.junit.Test;
import org.zapodot.junit.db.common.CompatibilityMode;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author zapodot
 */
public class H2JdbcUrlFactoryTest {

    @Test
    public void testFilterInitPropertiesNoInit() {
        Map<String, String> properties = new HashMap<>();
        properties.put("someKey", "someValue");
        assertEquals(properties, H2JdbcUrlFactory.filterInitProperties(properties));
    }

    @Test
    public void testFilterInitPropertiesInit() {
        final Map<String, String> properties = createPropertyWithInitValue("someValue");
        assertEquals(Collections.emptyMap(), H2JdbcUrlFactory.filterInitProperties(properties));
    }

    private Map<String, String> createPropertyWithInitValue(final String initPropValue) {
        final Map<String, String> properties = new HashMap<>();
        properties.put(H2JdbcUrlFactory.PROP_INIT_SQL, initPropValue);
        return properties;
    }

    @Test(expected = NullPointerException.class)
    public void testBuildWithNameNull() {
        new H2JdbcUrlFactory().connectionUrl(null, null);
    }

    @Test
    public void testBuildWitNameAndNoFilter() {
        final String name = "illegalSqlFromResource";
        final String jdbcUrl = new H2JdbcUrlFactory()
                .connectionUrlForInitialization(name, createPropertyWithInitValue("something"));
        assertTrue(jdbcUrl.contains(H2JdbcUrlFactory.PROP_INIT_SQL));
    }

    @Test
    public void testBuildWitNameAndFilter() {
        final String jdbcUrl = new H2JdbcUrlFactory()
                .connectionUrl("illegalSqlFromResource", createPropertyWithInitValue("something"));
        assertFalse(jdbcUrl.contains(H2JdbcUrlFactory.PROP_INIT_SQL));
    }

    @Test
    public void testBuildWithoutFilterAndNullProperties() {
        final String dbName = "illegalSqlFromResource";
        final String jdbcUrl = new H2JdbcUrlFactory().connectionUrl(dbName, null);
        assertEquals(H2JdbcUrlFactory.H2_IN_MEMORY_JDBC_URL_PREFIX + dbName, jdbcUrl);

    }

    @Test
    public void testBuildWithFilterAndNullProperties() {
        final String dbName = "illegalSqlFromResource";
        final String jdbcUrl = new H2JdbcUrlFactory().connectionUrlForInitialization(dbName, null);
        assertEquals(H2JdbcUrlFactory.H2_IN_MEMORY_JDBC_URL_PREFIX + dbName, jdbcUrl);
    }

    @Test
    public void testBuildWithCompatibilityMode() {
        final H2JdbcUrlFactory h2JdbcUrlFactory = new H2JdbcUrlFactory();
        final String name = "name";
        final String jdbcUrl = h2JdbcUrlFactory
                .connectionUrlForInitialization(name, h2JdbcUrlFactory.compatibilityModeParam(
                        CompatibilityMode.DB2));
        final String[] elements = jdbcUrl.split(";");
        assertEquals(2, elements.length);
        assertEquals(H2JdbcUrlFactory.H2_IN_MEMORY_JDBC_URL_PREFIX + name, elements[0]);
        assertEquals("MODE=DB2", elements[1]);
    }
}
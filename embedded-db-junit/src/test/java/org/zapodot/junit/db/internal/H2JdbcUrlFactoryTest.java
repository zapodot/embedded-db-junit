package org.zapodot.junit.db.internal;

import org.junit.Test;
import org.zapodot.junit.db.EmbeddedDatabaseRule;

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author zapodot
 */
public class H2JdbcUrlFactoryTest {

    @Test
    public void testFilterInitPropertiesNoInit() throws Exception {
        Map<String, String> properties = new HashMap<>();
        properties.put("someKey", "somevalue");
        assertEquals(properties, H2JdbcUrlFactory.filterInitProperties(properties));
    }

    @Test
    public void testFilterInitPropertiesInit() throws Exception {
        final Map<String, String> properties = createPropertyWithInitValue("somevalue");
        assertEquals(Collections.emptyMap(), H2JdbcUrlFactory.filterInitProperties(properties));
    }

    private Map<String, String> createPropertyWithInitValue(final String initPropValue) {
        final Map<String, String> properties = new HashMap<>();
        properties.put(EmbeddedDatabaseRule.PROP_INIT_SQL, initPropValue);
        return properties;
    }

    @Test(expected = NullPointerException.class)
    public void testBuildWithNameNull() throws Exception {
        H2JdbcUrlFactory.buildWithNameAndProperties(null, null);
    }

    @Test
    public void testBuildWitNameAndNoFilter() throws Exception {
        final String name = "name";
        final String jdbcUrl = H2JdbcUrlFactory.buildWithNameAndProperties(name, createPropertyWithInitValue("something"));
        assertTrue(jdbcUrl.contains(EmbeddedDatabaseRule.PROP_INIT_SQL));
    }

    @Test
    public void testBuildWitNameAndFilter() throws Exception {
        final String jdbcUrl = H2JdbcUrlFactory.buildFilteringInitProperties("name", createPropertyWithInitValue("something"));
        assertFalse(jdbcUrl.contains(EmbeddedDatabaseRule.PROP_INIT_SQL));
    }

    @Test
    public void testBuildWithoutFilterAndNullProperties() throws Exception {
        final String dbName = "name";
        final String jdbcUrl = H2JdbcUrlFactory.buildWithNameAndProperties(dbName, null);
        assertEquals(H2JdbcUrlFactory.H2_IN_MEMORY_JDBC_URL_PREFIX + dbName, jdbcUrl);

    }

    @Test
    public void testBuildWithFilterAndNullProperties() throws Exception {
        final String dbName = "name";
        final String jdbcUrl = H2JdbcUrlFactory.buildFilteringInitProperties(dbName, null);
        assertEquals(H2JdbcUrlFactory.H2_IN_MEMORY_JDBC_URL_PREFIX + dbName, jdbcUrl);

    }

    @Test(expected = IllegalAccessException.class)
    public void testInstantiation() throws Exception {
        final Constructor<H2JdbcUrlFactory> declaredConstructor = H2JdbcUrlFactory.class.getDeclaredConstructor();
        declaredConstructor.newInstance();

    }

    /**
     * This test is added only to reach 100% test coverage
     * @throws Exception
     */
    @Test
    public void testFakeInstantiation() throws Exception {
        final Constructor<H2JdbcUrlFactory> declaredConstructor = H2JdbcUrlFactory.class.getDeclaredConstructor();
        declaredConstructor.setAccessible(true);
        declaredConstructor.newInstance();

    }
}
package org.zapodot.junit.db.plugin;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class FlywayInitializerCheckConfigurationTest {

    public static final String INSTALLED_BY = "user";
    public static final String TABLE = "table";
    public static final String SCHEMA_ONE = "schemaOne";
    public static final String SCHEMA_TWO = "schemaTwo";
    public static final String PLACEHOLDER_PREFIX = "prefix";
    public static final String PLACEHOLDER_SUFFIX = "suffix";
    public static final String PLACEHOLDER_KEY = "key";
    public static final String PLACEHOLDER_VALUE = "value";
    public static final String ENCODING = StandardCharsets.UTF_8.name();
    public static final String TARGET_VERSION = "2";
    public static final String LOCATION = "classpath:placeholder";

    @Test
    public void testBuilder() {
        final FlywayInitializer flywayInitializer = new FlywayInitializer.Builder().withInstalledBy(INSTALLED_BY)
                .withTable(TABLE)
                .withPlaceholders(
                        ImmutableMap.of(PLACEHOLDER_KEY,
                                PLACEHOLDER_VALUE))
                .withPlaceholderPrefix(PLACEHOLDER_PREFIX)
                .withPlaceholderSuffix(PLACEHOLDER_SUFFIX)
                .withSchemas(SCHEMA_ONE, SCHEMA_TWO)
                .withTarget(TARGET_VERSION)
                .withAllowMixed()
                .withDoNotValidateOnMigrate()
                .withEncoding(ENCODING)
                .withLocations(LOCATION)
                .build();
        assertEquals(INSTALLED_BY, flywayInitializer.getFlywayConfiguration().getInstalledBy());
        assertEquals(TABLE, flywayInitializer.getFlywayConfiguration().getTable());
        assertEquals(1, flywayInitializer.getFlywayConfiguration().getPlaceholders().size());
        assertEquals(PLACEHOLDER_VALUE, flywayInitializer.getFlywayConfiguration().getPlaceholders().get(PLACEHOLDER_KEY));
        assertEquals(PLACEHOLDER_PREFIX, flywayInitializer.getFlywayConfiguration().getPlaceholderPrefix());
        assertEquals(PLACEHOLDER_SUFFIX, flywayInitializer.getFlywayConfiguration().getPlaceholderSuffix());
        assertEquals(2, flywayInitializer.getFlywayConfiguration().getSchemas().length);
        assertEquals(SCHEMA_ONE, flywayInitializer.getFlywayConfiguration().getSchemas()[0]);
        assertEquals(SCHEMA_TWO, flywayInitializer.getFlywayConfiguration().getSchemas()[1]);
        assertTrue(flywayInitializer.getFlywayConfiguration().isMixed());
        assertNotNull(flywayInitializer.getFlywayConfiguration().getIgnoreMigrationPatterns());
        assertFalse(flywayInitializer.getFlywayConfiguration().isValidateOnMigrate());
        assertEquals(ENCODING, flywayInitializer.getFlywayConfiguration().getEncoding().name());
        assertEquals(TARGET_VERSION, flywayInitializer.getFlywayConfiguration().getTarget().getVersion());
        assertEquals(LOCATION, flywayInitializer.getFlywayConfiguration().getLocations()[0].getDescriptor());

    }

    @Test
    public void ignorePatterns() {
        FlywayInitializer.builder().withIgnoreMigrationPatterns();
    }
}
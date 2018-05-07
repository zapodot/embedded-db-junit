package org.zapodot.junit.db.plugin;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.configuration.FlywayConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zapodot.junit.db.internal.EmbeddedDataSource;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

/**
 * Plugin for initializing in-memory database using <a href="https://flywaydb.org">Flyway</a>.
 */
public class FlywayInitializer implements InitializationPlugin {

    private final FlywayConfiguration flywayConfiguration;

    private static final Logger LOGGER = LoggerFactory.getLogger(FlywayInitializer.class);

    public static class Builder {
        private final Flyway flyway = new Flyway();

        public Builder() {
            flyway.setEncoding(StandardCharsets.UTF_8.name());
            flyway.setTarget(MigrationVersion.LATEST);
        }

        public Builder withInstalledBy(final String installedBy) {
            flyway.setInstalledBy(installedBy);
            return this;
        }

        public Builder withAllowMixed() {
            flyway.setMixed(true);
            return this;
        }

        public Builder withIgnoreMissingMigrations() {
            flyway.setIgnoreMissingMigrations(true);
            return this;
        }

        public Builder withDoNotIgnoreFutureMigrations() {
            flyway.setIgnoreFutureMigrations(false);
            return this;
        }

        public Builder withDoNotValidateOnMigrate() {
            flyway.setValidateOnMigrate(false);
            return this;
        }

        public Builder withLocations(final String... locations) {
            flyway.setLocations(locations);
            return this;
        }

        public Builder withEncoding(final String encoding) {
            flyway.setEncoding(encoding);
            return this;
        }

        public Builder withSchemas(final String... schemas) {
            flyway.setSchemas(schemas);
            return this;
        }

        public Builder withTable(final String table) {
            flyway.setTable(table);
            return this;
        }

        public Builder withPlaceholders(final Map<String, String> placeholders) {
            flyway.setPlaceholders(placeholders);
            return this;
        }

        public Builder withPlaceholderPrefix(final String placeholderPrefix) {
            flyway.setPlaceholderPrefix(placeholderPrefix);
            return this;
        }

        public Builder withPlaceholderSuffix(final String placeholderSuffix) {
            flyway.setPlaceholderSuffix(placeholderSuffix);
            return this;
        }

        public Builder withTarget(final String targetVersion) {
            flyway.setTargetAsString(targetVersion);
            return this;
        }

        public FlywayInitializer build() {
            return new FlywayInitializer(flyway);
        }
    }

    private FlywayInitializer(final FlywayConfiguration flywayConfiguration) {
        this.flywayConfiguration = flywayConfiguration;
    }

    @Override
    public void connectionMade(final String name, final Connection connection) {
        if (flywayConfiguration.getSchemas() != null) {
            createSchemas(connection, flywayConfiguration.getSchemas());
        }
        final Flyway flyway = new Flyway(flywayConfiguration);
        flyway.setDataSource(EmbeddedDataSource.create(connection));
        flyway.migrate();
    }

    FlywayConfiguration getFlywayConfiguration() {
        return flywayConfiguration;
    }

    private int createSchemas(final Connection connection, final String[] schemas) {
        try {
            int created = 0;
            for (String schema : schemas) {
                LOGGER.debug("Create schema \"{}\"", schema);
                try(final PreparedStatement preparedStatement = connection.prepareStatement(String.format("CREATE SCHEMA IF NOT EXISTS %s", schema))) {
                    preparedStatement.execute();
                }
                created++;
            }
            return created;
        } catch (SQLException e) {
            throw new RuntimeException(String.format("Could not create schemas %s", schemas), e);
        }
    }
}

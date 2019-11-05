package org.zapodot.junit.db.plugin;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationVersion;
import org.flywaydb.core.api.configuration.Configuration;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zapodot.junit.db.internal.EmbeddedDataSource;

import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.Map;

/**
 * Plugin for initializing in-memory database using <a href="https://flywaydb.org">Flyway</a>.
 */
public class FlywayInitializer implements InitializationPlugin {

    private final FluentConfiguration flywayConfiguration;

    private static final Logger LOGGER = LoggerFactory.getLogger(FlywayInitializer.class);

    public static class Builder {

        private final FluentConfiguration fluentConfiguration = Flyway.configure();

        public Builder() {
            fluentConfiguration.encoding(StandardCharsets.UTF_8.name())
                               .target(MigrationVersion.LATEST);
        }

        public Builder withInstalledBy(final String installedBy) {
            fluentConfiguration.installedBy(installedBy);
            return this;
        }

        public Builder withAllowMixed() {
            fluentConfiguration.mixed(true);
            return this;
        }

        public Builder withIgnoreMissingMigrations() {
            fluentConfiguration.ignoreMissingMigrations(true);
            return this;
        }

        public Builder withDoNotIgnoreFutureMigrations() {
            fluentConfiguration.ignoreFutureMigrations(false);
            return this;
        }

        public Builder withDoNotValidateOnMigrate() {
            fluentConfiguration.validateOnMigrate(false);
            return this;
        }

        public Builder withLocations(final String... locations) {
            fluentConfiguration.locations(locations);
            return this;
        }

        public Builder withEncoding(final String encoding) {
            fluentConfiguration.encoding(encoding);
            return this;
        }

        public Builder withSchemas(final String... schemas) {
            fluentConfiguration.schemas(schemas);
            return this;
        }

        public Builder withTable(final String table) {
            fluentConfiguration.table(table);
            return this;
        }

        public Builder withPlaceholders(final Map<String, String> placeholders) {
            fluentConfiguration.placeholders(placeholders);
            return this;
        }

        public Builder withPlaceholderPrefix(final String placeholderPrefix) {
            fluentConfiguration.placeholderPrefix(placeholderPrefix);
            return this;
        }

        public Builder withPlaceholderSuffix(final String placeholderSuffix) {
            fluentConfiguration.placeholderSuffix(placeholderSuffix);
            return this;
        }

        public Builder withTarget(final String targetVersion) {
            fluentConfiguration.target(targetVersion);
            return this;
        }

        public FlywayInitializer build() {
            return new FlywayInitializer(fluentConfiguration);
        }
    }

    public static Builder builder() {
        return new FlywayInitializer.Builder();
    }

    private FlywayInitializer(final FluentConfiguration flywayConfiguration) {
        this.flywayConfiguration = flywayConfiguration;
    }

    @Override
    public void connectionMade(final String name, final Connection connection) {
        LOGGER.debug("Initiating Flyway migration");
        final Flyway flyway = new Flyway(flywayConfiguration.dataSource(EmbeddedDataSource.create(connection)));
        flyway.migrate();
    }

    Configuration getFlywayConfiguration() {
        return flywayConfiguration;
    }

}

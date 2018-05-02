package org.zapodot.junit.db.internal;

import org.zapodot.junit.db.EmbeddedDatabaseRule;

import java.util.Map;

public interface JdbcUrlFactory {

    String connectionUrlForInitialization(final String name, final Map<String, String> properties);

    String connectionUrl(final String name, final Map<String, String> properties);

    Map<String, String> compatibilityModeParam(final EmbeddedDatabaseRule.CompatibilityMode compatibilityMode);

    default String createJdbcUrlParameterString(final Map<String, String> properties) {
        if (properties == null) {
            return "";
        }
        final StringBuilder paramStringBuilder = new StringBuilder();
        for (final Map.Entry<String, String> property : properties.entrySet()) {
            if (property.getValue() != null) {
                paramStringBuilder.append(';')
                                  .append(property.getKey())
                                  .append('=')
                                  .append(property.getValue());
            }
        }
        return paramStringBuilder.toString();
    }
}

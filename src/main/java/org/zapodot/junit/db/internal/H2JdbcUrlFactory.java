package org.zapodot.junit.db.internal;

import org.zapodot.junit.db.EmbeddedDatabaseRule;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zapodot
 */
public class H2JdbcUrlFactory {

    private H2JdbcUrlFactory() {
    }

    static final String H2_IN_MEMORY_JDBC_URL_PREFIX = "jdbc:h2:mem:";

    public static Map<String, String> filterInitProperties(final Map<String, String> jdbcUrlProperties) {
        if (jdbcUrlProperties == null) {
            return null;
        } else {
            final Map<String, String> propertiesCopy = new LinkedHashMap<>();
            for (final Map.Entry<String, String> property : jdbcUrlProperties.entrySet()) {
                if (!EmbeddedDatabaseRule.PROP_INIT_SQL.equalsIgnoreCase(property.getKey())) {
                    propertiesCopy.put(property.getKey(), property.getValue());
                }
            }
            return propertiesCopy;
        }
    }

    private static String createJdbcUrlParameterString(final Map<String, String> properties) {
        if (properties == null) {
            return "";
        }
        final StringBuilder paramStringBuilder = new StringBuilder("");
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

    public static String buildWithNameAndProperties(final String name, final Map<String, String> properties) {
        if (name == null) {
            throw new NullPointerException("The value of the \"name\" parameter can not be null");
        }
        return new StringBuilder(H2_IN_MEMORY_JDBC_URL_PREFIX)
                .append(name)
                .append(createJdbcUrlParameterString(properties))
                .toString();
    }

    public static String buildFilteringInitProperties(final String name, final Map<String, String> properties) {
        return buildWithNameAndProperties(name, filterInitProperties(properties));
    }
}

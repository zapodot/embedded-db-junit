package org.zapodot.junit.db.internal;

import org.zapodot.junit.db.common.CompatibilityMode;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author zapodot
 */
public class H2JdbcUrlFactory implements JdbcUrlFactory {

    public static final String PROP_MODE = "MODE";

    public static final String PROP_INIT_SQL = "INIT";

    static final String H2_IN_MEMORY_JDBC_URL_PREFIX = "jdbc:h2:mem:";

    static Map<String, String> filterInitProperties(final Map<String, String> jdbcUrlProperties) {
        if (jdbcUrlProperties == null) {
            return null;
        } else {
            final Map<String, String> propertiesCopy = new LinkedHashMap<>();
            for (final Map.Entry<String, String> property : jdbcUrlProperties.entrySet()) {
                if (!PROP_INIT_SQL.equalsIgnoreCase(property.getKey())) {
                    propertiesCopy.put(property.getKey(), property.getValue());
                }
            }
            return propertiesCopy;
        }
    }


    private String buildWithNameAndProperties(final String name, final Map<String, String> properties) {
        if (name == null) {
            throw new NullPointerException("The value of the \"name\" parameter can not be null");
        }
        return new StringBuilder(H2_IN_MEMORY_JDBC_URL_PREFIX)
                .append(name)
                .append(createJdbcUrlParameterString(properties))
                .toString();
    }

    @Override
    public String connectionUrlForInitialization(final String name, final Map<String, String> properties) {
        return buildWithNameAndProperties(name, properties);

    }

    @Override
    public String connectionUrl(final String name, final Map<String, String> properties) {
        return buildWithNameAndProperties(name,filterInitProperties(properties));
    }

    @Override
    public Map<String, String> compatibilityModeParam(final CompatibilityMode compatibilityMode) {
        return Optional.ofNullable(compatibilityMode)
                       .filter(cm -> cm != CompatibilityMode.REGULAR)
                       .map(cm -> Collections.singletonMap(PROP_MODE, cm.name()))
                       .orElse(Collections.emptyMap());
    }
}

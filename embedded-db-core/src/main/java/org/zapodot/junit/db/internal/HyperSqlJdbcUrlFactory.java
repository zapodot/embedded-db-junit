package org.zapodot.junit.db.internal;

import org.zapodot.junit.db.common.CompatibilityMode;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class HyperSqlJdbcUrlFactory implements JdbcUrlFactory {

    public static final String HSQLDB_MEM_URL = "jdbc:hsqldb:mem:";

    private static final String ENABLED_SETTING = Boolean.TRUE.toString();

    static final String SQL_SYNTAX_MSSQLSERVER = "sql.syntax_mss";

    static final String SQL_SYNTAX_DB2 = "sql.syntax_db2";

    static final String SQL_SYNTAX_ORACLE = "sql.syntax_ora";

    static final String SQL_SYNTAX_MYSQL = "sql.syntax_mys";

    static final String SQL_SYNTAX_POSTGRESQL = "sql.syntax_pgs";

    private static final String DATABASE_CREATE_SETTING = "create";

    @Override
    public String connectionUrlForInitialization(final String name, final Map<String, String> properties) {
        return connectionUrl(name, addCreateProperty(properties));
    }

    @Override
    public String connectionUrl(final String name, final Map<String, String> properties) {
        return new StringBuilder(HSQLDB_MEM_URL).append(name)
                                                .append(createJdbcUrlParameterString(properties))
                                                .toString();
    }

    private Map<String, String> addCreateProperty(final Map<String, String> properties) {
        final Map<String, String> props = new LinkedHashMap<>();
        props.putAll(properties);
        props.put(DATABASE_CREATE_SETTING, ENABLED_SETTING);
        return props;
    }

    @Override
    public Map<String, String> compatibilityModeParam(final CompatibilityMode compatibilityMode) {
        if (CompatibilityMode.MSSQLServer == compatibilityMode) {
            return createEnabledSettingMap(SQL_SYNTAX_MSSQLSERVER);
        } else if (CompatibilityMode.DB2 == compatibilityMode) {
            return createEnabledSettingMap(SQL_SYNTAX_DB2);
        } else if (CompatibilityMode.Oracle == compatibilityMode) {
            return createEnabledSettingMap(SQL_SYNTAX_ORACLE);
        } else if (CompatibilityMode.MySQL == compatibilityMode) {
            return createEnabledSettingMap(SQL_SYNTAX_MYSQL);
        } else if (CompatibilityMode.PostgreSQL == compatibilityMode) {
            return createEnabledSettingMap(SQL_SYNTAX_POSTGRESQL);
        } else {
            return Collections.emptyMap();
        }
    }

    private Map<String, String> createEnabledSettingMap(final String property) {
        return Collections.singletonMap(property, ENABLED_SETTING);
    }
}

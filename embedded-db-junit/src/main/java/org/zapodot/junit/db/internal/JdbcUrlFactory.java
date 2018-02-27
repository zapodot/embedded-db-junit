package org.zapodot.junit.db.internal;

import java.util.Map;

public interface JdbcUrlFactory {

    String connectionUrlForInitialization(final String name, final Map<String, String> properties);

    String connectionUrl(final String name, final Map<String, String> properties);
}

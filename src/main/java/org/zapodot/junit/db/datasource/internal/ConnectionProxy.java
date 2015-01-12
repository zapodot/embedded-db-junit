package org.zapodot.junit.db.datasource.internal;

import java.sql.Connection;

public interface ConnectionProxy {
    Connection getDelegatedConnection();

    void setDelegatedConnection(Connection delegatedConnection);
}

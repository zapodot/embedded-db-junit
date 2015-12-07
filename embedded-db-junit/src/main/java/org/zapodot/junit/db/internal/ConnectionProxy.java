package org.zapodot.junit.db.internal;

import java.sql.Connection;

/**
 * Needs to be public to be used by ByteBuddy. Part of internal api, so it may be changed or removed without prior warning
 */
public interface ConnectionProxy {
    Connection getDelegatedConnection();

    void setDelegatedConnection(Connection delegatedConnection);
}

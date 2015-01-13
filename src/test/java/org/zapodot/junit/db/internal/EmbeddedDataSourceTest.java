package org.zapodot.junit.db.internal;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.zapodot.junit.db.EmbeddedDatabaseRule;

import javax.sql.DataSource;

import java.io.PrintWriter;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class EmbeddedDataSourceTest {

    @Rule
    public EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.builder().withInitialSql(
            "CREATE TABLE Customer(id INTEGER PRIMARY KEY, name VARCHAR(512)); ").build();
    private DataSource dataSource;

    @Before
    public void setUp() throws Exception {
        dataSource = embeddedDatabaseRule.getDataSource();

    }

    @Test
    public void testGetConnection() throws Exception {
        assertNotNull(dataSource.getConnection());
    }

    @Test
    public void testGetConnectionWithUserNamePassword() throws Exception {
        assertNotNull(dataSource.getConnection("", ""));
    }

    @Test
    public void testGetLogWriter() throws Exception {
        dataSource.setLogWriter(new PrintWriter(System.err));
        assertNotNull(dataSource.getLogWriter());
    }


    @Test
    public void testSetLoginTimeout() throws Exception {
        dataSource.setLoginTimeout(1);
        assertEquals(0, dataSource.getLoginTimeout());
    }

    @Test
    public void testGetParentLogger() throws Exception {
        assertEquals(Logger.getGlobal(), dataSource.getParentLogger());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testUnwrap() throws Exception {
        dataSource.unwrap(DataSource.class);
    }

    @Test
    public void testIsWrapperFor() throws Exception {
        assertFalse(dataSource.isWrapperFor(JdbcDataSource.class));
    }
}
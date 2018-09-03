package org.zapodot.junit.db;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;
import org.zapodot.junit.db.EmbeddedDatabaseRule;
import org.zapodot.junit.db.internal.Slf4jInfoWriter;

import javax.sql.DataSource;

import java.io.PrintWriter;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

public class EmbeddedDataSourceTest {

    @Rule
    public final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.builder().withInitialSql(
            "CREATE TABLE Customer(id INTEGER PRIMARY KEY, illegalSqlFromResource VARCHAR(512)); ").build();
    private DataSource dataSource;

    @Before
    public void setUp() {
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
    public void testGetLog() throws Exception {

        try(final PrintWriter printWriter = dataSource.getLogWriter()) {
            printWriter.write("Test", 0, 4);
            assertNotNull(printWriter);
            printWriter.flush();
        }
    }

    @Test
    public void slf4jInfoWriter() {
        org.slf4j.Logger logger = Mockito.spy(org.slf4j.Logger.class);
        final Slf4jInfoWriter slf4jInfoWriter = new Slf4jInfoWriter(logger);
        final char[] charArray = "test".toCharArray();
        slf4jInfoWriter.write((char[]) null, 0, 0);
        slf4jInfoWriter.write(charArray, 0, charArray.length);
        verify(logger).info(anyString());
        verifyNoMoreInteractions(logger);
    }

    @Test(expected = IllegalArgumentException.class)
    public void nullSlf4jInfoWritter() {
        new Slf4jInfoWriter(null);
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
package org.zapodot.junit.db;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.junit.Assert.assertEquals;

public class EmbeddedDatabaseRuleUsingSpringTest {


    public static final String CUSTOMER_NAME = "John Doe";
    @Rule
    public final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.builder().withInitialSql(
            "CREATE TABLE Customer(id INTEGER PRIMARY KEY, name VARCHAR(512)); "
            + "INSERT INTO CUSTOMER(id, name) VALUES (1, '" + CUSTOMER_NAME + "')").build();

    private JdbcOperations jdbcOperations;

    @Before
    public void setUp() throws Exception {
        this.jdbcOperations = new JdbcTemplate(embeddedDatabaseRule.getDataSource());

    }

    @Test
    public void testLookupName() throws Exception {
        final String nameOfCustomer1 = this.jdbcOperations.queryForObject("SELECT name from CUSTOMER where id = ?", String.class, 1);
        assertEquals(nameOfCustomer1, CUSTOMER_NAME);

    }

    @Test
    public void testInsertCustomer() throws Exception {
        final int id = 2;
        final String customerName = "Jane Doe";

        final int updatedRows = this.jdbcOperations.update("INSERT INTO CUSTOMER(id, name) VALUES(?,?)", id, customerName);

        assertEquals(1, updatedRows);
        assertEquals(customerName, this.jdbcOperations.queryForObject("SELECT name from CUSTOMER where id = ?", String.class, id));

    }
}
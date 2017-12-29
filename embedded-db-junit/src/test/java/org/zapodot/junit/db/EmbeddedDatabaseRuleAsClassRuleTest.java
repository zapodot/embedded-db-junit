package org.zapodot.junit.db;

import com.github.davidmoten.rx.jdbc.Database;
import org.junit.ClassRule;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertEquals;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EmbeddedDatabaseRuleAsClassRuleTest {


    @ClassRule
    public static final EmbeddedDatabaseRule embeddedDatabaseClassRule = EmbeddedDatabaseRule.builder().withInitialSql(
            "CREATE TABLE Customer(id INTEGER PRIMARY KEY, name VARCHAR(512)); "
            + "INSERT INTO CUSTOMER(id, name) VALUES (1, 'John Doe')").build();

    @Test
    public void testA() throws Exception {
        final Database database = Database.from(embeddedDatabaseClassRule.getConnection());
        assertEquals(1L,
                     database.update("INSERT INTO CUSTOMER(id, name) VALUES (2, 'Jane Doe')")
                             .count()
                             .toBlocking()
                             .single().longValue());

    }


    @Test(expected = RuntimeException.class)
    public void testB() {
        final Database database = Database.from(embeddedDatabaseClassRule.getConnection());

        database.update("INSERT INTO CUSTOMER(id, name) VALUES (2, 'Jenny Doe')")
                .count()
                .toBlocking()
                .single()
                .longValue();
    }
}
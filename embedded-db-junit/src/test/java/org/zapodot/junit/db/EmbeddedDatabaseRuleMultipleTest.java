package org.zapodot.junit.db;

import com.github.davidmoten.rx.jdbc.Database;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;

import java.util.Date;

import static org.junit.Assert.assertTrue;

public class EmbeddedDatabaseRuleMultipleTest {

    // If you have more than one instance in your test class, you should name them explicitly
    @Rule
    public EmbeddedDatabaseRule embeddedDatabaseMysqlRule =
            EmbeddedDatabaseRule.builder().withName("db1").withMode("MySQL").build();

    @Rule
    public EmbeddedDatabaseRule embeddedDatabaseMsSqlServerRule =
            EmbeddedDatabaseRule.builder().withName("db2").withMode("MSSQLServer").build();

    @Test
    public void testStuff() throws Exception {
        final Database mysql = Database.from(embeddedDatabaseMysqlRule.getConnection());
        final Database mssqlServer = Database.from(embeddedDatabaseMsSqlServerRule.getConnection());

        final Observable<Date> dateMysql = mysql.select("select sysdate from dual").getAs(Date.class);
        final Observable<Date> dateMssql = mssqlServer.select("select sysdate from dual").getAs(Date.class);

        assertTrue(Observable.zip(dateMysql, dateMssql, (date, date2) -> date != null && date2 != null).toBlocking().single());


    }
}
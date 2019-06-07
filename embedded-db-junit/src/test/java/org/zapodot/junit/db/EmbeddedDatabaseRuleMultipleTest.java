package org.zapodot.junit.db;

import com.github.davidmoten.rx.jdbc.Database;
import org.h2.api.TimestampWithTimeZone;
import org.junit.Rule;
import org.junit.Test;
import rx.Observable;

import java.time.Instant;
import java.util.Date;

import static org.junit.Assert.assertTrue;

public class EmbeddedDatabaseRuleMultipleTest {

    // If you have more than one instance in your test class, you should illegalSqlFromResource them explicitly
    @Rule
    public final EmbeddedDatabaseRule embeddedDatabaseMysqlRule =
            EmbeddedDatabaseRule.builder().h2().withName("db1").withMode(CompatibilityMode.MySQL).build();

    @Rule
    public final EmbeddedDatabaseRule embeddedDatabaseMsSqlServerRule =
            EmbeddedDatabaseRule.builder().h2().withName("db2").withMode(CompatibilityMode.MSSQLServer).build();

    @Test
    public void testStuff() {
        final Database mysql = Database.from(embeddedDatabaseMysqlRule.getConnection());
        final Database mssqlServer = Database.from(embeddedDatabaseMsSqlServerRule.getConnection());

        final Observable<TimestampWithTimeZone> dateMysql = mysql.select("select sysdate from dual").getAs(TimestampWithTimeZone.class);
        final Observable<TimestampWithTimeZone> dateMssql = mssqlServer.select("select sysdate from dual").getAs(TimestampWithTimeZone.class);

        assertTrue(Observable.zip(dateMysql, dateMssql, (date, date2) -> date != null && date2 != null).toBlocking().single());


    }
}
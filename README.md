embedded-db-junit
=================

[![Build Status](https://travis-ci.org/zapodot/embedded-db-junit.svg)](https://travis-ci.org/zapodot/embedded-db-junit)
[![Coverage Status](https://img.shields.io/coveralls/zapodot/embedded-db-junit.svg)](https://coveralls.io/r/zapodot/embedded-db-junit)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.zapodot/embedded-db-junit/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.zapodot/embedded-db-junit)

[JUnit](http://junit.org/) Rule that provides a [H2 Embedded in-memory database](http://www.h2database.com/). It is compatible with all known JDBC access libraries such as [Spring JDBC](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/jdbc.html#jdbc-introduction), [RX-JDBC](https://github.com/davidmoten/rxjava-jdbc), [sql2o](http://www.sql2o.org/), [JDBI](http://jdbi.org/) or plain old JDBC.

## Why?
* makes it easy to stub out JDBC calls while still letting you JUnit test your SQL
* removes the need of having a database server running and available
* when you are refactoring legacy code where JDBC calls is tightly coupled with your business logic and you start off by testing it from the "outside" (as suggested by [Michael Feathers](http://www.informit.com/store/working-effectively-with-legacy-code-9780131177055?aid=15d186bd-1678-45e9-8ad3-fe53713e811b))

## Status
This library is distributed through the [Sonatype OSS repo](https://oss.sonatype.org/) and should thus be widely available.
Java 7 or higher is required.

## Changelog
* version 0.2: added datasource() for getting an embedded DataSource and suppressing close() call to the connection
* version 0.1: first release

## Usage

### Add dependency
#### Maven
```xml
<dependency>
    <groupId>org.zapodot</groupId>
    <artifactId>embedded-db-junit</artifactId>
    <version>0.2</version>
</dependency>
```

#### SBT
```scala
libraryDependencies += "org.zapodot" % "embedded-db-junit" % "0.2"
```

### Add to Junit test
```java
@Rule
public EmbeddedDatabaseRule dbRule = EmbeddedDatabaseRule
                                        .builder()
                                        .withMode("ORACLE")
                                        .withInitialSql("CREATE TABLE Customer(id INTEGER PRIMARY KEY, name VARCHAR(512)); "
                                                        + "INSERT INTO CUSTOMER(id, name) VALUES (1, 'John Doe')")
                                        .build();

@Test
public void testUsingRxJdbc() throws Exception {
    assertNotNull(dbRule.getConnection());
    final Database database = Database.from(dbRule.getConnection());
    assertNotNull(database.select("SELECT sysdate from DUAL")
                  .getAs(Date.class)
                  .toBlocking()
                  .single());

    assertEquals("John Doe", database.select("select name from customer where id=1")
                                     .getAs(String.class)
                                     .toBlocking()
                                     .single());
}

@Test
public void testUsingSpringJdbc() throws Exception {

    final JdbcOperations jdbcOperation = new JdbcTemplate(dbRule.getDataSource());
    final int id = 2;
    final String customerName = "Jane Doe";

    final int updatedRows = jdbcOperations.update("INSERT INTO CUSTOMER(id, name) VALUES(?,?)", id, customerName);

    assertEquals(1, updatedRows);
    assertEquals(customerName, jdbcOperations.queryForObject("SELECT name from CUSTOMER where id = ?", String.class, id));

}
```

#### Multiple data sources in the same test class
If you need more than one database instance in your test class, you should name them using the "withName" construct.
If not set the rule builder will generate the name using the name of the test class
```java
@Rule
public EmbeddedDatabaseRule embeddedDatabaseMysqlRule =
        EmbeddedDatabaseRule.builder().withName("db1").withMode("MySQL").build();

@Rule
public EmbeddedDatabaseRule embeddedDatabaseMsSqlServerRule =
        EmbeddedDatabaseRule.builder().withName("db2").withMode("MSSQLServer").build();
```

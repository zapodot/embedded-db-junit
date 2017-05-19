embedded-db-junit
=================

[![Build Status](https://travis-ci.org/zapodot/embedded-db-junit.svg)](https://travis-ci.org/zapodot/embedded-db-junit)
[![Coverage Status](https://img.shields.io/coveralls/zapodot/embedded-db-junit.svg)](https://coveralls.io/r/zapodot/embedded-db-junit)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.zapodot/embedded-db-junit/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.zapodot/embedded-db-junit)
[![Analytics](https://ga-beacon.appspot.com/UA-58568779-1/embedded-db-junit/README.md)](https://github.com/igrigorik/ga-beacon)
[![Apache V2 License](http://img.shields.io/badge/license-Apache%20V2-blue.svg)](//github.com/zapodot/embedded-db-junit/blob/master/LICENSE)
[![Libraries.io for GitHub](https://img.shields.io/librariesio/github/zapodot/embedded-db-junit.svg)](https://libraries.io/github/zapodot/embedded-db-junit)

[JUnit](http://junit.org/) Rule that provides a [H2 Embedded in-memory database](http://www.h2database.com/). It is compatible with all known JDBC access libraries such as [Spring JDBC](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/jdbc.html#jdbc-introduction), [RX-JDBC](//github.com/davidmoten/rxjava-jdbc), [sql2o](http://www.sql2o.org/), [JDBI](http://jdbi.org/) or plain old JDBC.

## Why?
* because you want to test the SQL code executed by your code without integrating with an actual DB server
* removes the need of having a database server running and available
* you are refactoring legacy code where JDBC calls is tightly coupled with your business logic and wants to start by testing the legacy code from the "outside" (as suggested by [Michael Feathers](http://www.informit.com/store/working-effectively-with-legacy-code-9780131177055?aid=15d186bd-1678-45e9-8ad3-fe53713e811b))
* starting with version 0.6 there is also a brand new InitializationPlugin API as well as a [Liquibase plugin implementation](./embedded-db-junit-liquibase/). This means you can now write tests for your database evolutions!

## Status
This library is distributed through the [Sonatype OSS repo](https://oss.sonatype.org/) and should thus be widely available.
Java 7 or higher is required.

## Changelog
* version 1.0-RC1: Fixed issue #8. InitializationPlugins will now be provided with a connection that will not be closed before the end of the test execution. Thanks to [@victornoel](//github.com/victornoel)
* version 0.9: improved logging as per request by @Gaibhne. Also updated H2 and SLF4J dependencies
* version 0.8: updated Mockito and ByteBuddy as suggested by @victornoel. Using an explicit name when the rule is invoked
as a method-based @Rule will issue a warning. Thx to [@victornoel](//github.com/victornoel) for valuable input
* version 0.7: merged pull request #3 which created by [@tmszdmsk](//github.com/tmszdmsk) allowing the developer to specify a schema for Liquibase. Thx!
* version 0.6: created InitializationPlugin API with the first implementation being the [LiquibaseInitializer](./embedded-db-junit-liquibase/)
* version 0.5: created builder method withInitialSqlFromResource that allows the initial SQL to be [read from file](#read-initial-sql-from-a-file-resource-v--05)
* version 0.4: created method getConnectionJdbcUrl that returns a filtered JDBC URL (for tools requiring a JDBC URL)
* version 0.3: updated all dependencies as well as some changes to the internal implementation
* version 0.2: added datasource() for getting an embedded DataSource and suppressing close() call to the connection
* version 0.1: first release

## Usage

### Add dependency
#### Maven
```xml
<dependency>
    <groupId>org.zapodot</groupId>
    <artifactId>embedded-db-junit</artifactId>
    <version>1.0-RC1</version>
</dependency>
```

#### SBT
```scala
libraryDependencies += "org.zapodot" % "embedded-db-junit" % "1.0-RC1"
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

    final int updatedRows = jdbcOperation.update("INSERT INTO CUSTOMER(id, name) VALUES(?,?)", id, customerName);

    assertEquals(1, updatedRows);
    assertEquals(customerName, jdbcOperation.queryForObject("SELECT name from CUSTOMER where id = ?", String.class, id));

}

@Test
public void testUsingConnectionUrl() throws Exception {

    try(final Connection connection = DriverManager.getConnection(embeddedDatabaseRule.getConnectionJdbcUrl())) {
        try(final Statement statement = connection.createStatement();
            final ResultSet resultSet = statement.executeQuery("SELECT * from CUSTOMER")
        ) {
            assertTrue(resultSet.next());
        }
    }

}

```

#### Read initial SQL from a file resource (v >= 0.5)
```java
@Rule
public EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.builder()
                                                                       .withInitialSqlFromResource(
                                                                               "classpath:initial.sql")
                                                                       .build();

@Test
public void testWithInitialSQL() throws Exception {
    try (final Connection connection = embeddedDatabaseRule.getConnection()) {

        try (final Statement statement = connection.createStatement();
             final ResultSet resultSet = statement.executeQuery("SELECT * from PEOPLE")) {

             assertTrue(resultSet.next());
        }

    }

}
```
In the example above a "classpath:" URI has been used to specify the location of the SQL file. All URIs that are supported by [H2's Pluggable File System](http://www.h2database.com/html/advanced.html#file_system) is supported.

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

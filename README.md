embedded-db-junit
=================

[![Build Status](https://github.com/zapodot/embedded-db-junit/actions/workflows/maven.yml/badge.svg)](https://github.com/zapodot/embedded-db-junit/actions/workflows/maven.yml)
[![Coverage status](https://codecov.io/gh/zapodot/embedded-db-junit/branch/master/graph/badge.svg?token=SvKrWdPu5e)](https://codecov.io/gh/zapodot/embedded-db-junit)
[![Maven Central](https://img.shields.io/maven-central/v/org.zapodot/embedded-db-junit.svg)](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22org.zapodot%22%20AND%20a%3A%22embedded-db-junit%22)
[![Apache V2 License](http://img.shields.io/badge/license-Apache%20V2-blue.svg)](//github.com/zapodot/embedded-db-junit/blob/master/LICENSE)
[![Open Source Helpers](https://www.codetriage.com/zapodot/embedded-db-junit/badges/users.svg)](https://www.codetriage.com/zapodot/embedded-db-junit)
[![Follow me @ Twitter](https://img.shields.io/twitter/follow/zapodot.svg?style=social&label=Follow)](https://twitter.com/intent/follow?screen_name=zapodot)

[JUnit](http://junit.org/) Rule that provides a in-memory database (both [H2](http://www.h2database.com/) and [HyperSQL](http://hsqldb.org) are supported). It is compatible with all known JDBC access libraries such as [Spring JDBC](http://docs.spring.io/spring/docs/current/spring-framework-reference/html/jdbc.html#jdbc-introduction), [RX-JDBC](//github.com/davidmoten/rxjava-jdbc), [sql2o](http://www.sql2o.org/), [JDBI](http://jdbi.org/) or plain old JDBC.

## Why?
* because you want to test the SQL code executed by your code without integrating with an actual DB server
* removes the need of having a database server running and available
* you are refactoring legacy code where JDBC calls is tightly coupled with your business logic and wants to start by testing the legacy code from the "outside" (as suggested by [Michael Feathers](http://www.informit.com/store/working-effectively-with-legacy-code-9780131177055?aid=15d186bd-1678-45e9-8ad3-fe53713e811b))
* you want to test your database evolutions with either they are maintened using [Liquibase](./embedded-db-junit-liquibase/) or [Flyway](./embedded-db-flyway/).

## Status
This library is distributed through the [Sonatype OSS repo](https://oss.sonatype.org/) and should thus be widely available.

| Version | Java version | JUnit version | H2 version | HSQLDB version | Branch                                                                        | Status        |
|---------| ------------ | ------------- | ---------- | -------------- |-------------------------------------------------------------------------------|---------------|
| 2.1.X   | 8.0          | 4.13/5.X      | 2.1.X      | 2.7.0          | [`master`](//github.com/zapodot/embedded-db-junit/tree/master)                | `active`      |
| 2.0.X   | 8.0          | 4.12/5.X      | 1.4.200    | 2.5.0          | [`release-2.0.x`](//github.com/zapodot/embedded-db-junit/tree/release-2.0.x)         | `maintenance` |
| 1.1.X   | 8.0          | 4.12          | 1.4.200    | 2.4.0          | [`release-1.1.x`](//github.com/zapodot/embedded-db-junit/tree/release-1.1.x)  | `obsolete`    |
| 1.0     | 1.7          | 4.12          | 1.4.196    | N/A            | [`release-1.x`](//github.com/zapodot/embedded-db-junit/tree/release-1.x)      | `obsolete`    | 

The versions that is described in this table are minimum versions. Later versions may be used but is currently not tested by the maintainer.

## Usage

### Add dependency
#### Maven

#### For JUnit 5 Jupiter
```xml
<dependency>
    <groupId>org.zapodot</groupId>
    <artifactId>embedded-db-junit-jupiter</artifactId>
    <version>2.1.0</version>
    <scope>test</scope>
</dependency>
```

#### For JUnit 4.X
```xml
<dependency>
    <groupId>org.zapodot</groupId>
    <artifactId>embedded-db-junit</artifactId>
    <version>2.1.0</version>
    <scope>test</scope>
</dependency>
```

##### Liquibase plugin
If you want to use the [Liquibase](//github.com/zapodot/embedded-db-junit/tree/master/embedded-db-junit-liquibase) plugin:
```xml
<dependency>
    <groupId>org.zapodot</groupId>
    <artifactId>embedded-db-junit-liquibase</artifactId>
    <version>2.1.0</version>
    <scope>test</scope>
</dependency>
```
##### Flyway plugin
If you want to use the [Flyway](//github.com/zapodot/embedded-db-junit/tree/master/embedded-db-flyway) plugin:
```xml
<dependency>
    <groupId>org.zapodot</groupId>
    <artifactId>embedded-db-flyway</artifactId>
    <version>2.1.0</version>
    <scope>test</scope>
</dependency>
```

#### SBT
```scala
libraryDependencies += "org.zapodot" % "embedded-db-junit" % "2.1.0" % "test"
```

### Add to Junit test
#### Junit 5.X Jupiter
##### Declarative style using annotations
```java
@EmbeddedDatabaseTest(
        engine = Engine.HSQLDB,
        initialSqls = "CREATE TABLE Customer(id INTEGER PRIMARY KEY, name VARCHAR(512)); "
                    + "INSERT INTO CUSTOMER(id, name) VALUES (1, 'John Doe')"
)
class EmbeddedDatabaseExtensionExtendWithTest {

    @Test
    void testUsingSpringJdbc(final @EmbeddedDatabase DataSource dataSource) {
        final JdbcOperations jdbcOperation = new JdbcTemplate(dataSource);
        final int id = 2;
        final String customerName = "Jane Doe";
    
        final int updatedRows = jdbcOperation.update("INSERT INTO CUSTOMER(id, name) VALUES(?,?)", id, customerName);
    
        assertEquals(1, updatedRows);
        assertEquals(customerName, jdbcOperation.queryForObject("SELECT name from CUSTOMER where id = ?", String.class, id));

    }
    
    void testUsingConnection(final @EmbeddedDatabase Connection connection) {
         try(final Statement statement = connection.createStatement();
             final ResultSet resultSet = statement.executeQuery("SELECT * from CUSTOMER")) {
                assertTrue(resultSet.next());
         }
    } 

}
```

##### Fluent style using builder and @RegisterExtension
```java
class EmbeddedDatabaseExtensionRegisterExtensionHSQLDBTest {

    @RegisterExtension
    static EmbeddedDatabaseExtension embeddedDatabaseExtension = EmbeddedDatabaseExtension.Builder.hsqldb()
                                            .withInitialSql("CREATE TABLE Customer(id INTEGER PRIMARY KEY, name VARCHAR(512)); "
                                                          + "INSERT INTO CUSTOMER(id, name) VALUES (1, 'John Doe')")
                                            .build();


    @Test
    void doDatabaseCall() throws SQLException {
        assertEquals("HSQL Database Engine", embeddedDatabaseExtension.getConnection().getMetaData().getDatabaseProductName());
    }

}
```

#### Junit 4.X
```java
@Rule
public final EmbeddedDatabaseRule dbRule = EmbeddedDatabaseRule
                                        .builder()
                                        .withMode(CompatibilityMode.Oracle)
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
public final EmbeddedDatabaseRule embeddedDatabaseRule = 
                                EmbeddedDatabaseRule.builder()
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

#### Use Liquibase changelog to populate the test database (v >= 0.6)
```java
@Rule
public final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule
        .builder()
        .withMode(CompatibilityMode.MSSQLServer)
        .initializedByPlugin(LiquibaseInitializer.builder()
                .withChangelogResource("example-changelog.sql")
                .build())
        .build();

@Test
public void testFindRolesInsertedByLiquibase() throws Exception {
    try(final Connection connection = embeddedDatabaseRule.getConnection()) {
        try(final PreparedStatement statement = connection.prepareStatement("Select * FROM ROLE r INNER JOIN USERROLE ur on r.ID = ur.ROLE_ID INNER JOIN USER u on ur.USER_ID = u.ID where u.NAME = ?")) {
            statement.setString(1, "Ada");
            try(final ResultSet resultSet = statement.executeQuery()) {
                final List<String> roles = new LinkedList<>();
                while(resultSet.next()) {
                    roles.add(resultSet.getString("name"));
                }
                assertEquals(2, roles.size());
            }
        }
    }

}
```
#### Use Flyway to populate the test database (v >= 1.0)
```java
@Rule
public final EmbeddedDatabaseRule embeddedDatabaseRule = 
                EmbeddedDatabaseRule.builder()
                                 .initializedByPlugin(
                                   new FlywayInitializer.Builder()
                                           .withLocations(
                                                   "classpath:migrations/")
                                           .build()).build();

@Test
public void checkMigrationsHasRun() throws Exception {
    try (final Connection connection = embeddedDatabaseRule.getConnection();
         final Statement statement = connection.createStatement();
         final ResultSet resultSet = statement.executeQuery("SELECT * FROM USER")) {
        assertTrue(resultSet.next());
    }
}
```

#### Multiple data sources in the same test class
If you need more than one database instance in your test class, you should name them using the "withName" construct.
If not set the rule builder will generate the name using the name of the test class
```java
@Rule
public final EmbeddedDatabaseRule embeddedDatabaseMysqlRule =
        EmbeddedDatabaseRule.builder().withName("db1").withMode(CompatibilityMode.MySQL).build();

@Rule
public final EmbeddedDatabaseRule embeddedDatabaseMsSqlServerRule =
        EmbeddedDatabaseRule.builder().withName("db2").withMode(CompatibilityMode.MSSQLServer).build();
```
## Changelog
Please consult the [wiki](//github.com/zapodot/embedded-db-junit/wiki/Changelog).

[![Analytics](https://ga-beacon.appspot.com/UA-58568779-1/embedded-db-junit/README.md)](https://github.com/igrigorik/ga-beacon)


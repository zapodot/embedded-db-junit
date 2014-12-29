embedded-db-junit
=================

[![Build Status](https://travis-ci.org/zapodot/embedded-db-junit.svg)](https://travis-ci.org/zapodot/embedded-db-junit)
[![Coverage Status](https://coveralls.io/repos/zapodot/embedded-db-junit/badge.png)](https://coveralls.io/r/zapodot/embedded-db-junit)

[JUnit](http://junit.org/) Rule that provides a [H2 Embedded in-memory database](http://www.h2database.com/)

## Status
This library is distributed through the [Sonatype OSS repo](https://oss.sonatype.org/) and should thus be widely available.
Java 7 or higher is required to use this plugin.

## Usage

### Add dependency
#### Maven
```xml
        <dependency>
            <groupId>org.zapodot</groupId>
            <artifactId>embedded-db-junit</artifactId>
            <version>0.1</version>
        </dependency>
```

#### SBT
```scala
    libraryDependencies += "org.zapodot" % "jackson-databind-java-optional" % "0.1" changing()
```

### Add to Junit test
```java
    @Rule
    public EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule
                                                                        .builder()
                                                                        .withMode("ORACLE")
                                                                        .withInitialSql("CREATE TABLE Customer(id INTEGER PRIMARY KEY, name VARCHAR(512)); "
                                                                                      + "INSERT INTO CUSTOMER(id, name) VALUES (1, 'John Doe')")
                                                                                      .build();

    @Test
    public void testUsingRxJdbc() throws Exception {
        assertNotNull(embeddedDatabaseRule.getConnection());
        final Database database = Database.from(embeddedDatabaseRule.getConnection());
        assertNotNull(database.select("SELECT sysdate from DUAL")
                      .getAs(Date.class)
                      .toBlocking()
                      .single());

        assertEquals("John Doe", database.select("select name from customer where id=1")
                                         .getAs(String.class)
                                         .toBlocking()
                                         .single());
    }
```

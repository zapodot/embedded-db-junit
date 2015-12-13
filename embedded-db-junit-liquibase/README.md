embedded-db-junit-liquibase
===========================

[Liquibase](http://www.liquibase.org/) initializer to be used with the [Embedded DB JUnit Rule](../README.md). 
When added as a plugin i runs the Liquibase update target after the
in-memory H2 database has been created. This allows you to write tests
for your real world database schema without affecting your real database

## Add to project

### Maven
```xml
<dependency>
    <groupId>org.zapodot</groupId>
    <artifactId>embedded-db-junit-liquibase</artifactId>
    <version>ADD_VERSION</version>
    <scope>test</scope>
</dependency>
```

## Add plugin to your the @Rule 
```java
@Rule
public EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule
        .builder()
        .withMode(EmbeddedDatabaseRule.CompatibilityMode.MSSQLServer)
        .initializedByPlugin(LiquibaseInitializer.builder()
                .withChangelogResource("example-changelog.sql")
                .withContexts("addUsersAndRoles")
                .build())
        .build();
```

The fluent builder API contains numerous methods that let you control various parameters
that are provided to Liquibase.

[![Analytics](https://ga-beacon.appspot.com/UA-58568779-1/embedded-db-junit/embedded-db-junit-liquibase/README.md)](https://github.com/igrigorik/ga-beacon)


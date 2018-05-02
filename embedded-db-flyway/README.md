embedded-db-flyway
=================
Plugin for the [embedded-db-junit](../README.md) that allows for [Flyway](//github.com/flyway/flyway) migrations to be run for the embedded database before the tests are executed.

## Usage instructions
### Add dependency
```xml
<dependency>
    <groupId>org.zapodot</groupId>
    <artifactId>embedded-db-flyway</artifactId>
    <version>...</version>
    <scope>test</scope>
</dependency>
```

### Set up as an initialization plugin
```java
@Rule
public final EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule.builder()
                                                                  .initializedByPlugin(
                                                                        new FlywayInitializer.Builder()
                                                                               .withLocations("classpath:migrations/")
                                                                               .build())
                                                                  .build();


@Test
public void checkMigrationsHasRun() throws Exception {
   try (final Connection connection = embeddedDatabaseRule.getConnection();
        final Statement statement = connection.createStatement();
        final ResultSet resultSet = statement.executeQuery("SELECT * FROM USER")) {
       
       assertTrue(resultSet.next());
    }
}

```

[![Analytics](https://ga-beacon.appspot.com/UA-58568779-1/embedded-db-junit/embedded-db-flyway/README.md)](https://github.com/igrigorik/ga-beacon)

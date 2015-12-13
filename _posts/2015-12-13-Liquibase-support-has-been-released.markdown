---
layout: post
title:  "Release 0.6, now with Liquibase support"
date:   2015-12-13 17:00:00 CET
categories: changelog release

---
The brand new 0.6 release of the [Embedded DB JUnit Rule](https://github.com/zapodot/embedded-db-junit) has been released with some notable new features:
* A new [initialization plugin API](#A_new_initialization_plugin_API)
* [Liquibase support](#Liquibase_support) by implementing the new initialization plugin

## A new initialization plugin API
You can now implement custom initialization code by implementing the new InitializationPlugin interface:

{% highlight java %}
public interface InitializationPlugin {

    void connectionMade(final String name, final Connection connection);
}
{% endhighlight %}

## Liquibase support
To initiate the embedded in-memory H2 database created by the @Rule, add the plugin to the @Rule initialization using the fluent Builder API:

{% highlight java %}
@Rule
public EmbeddedDatabaseRule embeddedDatabaseRule = EmbeddedDatabaseRule
        .builder()
        .withMode(EmbeddedDatabaseRule.CompatibilityMode.MSSQLServer)
        .initializedByPlugin(LiquibaseInitializer.builder()
                .withChangelogResource("example-changelog.sql")
                .build())
        .build();
{% endhighlight %}

## Links
* [How-to add the Liquibase plugin to your project](https://github.com/zapodot/embedded-db-junit/tree/master/embedded-db-junit-liquibase)
* Feel free to submit issues, pull requests or comments via the [GitHub project site](https://github.com/zapodot/embedded-db-junit)

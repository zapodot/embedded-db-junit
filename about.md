---
layout: page
title: About
permalink: /about/
---

The Embedded DB JUnit @Rule was created to simply testing database integration code.

It started off as I found my self repeat H2 initialization in all new projects with a JDBC back-end. As the wonderful [JUnit](//junit.org) framework contains TestRule interface that enables you to implement logic to be run before or after the test execution it was the obvious choice when implementing such a project.

To use it in your project, add the dependency to your build tool, and add a public field of type _EmbeddedDatabaseRule_ annotated with JUnit @Rule annotation. For detailed examples, check the documentation and JUnit tests at the [project GitHub site](https://github.com/zapodot/embedded-db-junit).  

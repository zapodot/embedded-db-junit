---
layout: page
title: About
permalink: /about/
---

The Embedded DB JUnit @Rule was created to simply testing database integration code.

It started off as I found my self repeating initialization of H2 in-memory databases in all new projects with a JDBC back-end. Then I found that the wonderful [JUnit](//junit.org) framework had this @Rule/TestRule constructs that makes it easy to create reusable code to run before or after test execution. Support for using the wonderful HyperSQL engine was added in version 1.1. 

The tool has proven to be quite valuable for my self in my day job as a software development consultant and testing evangelist working for various customers. It also seems to be used by various developers around the world, some of them with use cases I had never dreamt of using it for :-).

To use it in your project, add the dependency to your build tool, and add a public field of type _EmbeddedDatabaseRule_ annotated with JUnit @Rule annotation. For detailed examples, check the documentation and JUnit tests at the [project GitHub site](https://github.com/zapodot/embedded-db-junit).  

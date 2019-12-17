# RESTEasy

[![Build Status](https://travis-ci.org/resteasy/Resteasy.svg?branch=master)](https://travis-ci.org/resteasy/Resteasy)

RESTEasy is a JBoss.org project aimed at providing productivity frameworks for developing client and server RESTful applications and services in Java.  It is mainly a JAX-RS implementation but you'll find some other experimental code in the repository.

The project page can be found at [https://resteasy.jboss.org](https://resteasy.jboss.org).

## JAX-RS

RESTEasy is a JBoss project that provides various frameworks to help you build RESTful Web Services and RESTful Java applications. It is a portable implementation of the JAX-RS specification. The full name of JAX-RS is Jakarta RESTful Web Services that provides a Java API for RESTful Web Services over the HTTP protocol. Please note that the specification is now under the [Eclipse EE4J Project](https://github.com/eclipse-ee4j). You can read the entire specification at [Jakarta RESTful Web Services](https://github.com/eclipse-ee4j/jaxrs-api).

## Getting started with RESTEasy

- Read a [book](https://resteasy.jboss.org/books.html)
- Check out the [examples](https://github.com/resteasy/resteasy-examples) in the repository.
- Read the [documentation](https://resteasy.jboss.org/docs).
- The blog is [here](https://resteasy.jboss.org/blogs).

## Documentation

To read the documentation you can [read it online](https://resteasy.jboss.org/docs).

## Issues

Issues are kept in [JIRA](https://issues.redhat.com/projects/RESTEASY/issues).

## Build

After pulling down a clone of the RESTEasy repository run

```bash
$ mvn install
```

Currently it can be built using JDK 1.8 and 11.

If you don't have the JBoss Nexus repository configured in your own local Maven settings, you might need to add enable the `jboss-repository` profile:

```bash
mvn -Pjboss-repository install
```

## Contribute

You are most welcome to contribute to RESTEasy!

Read the [Contribution guidelines](./CONTRIBUTING.md)

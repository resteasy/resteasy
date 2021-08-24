# RESTEasy

[![Github CI](https://github.com/resteasy/resteasy/actions/workflows/maven.yml/badge.svg)](https://github.com/resteasy/resteasy/actions)

RESTEasy is a JBoss.org project aimed at providing productivity frameworks for developing client and server RESTful applications and services in Java.  It is mainly a JAX-RS implementation but you'll find some other experimental code in the repository.

The project page can be found at [https://resteasy.github.io](https://resteasy.github.io).

## JAX-RS

RESTEasy is a JBoss project that provides various frameworks to help you build RESTful Web Services and RESTful Java applications. It is a portable implementation of the JAX-RS specification. The full name of JAX-RS is Jakarta RESTful Web Services that provides a Java API for RESTful Web Services over the HTTP protocol. Please note that the specification is now under the [Eclipse EE4J Project](https://github.com/eclipse-ee4j). You can read the entire specification at [Jakarta RESTful Web Services](https://github.com/eclipse-ee4j/jaxrs-api).

## Getting started with RESTEasy

- Read a [book](https://resteasy.github.io/books)
- Check out the [examples](https://github.com/resteasy/resteasy-examples) in the repository.
- Read the [documentation](https://resteasy.github.io/docs).
- The blog is [here](https://resteasy.github.io//blogs).

## Documentation

To read the documentation you can [read it online](https://resteasy.github.io/docs).

## Issues

Issues are kept in [JIRA](https://issues.redhat.com/projects/RESTEASY/issues).

## Build

Currently RESTEasy can be built with JDK 11+.

If you want to purely build the project without running the tests, you need to pull down a clone of the RESTEasy repository and run:

```bash
$ mvn install -Dmaven.test.skip=true
```

If you want to build the project with testings run, you may need to specify a profile to use, and may need to configure the Wildfly version you want to run the tests with. Here is an example:

```bash
$ export SERVER_VERSION=17.0.0.Final
$ mvn -fae -Dserver.version=$SERVER_VERSION install
```

## Contribute

You are most welcome to contribute to RESTEasy!

Read the [Contribution guidelines](./CONTRIBUTING.md)

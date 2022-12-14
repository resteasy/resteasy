# RESTEasy

[![Github CI](https://github.com/resteasy/resteasy/actions/workflows/maven.yml/badge.svg)](https://github.com/resteasy/resteasy/actions)

RESTEasy is a JBoss.org project aimed at providing productivity frameworks for developing client and server RESTful
applications and services in Java.  It is mainly a Jakarta RESTful Web Services implementation but you'll find some 
other experimental code in the repository.

The project page can be found at [https://resteasy.dev](https://resteasy.dev).

## Jakarta RESTful Web Services

RESTEasy is a JBoss project that provides various frameworks to help you build RESTful Web Services and RESTful Java 
applications. It is a portable implementation of the Jakarta RESTful Web Services specification. The Jakarta RESTful Web 
Services provides a Java API for RESTful Web Services over the HTTP protocol. Please note that the specification is now 
under the [Jakarta EE Project](https://github.com/jakartaee). You can read the entire specification at 
[Jakarta RESTful Web Services](https://github.com/jakartaee/rest).

## Getting started with RESTEasy

- Read a [book](https://resteasy.dev/books)
- Check out the [examples](https://github.com/resteasy/resteasy-examples) in the repository.
- Read the [documentation](https://resteasy.dev/docs).
- View the [blogs](https://resteasy.dev/blogs).

## Documentation

To read the documentation you can [read it online](https://resteasy.dev/docs).

## Issues

Issues are kept in [JIRA](https://issues.redhat.com/browse/RESTEASY/).

## Build

Currently, RESTEasy requires JDK 11+.

If you want to build the project without running the tests, you need to pull down a clone of the RESTEasy repository and 
run:

```bash
$ ./mvnw clean install -DskipTests=true
```

If you want to build the project with testings run, you may need to specify a profile to use, and may need to configure 
the WildFly version you want to run the tests with. Here is an example:

```bash
$ SERVER_VERSION=27.0.0.Final ./mvnw clean -fae -Dserver.version=$SERVER_VERSION install
```

## Contribute

You are most welcome to contribute to RESTEasy!

Read the [Contribution guidelines](./CONTRIBUTING.adoc)

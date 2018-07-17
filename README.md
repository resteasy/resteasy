# RESTEasy

[![Build Status](https://travis-ci.org/resteasy/Resteasy.svg?branch=master)](https://travis-ci.org/resteasy/Resteasy)

RESTEasy is a JBoss.org project aimed at providing productivity frameworks for developing client and server RESTful applications and services in Java.  It is mainly a JAX-RS implementation but you'll find some other experimental code in the repository.

The project page can be found at http://resteasy.jboss.org

## JAX-RS
RESTEasy is a JBoss project that provides various frameworks to help you build RESTful Web Services and RESTful Java applications. It is a portable implementation of the JAX-RS specification. JAX-RS is a JCP specification that provides a Java API for RESTful Web Services over the HTTP protocol.

Read the entire specification [here](https://jax-rs-spec.java.net/)

The RESTEasy code is in the github repository, (https://github.com/resteasy/Resteasy.git).

## Getting started with RESTEasy
- Read a [book](http://resteasy.jboss.org/books.html)
- Check out the [examples](https://github.com/resteasy/resteasy-examples) in the repository.
- Read the [documentation](http://resteasy.jboss.org/docs).

## Documentation

To read the documentation you can [read it online](http://resteasy.jboss.org/docs). This is generated automatically from the [maven build](https://github.com/resteasy/Resteasy/tree/master/jaxrs/docbook)

A wiki exists with historical documents. You can find it [here](https://developer.jboss.org/wiki/ResteasyWIKI)

## Issues
Issues are kept in [JIRA](https://issues.jboss.org/browse/RESTEASY).

## Build
After pulling down a clone of the RESTEasy repository run

	mvn install

Currently it can be built using JDK 1.8 and 9.

### Requirements
- Java Cryptography Extension [JCE](http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html)

## Contribute
You are most welcome to contribute to RESTEasy!

Read the [Contribution guidelines](./CONTRIBUTING.md)

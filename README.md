# Resteasy
Resteasy is a JBoss.org project aimed at providing productivity frameworks for developing client and server RESTful applications and services in Java.  It is mainly a JAX-RS implementation but you'll find some other experimental code in the repository.

The project page can be found at http://resteasy.jboss.org

## JAX-RS
RESTEasy is a JBoss project that provides various frameworks to help you build RESTful Web Services and RESTful Java applications. It is a fully certified and portable implementation of the JAX-RS specification. JAX-RS is a new JCP specification that provides a Java API for RESTful Web Services over the HTTP protocol.

Read the entire specification [here](https://jax-rs-spec.java.net/)

The JAX-RS code is in the jaxrs directory.

## Getting started with RESTEasy
- Read a [book](http://resteasy.jboss.org/books.html)
- Check out the [examples](https://github.com/resteasy/Resteasy/tree/master/jaxrs/examples) in the repository.
- Read the [documentation](http://resteasy.jboss.org/docs.html).

## Documentation

To read the documentation you can [read it online](http://resteasy.jboss.org/docs.html). This is generated automatically from the [maven build](https://github.com/resteasy/Resteasy/tree/master/jaxrs/docbook)

A wiki exists. You can find it [here](http://wiki.jboss.org/wiki/RESTeasyJAXRS)

## Issues
Issues are kept both as a [JIRA](http://jira.jboss.org/jira/browse/RESTEASY) and on [Github](https://github.com/resteasy/Resteasy/issues)

## Build
After pulling down a clone of the Resteasy repository run

	mvn install

### Requirements
- Java Cryptography Extension [JCE](http://www.oracle.com/technetwork/java/javase/downloads/jce-7-download-432124.html)

## Contribute
You are most welcome to contribute to RESTEasy!

Read the [Contribution guidelines](./CONTRIBUTING.md)

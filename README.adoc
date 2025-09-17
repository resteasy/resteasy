= RESTEasy
:allow-uri-read:

image:https://github.com/resteasy/resteasy/actions/workflows/maven.yml/badge.svg[Github CI,link=https://github.com/resteasy/resteasy/actions/workflows/main.yml]

RESTEasy is a link:https://commonhaus.org[Commonhaus] project aimed at providing productivity frameworks
for developing client and server RESTful applications and services in Java. It is mainly a Jakarta RESTful Web Services
implementation, but you'll find some other experimental code in the repository.

The project page can be found at https://resteasy.dev.

== Jakarta RESTful Web Services

RESTEasy is a https://commonhaus.org[Commonhaus] project that provides various frameworks to help you build RESTful Web
Services and RESTful Java applications. It is a portable implementation of the Jakarta RESTful Web Services specification.
The Jakarta RESTful Web Services provides a Java API for RESTful Web Services over the HTTP protocol. Please note that
the specification is now under the https://github.com/jakartaee[Jakarta EE Project]. You can read the entire specification
at https://jakarta.ee/specifications/restful-ws/[Jakarta RESTful Web Services].

== Getting started with RESTEasy

* Read a https://resteasy.dev/books[book]
* Check out the https://github.com/resteasy/resteasy-examples[examples] in the repository.
* Read the https://resteasy.dev/docs[documentation].
* View the https://resteasy.dev/blogs[blogs].

== Documentation

To read the documentation you can https://resteasy.dev/docs[read it online].

== Issues

Issues are kept in https://issues.redhat.com/browse/RESTEASY/[JIRA].

== Build

Currently, RESTEasy requires JDK 17+.

If you want to build the project without running the tests, you need to pull down a clone of the RESTEasy repository and
run:

[source,bash]
----
$ ./mvnw clean install -DskipTests=true
----

If you want to build the project with testings run, you may need to specify a profile to use, and may need to configure
the WildFly version you want to run the tests with. Here is an example:

[source,bash]
----
$ SERVER_VERSION=27.0.0.Final ./mvnw clean -fae -Dserver.version=$SERVER_VERSION install
----

== Contribute

You are most welcome to contribute to RESTEasy!

Read the link:./CONTRIBUTING.adoc[Contribution guidelines]

== Releasing

Releasing this project requires access to https://repository.jboss.org/nexust[JBoss Nexus].

To perform a release ensure you're on the correct branch and that branch is up to date. Once done you can simply use
the `release.sh` bash script.

[source,bash]
----
./release.sh -r 7.0.0.Final -d 7.0.1.Final-SNAPSHOT
----

If this was successful, instructions will be printed on the console with the next set of instructions for processing.

[source,base]
----
...
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  14:17 min
[INFO] Finished at: 2025-09-18T16:03:43-07:00
[INFO] ------------------------------------------------------------------------

Your release has been successful. Check the validation after 16:13:43 on https://repository.jboss.org/nexus.
Once validation is successful, execute the following command:

git checkout v7.0.0.Final
mvn nxrm3:staging-move -Dmaven.repo.local="/tmp/m2/repository/resteasy"
git checkout test
git push upstream test
git push upstream v7.0.0.Final

gh release create --generate-notes --latest --verify-tag v7.0.0.Final
gh release upload v7.0.0.Final /home/jperkins/projects/resteasy/resteasy/distribution/target/distribution/resteasy-7.0.0.Final-all.zip /home/jperkins/projects/resteasy/resteasy/distribution/src-distribution/target/distribution/resteasy-7.0.0.Final-src.zip

You must also create a pull request to https://github.com/resteasy/resteasy.github.io with the documentation content.
----

IMPORTANT: If manually doing any of these steps it's important to enable the `release` profile with `-Prelease`.

=== Updating Documentation

The documentation repository can be found at https://github.com/resteasy/resteasy.github.io. When doing a release, there
must also be a pull request sent to this repository to update the documentation. The documentation can be found in the
`distribution/target/distribution/resteasy-*-all.zip` under the `docs` directory. Both the `javadocs` and `userguide`
subdirectories should be copied for the documentation.

Follow the instructions on
https://github.com/resteasy/resteasy.github.io/tree/main?tab=readme-ov-file#resteasy-documentation[the GitHub repository]
for further details.
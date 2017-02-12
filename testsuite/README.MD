# Introduction
The aim of testsuite module is to provide unit and integration tests for Resteasy.
All newly added tests go into this module. 
The integration tests run with specified Wildfly server.

# Testsuite structure
- arquillian-utils - contains helper classes used often in tests like - help methods for creating the deployment, 
categories for tests, server setup task etc.
- integration-tests - contains tests to run in container (Wildfly)
- integration-tests-spring - tests for resteasy integration with spring
- legacy-integration-tests - tests using legacy api to run in container (Wildfly) with resteasy-legacy module enabled
- unit-tests - standalone tests not running in container

# Run testsuite module

## All tests
Run default tests (excluding the ones expected to fail) with the default container (it is automatically downloaded):
> mvn clean verify

Run default tests (excluding the ones expected to fail) with path to the container specified:
> mvn clean verify -Dserver.home=PATH_TO_WIDLFLY_HOME

## All tests including the failing ones
By default all tests annotated with ``@Category({ ExpectedFailing.class })``
are excluded. 

To run the testsuite with failing tests included for testing purposes:
> mvn clean verify -Dserver.home=PATH_TO_WIDLFLY_HOME -Denable.expected.failing -fn

## Single test
> mvn clean verify -Dserver.home=PATH_TO_WIDLFLY_HOME -Dtest=NAME_OF_THE_TEST_CLASS -fn

## Custom resteasy version  
Resteasy version for unit-tests and for client side of integration tests can be specified with ``-Dversion.resteasy.testsuite=RESTEASY_VERSION``

> mvn clean verify -Dserver.home=PATH_TO_WIDLFLY_HOME -Dversion.resteasy.testsuite=RESTEASY_VERSION

## Legacy api enabled
Tests using non deprecated api, will run with resteasy-legacy module enabled. Sub-module legacy-integration-tests have
resteasy-legacy module enabled by default, no need enable this profile.

> mvn clean verify -Dserver.home=PATH_TO_WIDLFLY_HOME -Drun.server.with.resteasy.legacy.enabled -fn 

## IPv6 testing
Use ``ipv6`` property and specify IP address for EAP binding for IPv6 testing. Example:
 
> mvn clean verify -Dserver.home=PATH_TO_WIDLFLY_HOME -Dnode=2620:52:0:2804:56ee:75ff:fe1d:44c3 -Dipv6

## Test logs
Test logs are stored in ``/MODULE_NAME/target/surefire-reports/TEST_NAME-output.txt``

To redirect to standard output (console) use ``-Dmaven.test.redirectTestOutputToFile=false``.

> mvn clean verify -Dserver.home=PATH_TO_WIDLFLY_HOME -Dmaven.test.redirectTestOutputToFile=false

# Guidance for writing new tests
* Keep structure of the tests [example](integration-tests/src/test/java/org/jboss/resteasy/test/resource/param/CookieInjectionTest.java).
* Document test class and all tests within test class see [Test Documentation](#Test Documentation). It will help others to understand.
* Provide RESTEASY jira number if possible, it will help to track the issue in the future.
* For integration tests avoid using concrete address like "http://localhost:8080". Tests don't need to be run always on the default hostname/port.
See example above.
* If you keep in mind above points, you will help us to keep Resteasy testsuite organized and stable. 

# Test documentation
With each new test write appropriate javadoc documentation.

We keep test documentation for test cases (test classes) as follows:

```
/**
 * @tpChapter Main chapter name (see chapter structure below)
 * @tpSubChapter Sub-chapter name (see chapter structure below)
 * @tpTestCaseDetails Description of the test class, id of JIRA issue if applicable
 * @tpSince RESTEasy 3.x.x (in which version of resteasy was this test added)
 */
```

The test methods itself are annotated as follows:

```
/**
 * @tpTestDetails Description of what the test does
 * @tpSince RESTEasy 3.x.x (in which version of resteasy was this test added)
 */
```

## Test documentation - chapter structure

Following is the list of chapters and corresponding subchapters for javadoc test documentation:

- **Unit tests**
    - Crypto
    - Injection tests
    - Interception tests
    - Jose tests
    - Media type
    - Providers
    - Requests
    - Resource tests
    - Resteasy-client
    - Util tests

- **Client tests**
    - Configuration
    - Resteasy-client

- **Integration tests**
    - Asynchronous RESTEasy
    - Atom provider
    - CDI
    - Constructors
    - Core
    - Crypto
    - DataSource provider
    - Encoding
    - Exceptions
    - Form tests
    - IIOImage provider
    - Injection
    - Interceptors
    - Jackson2 provider
    - Jackson provider
    - Jaxb provider
    - Jettison provider
    - Jsapi
    - Json-p provider
    - Localization
    - Multipart provider
    - Parameters
    - Plain provider
    - Providers
    - Resources
    - Response
    - Security
    - Util tests
    - Validator provider
    - Writers
    - XXE
    - Yaml provider




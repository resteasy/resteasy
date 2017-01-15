# Introduction
The aim of testsuite module is to provide unit and integration tests for Resteasy.

# Run testsuite module

## All tests
Run default tests:
> mvn clean verify -Djboss.home=PATH_TO_WIDLFLY_HOME

By default all tests annotated with ``@Category({ ExpectedFailing.class })``
are excluded. 

To run the testsuite with failing tests included for testing purposes:

> mvn clean verify -Djboss.home=PATH_TO_WIDLFLY_HOME -Denable.expected.failing -fn

## Single test
> mvn clean verify -Djboss.home=PATH_TO_WIDLFLY_HOME -Dtest=NAME_OF_THE_TEST_CLASS -fn

For example:
> mvn clean verify -Djboss.home=PATH_TO_WIDLFLY_HOME -Dtest=TraceTest -fn

# Test documentation
With each new test write appropriate javadoc documentation.

We keep test documentation for test cases (test classes) as follows:

```
/**
 * @tpChapter Main chapter name (see chapter structure below)
 * @tpSubChapter Sub-chapter name (see chapter structure below)
 * @tpTestCaseDetails Description of the test class, id of JIRA issue if applicable
 * @tpSince RESTEasy 3.x.x (in which version of resteasy was this test added) [optional]
 */
```

The test methods itself are annotated as follows:

```
/**
 * @tpTestDetails Description of what the test does
 * @tpSince RESTEasy 3.x.x (in which version of resteasy was this test added) [optional]
 */
```

# Test documentation - chapter structure

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




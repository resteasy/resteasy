Tests are run in the maven phase "integration-test", but it is more convenient to use phase "verify", as in the future post-intergration-test phase can be used as well (eg. for cleanup after integration tests)
You have to select server where the tests will be run. The server is specified via activating profile.

Currently are supported following servers (profile is triggered by)
- Jetty 8 (-Djetty8)
- Jboss AS 7.1, EAP 6.0 (-Djboss710)
    requries to set environment variable JBOSS_HOME before running tests:
        export JBOSS_HOME=/tmp/jboss-eap-6.0
        mvn verify -fn -Djboss710
    does not work out of the box with EAP 6.0.1
        see https://bugzilla.redhat.com/show_bug.cgi?id=871413
- EAP 6.1.x, 6.2.x (-Djboss730 -Djboss.home=JBOSS_HOME)

To run tests on jetty 8 server append -Djetty8 to maven command

    mvn verify -Djetty8

To run single test using jetty8 server append artifact ID of module to test -Djetty8 and -pl :ARTIFACT_ID to maven command

    eg. mvn verify -Djetty8 -pl :RESTEASY-760

To run single test using Jboss / EAP server append -Djboss730 -Djboss.home=JBOSS_HOME to maven command

    eg. mvn verify -Djboss730 -Djboss.home=/home/development/JBEAP-6.2.0.GA/jboss-eap-6.2 -pl :RESTEASY-760

To run multiple tests at once append list of artifact ids of modules to test -pl :ARTIFACT_ID_1, ...., :ARTIFACT_ID_N to maven command

    eg. mvn verify -fn -Djboss730 -Djboss.home=/home/development/JBEAP-6.2.0.GA/jboss-eap-6.2 -pl :RESTEASY-736,:RESTEASY-752,:RESTEASY-760


Known failures
RESTEASY-736 fails for Jboss AS 7.1, EAP 6.0, 6.1.x, 6.2.x
RESTEASY-752 fails for Jboss AS 7.1, EAP 6.0
RESTEASY-760 fails for Jboss AS 7.1, EAP 6.0
RESTEASY-767 fails for Jboss AS 7.1, EAP 6.0

RESTEASY-802 fails for jetty 8

package org.jboss.resteasy.test.providers.custom;

import static org.jboss.resteasy.test.ContainerConstants.DEFAULT_CONTAINER_QUALIFIER;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Core
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.17
 * @tpTestCaseDetails Regression test for JBEAP-4719
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
@Tag("NotForBootableJar")
public class MissingProducerTest {
    private static final String ERR_MSG = "Warning was not logged";
    private static int initLogMsg1Count = parseLog1();
    private static int initLogMsg2Count = parseLog2();
    private static int initLogMsg3Count = parseLog3();

    private static int parseLog1() {
        return TestUtil.getWarningCount("RESTEASY002120: ClassNotFoundException: ", false, DEFAULT_CONTAINER_QUALIFIER);
    }

    private static int parseLog2() {
        return TestUtil.getWarningCount("Unable to load builtin provider org.jboss.resteasy.Missing from ", false,
                DEFAULT_CONTAINER_QUALIFIER);
    }

    private static int parseLog3() {
        return TestUtil.getWarningCount("classes/META-INF/services/jakarta.ws.rs.ext.Providers", false,
                DEFAULT_CONTAINER_QUALIFIER);
    }

    @SuppressWarnings(value = "unchecked")
    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(MissingProducerTest.class.getSimpleName());
        war.addAsResource(MissingProducerTest.class.getPackage(), "MissingProducer.Providers",
                "META-INF/services/jakarta.ws.rs.ext.Providers");
        return TestUtil.finishContainerPrepare(war, null, (Class<?>[]) null);
    }

    /**
     * @tpTestDetails Check logs for RESTEASY002120 warning message.
     * @tpSince RESTEasy 3.0.17
     */
    @Test
    public void testMissingProducer() {
        Assertions.assertEquals(1, parseLog1() - initLogMsg1Count, ERR_MSG);
        Assertions.assertEquals(1, parseLog2() - initLogMsg2Count, ERR_MSG);
        Assertions.assertEquals(1, parseLog3() - initLogMsg3Count, ERR_MSG);
    }
}

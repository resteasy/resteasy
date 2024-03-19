package org.jboss.resteasy.test.core.spi;

import static org.jboss.resteasy.test.ContainerConstants.DEFAULT_CONTAINER_QUALIFIER;

import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.logging.Logger;
import org.jboss.resteasy.test.core.spi.resource.ResourceClassProcessorErrorImplementation;
import org.jboss.resteasy.test.core.spi.resource.ResourceClassProcessorPureEndPoint;
import org.jboss.resteasy.utils.LogCounter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter ResourceClassProcessor SPI
 * @tpChapter Integration tests
 * @tpTestCaseDetails ResourceClassProcessor should print suitable error/exception, if some exception is thrown
 * @tpSince RESTEasy 3.6
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ResourceClassProcessorErrorTest {

    private static final String DEPLOYMENT_NAME = "deployment_name";

    protected static final Logger logger = Logger.getLogger(ResourceClassProcessorErrorTest.class.getName());

    @ArquillianResource
    private Deployer deployer;

    @Deployment(name = DEPLOYMENT_NAME, managed = false)
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ResourceClassProcessorErrorTest.class.getSimpleName());
        war.addClass(ResourceClassProcessorErrorTest.class);
        war.addClass(PortProviderUtil.class);

        return TestUtil.finishContainerPrepare(war, null,
                ResourceClassProcessorPureEndPoint.class,
                ResourceClassProcessorErrorImplementation.class);
    }

    /**
     * @tpTestDetails ResourceClassProcessor should print suitable error/exception, if some exception is thrown
     * @tpSince RESTEasy 3.6
     */
    @Test
    public void errorTest() {
        LogCounter errorLogCounter = new LogCounter(
                "java.lang.RuntimeException: Exception from ResourceClassProcessorErrorImplementation",
                false, DEFAULT_CONTAINER_QUALIFIER);
        try {
            deployer.deploy(DEPLOYMENT_NAME);
            Assertions.fail("Exception from ResourceClassProcessor was not thrown");
        } catch (Exception e) {
            Assertions.assertTrue(errorLogCounter.count() >= 1,
                    "Error message was not printed to server log");
            return;
        }
    }
}

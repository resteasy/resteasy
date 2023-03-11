package org.jboss.resteasy.test.core.spi;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.jboss.resteasy.test.ContainerConstants.DEFAULT_CONTAINER_QUALIFIER;

import org.hamcrest.MatcherAssert;
import org.jboss.arquillian.container.test.api.Deployer;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.logging.Logger;
import org.jboss.resteasy.test.core.spi.resource.ResourceClassProcessorErrorImplementation;
import org.jboss.resteasy.test.core.spi.resource.ResourceClassProcessorPureEndPoint;
import org.jboss.resteasy.utils.LogCounter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter ResourceClassProcessor SPI
 * @tpChapter Integration tests
 * @tpTestCaseDetails ResourceClassProcessor should print suitable error/exception, if some exception is thrown
 * @tpSince RESTEasy 3.6
 */
@RunWith(Arquillian.class)
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
            Assert.fail("Exception from ResourceClassProcessor was not thrown");
        } catch (Exception e) {
            MatcherAssert.assertThat("Error message was not printed to server log",
                    errorLogCounter.count(), greaterThanOrEqualTo(1));
            return;
        }
    }
}

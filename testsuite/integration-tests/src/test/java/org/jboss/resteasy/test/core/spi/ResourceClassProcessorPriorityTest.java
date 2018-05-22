package org.jboss.resteasy.test.core.spi;


import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.core.spi.resource.ResourceClassProcessorPriiorityAImplementation;
import org.jboss.resteasy.test.core.spi.resource.ResourceClassProcessorPriiorityBImplementation;
import org.jboss.resteasy.test.core.spi.resource.ResourceClassProcessorPriiorityCImplementation;
import org.jboss.resteasy.test.core.spi.resource.ResourceClassProcessorPureEndPoint;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

/**
 * @tpSubChapter ResourceClassProcessor SPI
 * @tpChapter Integration tests
 * @tpTestCaseDetails ResourceClassProcessor and Priority annotation test
 * @tpSince RESTEasy 3.6
 */
@RunWith(Arquillian.class)
public class ResourceClassProcessorPriorityTest {

    protected static final Logger logger = Logger.getLogger(ResourceClassProcessorPriorityTest.class.getName());

    private static List<String> visitedProcessors = new ArrayList<>();

    public static synchronized void addToVisitedProcessors(String item) {
        visitedProcessors.add(item);
    }

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(ResourceClassProcessorPriorityTest.class.getSimpleName());
        war.addClass(ResourceClassProcessorPriorityTest.class);
        war.addClass(PortProviderUtil.class);

        return TestUtil.finishContainerPrepare(war, null,
                ResourceClassProcessorPureEndPoint.class,
                ResourceClassProcessorPriiorityAImplementation.class,
                ResourceClassProcessorPriiorityBImplementation.class,
                ResourceClassProcessorPriiorityCImplementation.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ResourceClassProcessorPriorityTest.class.getSimpleName());
    }


    /**
     * @tpTestDetails Deployment uses three ResourceClassProcessors with Priority annotation,
     *                this priority annotation should be used by RESTEasy
     * @tpSince RESTEasy 3.6
     */
    @Test
    public void priorityTest() {
        // init client
        client = new ResteasyClientBuilder().build();

        // do request
        Response response = client.target(generateURL("/pure/pure")).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());

        // log visited processors
        int i = 0;
        for (String item : visitedProcessors) {
            logger.info(String.format("%d. %s", ++i, item));
        }

        // asserts
        Assert.assertThat(visitedProcessors.size(), greaterThanOrEqualTo(3));
        Assert.assertThat(visitedProcessors.get(0), is("A"));
        Assert.assertThat(visitedProcessors.get(1), is("C"));
        Assert.assertThat(visitedProcessors.get(2), is("B"));

        // close client
        client.close();
    }
}

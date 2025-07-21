package org.jboss.resteasy.test.core.spi;

import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.core.spi.resource.ResourceClassProcessorNotAppliedImplementation;
import org.jboss.resteasy.test.core.spi.resource.ResourceClassProcessorPureEndPoint;
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
 * @tpTestCaseDetails ResourceClassProcessor should not be used in some case
 * @tpSince RESTEasy 3.6
 */
@ExtendWith(ArquillianExtension.class)
public class ResourceClassProcessorNotAppliedTest {

    protected static final Logger logger = Logger.getLogger(ResourceClassProcessorNotAppliedTest.class.getName());

    private static List<String> visitedProcessors = new ArrayList<>();

    public static synchronized void addToVisitedProcessors(String item) {
        visitedProcessors.add(item);
    }

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ResourceClassProcessorNotAppliedTest.class.getSimpleName());
        war.addClass(ResourceClassProcessorNotAppliedTest.class);
        war.addClass(PortProviderUtil.class);
        war.addClass(ResourceClassProcessorNotAppliedImplementation.class);
        return TestUtil.finishContainerPrepare(war, null,
                ResourceClassProcessorPureEndPoint.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ResourceClassProcessorNotAppliedTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails ResourceClassProcessor implementation should not be used if web.xml doesn't contains provider name
     *                and resteasy.scan is not allowed
     * @tpSince RESTEasy 3.6
     */
    @Test
    public void notAppliedTest() {
        // init client
        client = (ResteasyClient) ClientBuilder.newClient();

        // do request
        Response response = client.target(generateURL("/pure/pure")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());

        // log visited processors
        int i = 0;
        for (String item : visitedProcessors) {
            logger.info(String.format("%d. %s", ++i, item));
        }

        // asserts
        Assertions.assertTrue(visitedProcessors.isEmpty(), "ResourceClassProcessor was used although it should not be used");

        // close client
        client.close();
    }
}

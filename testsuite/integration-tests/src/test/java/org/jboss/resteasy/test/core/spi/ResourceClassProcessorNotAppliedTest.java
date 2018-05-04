package org.jboss.resteasy.test.core.spi;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.core.spi.resource.ResourceClassProcessorNotAppliedImplementation;
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

import static org.hamcrest.Matchers.greaterThanOrEqualTo;

/**
 * @tpSubChapter ResourceClassProcessor SPI
 * @tpChapter Integration tests
 * @tpTestCaseDetails ResourceClassProcessor should not be used in some case
 * @tpSince RESTEasy 3.6
 */
@RunWith(Arquillian.class)
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
        Assert.assertThat("ResourceClassProcessor was used although it should not be used",
                visitedProcessors.size(), greaterThanOrEqualTo(0));

        // close client
        client.close();
    }
}

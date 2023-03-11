package org.jboss.resteasy.test.providers.custom;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.custom.resource.ResponseContainerResource;
import org.jboss.resteasy.test.providers.custom.resource.ResponseContainerResponseFilter;
import org.jboss.resteasy.test.providers.custom.resource.ResponseContainerSecondResponseFilter;
import org.jboss.resteasy.test.providers.custom.resource.ResponseContainerTemplateFilter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ResponseContainerFilterTest {

    protected static final Logger logger = Logger.getLogger(ResponseContainerFilterTest.class.getName());

    static Client client;

    @BeforeClass
    public static void setup() throws Exception {
        client = ClientBuilder.newClient();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ResponseContainerFilterTest.class.getSimpleName());
        war.addClasses(ResponseContainerTemplateFilter.class);
        return TestUtil.finishContainerPrepare(war, null, ResponseContainerResource.class,
                ResponseContainerResponseFilter.class, ResponseContainerSecondResponseFilter.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ResponseContainerFilterTest.class.getSimpleName());
    }

    @AfterClass
    public static void close() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Client sends POST request with it's custom header "OPERATION" specified in it. Server has registered
     *                two ContainerResponseFilters, which have common ancestor and different priority. The filter ResponseFilter
     *                with higher priority should be used here first, because the order of execution for Response filters is
     *                descending.
     * @tpPassCrit The ResponseFilter is used first for processing the response
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testHasEntity() {
        Response response = client.target(generateURL("/resource/hasentity")).request("*/*")
                .header("OPERATION", "hasentity").post(Entity.entity("entity", MediaType.WILDCARD_TYPE));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("The ResponseFilters were used in different order than expected", MediaType.TEXT_PLAIN_TYPE,
                response.getMediaType());
        logger.info(response.readEntity(String.class));
        response.close();

    }

}

package org.jboss.resteasy.test.providers.custom;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ResponseContainerFilterTest {

    protected static final Logger logger = Logger.getLogger(ResponseContainerFilterTest.class.getName());

    static Client client;

    @BeforeAll
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

    @AfterAll
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
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(MediaType.TEXT_PLAIN_TYPE,
                response.getMediaType(), "The ResponseFilters were used in different order than expected");
        logger.info(response.readEntity(String.class));
        response.close();

    }

}

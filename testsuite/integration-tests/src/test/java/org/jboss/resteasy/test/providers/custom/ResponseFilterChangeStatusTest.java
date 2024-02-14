package org.jboss.resteasy.test.providers.custom;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.custom.resource.ResponseFilterChangeStatusResource;
import org.jboss.resteasy.test.providers.custom.resource.ResponseFilterChangeStatusResponseFilter;
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
public class ResponseFilterChangeStatusTest {

    protected static final Logger logger = Logger.getLogger(ResponseFilterChangeStatusTest.class.getName());

    static Client client;

    @BeforeAll
    public static void setup() throws Exception {
        client = ClientBuilder.newClient();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ResponseFilterChangeStatusTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, ResponseFilterChangeStatusResource.class,
                ResponseFilterChangeStatusResponseFilter.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ResponseFilterChangeStatusTest.class.getSimpleName());
    }

    @AfterAll
    public static void close() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Client sends HEAD request. The response gets processed by custom ResponseFilter.
     * @tpPassCrit The response code status is changed to 201 (CREATED), the response doesn't contain any entity,
     *             because this was HEAD request and response has set up its MediaType
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDefaultHead() {
        ProcessingException thrown = Assertions.assertThrows(ProcessingException.class,
                () -> {
                    Response response = client.target(generateURL("/default_head")).request().head();
                    Assertions.assertEquals(HttpResponseCodes.SC_CREATED, response.getStatus());

                    response.readEntity(String.class);

                    logger.info(response.getMediaType());
                    Assertions.assertTrue(response.getMediaType().equals(MediaType.TEXT_PLAIN_TYPE),
                            "Response must heave set up all headers, as if GET request was called.");
                    response.close();
                });
        Assertions.assertTrue(thrown instanceof ProcessingException);
    }

    /**
     * @tpTestDetails Client sends POST request. The response gets processed by custom ResponseFilter.
     * @tpPassCrit The response code status is changed to 201 (CREATED)
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testChangeStatus() {
        Response response = client.target(generateURL("/empty")).request().post(null);
        Assertions.assertEquals(HttpResponseCodes.SC_CREATED, response.getStatus());
        response.close();
    }
}

package org.jboss.resteasy.test.providers.custom;

import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.custom.resource.ResponseFilterChangeStatusResource;
import org.jboss.resteasy.test.providers.custom.resource.ResponseFilterChangeStatusResponseFilter;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ResponseFilterChangeStatusTest {

    protected static final Logger logger = Logger.getLogger(ResponseFilterChangeStatusTest.class.getName());

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    static Client client;

    @BeforeClass
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

    @AfterClass
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
        Response response = client.target(generateURL("/default_head")).request().head();
        Assert.assertEquals(HttpResponseCodes.SC_CREATED, response.getStatus());

        thrown.expect(ProcessingException.class);
        response.readEntity(String.class);

        logger.info(response.getMediaType());
        Assert.assertTrue("Response must heave set up all headers, as if GET request was called.",
                response.getMediaType().isCompatible(MediaType.TEXT_PLAIN_TYPE));
        response.close();
    }

    /**
     * @tpTestDetails Client sends POST request. The response gets processed by custom ResponseFilter.
     * @tpPassCrit The response code status is changed to 201 (CREATED)
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testChangeStatus() {
        Response response = client.target(generateURL("/empty")).request().post(null);
        Assert.assertEquals(HttpResponseCodes.SC_CREATED, response.getStatus());
        response.close();
    }
}

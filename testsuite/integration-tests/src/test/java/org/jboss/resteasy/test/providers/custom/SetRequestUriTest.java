package org.jboss.resteasy.test.providers.custom;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.providers.custom.resource.SetRequestUriRequestFilter;
import org.jboss.resteasy.test.providers.custom.resource.SetRequestUriResource;
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
public class SetRequestUriTest {

    static Client client;

    @BeforeAll
    public static void setup() throws Exception {
        client = ClientBuilder.newClient();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(SetRequestUriTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, SetRequestUriResource.class, SetRequestUriRequestFilter.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, SetRequestUriTest.class.getSimpleName());
    }

    @AfterAll
    public static void close() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Client sends GET request with https protocol uri. The resource has injected UriInfo and returns
     *                response containing absolute path of the uri from the request.
     * @tpPassCrit The response code status is changed to 200 (SUCCESS) and the absolute uri matches the original request
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testSchemaChange() {
        String uri = generateURL("/base/resource/change");
        String httpsUri = uri.replace("http://", "https://");
        Response response = client.target(uri).request().header("X-Forwarded-Proto", "https").get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals(httpsUri, response.readEntity(String.class),
                "The original https uri doesn't match the entity in the response");
    }

    /**
     * @tpTestDetails Client sends GET request with https protocol uri. The resource has injected UriInfo and returns
     *                response containing absolute path of the uri from the request.
     * @tpPassCrit The response code status is changed to 200 (SUCCESS) and the absolute uri matches the original request
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testUriOverride() {
        Response response = client.target(generateURL("/base/resource/setrequesturi1")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("OK", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails Client sends GET request with non existing uri path. The request is catched by PreMatching
     *                RequestFilter which applies to all requests not matter if the resource exists on the server. The
     *                RequestFilter
     *                processes the request aborts processing of the request and sends response back to the client.
     * @tpPassCrit The response code status is 200 (SUCCESS) and the absolute uri is changed by RequestFilter
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testUriOverride2() {
        Response response = client.target(generateURL("/base/resource/setrequesturi2")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assertions.assertEquals("http://xx.yy:888/base/resource/sub", response.readEntity(String.class),
                "The original uri doesn't match the entity changed by RequestFilter");
    }

}

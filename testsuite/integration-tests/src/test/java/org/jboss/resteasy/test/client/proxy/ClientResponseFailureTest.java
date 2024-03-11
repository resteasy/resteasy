package org.jboss.resteasy.test.client.proxy;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.ClientBuilder;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.client.proxy.resource.ClientResponseFailureResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ClientResponseFailureTest {

    @Path("/test")
    public interface ClientResponseFailureResourceInterface {
        @GET
        @Produces("text/plain")
        String get();

        @GET
        @Path("error")
        @Produces("text/plain")
        String error();
    }

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ClientResponseFailureTest.class.getSimpleName());
        war.addClass(ClientResponseFailureTest.class);
        return TestUtil.finishContainerPrepare(war, null, ClientResponseFailureResource.class);
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newClient();
    }

    @AfterEach
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, ClientResponseFailureTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Client sends async GET requests thru client proxy. The NotFoundException should be thrown as response.
     * @tpPassCrit Exception NotFoundException is thrown
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testStreamStillOpen() throws Exception {

        final ClientResponseFailureResourceInterface proxy = client.target(generateURL(""))
                .proxy(ClientResponseFailureResourceInterface.class);
        boolean failed = true;
        try {
            proxy.error();
            failed = false;
        } catch (NotFoundException e) {
            Assertions.assertEquals(HttpResponseCodes.SC_NOT_FOUND, e.getResponse().getStatus());
            Assertions.assertEquals(e.getResponse().readEntity(String.class), "there was an error",
                    "There wasn't expected message");
            e.getResponse().close();
        }

        Assertions.assertTrue(failed, "The expected NotFoundException didn't happened");
    }
}

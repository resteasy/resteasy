package org.jboss.resteasy.test.client.proxy;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.client.proxy.resource.ClientResponseFailureResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.Assert;
import org.junit.runner.RunWith;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
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

    @Before
    public void init() {
         client = new ResteasyClientBuilder().build();
    }

    @After
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
            Assert.assertEquals(HttpResponseCodes.SC_NOT_FOUND, e.getResponse().getStatus());
            Assert.assertEquals("There wasn't expected message", e.getResponse().readEntity(String.class),
                    "there was an error");
            e.getResponse().close();
        }

        Assert.assertTrue("The expected NotFoundException didn't happened", failed);
    }
}

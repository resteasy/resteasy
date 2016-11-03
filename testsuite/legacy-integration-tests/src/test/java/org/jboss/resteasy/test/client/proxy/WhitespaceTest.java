package org.jboss.resteasy.test.client.proxy;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.client.proxy.resource.WhiteSpaceResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Client tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class WhitespaceTest {

    static ResteasyClient client;
    private static final String SPACES_REQUEST = "something something";

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(WhitespaceTest.class.getSimpleName());
        war.addClass(WhitespaceTest.class);
        return TestUtil.finishContainerPrepare(war, null, WhiteSpaceResource.class);
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
        return PortProviderUtil.generateURL(path, WhitespaceTest.class.getSimpleName());
    }

    @Path(value = "/sayhello")
    public interface HelloClient {

        @GET
        @Path("/en/{in}")
        @Produces("text/plain")
        String sayHi(@PathParam(value = "in") String in);
    }

    /**
     * @tpTestDetails Client sends GET requests thru client proxy. The string parameter passed in the request has white
     * space in it. The parameter is delivered with white space in the response as well.
     * @tpPassCrit The response entity contains white space as the original request
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEchoWithWhiteSpace() {
        HelloClient proxy = client.target(generateURL("")).proxy(HelloClient.class);
        Assert.assertEquals(SPACES_REQUEST, proxy.sayHi(SPACES_REQUEST));
    }
}

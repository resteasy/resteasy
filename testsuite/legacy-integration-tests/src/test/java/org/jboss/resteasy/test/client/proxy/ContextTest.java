package org.jboss.resteasy.test.client.proxy;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.client.proxy.resource.ContextTestResource;
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
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class ContextTest {

    @Path(value = "/test")
    public interface ResourceInterface {

        @GET
        @Produces("text/plain")
        String echo(@Context UriInfo info);
    }

    static ResteasyClient client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(ContextTest.class.getSimpleName());
        war.addClass(ContextTest.class);
        return TestUtil.finishContainerPrepare(war, null, ContextTestResource.class);
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
        return PortProviderUtil.generateURL(path, ContextTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Client sends async GET requests thru client proxy. UriInfo is injected as argument of the GET
     * method call.
     * @tpPassCrit UriInfo was injected into method call
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testContextInjectionProxy() {
        ResourceInterface proxy = client.target(generateURL("")).proxy(ResourceInterface.class);
        Assert.assertEquals("UriInfo was not injected", "content", proxy.echo(null));
    }
}

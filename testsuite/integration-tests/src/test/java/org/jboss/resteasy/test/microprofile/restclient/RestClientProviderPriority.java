package org.jboss.resteasy.test.microprofile.restclient;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.annotation.RegisterProviders;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Priority;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
@RunAsClient
public class RestClientProviderPriority
{
    @ArquillianResource
    URL url;

    @Deployment
    public static Archive<?> deploy()
    {
        WebArchive war = TestUtil.prepareArchive(RestClientProviderPriority.class.getSimpleName());
        war.addClass(HelloResource.class);
        war.addClass(HelloClient.class);
        war.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        war.addAsManifestResource(new StringAsset("Dependencies: org.eclipse.microprofile.restclient,org.jboss.resteasy.resteasy-rxjava2 services\n"), "MANIFEST.MF");
        return TestUtil.finishContainerPrepare(war, null);
    }

    private String generateURL(String path)
    {
        return PortProviderUtil.generateURL(path, RestClientProviderPriority.class.getSimpleName());
    }

    @Test
    public void helloUndefined() throws Exception {
        HelloClient helloClient =
            RestClientBuilder.newBuilder().baseUrl(new URL(generateURL(""))).build(HelloClient.class);

        assertEquals("Hello undefined", helloClient.hello(null));
    }

    @Test
    public void helloNaruto() throws Exception {
        HelloClient helloClient =
            RestClientBuilder.newBuilder().baseUrl(new URL(generateURL(""))).build(HelloClient.class);

        assertEquals("Hello Naruto", helloClient.hello("Naruto"));
    }

    @Test
    public void helloBar() throws Exception {
        HelloClient helloClient =
            RestClientBuilder.newBuilder().baseUrl(new URL(generateURL(""))).build(HelloClient.class);

        assertEquals("Hello Bar", helloClient.hello(null));
    }

    @Path("/")
    @Produces("text/plain")
    // Bar should execute first due to lower priority 1 vs Integer.MAX
    @RegisterProviders({@RegisterProvider(HelloFooProvider.class), @RegisterProvider(value = HelloBarProvider.class, priority = 1)})
    public interface HelloClient {
        @GET
        @Path("/hello")
        String hello(@QueryParam("who") String who);
    }

    @Path("/")
    public static class HelloResource {
        @GET
        @Path("/hello")
        public String hello(@QueryParam("who") String who) {
            if (who == null || who.isEmpty()) {
                who = "undefined";
            }

            return "Hello " + who;
        }
    }

    @Priority(value = Integer.MAX_VALUE)
    public static class HelloFooProvider implements ClientRequestFilter {
        @Override
        public void filter(final ClientRequestContext requestContext) throws IOException {
            if (requestContext.getUri().getQuery() == null) {
                requestContext.setUri(UriBuilder.fromUri(requestContext.getUri()).queryParam("who", "Foo").build());
            }
        }
    }

    public static class HelloBarProvider implements ClientRequestFilter {
        @Override
        public void filter(final ClientRequestContext requestContext) throws IOException {
            if (requestContext.getUri().getQuery() == null) {
                requestContext.setUri(UriBuilder.fromUri(requestContext.getUri()).queryParam("who", "Bar").build());
            }
        }
    }
}

package org.jboss.resteasy.test.client.proxy;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
@RunAsClient
public class ClientGetUriTest {

    @Path("http://example.com/base/uri")
    public interface UserClient {
        @GET
        @Path("name")
        @Produces("text/plain")
        Response getName(@QueryParam("test") String test);

        @POST
        @Path("submit")
        @Produces("text/plain")
        Response submitResult(@QueryParam("sub") String sub);
    }

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(ClientGetUriTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, ClientGetUriTest.class);
    }

    @Test
    public void testProxyResolvedURI() throws Exception {
        Client client = ClientBuilder.newClient();
        // path is an empty string as it is already declared in proxy interface
        ResteasyWebTarget target = (ResteasyWebTarget) client.target("");
        UserClient userClient = target.proxy(UserClient.class);
        ClientResponse res = (ClientResponse) userClient.getName("hello world");
        // unfortunately, no way to get the actual request uri:
        // the returned uri does not consider the proxy call of "userClient.getName()"
        Assert.assertEquals("http://example.com/base/uri/name?test=hello+world", res.getResolvedURI());

        res = (ClientResponse) userClient.submitResult("report");
        Assert.assertEquals("http://example.com/base/uri/submit?sub=report", res.getResolvedURI());
        client.close();
    }
}

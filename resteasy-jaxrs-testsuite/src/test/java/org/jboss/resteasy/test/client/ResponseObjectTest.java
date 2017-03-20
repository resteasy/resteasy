package org.jboss.resteasy.test.client;

import org.jboss.resteasy.annotations.Body;
import org.jboss.resteasy.annotations.LinkHeaderParam;
import org.jboss.resteasy.annotations.ResponseObject;
import org.jboss.resteasy.annotations.Status;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.test.EmbeddedContainer;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

import static org.jboss.resteasy.test.TestPortProvider.generateBaseUrl;

/**
 * @author <a href="mailto:mstefank@redhat.com">Martin Štefanko</a>
 */
public class ResponseObjectTest
{

    private static ResteasyClient client;
    private static ResponseObjectClient responseObjectClient;

    @Path("test")
    interface ResponseObjectClient
    {
        @GET
        BasicObject get();

        @GET
        @Path("link-header")
        HateoasObject performGetBasedOnHeader();
    }

    @ResponseObject
    public interface BasicObject
    {
        @Status
        int status();

        @Body
        String body();

        ClientResponse response();

        @HeaderParam("Content-Type")
        String contentType();
    }

    @ResponseObject
    public interface HateoasObject
    {
        @Status
        int status();

        @LinkHeaderParam(rel = "nextLink")
        URI nextLink();

        @GET
        @LinkHeaderParam(rel = "nextLink")
        String followNextLink();
    }

    @Path("test")
    public static class ResponseObjectResource
    {

        @GET
        @Produces("text/plain")
        public String get()
        {
            return "ABC";
        }

        @GET
        @Path("/link-header")
        public Response getWithHeader(@Context UriInfo uri)
        {
            URI subUri = uri.getAbsolutePathBuilder().path("next-link").build();
            org.jboss.resteasy.spi.Link link = new org.jboss.resteasy.spi.Link();
            link.setHref(subUri.toASCIIString());
            link.setRelationship("nextLink");
            return Response.noContent().header("Link", link.toString()).build();
        }

        @GET
        @Produces("text/plain")
        @Path("/link-header/next-link")
        public String getHeaderForward()
        {
            return "forwarded";
        }
    }

    @BeforeClass
    public static void before() throws Exception
    {
        final Dispatcher dispatcher = EmbeddedContainer.start().getDispatcher();
        client = new ResteasyClientBuilder().build();
        dispatcher.getRegistry().addPerRequestResource(ResponseObjectResource.class);
        responseObjectClient = ProxyBuilder.builder(ResponseObjectClient.class, client.target(generateBaseUrl())).build();
    }

    @AfterClass
    public static void after() throws Exception
    {
        client.close();
        EmbeddedContainer.stop();
    }

    @Test
    public void testSimple()
    {
        BasicObject obj = responseObjectClient.get();
        org.junit.Assert.assertEquals(HttpResponseCodes.SC_OK, obj.status());
        org.junit.Assert.assertEquals("The response object doesn't contain the expected string", "ABC", obj.body());
        org.junit.Assert.assertEquals("The response object doesn't contain the expected header",
                "text/plain;charset=UTF-8", obj.response().getHeaders().getFirst("Content-Type"));
        org.junit.Assert.assertEquals("The response object doesn't contain the expected header", "text/plain;charset=UTF-8", obj.contentType());
    }

    @Test
    public void testLinkFollow()
    {
        HateoasObject obj = responseObjectClient.performGetBasedOnHeader();
        org.junit.Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, obj.status());
        org.junit.Assert.assertTrue("The resource was not forwarded", obj.nextLink().getPath().endsWith("next-link"));
        org.junit.Assert.assertEquals("The resource was not forwarded", "forwarded", obj.followNextLink());

    }
}

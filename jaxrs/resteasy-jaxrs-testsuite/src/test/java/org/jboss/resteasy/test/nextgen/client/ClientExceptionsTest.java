package org.jboss.resteasy.test.nextgen.client;

import org.jboss.resteasy.test.BaseResourceTest;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import java.io.IOException;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/*
 * Test if client throws exceptions as described in JAXRS-2.0 specification:
 * "When a provider method throws an exception, the JAX-RS client runtime will map it to an instance of
 * ProcessingException if thrown while processing a request, and to a ResponseProcessingException
 * if thrown while processing a response."
 */
public class ClientExceptionsTest extends BaseResourceTest {

    @Path("/")
    public static class Resource {
        @POST
        @Path("post")
        public Response post(Data data) {
            System.out.println("HERE!: " + data);
            return Response.ok().entity(data).build();
        }

        @GET
        @Path("get")
        public String get() { return "OK"; }

        @Path("empty")
        @GET
        public Response getEmpty(@Context HttpHeaders headers) {
            return Response.ok().type(headers.getAcceptableMediaTypes().get(0)).header(HttpHeaders.CONTENT_LENGTH, 0).entity(new Data("test", "test")).build();
        }

        @GET
        @Path("error")
        public Response error() { throw new WebApplicationException(HttpResponseCodes.SC_FORBIDDEN); }
    }

    static Client client;

    @BeforeClass
    public static void setupClient()
    {
        addPerRequestResource(Resource.class);
        client = ClientBuilder.newClient();

    }

    @AfterClass
    public static void close()
    {
        client.close();
    }

    public static class Data
    {
        public String data;
        public String type;

        public Data(String data, String type)
        {
            this.data = data;
            this.type = type;
        }
    }

    /*
     * Send a request for entity which requires special MessageBodyReader which is not available.
     */
    @Test(expected = ProcessingException.class)
    public void noMessageBodyReaderExistsTest()
    {
        WebTarget base = client.target(generateURL("/") + "/empty");

        base.request().get(Data.class);
    }

    /*
     * Send a request and try to read Response for entity which requires special MessageBodyReader which is not available.
     */
    @Test(expected = ProcessingException.class)
    public void noMessageBodyReaderExistsReadEntityTest() {
        WebTarget base = client.target(generateURL("/") + "/empty");

        Response response = base.request().get();
        response.readEntity(Data.class);
    }

    public static class IOExceptionReaderInterceptor implements ReaderInterceptor
    {
        @Override
        public Object aroundReadFrom(final ReaderInterceptorContext context) throws IOException, WebApplicationException {
            throw new IOException("client io");
        }
    }

    /*
     * WebTarget registers ReaderInterceptor and sends entity to a server. Then tries to read the response, but gets
     * interrupted by exception happening in ReaderInterceptor.
     */
    @Test(expected = ProcessingException.class)
    public void interceptorThrowsExceptionTest()
    {
        WebTarget base = client.target(generateURL("/") + "/post");

        Response response = base.register(IOExceptionReaderInterceptor.class).request("text/plain").post(Entity.text("data"));
        response.readEntity(Data.class);
    }

    public static class CustomClientResponseFilter implements ClientResponseFilter
    {

        @Override
        public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
            throw new IOException("client io");
        }
    }

    /*
     * WebTarget registers ClientResponseFilter and sends request to a server. The processing of the response gets
     * interrupted in the ClientResponseFilter and processing ends with ResponseProcessingException.
     */
    @Test(expected = ResponseProcessingException.class)
    public void responseFilterThrowsExceptionTest()
    {
        WebTarget base = client.target(generateURL("/") + "/get");
        base.register(CustomClientResponseFilter.class).request("text/plain").get();

    }

    public static class CustomClientRequestFilter implements ClientRequestFilter
    {
        @Override
        public void filter(ClientRequestContext requestContext) throws IOException {
            throw new IOException("client io");
        }
    }

    /*
     * WebTarget registers ClientRequestFilter and sends request to a server. The processing of the request gets
     * interrupted in the ClientRequestFilter and processing ends with ProcessingException.
     */
    @Test(expected = ProcessingException.class)
    public void requestFilterThrowsException()
    {
        WebTarget base = client.target(generateURL("/") + "/get");
        base.register(CustomClientRequestFilter.class).request("text/plain").get();
    }
}

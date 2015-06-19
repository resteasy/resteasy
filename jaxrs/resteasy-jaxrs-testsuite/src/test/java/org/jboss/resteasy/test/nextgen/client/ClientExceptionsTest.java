package org.jboss.resteasy.test.nextgen.client;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/*
 * Test if client throws exceptions as described in JAXRS-2.0 specification:
 * "When a provider method throws an exception, the JAX-RS client runtime will map it to an instance of
 * ProcessingException if thrown while processing a request, and to a ResponseProcessingException
 * if thrown while processing a response."
 * "Note that the client runtime will only throw an instance of WebApplicationException (or any of its
 *  subclasses) as a result of a response from the server with status codes 3xx, 4xx or 5xx."
 */
public class ClientExceptionsTest extends BaseResourceTest {

    @Path("/")
    //@Produces("text/plain")
    //@Consumes("text/plain")
    public static class Resource {
        @POST
        @Path("post")
        public Data post(Data data) {
            System.out.println("HERE!: " + data);
            return data;
        }

        @GET
        @Path("get")
        public String nothing() { return "OK"; }
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

        @Override
        public String toString()
        {
            return "Data{" +
                    "data='" + data + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }

    /*
     * Send a request for entity which requires special MessageBodyReader which is not available.
     */
    @Test
    public void noMessageBodyReaderExistsTest()
    {
        Data data = new Data("test", "something");
        WebTarget base = client.target(generateURL("/") + "/post");
        try {
            Response response = base.request().post(Entity.entity("data", MediaType.WILDCARD_TYPE));
        } catch (ProcessingException e) {
            System.out.println(e.getCause().toString());
        }



    }

    //@Test
    public void noMessageBodyWriterExistsTest()
    {

    }
}

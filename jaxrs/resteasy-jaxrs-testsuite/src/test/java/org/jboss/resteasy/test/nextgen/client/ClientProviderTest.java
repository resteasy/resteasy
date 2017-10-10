package org.jboss.resteasy.test.nextgen.client;

import junit.framework.Assert;
import org.jboss.resteasy.plugins.interceptors.encoding.AcceptEncodingGZIPFilter;
import org.jboss.resteasy.plugins.interceptors.encoding.GZIPDecodingInterceptor;
import org.jboss.resteasy.plugins.interceptors.encoding.GZIPEncodingInterceptor;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import org.jboss.resteasy.test.BaseResourceTest;
import org.junit.Test;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

/**
 * Client registers it's own implementations as variants of built-in entity providers
 * The entity providers registered by client have to be used before built-in ones.
 * See spec 4.2.4:
 * "An implementation MUST support application-provided entity providers and MUST use those in preference to
 * its own pre-packaged providers when either could handle the same request."
 */
public class ClientProviderTest extends BaseResourceTest {

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

    public static class StringEntityProviderReader implements MessageBodyReader<String>
    {
        @Override
        public boolean isReadable(Class<?> type, Type genericType,
                                  Annotation[] annotations, MediaType mediaType) {
            return type == String.class;
        }

        @Override
        public String readFrom(Class<String> type,
                               Type genericType, Annotation[] annotations, MediaType mediaType,
                               MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
                throws IOException, WebApplicationException {
            String text = readFromStream(entityStream);
            entityStream.close();
            String result = "Application defined provider: " + text;
            return result;
        }
    }

    public static class StringEntityProviderWriter implements MessageBodyWriter<String>
    {
        @Override
        public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return type == String.class;
        }

        @Override
        public long getSize(String s, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return s.length();
        }

        @Override
        public void writeTo(String s, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
            entityStream.write(("Application defined provider: " + mediaType.toString() + httpHeaders.toString()).getBytes());
        }
    }

    @Path("/")
    @Produces("text/plain")
    @Consumes("text/plain")
    public static class Resource {
        @POST
        @Path("post")
        public String post(String value) {
            return value;
        }

        @GET
        @Path("get")
        public String nothing() { return "OK"; }
    }

    public static final//
    String readFromStream(InputStream stream) throws IOException {
        InputStreamReader isr = new InputStreamReader(stream);
        return readFromReader(isr);
    }

    public static final//
    String readFromReader(Reader reader) throws IOException {
        BufferedReader br = new BufferedReader(reader);
        String entity = br.readLine();
        br.close();
        return entity;
    }

    /*
     * Create WebTarget from client and register custom MessageBodyReader on it.
     * Verify application provided MessageBodyReader is used instead of built-in one.
     * Verify that following request is is processed by built-in MessageBodyReader again.
     */
    @Test
    public void applicationDefinedMessageBodyReaderTest()
    {
        WebTarget base = client.target(generateURL("/") + "/get");
        String result = base.register(StringEntityProviderReader.class).request().get(String.class);
        Assert.assertEquals("Application defined provider: OK", result);

        WebTarget base2 = client.target(generateURL("/") + "/get");
        result = base2.request().get(String.class);
        Assert.assertEquals("OK", result);
    }

    /*
     * Create WebTarget from client and register custom MessageBodyWriter on it.
     * Verify application provided MessageBodyWriter is used instead of built-in one.
     * Verify that following request is is processed by built-in MessageBodyWriter again.
     */
    @Test
    public void applicationDefinedMessageBodyWriterTest()
    {
        WebTarget base = client.target(generateURL("/") + "/post");
        String result = base.register(StringEntityProviderWriter.class).request().post(Entity.text("test"), String.class);
        Assert.assertEquals("Application defined provider: text/plain[Content-Type=text/plain]", result);

        WebTarget base2 = client.target(generateURL("/") + "/post");
        result = base2.request().post(Entity.text("test"), String.class);
        Assert.assertEquals("test", result);
    }



}

package org.jboss.resteasy.test.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.resteasy.annotations.cache.Cache;
import org.jboss.resteasy.annotations.cache.NoCache;
import org.jboss.resteasy.client.jaxrs.cache.BrowserCache;
import org.jboss.resteasy.client.jaxrs.cache.CacheInterceptor;
import org.jboss.resteasy.client.jaxrs.cache.LightweightBrowserCache;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocationBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class ClientCacheInterceptorTest {

    @XmlRootElement
    public static class Message {
        private String message;
        private long createdAt;

        public Message() {
            this.createdAt = Instant.now().toEpochMilli();
        }

        public String getMessage() {
            return this.message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public long getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(long createdAt) {
            this.createdAt = createdAt;
        }
    }

    @Path("echo")
    @Produces(value = { XML_WITH_CHARSET, JSON_WITH_CHARSET, TEXT_XML_WITH_CHARSET })
    public static class EchoResource {

        @GET
        @Cache(maxAge = 120)
        public Response echo(@QueryParam("msg") String msg) {
            Message message = new Message();
            message.setMessage(String.valueOf(msg));
            return Response.ok(message).build();
        }

        @GET
        @NoCache
        @Path("nocache")
        public Response echoNoCache(@QueryParam("msg") String msg) {
            Message message = new Message();
            message.setMessage(String.valueOf(msg));
            return Response.ok(message).build();
        }

    }

    private static Client client;
    public static final String XML_WITH_CHARSET = "application/xml;charset=UTF-8";
    public static final String JSON_WITH_CHARSET = "application/json;charset=UTF-8";
    public static final String XML_NO_CHARSET = "application/xml";
    public static final String JSON_NO_CHARSET = "application/json";
    public static final String TEXT_WILDCARD = "text/*";
    public static final String TEXT_XML_WITH_CHARSET = "text/xml;charset=UTF-8";
    public static final String TEXT_XML_NO_CHARSET = "text/xml";
    private static final String DEP = "ClientCacheInterceptorTest";

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(DEP);
        war.addClass(Message.class);
        war.addClass(EchoResource.class);
        return TestUtil.finishContainerPrepare(war, null, EchoResource.class);
    }

    @BeforeAll
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @AfterAll
    public static void cleanup() {
        client.close();
    }

    private static String generateURL() {
        return PortProviderUtil.generateBaseUrl(DEP);
    }

    @Test
    public void testCachedValueNoCache() throws Exception {
        BrowserCache cache = new LightweightBrowserCache();
        CacheInterceptor interceptor = new CacheInterceptor(cache);
        final String url = generateURL();
        ClientInvocationBuilder request = (ClientInvocationBuilder) client.target(url).register(interceptor).path("echo")
                .path("nocache").queryParam("msg", "Hello world").request();
        try (ClientResponse response = (ClientResponse) request.get()) {
            Assertions.assertEquals(Status.OK.getStatusCode(), response.getStatus());
            Assertions.assertNull(cache.getAny(request.getURI().toString()),
                    "Cache must not contain any data");
        }
    }

    @Test
    public void testCachedValueCharset() throws Exception {
        BrowserCache cache = new LightweightBrowserCache();
        CacheInterceptor interceptor = new CacheInterceptor(cache);
        final String url = generateURL();
        ClientInvocationBuilder request = (ClientInvocationBuilder) client.target(url).register(interceptor).path("echo")
                .queryParam("msg", "Hello world").request();
        try (ClientResponse responseA = (ClientResponse) request.accept(XML_NO_CHARSET).get();
                ClientResponse responseB = (ClientResponse) request.accept(XML_NO_CHARSET).get()) {
            Assertions.assertEquals(Status.OK.getStatusCode(), responseA.getStatus());
            Assertions.assertEquals(Status.OK.getStatusCode(), responseB.getStatus());
            Assertions.assertEquals(XML_WITH_CHARSET, responseA.getHeaderString("Content-Type"),
                    "Content type must be " + XML_WITH_CHARSET);
            Assertions.assertEquals(XML_WITH_CHARSET, responseB.getHeaderString("Content-Type"),
                    "Content type must be " + XML_WITH_CHARSET);
            // assert response body
            String responseAStr = responseA.readEntity(String.class);
            String responseBStr = responseB.readEntity(String.class);
            // if taken from the cache, the createAt epoch must be the same and thus string must be the same as well
            assertEquals(responseAStr, responseBStr, "Response entities must be the same");
            Assertions.assertNotNull(cache.getAny(request.getURI().toString()), "Cache must contain data");
            Assertions.assertNotNull(cache.get(request.getURI().toString(), MediaType.APPLICATION_XML_TYPE),
                    "Cache must contain data for the given accepted content type");
        }
    }

    @Test
    public void testCachedValueWithDifferentAccept() throws Exception {
        BrowserCache cache = new LightweightBrowserCache();
        CacheInterceptor interceptor = new CacheInterceptor(cache);
        final String url = generateURL();
        ClientInvocationBuilder requestA = (ClientInvocationBuilder) client.target(url).register(interceptor).path("echo")
                .queryParam("msg", "Hello world").request();
        ClientInvocationBuilder requestB = (ClientInvocationBuilder) client.target(url).register(interceptor).path("echo")
                .queryParam("msg", "Hello world").request();
        try (ClientResponse responseA = (ClientResponse) requestA.accept(JSON_NO_CHARSET).get();
                ClientResponse responseB = (ClientResponse) requestB.accept(XML_NO_CHARSET).get()) {
            Assertions.assertEquals(Status.OK.getStatusCode(), responseA.getStatus());
            Assertions.assertEquals(Status.OK.getStatusCode(), responseB.getStatus());
            Assertions.assertEquals(JSON_WITH_CHARSET, responseA.getHeaderString("Content-Type"),
                    "Content type must be " + JSON_WITH_CHARSET);
            Assertions.assertEquals(XML_WITH_CHARSET, responseB.getHeaderString("Content-Type"),
                    "Content type must be " + XML_WITH_CHARSET);
            Assertions.assertNotNull(cache.getAny(requestA.getURI().toString()), "Cache must contain data");
            // the response must be cached under both types
            Assertions.assertNotNull(cache.get(requestA.getURI().toString(), MediaType.APPLICATION_JSON_TYPE),
                    "Cache must contain data for the given accepted content type");
            Assertions.assertNotNull(cache.get(requestB.getURI().toString(), MediaType.APPLICATION_XML_TYPE),
                    "Cache must contain data for the given accepted content type");
        }
    }

    @Test
    public void testCachedValueWithWildCardAccept() throws Exception {
        BrowserCache cache = new LightweightBrowserCache();
        CacheInterceptor interceptor = new CacheInterceptor(cache);
        final String url = generateURL();
        ClientInvocationBuilder requestA = (ClientInvocationBuilder) client.target(url).register(interceptor).path("echo")
                .queryParam("msg", "Hello world").request();
        // this should produce text/xml since the resource produces text/xml
        try (ClientResponse responseA = (ClientResponse) requestA.accept(TEXT_WILDCARD).get()) {
            Assertions.assertEquals(Status.OK.getStatusCode(), responseA.getStatus());
            Assertions.assertEquals(TEXT_XML_WITH_CHARSET, responseA.getHeaderString("Content-Type"),
                    "Content type must be " + TEXT_XML_WITH_CHARSET);
            Assertions.assertNotNull(cache.getAny(requestA.getURI().toString()), "Cache must contain data");
            // the response must be cached under text/* instead of text/xml
            Assertions.assertNotNull(cache.get(requestA.getURI().toString(), MediaType.valueOf(TEXT_WILDCARD)),
                    "Cache must contain data for the given accepted content type");
            // since Accept is present, cache entry for media type text/xml (response content type) must not exist
            Assertions.assertNull(cache.get(requestA.getURI().toString(), MediaType.valueOf(TEXT_XML_NO_CHARSET)),
                    "Cache must contain data for the given accepted content type");
            Assertions.assertNull(cache.get(requestA.getURI().toString(), MediaType.valueOf(TEXT_XML_WITH_CHARSET)),
                    "Cache must contain data for the given accepted content type");
        }
    }

    @Test
    // Reproduces RESTEASY-2301
    public void testCachedValueWithMultipleAccept() throws Exception {
        BrowserCache cache = new LightweightBrowserCache();
        CacheInterceptor interceptor = new CacheInterceptor(cache);
        final String url = generateURL();
        ClientInvocationBuilder requestA = (ClientInvocationBuilder) client.target(url).register(interceptor).path("echo")
                .queryParam("msg", "Hello world").request();
        try (ClientResponse responseA = (ClientResponse) requestA.accept(JSON_NO_CHARSET, XML_NO_CHARSET + ";q=0.5").get()) {
            Assertions.assertEquals(Status.OK.getStatusCode(), responseA.getStatus());
            Assertions.assertEquals(JSON_WITH_CHARSET, responseA.getHeaderString("Content-Type"),
                    "Content type must be " + JSON_WITH_CHARSET);
            Assertions.assertNotNull(cache.getAny(requestA.getURI().toString()), "Cache must contain data");

            // the response must be cached as json
            Assertions.assertNotNull(cache.get(requestA.getURI().toString(), MediaType.APPLICATION_JSON_TYPE),
                    "Cache must contain data for the given accepted content type");

        }
    }

}

package org.jboss.resteasy.test.asyncio;

import static org.jboss.resteasy.test.TestPortProvider.generateURL;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.imageio.IIOImage;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMultipart;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.sse.InboundSseEvent;

import org.jboss.resteasy.annotations.providers.multipart.MultipartForm;
import org.jboss.resteasy.annotations.providers.multipart.XopWithMultipartRelated;
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartInput;
import org.jboss.resteasy.plugins.providers.multipart.MultipartRelatedInput;
import org.jboss.resteasy.plugins.providers.sse.SseEventInputImpl;
import org.jboss.resteasy.plugins.server.vertx.VertxContainer;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyDeployment;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class AsyncIOTest {

    static Client client;

    @BeforeClass
    public static void setup() throws Exception {
        ResteasyDeployment deployment = VertxContainer.start();
        deployment.getProviderFactory().register(BlockingWriter.class);
        deployment.getProviderFactory().register(BlockingThrowingWriter.class);
        deployment.getProviderFactory().register(AsyncWriter.class);
        deployment.getProviderFactory().register(AsyncThrowingWriter.class);
        deployment.getProviderFactory().register(InterceptorFeature.class);
        Registry registry = deployment.getRegistry();
        registry.addPerRequestResource(AsyncIOResource.class);
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void end() throws Exception {
        try {
            client.close();
        } catch (Exception e) {

        }
        VertxContainer.stop();
    }

    @Test
    public void testAsyncIo() throws Exception {
        WebTarget target = client.target(generateURL("/async-io/blocking-writer-on-io-thread"));
        String val = target.request().get(String.class);
        Assert.assertEquals("OK", val);

        target = client.target(generateURL("/async-io/async-writer-on-io-thread"));
        val = target.request().get(String.class);
        Assert.assertEquals("OK", val);

        target = client.target(generateURL("/async-io/slow-async-writer-on-io-thread"));
        val = target.request().get(String.class);
        Assert.assertEquals("OK", val);

        target = client.target(generateURL("/async-io/blocking-writer-on-worker-thread"));
        val = target.request().get(String.class);
        Assert.assertEquals("OK", val);

        target = client.target(generateURL("/async-io/async-writer-on-worker-thread"));
        val = target.request().get(String.class);
        Assert.assertEquals("OK", val);

        target = client.target(generateURL("/async-io/slow-async-writer-on-worker-thread"));
        val = target.request().get(String.class);
        Assert.assertEquals("OK", val);
    }

    @Test
    public void testThrowingWritersAndInterceptors() throws Exception {
        WebTarget target = client.target(generateURL("/async-io/throwing/blocking-writer"));
        String val = target.request().get(String.class);
        Assert.assertEquals("this is fine", val);

        target = client.target(generateURL("/async-io/throwing/blocking-interceptor"));
        val = target.request().get(String.class);
        Assert.assertEquals("this is fine", val);

        target = client.target(generateURL("/async-io/throwing/async-writer-1"));
        val = target.request().get(String.class);
        Assert.assertEquals("this is fine", val);

        target = client.target(generateURL("/async-io/throwing/async-writer-2"));
        val = target.request().get(String.class);
        Assert.assertEquals("this is fine", val);

        target = client.target(generateURL("/async-io/throwing/async-interceptor-1"));
        val = target.request().get(String.class);
        Assert.assertEquals("this is fine", val);

        target = client.target(generateURL("/async-io/throwing/async-interceptor-2"));
        val = target.request().get(String.class);
        Assert.assertEquals("this is fine", val);
    }

    @Test
    public void testWriters() {
        // vertx runs on the IO thread so we can't allow blocking interceptors
        WebTarget target = client.target(generateURL("/async-io/blocking/reject-blocking-interceptor"));
        try {
            target.request().get(String.class);
            Assert.fail();
        } catch (InternalServerErrorException x) {
            // good
        }

        testWriters("text", "OK");
        testWriters("bytes", "OK");
        testWriters("default-text", "K");
        testWriters("boolean", "true");
        testWriters("number", "42");
        testWriters("input-stream", "OK");
        // Does not handle async because I think the current blocking implementation is wrong
        //      testWriters("reader", "OK");
        testWriters("data-source", "OK");
        testWriters("source", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><foo/>");
        testWriters("document", "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><foo/>");
        testWriters("file", "OK");
        testWriters("file-range", "OK");
        testWriters("streaming-output", "OK");
        testWriters("iioimage", IIOImage.class, image -> {
            Assert.assertEquals(1, image.getRenderedImage().getHeight());
            Assert.assertEquals(1, image.getRenderedImage().getWidth());
        });
        testWriters("form-url-encoded", MultivaluedMap.class, map -> {
            Assert.assertEquals(1, map.size());
            Assert.assertEquals(Arrays.asList("bar"), map.get("foo"));
        });
        testWriters("jax-rs-form", Form.class, form -> {
            MultivaluedMap<String, String> map = form.asMap();
            Assert.assertEquals(1, map.size());
            Assert.assertEquals(Arrays.asList("bar"), map.get("foo"));
        });
        // atom provider
        testWriters("atom-feed", Feed.class, feed -> {
            Assert.assertEquals("fubar", feed.getLanguage());
        });

        // jaxb provider

        // This does not work
        // testWriters("jaxb-xml-see-also", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><foo/>");
        testWriters("jaxb-xml-root-element",
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><jaxbXmlRootElement><foo>bar</foo></jaxbXmlRootElement>");
        testWriters("jaxb-element",
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><jaxbXmlRootElement><foo>bar</foo></jaxbXmlRootElement>");
        testWriters("jaxb-xml-type",
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><jaxbXmlType><foo>bar</foo></jaxbXmlType>");
        testWriters("jaxb-collection",
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><collection><jaxbXmlRootElement><foo>bar</foo></jaxbXmlRootElement></collection>");
        testWriters("jaxb-map",
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><map><entry key=\"foo\"><jaxbXmlRootElement><foo>bar</foo></jaxbXmlRootElement></entry></map>");

        // multipart provider

        testWriters("multipart-output", MultipartInput.class, multipart -> {
            Assert.assertEquals(1, multipart.getParts().size());
            Assert.assertTrue(MediaType.TEXT_PLAIN_TYPE.isCompatible(multipart.getParts().get(0).getMediaType()));
            try {
                Assert.assertEquals("foo", multipart.getParts().get(0).getBody(String.class, String.class));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        testWriters("multipart-form-data-output", MultipartFormDataInput.class, multipart -> {
            Assert.assertEquals(1, multipart.getParts().size());
            Assert.assertTrue(MediaType.TEXT_PLAIN_TYPE.isCompatible(multipart.getParts().get(0).getMediaType()));
            try {
                Assert.assertEquals("bar", multipart.getParts().get(0).getBody(String.class, String.class));
                Assert.assertEquals("bar", multipart.getFormDataPart("foo", String.class, String.class));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        testWriters("multipart-related-output", MultipartRelatedInput.class, multipart -> {
            Assert.assertEquals(1, multipart.getParts().size());
            Assert.assertTrue(MediaType.TEXT_PLAIN_TYPE.isCompatible(multipart.getParts().get(0).getMediaType()));
            try {
                Assert.assertEquals("foo", multipart.getParts().get(0).getBody(String.class, String.class));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        testWriters("multipart-list", new GenericType<List<String>>() {
        }, multipart -> {
            Assert.assertEquals(1, multipart.size());
            Assert.assertEquals("bar", multipart.get(0));
        });
        testWriters("multipart-map", new GenericType<Map<String, String>>() {
        }, multipart -> {
            Assert.assertEquals(1, multipart.size());
            Assert.assertEquals("bar", multipart.get("foo"));
        });
        testWriters("multipart-form-annotation", AsyncIOResource.MyForm.class, multipart -> {
            Assert.assertEquals("bar", multipart.foo);
        }, new MultipartForm() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return MultipartForm.class;
            }
        });
        testWriters("multipart-mime", MimeMultipart.class, multipart -> {
            try {
                Assert.assertEquals(1, multipart.getCount());
                Assert.assertEquals("OK", multipart.getBodyPart(0).getContent());
            } catch (MessagingException | IOException e) {
                throw new RuntimeException(e);
            }
        });
        testWriters("multipart-xop-related", AsyncIOResource.XopRelatedForm.class, multipart -> {
            Assert.assertArrayEquals(AsyncIOResource.OK_BYTES, multipart.foo);
        }, new XopWithMultipartRelated() {
            @Override
            public Class<? extends Annotation> annotationType() {
                return XopWithMultipartRelated.class;
            }
        });

        // jsonp provider

        testWriters("jsonp-array", JsonArray.class, array -> {
            Assert.assertEquals(1, array.size());
            Assert.assertTrue(array.get(0) instanceof JsonString);
            Assert.assertEquals("foo", ((JsonString) array.get(0)).getString());
        });
        testWriters("jsonp-structure", JsonArray.class, array -> {
            Assert.assertEquals(1, array.size());
            Assert.assertTrue(array.get(0) instanceof JsonString);
            Assert.assertEquals("foo", ((JsonString) array.get(0)).getString());
        });
        testWriters("jsonp-object", JsonObject.class, object -> {
            Assert.assertEquals(1, object.size());
            Assert.assertTrue(object.get("foo") instanceof JsonString);
            Assert.assertEquals("bar", object.getString("foo"));
        });
        testWriters("jsonp-value", JsonString.class, val -> {
            Assert.assertEquals("foo", val.getString());
        });

        // SSE
        testWriters("sse", SseEventInputImpl.class, val -> {
            try {
                InboundSseEvent event = val.read();
                Assert.assertEquals("foo", event.getName());
                Assert.assertEquals("bar\ngee", event.readData());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        // Jackson
        testWriters("jackson", "{\"foo\":\"bar\"}");
    }

    private void testWriters(String uri, String value, Annotation... annotations) {
        testWriters(uri, String.class, string -> Assert.assertEquals(value, string), annotations);
    }

    private <T> void testWriters(String uri, Class<T> klass, Consumer<T> tester, Annotation... annotations) {
        testWriters(uri, new GenericType<>(klass), tester, annotations);
    }

    private <T> void testWriters(String uri, GenericType<T> klass, Consumer<T> tester, Annotation... annotations) {
        WebTarget target = client.target(generateURL("/async-io/async/" + uri));
        Response response = target.request().get();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals("async", response.getHeaderString("X-Writer"));
        tester.accept(response.readEntity(klass, annotations));

        target = client.target(generateURL("/async-io/blocking/" + uri));
        response = target.request().get();
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        Assert.assertEquals("blocking", response.getHeaderString("X-Writer"));
        tester.accept(response.readEntity(klass, annotations));
    }
}

package org.jboss.resteasy.test.request;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import jakarta.ws.rs.core.MediaType;

import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.mock.MockHttpRequest;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.test.request.resource.AcceptComplexResource;
import org.jboss.resteasy.test.request.resource.AcceptConsumeResource;
import org.jboss.resteasy.test.request.resource.AcceptMultipleResource;
import org.jboss.resteasy.test.request.resource.AcceptResource;
import org.jboss.resteasy.test.request.resource.AcceptXmlResource;
import org.jboss.resteasy.test.request.resource.AcceptXmlResourceSecond;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Requests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for accepting correct method.
 * @tpSince RESTEasy 3.0.16
 */
public class AcceptTest {
    private static final String METHOD_ERROR_MESSAGE = "Unexpected method received";

    private HttpRequest createRequest(String httpMethod, String path, MediaType contentType, List<MediaType> accepts) {
        MockHttpRequest request = null;
        try {
            request = MockHttpRequest.create(httpMethod, path).contentType(contentType);
            request.accept(accepts);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return request;
    }

    /**
     * @tpTestDetails Accepting GET method
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testAcceptGet() throws Exception {
        Registry registry = new ResourceMethodRegistry(ResteasyProviderFactory.getInstance());
        registry.addPerRequestResource(AcceptResource.class);

        MediaType contentType = new MediaType("text", "plain");

        {
            ArrayList<MediaType> accepts = new ArrayList<MediaType>();
            accepts.add(MediaType.valueOf("application/foo"));
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry
                    .getResourceInvoker(createRequest("GET", "/", contentType, accepts));
            Assertions.assertNotNull(method, METHOD_ERROR_MESSAGE);
            Assertions.assertEquals(AcceptResource.class.getMethod("doGetFoo"), method.getMethod(),
                    METHOD_ERROR_MESSAGE);
        }

        {
            ArrayList<MediaType> accepts = new ArrayList<MediaType>();
            accepts.add(MediaType.valueOf("application/foo;q=0.1"));
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry
                    .getResourceInvoker(createRequest("GET", "/", contentType, accepts));
            Assertions.assertNotNull(method, METHOD_ERROR_MESSAGE);
            Assertions.assertEquals(AcceptResource.class.getMethod("doGetFoo"), method.getMethod(),
                    METHOD_ERROR_MESSAGE);
        }

        {
            ArrayList<MediaType> accepts = new ArrayList<MediaType>();
            accepts.add(MediaType.valueOf("application/foo"));
            accepts.add(MediaType.valueOf("application/bar;q=0.4"));
            accepts.add(MediaType.valueOf("application/baz;q=0.2"));
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry
                    .getResourceInvoker(createRequest("GET", "/", contentType, accepts));
            Assertions.assertNotNull(method, METHOD_ERROR_MESSAGE);
            Assertions.assertEquals(AcceptResource.class.getMethod("doGetFoo"), method.getMethod(),
                    METHOD_ERROR_MESSAGE);
        }

        {
            ArrayList<MediaType> accepts = new ArrayList<MediaType>();
            accepts.add(MediaType.valueOf("application/foo;q=0.4"));
            accepts.add(MediaType.valueOf("application/bar"));
            accepts.add(MediaType.valueOf("application/baz;q=0.2"));
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry
                    .getResourceInvoker(createRequest("GET", "/", contentType, accepts));
            Assertions.assertNotNull(method, METHOD_ERROR_MESSAGE);
            Assertions.assertEquals(AcceptResource.class.getMethod("doGetBar"), method.getMethod(),
                    METHOD_ERROR_MESSAGE);
        }

        {
            ArrayList<MediaType> accepts = new ArrayList<MediaType>();
            accepts.add(MediaType.valueOf("application/foo;q=0.4"));
            accepts.add(MediaType.valueOf("application/bar;q=0.2"));
            accepts.add(MediaType.valueOf("application/baz"));
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry
                    .getResourceInvoker(createRequest("GET", "/", contentType, accepts));
            Assertions.assertNotNull(method, METHOD_ERROR_MESSAGE);
            Assertions.assertEquals(AcceptResource.class.getMethod("doGetBaz"), method.getMethod(),
                    METHOD_ERROR_MESSAGE);
        }
    }

    /**
     * @tpTestDetails Accepting PUT method, schema attribute is added to media type
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testConsume() throws Exception {
        Registry registry = new ResourceMethodRegistry(ResteasyProviderFactory.getInstance());
        registry.addPerRequestResource(AcceptXmlResource.class);

        MediaType contentType = MediaType.valueOf("application/xml;schema=bar");

        {
            ArrayList<MediaType> accepts = new ArrayList<MediaType>();
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry
                    .getResourceInvoker(createRequest("PUT", "/xml", contentType, accepts));
            Assertions.assertNotNull(method, METHOD_ERROR_MESSAGE);
            Assertions.assertEquals(AcceptXmlResource.class.getMethod("putBar", String.class),
                    method.getMethod(), METHOD_ERROR_MESSAGE);
        }
    }

    /**
     * @tpTestDetails Accepting updated PUT method, schema and q attributes are added to media type
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testConsume2() throws Exception {
        Registry registry = new ResourceMethodRegistry(ResteasyProviderFactory.getInstance());
        registry.addPerRequestResource(AcceptXmlResourceSecond.class);

        MediaType contentType = MediaType.valueOf("application/xml;schema=bar");

        {
            ArrayList<MediaType> accepts = new ArrayList<MediaType>();
            accepts.add(MediaType.valueOf("application/xml;schema=junk;q=1.0"));
            accepts.add(MediaType.valueOf("application/xml;schema=stuff;q=0.5"));
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry
                    .getResourceInvoker(createRequest("PUT", "/xml", contentType, accepts));
            Assertions.assertNotNull(method, METHOD_ERROR_MESSAGE);
            Assertions.assertEquals(AcceptXmlResourceSecond.class.getMethod("putBar", String.class),
                    method.getMethod(), METHOD_ERROR_MESSAGE);
        }
    }

    /**
     * @tpTestDetails Accepting PUT method, schema and q attributes are added to media type
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testConsume3() throws Exception {
        Registry registry = new ResourceMethodRegistry(ResteasyProviderFactory.getInstance());
        registry.addPerRequestResource(AcceptXmlResourceSecond.class);

        MediaType contentType = MediaType.valueOf("application/xml;schema=blah");

        {
            ArrayList<MediaType> accepts = new ArrayList<MediaType>();
            accepts.add(MediaType.valueOf("application/xml;schema=junk;q=1.0"));
            accepts.add(MediaType.valueOf("application/xml;schema=stuff;q=0.5"));
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry
                    .getResourceInvoker(createRequest("PUT", "/xml", contentType, accepts));
            Assertions.assertNotNull(method, METHOD_ERROR_MESSAGE);
            Assertions.assertEquals(AcceptXmlResourceSecond.class.getMethod("put", String.class),
                    method.getMethod(), METHOD_ERROR_MESSAGE);
        }
    }

    /**
     * @tpTestDetails Accepting wild card
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testAcceptGetWildCard() throws Exception {
        Registry registry = new ResourceMethodRegistry(ResteasyProviderFactory.getInstance());
        registry.addPerRequestResource(AcceptResource.class);

        MediaType contentType = new MediaType("text", "plain");

        {
            ArrayList<MediaType> accepts = new ArrayList<MediaType>();
            accepts.add(MediaType.valueOf("application/wildcard"));
            accepts.add(MediaType.valueOf("application/foo;q=0.6"));
            accepts.add(MediaType.valueOf("application/bar;q=0.4"));
            accepts.add(MediaType.valueOf("application/baz;q=0.2"));
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry
                    .getResourceInvoker(createRequest("GET", "/", contentType, accepts));
            Assertions.assertNotNull(method, METHOD_ERROR_MESSAGE);
            Assertions.assertEquals(AcceptResource.class.getMethod("doGetWildCard"), method.getMethod(),
                    METHOD_ERROR_MESSAGE);
        }
    }

    /**
     * @tpTestDetails Accepting multiple values
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testAcceptMultiple() throws Exception {
        Registry registry = new ResourceMethodRegistry(ResteasyProviderFactory.getInstance());
        registry.addPerRequestResource(AcceptMultipleResource.class);

        MediaType contentType = new MediaType("text", "plain");

        MediaType foo = MediaType.valueOf("application/foo");
        MediaType bar = MediaType.valueOf("application/bar");

        {
            ArrayList<MediaType> accepts = new ArrayList<MediaType>();
            accepts.add(foo);
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry
                    .getResourceInvoker(createRequest("GET", "/", contentType, accepts));
            Assertions.assertNotNull(method, METHOD_ERROR_MESSAGE);
            Assertions.assertEquals(AcceptMultipleResource.class.getMethod("get"), method.getMethod(),
                    METHOD_ERROR_MESSAGE);
        }
        {
            ArrayList<MediaType> accepts = new ArrayList<MediaType>();
            accepts.add(bar);
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry
                    .getResourceInvoker(createRequest("GET", "/", contentType, accepts));
            Assertions.assertNotNull(method, METHOD_ERROR_MESSAGE);
            Assertions.assertEquals(AcceptMultipleResource.class.getMethod("get"), method.getMethod(),
                    METHOD_ERROR_MESSAGE);
        }
        {
            ArrayList<MediaType> accepts = new ArrayList<MediaType>();
            accepts.add(MediaType.valueOf("*/*"));
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry
                    .getResourceInvoker(createRequest("GET", "/", contentType, accepts));
            Assertions.assertNotNull(method, METHOD_ERROR_MESSAGE);
            Assertions.assertEquals(AcceptMultipleResource.class.getMethod("get"), method.getMethod(),
                    METHOD_ERROR_MESSAGE);
        }
        {
            ArrayList<MediaType> accepts = new ArrayList<MediaType>();
            accepts.add(MediaType.valueOf("application/*"));
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry
                    .getResourceInvoker(createRequest("GET", "/", contentType, accepts));
            Assertions.assertNotNull(method, METHOD_ERROR_MESSAGE);
            Assertions.assertEquals(AcceptMultipleResource.class.getMethod("get"), method.getMethod(),
                    METHOD_ERROR_MESSAGE);
        }
    }

    /**
     * @tpTestDetails Test content type matching
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testContentTypeMatching() throws Exception {
        Registry registry = new ResourceMethodRegistry(ResteasyProviderFactory.getInstance());
        registry.addPerRequestResource(AcceptConsumeResource.class);

        ArrayList<MediaType> accepts = new ArrayList<MediaType>();

        {
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry
                    .getResourceInvoker(createRequest("GET", "/", MediaType.valueOf("text/plain"), accepts));
            Assertions.assertNotNull(method, METHOD_ERROR_MESSAGE);
            Assertions.assertEquals(AcceptConsumeResource.class.getMethod("doGetWildCard"),
                    method.getMethod(), METHOD_ERROR_MESSAGE);
        }
        {
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry
                    .getResourceInvoker(createRequest("GET", "/", MediaType.valueOf("application/foo"), accepts));
            Assertions.assertNotNull(method, METHOD_ERROR_MESSAGE);
            Assertions.assertEquals(AcceptConsumeResource.class.getMethod("doGetFoo"),
                    method.getMethod(), METHOD_ERROR_MESSAGE);
        }
        {
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry
                    .getResourceInvoker(createRequest("GET", "/", MediaType.valueOf("application/bar"), accepts));
            Assertions.assertNotNull(method, METHOD_ERROR_MESSAGE);
            Assertions.assertEquals(AcceptConsumeResource.class.getMethod("doGetBar"),
                    method.getMethod(), METHOD_ERROR_MESSAGE);
        }
        {
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry
                    .getResourceInvoker(createRequest("GET", "/", MediaType.valueOf("application/baz"), accepts));
            Assertions.assertNotNull(method, METHOD_ERROR_MESSAGE);
            Assertions.assertEquals(AcceptConsumeResource.class.getMethod("doGetBaz"),
                    method.getMethod(), METHOD_ERROR_MESSAGE);
        }
    }

    /**
     * @tpTestDetails Complex test
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testComplex() throws Exception {
        Registry registry = new ResourceMethodRegistry(ResteasyProviderFactory.getInstance());
        registry.addPerRequestResource(AcceptComplexResource.class);

        MediaType contentType = MediaType.TEXT_XML_TYPE;

        ArrayList<MediaType> accepts = new ArrayList<MediaType>();
        accepts.add(MediaType.WILDCARD_TYPE);
        accepts.add(MediaType.TEXT_HTML_TYPE);

        {
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry
                    .getResourceInvoker(createRequest("GET", "/", contentType, accepts));
            Assertions.assertNotNull(method, METHOD_ERROR_MESSAGE);
            Assertions.assertEquals(AcceptComplexResource.class.getMethod("method2"), method.getMethod(),
                    METHOD_ERROR_MESSAGE);
        }
    }
}

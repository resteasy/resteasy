package org.jboss.resteasy.test.request;

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
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.core.MediaType;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry.getResourceInvoker(createRequest("GET", "/", contentType, accepts));
            Assert.assertNotNull(METHOD_ERROR_MESSAGE, method);
            Assert.assertEquals(METHOD_ERROR_MESSAGE, AcceptResource.class.getMethod("doGetFoo"), method.getMethod());
        }

        {
            ArrayList<MediaType> accepts = new ArrayList<MediaType>();
            accepts.add(MediaType.valueOf("application/foo;q=0.1"));
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry.getResourceInvoker(createRequest("GET", "/", contentType, accepts));
            Assert.assertNotNull(METHOD_ERROR_MESSAGE, method);
            Assert.assertEquals(METHOD_ERROR_MESSAGE, AcceptResource.class.getMethod("doGetFoo"), method.getMethod());
        }

        {
            ArrayList<MediaType> accepts = new ArrayList<MediaType>();
            accepts.add(MediaType.valueOf("application/foo"));
            accepts.add(MediaType.valueOf("application/bar;q=0.4"));
            accepts.add(MediaType.valueOf("application/baz;q=0.2"));
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry.getResourceInvoker(createRequest("GET", "/", contentType, accepts));
            Assert.assertNotNull(METHOD_ERROR_MESSAGE, method);
            Assert.assertEquals(METHOD_ERROR_MESSAGE, AcceptResource.class.getMethod("doGetFoo"), method.getMethod());
        }

        {
            ArrayList<MediaType> accepts = new ArrayList<MediaType>();
            accepts.add(MediaType.valueOf("application/foo;q=0.4"));
            accepts.add(MediaType.valueOf("application/bar"));
            accepts.add(MediaType.valueOf("application/baz;q=0.2"));
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry.getResourceInvoker(createRequest("GET", "/", contentType, accepts));
            Assert.assertNotNull(METHOD_ERROR_MESSAGE, method);
            Assert.assertEquals(METHOD_ERROR_MESSAGE, AcceptResource.class.getMethod("doGetBar"), method.getMethod());
        }

        {
            ArrayList<MediaType> accepts = new ArrayList<MediaType>();
            accepts.add(MediaType.valueOf("application/foo;q=0.4"));
            accepts.add(MediaType.valueOf("application/bar;q=0.2"));
            accepts.add(MediaType.valueOf("application/baz"));
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry.getResourceInvoker(createRequest("GET", "/", contentType, accepts));
            Assert.assertNotNull(METHOD_ERROR_MESSAGE, method);
            Assert.assertEquals(METHOD_ERROR_MESSAGE, AcceptResource.class.getMethod("doGetBaz"), method.getMethod());
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
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry.getResourceInvoker(createRequest("PUT", "/xml", contentType, accepts));
            Assert.assertNotNull(METHOD_ERROR_MESSAGE, method);
            Assert.assertEquals(METHOD_ERROR_MESSAGE, AcceptXmlResource.class.getMethod("putBar", String.class), method.getMethod());
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
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry.getResourceInvoker(createRequest("PUT", "/xml", contentType, accepts));
            Assert.assertNotNull(METHOD_ERROR_MESSAGE, method);
            Assert.assertEquals(METHOD_ERROR_MESSAGE, AcceptXmlResourceSecond.class.getMethod("putBar", String.class), method.getMethod());
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
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry.getResourceInvoker(createRequest("PUT", "/xml", contentType, accepts));
            Assert.assertNotNull(METHOD_ERROR_MESSAGE, method);
            Assert.assertEquals(METHOD_ERROR_MESSAGE, AcceptXmlResourceSecond.class.getMethod("put", String.class), method.getMethod());
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
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry.getResourceInvoker(createRequest("GET", "/", contentType, accepts));
            Assert.assertNotNull(METHOD_ERROR_MESSAGE, method);
            Assert.assertEquals(METHOD_ERROR_MESSAGE, AcceptResource.class.getMethod("doGetWildCard"), method.getMethod());
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
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry.getResourceInvoker(createRequest("GET", "/", contentType, accepts));
            Assert.assertNotNull(METHOD_ERROR_MESSAGE, method);
            Assert.assertEquals(METHOD_ERROR_MESSAGE, AcceptMultipleResource.class.getMethod("get"), method.getMethod());
        }
        {
            ArrayList<MediaType> accepts = new ArrayList<MediaType>();
            accepts.add(bar);
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry.getResourceInvoker(createRequest("GET", "/", contentType, accepts));
            Assert.assertNotNull(METHOD_ERROR_MESSAGE, method);
            Assert.assertEquals(METHOD_ERROR_MESSAGE, AcceptMultipleResource.class.getMethod("get"), method.getMethod());
        }
        {
            ArrayList<MediaType> accepts = new ArrayList<MediaType>();
            accepts.add(MediaType.valueOf("*/*"));
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry.getResourceInvoker(createRequest("GET", "/", contentType, accepts));
            Assert.assertNotNull(METHOD_ERROR_MESSAGE, method);
            Assert.assertEquals(METHOD_ERROR_MESSAGE, AcceptMultipleResource.class.getMethod("get"), method.getMethod());
        }
        {
            ArrayList<MediaType> accepts = new ArrayList<MediaType>();
            accepts.add(MediaType.valueOf("application/*"));
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry.getResourceInvoker(createRequest("GET", "/", contentType, accepts));
            Assert.assertNotNull(METHOD_ERROR_MESSAGE, method);
            Assert.assertEquals(METHOD_ERROR_MESSAGE, AcceptMultipleResource.class.getMethod("get"), method.getMethod());
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
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry.getResourceInvoker(createRequest("GET", "/", MediaType.valueOf("text/plain"), accepts));
            Assert.assertNotNull(METHOD_ERROR_MESSAGE, method);
            Assert.assertEquals(METHOD_ERROR_MESSAGE, AcceptConsumeResource.class.getMethod("doGetWildCard"), method.getMethod());
        }
        {
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry.getResourceInvoker(createRequest("GET", "/", MediaType.valueOf("application/foo"), accepts));
            Assert.assertNotNull(METHOD_ERROR_MESSAGE, method);
            Assert.assertEquals(METHOD_ERROR_MESSAGE, AcceptConsumeResource.class.getMethod("doGetFoo"), method.getMethod());
        }
        {
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry.getResourceInvoker(createRequest("GET", "/", MediaType.valueOf("application/bar"), accepts));
            Assert.assertNotNull(METHOD_ERROR_MESSAGE, method);
            Assert.assertEquals(METHOD_ERROR_MESSAGE, AcceptConsumeResource.class.getMethod("doGetBar"), method.getMethod());
        }
        {
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry.getResourceInvoker(createRequest("GET", "/", MediaType.valueOf("application/baz"), accepts));
            Assert.assertNotNull(METHOD_ERROR_MESSAGE, method);
            Assert.assertEquals(METHOD_ERROR_MESSAGE, AcceptConsumeResource.class.getMethod("doGetBaz"), method.getMethod());
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
            ResourceMethodInvoker method = (ResourceMethodInvoker) registry.getResourceInvoker(createRequest("GET", "/", contentType, accepts));
            Assert.assertNotNull(METHOD_ERROR_MESSAGE, method);
            Assert.assertEquals(METHOD_ERROR_MESSAGE, AcceptComplexResource.class.getMethod("method2"), method.getMethod());
        }
    }
}

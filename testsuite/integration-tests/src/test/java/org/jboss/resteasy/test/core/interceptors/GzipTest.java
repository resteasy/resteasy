package org.jboss.resteasy.test.core.interceptors;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.category.NotForForwardCompatibility;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.plugins.interceptors.AcceptEncodingGZIPFilter;
import org.jboss.resteasy.plugins.interceptors.GZIPDecodingInterceptor;
import org.jboss.resteasy.plugins.interceptors.GZIPEncodingInterceptor;
import org.jboss.resteasy.test.core.interceptors.resource.GzipProxy;
import org.jboss.resteasy.test.core.interceptors.resource.GzipResource;
import org.jboss.resteasy.test.core.interceptors.resource.GzipIGZIP;
import org.jboss.resteasy.test.core.interceptors.resource.Pair;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.util.ReadFromStream;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Variant;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @tpSubChapter Interceptors
 * @tpChapter Integration tests
 * @tpTestCaseDetails Gzip compression tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class GzipTest {

    static ResteasyClient client;
    protected static final Logger logger = LogManager.getLogger(GzipTest.class.getName());

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(GzipTest.class.getSimpleName());
        war.addClasses(GzipIGZIP.class, Pair.class);
        // Activate gzip compression:
        war.addAsManifestResource("org/jboss/resteasy/test/client/javax.ws.rs.ext.Providers", "services/javax.ws.rs.ext.Providers");
        return TestUtil.finishContainerPrepare(war, null, GzipResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, GzipTest.class.getSimpleName());
    }

    @Before
    public void init() {
        client = new ResteasyClientBuilder()
                    .register(AcceptEncodingGZIPFilter.class)
                    .register(GZIPDecodingInterceptor.class)
                    .register(GZIPEncodingInterceptor.class)
                    .build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    /**
     * @tpTestDetails Check ByteArrayOutputStream of gzip data
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRawStreams() throws Exception {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        GZIPEncodingInterceptor.EndableGZIPOutputStream outputStream = new GZIPEncodingInterceptor.EndableGZIPOutputStream(byteStream);
        outputStream.write("hello world".getBytes());
        outputStream.finish();
        outputStream.close();

        byte[] bytes1 = byteStream.toByteArray();
        logger.info("Output stream length: " + bytes1.length);
        logger.info("Output stream value:" + new String(bytes1));
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes1);
        GZIPDecodingInterceptor.FinishableGZIPInputStream is = new GZIPDecodingInterceptor.FinishableGZIPInputStream(bis, false);
        byte[] bytes = ReadFromStream.readFromStream(1024, is);
        is.finish();
        String str = new String(bytes);
        Assert.assertEquals("Output stream has wrong content", "hello world", str);


    }

    /**
     * @tpTestDetails Check ProxyFactory
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testProxy() throws Exception {
        GzipIGZIP proxy = client.target(generateURL("")).proxy(GzipIGZIP.class);
        Assert.assertEquals("Proxy return wrong content", "HELLO WORLD", proxy.getText());
        Assert.assertEquals("Proxy return wrong content", "HELLO WORLD", proxy.getGzipText());

        // resteasy-651
        try {
            proxy.getGzipErrorText();
            Assert.fail("Proxy is unreachable");
        } catch (InternalServerErrorException failure) {
            Assert.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, failure.getResponse().getStatus());
            String txt = failure.getResponse().readEntity(String.class);
            Assert.assertEquals("Response contain wrong content", "Hello", txt);
        }
    }

    /**
     * @tpTestDetails Check length of content. Gzip data should have at least 11 bytes
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testContentLength() throws Exception {
        {
            Response response = client.target(generateURL("/text")).request().get();
            Assert.assertEquals("Response has wrong content", "HELLO WORLD", response.readEntity(String.class));
            String cl = response.getHeaderString("Content-Length");
            if (cl != null) {
                // make sure the content length is greater than 11 because this will be a gzipped encoding
                Assert.assertTrue("Content length should be greater than 11 because this will be a gzipped encoding", Integer.parseInt(cl) > 11);
            }
        }
        {
            Response response = client.target(generateURL("/bytes")).request().get();
            String cl = response.getHeaderString("Content-Length");
            if (cl != null) {
                // make sure the content length is greater than 11 because this will be a gzipped encoding
                int integerCl = Integer.parseInt(cl);
                logger.info("Content-Length: " + integerCl);
                Assert.assertTrue("Content length should be greater than 11 because this will be a gzipped encoding", integerCl > 11);
            }
            Assert.assertEquals("Response contains wrong content", "HELLO WORLD", response.readEntity(String.class));
        }
    }

    /**
     * @tpTestDetails Check wrong URL
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRequestError() throws Exception {
        Response response = client.target(generateURL("/error")).request().get();
        Assert.assertEquals(HttpResponseCodes.SC_METHOD_NOT_ALLOWED, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Check stream from PUT request
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPutStream() throws Exception {
        Response response = client.target(generateURL("/stream")).request().header("Content-Encoding", "gzip")
                .put(Entity.entity("hello world", "text/plain"));
        Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Check text from PUT request
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPutText() throws Exception {
        Response response = client.target(generateURL("/text")).request().header("Content-Encoding", "gzip")
                .put(Entity.entity("hello world", "text/plain"));
        Assert.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Check plain text response
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRequestPlain() throws Exception {
        Response response = client.target(generateURL("/text")).request().get();
        Assert.assertEquals("HELLO WORLD", response.readEntity(String.class));

    }

    /**
     * @tpTestDetails Check encoded text response
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRequestEncoded() throws Exception {
        Response response = client.target(generateURL("/encoded/text")).request().get();
        Assert.assertEquals("Response contains wrong content", "HELLO WORLD", response.readEntity(String.class));
    }

    /**
     * @tpTestDetails Test that it was zipped by running it through Apache HTTP Client which does not automatically unzip
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testWasZipped() throws Exception {
        CloseableHttpClient client = HttpClientBuilder.create().disableContentCompression().build();
        {
            HttpGet get = new HttpGet(generateURL("/encoded/text"));
            get.addHeader("Accept-Encoding", "gzip, deflate");
            HttpResponse response = client.execute(get);
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatusLine().getStatusCode());
            Assert.assertEquals("Wrong encoding format", "gzip", response.getFirstHeader("Content-Encoding").getValue());

            // test that it is actually zipped
            String entity = EntityUtils.toString(response.getEntity());
            logger.info("Entity: " + entity);
            Assert.assertNotSame("Wrong entity content", entity, "HELLO WORLD");
        }

        {
            HttpGet get = new HttpGet(generateURL("/text"));
            get.addHeader("Accept-Encoding", "gzip, deflate");
            HttpResponse response = client.execute(get);
            Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatusLine().getStatusCode());
            Assert.assertEquals("Wrong encoding format", "gzip", response.getFirstHeader("Content-Encoding").getValue());

            // test that it is actually zipped
            String entity = EntityUtils.toString(response.getEntity());
            Assert.assertNotSame("Wrong entity content", entity, "HELLO WORLD");
        }
    }

    /**
     * @tpTestDetails Test that if there is no accept-encoding: gzip header that result isn't encoded
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testWithoutAcceptEncoding() throws Exception {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpGet get = new HttpGet(generateURL("/encoded/text"));
        HttpResponse response = client.execute(get);
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatusLine().getStatusCode());
        Assert.assertNull(response.getFirstHeader("Content-Encoding"));

        // test that it is actually zipped
        String entity = EntityUtils.toString(response.getEntity());
        Assert.assertEquals("Response contains wrong content", entity, "HELLO WORLD");
    }

    /**
     * @tpTestDetails Send POST request with gzip encoded data using @GZIP annotation and client proxy framework
     * @tpInfo RESTEASY-1499
     * @tpSince RESTEasy 3.1.0
     */
    @Test
    public void testGzipPost() {
        GzipProxy gzipProxy = ProxyBuilder.builder(GzipProxy.class, client.target(generateURL(""))).build();
        Pair data = new Pair();
        data.setP1("first");
        data.setP2("second");

        Response response = gzipProxy.post(data);
        Assert.assertEquals("gzip", response.getHeaderString("Content-Encoding"));
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
    }
    
    /**
     * @tpTestDetails Test exceeding default maximum size
     * @tpInfo RESTEASY-1484
     * @tpSince RESTEasy 3.1.0.Final
     */
    @Test
    @Category({NotForForwardCompatibility.class})
    public void testMaxDefaultSizeSending() throws Exception {
        byte[] b = new byte[10000001];
        Variant variant = new Variant(MediaType.APPLICATION_OCTET_STREAM_TYPE, "", "gzip");
        Response response = client.target(generateURL("/big/send")).request().post(Entity.entity(b, variant));
        Assert.assertEquals(HttpResponseCodes.SC_REQUEST_ENTITY_TOO_LARGE, response.getStatus());
        String message = response.readEntity(String.class);
        Assert.assertTrue(message.contains("RESTEASY003357"));
        Assert.assertTrue(message.contains("10000000"));
    }
}

package org.jboss.resteasy.test.core.interceptors;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Variant;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.plugins.interceptors.AcceptEncodingGZIPFilter;
import org.jboss.resteasy.plugins.interceptors.GZIPDecodingInterceptor;
import org.jboss.resteasy.plugins.interceptors.GZIPEncodingInterceptor;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.core.interceptors.resource.GzipIGZIP;
import org.jboss.resteasy.test.core.interceptors.resource.GzipProxy;
import org.jboss.resteasy.test.core.interceptors.resource.GzipResource;
import org.jboss.resteasy.test.core.interceptors.resource.Pair;
import org.jboss.resteasy.util.ReadFromStream;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Interceptors
 * @tpChapter Integration tests
 * @tpTestCaseDetails Gzip compression tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class GzipTest {

    static ResteasyClient client;
    protected static final Logger logger = Logger.getLogger(GzipTest.class.getName());

    @Deployment
    public static Archive<?> deploySimpleResource() {
        WebArchive war = TestUtil.prepareArchive(GzipTest.class.getSimpleName());
        war.addClasses(GzipIGZIP.class, Pair.class);
        // Activate gzip compression:
        war.addAsManifestResource("org/jboss/resteasy/test/client/jakarta.ws.rs.ext.Providers",
                "services/jakarta.ws.rs.ext.Providers");
        return TestUtil.finishContainerPrepare(war, null, GzipResource.class);
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, GzipTest.class.getSimpleName());
    }

    @BeforeEach
    public void init() {
        client = (ResteasyClient) ClientBuilder.newBuilder()
                .register(AcceptEncodingGZIPFilter.class)
                .register(GZIPDecodingInterceptor.class)
                .register(GZIPEncodingInterceptor.class)
                .build();
    }

    @AfterEach
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
        GZIPEncodingInterceptor.EndableGZIPOutputStream outputStream = new GZIPEncodingInterceptor.EndableGZIPOutputStream(
                byteStream);
        outputStream.write("hello world".getBytes());
        outputStream.finish();
        outputStream.close();

        byte[] bytes1 = byteStream.toByteArray();
        logger.info("Output stream length: " + bytes1.length);
        logger.info("Output stream value:" + new String(bytes1));
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes1);
        GZIPDecodingInterceptor.FinishableGZIPInputStream is = new GZIPDecodingInterceptor.FinishableGZIPInputStream(bis,
                false);
        byte[] bytes = ReadFromStream.readFromStream(1024, is);
        is.finish();
        String str = new String(bytes);
        Assertions.assertEquals("hello world", str, "Output stream has wrong content");

    }

    /**
     * @tpTestDetails Check ProxyFactory
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testProxy() throws Exception {
        GzipIGZIP proxy = client.target(generateURL("")).proxy(GzipIGZIP.class);
        Assertions.assertEquals("HELLO WORLD", proxy.getText(), "Proxy return wrong content");
        Assertions.assertEquals("HELLO WORLD", proxy.getGzipText(), "Proxy return wrong content");

        // resteasy-651
        try {
            proxy.getGzipErrorText();
            Assertions.fail("Proxy is unreachable");
        } catch (InternalServerErrorException failure) {
            Assertions.assertEquals(HttpResponseCodes.SC_INTERNAL_SERVER_ERROR, failure.getResponse().getStatus());
            String txt = failure.getResponse().readEntity(String.class);
            Assertions.assertEquals("Hello", txt, "Response contain wrong content");
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
            Assertions.assertEquals("HELLO WORLD", response.readEntity(String.class),
                    "Response has wrong content");
            String cl = response.getHeaderString("Content-Length");
            if (cl != null) {
                // make sure the content length is greater than 11 because this will be a gzipped encoding
                Assertions.assertTrue(Integer.parseInt(cl) > 11,
                        "Content length should be greater than 11 because this will be a gzipped encoding");
            }
        }
        {
            Response response = client.target(generateURL("/bytes")).request().get();
            String cl = response.getHeaderString("Content-Length");
            if (cl != null) {
                // make sure the content length is greater than 11 because this will be a gzipped encoding
                int integerCl = Integer.parseInt(cl);
                logger.info("Content-Length: " + integerCl);
                Assertions.assertTrue(integerCl > 11,
                        "Content length should be greater than 11 because this will be a gzipped encoding");
            }
            Assertions.assertEquals("HELLO WORLD", response.readEntity(String.class),
                    "Response contains wrong content");
        }
    }

    /**
     * @tpTestDetails Check wrong URL
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRequestError() throws Exception {
        Response response = client.target(generateURL("/error")).request().get();
        Assertions.assertEquals(HttpResponseCodes.SC_METHOD_NOT_ALLOWED, response.getStatus());
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
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
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
        Assertions.assertEquals(HttpResponseCodes.SC_NO_CONTENT, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Check plain text response
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRequestPlain() throws Exception {
        Response response = client.target(generateURL("/text")).request().get();
        Assertions.assertEquals("HELLO WORLD", response.readEntity(String.class));

    }

    /**
     * @tpTestDetails Check encoded text response
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRequestEncoded() throws Exception {
        Response response = client.target(generateURL("/encoded/text")).request().get();
        Assertions.assertEquals("HELLO WORLD", response.readEntity(String.class),
                "Response contains wrong content");
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
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatusLine().getStatusCode());
            Assertions.assertEquals("gzip", response.getFirstHeader("Content-Encoding").getValue(),
                    "Wrong encoding format");

            // test that it is actually zipped
            String entity = EntityUtils.toString(response.getEntity());
            logger.info("Entity: " + entity);
            Assertions.assertNotSame(entity, "HELLO WORLD", "Wrong entity content");
        }

        {
            HttpGet get = new HttpGet(generateURL("/text"));
            get.addHeader("Accept-Encoding", "gzip, deflate");
            HttpResponse response = client.execute(get);
            Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatusLine().getStatusCode());
            Assertions.assertEquals("gzip", response.getFirstHeader("Content-Encoding").getValue(),
                    "Wrong encoding format");

            // test that it is actually zipped
            String entity = EntityUtils.toString(response.getEntity());
            Assertions.assertNotSame(entity, "HELLO WORLD", "Wrong entity content");
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
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatusLine().getStatusCode());
        Assertions.assertNull(response.getFirstHeader("Content-Encoding"));

        // test that it is actually zipped
        String entity = EntityUtils.toString(response.getEntity());
        Assertions.assertEquals(entity, "HELLO WORLD", "Response contains wrong content");
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
        Assertions.assertEquals("gzip", response.getHeaderString("Content-Encoding"));
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
    }

    /**
     * @tpTestDetails Test exceeding default maximum size
     * @tpInfo RESTEASY-1484
     * @tpSince RESTEasy 3.1.0.Final
     */
    @Test
    public void testMaxDefaultSizeSending() throws Exception {
        byte[] b = new byte[10000001];
        Variant variant = new Variant(MediaType.APPLICATION_OCTET_STREAM_TYPE, "", "gzip");
        Response response = client.target(generateURL("/big/send")).request().post(Entity.entity(b, variant));
        Assertions.assertEquals(HttpResponseCodes.SC_REQUEST_ENTITY_TOO_LARGE, response.getStatus());
        String message = response.readEntity(String.class);
        Assertions.assertTrue(message.contains("RESTEASY003357"));
        Assertions.assertTrue(message.contains("10000000"));
    }
}

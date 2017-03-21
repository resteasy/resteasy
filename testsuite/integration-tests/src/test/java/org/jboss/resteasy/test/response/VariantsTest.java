package org.jboss.resteasy.test.response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.plugins.delegates.LocaleDelegate;
import org.jboss.resteasy.test.response.resource.VariantComplexResource;
import org.jboss.resteasy.test.response.resource.VariantEncodingResource;
import org.jboss.resteasy.test.response.resource.VariantLanguageResource;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.MediaType;
import java.util.List;

import org.jboss.resteasy.utils.PortProviderUtil;

/**
 * @tpSubChapter Response
 * @tpChapter Integration tests
 * @tpTestCaseDetails Tests that correct variant headers are returned in the response
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class VariantsTest {

    protected static final Logger logger = LogManager.getLogger(VariantsTest.class.getName());

    static Client client;

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(VariantsTest.class.getSimpleName());
        war.addClass(VariantsTest.class);
        return TestUtil.finishContainerPrepare(war, null, VariantLanguageResource.class, VariantComplexResource.class, VariantEncodingResource.class);
    }

    @Before
    public void init() {
        client = ClientBuilder.newClient();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    private String generateURL(String path) {
        return PortProviderUtil.generateURL(path, VariantsTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Simple http GET conditional request
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void evaluatePreconditionsTagNullAndSimpleGetTest() {
        client = ClientBuilder.newClient();
        logger.info(generateURL("/preconditionsSimpleGet"));
        Response response = client.target(generateURL("/preconditionsSimpleGet")).request()
                .get();
        Assert.assertEquals(200, response.getStatus());
        response.close();
        client.close();
    }

    /**
     * @tpTestDetails PUT request is send, request selects best variant from the list od empty variants,
     * IllegalArgumentException is thrown
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void selectVariantPutRequestTest() {
        Response response = client.target(generateURL("/SelectVariantTestPut")).request()
                .put(null);
        Assert.assertEquals(200, response.getStatus());
        Assert.assertEquals("PASSED", response.readEntity(String.class));
        response.close();
    }

    /**
     * @tpTestDetails GET request is send, test that variant is selected and response contains proper headers
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void selectVariantResponseVaryTest() {
        Response response = client.target(generateURL("/SelectVariantTestResponse")).request()
                .accept("application/json")
                .acceptEncoding("*").get();
        Assert.assertEquals(200, response.getStatus());
        List<String> headers = response.getStringHeaders().get("Vary");
        Assert.assertEquals(1, headers.size());
        String vary = headers.get(0);
        logger.info(vary);
        Assert.assertTrue(vary.contains("Accept-Language"));
        Assert.assertTrue(vary.contains("Accept-Encoding"));
        Assert.assertTrue(vary.matches(".*Accept.*Accept.*Accept.*"));
        response.close();
    }

    /**
     * @tpTestDetails Tests that variant preferred by client request by Accept-Language is selected
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetLanguageEn() throws Exception {
        Response response = client.target(generateURL("/")).request().acceptLanguage("en").get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("en", response.readEntity(String.class));
        Assert.assertEquals("en", response.getLanguage().toString());
        response.close();
    }

    /**
     * @tpTestDetails Tests that given wildcard client request by Accept-Language header returns some concrete language
     * header in the response.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetLanguageWildcard() throws Exception {
        Response response = client.target(generateURL("/")).request().acceptLanguage("*").get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertNotNull(response.getLanguage());
        response.close();
    }

    /**
     * @tpTestDetails Tests that variant preferred by client request by Accept-Language is selected. Variant defined with
     * Locale("pt", "BR")
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetLanguageSubLocal() throws Exception {
        Response response = client.target(generateURL("/brazil")).request()
                .acceptLanguage("pt").get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertNotNull(response.getLanguage());
        response.close();
    }

    /**
     * @tpTestDetails Test that language variant which has 0 preference is not returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetLanguageZero() throws Exception {
        Response response = client.target(generateURL("/")).request()
                .acceptLanguage("*", "zh;q=0", "en;q=0", "fr;q=0").get();
        Assert.assertEquals(HttpResponseCodes.SC_NOT_ACCEPTABLE, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Simple Get for with Accept-Language "zh". Lanfuage "zh" in the response is returned
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetLanguageZh() throws Exception {
        Response response = client.target(generateURL("/")).request().acceptLanguage("zh").get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("zh", response.readEntity(String.class));
        Assert.assertEquals("zh", response.getLanguage().toString());
        response.close();
    }

    /**
     * @tpTestDetails Tests client request with multiple Accept-language preferences, check the most preferred language
     * is returned in the response
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetLanguageMultiple() throws Exception {
        Response response = client.target(generateURL("/")).request()
                .acceptLanguage("en;q=0.3", "zh;q=0.4", "fr").get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("fr", response.readEntity(String.class));
        Assert.assertEquals("fr", response.getLanguage().toString());
        response.close();
    }

    /**
     * @tpTestDetails Verifies that a more specific media type is preferred.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetComplexAcceptLanguageEn() throws Exception {
        Response response = client.target(generateURL("/complex")).request()
                .accept("text/xml", "application/xml", "application/xhtml+xml", "image/png", "text/html;q=0.9", "text/plain;q=0.8", "*/*;q=0.5")
                .acceptLanguage("en-us", "en;q=0.5").get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("GET", response.readEntity(String.class));
        Assert.assertEquals(MediaType.APPLICATION_XML_TYPE.withCharset("UTF-8"), response.getMediaType());
        Assert.assertEquals("en-us", new LocaleDelegate().toString(response.getLanguage()));
        response.close();
    }

    /**
     * @tpTestDetails Verifies that a more specific media type is preferred.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetComplexAcceptLanguageEnUs() throws Exception {
        Response response = client.target(generateURL("/complex")).request()
                .accept("text/xml", "application/xml", "application/xhtml+xml", "image/png", "text/html;q=0.9", "text/plain;q=0.8", "*/*;q=0.5")
                .acceptLanguage("en", "en-us").get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("GET", response.readEntity(String.class));
        Assert.assertEquals(MediaType.APPLICATION_XML_TYPE.withCharset("UTF-8"), response.getMediaType());
        Assert.assertEquals("en-us", new LocaleDelegate().toString(response.getLanguage()));
        response.close();
    }

    /**
     * @tpTestDetails Test that expected variants are selected from list of multiple weighted content and language type.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetComplexShuffleAcceptMedia() throws Exception {
        Response response = client.target(generateURL("/complex")).request()
                .accept("application/xml", "text/xml", "application/xhtml+xml", "image/png", "text/html;q=0.9", "text/plain;q=0.8", "*/*;q=0.5")
                .acceptLanguage("en-us", "en;q=0.5").get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("GET", response.readEntity(String.class));
        Assert.assertEquals(MediaType.APPLICATION_XML_TYPE.withCharset("UTF-8"), response.getMediaType());
        Assert.assertEquals("en-us", new LocaleDelegate().toString(response.getLanguage()));
        response.close();
    }

    /**
     * @tpTestDetails  Test that expected variants are selected from list of multiple weighted content and language type.
     * en-us with preference
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetComplexAcceptLanguageEnUsWithPreference() throws Exception {
        Response response = client.target(generateURL("/complex")).request()
                .accept("application/xml", "text/xml", "application/xhtml+xml", "image/png", "text/html;q=0.9", "text/plain;q=0.8", "*/*;q=0.5")
                .acceptLanguage("en", "en-us;q=0.5").get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        Assert.assertEquals("GET", response.readEntity(String.class));
        Assert.assertEquals("en", response.getLanguage().toString());
        Assert.assertEquals(MediaType.TEXT_XML_TYPE.withCharset("UTF-8"), response.getMediaType());
        response.close();
    }

    /**
     * @tpTestDetails Tests client request which has accept header which cannot be served by server.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetComplexNotAcceptable() throws Exception {
        {
            Response response = client.target(generateURL("/complex")).request()
                    .accept("application/atom+xml")
                    .acceptLanguage("en-us", "en").get();
            Assert.assertEquals(406, response.getStatus());

            String vary = response.getHeaderString(HttpHeaderNames.VARY);
            Assert.assertNotNull(vary);
            logger.info("vary: " + vary);
            Assert.assertTrue(contains(vary, "Accept"));
            Assert.assertTrue(contains(vary, "Accept-Language"));
            response.close();
        }

        {
            Response response = client.target(generateURL("/complex")).request()
                    .accept("application/xml")
                    .acceptLanguage("fr").get();
            Assert.assertEquals(406, response.getStatus());

            String vary = response.getHeaderString(HttpHeaderNames.VARY);
            Assert.assertNotNull(vary);
            logger.info("vary: " + vary);
            Assert.assertTrue(contains(vary, "Accept"));
            Assert.assertTrue(contains(vary, "Accept-Language"));
            response.close();
        }
    }

    /**
     * @tpTestDetails Tests client request with custom Accept-encoding
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetEncodingCustomEnc1() throws Exception {
        Response response = client.target(generateURL("/encoding")).request()
                .acceptEncoding("enc1").get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());

        Assert.assertEquals("enc1", response.readEntity(String.class));
        Assert.assertEquals("enc1", response.getHeaderString(HttpHeaderNames.CONTENT_ENCODING));
        response.close();
    }

    /**
     * @tpTestDetails Tests client request with custom Accept-encoding
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetEncodingCustomEnc2() throws Exception {
        Response response = client.target(generateURL("/encoding")).request()
                .acceptEncoding("enc2").get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());

        Assert.assertEquals("enc2", response.readEntity(String.class));
        Assert.assertEquals("enc2", response.getHeaderString(HttpHeaderNames.CONTENT_ENCODING));
        response.close();
    }

    /**
     * @tpTestDetails Tests client request with custom Accept-encoding
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetEncodingCustomEnc3() throws Exception {
        Response response = client.target(generateURL("/encoding")).request()
                .acceptEncoding("enc3").get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());

        Assert.assertEquals("enc3", response.readEntity(String.class));
        Assert.assertEquals("enc3", response.getHeaderString(HttpHeaderNames.CONTENT_ENCODING));
        response.close();
    }

    /**
     * @tpTestDetails Tests client request with custom Accept-encoding with preference specified
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetEncodingCustomPreference() throws Exception {
        Response response = client.target(generateURL("/encoding")).request()
                .acceptEncoding("enc1;q=0.5", "enc2;q=0.9").get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());

        Assert.assertEquals("enc2", response.readEntity(String.class));
        Assert.assertEquals("enc2", response.getHeaderString(HttpHeaderNames.CONTENT_ENCODING));
        response.close();
    }

    /**
     * @tpTestDetails Tests client request with custom Accept-encoding with preference specified
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testGetEncodingCustomPreferenceZero() throws Exception {
        Response response = client.target(generateURL("/encoding")).request()
                .acceptEncoding("enc1;q=0", "enc2;q=0.888", "enc3;q=0.889").get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());

        Assert.assertEquals("enc3", response.readEntity(String.class));
        Assert.assertEquals("enc3", response.getHeaderString(HttpHeaderNames.CONTENT_ENCODING));
        response.close();
    }

    private boolean contains(String all, String one) {
        String[] allSplit = all.split(",");
        for (String s : allSplit) {
            s = s.trim();
            if (s.equalsIgnoreCase(one)) {
                return true;
            }
        }

        return false;
    }
}

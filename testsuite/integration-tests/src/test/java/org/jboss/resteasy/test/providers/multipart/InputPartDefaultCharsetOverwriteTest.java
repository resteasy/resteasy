package org.jboss.resteasy.test.providers.multipart;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.providers.multipart.resource.InputPartDefaultCharsetOverwriteContentTypeCharsetUTF16;
import org.jboss.resteasy.test.providers.multipart.resource.InputPartDefaultCharsetOverwriteContentTypeCharsetUTF8;
import org.jboss.resteasy.test.providers.multipart.resource.InputPartDefaultCharsetOverwriteContentTypeNoCharsetUTF16;
import org.jboss.resteasy.test.providers.multipart.resource.InputPartDefaultCharsetOverwriteContentTypeNoCharsetUTF8;
import org.jboss.resteasy.test.providers.multipart.resource.InputPartDefaultCharsetOverwriteNoContentTypeCharsetUTF16;
import org.jboss.resteasy.test.providers.multipart.resource.InputPartDefaultCharsetOverwriteNoContentTypeCharsetUTF8;
import org.jboss.resteasy.test.providers.multipart.resource.InputPartDefaultCharsetOverwriteService;
import org.jboss.resteasy.util.Encode;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * @tpSubChapter Multipart provider
 * @tpChapter Integration tests
 * @tpTestCaseDetails Regression test for RESTEASY-723
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class InputPartDefaultCharsetOverwriteTest {
    private static final org.jboss.logging.Logger logger = org.jboss.logging.Logger.getLogger(InputPartDefaultCharsetOverwriteTest.class);

    static Client client;

    public static final String UTF_8 = "UTF-8";
    public static final String UTF_16 = "UTF-16";
    public static final String TEXT_PLAIN = "text/plain";
    public static final String TEXT_HTTP = "text/http";
    public static final String TEXT_PLAIN_WITH_CHARSET_US_ASCII = normalize("text/plain; charset=US-ASCII");
    public static final String TEXT_PLAIN_WITH_CHARSET_UTF_16 = normalize("text/plain; charset=UTF-16");
    public static final String TEXT_HTTP_WITH_CHARSET_US_ASCII = normalize("text/http; charset=US-ASCII");
    public static final String TEXT_HTTP_WITH_CHARSET_UTF_8 = normalize("text/http; charset=UTF-8");
    public static final String TEXT_HTTP_WITH_CHARSET_UTF_16 = normalize("text/http; charset=UTF-16");
    public static final String APPLICATION_XML = normalize("application/xml");
    public static final String APPLICATION_XML_WITH_CHARSET_US_ASCII = normalize("application/xml; charset=US-ASCII");
    public static final String APPLICATION_XML_WITH_CHARSET_UTF_8 = normalize("application/xml; charset=UTF-8");
    public static final String APPLICATION_XML_WITH_CHARSET_UTF_16 = normalize("application/xml; charset=UTF-16");
    public static final String APPLICATION_OCTET_STREAM = normalize("application/octet-stream");
    public static final String abc_us_ascii = "abc";
    public static final byte[] abc_us_ascii_bytes = abc_us_ascii.getBytes(Charset.forName("us-ascii"));
    public static final String abc_utf8 = new String("abc\u20AC");
    public static final byte[] abc_utf8_bytes = abc_utf8.getBytes(StandardCharsets.UTF_8);
    public static final String abc_utf16 = new String("abc\u20AC");
    public static final byte[] abc_utf16_bytes = abc_utf16.getBytes(Charset.forName("utf-16"));
    public static final String TEXT_PLAIN_WITH_CHARSET_UTF_8 = normalize("text/plain; charset=UTF-8");

    @BeforeClass
    public static void init() {
        client = ClientBuilder.newClient();
    }

    @AfterClass
    public static void after() throws Exception {
        client.close();
        client = null;
    }

    @Deployment(name = "InputPartDefaultCharsetOverwriteTest")
    public static Archive<?> createTestArchiveBasic() {
        WebArchive war = TestUtil.prepareArchive(InputPartDefaultCharsetOverwriteTest.class.getSimpleName());
        war.addClasses(InputPartDefaultCharsetOverwriteTest.class);
        war.addClasses(TestUtil.class, PortProviderUtil.class);
        return TestUtil.finishContainerPrepare(war, null, InputPartDefaultCharsetOverwriteService.class);
    }

    @Deployment(name = "InputPartDefaultCharsetOverwriteContentTypeCharsetUTF8")
    public static Archive<?> createTestArchiveContentTypeCharsetUTF8() {
        WebArchive war = TestUtil.prepareArchive(InputPartDefaultCharsetOverwriteContentTypeCharsetUTF8.class.getSimpleName());
        war.addClasses(InputPartDefaultCharsetOverwriteTest.class);
        war.addClasses(TestUtil.class, PortProviderUtil.class);
        return TestUtil.finishContainerPrepare(war, null, InputPartDefaultCharsetOverwriteContentTypeCharsetUTF8.class,
                InputPartDefaultCharsetOverwriteService.class);
    }

    @Deployment(name = "InputPartDefaultCharsetOverwriteContentTypeCharsetUTF16")
    public static Archive<?> createTestArchiveContentTypeCharsetUTF8ContentTypeCharsetUTF16() {
        WebArchive war = TestUtil.prepareArchive(InputPartDefaultCharsetOverwriteContentTypeCharsetUTF16.class.getSimpleName());
        war.addClasses(InputPartDefaultCharsetOverwriteTest.class);
        war.addClasses(TestUtil.class, PortProviderUtil.class);
        return TestUtil.finishContainerPrepare(war, null, InputPartDefaultCharsetOverwriteContentTypeCharsetUTF16.class,
                InputPartDefaultCharsetOverwriteService.class);
    }

    @Deployment(name = "InputPartDefaultCharsetOverwriteContentTypeNoCharsetUTF8")
    public static Archive<?> createTestArchiveContentTypeNoCharsetUTF8() {
        WebArchive war = TestUtil.prepareArchive(InputPartDefaultCharsetOverwriteContentTypeNoCharsetUTF8.class.getSimpleName());
        war.addClasses(InputPartDefaultCharsetOverwriteTest.class);
        war.addClasses(TestUtil.class, PortProviderUtil.class);
        return TestUtil.finishContainerPrepare(war, null, InputPartDefaultCharsetOverwriteContentTypeNoCharsetUTF8.class,
                InputPartDefaultCharsetOverwriteService.class);
    }

    @Deployment(name = "InputPartDefaultCharsetOverwriteContentTypeNoCharsetUTF16")
    public static Archive<?> createTestArchiveContentTypeNoCharsetUTF16() {
        WebArchive war = TestUtil.prepareArchive(InputPartDefaultCharsetOverwriteContentTypeNoCharsetUTF16.class.getSimpleName());
        war.addClasses(InputPartDefaultCharsetOverwriteTest.class);
        war.addClasses(TestUtil.class, PortProviderUtil.class);
        return TestUtil.finishContainerPrepare(war, null, InputPartDefaultCharsetOverwriteContentTypeNoCharsetUTF16.class,
                InputPartDefaultCharsetOverwriteService.class);
    }

    @Deployment(name = "InputPartDefaultCharsetOverwriteNoContentTypeCharsetUTF8")
    public static Archive<?> createTestArchiveNoContentTypeCharsetUTF8() {
        WebArchive war = TestUtil.prepareArchive(InputPartDefaultCharsetOverwriteNoContentTypeCharsetUTF8.class.getSimpleName());
        war.addClasses(InputPartDefaultCharsetOverwriteTest.class);
        war.addClasses(TestUtil.class, PortProviderUtil.class);
        return TestUtil.finishContainerPrepare(war, null, InputPartDefaultCharsetOverwriteNoContentTypeCharsetUTF8.class,
                InputPartDefaultCharsetOverwriteService.class);
    }

    @Deployment(name = "InputPartDefaultCharsetOverwriteNoContentTypeCharsetUTF16")
    public static Archive<?> createTestArchiveNoContentTypeCharsetUTF16() {
        WebArchive war = TestUtil.prepareArchive(InputPartDefaultCharsetOverwriteNoContentTypeCharsetUTF16.class.getSimpleName());
        war.addClasses(InputPartDefaultCharsetOverwriteTest.class);
        war.addClasses(TestUtil.class, PortProviderUtil.class);
        return TestUtil.finishContainerPrepare(war, null, InputPartDefaultCharsetOverwriteNoContentTypeCharsetUTF16.class,
                InputPartDefaultCharsetOverwriteService.class);
    }

    /**
     * @tpTestDetails Test UTF8 content-type, no charset, preprocessor with no content-type
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testUTF8ContentTypeNoCharsetPreprocessorWithNoContentTypeCharset() throws Exception {
        doTestWithContentTypeInMessage(abc_utf8_bytes, abc_utf8, TEXT_PLAIN, TEXT_PLAIN_WITH_CHARSET_UTF_8, InputPartDefaultCharsetOverwriteNoContentTypeCharsetUTF8.class);
    }

    /**
     * @tpTestDetails The tests use no interceptor.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void noInterceptorTest() throws Exception {
        // testNoContentTypeDefault
        doTestNoContentTypeInMessage(abc_us_ascii_bytes, abc_us_ascii, TEXT_PLAIN_WITH_CHARSET_US_ASCII, InputPartDefaultCharsetOverwriteTest.class);
        // testContentTypeNoCharsetDefault
        doTestWithContentTypeInMessage(abc_us_ascii_bytes, abc_us_ascii, TEXT_HTTP,
                TEXT_HTTP_WITH_CHARSET_US_ASCII, InputPartDefaultCharsetOverwriteTest.class);
        // testContentTypeCharsetDefaultUTF8
        doTestWithContentTypeInMessage(abc_utf8_bytes, abc_utf8, TEXT_HTTP_WITH_CHARSET_UTF_8,
                TEXT_HTTP_WITH_CHARSET_UTF_8, InputPartDefaultCharsetOverwriteTest.class);
        // testContentTypeCharsetDefaultUTF16
        doTestWithContentTypeInMessage(abc_utf16_bytes, abc_utf16, TEXT_HTTP_WITH_CHARSET_UTF_16,
                TEXT_HTTP_WITH_CHARSET_UTF_16, InputPartDefaultCharsetOverwriteTest.class);
        // testNoContentTypeInputPartContentTypeUTF8
        doTestNoContentTypeInMessageContentTypeInQuery(abc_utf8_bytes, abc_utf8, TEXT_HTTP_WITH_CHARSET_UTF_8,
                TEXT_HTTP_WITH_CHARSET_UTF_8, InputPartDefaultCharsetOverwriteTest.class);
        // testNoContentTypeInputPartContentTypeUTF16
        doTestNoContentTypeInMessageContentTypeInQuery(abc_utf16_bytes, abc_utf16, TEXT_HTTP_WITH_CHARSET_UTF_16,
                TEXT_HTTP_WITH_CHARSET_UTF_16, InputPartDefaultCharsetOverwriteTest.class);
        // testContentTypeInputPartContentTypeUTF8
        doTestWithContentTypeInMessageContentTypeInQuery(abc_utf8_bytes, abc_utf8, TEXT_HTTP_WITH_CHARSET_UTF_16, TEXT_HTTP_WITH_CHARSET_UTF_8,
                TEXT_HTTP_WITH_CHARSET_UTF_8, InputPartDefaultCharsetOverwriteTest.class);
        // testContentTypeInputPartContentTypeUTF16
        doTestWithContentTypeInMessageContentTypeInQuery(abc_utf16_bytes, abc_utf16, TEXT_HTTP_WITH_CHARSET_UTF_8, TEXT_HTTP_WITH_CHARSET_UTF_16,
                TEXT_HTTP_WITH_CHARSET_UTF_16, InputPartDefaultCharsetOverwriteTest.class);
    }

    /**
     * @tpTestDetails The tests use an interceptor that installs a content-type but no charset.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void interceptorNoCharsetYesContentType() throws Exception {
        // testNoContentTypePreprocessorWithContentTypeNoCharsetUTF8
        doTestNoContentTypeInMessage(abc_utf8_bytes, abc_utf8, TEXT_HTTP_WITH_CHARSET_UTF_8,
                InputPartDefaultCharsetOverwriteContentTypeNoCharsetUTF8.class);
        // testNoContentTypePreprocessorWithContentTypeNoCharsetUTF16
        doTestNoContentTypeInMessage(abc_utf16_bytes, abc_utf16, TEXT_HTTP_WITH_CHARSET_UTF_16,
                InputPartDefaultCharsetOverwriteContentTypeNoCharsetUTF16.class);
        // testContentTypeNoCharsetPreprocessorWithContentTypeNoCharsetUTF8
        doTestWithContentTypeInMessage(abc_utf8_bytes, abc_utf8, TEXT_PLAIN, TEXT_PLAIN_WITH_CHARSET_UTF_8,
                InputPartDefaultCharsetOverwriteNoContentTypeCharsetUTF8.class);
        // testContentTypeNoCharsetPreprocessorWithContentTypeNoCharsetUTF16
        doTestWithContentTypeInMessage(abc_utf16_bytes, abc_utf16, TEXT_PLAIN, TEXT_PLAIN_WITH_CHARSET_UTF_16,
                InputPartDefaultCharsetOverwriteNoContentTypeCharsetUTF16.class);
        // testContentTypeCharsetPreprocessorWithContentTypeNoCharsetUTF8
        doTestWithContentTypeInMessage(abc_utf8_bytes, abc_utf8, TEXT_PLAIN_WITH_CHARSET_UTF_8, TEXT_PLAIN_WITH_CHARSET_UTF_8,
                InputPartDefaultCharsetOverwriteContentTypeNoCharsetUTF16.class);
        // testContentTypeCharsetPreprocessorWithContentTypeNoCharsetUTF16
        doTestWithContentTypeInMessage(abc_utf16_bytes, abc_utf16, TEXT_PLAIN_WITH_CHARSET_UTF_16, TEXT_PLAIN_WITH_CHARSET_UTF_16,
                InputPartDefaultCharsetOverwriteContentTypeNoCharsetUTF8.class);
        // testNoContentTypePreprocessorWithContentTypeNoCharsetInputPartContentTypeUTF8
        doTestNoContentTypeInMessageContentTypeInQuery(abc_utf8_bytes, abc_utf8, TEXT_HTTP_WITH_CHARSET_UTF_8, TEXT_HTTP_WITH_CHARSET_UTF_8,
                InputPartDefaultCharsetOverwriteContentTypeNoCharsetUTF16.class);
        // testNoContentTypePreprocessorWithContentTypeNoCharsetInputPartContentTypeUTF16
        doTestNoContentTypeInMessageContentTypeInQuery(abc_utf16_bytes, abc_utf16, TEXT_HTTP_WITH_CHARSET_UTF_16, TEXT_HTTP_WITH_CHARSET_UTF_16,
                InputPartDefaultCharsetOverwriteContentTypeNoCharsetUTF8.class);
        // testContentTypePreprocessorWithContentTypeNoCharsetInputPartContentTypeUTF8
        doTestWithContentTypeInMessageContentTypeInQuery(abc_utf8_bytes, abc_utf8, TEXT_HTTP_WITH_CHARSET_UTF_16, TEXT_HTTP_WITH_CHARSET_UTF_8,
                TEXT_HTTP_WITH_CHARSET_UTF_8, InputPartDefaultCharsetOverwriteContentTypeNoCharsetUTF16.class);
        // testContentTypereprocessorWithContentTypeNoCharsetInputPartContentTypeUTF16
        doTestWithContentTypeInMessageContentTypeInQuery(abc_utf16_bytes, abc_utf16, TEXT_HTTP_WITH_CHARSET_UTF_8, TEXT_HTTP_WITH_CHARSET_UTF_16,
                TEXT_HTTP_WITH_CHARSET_UTF_16, InputPartDefaultCharsetOverwriteContentTypeNoCharsetUTF8.class);
    }

    /**
     * @tpTestDetails The tests use an interceptor that installs a charset but no content-type.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNoContentTypePreprocessorWithNoContentTypeCharsetUTF8() throws Exception {
        // testNoContentTypePreprocessorWithNoContentTypeCharsetUTF8
        doTestNoContentTypeInMessage(abc_utf8_bytes, abc_utf8, TEXT_PLAIN_WITH_CHARSET_UTF_8,
                InputPartDefaultCharsetOverwriteNoContentTypeCharsetUTF8.class);
        // testNoContentTypePreprocessorWithNoContentTypeCharsetUTF16
        doTestNoContentTypeInMessage(abc_utf16_bytes, abc_utf16, TEXT_PLAIN_WITH_CHARSET_UTF_16,
                InputPartDefaultCharsetOverwriteNoContentTypeCharsetUTF16.class);
        // testContentTypeNoCharsetPreprocessorWithNoContentTypeCharsetUTF8
        doTestWithContentTypeInMessage(abc_utf8_bytes, abc_utf8, TEXT_HTTP, TEXT_HTTP_WITH_CHARSET_UTF_8,
                InputPartDefaultCharsetOverwriteNoContentTypeCharsetUTF8.class);
        // testContentTypeNoCharsetPreprocessorWithNoContentTypeCharsetUTF16
        doTestWithContentTypeInMessage(abc_utf16_bytes, abc_utf16, TEXT_HTTP, TEXT_HTTP_WITH_CHARSET_UTF_16,
                InputPartDefaultCharsetOverwriteNoContentTypeCharsetUTF16.class);
        // testContentTypeCharsetPreprocessorWithNoContentTypeCharset8
        doTestWithContentTypeInMessage(abc_utf8_bytes, abc_utf8, TEXT_PLAIN_WITH_CHARSET_UTF_8, TEXT_PLAIN_WITH_CHARSET_UTF_8,
                InputPartDefaultCharsetOverwriteNoContentTypeCharsetUTF16.class);
        // testContentTypeCharsetPreprocessorWithNoContentTypeCharset16
        doTestWithContentTypeInMessage(abc_utf16_bytes, abc_utf16, TEXT_PLAIN_WITH_CHARSET_UTF_16, TEXT_PLAIN_WITH_CHARSET_UTF_16,
                InputPartDefaultCharsetOverwriteNoContentTypeCharsetUTF8.class);
        // testNoContentTypePreprocessorWithNoContentTypeCharsetInputPartContentTypeUTF8
        doTestNoContentTypeInMessageContentTypeInQuery(abc_utf8_bytes, abc_utf8, TEXT_HTTP_WITH_CHARSET_UTF_8, TEXT_HTTP_WITH_CHARSET_UTF_8,
                InputPartDefaultCharsetOverwriteNoContentTypeCharsetUTF16.class);
        // testNoContentTypePreprocessorWithNoContentTypeCharsetInputPartContentTypeUTF16
        doTestNoContentTypeInMessageContentTypeInQuery(abc_utf16_bytes, abc_utf16, TEXT_HTTP_WITH_CHARSET_UTF_16,
                TEXT_HTTP_WITH_CHARSET_UTF_16, InputPartDefaultCharsetOverwriteNoContentTypeCharsetUTF8.class);
        // testContentTypePreprocessorWithNoContentTypeCharsetInputPartContentTypeUTF8
        doTestWithContentTypeInMessageContentTypeInQuery(abc_utf8_bytes, abc_utf8, TEXT_HTTP_WITH_CHARSET_UTF_16, TEXT_HTTP_WITH_CHARSET_UTF_8,
                TEXT_HTTP_WITH_CHARSET_UTF_8, InputPartDefaultCharsetOverwriteNoContentTypeCharsetUTF16.class);
        // testContentTypereprocessorWithNoContentTypeCharsetInputPartContentTypeUTF16
        doTestWithContentTypeInMessageContentTypeInQuery(abc_utf16_bytes, abc_utf16, TEXT_HTTP_WITH_CHARSET_UTF_8, TEXT_HTTP_WITH_CHARSET_UTF_16,
                TEXT_HTTP_WITH_CHARSET_UTF_16, InputPartDefaultCharsetOverwriteNoContentTypeCharsetUTF8.class);
    }

    /**
     * @tpTestDetails The tests use an interceptor that installs both a content-type and a charset.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNoContentTypePreprocessorWithContentTypeCharset8() throws Exception {
        // testNoContentTypePreprocessorWithContentTypeCharset8
        doTestNoContentTypeInMessage(abc_utf8_bytes, abc_utf8, TEXT_HTTP_WITH_CHARSET_UTF_8,
                InputPartDefaultCharsetOverwriteContentTypeCharsetUTF8.class);
        // testNoContentTypePreprocessorWithContentTypeCharset16
        doTestNoContentTypeInMessage(abc_utf16_bytes, abc_utf16, TEXT_HTTP_WITH_CHARSET_UTF_16,
                InputPartDefaultCharsetOverwriteContentTypeCharsetUTF16.class);
        // testContentTypeNoCharsetPreprocessorWithContentTypeCharset8
        doTestWithContentTypeInMessage(abc_utf8_bytes, abc_utf8, TEXT_PLAIN, TEXT_PLAIN_WITH_CHARSET_UTF_8,
                InputPartDefaultCharsetOverwriteContentTypeCharsetUTF8.class);
        // testContentTypeNoCharsetPreprocessorWithContentTypeCharset16
        doTestWithContentTypeInMessage(abc_utf16_bytes, abc_utf16, TEXT_PLAIN, TEXT_PLAIN_WITH_CHARSET_UTF_16,
                InputPartDefaultCharsetOverwriteContentTypeCharsetUTF16.class);
        // testContentTypeCharsetPreprocessorWithContentTypeCharset8
        doTestWithContentTypeInMessage(abc_utf8_bytes, abc_utf8, TEXT_PLAIN_WITH_CHARSET_UTF_8, TEXT_PLAIN_WITH_CHARSET_UTF_8,
                InputPartDefaultCharsetOverwriteContentTypeCharsetUTF16.class);
        // testContentTypeCharsetPreprocessorWithContentTypeCharset16
        doTestWithContentTypeInMessage(abc_utf16_bytes, abc_utf16, TEXT_PLAIN_WITH_CHARSET_UTF_16, TEXT_PLAIN_WITH_CHARSET_UTF_16,
                InputPartDefaultCharsetOverwriteContentTypeCharsetUTF8.class);
        // testNoContentTypePreprocessorWithContentTypeCharsetInputPartContentTypeUTF8
        doTestNoContentTypeInMessageContentTypeInQuery(abc_utf8_bytes, abc_utf8, TEXT_HTTP_WITH_CHARSET_UTF_8, TEXT_HTTP_WITH_CHARSET_UTF_8,
                InputPartDefaultCharsetOverwriteContentTypeCharsetUTF16.class);
        // testNoContentTypePreprocessorWithContentTypeCharsetInputPartContentTypeUTF16
        doTestNoContentTypeInMessageContentTypeInQuery(abc_utf16_bytes, abc_utf16, TEXT_HTTP_WITH_CHARSET_UTF_16, TEXT_HTTP_WITH_CHARSET_UTF_16,
                InputPartDefaultCharsetOverwriteContentTypeCharsetUTF8.class);
        // testContentTypePreprocessorWithContentTypeCharsetInputPartContentTypeUTF8
        doTestWithContentTypeInMessageContentTypeInQuery(abc_utf8_bytes, abc_utf8, TEXT_HTTP_WITH_CHARSET_UTF_16, TEXT_HTTP_WITH_CHARSET_UTF_8,
                TEXT_HTTP_WITH_CHARSET_UTF_8, InputPartDefaultCharsetOverwriteContentTypeCharsetUTF16.class);
        // testContentTypereprocessorWithContentTypeCharsetInputPartContentTypeUTF16
        doTestWithContentTypeInMessageContentTypeInQuery(abc_utf16_bytes, abc_utf16, TEXT_HTTP_WITH_CHARSET_UTF_8, TEXT_HTTP_WITH_CHARSET_UTF_16,
                TEXT_HTTP_WITH_CHARSET_UTF_16, InputPartDefaultCharsetOverwriteContentTypeCharsetUTF8.class);
    }

    /**
     * @tpTestDetails The tests use a non-text media type.
     * @tpSince RESTEasy 3.0.16
     */
    //////////////////////////////////////////////////////////////////////////////////////////
    //
    //////////////////////////////////////////////////////////////////////////////////////////
    @Test
    public void testApplicationXmlUSAscii() throws Exception {
        // testApplicationXmlUSAscii
        doTestWithContentTypeInMessage(abc_us_ascii_bytes, abc_us_ascii, APPLICATION_XML_WITH_CHARSET_US_ASCII,
                APPLICATION_XML_WITH_CHARSET_US_ASCII, InputPartDefaultCharsetOverwriteTest.class);
        // testApplicationXmlUTF8
        doTestWithContentTypeInMessage(abc_utf8_bytes, abc_utf8, APPLICATION_XML_WITH_CHARSET_UTF_8,
                APPLICATION_XML_WITH_CHARSET_UTF_8, InputPartDefaultCharsetOverwriteTest.class);
        // testApplicationXmlUTF16
        doTestWithContentTypeInMessage(abc_utf16_bytes, abc_utf16, APPLICATION_XML_WITH_CHARSET_UTF_16,
                APPLICATION_XML_WITH_CHARSET_UTF_16, InputPartDefaultCharsetOverwriteTest.class);
        // testApplicationOctetStreamUSAscii
        doTestByteArray(abc_us_ascii_bytes, APPLICATION_OCTET_STREAM, InputPartDefaultCharsetOverwriteTest.class);
        // testApplicationOctetStreamUTF8
        doTestByteArray(abc_utf8_bytes, APPLICATION_OCTET_STREAM, InputPartDefaultCharsetOverwriteTest.class);
        // testApplicationOctetStreamUTF16
        doTestByteArray(abc_utf16_bytes, APPLICATION_OCTET_STREAM, InputPartDefaultCharsetOverwriteTest.class);
        // testByteArray
        byte[] body = new byte[512];
        Random r = new Random(System.currentTimeMillis());
        r.nextBytes(body);
        doTestByteArray(body, APPLICATION_OCTET_STREAM, InputPartDefaultCharsetOverwriteTest.class);
    }

    private static void doTestNoContentTypeInMessage(byte[] body, String expectedBody, String expectedContentType, Class<?> providerClass) throws Exception {
        byte[] start = ("--boo\r\nContent-Disposition: form-data; name=\"foo\"\r\nContent-Transfer-Encoding: 8bit\r\n\r\n").getBytes();
        byte[] end = "\r\n--boo--\r\n".getBytes();
        byte[] buf = new byte[start.length + body.length + end.length];
        int pos0 = 0;
        int pos1 = pos0 + start.length;
        int pos2 = pos1 + body.length;
        System.arraycopy(start, 0, buf, pos0, start.length);
        System.arraycopy(body, 0, buf, pos1, body.length);
        System.arraycopy(end, 0, buf, pos2, end.length);
        Response response = client.target(PortProviderUtil.generateURL("/test/", providerClass.getSimpleName())).request()
                .post(Entity.entity(buf, "multipart/form-data; boundary=boo"));
        String responseStr = response.readEntity(String.class);
        logger.info("status: " + response.getStatus());
        logger.info("client response: " + responseStr);
        Assert.assertEquals("Status code is wrong.", 20, response.getStatus() / 10);
        String[] answer = responseStr.split(":");
        Assert.assertEquals("Wrong size of response", 3, answer.length);
        logger.info("response charset: " + answer[0]);
        Assert.assertEquals("Response has wrong encoding", normalize(expectedContentType), normalize(answer[0]));
        Assert.assertEquals("Wrong content of response", expectedBody, answer[1]);
        Assert.assertEquals("Wrong content of response", expectedBody, answer[2]);
        response.close();
    }

    private static void doTestWithContentTypeInMessage(byte[] body, String expectedBody, String inputContentType, String expectedContentType, Class<?> deployment) throws Exception {
        byte[] start = ("--boo\r\nContent-Disposition: form-data; name=\"foo\"\r\nContent-Type: ").getBytes();
        byte[] middle = (inputContentType + "\r\n\r\n").getBytes();
        byte[] end = "\r\n--boo--\r\n".getBytes();
        byte[] buf = new byte[start.length + middle.length + body.length + end.length];
        int pos0 = 0;
        int pos1 = pos0 + start.length;
        int pos2 = pos1 + middle.length;
        int pos3 = pos2 + body.length;
        System.arraycopy(start, 0, buf, pos0, start.length);
        System.arraycopy(middle, 0, buf, pos1, middle.length);
        System.arraycopy(body, 0, buf, pos2, body.length);
        System.arraycopy(end, 0, buf, pos3, end.length);
        Response response = client.target(PortProviderUtil.generateURL("/test/", deployment.getSimpleName())).request()
                .post(Entity.entity(buf, "multipart/form-data; boundary=boo"));
        String responseStr = response.readEntity(String.class);
        logger.info("status: " + response.getStatus());
        logger.info("client response: " + responseStr);
        Assert.assertEquals("Status code is wrong.", 20, response.getStatus() / 10);
        String[] answer = responseStr.split(":");
        Assert.assertEquals("Wrong size of response", 3, answer.length);
        logger.info("response charset: " + answer[0]);
        Assert.assertEquals("Response has wrong encoding", normalize(expectedContentType), normalize(answer[0]));
        Assert.assertEquals("Wrong content of response", expectedBody, answer[1]);
        Assert.assertEquals("Wrong content of response", expectedBody, answer[2]);
    }

    private static void doTestNoContentTypeInMessageContentTypeInQuery(
            byte[] body, String expectedBody, String queryContentType, String expectedContentType, Class<?> deployment) throws Exception {
        byte[] start = ("--boo\r\nContent-Disposition: form-data; name=\"foo\"\r\nContent-Transfer-Encoding: 8bit\r\n\r\n").getBytes();
        byte[] end = "\r\n--boo--\r\n".getBytes();
        byte[] buf = new byte[start.length + body.length + end.length];
        int pos0 = 0;
        int pos1 = pos0 + start.length;
        int pos2 = pos1 + body.length;
        System.arraycopy(start, 0, buf, pos0, start.length);
        System.arraycopy(body, 0, buf, pos1, body.length);
        System.arraycopy(end, 0, buf, pos2, end.length);
        Response response = client.target(PortProviderUtil.generateURL("/query?contentType=" + Encode.encodeQueryParamAsIs(queryContentType), deployment.getSimpleName())).request()
                .post(Entity.entity(buf, "multipart/form-data; boundary=boo"));
        String responseStr = response.readEntity(String.class);
        logger.info("status: " + response.getStatus());
        logger.info("client response: " + responseStr);
        Assert.assertEquals("Status code is wrong.", 20, response.getStatus() / 10);
        String[] answer = responseStr.split(":");
        Assert.assertEquals("Wrong size of response", 3, answer.length);
        logger.info("response charset: " + answer[0]);
        Assert.assertEquals("Response has wrong encoding", normalize(expectedContentType), normalize(answer[0]));
        Assert.assertEquals("Wrong content of response", expectedBody, answer[1]);
        Assert.assertEquals("Wrong content of response", expectedBody, answer[2]);
    }

    private static void doTestWithContentTypeInMessageContentTypeInQuery(
            byte[] body, String expectedBody, String inputContentType, String queryContentType, String expectedContentType, Class<?> deployment) throws Exception {
        byte[] start = ("--boo\r\nContent-Disposition: form-data; name=\"foo\"\r\nContent-Type: ").getBytes();
        byte[] middle = (inputContentType + "\r\n\r\n").getBytes();
        byte[] end = "\r\n--boo--\r\n".getBytes();
        byte[] buf = new byte[start.length + middle.length + body.length + end.length];
        int pos0 = 0;
        int pos1 = pos0 + start.length;
        int pos2 = pos1 + middle.length;
        int pos3 = pos2 + body.length;
        System.arraycopy(start, 0, buf, pos0, start.length);
        System.arraycopy(middle, 0, buf, pos1, middle.length);
        System.arraycopy(body, 0, buf, pos2, body.length);
        System.arraycopy(end, 0, buf, pos3, end.length);
        Response response = client.target(PortProviderUtil.generateURL("/query?contentType=" + Encode.encodeQueryParamAsIs(queryContentType), deployment.getSimpleName())).request()
                .post(Entity.entity(buf, "multipart/form-data; boundary=boo"));
        String responseStr = response.readEntity(String.class);
        logger.info("status: " + response.getStatus());
        logger.info("client response: " + responseStr);
        Assert.assertEquals("Status code is wrong.", 20, response.getStatus() / 10);
        String[] answer = responseStr.split(":");
        Assert.assertEquals("Wrong size of response", 3, answer.length);
        logger.info("response charset: " + answer[0]);
        Assert.assertEquals("Response has wrong encoding", normalize(expectedContentType), normalize(answer[0]));
        Assert.assertEquals("Wrong content of response", expectedBody, answer[1]);
        Assert.assertEquals("Wrong content of response", expectedBody, answer[2]);
    }

    private static void doTestByteArray(byte[] body, String contentType, Class<?> deployment) throws Exception {
        byte[] start = ("--boo\r\nContent-Disposition: form-data; name=\"foo\"\r\nContent-Type: " + contentType + "\r\n\r\n").getBytes();
        byte[] end = "\r\n--boo--\r\n".getBytes();
        byte[] buf = new byte[start.length + body.length + end.length];
        int pos0 = 0;
        int pos1 = pos0 + start.length;
        int pos2 = pos1 + body.length;
        System.arraycopy(start, 0, buf, pos0, start.length);
        System.arraycopy(body, 0, buf, pos1, body.length);
        System.arraycopy(end, 0, buf, pos2, end.length);
        Response response = client.target(PortProviderUtil.generateURL("/bytes/", deployment.getSimpleName())).request()
                .post(Entity.entity(buf, "multipart/form-data; boundary=boo"));
        logger.info("status: " + response.getStatus());
        Assert.assertEquals("Status code is wrong.", 20, response.getStatus() / 10);
        byte[] b = response.readEntity(byte[].class);
        for (int i = 0; i < body.length; i++) {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Wrong content of response: ").append(i).append(": ").append(body[i]).append(" != ").append(b[i]);
            Assert.assertEquals(errorMessage.toString(), body[i], b[i]);
        }
    }

    private static String normalize(String s) {
        String sl = s.toLowerCase();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); i++) {
            if (sl.charAt(i) != ' ' && sl.charAt(i) != '"') {
                sb.append(sl.charAt(i));
            }
        }
        return sb.toString();
    }
}

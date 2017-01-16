package org.jboss.resteasy.test.core.encoding;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.core.encoding.resource.EncodingTestResource;
import org.jboss.resteasy.test.core.encoding.resource.EncodingTestClient;
import org.jboss.resteasy.util.Encode;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.StandardCharsets;


/**
 * @tpSubChapter Encoding
 * @tpChapter Integration tests
 * @tpTestCaseDetails Test for HTML encoding and decoding.
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class EncodingTest {
    static ResteasyClient client;

    protected static final Logger logger = LogManager.getLogger(EncodingTest.class.getName());

    private static EncodingTestClient testClient;

    Character[] RESERVED_CHARACTERS = {
            '?', ':', '@', '&', '=', '+', '$', ','
    };

    //also includes a-zA-Z0-9
    Character[] UNRESERVED_CHARACTERS = {
            '-', '_', '.', '!', '~', '*', '\'', '(', ')'
    };
    //also includes 0x00-0x1F and 0x7F
    Character[] EXCLUDED_CHARACTERS = {
            ' ', '<', '>', '#', '%', '\"'
    };
    Character[] UNWISE_CHARACTERS = {
            '{', '}', '|', '\\', '^', '[', ']', '`'
    };

    @Before
    public void init() {
        client = new ResteasyClientBuilder().build();
        testClient = client.target(PortProviderUtil.generateBaseUrl(EncodingTest.class.getSimpleName())).proxyBuilder(EncodingTestClient.class).build();
    }

    @After
    public void after() throws Exception {
        client.close();
    }

    @Deployment
    public static Archive<?> createTestArchive() {
        WebArchive war = TestUtil.prepareArchive(EncodingTest.class.getSimpleName());
        war.addClasses(EncodingTestClient.class);

        return TestUtil.finishContainerPrepare(war, null, EncodingTestResource.class);
    }

    /**
     * @tpTestDetails Tests requesting special characters via a ClientProxy.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEncodingCharacters() throws Exception {
        for (Character ch : RESERVED_CHARACTERS) {
            encodingCharacter(ch);
        }
        for (Character ch : UNRESERVED_CHARACTERS) {
            encodingCharacter(ch);
        }
        for (Character ch : EXCLUDED_CHARACTERS) {
            encodingCharacter(ch);
        }
        for (Character ch : UNWISE_CHARACTERS) {
            encodingCharacter(ch);
        }
    }

    public void encodingCharacter(Character toTest) {
        String paramWithChar = "start" + toTest + "end";
        Response returned = testClient.getPathParam(paramWithChar);
        Assert.assertNotNull("Wrong returned value", returned);
        Assert.assertEquals("Wrong returned status", returned.getStatus(), HttpURLConnection.HTTP_OK);
        Assert.assertEquals("Wrong returned value", returned.readEntity(String.class), paramWithChar);
    }

    /**
     * @tpTestDetails Tests backslash encoding.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPercent() {
        encodingCharacter('\\');
    }

    /**
     * @tpTestDetails Tests requesting special characters via manual URL construction.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testViaDirectURI() throws Exception {
        for (Character ch : RESERVED_CHARACTERS) {
            viaDirectURI(ch);
        }
        for (Character ch : UNRESERVED_CHARACTERS) {
            viaDirectURI(ch);
        }
        for (Character ch : EXCLUDED_CHARACTERS) {
            viaDirectURI(ch);
        }
        for (Character ch : UNWISE_CHARACTERS) {
            viaDirectURI(ch);
        }

    }

    /**
     * @tpTestDetails Chinese character decoding in path.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void decodeChineseInPath() throws Exception {
        String path = Encode.decodePath("/%E5%B9%B4%E5%81%87%E6%9C%9F/%E5%B9%B4%E5%81%87%E6%9C%9F");
        logger.info(path);
        Assert.assertEquals("Wrong decoded path", "/\u5E74\u5047\u671F/\u5E74\u5047\u671F", path);
    }

    /**
     * @tpTestDetails Western European character decoding in path.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void decodeWesternEuropeanCharsInPath() throws Exception {
        Assert.assertEquals("Wrong decoded path", "Gr\u00FC\u00DF Gott", Encode.decodePath("Gr%C3%BC%C3%9F%20Gott"));
        Assert.assertEquals("Wrong decoded path", "D\u00E6lenenga Gr\u00FCnerl\u00F8kka", Encode.decodePath("D%C3%A6lenenga%20Gr%C3%BCnerl%C3%B8kka"));
        Assert.assertEquals("Wrong decoded path", "\u00C4lv\u00E5s V\u00E4stra G\u00F6taland", Encode.decodePath("%C3%84lv%C3%A5s%20V%C3%A4stra%20G%C3%B6taland"));
    }

    public void viaDirectURI(Character toTest) throws Exception {
        String expected = "start" + toTest + "end";
        String encoded = "start%" + Integer.toHexString(toTest).toUpperCase() + "end";
        String stringUri = PortProviderUtil.generateURL("/test/path-param/" + encoded, EncodingTest.class.getSimpleName());
        URI uri = URI.create(stringUri);
        HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("accept", "text/plain");
        InputStream is = connection.getInputStream();
        Reader r = new InputStreamReader(is, StandardCharsets.UTF_8);
        StringBuffer buf = new StringBuffer();
        char[] chars = new char[1024];
        int charsRead;
        while ((charsRead = r.read(chars)) != -1) {
            buf.append(chars, 0, charsRead);
        }
        r.close();
        is.close();

        Assert.assertEquals("Wrong answer (answer may be decoded badly)", buf.toString(), expected);
    }

    /**
     * @tpTestDetails Test method for {@link org.jboss.resteasy.util.Encode#encodeQueryParam(String)}
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEncodeQueryParam() {
        Assert.assertEquals("Wrong encoded path", "Test", Encode.encodeQueryParam("Test"));
    }

    /**
     * @tpTestDetails Test method for {@link org.jboss.resteasy.util.Encode#encodeQueryParam(String)}.
     *                Special characters are used.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEncodeQueryParam_special() {
        Assert.assertEquals("Wrong encoded path", ".-%2B%2F%7C", Encode.encodeQueryParam(".-+/|"));
    }

    /**
     * @tpTestDetails Test for scands characters.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEncodeQueryParamScands() throws UnsupportedEncodingException {
        String input = "\u00E5\u00E4\u00F6\u00C5\u00C4\u00D6";
        String expected = "%C3%A5%C3%A4%C3%B6%C3%85%C3%84%C3%96";
        Assert.assertEquals("Wrong encoded path", expected, Encode.encodeQueryParam(input));
    }

    /**
     * @tpTestDetails Test for 2bytes UTF8 character.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEncodeQueryParamUtf8TwoByte() throws CharacterCodingException, UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        for (int n = 0; n < 2047; n++) {
            sb.append((char) n);
        }
        String characters = sb.toString();
        String encodedQueryParam = Encode.encodeQueryParam(characters);
        String expected = "%00%01%02%03%04%05%06%07%08%09%0A%0B%0C%0D%0E%0F%10%11%12%13%14%15%16%17%18%19%1A%1B%1C%1D%1E%1F+%21%22%23%24%25%26%27%28%29*%2B%2C-.%2F0123456789%3A%3B%3C%3D%3E?%40ABCDEFGHIJKLMNOPQRSTUVWXYZ%5B%5C%5D%5E_%60abcdefghijklmnopqrstuvwxyz{|}~%7F%C2%80%C2%81%C2%82%C2%83%C2%84%C2%85%C2%86%C2%87%C2%88%C2%89%C2%8A%C2%8B%C2%8C%C2%8D%C2%8E%C2%8F%C2%90%C2%91%C2%92%C2%93%C2%94%C2%95%C2%96%C2%97%C2%98%C2%99%C2%9A%C2%9B%C2%9C%C2%9D%C2%9E%C2%9F%C2%A0%C2%A1%C2%A2%C2%A3%C2%A4%C2%A5%C2%A6%C2%A7%C2%A8%C2%A9%C2%AA%C2%AB%C2%AC%C2%AD%C2%AE%C2%AF%C2%B0%C2%B1%C2%B2%C2%B3%C2%B4%C2%B5%C2%B6%C2%B7%C2%B8%C2%B9%C2%BA%C2%BB%C2%BC%C2%BD%C2%BE%C2%BF%C3%80%C3%81%C3%82%C3%83%C3%84%C3%85%C3%86%C3%87%C3%88%C3%89%C3%8A%C3%8B%C3%8C%C3%8D%C3%8E%C3%8F%C3%90%C3%91%C3%92%C3%93%C3%94%C3%95%C3%96%C3%97%C3%98%C3%99%C3%9A%C3%9B%C3%9C%C3%9D%C3%9E%C3%9F%C3%A0%C3%A1%C3%A2%C3%A3%C3%A4%C3%A5%C3%A6%C3%A7%C3%A8%C3%A9%C3%AA%C3%AB%C3%AC%C3%AD%C3%AE%C3%AF%C3%B0%C3%B1%C3%B2%C3%B3%C3%B4%C3%B5%C3%B6%C3%B7%C3%B8%C3%B9%C3%BA%C3%BB%C3%BC%C3%BD%C3%BE%C3%BF%C4%80%C4%81%C4%82%C4%83%C4%84%C4%85%C4%86%C4%87%C4%88%C4%89%C4%8A%C4%8B%C4%8C%C4%8D%C4%8E%C4%8F%C4%90%C4%91%C4%92%C4%93%C4%94%C4%95%C4%96%C4%97%C4%98%C4%99%C4%9A%C4%9B%C4%9C%C4%9D%C4%9E%C4%9F%C4%A0%C4%A1%C4%A2%C4%A3%C4%A4%C4%A5%C4%A6%C4%A7%C4%A8%C4%A9%C4%AA%C4%AB%C4%AC%C4%AD%C4%AE%C4%AF%C4%B0%C4%B1%C4%B2%C4%B3%C4%B4%C4%B5%C4%B6%C4%B7%C4%B8%C4%B9%C4%BA%C4%BB%C4%BC%C4%BD%C4%BE%C4%BF%C5%80%C5%81%C5%82%C5%83%C5%84%C5%85%C5%86%C5%87%C5%88%C5%89%C5%8A%C5%8B%C5%8C%C5%8D%C5%8E%C5%8F%C5%90%C5%91%C5%92%C5%93%C5%94%C5%95%C5%96%C5%97%C5%98%C5%99%C5%9A%C5%9B%C5%9C%C5%9D%C5%9E%C5%9F%C5%A0%C5%A1%C5%A2%C5%A3%C5%A4%C5%A5%C5%A6%C5%A7%C5%A8%C5%A9%C5%AA%C5%AB%C5%AC%C5%AD%C5%AE%C5%AF%C5%B0%C5%B1%C5%B2%C5%B3%C5%B4%C5%B5%C5%B6%C5%B7%C5%B8%C5%B9%C5%BA%C5%BB%C5%BC%C5%BD%C5%BE%C5%BF%C6%80%C6%81%C6%82%C6%83%C6%84%C6%85%C6%86%C6%87%C6%88%C6%89%C6%8A%C6%8B%C6%8C%C6%8D%C6%8E%C6%8F%C6%90%C6%91%C6%92%C6%93%C6%94%C6%95%C6%96%C6%97%C6%98%C6%99%C6%9A%C6%9B%C6%9C%C6%9D%C6%9E%C6%9F%C6%A0%C6%A1%C6%A2%C6%A3%C6%A4%C6%A5%C6%A6%C6%A7%C6%A8%C6%A9%C6%AA%C6%AB%C6%AC%C6%AD%C6%AE%C6%AF%C6%B0%C6%B1%C6%B2%C6%B3%C6%B4%C6%B5%C6%B6%C6%B7%C6%B8%C6%B9%C6%BA%C6%BB%C6%BC%C6%BD%C6%BE%C6%BF%C7%80%C7%81%C7%82%C7%83%C7%84%C7%85%C7%86%C7%87%C7%88%C7%89%C7%8A%C7%8B%C7%8C%C7%8D%C7%8E%C7%8F%C7%90%C7%91%C7%92%C7%93%C7%94%C7%95%C7%96%C7%97%C7%98%C7%99%C7%9A%C7%9B%C7%9C%C7%9D%C7%9E%C7%9F%C7%A0%C7%A1%C7%A2%C7%A3%C7%A4%C7%A5%C7%A6%C7%A7%C7%A8%C7%A9%C7%AA%C7%AB%C7%AC%C7%AD%C7%AE%C7%AF%C7%B0%C7%B1%C7%B2%C7%B3%C7%B4%C7%B5%C7%B6%C7%B7%C7%B8%C7%B9%C7%BA%C7%BB%C7%BC%C7%BD%C7%BE%C7%BF%C8%80%C8%81%C8%82%C8%83%C8%84%C8%85%C8%86%C8%87%C8%88%C8%89%C8%8A%C8%8B%C8%8C%C8%8D%C8%8E%C8%8F%C8%90%C8%91%C8%92%C8%93%C8%94%C8%95%C8%96%C8%97%C8%98%C8%99%C8%9A%C8%9B%C8%9C%C8%9D%C8%9E%C8%9F%C8%A0%C8%A1%C8%A2%C8%A3%C8%A4%C8%A5%C8%A6%C8%A7%C8%A8%C8%A9%C8%AA%C8%AB%C8%AC%C8%AD%C8%AE%C8%AF%C8%B0%C8%B1%C8%B2%C8%B3%C8%B4%C8%B5%C8%B6%C8%B7%C8%B8%C8%B9%C8%BA%C8%BB%C8%BC%C8%BD%C8%BE%C8%BF%C9%80%C9%81%C9%82%C9%83%C9%84%C9%85%C9%86%C9%87%C9%88%C9%89%C9%8A%C9%8B%C9%8C%C9%8D%C9%8E%C9%8F%C9%90%C9%91%C9%92%C9%93%C9%94%C9%95%C9%96%C9%97%C9%98%C9%99%C9%9A%C9%9B%C9%9C%C9%9D%C9%9E%C9%9F%C9%A0%C9%A1%C9%A2%C9%A3%C9%A4%C9%A5%C9%A6%C9%A7%C9%A8%C9%A9%C9%AA%C9%AB%C9%AC%C9%AD%C9%AE%C9%AF%C9%B0%C9%B1%C9%B2%C9%B3%C9%B4%C9%B5%C9%B6%C9%B7%C9%B8%C9%B9%C9%BA%C9%BB%C9%BC%C9%BD%C9%BE%C9%BF%CA%80%CA%81%CA%82%CA%83%CA%84%CA%85%CA%86%CA%87%CA%88%CA%89%CA%8A%CA%8B%CA%8C%CA%8D%CA%8E%CA%8F%CA%90%CA%91%CA%92%CA%93%CA%94%CA%95%CA%96%CA%97%CA%98%CA%99%CA%9A%CA%9B%CA%9C%CA%9D%CA%9E%CA%9F%CA%A0%CA%A1%CA%A2%CA%A3%CA%A4%CA%A5%CA%A6%CA%A7%CA%A8%CA%A9%CA%AA%CA%AB%CA%AC%CA%AD%CA%AE%CA%AF%CA%B0%CA%B1%CA%B2%CA%B3%CA%B4%CA%B5%CA%B6%CA%B7%CA%B8%CA%B9%CA%BA%CA%BB%CA%BC%CA%BD%CA%BE%CA%BF%CB%80%CB%81%CB%82%CB%83%CB%84%CB%85%CB%86%CB%87%CB%88%CB%89%CB%8A%CB%8B%CB%8C%CB%8D%CB%8E%CB%8F%CB%90%CB%91%CB%92%CB%93%CB%94%CB%95%CB%96%CB%97%CB%98%CB%99%CB%9A%CB%9B%CB%9C%CB%9D%CB%9E%CB%9F%CB%A0%CB%A1%CB%A2%CB%A3%CB%A4%CB%A5%CB%A6%CB%A7%CB%A8%CB%A9%CB%AA%CB%AB%CB%AC%CB%AD%CB%AE%CB%AF%CB%B0%CB%B1%CB%B2%CB%B3%CB%B4%CB%B5%CB%B6%CB%B7%CB%B8%CB%B9%CB%BA%CB%BB%CB%BC%CB%BD%CB%BE%CB%BF%CC%80%CC%81%CC%82%CC%83%CC%84%CC%85%CC%86%CC%87%CC%88%CC%89%CC%8A%CC%8B%CC%8C%CC%8D%CC%8E%CC%8F%CC%90%CC%91%CC%92%CC%93%CC%94%CC%95%CC%96%CC%97%CC%98%CC%99%CC%9A%CC%9B%CC%9C%CC%9D%CC%9E%CC%9F%CC%A0%CC%A1%CC%A2%CC%A3%CC%A4%CC%A5%CC%A6%CC%A7%CC%A8%CC%A9%CC%AA%CC%AB%CC%AC%CC%AD%CC%AE%CC%AF%CC%B0%CC%B1%CC%B2%CC%B3%CC%B4%CC%B5%CC%B6%CC%B7%CC%B8%CC%B9%CC%BA%CC%BB%CC%BC%CC%BD%CC%BE%CC%BF%CD%80%CD%81%CD%82%CD%83%CD%84%CD%85%CD%86%CD%87%CD%88%CD%89%CD%8A%CD%8B%CD%8C%CD%8D%CD%8E%CD%8F%CD%90%CD%91%CD%92%CD%93%CD%94%CD%95%CD%96%CD%97%CD%98%CD%99%CD%9A%CD%9B%CD%9C%CD%9D%CD%9E%CD%9F%CD%A0%CD%A1%CD%A2%CD%A3%CD%A4%CD%A5%CD%A6%CD%A7%CD%A8%CD%A9%CD%AA%CD%AB%CD%AC%CD%AD%CD%AE%CD%AF%CD%B0%CD%B1%CD%B2%CD%B3%CD%B4%CD%B5%CD%B6%CD%B7%CD%B8%CD%B9%CD%BA%CD%BB%CD%BC%CD%BD%CD%BE%CD%BF%CE%80%CE%81%CE%82%CE%83%CE%84%CE%85%CE%86%CE%87%CE%88%CE%89%CE%8A%CE%8B%CE%8C%CE%8D%CE%8E%CE%8F%CE%90%CE%91%CE%92%CE%93%CE%94%CE%95%CE%96%CE%97%CE%98%CE%99%CE%9A%CE%9B%CE%9C%CE%9D%CE%9E%CE%9F%CE%A0%CE%A1%CE%A2%CE%A3%CE%A4%CE%A5%CE%A6%CE%A7%CE%A8%CE%A9%CE%AA%CE%AB%CE%AC%CE%AD%CE%AE%CE%AF%CE%B0%CE%B1%CE%B2%CE%B3%CE%B4%CE%B5%CE%B6%CE%B7%CE%B8%CE%B9%CE%BA%CE%BB%CE%BC%CE%BD%CE%BE%CE%BF%CF%80%CF%81%CF%82%CF%83%CF%84%CF%85%CF%86%CF%87%CF%88%CF%89%CF%8A%CF%8B%CF%8C%CF%8D%CF%8E%CF%8F%CF%90%CF%91%CF%92%CF%93%CF%94%CF%95%CF%96%CF%97%CF%98%CF%99%CF%9A%CF%9B%CF%9C%CF%9D%CF%9E%CF%9F%CF%A0%CF%A1%CF%A2%CF%A3%CF%A4%CF%A5%CF%A6%CF%A7%CF%A8%CF%A9%CF%AA%CF%AB%CF%AC%CF%AD%CF%AE%CF%AF%CF%B0%CF%B1%CF%B2%CF%B3%CF%B4%CF%B5%CF%B6%CF%B7%CF%B8%CF%B9%CF%BA%CF%BB%CF%BC%CF%BD%CF%BE%CF%BF%D0%80%D0%81%D0%82%D0%83%D0%84%D0%85%D0%86%D0%87%D0%88%D0%89%D0%8A%D0%8B%D0%8C%D0%8D%D0%8E%D0%8F%D0%90%D0%91%D0%92%D0%93%D0%94%D0%95%D0%96%D0%97%D0%98%D0%99%D0%9A%D0%9B%D0%9C%D0%9D%D0%9E%D0%9F%D0%A0%D0%A1%D0%A2%D0%A3%D0%A4%D0%A5%D0%A6%D0%A7%D0%A8%D0%A9%D0%AA%D0%AB%D0%AC%D0%AD%D0%AE%D0%AF%D0%B0%D0%B1%D0%B2%D0%B3%D0%B4%D0%B5%D0%B6%D0%B7%D0%B8%D0%B9%D0%BA%D0%BB%D0%BC%D0%BD%D0%BE%D0%BF%D1%80%D1%81%D1%82%D1%83%D1%84%D1%85%D1%86%D1%87%D1%88%D1%89%D1%8A%D1%8B%D1%8C%D1%8D%D1%8E%D1%8F%D1%90%D1%91%D1%92%D1%93%D1%94%D1%95%D1%96%D1%97%D1%98%D1%99%D1%9A%D1%9B%D1%9C%D1%9D%D1%9E%D1%9F%D1%A0%D1%A1%D1%A2%D1%A3%D1%A4%D1%A5%D1%A6%D1%A7%D1%A8%D1%A9%D1%AA%D1%AB%D1%AC%D1%AD%D1%AE%D1%AF%D1%B0%D1%B1%D1%B2%D1%B3%D1%B4%D1%B5%D1%B6%D1%B7%D1%B8%D1%B9%D1%BA%D1%BB%D1%BC%D1%BD%D1%BE%D1%BF%D2%80%D2%81%D2%82%D2%83%D2%84%D2%85%D2%86%D2%87%D2%88%D2%89%D2%8A%D2%8B%D2%8C%D2%8D%D2%8E%D2%8F%D2%90%D2%91%D2%92%D2%93%D2%94%D2%95%D2%96%D2%97%D2%98%D2%99%D2%9A%D2%9B%D2%9C%D2%9D%D2%9E%D2%9F%D2%A0%D2%A1%D2%A2%D2%A3%D2%A4%D2%A5%D2%A6%D2%A7%D2%A8%D2%A9%D2%AA%D2%AB%D2%AC%D2%AD%D2%AE%D2%AF%D2%B0%D2%B1%D2%B2%D2%B3%D2%B4%D2%B5%D2%B6%D2%B7%D2%B8%D2%B9%D2%BA%D2%BB%D2%BC%D2%BD%D2%BE%D2%BF%D3%80%D3%81%D3%82%D3%83%D3%84%D3%85%D3%86%D3%87%D3%88%D3%89%D3%8A%D3%8B%D3%8C%D3%8D%D3%8E%D3%8F%D3%90%D3%91%D3%92%D3%93%D3%94%D3%95%D3%96%D3%97%D3%98%D3%99%D3%9A%D3%9B%D3%9C%D3%9D%D3%9E%D3%9F%D3%A0%D3%A1%D3%A2%D3%A3%D3%A4%D3%A5%D3%A6%D3%A7%D3%A8%D3%A9%D3%AA%D3%AB%D3%AC%D3%AD%D3%AE%D3%AF%D3%B0%D3%B1%D3%B2%D3%B3%D3%B4%D3%B5%D3%B6%D3%B7%D3%B8%D3%B9%D3%BA%D3%BB%D3%BC%D3%BD%D3%BE%D3%BF%D4%80%D4%81%D4%82%D4%83%D4%84%D4%85%D4%86%D4%87%D4%88%D4%89%D4%8A%D4%8B%D4%8C%D4%8D%D4%8E%D4%8F%D4%90%D4%91%D4%92%D4%93%D4%94%D4%95%D4%96%D4%97%D4%98%D4%99%D4%9A%D4%9B%D4%9C%D4%9D%D4%9E%D4%9F%D4%A0%D4%A1%D4%A2%D4%A3%D4%A4%D4%A5%D4%A6%D4%A7%D4%A8%D4%A9%D4%AA%D4%AB%D4%AC%D4%AD%D4%AE%D4%AF%D4%B0%D4%B1%D4%B2%D4%B3%D4%B4%D4%B5%D4%B6%D4%B7%D4%B8%D4%B9%D4%BA%D4%BB%D4%BC%D4%BD%D4%BE%D4%BF%D5%80%D5%81%D5%82%D5%83%D5%84%D5%85%D5%86%D5%87%D5%88%D5%89%D5%8A%D5%8B%D5%8C%D5%8D%D5%8E%D5%8F%D5%90%D5%91%D5%92%D5%93%D5%94%D5%95%D5%96%D5%97%D5%98%D5%99%D5%9A%D5%9B%D5%9C%D5%9D%D5%9E%D5%9F%D5%A0%D5%A1%D5%A2%D5%A3%D5%A4%D5%A5%D5%A6%D5%A7%D5%A8%D5%A9%D5%AA%D5%AB%D5%AC%D5%AD%D5%AE%D5%AF%D5%B0%D5%B1%D5%B2%D5%B3%D5%B4%D5%B5%D5%B6%D5%B7%D5%B8%D5%B9%D5%BA%D5%BB%D5%BC%D5%BD%D5%BE%D5%BF%D6%80%D6%81%D6%82%D6%83%D6%84%D6%85%D6%86%D6%87%D6%88%D6%89%D6%8A%D6%8B%D6%8C%D6%8D%D6%8E%D6%8F%D6%90%D6%91%D6%92%D6%93%D6%94%D6%95%D6%96%D6%97%D6%98%D6%99%D6%9A%D6%9B%D6%9C%D6%9D%D6%9E%D6%9F%D6%A0%D6%A1%D6%A2%D6%A3%D6%A4%D6%A5%D6%A6%D6%A7%D6%A8%D6%A9%D6%AA%D6%AB%D6%AC%D6%AD%D6%AE%D6%AF%D6%B0%D6%B1%D6%B2%D6%B3%D6%B4%D6%B5%D6%B6%D6%B7%D6%B8%D6%B9%D6%BA%D6%BB%D6%BC%D6%BD%D6%BE%D6%BF%D7%80%D7%81%D7%82%D7%83%D7%84%D7%85%D7%86%D7%87%D7%88%D7%89%D7%8A%D7%8B%D7%8C%D7%8D%D7%8E%D7%8F%D7%90%D7%91%D7%92%D7%93%D7%94%D7%95%D7%96%D7%97%D7%98%D7%99%D7%9A%D7%9B%D7%9C%D7%9D%D7%9E%D7%9F%D7%A0%D7%A1%D7%A2%D7%A3%D7%A4%D7%A5%D7%A6%D7%A7%D7%A8%D7%A9%D7%AA%D7%AB%D7%AC%D7%AD%D7%AE%D7%AF%D7%B0%D7%B1%D7%B2%D7%B3%D7%B4%D7%B5%D7%B6%D7%B7%D7%B8%D7%B9%D7%BA%D7%BB%D7%BC%D7%BD%D7%BE%D7%BF%D8%80%D8%81%D8%82%D8%83%D8%84%D8%85%D8%86%D8%87%D8%88%D8%89%D8%8A%D8%8B%D8%8C%D8%8D%D8%8E%D8%8F%D8%90%D8%91%D8%92%D8%93%D8%94%D8%95%D8%96%D8%97%D8%98%D8%99%D8%9A%D8%9B%D8%9C%D8%9D%D8%9E%D8%9F%D8%A0%D8%A1%D8%A2%D8%A3%D8%A4%D8%A5%D8%A6%D8%A7%D8%A8%D8%A9%D8%AA%D8%AB%D8%AC%D8%AD%D8%AE%D8%AF%D8%B0%D8%B1%D8%B2%D8%B3%D8%B4%D8%B5%D8%B6%D8%B7%D8%B8%D8%B9%D8%BA%D8%BB%D8%BC%D8%BD%D8%BE%D8%BF%D9%80%D9%81%D9%82%D9%83%D9%84%D9%85%D9%86%D9%87%D9%88%D9%89%D9%8A%D9%8B%D9%8C%D9%8D%D9%8E%D9%8F%D9%90%D9%91%D9%92%D9%93%D9%94%D9%95%D9%96%D9%97%D9%98%D9%99%D9%9A%D9%9B%D9%9C%D9%9D%D9%9E%D9%9F%D9%A0%D9%A1%D9%A2%D9%A3%D9%A4%D9%A5%D9%A6%D9%A7%D9%A8%D9%A9%D9%AA%D9%AB%D9%AC%D9%AD%D9%AE%D9%AF%D9%B0%D9%B1%D9%B2%D9%B3%D9%B4%D9%B5%D9%B6%D9%B7%D9%B8%D9%B9%D9%BA%D9%BB%D9%BC%D9%BD%D9%BE%D9%BF%DA%80%DA%81%DA%82%DA%83%DA%84%DA%85%DA%86%DA%87%DA%88%DA%89%DA%8A%DA%8B%DA%8C%DA%8D%DA%8E%DA%8F%DA%90%DA%91%DA%92%DA%93%DA%94%DA%95%DA%96%DA%97%DA%98%DA%99%DA%9A%DA%9B%DA%9C%DA%9D%DA%9E%DA%9F%DA%A0%DA%A1%DA%A2%DA%A3%DA%A4%DA%A5%DA%A6%DA%A7%DA%A8%DA%A9%DA%AA%DA%AB%DA%AC%DA%AD%DA%AE%DA%AF%DA%B0%DA%B1%DA%B2%DA%B3%DA%B4%DA%B5%DA%B6%DA%B7%DA%B8%DA%B9%DA%BA%DA%BB%DA%BC%DA%BD%DA%BE%DA%BF%DB%80%DB%81%DB%82%DB%83%DB%84%DB%85%DB%86%DB%87%DB%88%DB%89%DB%8A%DB%8B%DB%8C%DB%8D%DB%8E%DB%8F%DB%90%DB%91%DB%92%DB%93%DB%94%DB%95%DB%96%DB%97%DB%98%DB%99%DB%9A%DB%9B%DB%9C%DB%9D%DB%9E%DB%9F%DB%A0%DB%A1%DB%A2%DB%A3%DB%A4%DB%A5%DB%A6%DB%A7%DB%A8%DB%A9%DB%AA%DB%AB%DB%AC%DB%AD%DB%AE%DB%AF%DB%B0%DB%B1%DB%B2%DB%B3%DB%B4%DB%B5%DB%B6%DB%B7%DB%B8%DB%B9%DB%BA%DB%BB%DB%BC%DB%BD%DB%BE%DB%BF%DC%80%DC%81%DC%82%DC%83%DC%84%DC%85%DC%86%DC%87%DC%88%DC%89%DC%8A%DC%8B%DC%8C%DC%8D%DC%8E%DC%8F%DC%90%DC%91%DC%92%DC%93%DC%94%DC%95%DC%96%DC%97%DC%98%DC%99%DC%9A%DC%9B%DC%9C%DC%9D%DC%9E%DC%9F%DC%A0%DC%A1%DC%A2%DC%A3%DC%A4%DC%A5%DC%A6%DC%A7%DC%A8%DC%A9%DC%AA%DC%AB%DC%AC%DC%AD%DC%AE%DC%AF%DC%B0%DC%B1%DC%B2%DC%B3%DC%B4%DC%B5%DC%B6%DC%B7%DC%B8%DC%B9%DC%BA%DC%BB%DC%BC%DC%BD%DC%BE%DC%BF%DD%80%DD%81%DD%82%DD%83%DD%84%DD%85%DD%86%DD%87%DD%88%DD%89%DD%8A%DD%8B%DD%8C%DD%8D%DD%8E%DD%8F%DD%90%DD%91%DD%92%DD%93%DD%94%DD%95%DD%96%DD%97%DD%98%DD%99%DD%9A%DD%9B%DD%9C%DD%9D%DD%9E%DD%9F%DD%A0%DD%A1%DD%A2%DD%A3%DD%A4%DD%A5%DD%A6%DD%A7%DD%A8%DD%A9%DD%AA%DD%AB%DD%AC%DD%AD%DD%AE%DD%AF%DD%B0%DD%B1%DD%B2%DD%B3%DD%B4%DD%B5%DD%B6%DD%B7%DD%B8%DD%B9%DD%BA%DD%BB%DD%BC%DD%BD%DD%BE%DD%BF%DE%80%DE%81%DE%82%DE%83%DE%84%DE%85%DE%86%DE%87%DE%88%DE%89%DE%8A%DE%8B%DE%8C%DE%8D%DE%8E%DE%8F%DE%90%DE%91%DE%92%DE%93%DE%94%DE%95%DE%96%DE%97%DE%98%DE%99%DE%9A%DE%9B%DE%9C%DE%9D%DE%9E%DE%9F%DE%A0%DE%A1%DE%A2%DE%A3%DE%A4%DE%A5%DE%A6%DE%A7%DE%A8%DE%A9%DE%AA%DE%AB%DE%AC%DE%AD%DE%AE%DE%AF%DE%B0%DE%B1%DE%B2%DE%B3%DE%B4%DE%B5%DE%B6%DE%B7%DE%B8%DE%B9%DE%BA%DE%BB%DE%BC%DE%BD%DE%BE%DE%BF%DF%80%DF%81%DF%82%DF%83%DF%84%DF%85%DF%86%DF%87%DF%88%DF%89%DF%8A%DF%8B%DF%8C%DF%8D%DF%8E%DF%8F%DF%90%DF%91%DF%92%DF%93%DF%94%DF%95%DF%96%DF%97%DF%98%DF%99%DF%9A%DF%9B%DF%9C%DF%9D%DF%9E%DF%9F%DF%A0%DF%A1%DF%A2%DF%A3%DF%A4%DF%A5%DF%A6%DF%A7%DF%A8%DF%A9%DF%AA%DF%AB%DF%AC%DF%AD%DF%AE%DF%AF%DF%B0%DF%B1%DF%B2%DF%B3%DF%B4%DF%B5%DF%B6%DF%B7%DF%B8%DF%B9%DF%BA%DF%BB%DF%BC%DF%BD%DF%BE";
        Assert.assertEquals("Wrong encoded path", expected, encodedQueryParam);
    }

    public void testPathParamWithDoublePercent() {
        String paramWithDoublePercent = "start%%end";
        Response returned = testClient.getPathParam(paramWithDoublePercent);
        Assert.assertNotNull(returned);
        Assert.assertEquals(HttpURLConnection.HTTP_OK, returned.getStatus());
        Assert.assertEquals(paramWithDoublePercent, returned.readEntity(String.class));
    }

    /**
     * @tpTestDetails Test path for {} characters
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPathParamWithBraces() {
        String paramWithBraces = "start{param}end";
        Response returned = testClient.getPathParam(paramWithBraces);
        Assert.assertNotNull("Wrong content of response", returned);
        Assert.assertEquals(HttpURLConnection.HTTP_OK, returned.getStatus());
        Assert.assertEquals("Wrong content of response", paramWithBraces, returned.readEntity(String.class));
    }

    /**
     * @tpTestDetails Test path for % character
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testPathParamWithLifePercentDeath() {
        String paramWithLifePercentDeath = "life%death";
        Response returned = testClient.getPathParam(paramWithLifePercentDeath);
        Assert.assertNotNull("Wrong content of response", returned);
        Assert.assertEquals(HttpURLConnection.HTTP_OK, returned.getStatus());
        Assert.assertEquals("Wrong content of response", paramWithLifePercentDeath, returned.readEntity(String.class));
    }

    /**
     * @tpTestDetails Test query for %% characters
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testQueryParamWithDoublePercent() {
        String paramWithDoublePercent = "start%%end";
        Response returned = testClient.getQueryParam(paramWithDoublePercent);
        Assert.assertNotNull("Wrong content of response", returned);
        Assert.assertEquals(HttpURLConnection.HTTP_OK, returned.getStatus());
        Assert.assertEquals("Wrong content of response", paramWithDoublePercent, returned.readEntity(String.class));
    }

    /**
     * @tpTestDetails Test query for {} characters
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testQueryParamWithBraces() {
        String paramWithBraces = "start{param}end";
        Response returned = testClient.getQueryParam(paramWithBraces);
        Assert.assertNotNull("Wrong content of response", returned);
        Assert.assertEquals(HttpURLConnection.HTTP_OK, returned.getStatus());
        Assert.assertEquals("Wrong content of response", paramWithBraces, returned.readEntity(String.class));
    }

    /**
     * @tpTestDetails Test query for % character
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testQueryParamWithLifePercentDeath() {
        String paramWithLifePercentDeath = "life%death";
        Response returned = testClient.getQueryParam(paramWithLifePercentDeath);
        Assert.assertNotNull("Wrong content of response", returned);
        Assert.assertEquals(HttpURLConnection.HTTP_OK, returned.getStatus());
        Assert.assertEquals("Wrong content of response", paramWithLifePercentDeath, returned.readEntity(String.class));
    }

    /**
     * @tpTestDetails Test for encoding string seems to be a code, but it is not a code
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEncodeNonCodes() {
        final String ERROR_MSG = "Text was not encoded successfully";
        Assert.assertEquals(ERROR_MSG, "", Encode.encodeNonCodes(""));
        Assert.assertEquals(ERROR_MSG, "a simple string", Encode.encodeNonCodes("a simple string"));
        Assert.assertEquals(ERROR_MSG, "%25", Encode.encodeNonCodes("%"));
        Assert.assertEquals(ERROR_MSG, "%25%25%25%25", Encode.encodeNonCodes("%%%%"));
        Assert.assertEquals(ERROR_MSG, "%25%25", Encode.encodeNonCodes("%%25"));
        Assert.assertEquals(ERROR_MSG, "%25a%25", Encode.encodeNonCodes("%a%25"));
        Assert.assertEquals(ERROR_MSG, "a%25b", Encode.encodeNonCodes("a%b"));
        Assert.assertEquals(ERROR_MSG, "a%25b", Encode.encodeNonCodes("a%25b"));
        Assert.assertEquals(ERROR_MSG, "a%25%25%25%25b%25%25%25%25c", Encode.encodeNonCodes("a%%%%b%%25%%c"));
    }

}

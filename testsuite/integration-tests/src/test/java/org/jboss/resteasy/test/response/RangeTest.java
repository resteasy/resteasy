package org.jboss.resteasy.test.response;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.logging.Logger;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.jboss.resteasy.test.response.resource.RangeResource;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@ExtendWith(ArquillianExtension.class)
@RunAsClient
public class RangeTest {

    protected final Logger logger = Logger.getLogger(VariantsTest.class.getName());

    static Client client;

    @BeforeAll
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(RangeTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, RangeResource.class);
    }

    @AfterAll
    public static void close() {
        Response response = client.target(generateURL("/deletefile")).request().get();
        response.close();
        response = client.target(generateURL("/deletesmallfile")).request().get();
        response.close();
        client.close();
        client = null;
    }

    private static String generateURL(String path) {
        return PortProviderUtil.generateURL(path, RangeTest.class.getSimpleName());
    }

    /**
     * @tpTestDetails Verify that required date object can be created and parsed to string
     * @tpPassCrit Date object is created
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testDate() {
        SimpleDateFormat dateFormatRFC822 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        dateFormatRFC822.setTimeZone(TimeZone.getTimeZone("GMT"));
        String format = dateFormatRFC822.format(new Date());
        logger.info(format);
        try {
            Date date = dateFormatRFC822.parse(format);
            logger.info(date.toString());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @tpTestDetails Client sends GET request for a file with request header set to Range 0-3
     * @tpPassCrit The returned response code is 206 (Partial Content), the length of the response is 4, the response
     *             contains string "hell" and the response contains "Content-Range" header as written in HTTP 1.1 specification.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRange0to3() {
        Response response = client.target(generateURL("/file")).request()
                .header("Range", "bytes=0-3").get();
        Assertions.assertEquals(HttpResponseCodes.SC_PARTIAL_CONTENT, response.getStatus());
        Assertions.assertEquals(4, response.getLength(), "The response doesn't contain the expected length of the answer");
        logger.info("Content-Range: " + response.getHeaderString("Content-Range"));
        Assertions.assertEquals(response.readEntity(String.class), "hell",
                "The response doesn't contain the expected substring");
        response.close();
    }

    /**
     * @tpTestDetails Client sends GET request for a file with request header set to Range 1-4
     * @tpPassCrit The returned response code is 206 (Partial Content), the length of the response is 4, the response
     *             contains string "ello" and the response contains "Content-Range" header as written in HTTP 1.1 specification.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRange1to4() {
        Response response = client.target(generateURL("/file")).request()
                .header("Range", "bytes=1-4").get();
        Assertions.assertEquals(HttpResponseCodes.SC_PARTIAL_CONTENT, response.getStatus());
        Assertions.assertEquals(4, response.getLength(), "The response doesn't contain the expected length of the answer");
        logger.info("Content-Range: " + response.getHeaderString("Content-Range"));
        Assertions.assertEquals(response.readEntity(String.class), "ello",
                "The response doesn't contain the expected substring");
        response.close();
    }

    /**
     * @tpTestDetails Client sends GET request for a file with request header set to Range 0-3000
     * @tpPassCrit The returned response code is 206 (Partial Content), the length of the response is 3001
     *             and the response contains "Content-Range" header as written in HTTP 1.1 specification.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRange0to3000() {
        Response response = client.target(generateURL("/file")).request()
                .header("Range", "bytes=0-3000").get();
        Assertions.assertEquals(HttpResponseCodes.SC_PARTIAL_CONTENT, response.getStatus());
        Assertions.assertEquals(3001, response.getLength(), "The response doesn't contain the expected length of the answer");
        logger.info("Content-Range: " + response.getHeaderString("Content-Range"));
        byte[] bytes = response.readEntity(new GenericType<byte[]>() {
        });
        Assertions.assertEquals(3001, bytes.length, "The response doesn't contain the expected length of entity");
        response.close();
    }

    /**
     * @tpTestDetails Client sends GET request for a file with request header set to Range -4
     * @tpPassCrit The returned response code is 206 (Partial Content), the length of the response is 4,
     *             the response contains the last "1234" (the last 4 bytes from the file) and the response contains
     *             "Content-Range" header as written in HTTP 1.1 specification.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNegativeRange4() {
        Response response = client.target(generateURL("/file")).request()
                .header("Range", "bytes=-4").get();
        Assertions.assertEquals(HttpResponseCodes.SC_PARTIAL_CONTENT, response.getStatus());
        Assertions.assertEquals(4, response.getLength(), "The response doesn't contain the expected length of the answer");
        logger.info("Content-Range: " + response.getHeaderString("Content-Range"));
        Assertions.assertEquals(response.readEntity(String.class), "1234",
                "The response doesn't contain the expected substring");
        response.close();
    }

    /**
     * @tpTestDetails Client sends GET request for a file with request header set to Range -6000, which is higher than
     *                the size of the file
     * @tpPassCrit The returned response code is 200 (Success) as server should return whole content of the file this
     *             time.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNegativeRange6000OutOfSize() {
        Response response = client.target(generateURL("/file")).request()
                .header("Range", "bytes=-6000").get();
        Assertions.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Client sends GET request for a file with request header set to Range full size of the file.
     * @tpPassCrit The returned response code is 206 (Partial Content), the length of the response is 9,
     *             the response contains the last "123456789" and the response contains "Content-Range" header
     *             as written in HTTP 1.1 specification.
     * @tpInfo RESTEASY-1094
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFullRange() {
        Response response = client.target(generateURL("/smallfile")).request()
                .header("Range", "bytes=0-8").get();
        Assertions.assertEquals(HttpResponseCodes.SC_PARTIAL_CONTENT, response.getStatus());
        Assertions.assertEquals(9, response.getLength(), "The response doesn't contain the expected length of the answer");
        logger.info("Content-Range: " + response.getHeaderString("Content-Range"));
        Assertions.assertEquals(response.readEntity(String.class), "123456789",
                "The response doesn't contain the expected substring");
        response.close();
    }

}

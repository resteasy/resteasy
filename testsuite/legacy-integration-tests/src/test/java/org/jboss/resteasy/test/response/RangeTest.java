package org.jboss.resteasy.test.response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.resteasy.test.response.resource.RangeResource;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.jboss.resteasy.utils.PortProviderUtil;
import org.jboss.resteasy.utils.TestUtil;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Integration tests
 * @tpSince RESTEasy 3.0.16
 */
@RunWith(Arquillian.class)
@RunAsClient
public class RangeTest {

    protected final Logger logger = LogManager.getLogger(VariantsTest.class.getName());

    static Client client;

    @BeforeClass
    public static void setup() {
        client = ClientBuilder.newClient();
    }

    @Deployment
    public static Archive<?> deploy() {
        WebArchive war = TestUtil.prepareArchive(RangeTest.class.getSimpleName());
        return TestUtil.finishContainerPrepare(war, null, RangeResource.class);
    }

    @AfterClass
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
     * contains string "hell" and the response contains "Content-Range" header as written in HTTP 1.1 specification.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRange0to3() {
        Response response = client.target(generateURL("/file")).request()
                .header("Range", "bytes=0-3").get();
        Assert.assertEquals(HttpResponseCodes.SC_PARTIAL_CONTENT, response.getStatus());
        Assert.assertEquals("The response doesn't contain the expected length of the answer", 4, response.getLength());
        logger.info("Content-Range: " + response.getHeaderString("Content-Range"));
        Assert.assertEquals("The response doesn't contain the expected substring", response.readEntity(String.class)
                , "hell");
        response.close();
    }

    /**
     * @tpTestDetails Client sends GET request for a file with request header set to Range 1-4
     * @tpPassCrit The returned response code is 206 (Partial Content), the length of the response is 4, the response
     * contains string "ello" and the response contains "Content-Range" header as written in HTTP 1.1 specification.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRange1to4() {
        Response response = client.target(generateURL("/file")).request()
                .header("Range", "bytes=1-4").get();
        Assert.assertEquals(HttpResponseCodes.SC_PARTIAL_CONTENT, response.getStatus());
        Assert.assertEquals("The response doesn't contain the expected length of the answer", 4, response.getLength());
        logger.info("Content-Range: " + response.getHeaderString("Content-Range"));
        Assert.assertEquals("The response doesn't contain the expected substring", response.readEntity(String.class)
                , "ello");
        response.close();
    }

    /**
     * @tpTestDetails Client sends GET request for a file with request header set to Range 0-3000
     * @tpPassCrit The returned response code is 206 (Partial Content), the length of the response is 3001
     * and the response contains "Content-Range" header as written in HTTP 1.1 specification.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testRange0to3000() {
        Response response = client.target(generateURL("/file")).request()
                .header("Range", "bytes=0-3000").get();
        Assert.assertEquals(HttpResponseCodes.SC_PARTIAL_CONTENT, response.getStatus());
        Assert.assertEquals("The response doesn't contain the expected length of the answer", 3001, response.getLength());
        logger.info("Content-Range: " + response.getHeaderString("Content-Range"));
        byte[] bytes = response.readEntity(new GenericType<byte[]>() {
        });
        Assert.assertEquals("The response doesn't contain the expected length of entity", 3001, bytes.length);
        response.close();
    }

    /**
     * @tpTestDetails Client sends GET request for a file with request header set to Range -4
     * @tpPassCrit The returned response code is 206 (Partial Content), the length of the response is 4,
     * the response contains the last "1234" (the last 4 bytes from the file) and the response contains
     * "Content-Range" header as written in HTTP 1.1 specification.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNegativeRange4() {
        Response response = client.target(generateURL("/file")).request()
                .header("Range", "bytes=-4").get();
        Assert.assertEquals(HttpResponseCodes.SC_PARTIAL_CONTENT, response.getStatus());
        Assert.assertEquals("The response doesn't contain the expected length of the answer", 4, response.getLength());
        logger.info("Content-Range: " + response.getHeaderString("Content-Range"));
        Assert.assertEquals("The response doesn't contain the expected substring", response.readEntity(String.class)
                , "1234");
        response.close();
    }

    /**
     * @tpTestDetails Client sends GET request for a file with request header set to Range -6000, which is higher than
     * the size of the file
     * @tpPassCrit The returned response code is 200 (Success) as server should return whole content of the file this
     * time.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNegativeRange6000OutOfSize() {
        Response response = client.target(generateURL("/file")).request()
                .header("Range", "bytes=-6000").get();
        Assert.assertEquals(HttpResponseCodes.SC_OK, response.getStatus());
        response.close();
    }

    /**
     * @tpTestDetails Client sends GET request for a file with request header set to Range full size of the file.
     * @tpPassCrit The returned response code is 206 (Partial Content), the length of the response is 9,
     * the response contains the last "123456789" and the response contains "Content-Range" header
     * as written in HTTP 1.1 specification.
     * @tpInfo RESTEASY-1094
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testFullRange() {
        Response response = client.target(generateURL("/smallfile")).request()
                .header("Range", "bytes=0-8").get();
        Assert.assertEquals(HttpResponseCodes.SC_PARTIAL_CONTENT, response.getStatus());
        Assert.assertEquals("The response doesn't contain the expected length of the answer", 9, response.getLength());
        logger.info("Content-Range: " + response.getHeaderString("Content-Range"));
        Assert.assertEquals("The response doesn't contain the expected substring", response.readEntity(String.class)
                , "123456789");
        response.close();
    }

}

package org.jboss.resteasy.test.client;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.test.client.resource.NoContentStreamingCloseTestFilter;
import org.jboss.resteasy.test.client.resource.NoContentStreamingCloseTestInputStream;
import org.jboss.resteasy.test.client.resource.NoContentStreamingCloseTestResponse;
import org.junit.Assert;
import org.junit.Test;

import javax.activation.DataSource;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.Reader;

/**
 * @tpSubChapter Resteasy-client
 * @tpChapter Unit tests
 * @tpSince RESTEasy 3.0.16
 * @tpTestCaseDetails Test for close calling, if no content in stream is used.
 */
public class NoContentStreamingCloseTest {
    private static final String NULL_STREAM_ERROR_MSG = "Stream should not be null";
    private static final String RESPONSE_ERROR_MSG = "Response has wrong content";
    private static final String CLOSE_ERROR_MSG = "Object was not closed";

    /**
     * @tpTestDetails Non-empty input stream test.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testNonEmptyInputStream() throws Exception {
        String expected = "hi";
        NoContentStreamingCloseTestInputStream testInputStream = new NoContentStreamingCloseTestInputStream(expected.getBytes());

        ResteasyClient client = new ResteasyClientBuilder() //
                .register(new NoContentStreamingCloseTestFilter(NoContentStreamingCloseTestResponse.buildStreamingResponse(testInputStream, -1))) //
                .build();

        InputStream is = client //
                .target("http://localhost/uri_is_ignored") //
                .request() //
                .get(InputStream.class); // nowhere a response to close
        Assert.assertNotNull(NULL_STREAM_ERROR_MSG, is);
        String str = new String(IOUtils.toByteArray(is));
        is.close();
        Assert.assertEquals(RESPONSE_ERROR_MSG, expected, str);
        // the stream returned by the internal httpclient must be closed!
        Assert.assertTrue(CLOSE_ERROR_MSG, testInputStream.isClosed());

        client.close();
    }

    /**
     * @tpTestDetails Empty input stream test.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEmptyInputStream() throws Exception {
        NoContentStreamingCloseTestInputStream testInputStream = new NoContentStreamingCloseTestInputStream(new byte[0]);

        ResteasyClient client = new ResteasyClientBuilder() //
                .register(new NoContentStreamingCloseTestFilter(NoContentStreamingCloseTestResponse.buildStreamingResponse(testInputStream, 0))) //
                .build();

        InputStream is = client //
                .target("http://localhost/uri_is_ignored") //
                .request() //
                .get(InputStream.class);
        Assert.assertNotNull(NULL_STREAM_ERROR_MSG, is);
        byte[] readden = IOUtils.toByteArray(is);
        is.close();
        Assert.assertEquals(RESPONSE_ERROR_MSG, 0, readden.length);
        Assert.assertTrue(CLOSE_ERROR_MSG, testInputStream.isClosed());

        client.close();
    }

    /**
     * @tpTestDetails Empty Reader test.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEmptyReader() throws Exception {
        NoContentStreamingCloseTestInputStream testInputStream = new NoContentStreamingCloseTestInputStream(new byte[0]);

        ResteasyClient client = new ResteasyClientBuilder() //
                .register(new NoContentStreamingCloseTestFilter(NoContentStreamingCloseTestResponse.buildStreamingResponse(testInputStream, 0))) //
                .build();

        Reader in = client //
                .target("http://localhost/uri_is_ignored") //
                .request() //
                .get(Reader.class);
        Assert.assertNotNull("Reader should not be null", in);
        String readden = IOUtils.toString(in);
        in.close();
        Assert.assertEquals(RESPONSE_ERROR_MSG, 0, readden.length());
        Assert.assertTrue(CLOSE_ERROR_MSG, testInputStream.isClosed());

        client.close();
    }

    /**
     * @tpTestDetails Empty DataSource test.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEmptyDataSource() throws Exception {
        NoContentStreamingCloseTestInputStream testInputStream = new NoContentStreamingCloseTestInputStream(new byte[0]);

        ResteasyClient client = new ResteasyClientBuilder() //
                .register(new NoContentStreamingCloseTestFilter(NoContentStreamingCloseTestResponse.buildStreamingResponse(testInputStream, 0))) //
                .build();

        DataSource in = client //
                .target("http://localhost/uri_is_ignored") //
                .request() //
                .get(DataSource.class);
        Assert.assertNotNull("DataSource should not be null", in);
        InputStream is = in.getInputStream();
        Assert.assertEquals(RESPONSE_ERROR_MSG, 0, IOUtils.toByteArray(is).length);
        is.close();
        Assert.assertTrue(CLOSE_ERROR_MSG, testInputStream.isClosed());

        client.close();
    }

    /**
     * @tpTestDetails Empty Stream source test.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEmptyStreamSource() throws Exception {
        NoContentStreamingCloseTestInputStream testInputStream = new NoContentStreamingCloseTestInputStream(new byte[0]);

        ResteasyClient client = new ResteasyClientBuilder() //
                .register(new NoContentStreamingCloseTestFilter( //
                        NoContentStreamingCloseTestResponse.buildStreamingResponse("application/streamsource+xml", testInputStream, 0))) //
                .build();

        Source in = client //
                .target("http://localhost/uri_is_ignored") //
                .request() //
                .get(Source.class);
        Assert.assertNotNull("Source should not be null", in);
        InputStream is = ((StreamSource) in).getInputStream();
        Assert.assertEquals(RESPONSE_ERROR_MSG, 0, IOUtils.toByteArray(is).length);
        is.close();
        Assert.assertTrue(CLOSE_ERROR_MSG, testInputStream.isClosed());

        client.close();
    }

    /**
     * @tpTestDetails Empty Byte array source test.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEmptyByteArray() throws Exception {
        NoContentStreamingCloseTestInputStream testInputStream = new NoContentStreamingCloseTestInputStream(new byte[0]);

        ResteasyClient client = new ResteasyClientBuilder() //
                .register(new NoContentStreamingCloseTestFilter(NoContentStreamingCloseTestResponse.buildStreamingResponse(testInputStream, 0))) //
                .build();

        byte[] in = client //
                .target("http://localhost/uri_is_ignored") //
                .request() //
                .get(byte[].class);
        Assert.assertNotNull("Byte array should not be null", in);
        Assert.assertEquals(RESPONSE_ERROR_MSG, 0, in.length);
        Assert.assertTrue(CLOSE_ERROR_MSG, testInputStream.isClosed());

        client.close();
    }
}

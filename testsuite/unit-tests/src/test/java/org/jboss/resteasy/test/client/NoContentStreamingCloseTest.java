package org.jboss.resteasy.test.client;

import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import jakarta.activation.DataSource;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;
import org.jboss.resteasy.test.client.resource.NoContentStreamingCloseTestFilter;
import org.jboss.resteasy.test.client.resource.NoContentStreamingCloseTestInputStream;
import org.jboss.resteasy.test.client.resource.NoContentStreamingCloseTestResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
        NoContentStreamingCloseTestInputStream testInputStream = new NoContentStreamingCloseTestInputStream(
                expected.getBytes());

        ResteasyClient client = new ResteasyClientBuilderImpl() //
                .register(new NoContentStreamingCloseTestFilter(
                        NoContentStreamingCloseTestResponse.buildStreamingResponse(testInputStream, -1))) //
                .build();

        InputStream is = client //
                .target("http://localhost/uri_is_ignored") //
                .request() //
                .get(InputStream.class); // nowhere a response to close
        Assertions.assertNotNull(is, NULL_STREAM_ERROR_MSG);
        String str = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        is.close();
        Assertions.assertEquals(expected, str, RESPONSE_ERROR_MSG);
        // the stream returned by the internal httpclient must be closed!
        Assertions.assertTrue(testInputStream.isClosed(), CLOSE_ERROR_MSG);

        client.close();
    }

    /**
     * @tpTestDetails Empty input stream test.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEmptyInputStream() throws Exception {
        NoContentStreamingCloseTestInputStream testInputStream = new NoContentStreamingCloseTestInputStream(new byte[0]);

        ResteasyClient client = new ResteasyClientBuilderImpl() //
                .register(new NoContentStreamingCloseTestFilter(
                        NoContentStreamingCloseTestResponse.buildStreamingResponse(testInputStream, 0))) //
                .build();

        InputStream is = client //
                .target("http://localhost/uri_is_ignored") //
                .request() //
                .get(InputStream.class);
        Assertions.assertNotNull(is, NULL_STREAM_ERROR_MSG);
        byte[] readden = is.readAllBytes();
        is.close();
        Assertions.assertEquals(0, readden.length, RESPONSE_ERROR_MSG);
        Assertions.assertTrue(testInputStream.isClosed(), CLOSE_ERROR_MSG);

        client.close();
    }

    /**
     * @tpTestDetails Empty Reader test.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEmptyReader() throws Exception {
        NoContentStreamingCloseTestInputStream testInputStream = new NoContentStreamingCloseTestInputStream(new byte[0]);

        ResteasyClient client = new ResteasyClientBuilderImpl() //
                .register(new NoContentStreamingCloseTestFilter(
                        NoContentStreamingCloseTestResponse.buildStreamingResponse(testInputStream, 0))) //
                .build();

        Reader in = client //
                .target("http://localhost/uri_is_ignored") //
                .request() //
                .get(Reader.class);
        Assertions.assertNotNull(in, "Reader should not be null");
        String readden = new String(testInputStream.readAllBytes(), StandardCharsets.UTF_8);
        in.close();
        Assertions.assertEquals(0, readden.length(), RESPONSE_ERROR_MSG);
        Assertions.assertTrue(testInputStream.isClosed(), CLOSE_ERROR_MSG);

        client.close();
    }

    /**
     * @tpTestDetails Empty DataSource test.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEmptyDataSource() throws Exception {
        NoContentStreamingCloseTestInputStream testInputStream = new NoContentStreamingCloseTestInputStream(new byte[0]);

        ResteasyClient client = new ResteasyClientBuilderImpl() //
                .register(new NoContentStreamingCloseTestFilter(
                        NoContentStreamingCloseTestResponse.buildStreamingResponse(testInputStream, 0))) //
                .build();

        DataSource in = client //
                .target("http://localhost/uri_is_ignored") //
                .request() //
                .get(DataSource.class);
        Assertions.assertNotNull(in, "DataSource should not be null");
        InputStream is = in.getInputStream();
        Assertions.assertEquals(0, is.readAllBytes().length, RESPONSE_ERROR_MSG);
        is.close();
        Assertions.assertTrue(testInputStream.isClosed(), CLOSE_ERROR_MSG);

        client.close();
    }

    /**
     * @tpTestDetails Empty Stream source test.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEmptyStreamSource() throws Exception {
        NoContentStreamingCloseTestInputStream testInputStream = new NoContentStreamingCloseTestInputStream(new byte[0]);

        ResteasyClient client = new ResteasyClientBuilderImpl() //
                .register(new NoContentStreamingCloseTestFilter( //
                        NoContentStreamingCloseTestResponse.buildStreamingResponse("application/streamsource+xml",
                                testInputStream, 0))) //
                .build();

        Source in = client //
                .target("http://localhost/uri_is_ignored") //
                .request() //
                .get(Source.class);
        Assertions.assertNotNull(in, "Source should not be null");
        InputStream is = ((StreamSource) in).getInputStream();
        Assertions.assertEquals(0, is.readAllBytes().length, RESPONSE_ERROR_MSG);
        is.close();
        Assertions.assertTrue(testInputStream.isClosed(), CLOSE_ERROR_MSG);

        client.close();
    }

    /**
     * @tpTestDetails Empty Byte array source test.
     * @tpSince RESTEasy 3.0.16
     */
    @Test
    public void testEmptyByteArray() throws Exception {
        NoContentStreamingCloseTestInputStream testInputStream = new NoContentStreamingCloseTestInputStream(new byte[0]);

        ResteasyClient client = new ResteasyClientBuilderImpl() //
                .register(new NoContentStreamingCloseTestFilter(
                        NoContentStreamingCloseTestResponse.buildStreamingResponse(testInputStream, 0))) //
                .build();

        byte[] in = client //
                .target("http://localhost/uri_is_ignored") //
                .request() //
                .get(byte[].class);
        Assertions.assertNotNull(in, "Byte array should not be null");
        Assertions.assertEquals(0, in.length, RESPONSE_ERROR_MSG);
        Assertions.assertTrue(testInputStream.isClosed(), CLOSE_ERROR_MSG);

        client.close();
    }
}

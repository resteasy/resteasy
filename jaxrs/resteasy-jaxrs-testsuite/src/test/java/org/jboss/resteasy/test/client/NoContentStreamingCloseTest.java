package org.jboss.resteasy.test.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.activation.DataSource;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.Response;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.junit.Assert;
import org.junit.Test;

public class NoContentStreamingCloseTest {

    @Test
    public void testNonEmptyInputStream() throws Exception {
        String expected = "hi";
        TestInputStream testInputStream = new TestInputStream(expected.getBytes());

        ResteasyClient client = new ResteasyClientBuilder() //
                .register(new TestFilter(TestResponse.buildStreamingResponse(testInputStream, -1))) //
                .build();

        InputStream is = client //
                .target("http://localhost/uri_is_ignored") //
                .request() //
                .get(InputStream.class); // nowhere a response to close
        Assert.assertNotNull(is);
        String str = new String(IOUtils.toByteArray(is));
        is.close();
        Assert.assertEquals(expected, str);
        // the stream returned by the internal httpclient must be closed!
        Assert.assertTrue(testInputStream.isClosed());

        client.close();
    }

    @Test
    public void testEmptyInputStream() throws Exception {
        TestInputStream testInputStream = new TestInputStream(new byte[0]);

        ResteasyClient client = new ResteasyClientBuilder() //
                .register(new TestFilter(TestResponse.buildStreamingResponse(testInputStream, 0))) //
                .build();

        InputStream is = client //
                .target("http://localhost/uri_is_ignored") //
                .request() //
                .get(InputStream.class);
        Assert.assertNotNull(is);
        byte[] readden = IOUtils.toByteArray(is);
        is.close();
        Assert.assertEquals(0, readden.length);
        Assert.assertTrue(testInputStream.isClosed());

        client.close();
    }

    @Test
    public void testEmptyReader() throws Exception {
        TestInputStream testInputStream = new TestInputStream(new byte[0]);

        ResteasyClient client = new ResteasyClientBuilder() //
                .register(new TestFilter(TestResponse.buildStreamingResponse(testInputStream, 0))) //
                .build();

        Reader in = client //
                .target("http://localhost/uri_is_ignored") //
                .request() //
                .get(Reader.class);
        Assert.assertNotNull(in);
        String readden = IOUtils.toString(in);
        in.close();
        Assert.assertEquals(0, readden.length());
        Assert.assertTrue(testInputStream.isClosed());

        client.close();
    }
    
    @Test
    public void testEmptyDataSource() throws Exception {
        TestInputStream testInputStream = new TestInputStream(new byte[0]);

        ResteasyClient client = new ResteasyClientBuilder() //
                .register(new TestFilter(TestResponse.buildStreamingResponse(testInputStream, 0))) //
                .build();

        DataSource in = client //
                .target("http://localhost/uri_is_ignored") //
                .request() //
                .get(DataSource.class);
        Assert.assertNotNull(in);
        InputStream is = in.getInputStream();
        Assert.assertEquals(0, IOUtils.toByteArray(is).length);
        is.close();
        Assert.assertTrue(testInputStream.isClosed());

        client.close();
    }

    @Test
    public void testEmptyStreamSource() throws Exception {
        TestInputStream testInputStream = new TestInputStream(new byte[0]);

        ResteasyClient client = new ResteasyClientBuilder() //
                .register(new TestFilter( //
                        TestResponse.buildStreamingResponse("application/streamsource+xml", testInputStream, 0))) //
                .build();

        Source in = client //
                .target("http://localhost/uri_is_ignored") //
                .request() //
                .get(Source.class);
        Assert.assertNotNull(in);
        InputStream is = ((StreamSource) in).getInputStream();
        Assert.assertEquals(0, IOUtils.toByteArray(is).length);
        is.close();
        Assert.assertTrue(testInputStream.isClosed());

        client.close();
    }
    
    @Test
    public void testEmptyByteArray() throws Exception {
        TestInputStream testInputStream = new TestInputStream(new byte[0]);

        ResteasyClient client = new ResteasyClientBuilder() //
                .register(new TestFilter(TestResponse.buildStreamingResponse(testInputStream, 0))) //
                .build();

        byte[] in = client //
                .target("http://localhost/uri_is_ignored") //
                .request() //
                .get(byte[].class);
        Assert.assertNotNull(in);
        Assert.assertEquals(0, in.length);
        Assert.assertTrue(testInputStream.isClosed());

        client.close();
    }

    static class TestInputStream extends ByteArrayInputStream {
        private boolean closed;

        public TestInputStream(byte[] b) {
            super(b);
        }

        public void close() throws IOException {
            super.close();
            closed = true;
        }

        public boolean isClosed() {
            return closed;
        }
    }

    static class TestResponse extends BuiltResponse {

        public static TestResponse buildStreamingResponse(InputStream stream, int len) {
            return new TestResponse(stream, headers(len));
        }

        public static TestResponse buildStreamingResponse(String contentType, InputStream stream, int len) {
            return new TestResponse(stream, headers(contentType, len));
        }
        
        private static Headers<Object> headers(int len) {
            return headers("application/octet-stream", len);
        }

        private static Headers<Object> headers(String contentType, int len) {
            Headers<Object> headers = new Headers<Object>();
            headers.putSingle("Content-Type", contentType);
            if (len >= 0) {
                headers.putSingle("Content-Length", Integer.toString(len));
            }
            return headers;
        }
        
        private TestResponse(InputStream stream, Headers<Object> headers) {
            super(200, headers, stream, null);
        }

        @Override
        public <T> T readEntity(Class<T> type, Type genericType, Annotation[] anns) {
            return type.cast(getEntity());
        }

        @Override
        public void close() {
            Assert.fail("Due to the InputStream-Entity the Response must not be closed but the Inputstream");
            super.close();
        }
    }

    static class TestFilter implements ClientRequestFilter {

        private final Response response;

        public TestFilter(Response response) {
            this.response = response;
        }

        @Override
        public void filter(ClientRequestContext requestContext) throws IOException {
            requestContext.abortWith(response);
        }
    }

}

package org.jboss.resteasy.test.client.resource;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.spi.HttpResponseCodes;
import org.junit.jupiter.api.Assertions;

public class NoContentStreamingCloseTestResponse extends BuiltResponse {

    public static NoContentStreamingCloseTestResponse buildStreamingResponse(InputStream stream, int len) {
        return new NoContentStreamingCloseTestResponse(stream, headers(len));
    }

    public static NoContentStreamingCloseTestResponse buildStreamingResponse(String contentType, InputStream stream, int len) {
        return new NoContentStreamingCloseTestResponse(stream, headers(contentType, len));
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

    private NoContentStreamingCloseTestResponse(final InputStream stream, final Headers<Object> headers) {
        super(HttpResponseCodes.SC_OK, headers, stream, null);
    }

    @Override
    public <T> T readEntity(Class<T> type, Type genericType, Annotation[] anns) {
        return type.cast(getEntity());
    }

    @Override
    public void close() {
        Assertions.fail("Due to the InputStream-Entity the Response must not be closed but the Inputstream");
        super.close();
    }
}

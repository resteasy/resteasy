package org.jboss.resteasy.test.asyncio;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

import io.vertx.core.Context;

@Provider
public class BlockingWriter implements MessageBodyWriter<BlockingWriterData> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == BlockingWriterData.class;
    }

    @Override
    public void writeTo(BlockingWriterData t, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {
        String resp = Context.isOnEventLoopThread() ? "KO" : "OK";
        entityStream.write(resp.getBytes(Charset.forName("UTF-8")));
        entityStream.close();
    }

}

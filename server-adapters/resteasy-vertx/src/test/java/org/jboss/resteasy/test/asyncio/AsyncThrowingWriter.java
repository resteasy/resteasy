package org.jboss.resteasy.test.asyncio;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.AsyncMessageBodyWriter;
import org.jboss.resteasy.spi.AsyncOutputStream;

@Provider
public class AsyncThrowingWriter implements AsyncMessageBodyWriter<AsyncThrowingWriterData> {

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == AsyncThrowingWriterData.class;
    }

    @Override
    public void writeTo(AsyncThrowingWriterData t, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {
        entityStream.write("KO".getBytes(Charset.forName("UTF-8")));
        entityStream.close();
    }

    @Override
    public CompletionStage<Void> asyncWriteTo(AsyncThrowingWriterData t, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
            AsyncOutputStream entityStream) {
       WebApplicationException ex = new WebApplicationException(Response.ok("this is fine").build());
       if(t.throwNow)
          throw ex;
       CompletableFuture<Void> ret = new CompletableFuture<>();
       ret.completeExceptionally(ex);
       return ret;
    }

}

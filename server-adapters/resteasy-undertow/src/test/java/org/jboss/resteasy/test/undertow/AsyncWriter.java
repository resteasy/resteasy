package org.jboss.resteasy.test.undertow;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.spi.AsyncMessageBodyWriter;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.spi.HttpRequest;

@Provider
public class AsyncWriter implements AsyncMessageBodyWriter<AsyncWriterData> {

    @Context
    HttpRequest request;

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return true;
    }

    @Override
    public void writeTo(AsyncWriterData t, Class<?> type, Type genericType, Annotation[] annotations,
            MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
            throws IOException, WebApplicationException {
        entityStream.write("KO".getBytes(Charset.forName("UTF-8")));
        entityStream.close();
    }

    @Override
    public CompletionStage<Void> asyncWriteTo(AsyncWriterData t, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
            AsyncOutputStream entityStream) {
        String resp = Thread.currentThread() == t.requestThread && !request.getAsyncContext().isSuspended() ? "OK" : "KO";
        CompletionStage<Void> start = t.simulateSlowIo
                ? CompletableFuture.runAsync(() -> {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })
                        : CompletableFuture.completedFuture(null);
        return start.thenCompose(v -> entityStream.asyncWrite(resp.getBytes(Charset.forName("UTF-8"))))
                .thenCompose(v -> entityStream.asyncFlush());
    }

}

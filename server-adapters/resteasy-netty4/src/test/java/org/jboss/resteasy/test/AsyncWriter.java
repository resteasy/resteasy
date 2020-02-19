package org.jboss.resteasy.test;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.server.netty.NettyUtil;
import org.jboss.resteasy.spi.AsyncMessageBodyWriter;
import org.jboss.resteasy.spi.AsyncOutputStream;

@Provider
public class AsyncWriter implements AsyncMessageBodyWriter<AsyncWriterData> {

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
        String resp = t.expectOnIoThread == NettyUtil.isIoThread() ? "OK" : "KO";
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

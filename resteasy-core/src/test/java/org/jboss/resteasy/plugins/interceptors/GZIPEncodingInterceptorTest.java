package org.jboss.resteasy.plugins.interceptors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.ext.WriterInterceptorContext;

import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.spi.AsyncOutputStream;
import org.jboss.resteasy.spi.AsyncWriterInterceptorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.internal.util.io.IOUtil;

public class GZIPEncodingInterceptorTest {
    private Headers<Object> headers;
    private WriterInterceptorContext context;
    private AsyncWriterInterceptorContext asyncContext;
    private CompletionStage<Void> asyncContextProceedResult;
    private GZIPEncodingInterceptor interceptor;

    @BeforeEach
    public void setUp() {
        headers = mock(Headers.class);
        context = mock(WriterInterceptorContext.class);
        when(context.getHeaders()).thenReturn(headers);
        asyncContext = mock(AsyncWriterInterceptorContext.class);
        when(asyncContext.getHeaders()).thenReturn(headers);
        asyncContextProceedResult = CompletableFuture.completedFuture(null);
        interceptor = new GZIPEncodingInterceptor();
    }

    @Test
    public void testAroundWriteToWhenNoHeader() throws IOException {
        interceptor.aroundWriteTo(context);
        verify(context).proceed();
        verify(headers, never()).remove(anyString());
        verify(context, never()).setOutputStream(any(OutputStream.class));
    }

    @Test
    public void testAroundWriteToWhenNoGzipEncoding() throws IOException {
        when(headers.getFirst(HttpHeaders.CONTENT_ENCODING)).thenReturn("deflate");
        interceptor.aroundWriteTo(context);
        verify(context).proceed();
        verify(headers, never()).remove(anyString());
        verify(context, never()).setOutputStream(any(OutputStream.class));
    }

    @Test
    public void testAroundWriteToWhenGzipEncoding() throws IOException {
        when(headers.getFirst(HttpHeaders.CONTENT_ENCODING)).thenReturn("gzip");
        OutputStream originalStream = mock(OutputStream.class);
        when(context.getOutputStream()).thenReturn(originalStream);
        ArgumentCaptor<OutputStream> outputStreamCaptor = ArgumentCaptor.forClass(OutputStream.class);

        interceptor.aroundWriteTo(context);

        verify(context).proceed();
        verify(headers).remove(HttpHeaders.CONTENT_LENGTH);
        verify(context, times(2)).setOutputStream(outputStreamCaptor.capture());
        assertTrue(outputStreamCaptor.getAllValues().get(0) instanceof GZIPEncodingInterceptor.CommittedGZIPOutputStream);
        assertEquals(originalStream, outputStreamCaptor.getAllValues().get(1));
    }

    @Test
    public void testCommittedGzipAsyncOutputStreamWrite() throws Exception {
        ByteArrayOutputStream originalStream = new ByteArrayOutputStream();
        var gzipStream = new GZIPEncodingInterceptor.CommittedGZIPOutputStream(originalStream, null);
        String plainContent = "This is a sample plain text response content";

        gzipStream.write(plainContent.getBytes(StandardCharsets.UTF_8));
        gzipStream.flush();
        gzipStream.finish();

        try (InputStream inputStream = new GZIPInputStream(new ByteArrayInputStream(originalStream.toByteArray()))) {
            String ungzippedContent = IOUtil.readLines(inputStream).stream()
                    .collect(Collectors.joining(System.lineSeparator()));
            assertEquals(plainContent, ungzippedContent);
        }
    }

    @Test
    public void testAsyncAroundWriteToWhenNoHeader() {
        when(asyncContext.asyncProceed()).thenReturn(asyncContextProceedResult);

        CompletionStage<Void> result = interceptor.asyncAroundWriteTo(asyncContext);

        assertEquals(asyncContextProceedResult, result);
        verify(headers, never()).remove(anyString());
        verify(asyncContext, never()).setAsyncOutputStream(any(AsyncOutputStream.class));
    }

    @Test
    public void testAsyncAroundWriteToWhenNoGzipEncoding() {
        when(headers.getFirst(HttpHeaders.CONTENT_ENCODING)).thenReturn("deflate");
        when(asyncContext.asyncProceed()).thenReturn(asyncContextProceedResult);

        CompletionStage<Void> result = interceptor.asyncAroundWriteTo(asyncContext);

        assertEquals(asyncContextProceedResult, result);
        verify(headers, never()).remove(anyString());
        verify(asyncContext, never()).setAsyncOutputStream(any(AsyncOutputStream.class));
    }

    @Test
    public void testAsyncAroundWriteToWhenGzipEncoding() throws ExecutionException, InterruptedException {
        when(headers.getFirst(HttpHeaders.CONTENT_ENCODING)).thenReturn("gzip");
        AsyncOutputStream originalStream = mock(AsyncOutputStream.class);
        when(asyncContext.getAsyncOutputStream()).thenReturn(originalStream);
        when(asyncContext.asyncProceed()).thenReturn(asyncContextProceedResult);
        ArgumentCaptor<AsyncOutputStream> asyncOutputStreamCaptor = ArgumentCaptor.forClass(AsyncOutputStream.class);

        CompletionStage<Void> result = interceptor.asyncAroundWriteTo(asyncContext);
        result.toCompletableFuture().get();

        assertNotEquals(asyncContextProceedResult, result);
        verify(headers).remove(HttpHeaders.CONTENT_LENGTH);
        verify(asyncContext, times(2)).setAsyncOutputStream(asyncOutputStreamCaptor.capture());
        assertTrue(asyncOutputStreamCaptor.getAllValues().get(0) instanceof GZIPEncodingInterceptor.CommittedGZIPOutputStream);
        assertEquals(originalStream, asyncOutputStreamCaptor.getAllValues().get(1));
    }

    @Test
    public void testCommittedGzipAsyncOutputStreamAsyncWrite() throws Exception {
        ByteArrayOutputStream originalStream = new ByteArrayOutputStream();
        var gzipStream = new GZIPEncodingInterceptor.CommittedGZIPOutputStream(originalStream, null);
        String plainContent = "This is a sample plain text response content";

        gzipStream.asyncWrite(plainContent.getBytes(StandardCharsets.UTF_8))
                .thenCompose(v -> gzipStream.asyncFlush())
                .thenCompose(v -> gzipStream.asyncFinish())
                .toCompletableFuture()
                .get();

        try (InputStream inputStream = new GZIPInputStream(new ByteArrayInputStream(originalStream.toByteArray()))) {
            String ungzippedContent = IOUtil.readLines(inputStream).stream()
                    .collect(Collectors.joining(System.lineSeparator()));
            assertEquals(plainContent, ungzippedContent);
        }
    }
}

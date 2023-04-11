package org.jboss.resteasy.grpc.runtime.sse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletionStage;

import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.sse.OutboundSseEvent;
import jakarta.ws.rs.sse.SseEventSink;

import org.jboss.resteasy.grpc.runtime.servlet.AsyncMockServletOutputStream;

public class SseEventOutputImpl extends GenericType<OutboundSseEvent> implements SseEventSink {
    private boolean closed;

    private final MessageBodyWriter<Object> writer;
    private final AsyncMockServletOutputStream amsos;

    @SuppressWarnings("unchecked")
    public SseEventOutputImpl(final MessageBodyWriter<?> writer, final AsyncMockServletOutputStream amsos) {
        this.writer = (MessageBodyWriter<Object>) writer;
        this.amsos = amsos;
    }

    @Override
    public void close() {
        closed = true;
        try {
            amsos.close();
        } catch (IOException e) {
            //
        }
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public CompletionStage<?> send(OutboundSseEvent event) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            writer.writeTo(event, null, null, null, null, null, baos);
        } catch (IOException e) {
            //
        }
        return null; //???
    }
}

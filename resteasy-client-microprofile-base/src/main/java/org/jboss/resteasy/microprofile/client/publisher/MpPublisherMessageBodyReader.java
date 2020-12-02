package org.jboss.resteasy.microprofile.client.publisher;


import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.Providers;

import org.jboss.resteasy.plugins.providers.sse.SseConstants;
import org.jboss.resteasy.plugins.providers.sse.SseEventInputImpl;
import org.reactivestreams.Publisher;

@Provider
@Consumes(MediaType.SERVER_SENT_EVENTS)
public class MpPublisherMessageBodyReader implements MessageBodyReader<Publisher<?>> {
    @Context
    protected Providers providers;
    private ExecutorService executor;
    public MpPublisherMessageBodyReader(final ExecutorService  ex) {
       executor = ex;
    }
    public MpPublisherMessageBodyReader() {
        executor = Executors.newCachedThreadPool();
     }
    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Publisher.class.isAssignableFrom(type) && MediaType.SERVER_SENT_EVENTS_TYPE.isCompatible(mediaType);
    }

    @Override
    public Publisher<?> readFrom(Class<Publisher<?>> type, Type genericType, Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException {
        MediaType streamType = mediaType;
        if (mediaType.getParameters() != null) {
            Map<String, String> map = mediaType.getParameters();
            String elementType = map.get(SseConstants.SSE_ELEMENT_MEDIA_TYPE);
            if (elementType != null) {
                streamType = MediaType.valueOf(elementType);
            }
        }
        SseEventInputImpl sseEventInput = new SseEventInputImpl(annotations, streamType, mediaType, httpHeaders, entityStream);
        return new SSEPublisher<>(genericType, providers, sseEventInput, executor);
    }
}

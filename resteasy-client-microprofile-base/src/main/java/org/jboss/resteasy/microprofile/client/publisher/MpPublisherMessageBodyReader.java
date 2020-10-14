package org.jboss.resteasy.microprofile.client.publisher;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import javax.ws.rs.sse.InboundSseEvent;

import org.jboss.resteasy.plugins.providers.sse.SseConstants;
import org.jboss.resteasy.plugins.providers.sse.SseEventInputImpl;
import org.reactivestreams.Publisher;

import reactor.core.publisher.Flux;

@Provider
@Consumes(MediaType.SERVER_SENT_EVENTS)
public class MpPublisherMessageBodyReader implements MessageBodyReader<Publisher<?>> {

    @Context
    protected Providers providers;

    @Override
    public boolean isReadable(Class<?> type, Type genericType,
                              Annotation[] annotations, MediaType mediaType) {
        return Publisher.class.isAssignableFrom(type)
                && MediaType.SERVER_SENT_EVENTS_TYPE.isCompatible(mediaType);
    }

    @Override
    public Publisher<?> readFrom(Class<Publisher<?>> type, Type genericType,
                                 Annotation[] annotations, MediaType mediaType,
                                 MultivaluedMap<String, String> httpHeaders,
                                 InputStream entityStream) throws IOException, WebApplicationException {
        //ExecutorService executor = Executors.newSingleThreadExecutor();
        //ClientConfiguration clinetConfiguraiton = new ClientConfiguration(getProviderFactory());
        /*MpPublisher publisher = new MpPublisher(entityStream, executor, providers,
                annotations, mediaType, httpHeaders);*/
       MediaType streamType = mediaType;
       if (mediaType.getParameters() != null)
       {
          Map<String, String> map = mediaType.getParameters();
          String elementType = map.get(SseConstants.SSE_ELEMENT_MEDIA_TYPE);
          if (elementType != null)
          {
             streamType = MediaType.valueOf(elementType);
          }
       }
        SseEventInputImpl sseEventInput =  new SseEventInputImpl(annotations, streamType, mediaType, httpHeaders, entityStream);
        final Flux<?> flux = Flux.create(emitter -> {
            Type typeArgument = null;
            InboundSseEvent event;
            if (genericType instanceof ParameterizedType) {
                typeArgument = ((ParameterizedType)genericType).getActualTypeArguments()[0];
                if (typeArgument.equals(InboundSseEvent.class)) {
                    try {
                        while ( (event = sseEventInput.read(this.providers)) != null) {
                            emitter.next(event);
                        }
                    } catch (Exception e) {
                        emitter.error(e);
                    }
                    emitter.complete();
                } else {
                    try {
                        while ( (event = sseEventInput.read(this.providers)) != null) {
                            emitter.next(event.readData((Class)typeArgument));
                        }
                    } catch (Exception e) {
                        emitter.error(e);
                    }
                    emitter.complete();
                }
            }
        });
        return flux;
    }
}

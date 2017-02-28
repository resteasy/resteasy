package org.jboss.resteasy.plugins.providers.sse;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.sse.SseEventSink;

import org.jboss.resteasy.spi.ResteasyProviderFactory;

@Provider
@Priority(Integer.MAX_VALUE)
public class SseEventSinkInterceptor implements ContainerRequestFilter//, ContainerResponseFilter
{
   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException
   {
      if (requestContext.getAcceptableMediaTypes().contains(MediaType.SERVER_SENT_EVENTS_TYPE)) {
         SseEventOutputImpl sink = new SseEventOutputImpl(new SseEventProvider());
         ResteasyProviderFactory.getContextDataMap().put(SseEventSink.class, sink);
         sink.init();
      }
   }

}

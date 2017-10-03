package org.jboss.resteasy.plugins.providers.sse;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.sse.SseEventSink;

import org.jboss.resteasy.core.interception.jaxrs.PostMatchContainerRequestContext;
import org.jboss.resteasy.plugins.server.servlet.Cleanable;
import org.jboss.resteasy.plugins.server.servlet.Cleanables;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

@Provider
@Priority(Integer.MAX_VALUE)
public class SseEventSinkInterceptor implements ContainerRequestFilter
{
   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException
   {
      if (requestContext.getAcceptableMediaTypes().contains(MediaType.SERVER_SENT_EVENTS_TYPE)) {
         SseEventOutputImpl sink = new SseEventOutputImpl(new SseEventProvider());
         // make sure we register this as being an async method
         if(requestContext instanceof PostMatchContainerRequestContext) {
            ((PostMatchContainerRequestContext) requestContext).getResourceMethod().markMethodAsAsync();
         }
         ResteasyProviderFactory.getContextDataMap().put(SseEventSink.class, sink);
         ResteasyProviderFactory.getContextData(Cleanables.class).addCleanable(new Cleanable()
         {
            @Override
            public void clean() throws Exception
            {
               sink.flushResponseToClient();
            }
         });
      }
   }
}

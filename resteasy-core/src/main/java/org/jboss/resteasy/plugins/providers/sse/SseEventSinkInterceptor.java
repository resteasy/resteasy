package org.jboss.resteasy.plugins.providers.sse;

import java.io.IOException;

import javax.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.sse.SseEventSink;

import org.jboss.resteasy.core.PostResourceMethodInvoker;
import org.jboss.resteasy.core.PostResourceMethodInvokers;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.core.interception.jaxrs.PostMatchContainerRequestContext;
import org.jboss.resteasy.spi.Dispatcher;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

@Provider
@Priority(Integer.MAX_VALUE)
public class SseEventSinkInterceptor implements ContainerRequestFilter
{
   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException
   {
      ResourceMethodInvoker rmi = ((PostMatchContainerRequestContext) requestContext).getResourceMethod();
      if (rmi.isAsyncStreamProvider() || rmi.isSse())
      {
         Dispatcher dispatcher = ResteasyContext.getContextData(Dispatcher.class);
         ResteasyProviderFactory providerFactory = dispatcher != null ? dispatcher.getProviderFactory() : ResteasyProviderFactory.getInstance();
         SseEventOutputImpl sink = new SseEventOutputImpl(new SseEventProvider(providerFactory), providerFactory);
         ResteasyContext.getContextDataMap().put(SseEventSink.class, sink);
         ResteasyContext.getContextData(PostResourceMethodInvokers.class).addInvokers(new PostResourceMethodInvoker()
         {
            @Override
            public void invoke()
            {
               sink.flushResponseToClient();
            }
         });
      }
   }
}

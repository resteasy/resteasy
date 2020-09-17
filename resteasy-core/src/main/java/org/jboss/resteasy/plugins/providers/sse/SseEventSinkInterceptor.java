package org.jboss.resteasy.plugins.providers.sse;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.sse.SseEventSink;

import org.jboss.resteasy.annotations.Stream;
import org.jboss.resteasy.core.PostResourceMethodInvoker;
import org.jboss.resteasy.core.PostResourceMethodInvokers;
import org.jboss.resteasy.core.ResourceMethodInvoker;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.core.interception.jaxrs.PostMatchContainerRequestContext;
import org.jboss.resteasy.spi.Dispatcher;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.util.FindAnnotation;

@Provider
@Priority(Integer.MAX_VALUE)
public class SseEventSinkInterceptor implements ContainerRequestFilter
{
   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException
   {
      ResourceMethodInvoker rmi = ((PostMatchContainerRequestContext) requestContext).getResourceMethod();
      Stream stream = FindAnnotation.findAnnotation(rmi.getMethodAnnotations(), Stream.class);
      Stream.MODE mode = stream != null ? stream.value() : null;

      Dispatcher dispatcher = ResteasyContext.getContextData(Dispatcher.class);
      ResteasyProviderFactory providerFactory = dispatcher != null ? dispatcher.getProviderFactory() : ResteasyProviderFactory.getInstance();
      if ((mode == Stream.MODE.GENERAL && providerFactory.getAsyncStreamProvider(rmi.getReturnType()) != null)  ||
         requestContext instanceof PostMatchContainerRequestContext && rmi.isSse())
      {
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

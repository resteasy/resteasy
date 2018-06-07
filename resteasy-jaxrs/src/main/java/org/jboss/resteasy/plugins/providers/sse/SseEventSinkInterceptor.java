package org.jboss.resteasy.plugins.providers.sse;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.sse.SseEventSink;

import org.jboss.resteasy.annotations.Stream;
import org.jboss.resteasy.core.interception.PostMatchContainerRequestContext;
import org.jboss.resteasy.core.PostResourceMethodInvoker;
import org.jboss.resteasy.core.PostResourceMethodInvokers;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

@Provider
@Priority(Integer.MAX_VALUE)
public class SseEventSinkInterceptor implements ContainerRequestFilter
{
   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException
   {
      Method method = ((PostMatchContainerRequestContext) requestContext).getResourceMethod().getMethod();
      Stream stream = method.getAnnotation(Stream.class);
      Stream.MODE mode = stream != null ? stream.value() : null;
      Class<?> clazz = method.getReturnType();
      if ((mode == Stream.MODE.GENERAL && ResteasyProviderFactory.getInstance().getAsyncStreamProvider(clazz) != null)  ||
         requestContext instanceof PostMatchContainerRequestContext && ((PostMatchContainerRequestContext) requestContext).getResourceMethod().isSse())
      {
    	  SseEventOutputImpl sink = new SseEventOutputImpl(new SseEventProvider());
          ResteasyProviderFactory.getContextDataMap().put(SseEventSink.class, sink);
          ResteasyProviderFactory.getContextData(PostResourceMethodInvokers.class).addInvokers(new PostResourceMethodInvoker()
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

package org.jboss.resteasy.plugins.providers.sse;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.sse.SseEventSink;

import org.jboss.resteasy.core.PostResourceMethodInvoker;
import org.jboss.resteasy.core.PostResourceMethodInvokers;
import org.jboss.resteasy.core.interception.jaxrs.PostMatchContainerRequestContext;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

@Provider
@Priority(Integer.MAX_VALUE)
public class SseEventSinkInterceptor implements ContainerRequestFilter
{
   @Override
   public void filter(ContainerRequestContext requestContext) throws IOException
   {
      if (requestContext instanceof PostMatchContainerRequestContext && ((PostMatchContainerRequestContext) requestContext).getResourceMethod().isSse())
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

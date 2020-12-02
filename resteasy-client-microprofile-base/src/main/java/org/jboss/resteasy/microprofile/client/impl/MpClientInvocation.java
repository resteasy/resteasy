package org.jboss.resteasy.microprofile.client.impl;

import java.net.URI;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MultivaluedMap;

import org.eclipse.microprofile.rest.client.ext.AsyncInvocationInterceptor;
import org.eclipse.microprofile.rest.client.ext.AsyncInvocationInterceptorFactory;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.internal.ClientConfiguration;
import org.jboss.resteasy.client.jaxrs.internal.ClientInvocation;
import org.jboss.resteasy.client.jaxrs.internal.ClientRequestContextImpl;
import org.jboss.resteasy.client.jaxrs.internal.ClientRequestHeaders;
import org.jboss.resteasy.client.jaxrs.internal.ClientResponse;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.microprofile.client.async.ExecutorServiceWrapper;


public class MpClientInvocation extends ClientInvocation {

    public static final String CONTAINER_HEADERS = "MP_CLIENT_CONTAINER_HEADERS";

    private MultivaluedMap<String, String> containerHeaders;
    private List<AsyncInvocationInterceptor> asyncInvocationInterceptors;

    private ExecutorService invocationExecutor;

    protected MpClientInvocation(final ClientInvocation clientInvocation,
                                 final List<AsyncInvocationInterceptorFactory> asyncInterceptorFactories) {
        super(clientInvocation);
        captureContainerHeaders();
        captureContext(asyncInterceptorFactories);
    }

   protected MpClientInvocation(final ResteasyClient client, final URI uri, final ClientRequestHeaders headers, final ClientConfiguration parent,
                                 final List<AsyncInvocationInterceptorFactory> asyncInterceptorFactories) {
        super(client, uri, headers, parent);
        captureContainerHeaders();
        captureContext(asyncInterceptorFactories);
   }

   private void captureContext(List<AsyncInvocationInterceptorFactory> asyncInterceptorFactories)
   {
      if(asyncInterceptorFactories != null) {
         asyncInvocationInterceptors = asyncInterceptorFactories.stream().map(AsyncInvocationInterceptorFactory::newInterceptor).collect(Collectors.toList());
         asyncInvocationInterceptors.forEach(AsyncInvocationInterceptor::prepareContext);
         invocationExecutor = new ExecutorServiceWrapper(client.asyncInvocationExecutor(), new Decorator(asyncInvocationInterceptors));
      } else {
         invocationExecutor = client.asyncInvocationExecutor();
      }
   }

   @Override
   public ExecutorService asyncInvocationExecutor() {
      return invocationExecutor;
   }

    private void captureContainerHeaders() {
        HttpHeaders containerHeaders = ResteasyContext.getContextData(HttpHeaders.class);
        if(containerHeaders != null) {
            this.containerHeaders = containerHeaders.getRequestHeaders();
        }
    }

    @Override
    protected ClientResponse filterRequest(ClientRequestContextImpl requestContext) {
        if(containerHeaders != null) {
            requestContext.setProperty(CONTAINER_HEADERS, containerHeaders);
        }
        return super.filterRequest(requestContext);
    }

    public static class Decorator implements ExecutorServiceWrapper.Decorator {

       private List<AsyncInvocationInterceptor> asyncInvocationInterceptors;

       public Decorator(final List<AsyncInvocationInterceptor> asyncInvocationInterceptors) {
           this.asyncInvocationInterceptors = asyncInvocationInterceptors;
       }

        @Override
        public Runnable decorate(Runnable runnable) {
            return () -> {
                if (asyncInvocationInterceptors != null) {
                    asyncInvocationInterceptors.forEach(AsyncInvocationInterceptor::applyContext);
                }
                try {
                   runnable.run();
                } finally {
                   if (asyncInvocationInterceptors != null) {
                      asyncInvocationInterceptors.forEach(AsyncInvocationInterceptor::removeContext);
                   }
                }
            };
        }

        @Override
        public <V> Callable<V> decorate(Callable<V> callable) {
            return () -> {
                if (asyncInvocationInterceptors != null) {
                    asyncInvocationInterceptors.forEach(AsyncInvocationInterceptor::applyContext);
                }
                try {
                   return callable.call();
                } finally {
                   if (asyncInvocationInterceptors != null) {
                      asyncInvocationInterceptors.forEach(AsyncInvocationInterceptor::removeContext);
                   }
                }
            };
        }
    }
}

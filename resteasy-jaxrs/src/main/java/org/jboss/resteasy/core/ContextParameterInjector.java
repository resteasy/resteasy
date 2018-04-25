package org.jboss.resteasy.core;

import org.jboss.resteasy.plugins.providers.sse.SseImpl;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.LoggableFailure;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Providers;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
public class ContextParameterInjector implements ValueInjector
{
   private Class rawType;
   private Class proxy;
   private ResteasyProviderFactory factory;
   private Type genericType;

   public ContextParameterInjector(Class proxy, Class rawType, Type genericType, ResteasyProviderFactory factory)
   {
      this.rawType = rawType;
      this.genericType = genericType;
      this.proxy = proxy;
      this.factory = factory;
   }

   @Override
   public CompletionStage<Object> inject(HttpRequest request, HttpResponse response)
   {
      // we always inject a proxy for interface types just in case the per-request target is a pooled object
      // i.e. in the case of an SLSB
      if (rawType.equals(Providers.class)) return CompletableFuture.completedFuture(factory);
      if (!rawType.isInterface() || rawType.equals(SseEventSink.class))
      {
         return unwrapIfRequired(factory.getContextData(rawType, genericType));
      }
      else if (rawType.equals(Sse.class))
      {
         return CompletableFuture.completedFuture(new SseImpl());
      }
      // FIXME: do not proxy for CompletionStage!
      return CompletableFuture.completedFuture(createProxy());
   }

   private CompletionStage<Object> unwrapIfRequired(Object contextData)
   {
      if(rawType != CompletionStage.class && contextData instanceof CompletionStage)
         return (CompletionStage<Object>) contextData;
      return CompletableFuture.completedFuture(contextData);
   }

   private class GenericDelegatingProxy implements InvocationHandler
   {
      public Object invoke(Object o, Method method, Object[] objects) throws Throwable
      {
         try
         {
           
            Object delegate = ResteasyProviderFactory.getContextData(type);
            if (delegate == null)
            {
               String name = method.getName();
               if (o instanceof ResourceInfo && ("getResourceMethod".equals(name) || "getResourceClass".equals(name)))
               {
                  return null;
               }
              
               if ("getContextResolver".equals(name)) 
               {
                  return method.invoke(factory, objects);
               }
               throw new LoggableFailure(Messages.MESSAGES.unableToFindContextualData(rawType.getName()));
            }
            return method.invoke(delegate, objects);
         }
         catch (IllegalAccessException e)
         {
            throw new RuntimeException(e);
         }
         catch (IllegalArgumentException e)
         {
            throw new RuntimeException(e);
         }
         catch (InvocationTargetException e)
         {
            throw e.getCause();
         }
      }
   }

   @Override
   public CompletionStage<Object> inject()
   {
      //if (type.equals(Providers.class)) return factory;
      if (rawType.equals(Application.class) || rawType.equals(SseEventSink.class))
      {
         return CompletableFuture.completedFuture(factory.getContextData(rawType, genericType));
      }
      else if (rawType.equals(Sse.class))
      {
         return CompletableFuture.completedFuture(new SseImpl());
      }
      else if (!rawType.isInterface())
      {
         Object delegate = factory.getContextData(rawType, genericType);
         if (delegate != null) return unwrapIfRequired(delegate);
         throw new RuntimeException(Messages.MESSAGES.illegalToInjectNonInterfaceType());
      }

      // FIXME: do not proxy for CompletionStage!
      return CompletableFuture.completedFuture(createProxy());
   }

    protected Object createProxy()
    {
        if (proxy != null)
        {
            try
            {
                return proxy.getConstructors()[0].newInstance(new GenericDelegatingProxy());
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
        else
        {
            Class[] intfs = {rawType};
            return Proxy.newProxyInstance(rawType.getClassLoader(), intfs, new GenericDelegatingProxy());
        }
    }
}

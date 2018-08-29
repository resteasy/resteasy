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
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
public class ContextParameterInjector implements ValueInjector
{
   private Class type;
   private Class proxy;
   private ResteasyProviderFactory factory;

   public ContextParameterInjector(Class proxy, Class type, ResteasyProviderFactory factory)
   {
      this.type = type;
      this.proxy = proxy;
      this.factory = factory;
   }

   public Object inject(HttpRequest request, HttpResponse response)
   {
      // we always inject a proxy for interface types just in case the per-request target is a pooled object
      // i.e. in the case of an SLSB
      if (type.equals(Providers.class)) return factory;
      if (!type.isInterface() || type.equals(SseEventSink.class))
      {
         return ResteasyProviderFactory.getContextData(type);
      }
      else if (type.equals(Sse.class))
      {
         return new SseImpl();
      }
      return createProxy();
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
               throw new LoggableFailure(Messages.MESSAGES.unableToFindContextualData(type.getName()));
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

   public Object inject()
   {
      //if (type.equals(Providers.class)) return factory;
      if (type.equals(Application.class) || type.equals(SseEventSink.class))
      {
         return ResteasyProviderFactory.getContextData(type);
      }
      else if (type.equals(Sse.class))
      {
         return new SseImpl();
      }
      else if (!type.isInterface())
      {
         Object delegate = ResteasyProviderFactory.getContextData(type);
         if (delegate != null) return delegate;
         throw new RuntimeException(Messages.MESSAGES.illegalToInjectNonInterfaceType());
      }

      return createProxy();
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
           Class[] intfs = {type};
           ClassLoader clazzLoader = null;
           final SecurityManager sm = System.getSecurityManager();
           if (sm == null)
           {
              clazzLoader = type.getClassLoader();
           } else
           {

              clazzLoader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
                 @Override
                 public ClassLoader run() {
                    return type.getClassLoader();
                 }
              });
           }
           return Proxy.newProxyInstance(clazzLoader, intfs, new GenericDelegatingProxy());
        }
    }
}

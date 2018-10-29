package org.jboss.resteasy.client.jaxrs.internal.proxy;

import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ProxyConfig;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.i18n.Messages;
import org.jboss.resteasy.client.jaxrs.internal.proxy.ClientInvoker;
import org.jboss.resteasy.client.jaxrs.internal.proxy.ClientInvokerFactory;
import org.jboss.resteasy.client.jaxrs.internal.proxy.ClientProxy;
import org.jboss.resteasy.client.jaxrs.internal.proxy.MethodInvoker;
import org.jboss.resteasy.client.jaxrs.internal.proxy.ResteasyClientProxy;
import org.jboss.resteasy.client.jaxrs.internal.proxy.SubResourceInvoker;
import org.jboss.resteasy.util.IsHttpMethod;

import javax.ws.rs.Path;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class ProxyBuilderImpl<T> extends ProxyBuilder<T>
{
   private static final Class<?>[] cClassArgArray = {Class.class};

   private final Class<T> iface;

   private final WebTarget webTarget;

   private ClassLoader loader;

   private MediaType serverConsumes;

   private MediaType serverProduces;

   private static <T> ClientInvoker createClientInvoker(Class<T> clazz, Method method, ResteasyWebTarget base,
         ProxyConfig config)
   {
      Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
      if (httpMethods == null || httpMethods.size() != 1)
      {
         throw new RuntimeException(Messages.MESSAGES.mustUseExactlyOneHttpMethod(method.toString()));
      }
      ClientInvoker invoker = new ClientInvoker(base, clazz, method, config);
      invoker.setHttpMethod(httpMethods.iterator().next());
      return invoker;
   }

   public ProxyBuilderImpl(final Class<T> iface, final WebTarget webTarget)
   {
      if (System.getSecurityManager() == null)
      {
         this.loader = Thread.currentThread().getContextClassLoader();
      }
      else
      {
         this.loader = AccessController.doPrivileged(new PrivilegedAction<ClassLoader>()
         {
            @Override
            public ClassLoader run()
            {
               return Thread.currentThread().getContextClassLoader();
            }
         });
      }
      this.iface = iface;
      this.webTarget = webTarget;
   }

   public ProxyBuilderImpl<T> classloader(ClassLoader cl)
   {
      this.loader = cl;
      return this;
   }

   public ProxyBuilderImpl<T> defaultProduces(MediaType type)
   {
      this.serverProduces = type;
      return this;
   }

   public ProxyBuilderImpl<T> defaultConsumes(MediaType type)
   {
      this.serverConsumes = type;
      return this;
   }

   public ProxyBuilderImpl<T> defaultProduces(String type)
   {
      this.serverProduces = MediaType.valueOf(type);
      return this;
   }

   public ProxyBuilderImpl<T> defaultConsumes(String type)
   {
      this.serverConsumes = MediaType.valueOf(type);
      return this;
   }

   public T build()
   {
      return build(new ProxyConfig(loader, serverConsumes, serverProduces));
   }

   @SuppressWarnings("unchecked")
   public T build(final ProxyConfig config)
   {
      WebTarget base = webTarget;
      if (iface.isAnnotationPresent(Path.class))
      {
         Path path = iface.getAnnotation(Path.class);
         if (!path.value().equals("") && !path.value().equals("/"))
         {
            base = base.path(path.value());
         }
      }
      HashMap<Method, MethodInvoker> methodMap = new HashMap<Method, MethodInvoker>();
      for (Method method : iface.getMethods())
      {
         // ignore the as method to allow declaration in client interfaces
         if ("as".equals(method.getName()) && Arrays.equals(method.getParameterTypes(), cClassArgArray))
         {
            continue;
         }
         MethodInvoker invoker;
         Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
         if ((httpMethods == null || httpMethods.size() == 0) && method.isAnnotationPresent(Path.class)
               && method.getReturnType().isInterface())
         {
            invoker = new SubResourceInvoker((ResteasyWebTarget) base, method, config);
         }
         else if (httpMethods == null)
         {
            // ignore methods without http method annotations
            continue;
         }
         else if (base instanceof ClientInvokerFactory)
         {
            invoker = ((ClientInvokerFactory) base).createClientInvoker(iface, method, config);
         }
         else
         {
            invoker = createClientInvoker(iface, method, (ResteasyWebTarget) base, config);
         }
         methodMap.put(method, invoker);
      }

      Class<?>[] intfs = {iface, ResteasyClientProxy.class};

      ClientProxy clientProxy = new ClientProxy(methodMap, base, config);
      // this is done so that equals and hashCode work ok. Adding the proxy to a
      // Collection will cause equals and hashCode to be invoked. The Spring
      // infrastructure had some problems without this.
      clientProxy.setClazz(iface);

      return (T) Proxy.newProxyInstance(config.getLoader(), intfs, clientProxy);
   }

}

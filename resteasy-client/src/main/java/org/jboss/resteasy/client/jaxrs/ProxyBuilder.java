package org.jboss.resteasy.client.jaxrs;

import org.jboss.resteasy.client.jaxrs.i18n.Messages;
import org.jboss.resteasy.client.jaxrs.internal.proxy.ClientInvoker;
import org.jboss.resteasy.client.jaxrs.internal.proxy.ClientProxy;
import org.jboss.resteasy.client.jaxrs.internal.proxy.MethodInvoker;
import org.jboss.resteasy.client.jaxrs.internal.proxy.ResteasyClientProxy;
import org.jboss.resteasy.client.jaxrs.internal.proxy.SubResourceInvoker;
import org.jboss.resteasy.util.IsHttpMethod;

import javax.ws.rs.Path;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Set;

public class ProxyBuilder<T>
{
   private static final Class<?>[] cClassArgArray = {Class.class};

   private final Class<T> iface;
   private final ResteasyWebTarget webTarget;
   private ClassLoader loader;
   private MediaType serverConsumes;
   private MediaType serverProduces;

   public static <T> ProxyBuilder<T> builder(Class<T> iface, WebTarget webTarget)
   {
      return new ProxyBuilder<T>(iface, (ResteasyWebTarget)webTarget);
   }

   @SuppressWarnings("unchecked")
   public static <T> T proxy(final Class<T> iface, WebTarget base, final ProxyConfig config)
   {
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
         if ((httpMethods == null || httpMethods.size() == 0) && method.isAnnotationPresent(Path.class) && method.getReturnType().isInterface())
         {
            invoker = new SubResourceInvoker((ResteasyWebTarget)base, method, config);
         }
         else if (httpMethods == null) {
            // ignore methods without http method annotations
            continue;
         }
         else
         {
            invoker = createClientInvoker(iface, method, (ResteasyWebTarget)base, config);
         }
         methodMap.put(method, invoker);
      }

      Class<?>[] intfs =
      {
         iface, ResteasyClientProxy.class
      };

      ClientProxy clientProxy = new ClientProxy(methodMap, base, config);
      // this is done so that equals and hashCode work ok. Adding the proxy to a
      // Collection will cause equals and hashCode to be invoked. The Spring
      // infrastructure had some problems without this.
      clientProxy.setClazz(iface);

      ClassLoader cl = config.getLoader();
      try {
         cl.loadClass(iface.getName());
      } catch (Throwable t) {
         cl = new DelegateClassLoader(iface.getClassLoader(), cl);
      }

      return (T) Proxy.newProxyInstance(cl, intfs, clientProxy);
   }

   private static <T> ClientInvoker createClientInvoker(Class<T> clazz, Method method, ResteasyWebTarget base, ProxyConfig config)
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

   private ProxyBuilder(final Class<T> iface, final ResteasyWebTarget webTarget)
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

   public ProxyBuilder<T> classloader(ClassLoader cl)
   {
      this.loader = cl;
      return this;
   }

   public ProxyBuilder<T> defaultProduces(MediaType type)
   {
      this.serverProduces = type;
      return this;
   }

   public ProxyBuilder<T> defaultConsumes(MediaType type)
   {
      this.serverConsumes = type;
      return this;
   }

   public ProxyBuilder<T> defaultProduces(String type)
   {
      this.serverProduces = MediaType.valueOf(type);
      return this;
   }

   public ProxyBuilder<T> defaultConsumes(String type)
   {
      this.serverConsumes = MediaType.valueOf(type);
      return this;
   }
   public T build()
   {
      return proxy(iface, webTarget, new ProxyConfig(loader, serverConsumes, serverProduces));
   }


   public static class DelegateClassLoader extends SecureClassLoader
   {
      private final ClassLoader delegate;

      private final ClassLoader parent;

      public DelegateClassLoader(final ClassLoader delegate, final ClassLoader parent)
      {
         super(parent);
         this.delegate = delegate;
         this.parent = parent;
      }

      /** {@inheritDoc} */
      @Override
      public Class<?> loadClass(final String className) throws ClassNotFoundException
      {
         if (parent != null)
         {
            try
            {
               return parent.loadClass(className);
            }
            catch (ClassNotFoundException cnfe)
            {
               //NOOP, use delegate
            }
         }
         return delegate.loadClass(className);
      }

      /** {@inheritDoc} */
      @Override
      public URL getResource(final String name)
      {
         URL url = null;
         if (parent != null)
         {
            url = parent.getResource(name);
         }
         return (url == null) ? delegate.getResource(name) : url;
      }

      /** {@inheritDoc} */
      @Override
      public Enumeration<URL> getResources(final String name) throws IOException
      {
         final ArrayList<Enumeration<URL>> foundResources = new ArrayList<Enumeration<URL>>();

         foundResources.add(delegate.getResources(name));
         if (parent != null)
         {
            foundResources.add(parent.getResources(name));
         }

         return new Enumeration<URL>()
         {
            private int position = foundResources.size() - 1;

            public boolean hasMoreElements()
            {
               while (position >= 0)
               {
                  if (foundResources.get(position).hasMoreElements())
                  {
                     return true;
                  }
                  position--;
               }
               return false;
            }

            public URL nextElement()
            {
               while (position >= 0)
               {
                  try
                  {
                     return (foundResources.get(position)).nextElement();
                  }
                  catch (NoSuchElementException e)
                  {
                  }
                  position--;
               }
               throw new NoSuchElementException();
            }
         };
      }

      /** {@inheritDoc} */
      @Override
      public InputStream getResourceAsStream(final String name)
      {
         InputStream is = null;
         if (parent != null)
         {
            is = parent.getResourceAsStream(name);
         }
         return (is == null) ? delegate.getResourceAsStream(name) : is;
      }
   }

}

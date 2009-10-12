package org.jboss.resteasy.core;

import org.jboss.resteasy.core.registry.RootSegment;
import org.jboss.resteasy.plugins.server.resourcefactory.JndiResourceFactory;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.plugins.server.resourcefactory.SingletonResource;
import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;
import org.jboss.resteasy.util.IsHttpMethod;

import javax.ws.rs.Path;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * Registry of resources and methods/classes that can dispatch HTTP method requests
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResourceMethodRegistry implements Registry
{
   protected int size;

   protected ResteasyProviderFactory providerFactory;
   protected RootSegment rootSegment = new RootSegment();

   public ResourceMethodRegistry(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
   }

   public void addPerRequestResource(Class clazz, String basePath)
   {
      addResourceFactory(new POJOResourceFactory(clazz), basePath);

   }

   public void addSingletonResource(Object singleton)
   {
      addResourceFactory(new SingletonResource(singleton));
   }

   public void addSingletonResource(Object singleton, String basePath)
   {
      addResourceFactory(new SingletonResource(singleton), basePath);
   }

   public void addJndiResource(String jndiName)
   {
      addResourceFactory(new JndiResourceFactory(jndiName));
   }

   public void addJndiResource(String jndiName, String basePath)
   {
      addResourceFactory(new JndiResourceFactory(jndiName), basePath);
   }

   /**
    * Register a vanilla JAX-RS resource class
    *
    * @param clazz
    */
   public void addPerRequestResource(Class clazz)
   {
      addResourceFactory(new POJOResourceFactory(clazz));
   }

   /**
    * Bind an endpoint ResourceFactory.  ResourceFactory.getScannableClass() defines what class should be scanned
    * for JAX-RS annotations.  The class and any implemented interfaces are scanned for annotations.
    *
    * @param factory
    */
   public void addResourceFactory(ResourceFactory ref)
   {
      addResourceFactory(ref, null);
   }

   /**
    * ResourceFactory.getScannableClass() defines what class should be scanned
    * for JAX-RS annotations.    The class and any implemented interfaces are scanned for annotations.
    *
    * @param factory
    * @param base    base URI path for any resources provided by the factory, in addition to rootPath
    */
   public void addResourceFactory(ResourceFactory ref, String base)
   {
      Class<?> clazz = ref.getScannableClass();
      Class restful = GetRestful.getRootResourceClass(clazz);
      if (restful == null)
      {
         String msg = "Class is not a root resource.  It, or one of its interfaces must be annotated with @Path: " + clazz.getName() + " implements: ";
         for (Class intf : clazz.getInterfaces())
         {
            msg += " " + intf.getName();
         }
         throw new RuntimeException(msg);
      }
      addResourceFactory(ref, base, restful);
   }

   /**
    * ResourceFactory.getScannableClass() is not used, only the clazz parameter and not any implemented interfaces
    * of the clazz parameter.
    *
    * @param factory
    * @param base    base URI path for any resources provided by the factory, in addition to rootPath
    * @param clazz   specific class
    * @param offset  path segment offset.  > 0 means we're within a locator.
    */
   public void addResourceFactory(ResourceFactory ref, String base, Class<?> clazz)
   {
      if (ref != null) ref.registered(new InjectorFactoryImpl(providerFactory));
      for (Method method : clazz.getMethods())
      {
         processMethod(ref, base, clazz, method);

      }
   }

   protected void processMethod(ResourceFactory ref, String base, Class<?> clazz, Method method)
   {
      Path path = method.getAnnotation(Path.class);
      Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
      if (path == null && httpMethods == null)
      {
         if (clazz.isInterface()) return;

         Method intfMethod = null;
         for (Class intf : clazz.getInterfaces())
         {
            try
            {
               Method tmp = intf.getMethod(method.getName(), method.getParameterTypes());
               if (intfMethod != null)
                  throw new RuntimeException("Ambiguous inherited JAX-RS annotations applied to method: " + method);
               path = tmp.getAnnotation(Path.class);
               httpMethods = IsHttpMethod.getHttpMethods(tmp);
               if (path != null || httpMethods != null) intfMethod = tmp;
            }
            catch (NoSuchMethodException ignored)
            {
            }
         }
         if (intfMethod == null) return;
         processMethod(ref, base, clazz, intfMethod);
         return;
      }

      UriBuilderImpl builder = new UriBuilderImpl();
      if (base != null) builder.path(base);
      if (clazz.isAnnotationPresent(Path.class))
      {
         builder.path(clazz);
      }
      if (path != null)
      {
         builder.path(method);
      }
      String pathExpression = builder.getPath();
      if (pathExpression == null) pathExpression = "";

      InjectorFactory injectorFactory = new InjectorFactoryImpl(providerFactory);
      if (httpMethods == null)
      {
         ResourceLocator locator = new ResourceLocator(ref, injectorFactory, providerFactory, clazz, method);
         rootSegment.addPath(pathExpression, locator);
      }
      else
      {
         ResourceMethod invoker = new ResourceMethod(clazz, method, injectorFactory, ref, providerFactory, httpMethods);
         rootSegment.addPath(pathExpression, invoker);
      }
      size++;
   }

   /**
    * Find all endpoints reachable by clazz and unregister them
    *
    * @param clazz
    */
   public void removeRegistrations(Class clazz)
   {
      removeRegistrations(clazz, null);
   }

   public void removeRegistrations(Class clazz, String base)
   {
      Class restful = GetRestful.getRootResourceClass(clazz);
      removeRegistration(base, restful);
   }

   private void removeRegistration(String base, Class<?> clazz)
   {
      for (Method method : clazz.getMethods())
      {
         Path path = method.getAnnotation(Path.class);
         Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
         if (path == null && httpMethods == null) continue;

         UriBuilderImpl builder = new UriBuilderImpl();
         if (base != null) builder.path(base);
         if (clazz.isAnnotationPresent(Path.class)) builder.path(clazz);
         if (path != null) builder.path(method);
         String pathExpression = builder.getPath();
         if (pathExpression == null) pathExpression = "";

         ResourceInvoker invoker = rootSegment.removePath(pathExpression, method);
         if (invoker != null)
         {
            size--;
            if (invoker instanceof ResourceMethod)
            {
               ((ResourceMethod) invoker).cleanup();
            }
         }
      }
   }

   public RootSegment getRoot()
   {
      return rootSegment;
   }


   /**
    * Number of endpoints registered
    *
    * @return
    */
   public int getSize()
   {
      return size;
   }

   /**
    * Find a resource to invoke on
    *
    * @param httpMethod  GET, POST, PUT, OPTIONS, TRACE, etc...
    * @param path        uri path
    * @param contentType produced type
    * @param accepts     accept header
    * @return
    */
   public ResourceInvoker getResourceInvoker(HttpRequest request, HttpResponse response)
   {
      List<String> matchedUris = request.getUri().getMatchedURIs(false);
      if (matchedUris == null || matchedUris.size() == 0) return rootSegment.matchRoot(request);
      // resource location 
      String currentUri = request.getUri().getMatchedURIs(false).get(0);
      return rootSegment.matchRoot(request, currentUri.length());
   }
}

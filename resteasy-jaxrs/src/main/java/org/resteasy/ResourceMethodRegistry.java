package org.resteasy;

import org.resteasy.plugins.server.resourcefactory.JndiResourceFactory;
import org.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.resteasy.plugins.server.resourcefactory.SingletonResource;
import org.resteasy.specimpl.UriBuilderImpl;
import org.resteasy.spi.InjectorFactory;
import org.resteasy.spi.Registry;
import org.resteasy.spi.ResourceFactory;
import org.resteasy.spi.ResteasyProviderFactory;
import org.resteasy.util.GetRestful;
import org.resteasy.util.IsHttpMethod;

import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.PathSegment;
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
   private int size;

   private PathSegmentNode root = new PathSegmentNode();
   private ResteasyProviderFactory providerFactory;

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
    * @param base    base URI path for any resources provided by the factory
    */
   public void addResourceFactory(ResourceFactory ref, String base)
   {
      Class<?> clazz = ref.getScannableClass();
      List<Class> restful = GetRestful.getRestfulClasses(clazz);
      if (restful == null)
      {
         String msg = "Class is not a root resource.  It, or one of its interfaces must be annotated with @Path: " + clazz.getName() + " implements: ";
         for (Class intf : clazz.getInterfaces())
         {
            msg += " " + intf.getName();
         }
         throw new RuntimeException(msg);
      }
      for (Class cls : restful) addResourceFactory(ref, base, cls);
   }

   /**
    * ResourceFactory.getScannableClass() is not used, only the clazz parameter and not any implemented interfaces
    * of the clazz parameter.
    *
    * @param factory
    * @param base    base URI path for any resources provided by the factory
    * @param clazz   specific class
    */
   protected void addResourceFactory(ResourceFactory ref, String base, Class<?> clazz)
   {
      ref.registered(new InjectorFactoryImpl(null, providerFactory));
      for (Method method : clazz.getMethods())
      {
         Path path = method.getAnnotation(Path.class);
         Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
         if (path == null && httpMethods == null) continue;

         UriBuilderImpl builder = new UriBuilderImpl();
         builder.setPath(base);
         if (clazz.isAnnotationPresent(Path.class)) builder.path(clazz);
         if (path != null) builder.path(method);
         String pathExpression = builder.getPath();
         if (pathExpression == null) pathExpression = "";

         PathParamIndex index = new PathParamIndex(pathExpression);
         InjectorFactory injectorFactory = new InjectorFactoryImpl(new PathParamIndex(pathExpression), providerFactory);
         if (httpMethods == null)
         {
            ResourceLocator locator = new ResourceLocator(ref, injectorFactory, providerFactory, method, index);
            addResourceFactory(locator, pathExpression, locator.getScannableClass());
         }
         else
         {
            ResourceMethod invoker = new ResourceMethod(clazz, method, injectorFactory, ref, providerFactory, httpMethods, index);
            if (pathExpression.startsWith("/")) pathExpression = pathExpression.substring(1);
            String[] paths = pathExpression.split("/");
            root.addChild(paths, 0, invoker);
            size++;
         }

      }
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
      List<Class> restful = GetRestful.getRestfulClasses(clazz);
      for (Class cls : restful) removeRegistration(base, cls);
   }

   private void removeRegistration(String base, Class<?> clazz)
   {
      for (Method method : clazz.getMethods())
      {
         Path path = method.getAnnotation(Path.class);
         Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
         if (path == null && httpMethods == null) continue;

         UriBuilderImpl builder = new UriBuilderImpl();
         builder.setPath(base);
         if (clazz.isAnnotationPresent(Path.class)) builder.path(clazz);
         if (path != null) builder.path(method);
         String pathExpression = builder.getPath();
         if (pathExpression == null) pathExpression = "";

         if (httpMethods == null)
         {
            removeRegistrations(method.getReturnType(), pathExpression);
         }
         else
         {
            if (pathExpression.startsWith("/")) pathExpression = pathExpression.substring(1);
            String[] paths = pathExpression.split("/");
            try
            {
               if (root.removeChild(paths, 0, method) != null) size--;
            }
            catch (Exception e)
            {
               throw new RuntimeException("pathExpression: " + pathExpression, e);
            }

         }

      }
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
   public ResourceMethod getResourceInvoker(String httpMethod, List<PathSegment> path, MediaType contentType, List<MediaType> accepts)
   {
      return root.findResourceInvoker(httpMethod, path, 0, contentType, accepts);
   }
}

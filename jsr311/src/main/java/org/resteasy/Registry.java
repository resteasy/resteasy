package org.resteasy;

import org.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.resteasy.spi.ResourceFactory;
import org.resteasy.spi.ResteasyProviderFactory;
import org.resteasy.util.GetRestful;
import org.resteasy.util.IsHttpMethod;

import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Registry
{
   private int size;

   private PathSegmentNode root = new PathSegmentNode();
   private ResteasyProviderFactory providerFactory;

   public Registry(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
   }

   /**
    * Register a vanilla JAX-RS resource class
    *
    * @param clazz
    */
   public void addResource(Class clazz)
   {
      addResourceFactory(new POJOResourceFactory(clazz));
   }

   /**
    * Bind an endpoint ResourceFactory.  ResourceFactory.getScannableClass() defines what class should be scanned
    * for JAX-RS annotations.  The class and any implemented interfaces are scanned for annotations.
    *
    * @param factory
    */
   public void addResourceFactory(ResourceFactory factory)
   {
      addResourceFactory(factory, null);
   }

   /**
    * ResourceFactory.getScannableClass() defines what class should be scanned
    * for JAX-RS annotations.    The class and any implemented interfaces are scanned for annotations.
    *
    * @param factory
    * @param base    base URI path for any resources provided by the factory
    */
   public void addResourceFactory(ResourceFactory factory, String base)
   {
      Class<?> clazz = factory.getScannableClass();
      List<Class> restful = GetRestful.getRestfulClasses(clazz);
      for (Class cls : restful) addResourceFactory(factory, base, cls);
   }

   /**
    * ResourceFactory.getScannableClass() is not used, only the clazz parameter and not any implemented interfaces
    * of the clazz parameter.
    *
    * @param factory
    * @param base    base URI path for any resources provided by the factory
    * @param clazz   specific class
    */
   public void addResourceFactory(ResourceFactory factory, String base, Class<?> clazz)
   {
      Path classBasePath = clazz.getAnnotation(Path.class);
      String classBase = (classBasePath == null) ? null : classBasePath.value();
      if (base == null) base = classBase;
      else if (classBase != null) base = base + "/" + classBase;

      for (Method method : clazz.getMethods())
      {
         Path path = method.getAnnotation(Path.class);
         Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
         if (path == null && httpMethods == null) continue;

         String pathExpression = null;
         if (base != null) pathExpression = base;
         if (path != null)
            pathExpression = (pathExpression == null) ? path.value() : pathExpression + "/" + path.value();
         if (pathExpression == null) pathExpression = "";
         if (httpMethods == null)
         {
            ResourceLocator locator = new ResourceLocator(pathExpression, factory, method, providerFactory);
            addResourceFactory(locator, pathExpression);
         }
         else
         {
            ResourceMethod invoker = new ResourceMethod(pathExpression, clazz, method, factory, providerFactory, httpMethods);
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
      Path classBasePath = clazz.getAnnotation(Path.class);
      String classBase = (classBasePath == null) ? null : classBasePath.value();
      if (base == null) base = classBase;
      else if (classBase != null) base = base + "/" + classBase;

      for (Method method : clazz.getMethods())
      {
         Path path = method.getAnnotation(Path.class);
         Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
         if (path == null && httpMethods == null) continue;

         String pathExpression = null;
         if (base != null) pathExpression = base;
         if (path != null)
            pathExpression = (pathExpression == null) ? path.value() : pathExpression + "/" + path.value();
         if (pathExpression == null) pathExpression = "";
         if (httpMethods == null)
         {
            removeRegistrations(method.getReturnType(), pathExpression);
         }
         else
         {
            String[] paths = pathExpression.split("/");
            if (root.removeChild(paths, 0, method) != null) size--;

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
   public ResourceMethod getResourceInvoker(String httpMethod, String path, MediaType contentType, List<MediaType> accepts)
   {
      if (path.startsWith("/")) path = path.substring(1);
      return root.findResourceInvoker(httpMethod, path.split("/"), 0, contentType, accepts);
   }
}

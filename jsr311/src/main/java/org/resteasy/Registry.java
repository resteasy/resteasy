package org.resteasy;

import org.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.resteasy.spi.ResourceFactory;
import org.resteasy.spi.ResteasyProviderFactory;
import org.resteasy.util.GetRestful;
import org.resteasy.util.IsHttpMethod;
import org.resteasy.util.PathHelper;

import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Registry
{
   private int size;

   public class Node
   {
      private List<ResourceMethod> invokers = new ArrayList<ResourceMethod>();
      private List<Node> uriParamChildren = new ArrayList<Node>();
      private Map<String, Node> children = new HashMap<String, Node>();
      private Node wildcard;

      public Node()
      {
      }

      public void addChild(String[] path, int pathIndex, ResourceMethod invoker)
      {
         Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(path[pathIndex]);
         if (matcher.matches())
         {
            Node child = new Node();
            uriParamChildren.add(child);
            if (path.length == pathIndex + 1)
            {
               child.invokers.add(invoker);
            }
            else
            {
               child.addChild(path, ++pathIndex, invoker);
            }
         }
         else if (path[pathIndex].trim().equals("*"))
         {
            if (wildcard == null) wildcard = new Node();
            wildcard.invokers.add(invoker);
         }
         else
         {
            Node child = children.get(path[pathIndex]);
            if (child == null)
            {
               child = new Node();
               children.put(path[pathIndex], child);
            }
            if (path.length == pathIndex + 1)
            {
               child.invokers.add(invoker);
            }
            else
            {
               child.addChild(path, ++pathIndex, invoker);
            }
         }
      }

      public ResourceMethod removeChild(String[] path, int pathIndex, Method method)
      {
         Matcher matcher = PathHelper.URI_TEMPLATE_PATTERN.matcher(path[pathIndex]);
         if (matcher.matches())
         {
            if (path.length == pathIndex + 1)
            {
               for (Node child : uriParamChildren)
               {
                  ResourceMethod rm = tryRemoveInvoker(child.invokers, method);
                  if (rm != null) return rm;
               }
               return null;
            }
            else
            {
               for (Node child : uriParamChildren)
               {
                  ResourceMethod rm = child.removeChild(path, ++pathIndex, method);
                  if (rm != null) return rm;
               }
               return null;
            }
         }
         else if (path[pathIndex].trim().equals("*"))
         {
            if (wildcard == null) return null;
            return tryRemoveInvoker(wildcard.invokers, method);
         }
         else
         {
            Node child = children.get(path[pathIndex]);
            if (path.length == pathIndex + 1)
            {
               return tryRemoveInvoker(child.invokers, method);
            }
            else
            {
               return child.removeChild(path, ++pathIndex, method);
            }
         }
      }

      private ResourceMethod tryRemoveInvoker(List<ResourceMethod> invokers, Method method)
      {
         List<ResourceMethod> copy = new ArrayList<ResourceMethod>();
         copy.addAll(invokers);
         for (ResourceMethod rm : copy)
         {
            if (method.equals(rm.getMethod())) invokers.remove(rm);
            return rm;
         }
         return null;
      }

      public ResourceMethod findResourceInvoker(String httpMethod, String[] path, int pathIndex, MediaType contentType, List<MediaType> accepts)
      {
         if (pathIndex >= path.length) return match(httpMethod, contentType, accepts);
         else return findChild(httpMethod, path, pathIndex, contentType, accepts);
      }

      private ResourceMethod findChild(String httpMethod, String[] path, int pathIndex, MediaType contentType, List<MediaType> accepts)
      {
         Node next = children.get(path[pathIndex]);
         if (next != null)
         {
            ResourceMethod method = next.findResourceInvoker(httpMethod, path, ++pathIndex, contentType, accepts);
            if (method != null) return method;
         }
         if (uriParamChildren != null)
         {
            for (Node wildcard : uriParamChildren)
            {
               ResourceMethod wildcardReturn = wildcard.findResourceInvoker(httpMethod, path, ++pathIndex, contentType, accepts);
               if (wildcardReturn != null) return wildcardReturn;
            }
         }
         if (wildcard != null)
         {
            return wildcard.match(httpMethod, contentType, accepts);
         }
         return null;
      }

      private ResourceMethod match(String httpMethod, MediaType contentType, List<MediaType> accepts)
      {
         for (ResourceMethod invoker : invokers)
         {
            if (invoker.matchByType(contentType, accepts) && invoker.getHttpMethods().contains(httpMethod))
               return invoker;
         }
         return null;
      }


   }

   private Node root = new Node();
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

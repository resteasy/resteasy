package org.jboss.resteasy.core;

import org.jboss.resteasy.core.registry.RootSegment;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.plugins.server.resourcefactory.JndiResourceFactory;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.plugins.server.resourcefactory.SingletonResource;
import org.jboss.resteasy.specimpl.ResteasyUriBuilder;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.NotFoundException;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.ResourceBuilder;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.metadata.ResourceLocator;
import org.jboss.resteasy.spi.metadata.ResourceMethod;
import org.jboss.resteasy.util.GetRestful;
import org.jboss.resteasy.util.IsHttpMethod;
import org.jboss.resteasy.util.Types;

import javax.ws.rs.Path;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
   protected RootSegment resourceMethodRoot = new RootSegment();
   protected RootSegment resourceLocatorRoot = new RootSegment();

   private final static Logger logger = Logger.getLogger(ResourceMethodRegistry.class);


   public ResourceMethodRegistry(ResteasyProviderFactory providerFactory)
   {
      this.providerFactory = providerFactory;
   }

   public void addPerRequestResource(Class clazz, String basePath)
   {
      addResourceFactory(new POJOResourceFactory(clazz), basePath);

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

   @Override
   public void addPerRequestResource(ResourceClass clazz)
   {
      POJOResourceFactory resourceFactory = new POJOResourceFactory(clazz);
      register(resourceFactory, null, clazz);
      if (resourceFactory != null) resourceFactory.registered(providerFactory);
   }

   @Override
   public void addPerRequestResource(ResourceClass clazz, String basePath)
   {
      POJOResourceFactory resourceFactory = new POJOResourceFactory(clazz);
      register(resourceFactory, basePath, clazz);
      if (resourceFactory != null) resourceFactory.registered(providerFactory);
   }

   public void addSingletonResource(Object singleton)
   {
      addResourceFactory(new SingletonResource(singleton));
   }

   public void addSingletonResource(Object singleton, String basePath)
   {
      addResourceFactory(new SingletonResource(singleton), basePath);
   }

   @Override
   public void addSingletonResource(Object singleton, ResourceClass resourceClass)
   {
      SingletonResource resourceFactory = new SingletonResource(singleton, resourceClass);
      register(resourceFactory, null, resourceClass);
      if (resourceFactory != null) resourceFactory.registered(providerFactory);
   }

   @Override
   public void addSingletonResource(Object singleton, ResourceClass resourceClass, String basePath)
   {
      SingletonResource resourceFactory = new SingletonResource(singleton);
      register(resourceFactory, basePath, resourceClass);
      if (resourceFactory != null) resourceFactory.registered(providerFactory);
   }


   public void addJndiResource(String jndiName)
   {
      addResourceFactory(new JndiResourceFactory(jndiName));
   }

   public void addJndiResource(String jndiName, String basePath)
   {
      addResourceFactory(new JndiResourceFactory(jndiName), basePath);
   }

   @Override
   public void addJndiResource(String jndiName, ResourceClass resourceClass)
   {
      JndiResourceFactory resourceFactory = new JndiResourceFactory(jndiName);
      register(resourceFactory, null, resourceClass);
      if (resourceFactory != null) resourceFactory.registered(providerFactory);
   }

   @Override
   public void addJndiResource(String jndiName, ResourceClass resourceClass, String basePath)
   {
      JndiResourceFactory resourceFactory = new JndiResourceFactory(jndiName);
      register(resourceFactory, basePath, resourceClass);
      if (resourceFactory != null) resourceFactory.registered(providerFactory);
   }




   /**
    * Bind an endpoint ResourceFactory.  ResourceFactory.getScannableClass() defines what class should be scanned
    * for JAX-RS annotations.  The class and any implemented interfaces are scanned for annotations.
    *
    * @param ref
    */
   public void addResourceFactory(ResourceFactory ref)
   {
      addResourceFactory(ref, null);
   }

   /**
    * ResourceFactory.getScannableClass() defines what class should be scanned
    * for JAX-RS annotations.    The class and any implemented interfaces are scanned for annotations.
    *
    * @param ref
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
    * @param ref
    * @param base    base URI path for any resources provided by the factory, in addition to rootPath
    * @param clazz   specific class
    */
   public void addResourceFactory(ResourceFactory ref, String base, Class<?> clazz)
   {
      Class<?>[] classes = {clazz};
      addResourceFactory(ref, base, classes);
      if (ref != null) ref.registered(providerFactory);
   }
   
   /**
    * ResourceFactory.getScannableClass() is not used, only the clazz parameter and not any implemented interfaces
    * of the clazz parameter.
    *
    * @param ref
    * @param base    base URI path for any resources provided by the factory, in addition to rootPath
    * @param classes   specific class
    */
   public void addResourceFactory(ResourceFactory ref, String base, Class<?>[] classes)
   {
      if (ref != null) ref.registered(providerFactory);
      for (Class<?> clazz: classes)
      {
         if (Proxy.isProxyClass(clazz))
         {
            for (Class<?> intf : clazz.getInterfaces())
            {
               ResourceClass resourceClass = ResourceBuilder.rootResourceFromAnnotations(intf);
               register(ref, base, resourceClass);
            }
         }
         else
         {
            ResourceClass resourceClass = ResourceBuilder.rootResourceFromAnnotations(clazz);
            register(ref, base, resourceClass);
         }
      }
      
      // https://issues.jboss.org/browse/JBPAPP-7871
      for (Class<?> clazz: classes)
      {
         for (Method method : clazz.getDeclaredMethods()) {
            Method _method = findAnnotatedMethod(clazz, method);
            if (_method != null && !java.lang.reflect.Modifier.isPublic(_method.getModifiers())) {
               logger.warn("JAX-RS annotations found at non-public method: " + method.getDeclaringClass().getName() + "." + method.getName() + "(); Only public methods may be exposed as resource methods.");
            }
         }
      }

   }

   @Override
   public void addResourceFactory(ResourceFactory rf, String base, ResourceClass resourceClass)
   {
      if (rf != null) rf.registered(providerFactory);
      register(rf, base, resourceClass);
   }

   protected void register(ResourceFactory rf, String base, ResourceClass resourceClass)
   {
      for (ResourceMethod method : resourceClass.getResourceMethods())
      {
         processMethod(rf, base, method);
      }
      for (ResourceLocator method : resourceClass.getResourceLocators())
      {
         processMethod(rf, base, method);
      }
   }

   protected void processMethod(ResourceFactory rf, String base, ResourceLocator method)
   {
      ResteasyUriBuilder builder = new ResteasyUriBuilder();
      if (base != null)
         builder.path(base);
      builder.path(method.getPath());
      String pathExpression = builder.getPath();
      if (pathExpression == null)
         pathExpression = "";

      InjectorFactory injectorFactory = providerFactory.getInjectorFactory();
      if (method instanceof ResourceMethod)
      {
         ResourceMethodInvoker invoker = new ResourceMethodInvoker((ResourceMethod)method, injectorFactory, rf, providerFactory);
         resourceMethodRoot.addPath(pathExpression, invoker);
      }
      else
      {
         ResourceLocatorInvoker locator = new ResourceLocatorInvoker(rf, injectorFactory, providerFactory, method);
         resourceLocatorRoot.addPath(pathExpression, locator);
      }
      size++;
   }

	private Method findAnnotatedInterfaceMethod(Class<?> root, Class<?> iface, Method implementation)
	{
      for (Method method : iface.getMethods())
      {
         if (method.isSynthetic()) continue;

         if (!method.getName().equals(implementation.getName())) continue;
         if (method.getParameterTypes().length != implementation.getParameterTypes().length) continue;

         Method actual = Types.getImplementingMethod(root, method);
         if (!actual.equals(implementation)) continue;

         if (method.isAnnotationPresent(Path.class) || IsHttpMethod.getHttpMethods(method) != null)
            return method;

      }
		for (Class<?> extended : iface.getInterfaces())
		{
			Method m = findAnnotatedInterfaceMethod(root, extended, implementation);
			if(m != null)
				return m;
		}
		return null;
	}

	private Method findAnnotatedMethod(Class<?> root, Method implementation)
	{
      // check the method itself
      if (implementation.isAnnotationPresent(Path.class) || IsHttpMethod.getHttpMethods(implementation) != null)
         return implementation;

		// Per http://download.oracle.com/auth/otn-pub/jcp/jaxrs-1.0-fr-oth-JSpec/jaxrs-1.0-final-spec.pdf
		// Section 3.2 Annotation Inheritance

		// Check possible superclass declarations
		for (Class<?> clazz = implementation.getDeclaringClass().getSuperclass(); clazz != null; clazz = clazz.getSuperclass())
		{
			try
			{
				Method method = clazz.getDeclaredMethod(implementation.getName(), implementation.getParameterTypes());
				if (method.isAnnotationPresent(Path.class) || IsHttpMethod.getHttpMethods(method) != null)
					return method;
			}
			catch (NoSuchMethodException e)
			{
				// ignore
			}
		}

		// Not found yet, so next check ALL interfaces from the root, 
		// but ensure no redefinition by peer interfaces (ambiguous) to preserve logic found in 
		// original implementation
		for (Class<?> clazz = root; clazz != null; clazz = clazz.getSuperclass())
		{
			Method method = null;
			for (Class<?> iface : clazz.getInterfaces())
			{
				Method m = findAnnotatedInterfaceMethod(root, iface, implementation);
				if (m != null)
				{
					if(method != null && !m.equals(method))
						throw new RuntimeException("Ambiguous inherited JAX-RS annotations applied to method: " + implementation);
					method = m;
				}
			}
			if (method != null)
				return method;
		}
		return null;
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

   @Override
   public void removeRegistrations(ResourceClass resourceClass)
   {
      for (ResourceMethod method : resourceClass.getResourceMethods())
      {
         removeBinding(method.getMethod(), method.getPath());
      }
      for (ResourceLocator method : resourceClass.getResourceLocators())
      {
         removeBinding(method.getMethod(), method.getPath());
      }
   }

   private void removeRegistration(String base, Class<?> clazz)
   {
      for (Method method : clazz.getMethods())
      {
         Path path = method.getAnnotation(Path.class);
         Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);
         if (path == null && httpMethods == null) continue;

         ResteasyUriBuilder builder = new ResteasyUriBuilder();
         if (base != null) builder.path(base);
         if (clazz.isAnnotationPresent(Path.class)) builder.path(clazz);
         if (path != null) builder.path(method);
         String pathExpression = builder.getPath();
         if (pathExpression == null) pathExpression = "";

         removeBinding(method, pathExpression);
      }
   }

   private void removeBinding(Method method, String pathExpression)
   {
      ResourceInvoker invoker = resourceMethodRoot.removePath(pathExpression, method);
      if (invoker == null) invoker = resourceLocatorRoot.removePath(pathExpression, method);
      if (invoker != null)
      {
         size--;
         if (invoker instanceof ResourceMethodInvoker)
         {
            ((ResourceMethodInvoker) invoker).cleanup();
         }
      }
   }
   public Map<String, List<ResourceInvoker>> getBounded()
   {
      Map<String, List<ResourceInvoker>> bounded = new LinkedHashMap<String, List<ResourceInvoker>>();
      for (Map.Entry<String, List<ResourceInvoker>> entry : resourceMethodRoot.getBounded().entrySet())
      {
         List<ResourceInvoker> addTo = bounded.get(entry.getKey());
         if (addTo == null)
         {
            addTo = new ArrayList<ResourceInvoker>();
            bounded.put(entry.getKey(), addTo);
         }
         addTo.addAll(entry.getValue());
      }
      for (Map.Entry<String, List<ResourceInvoker>> entry : resourceLocatorRoot.getBounded().entrySet())
      {
         List<ResourceInvoker> addTo = bounded.get(entry.getKey());
         if (addTo == null)
         {
            addTo = new ArrayList<ResourceInvoker>();
            bounded.put(entry.getKey(), addTo);
         }
         addTo.addAll(entry.getValue());
      }
      return bounded;
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
    * @return
    */
   public ResourceInvoker getResourceInvoker(HttpRequest request)
   {
      try
      {
         List<String> matchedUris = request.getUri().getEncodedMatchedPaths();
         if (matchedUris == null || matchedUris.size() == 0)
         {
            try
            {
               return resourceMethodRoot.matchRoot(request);
            }
            catch (NotFoundException e)
            {
               return resourceLocatorRoot.matchRoot(request);
            }
         }
         // resource location
         String currentUri = request.getUri().getEncodedMatchedPaths().get(0);
         try
         {
            return resourceMethodRoot.matchRoot(request, currentUri.length());
         }
         catch (NotFoundException e)
         {
            return resourceLocatorRoot.matchRoot(request, currentUri.length());
         }
      }
      catch (RuntimeException e)
      {
         e.printStackTrace();
         throw e;
      }
   }
}

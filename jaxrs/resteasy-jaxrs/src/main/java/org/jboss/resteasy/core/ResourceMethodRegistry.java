package org.jboss.resteasy.core;

import org.jboss.resteasy.core.registry.RootSegment;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.plugins.server.resourcefactory.JndiResourceFactory;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.plugins.server.resourcefactory.SingletonResource;
import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GetRestful;
import org.jboss.resteasy.util.IsHttpMethod;
import org.jboss.resteasy.util.Types;

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

   private final static Logger logger = Logger.getLogger(ResourceMethodRegistry.class);


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
      if (ref != null) ref.registered(providerFactory);
      for (Method method : clazz.getMethods())
      {
    	  if(!method.isSynthetic())
    		  processMethod(ref, base, clazz, method);

      }

      // https://issues.jboss.org/browse/JBPAPP-7871
      for (Method method : clazz.getDeclaredMethods()) {
           Method _method = findAnnotatedMethod(clazz, method);
          if (_method != null && !java.lang.reflect.Modifier.isPublic(_method.getModifiers())) {
                  logger.warn("JAX-RS annotations found at non-public method: " + method.getDeclaringClass().getName() + "." + method.getName() + "(); Only public methods may be exposed as resource methods.");
          }
      }

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
   public void addResourceFactory(ResourceFactory ref, String base, Class<?>[] classes)
   {
      if (ref != null) ref.registered(providerFactory);
      for (Class<?> clazz: classes)
      {
         for (Method method : clazz.getMethods())
         {
            if(!method.isSynthetic())
               processMethod(ref, base, clazz, method);

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

	protected void processMethod(ResourceFactory ref, String base, Class<?> clazz, Method implementation)
	{
		Method method = findAnnotatedMethod(clazz, implementation);
		if (method != null)
		{
			Path path = method.getAnnotation(Path.class);
			Set<String> httpMethods = IsHttpMethod.getHttpMethods(method);

			UriBuilderImpl builder = new UriBuilderImpl();
			if (base != null)
				builder.path(base);
			if (clazz.isAnnotationPresent(Path.class))
			{
				builder.path(clazz);
			}
			if (path != null)
			{
				builder.path(method);
			}
			String pathExpression = builder.getPath();
			if (pathExpression == null)
				pathExpression = "";

			InjectorFactory injectorFactory = providerFactory.getInjectorFactory();
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
    * @return
    */
   public ResourceInvoker getResourceInvoker(HttpRequest request)
   {
      List<String> matchedUris = request.getUri().getMatchedURIs(false);
      if (matchedUris == null || matchedUris.size() == 0) return rootSegment.matchRoot(request);
      // resource location 
      String currentUri = request.getUri().getMatchedURIs(false).get(0);
      return rootSegment.matchRoot(request, currentUri.length());
   }
}

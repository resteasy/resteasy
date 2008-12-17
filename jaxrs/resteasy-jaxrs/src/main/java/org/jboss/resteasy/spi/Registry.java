package org.jboss.resteasy.spi;

import org.jboss.resteasy.core.ResourceInvoker;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface Registry
{
   /**
    * Add a JAX-RS endpoint.  Objects of clazz will be created and destroy and the beginning/end of every request
    *
    * @param clazz
    */
   void addPerRequestResource(Class<?> clazz);

   /**
    * Add a JAX-RS endpoint.  Objects of clazz will be created and destroy and the beginning/end of every request
    *
    * @param clazz
    * @param basePath prefix path of resource
    */
   void addPerRequestResource(Class<?> clazz, String basePath);


   /**
    * Add a JAX-RS endpoint.
    *
    * @param clazz
    */
   void addSingletonResource(Object singleton);

   /**
    * Add a JAX-RS endpoint.
    *
    * @param clazz
    * @param basePath prefix path of resource
    */
   void addSingletonResource(Object singleton, String basePath);

   /**
    * Add a JAX-RS endpoint that exists in JNDI
    *
    * @param clazz
    */
   void addJndiResource(String jndiName);

   /**
    * Add a JAX-RS endpoint that exists in JNDI.
    *
    * @param clazz
    * @param basePath prefix path of resource
    */
   void addJndiResource(String jndiName, String basePath);

   /**
    * Add a custom resource implementation endpoint.
    *
    * @param ref
    */
   void addResourceFactory(ResourceFactory ref);

   /**
    * Add a custom resource implementation endpoint.
    *
    * @param ref
    * @param basePath prefix path of resource
    */
   void addResourceFactory(ResourceFactory ref, String basePath);

   /**
    * ResourceFactory.getScannableClass() is not used, only the clazz parameter and not any implemented interfaces
    * of the clazz parameter.
    *
    * @param factory
    * @param base    base URI path for any resources provided by the factory, in addition to rootPath
    * @param clazz   specific class
    * @param offset  path segment offset.  > 0 means we're within a locator.
    */
   void addResourceFactory(ResourceFactory ref, String base, Class<?> clazz);

   void removeRegistrations(Class<?> clazz);

   void removeRegistrations(Class<?> clazz, String base);

   int getSize();

   ResourceInvoker getResourceInvoker(HttpRequest request, HttpResponse response);
}

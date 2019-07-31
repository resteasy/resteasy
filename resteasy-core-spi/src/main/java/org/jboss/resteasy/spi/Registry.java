package org.jboss.resteasy.spi;

import org.jboss.resteasy.spi.metadata.ResourceBuilder;
import org.jboss.resteasy.spi.metadata.ResourceClass;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface Registry
{
   /**
    * Add a JAX-RS endpoint.  Objects of clazz will be created and destroy and the beginning/end of every request.
    *
    * @param clazz class
    */
   void addPerRequestResource(Class<?> clazz);

   default void addPerRequestResource(Class<?> clazz, ResourceBuilder resourceBuilder) {
      throw new IllegalStateException("Not implemented");
   }

   /**
    * Add a JAX-RS endpoint.  Objects of clazz will be created and destroy and the beginning/end of every request.
    *
    * @param clazz class
    * @param basePath prefix path of resource
    */
   void addPerRequestResource(Class<?> clazz, String basePath);


   /**
    * Add a JAX-RS endpoint.
    *
    * @param singleton resource
    */
   void addSingletonResource(Object singleton);

   /**
    * Add a JAX-RS endpoint.
    *
    * @param singleton resource
    * @param basePath prefix path of resource
    */
   void addSingletonResource(Object singleton, String basePath);

   /**
    * Add a JAX-RS endpoint that exists in JNDI.
    *
    * @param jndiName JNDI name
    */
   void addJndiResource(String jndiName);

   /**
    * Add a JAX-RS endpoint that exists in JNDI.
    *
    * @param jndiName JNDI name
    * @param basePath prefix path of resource
    */
   void addJndiResource(String jndiName, String basePath);

   /**
    * Add a custom resource implementation endpoint.
    *
    * @param ref resource factory
    */
   void addResourceFactory(ResourceFactory ref);

   default void addResourceFactory(ResourceFactory ref, ResourceBuilder resourceBuilder) {
      throw new IllegalStateException("Not implemented");
   }

   /**
    * Add a custom resource implementation endpoint.
    *
    * @param ref resource factory
    * @param basePath prefix path of resource
    */
   void addResourceFactory(ResourceFactory ref, String basePath);

   default void addResourceFactory(ResourceFactory ref, ResourceBuilder resourceBuilder, String basePath) {
      throw new IllegalStateException("Not implemented");
   }

   /**
    * ResourceFactory.getScannableClass() is not used, only the clazz parameter and not any implemented interfaces
    * of the clazz parameter.
    *
    * @param ref resource factory
    * @param base    base URI path for any resources provided by the factory, in addition to rootPath
    * @param clazz   specific class
    */
   void addResourceFactory(ResourceFactory ref, String base, Class<?> clazz);

   default void addResourceFactory(ResourceFactory ref, ResourceBuilder resourceBuilder, String base, Class<?> clazz) {
      throw new IllegalStateException("Not implemented");
   }

   void addResourceFactory(ResourceFactory ref, String base, Class<?>[] classes);

   default void addResourceFactory(ResourceFactory ref, ResourceBuilder resourceBuilder, String base, Class<?>[] classes) {
      throw new IllegalStateException("Not implemented");
   }

   void removeRegistrations(Class<?> clazz);

   void removeRegistrations(Class<?> clazz, String base);

   int getSize();

   ResourceInvoker getResourceInvoker(HttpRequest request);

   void addResourceFactory(ResourceFactory rf, String base, ResourceClass resourceClass);

   void removeRegistrations(ResourceClass resourceClass);

   void addPerRequestResource(ResourceClass clazz);

   void addPerRequestResource(ResourceClass clazz, String basePath);

   void addSingletonResource(Object singleton, ResourceClass resourceClass);

   void addSingletonResource(Object singleton, ResourceClass resourceClass, String basePath);

   void addJndiResource(String jndiName, ResourceClass resourceClass);

   void addJndiResource(String jndiName, ResourceClass resourceClass, String basePath);

   void checkAmbiguousUri();
}

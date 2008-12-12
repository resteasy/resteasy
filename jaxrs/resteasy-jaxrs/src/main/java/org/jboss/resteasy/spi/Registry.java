package org.jboss.resteasy.spi;

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

   void removeRegistrations(Class<?> clazz);

   void removeRegistrations(Class<?> clazz, String base);

   int getSize();
}

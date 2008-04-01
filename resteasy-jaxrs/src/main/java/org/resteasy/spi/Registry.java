package org.resteasy.spi;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface Registry
{
   void addResource(Class clazz);

   void addResourceFactory(ResourceFactory ref);

   void addResourceFactory(ResourceFactory ref, String base);

   void addResourceFactory(ResourceFactory ref, String base, Class<?> clazz);

   void removeRegistrations(Class clazz);

   void removeRegistrations(Class clazz, String base);

   int getSize();
}

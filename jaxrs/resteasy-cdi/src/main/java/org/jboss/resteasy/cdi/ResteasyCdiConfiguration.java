package org.jboss.resteasy.cdi;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;

/**
 * This component holds data gathered during CDI bootstrap and needed
 * by CdiInjectorFactory.
 * 
 * @author Jozef Hartinger
 *
 */
@ApplicationScoped
public class ResteasyCdiConfiguration
{
   // (Bean class -> Local interface) map for session beans with local interfaces. This map is 
   // necessary since RESTEasy identifies a bean class as JAX-RS components while CDI requires
   // a local interface to be used for lookup.
   private Map<Class<?>, Class<?>> sessionBeanInterfaceMap = new HashMap<Class<?>, Class<?>>();

   public boolean containsSessionBeanClass(Class<?> clazz)
   {
      return sessionBeanInterfaceMap.containsKey(clazz);
   }
   
   public Class<?> getSessionBeanLocalInterface(Class<?> clazz)
   {
      return sessionBeanInterfaceMap.get(clazz);
   }

   void setSessionBeanInterfaceMap(Map<Class<?>, Class<?>> sessionBeanInterfaceMap)
   {
      this.sessionBeanInterfaceMap = sessionBeanInterfaceMap;
   }
}

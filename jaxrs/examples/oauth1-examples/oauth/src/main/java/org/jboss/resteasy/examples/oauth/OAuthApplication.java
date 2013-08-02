package org.jboss.resteasy.examples.oauth;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public class OAuthApplication extends Application
{
   HashSet<Object> singletons = new HashSet<Object>();

   public OAuthApplication()
   {
      singletons.add(new ConsumerResource());
      singletons.add(new ServiceProviderResource());
   }

   @Override
   public Set<Class<?>> getClasses()
   {
      HashSet<Class<?>> set = new HashSet<Class<?>>();
      return set;
   }

   @Override
   public Set<Object> getSingletons()
   {
      return singletons;  
   }
}

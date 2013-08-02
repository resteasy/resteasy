package org.jboss.resteasy.tests;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@ApplicationPath("/jaxrs")
public class MyApplication extends Application
{
   protected Set<Object> singletons = new HashSet<Object>();

   public MyApplication()
   {
      singletons.add(new NewFeaturesResource());
   }

   @Override
   public Set<Object> getSingletons()
   {
      return singletons;
   }
}

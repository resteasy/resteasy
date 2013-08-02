package org.jboss.resteasy.test.application;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * This is for RESTEASY-381
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MyApplication extends Application
{
   public static int num_instantiations = 0;

   protected Set<Object> singletons = new HashSet<Object>();
   protected Set<Class<?>> clazzes = new HashSet<Class<?>>();

   public MyApplication()
   {
      num_instantiations++;
      singletons.add(new MyResource());
      clazzes.add(FooExceptionMapper.class);

   }

   @Override
   public Set<Class<?>> getClasses()
   {
      return clazzes;
   }

   @Override
   public Set<Object> getSingletons()
   {
      return singletons;
   }

   public String getHello()
   {
      return "hello";
   }
}

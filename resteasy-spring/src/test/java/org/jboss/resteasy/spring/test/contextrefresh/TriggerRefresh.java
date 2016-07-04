package org.jboss.resteasy.spring.test.contextrefresh;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ConfigurableWebApplicationContext;

/**
 * RESTEASY-632.
 * Test suggested by Holger Morch.
 * 
 * @author <a href="mailto:ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 * Created Feb 12, 2012
 */
public class TriggerRefresh implements ApplicationContextAware, InitializingBean
{
   private static ConfigurableWebApplicationContext context;
   private static int counter;
   private static boolean ok = true;

   public TriggerRefresh()
   {
      System.out.println(this);
   }

   @Override
   public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException
   {
      context = (ConfigurableWebApplicationContext)applicationContext;
      System.out.println(this + ".setApplicationContext(): " + context);
   }

   @Override
   public void afterPropertiesSet() throws BeansException
   {
      if (counter++ > 0)
      {
         return;
      }
      System.out.println(this + ".afterPropertiesSet()");
      try
      {
         context.refresh();
      }
      catch (Exception e)
      {
         // Actually, the exception (before the fix) didn't make it to here.  It was
         // stored in the servlet context.
         ok = false;
         System.out.println("Caught exception: " + e.getMessage());
      }
   }

   public static ConfigurableWebApplicationContext getApplicationContext()
   {
      return context;
   }

   public static boolean isOK()
   {
      return ok;
   }
}

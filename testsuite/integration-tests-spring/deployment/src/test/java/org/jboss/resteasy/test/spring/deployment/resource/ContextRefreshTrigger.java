package org.jboss.resteasy.test.spring.deployment.resource;


import org.jboss.logging.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.context.ConfigurableWebApplicationContext;

public class ContextRefreshTrigger implements ApplicationContextAware, InitializingBean {

   private static Logger logger = Logger.getLogger(ContextRefreshTrigger.class);
   private static ConfigurableWebApplicationContext context;
   private static int counter;
   private static boolean ok = true;

   public ContextRefreshTrigger() {
      logger.info(this);
   }

   @Override
   public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
      context = (ConfigurableWebApplicationContext) applicationContext;
      logger.info(this + ".setApplicationContext(): " + context);
   }

   @Override
   public void afterPropertiesSet() throws BeansException {
      if (counter++ > 0) {
         return;
      }
      logger.info(this + ".afterPropertiesSet()");
      try {
         context.refresh();
      } catch (Exception e) {
         // Actually, the exception (before the fix) didn't make it to here.  It was
         // stored in the servlet context.
         ok = false;
         logger.info("Caught exception: " + e.getMessage());
      }
   }

   public static ConfigurableWebApplicationContext getApplicationContext() {
      return context;
   }

   public static boolean isOK() {
      return ok;
   }
}

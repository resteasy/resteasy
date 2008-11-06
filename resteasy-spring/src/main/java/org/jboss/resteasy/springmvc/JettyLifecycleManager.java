package org.jboss.resteasy.springmvc;

import org.mortbay.jetty.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
* 
* @author <a href="mailto:sduskis@gmail.com">Solomn Duskis</a>
* @version $Revision: 1 $
*/

public class JettyLifecycleManager implements InitializingBean, DisposableBean
{
   private final static Logger logger = LoggerFactory
         .getLogger(JettyLifecycleManager.class);

   private Server servletContainer;

   public Server getServletContainer()
   {
      return servletContainer;
   }

   public void setServletContainer(Server servletContainer)
   {
      this.servletContainer = servletContainer;
   }

   public void afterPropertiesSet() throws Exception
   {
      logger.info("Starting up Jetty");
      try
      {
         servletContainer.start();

         while (!servletContainer.isStarted())
         {
            Thread.sleep(1000);
         }
      }
      catch (InterruptedException e)
      {
         logger.error("Interrupted while starting up Jetty", e);
      }
      catch (Exception e)
      {
         logger.error("Exception while starting up Jetty", e);
      }

      /*
       * For manual testing (e.g. browser, infinite loop while(true) { try {
       * while (!servletContainer.isStarted()) { Thread.sleep(3000); } } catch
       * (InterruptedException e) { // TODO Auto-generated catch block
       * e.printStackTrace(); } }
       */
   }

   public void destroy() throws Exception
   {
      logger.info("Shutting down Jetty");
      try
      {
         servletContainer.stop();
      }
      catch (Exception e)
      {
         logger.info("Exception while shutting down Jetty", e);
      }
   }

}

package org.jboss.resteasy.springmvc;

import org.jboss.resteasy.spring.jetty.i18n.LogMessages;
import org.jboss.resteasy.spring.jetty.i18n.Messages;
import org.mortbay.jetty.Server;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
* 
* @author <a href="mailto:sduskis@gmail.com">Solomon Duskis</a>
* @version $Revision: 1 $
*/

public class JettyLifecycleManager implements InitializingBean, DisposableBean
{
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
      LogMessages.LOGGER.info(Messages.MESSAGES.startingJetty());
      
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
         LogMessages.LOGGER.error(Messages.MESSAGES.interruptedWhileStartingJetty(), e);
      }
      catch (Exception e)
      {
         LogMessages.LOGGER.error(Messages.MESSAGES.exceptionWhileStartingJetty(), e);
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
      LogMessages.LOGGER.info(Messages.MESSAGES.shuttingDownJetty());
      try
      {
         servletContainer.stop();
      }
      catch (Exception e)
      {
         LogMessages.LOGGER.info(Messages.MESSAGES.exceptionWhileShuttingDownJetty(), e);
      }
   }

}

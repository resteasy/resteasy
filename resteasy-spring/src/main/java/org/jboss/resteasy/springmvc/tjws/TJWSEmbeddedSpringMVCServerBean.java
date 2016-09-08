package org.jboss.resteasy.springmvc.tjws;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**
* @author <a href="mailto:sduskis@gmail.com">Solomn Duskis</a>
* @version $Revision: 1 $
* 
*/
@Deprecated
public class TJWSEmbeddedSpringMVCServerBean implements InitializingBean,
      DisposableBean, FactoryBean<TJWSEmbeddedSpringMVCServer>
{
   private String applicationContextConfiguration;
   private int port;
   private String context = ""; 

   private TJWSEmbeddedSpringMVCServer server;

   public String getApplicationContextConfiguration()
   {
      return applicationContextConfiguration;
   }

   public void setApplicationContextConfiguration(
         String applicationContextConfiguration)
   {
      this.applicationContextConfiguration = applicationContextConfiguration;
   }

   public int getPort()
   {
      return port;
   }

   public void setPort(int port)
   {
      this.port = port;
   }

   public TJWSEmbeddedSpringMVCServer getServer()
   {
      return server;
   }

   public void setServer(TJWSEmbeddedSpringMVCServer server)
   {
      this.server = server;
   }

   public String getContext()
   {
      return context;
   }

   public void setContext(String context)
   {
      this.context = context;
   }

   public void afterPropertiesSet() throws Exception
   {
      server = new TJWSEmbeddedSpringMVCServer(
            this.applicationContextConfiguration, this.port, context);
      server.start();
   }

   public void destroy() throws Exception
   {
      server.stop();
      server = null;
   }

   public TJWSEmbeddedSpringMVCServer getObject() throws Exception
   {
      return server;
   }

   public Class<TJWSEmbeddedSpringMVCServer> getObjectType()
   {
      return TJWSEmbeddedSpringMVCServer.class;
   }

   public boolean isSingleton()
   {
      return true;
   }

}

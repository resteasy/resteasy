package org.jboss.resteasy.plugins.spring;

import org.jboss.resteasy.util.PortProvider;
import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

public class EmbeddedContainerBean implements InitializingBean, DisposableBean
{
   private TJWSEmbeddedJaxrsServer embeddedServer = new TJWSEmbeddedJaxrsServer();
   private String rootPath = "/";
   private int port = PortProvider.getPort();
   private SecurityDomain securityDoamin = null;

   public String getRootPath()
   {
      return rootPath;
   }

   public void setRootPath(String rootPath)
   {
      this.rootPath = rootPath;
   }

   public int getPort()
   {
      return port;
   }

   public void setPort(int port)
   {
      this.port = port;
   }

   public TJWSEmbeddedJaxrsServer getEmbeddedServer()
   {
      return embeddedServer;
   }

   public SecurityDomain getSecurityDoamin()
   {
      return securityDoamin;
   }

   public void setSecurityDoamin(SecurityDomain securityDoamin)
   {
      this.securityDoamin = securityDoamin;
   }

   public Dispatcher getDispatcher()
   {
      return embeddedServer.getDeployment().getDispatcher();
   }

   public void afterPropertiesSet() throws Exception
   {
      embeddedServer.setPort(getPort());
      embeddedServer.setRootResourcePath(getRootPath());
      embeddedServer.setSecurityDomain(getSecurityDoamin());
      embeddedServer.start();
   }

   public void destroy() throws Exception
   {
      embeddedServer.stop();
   }

}

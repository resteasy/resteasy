package org.jboss.resteasy.plugins.server.sun.http;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.spi.ResteasyDeployment;

/**
 * Helper class to create a ResteasyDeployment and bind it to an HttpContext of an HttpServer.  Setting the SecurityDomain
 * will turn on Basic Authentication.  Right now, only BasicAuthentication is supported.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResteasyContext
{
   protected ResteasyDeployment deployment = new ResteasyDeployment();
   protected String path = "";
   protected ResteasyHttpHandler handler;
   protected SecurityDomain securityDomain;
   protected HttpContext boundContext;

   public ResteasyDeployment getDeployment()
   {
      return deployment;
   }

   public void setDeployment(ResteasyDeployment deployment)
   {
      this.deployment = deployment;
   }

   public String getPath()
   {
      return path;
   }

   public void setPath(String path)
   {
      this.path = path;
      if (!this.path.startsWith("/"))
      {
         this.path = "/" + path;
      }
   }

   public ResteasyHttpHandler getHandler()
   {
      return handler;
   }

   public void setHandler(ResteasyHttpHandler handler)
   {
      this.handler = handler;
   }

   public SecurityDomain getSecurityDomain()
   {
      return securityDomain;
   }

   /**
    * Will turn on Basic Authentication
    *
    * @param securityDomain
    */
   public void setSecurityDomain(SecurityDomain securityDomain)
   {
      this.securityDomain = securityDomain;
   }

   public HttpContext bind(HttpServer server)
   {
      deployment.start();
      handler = new ResteasyHttpHandler();
      handler.setDispatcher(deployment.getDispatcher());
      handler.setProviderFactory(deployment.getProviderFactory());
      boundContext = server.createContext(path, handler);
      if (securityDomain != null)
      {
         boundContext.getFilters().add(new BasicAuthFilter(securityDomain));
      }
      return boundContext;

   }

   public void cleanup()
   {
      deployment.stop();
   }
}

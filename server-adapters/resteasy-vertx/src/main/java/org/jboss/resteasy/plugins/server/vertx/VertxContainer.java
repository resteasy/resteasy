package org.jboss.resteasy.plugins.server.vertx;

import org.jboss.resteasy.util.PortProvider;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class VertxContainer
{
   public static VertxJaxrsServer vertx;

   public static VertxResteasyDeployment start() throws Exception
   {
      return start("");
   }

   public static VertxResteasyDeployment start(String bindPath) throws Exception
   {
      return start(bindPath, null);
   }

   public static void start(VertxResteasyDeployment deployment) throws Exception
   {
      vertx = new VertxJaxrsServer();
      vertx.setDeployment(deployment);
      vertx.setPort(PortProvider.getPort());
      vertx.setRootResourcePath("");
      vertx.setSecurityDomain(null);
      vertx.start();
   }

   public static VertxResteasyDeployment start(String bindPath, SecurityDomain domain) throws Exception
   {
      VertxResteasyDeployment deployment = new VertxResteasyDeployment();
      deployment.setSecurityEnabled(true);
      return start(bindPath, domain, deployment);
   }

   public static VertxResteasyDeployment start(String bindPath, SecurityDomain domain, VertxResteasyDeployment deployment) throws Exception
   {
      vertx = new VertxJaxrsServer();
      vertx.setDeployment(deployment);
      vertx.setPort(PortProvider.getPort());
      vertx.setRootResourcePath(bindPath);
      vertx.setSecurityDomain(domain);
      vertx.start();
      return vertx.getDeployment();
   }

   public static void stop() throws Exception
   {
      if (vertx != null)
      {
         try
         {
            vertx.stop();
         }
         catch (Exception e)
         {

         }
      }
      vertx = null;
   }

   public static void main(String args[]) throws Exception {
       start();
   }
}

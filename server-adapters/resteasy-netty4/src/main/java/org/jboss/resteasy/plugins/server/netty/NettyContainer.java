package org.jboss.resteasy.plugins.server.netty;

import org.jboss.resteasy.util.PortProvider;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.spi.ResteasyDeployment;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class NettyContainer
{
   public static NettyJaxrsServer netty;

   public static ResteasyDeployment start() throws Exception
   {
      return start("");
   }

   public static ResteasyDeployment start(String bindPath) throws Exception
   {
      return start(bindPath, null);
   }

   public static void start(ResteasyDeployment deployment) throws Exception
   {
      netty = new NettyJaxrsServer();
      netty.setDeployment(deployment);
      netty.setPort(PortProvider.getPort());
      netty.setRootResourcePath("");
      netty.setSecurityDomain(null);
      netty.start();
   }

   public static ResteasyDeployment start(String bindPath, SecurityDomain domain) throws Exception
   {
      ResteasyDeployment deployment = new ResteasyDeployment();
      deployment.setSecurityEnabled(true);
      return start(bindPath, domain, deployment);
   }

   public static ResteasyDeployment start(String bindPath, SecurityDomain domain, ResteasyDeployment deployment) throws Exception
   {
      netty = new NettyJaxrsServer();
      netty.setDeployment(deployment);
      netty.setPort(PortProvider.getPort());
      netty.setRootResourcePath(bindPath);
      netty.setSecurityDomain(domain);
      netty.start();
      return netty.getDeployment();
   }

   public static void stop() throws Exception
   {
      if (netty != null)
      {
         try
         {
            netty.stop();
         }
         catch (Exception e)
         {

         }
      }
      netty = null;
   }

   public static void main(String args[]) throws Exception {
       start();
   }
}

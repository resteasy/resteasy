package org.jboss.resteasy.test;

import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;
import org.jboss.resteasy.spi.ResteasyDeployment;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TJWSServletContainer
{
   public static TJWSEmbeddedJaxrsServer tjws;

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
      tjws = new TJWSEmbeddedJaxrsServer();
      tjws.setDeployment(deployment);
      tjws.setPort(TestPortProvider.getPort());
      tjws.setRootResourcePath("");
      tjws.setSecurityDomain(null);
      tjws.start();
   }

   public static ResteasyDeployment start(String bindPath, SecurityDomain domain) throws Exception
   {
      ResteasyDeployment deployment = new ResteasyDeployment();
      deployment.setSecurityEnabled(true);
      return start(bindPath, domain, deployment);
   }

   public static ResteasyDeployment start(String bindPath, SecurityDomain domain, ResteasyDeployment deployment) throws Exception
   {
      tjws = new TJWSEmbeddedJaxrsServer();
      tjws.setDeployment(deployment);
      tjws.setPort(TestPortProvider.getPort());
      tjws.setRootResourcePath(bindPath);
      tjws.setSecurityDomain(domain);
      tjws.start();
      return tjws.getDeployment();
   }

   public static void stop() throws Exception
   {
      if (tjws != null)
         tjws.stop();
      tjws = null;
   }

}
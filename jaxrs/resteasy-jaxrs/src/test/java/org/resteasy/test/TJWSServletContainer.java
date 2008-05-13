package org.resteasy.test;

import org.resteasy.Dispatcher;
import org.resteasy.plugins.server.embedded.SecurityDomain;
import org.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TJWSServletContainer
{
   public static TJWSEmbeddedJaxrsServer tjws;

   public static Dispatcher start() throws Exception
   {
      return start("");
   }

   public static Dispatcher start(String bindPath) throws Exception
   {
      return start(bindPath, null);
   }

   public static Dispatcher start(String bindPath, SecurityDomain domain) throws Exception
   {
      tjws = new TJWSEmbeddedJaxrsServer();
      tjws.setPort(8081);
      tjws.setRootResourcePath(bindPath);
      tjws.setSecurityDomain(domain);
      tjws.start();
      return tjws.getDispatcher();
   }

   public static void stop() throws Exception
   {
      tjws.stop();
      tjws = null;
   }

}
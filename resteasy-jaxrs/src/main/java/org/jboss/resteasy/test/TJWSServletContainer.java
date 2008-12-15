package org.jboss.resteasy.test;

import org.jboss.resteasy.core.Dispatcher;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.plugins.server.tjws.TJWSEmbeddedJaxrsServer;

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

   public static void start(Dispatcher dispatcher) throws Exception
   {
      tjws = new TJWSEmbeddedJaxrsServer();
      tjws.setDispatcher(dispatcher);
      tjws.setPort(TestPortProvider.getPort());
      tjws.setRootResourcePath("");
      tjws.setSecurityDomain(null);
      tjws.start();
   }

   public static Dispatcher start(String bindPath, SecurityDomain domain) throws Exception
   {
      tjws = new TJWSEmbeddedJaxrsServer();
      tjws.setPort(TestPortProvider.getPort());
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
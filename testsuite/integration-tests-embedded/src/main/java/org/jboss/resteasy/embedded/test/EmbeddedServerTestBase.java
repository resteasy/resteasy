package org.jboss.resteasy.test;

import org.jboss.resteasy.plugins.server.embedded.EmbeddedJaxrsServer;

public class EmbeddedServerTestBase {
   private static String serverFilename;
   private static EmbeddedJaxrsServer server;
   private static Class serverClazz;

   public static EmbeddedJaxrsServer getServer()  throws Exception {
      if (server == null)
      {
         serverFilename = System.getProperty("fileName");
         ClassLoader cl = Thread.currentThread().getContextClassLoader();
         serverClazz = cl.loadClass(serverFilename);
      }
      server = (EmbeddedJaxrsServer) serverClazz.newInstance();
      return server;
   }

   public static String getServerFilename() {
      return serverFilename;
   }
}
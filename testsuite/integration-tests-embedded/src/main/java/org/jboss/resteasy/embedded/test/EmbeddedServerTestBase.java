package org.jboss.resteasy.test;

import org.jboss.resteasy.plugins.server.embedded.EMBEDDEDJaxrsServer;

public class EmbeddedServerTestBase {
   private static String serverFilename;
   private static EMBEDDEDJaxrsServer server;
   private static Class serverClazz;

   public static EMBEDDEDJaxrsServer getServer()  throws Exception {
      if (server == null)
      {
         serverFilename = System.getProperty("fileName");
         ClassLoader cl = Thread.currentThread().getContextClassLoader();
         serverClazz = cl.loadClass(serverFilename);
      }
      server = (EMBEDDEDJaxrsServer) serverClazz.newInstance();
      return server;
   }

   public static String getServerFilename() {
      return serverFilename;
   }
}
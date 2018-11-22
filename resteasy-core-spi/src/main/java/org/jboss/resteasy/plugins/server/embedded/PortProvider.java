package org.jboss.resteasy.plugins.server.embedded;

import java.io.IOException;
import java.net.ServerSocket;

public class PortProvider
{
   public static int portNumber = 8989;

   public static synchronized int getFreePort()
   {
      int port = -1;
      while (port == -1)
      {
         int pn = portNumber++;
         try
         {
            ServerSocket sock = new ServerSocket(pn);
            sock.close();
            port = pn;
            portNumber = pn;
         }
         catch (IOException ex)
         {
            //
         }
      }
      return port;
   }
}

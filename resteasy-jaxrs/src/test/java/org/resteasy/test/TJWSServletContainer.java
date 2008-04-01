package org.resteasy.test;

import Acme.Serve.Serve;
import org.resteasy.Dispatcher;
import org.resteasy.plugins.providers.RegisterBuiltin;
import org.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.resteasy.spi.ResteasyProviderFactory;

import java.util.Properties;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TJWSServletContainer
{
   private static Serve server = null;
   private static HttpServletDispatcher servlet = new HttpServletDispatcher();

   public static Dispatcher start(String bindPath) throws Exception
   {
      server = new Serve();
      Properties props = new Properties();
      props.put("port", 8081);
      props.setProperty(Serve.ARG_NOHUP, "nohup");
      server.arguments = props;
      server.addDefaultServlets(null); // optional file servlet
      server.addServlet(bindPath, servlet); // optional
      new Thread()
      {
         public void run()
         {
            server.serve();
         }
      }.start();

      ResteasyProviderFactory.setInstance(servlet.getDispatcher().getProviderFactory());
      RegisterBuiltin.register(servlet.getDispatcher().getProviderFactory());
      return servlet.getDispatcher();
   }

   public static void stop() throws Exception
   {
      server.notifyStop();
   }
}
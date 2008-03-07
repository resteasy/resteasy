package org.resteasy.mom.test;

import Acme.Serve.Serve;
import org.resteasy.plugins.providers.RegisterBuiltin;
import org.resteasy.plugins.server.servlet.HttpServletDispatcher;
import org.resteasy.spi.ResteasyProviderFactory;

import java.util.Properties;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class EmbeddedServlet
{
   private static Serve server = null;
   private static HttpServletDispatcher dispatcher = new HttpServletDispatcher();

   public static HttpServletDispatcher start() throws Exception
   {
      return start("/");
   }


   public static HttpServletDispatcher start(String bindPath) throws Exception
   {
      server = new Serve();
      Properties props = new Properties();
      props.put("port", 8081);
      props.setProperty(Serve.ARG_NOHUP, "nohup");
      server.arguments = props;
      server.addDefaultServlets(null); // optional file servlet
      server.addServlet(bindPath, dispatcher); // optional
      new Thread()
      {
         public void run()
         {
            server.serve();
         }
      }.start();
      ResteasyProviderFactory.setInstance(dispatcher.getProviderFactory());
      RegisterBuiltin.register(dispatcher.getProviderFactory());

      return dispatcher;
   }

   public static void stop() throws Exception
   {
      server.notifyStop();
   }
}

package org.jboss.resteasy.star.messaging.integration;

import org.jboss.resteasy.star.messaging.MessageServiceManager;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class RestMessagingJaxrsBootstrap extends Application
{
   MessageServiceManager manager = new MessageServiceManager();
   HashSet<Object> singletons = new HashSet<Object>();

   public RestMessagingJaxrsBootstrap(@Context ServletContext context) throws Exception
   {
      System.out.println("IN CONSTRUCTOR!!!!");
      String configfile = context.getInitParameter("rest.messaging.configfile");
      if (configfile != null)
      {
         URL url = context.getResource(configfile);
         manager.setConfigurationUrl(url.toString());
      }
      manager.start();
      singletons.add(manager.getQueueManager().getDestination());
      singletons.add(manager.getTopicManager().getDestination());
   }

   @Override
   public Set<Object> getSingletons()
   {
      return singletons;
   }
}

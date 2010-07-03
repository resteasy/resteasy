package org.jboss.resteasy.star.messaging.test;

import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.BaseClientResponse;
import org.jboss.resteasy.spi.Link;
import org.jboss.resteasy.star.messaging.MessageServiceManager;
import org.jboss.resteasy.star.messaging.util.LinkHeaderLinkStrategy;
import org.jboss.resteasy.star.messaging.util.LinkStrategy;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import java.lang.reflect.Field;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class BaseMessageTest
{
   public static Embedded server;
   public static MessageServiceManager manager;
   private static Field executorField;

   static
   {
      try
      {
         executorField = BaseClientResponse.class.getDeclaredField("executor");
      }
      catch (NoSuchFieldException e)
      {
         throw new RuntimeException(e);
      }
      executorField.setAccessible(true);
   }

   @BeforeClass
   public static void setupHornetQServerAndManager() throws Exception
   {
      server = new Embedded();
      server.start();
      manager = server.getManager();
   }

   @AfterClass
   public static void shutdownHornetqServerAndManager() throws Exception
   {
      manager = null;
      server.stop();
      server = null;
   }

   public static Link getLinkByTitle(LinkStrategy strategy, ClientResponse response, String title)
   {
      if (strategy instanceof LinkHeaderLinkStrategy)
      {
         return response.getLinkHeader().getLinkByTitle(title);
      }
      else
      {
         String headerName = "msg-" + title;
         String href = (String) response.getHeaders().getFirst(headerName);
         if (href == null) return null;
         //System.out.println(headerName + ": " + href);
         Link l = new Link(title, null, href, null, null);
         try
         {
            l.setExecutor((ClientExecutor) executorField.get(response));
         }
         catch (IllegalAccessException e)
         {
            throw new RuntimeException(e);
         }
         return l;
      }
   }

}

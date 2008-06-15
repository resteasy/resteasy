package org.resteasy.mom;

import org.resteasy.plugins.server.resourcefactory.SingletonResource;
import org.resteasy.spi.Registry;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ResteasyMomBootstrap implements ServletContextListener
{
   private Destinations destinations;
   private Registry registry;

   public void contextInitialized(ServletContextEvent event)
   {
      registry = (Registry) event.getServletContext().getAttribute(Registry.class.getName());
      destinations = new Destinations();
      event.getServletContext().setAttribute(Destinations.class.getName(), destinations);
      String queueJndiPrefix = event.getServletContext().getInitParameter("resteasy.mom.queue.jndi.prefix");
      if (queueJndiPrefix != null && !queueJndiPrefix.trim().equals(""))
         destinations.setQueueJndiPrefix(queueJndiPrefix.trim());
      String topicJndiPrefix = event.getServletContext().getInitParameter("resteasy.mom.topic.jndi.prefix");
      if (topicJndiPrefix != null && !topicJndiPrefix.trim().equals(""))
         destinations.setTopicJndiPrefix(topicJndiPrefix.trim());

      String factoryLocation = event.getServletContext().getInitParameter("resteasy.mom.connection.factory");
      if (factoryLocation == null || factoryLocation.trim().equals("")) factoryLocation = "java:/ConnectionFactory";

      try
      {
         InitialContext ctx = new InitialContext();
         ConnectionFactory factory = (ConnectionFactory) ctx.lookup(factoryLocation);

         String dlqLocation = event.getServletContext().getInitParameter("resteasy.mom.dlq.jndi");
         if (dlqLocation == null || dlqLocation.trim().equals("")) dlqLocation = "queue/DLQ";
         Destination dlq = (Destination) ctx.lookup(dlqLocation);

         int bufferSize = 1024;
         String buffer = event.getServletContext().getInitParameter("resteasy.mom.message.buffer.size");
         if (buffer != null && !buffer.trim().equals("")) bufferSize = Integer.parseInt(buffer.trim());

         StreamMessageProcessor processor = new StreamMessageProcessor(bufferSize);
         //ByteArrayMessageProcessor processor = new ByteArrayMessageProcessor();
         DlqProcessor dlqProcessor = new DlqProcessor(factory.createConnection(), dlq);

         destinations.setFactory(factory);
         destinations.setProcessor(processor);
         destinations.setDlq(dlqProcessor);

         String queues = event.getServletContext().getInitParameter("resteasy.mom.queues.jndi");
         if (queues != null)
         {
            Map<String, String> queueMap = parseMap(queues.trim());
            for (String name : queueMap.keySet())
            {
               String jndiName = queueMap.get(name).trim();
               Destination queue = null;
               try
               {
                  queue = (Destination) ctx.lookup(jndiName);
               }
               catch (NamingException e)
               {
                  throw new RuntimeException("Unable to find preconfigured jndi queue" + jndiName, e);
               }
               destinations.addQueue(name, queue);
            }
         }

         String topics = event.getServletContext().getInitParameter("resteasy.mom.topics.jndi");
         if (topics != null)
         {
            Map<String, String> topicMap = parseMap(topics.trim());
            for (String name : topicMap.keySet())
            {
               String jndiName = topicMap.get(name).trim();
               Destination topic = null;
               try
               {
                  topic = (Destination) ctx.lookup(jndiName);
               }
               catch (NamingException e)
               {
                  throw new RuntimeException("Unable to find preconfigured jndi topic" + jndiName, e);
               }
               destinations.addTopic(name, topic);
            }
         }
         registry.addResourceFactory(new SingletonResource(destinations));
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }


   }

   protected static Map<String, String> parseMap(String map)
   {
      Map<String, String> parsed = new HashMap<String, String>();
      String[] entries = map.trim().split(",");
      for (String entry : entries)
      {
         entry = entry.trim();
         int firstColon = entry.indexOf(':');
         String name = entry.substring(0, firstColon).trim();
         String val = entry.substring(firstColon + 1).trim();
         parsed.put(name, val);

      }
      return parsed;
   }


   public void contextDestroyed(ServletContextEvent event)
   {
      registry.removeRegistrations(destinations.getClass());
      destinations.stop();
   }

   public static void main(String[] args)
   {
      String config = "stuff : java:comp/env/jms/Blah, another : java:comp/env/jms/foo";
      Map<String, String> map = parseMap(config);
      for (String key : map.keySet())
      {
         System.out.println(key + " : \"" + map.get(key));
      }
   }
}

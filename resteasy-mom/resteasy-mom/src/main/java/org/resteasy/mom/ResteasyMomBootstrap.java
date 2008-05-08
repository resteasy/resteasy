package org.resteasy.mom;

import org.resteasy.plugins.server.resourcefactory.SingletonResource;
import org.resteasy.spi.Registry;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.naming.InitialContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

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

         String dlqLocation = event.getServletContext().getInitParameter("resteasy.mom.dlq");
         if (dlqLocation == null || dlqLocation.trim().equals("")) dlqLocation = "queue/DLQ";
         Destination dlq = (Destination) ctx.lookup(dlqLocation);

         int bufferSize = 1000;
         String buffer = event.getServletContext().getInitParameter("resteasy.mom.message.buffer.size");
         if (buffer != null && !buffer.trim().equals("")) bufferSize = Integer.parseInt(buffer.trim());

         StreamMessageProcessor processor = new StreamMessageProcessor(bufferSize);
         //ByteArrayMessageProcessor processor = new ByteArrayMessageProcessor();
         DlqProcessor dlqProcessor = new DlqProcessor(factory.createConnection(), dlq);

         destinations.setFactory(factory);
         destinations.setProcessor(processor);
         destinations.setDlq(dlqProcessor);

         registry.addResourceFactory(new SingletonResource(destinations));
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }


   }

   public void contextDestroyed(ServletContextEvent event)
   {
      registry.removeRegistrations(destinations.getClass());
      destinations.stop();
   }
}

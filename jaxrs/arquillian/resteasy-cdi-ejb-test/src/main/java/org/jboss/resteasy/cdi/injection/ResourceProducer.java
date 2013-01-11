package org.jboss.resteasy.cdi.injection;

import java.util.logging.Logger;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Nov 16, 2012
 */
@ApplicationScoped
@Resource(name="queue/test")
public class ResourceProducer
{  
   private static Connection connection;
   private static Session session;
   private static boolean disposed;
   
   @Inject private Logger log;
   @Resource(mappedName="java:jboss/exported/jms/queue/test")
   Queue bookQueue;
   
   @Resource(mappedName="java:jboss/exported/jms/RemoteConnectionFactory")
   ConnectionFactory connectionFactory;
   
   @ResourceBinding
   @Produces
   public Queue toDestination()
   {
      log.info("Queue: " + bookQueue);
      return bookQueue;
   }
   
   public static void dispose(@Disposes @ResourceBinding Queue queue)
   {
      System.out.println("ResourceProducer.dispose() called");
      disposed = true;
   }
   
   public static boolean isDisposed()
   {
      return disposed;
   }

   @Produces
   @ResourceBinding
   public Session getSession() throws JMSException
   {
      if (connection == null)
      {
         connection = connectionFactory.createConnection("guest", "pass");
         connection.start();
      }
      if (session == null)
      {
         session =  connection.createSession(false, Session.AUTO_ACKNOWLEDGE);  
      }
      return session;
   }
   
   @PreDestroy
   public void preDestroy() throws JMSException
   {
      if (connection != null)
      {
         connection.close();
         log.info("ResourceProducer.preDestory(): closed connection");
      }
   }
}

package org.jboss.resteasy.cdi.injection;

import javax.annotation.Resource;
import javax.enterprise.inject.Produces;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.Session;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Nov 16, 2012
 */
@Resource(name="queue/test")
public class ResourceProducer
{
   private static Connection connection;
   private static Session session;
   
   @Produces
   @ResourceBinding
   @Resource(mappedName="java:jboss/exported/jms/queue/test")
   Queue bookQueue;

   @Resource(mappedName="java:jboss/exported/jms/RemoteConnectionFactory")
   ConnectionFactory connectionFactory;
   
   @Produces
   @ResourceBinding
   public Session getSession() throws JMSException
   {
      if (connection == null)
      {
         connection = connectionFactory.createConnection("guest", "pass");
      }
      if (session == null)
      {
         session =  connection.createSession(false, Session.AUTO_ACKNOWLEDGE);  
      }
      return session;
   }
}

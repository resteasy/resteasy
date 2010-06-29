package org.jboss.resteasy.star.messaging.queue;

import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.jboss.resteasy.star.messaging.util.HttpMessageHelper;

import javax.ws.rs.core.HttpHeaders;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PostMessage
{
   protected ClientSessionFactory sessionFactory;
   protected String destination;
   protected boolean defaultDurable = false;

   public ClientSessionFactory getSessionFactory()
   {
      return sessionFactory;
   }

   public void setSessionFactory(ClientSessionFactory sessionFactory)
   {
      this.sessionFactory = sessionFactory;
   }

   public String getDestination()
   {
      return destination;
   }

   public void setDestination(String destination)
   {
      this.destination = destination;
   }

   public boolean isDefaultDurable()
   {
      return defaultDurable;
   }

   public void setDefaultDurable(boolean defaultDurable)
   {
      this.defaultDurable = defaultDurable;
   }


   protected ClientMessage createHornetQMessage(HttpHeaders headers, byte[] body, boolean durable, ClientSession session) throws Exception
   {
      ClientMessage message = session.createMessage(durable);
      HttpMessageHelper.writeHttpMessage(headers, body, message);
      return message;
   }
}

package org.jboss.resteasy.star.messaging.queue;

import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.star.messaging.SimpleMessage;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

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
      SimpleMessage msg = new SimpleMessage(headers, body);

      ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);

      ObjectOutputStream oos = new ObjectOutputStream(baos);

      oos.writeObject(msg);

      oos.flush();

      message.getBodyBuffer().writeBytes(baos.toByteArray());
      return message;
   }
}

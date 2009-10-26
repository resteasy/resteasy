package org.jboss.resteasy.star.messaging;

import org.hornetq.core.client.ClientMessage;
import org.hornetq.core.client.ClientSession;
import org.hornetq.core.client.ClientSessionFactory;
import org.hornetq.core.client.MessageHandler;
import org.hornetq.core.exception.HornetQException;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ConsumerForwarding implements MessageHandler
{
   private String uri;
   private String topicName;
   private TopicMessageRepository repository;
   private ClientExecutor executor = new ApacheHttpClientExecutor();
   private ClientSession session;
   private String uuid;

   public ConsumerForwarding(String uri, String topicName, ClientSessionFactory factory, TopicMessageRepository repository, boolean durable)
   {
      this.uri = uri;
      this.topicName = topicName;
      this.repository = repository;
      try
      {
         session = factory.createSession(false, false, false);
         uuid = UUID.randomUUID().toString();

         session.createTemporaryQueue(topicName, uuid, null);
         session.createConsumer(uuid, null, false).setMessageHandler(this);
         session.start();
      }
      catch (HornetQException e)
      {
         throw new RuntimeException(e);
      }

   }

   public String getId()
   {
      return uuid;
   }

   public void cleanup()
   {
      try
      {
         session.deleteQueue(uuid);
         session.close();
      }
      catch (HornetQException e)
      {
         throw new RuntimeException(e);
      }
   }

   public void onMessage(ClientMessage notification)
   {
      long id = (Long) notification.getProperty("m-id");
      Message message = repository.getMessage(id);
      ClientRequest request = new ClientRequest(uri, executor);
      for (Map.Entry<String, List<String>> entry : message.getHeaders().entrySet())
      {
         for (String value : entry.getValue())
         {
            request.header(entry.getKey(), value);
         }
      }
      request.body(message.getHeaders().getFirst("Content-Type"), message.getBody());
      boolean success = false;
      try
      {
         ClientResponse response = request.post();
         success = response.getStatus() >= 200 || response.getStatus() < 400;
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      if (success)
      {
         try
         {
            notification.acknowledge();
         }
         catch (HornetQException e)
         {
            e.printStackTrace();
         }
      }
   }
}

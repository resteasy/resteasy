package org.jboss.resteasy.star.messaging.queue;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.client.ClientSession;
import org.jboss.resteasy.star.messaging.queue.push.PushConsumerResource;
import org.jboss.resteasy.star.messaging.queue.push.xml.PushRegistration;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Path("/queues")
public class QueueDestinationsResource
{
   private Map<String, QueueResource> queues = new ConcurrentHashMap<String, QueueResource>();
   private QueueServiceManager manager;

   public QueueDestinationsResource(QueueServiceManager manager)
   {
      this.manager = manager;
   }

   public Map<String, QueueResource> getQueues()
   {
      return queues;
   }

   @Path("/{queue-name}")
   public QueueResource findQueue(@PathParam("queue-name") String name) throws Exception
   {
      QueueResource queue = queues.get(name);
      if (queue == null)
      {
         String queueName = name;
         ClientSession session = manager.getSessionFactory().createSession(false, false, false);
         try
         {
            ClientSession.QueueQuery query = session.queueQuery(new SimpleString(queueName));
            if (!query.isExists())
            {
               throw new WebApplicationException(Response.status(404).type("text/plain").entity("Queue does not exist").build());
            }
            DestinationSettings queueSettings = manager.getDefaultSettings();
            boolean defaultDurable = queueSettings.isDurableSend() || query.isDurable();

            queue = createQueueResource(queueName, defaultDurable, queueSettings.getConsumerSessionTimeoutSeconds(), queueSettings.isDuplicatesAllowed());
         }
         finally
         {
            try
            {
               session.close();
            }
            catch (HornetQException e)
            {
            }
         }
      }
      return queue;
   }

   public QueueResource createQueueResource(String queueName, boolean defaultDurable, int timeoutSeconds, boolean duplicates)
           throws Exception
   {
      QueueResource queueResource = new QueueResource();
      queueResource.setDestination(queueName);

      ConsumersResource consumers = new ConsumersResource();
      consumers.setConsumerTimeoutSeconds(timeoutSeconds);
      consumers.setConsumerTimeoutTask(manager.getTimeoutTask());
      consumers.setDestination(queueName);
      consumers.setSessionFactory(manager.getConsumerSessionFactory());
      queueResource.setConsumers(consumers);

      PushConsumerResource push = new PushConsumerResource();
      push.setDestination(queueName);
      push.setSessionFactory(manager.getConsumerSessionFactory());
      queueResource.setPushConsumers(push);

      PostMessage sender = null;
      if (duplicates)
      {
         sender = new PostMessageDupsOk();
      }
      else
      {
         sender = new PostMessageNoDups();
      }
      sender.setDefaultDurable(defaultDurable);
      sender.setDestination(queueName);
      sender.setSessionFactory(manager.getSessionFactory());
      sender.setPoolSize(manager.getProducerPoolSize());
      sender.init();
      queueResource.setSender(sender);

      if (manager.getPushStore() != null)
      {
         push.setPushStore(manager.getPushStore());
         List<PushRegistration> regs = manager.getPushStore().getByDestination(queueName);
         for (PushRegistration reg : regs)
         {
            push.addRegistration(reg);
         }
      }

      queueResource.start();
      getQueues().put(queueName, queueResource);
      return queueResource;
   }
}

package org.jboss.resteasy.star.messaging.queue;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.jms.client.HornetQDestination;
import org.hornetq.jms.client.HornetQQueue;
import org.hornetq.jms.server.config.JMSQueueConfiguration;
import org.hornetq.jms.server.impl.JMSServerConfigParserImpl;
import org.jboss.resteasy.star.messaging.queue.push.PushConsumerResource;
import org.jboss.resteasy.star.messaging.queue.push.xml.PushRegistration;
import org.w3c.dom.Document;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
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

   @POST
   @Consumes("application/hornetq.jms.queue+xml")
   public Response createJmsQueue(@Context UriInfo uriInfo, Document document)
   {
      try
      {
         JMSServerConfigParserImpl parser = new JMSServerConfigParserImpl();
         JMSQueueConfiguration queue = parser.parseQueueConfiguration(document.getDocumentElement());
         HornetQQueue hqQueue = HornetQDestination.createQueue(queue.getName());
         String queueName = hqQueue.getAddress();
         ClientSession session = manager.getSessionFactory().createSession(false, false, false);
         try
         {

            ClientSession.QueueQuery query = session.queueQuery(new SimpleString(queueName));
            if (!query.isExists())
            {
               if (queue.getSelector() != null)
               {
                  session.createQueue(queueName, queueName, queue.getSelector(), queue.isDurable());
               }
               else
               {
                  session.createQueue(queueName, queueName, queue.isDurable());
               }

            }
            else
            {
               throw new WebApplicationException(Response.status(412).type("text/plain").entity("Queue already exists.").build());
            }
         }
         finally
         {
            try { session.close(); } catch (Exception ignored) {}
         }
         if (queue.getBindings() != null && queue.getBindings().length > 0 && manager.getRegistry() != null)
         {
            for (String binding : queue.getBindings())
            {
               manager.getRegistry().bind(binding, hqQueue);
            }
         }
         URI uri = uriInfo.getRequestUriBuilder().path(queueName).build();
         return Response.created(uri).build();
      }
      catch (Exception e)
      {
         if (e instanceof WebApplicationException) throw (WebApplicationException) e;
         throw new WebApplicationException(e, Response.serverError().type("text/plain").entity("Failed to create queue.").build());
      }
   }

   public Map<String, QueueResource> getQueues()
   {
      return queues;
   }

   @DELETE
   @Path("/{queue-name}")
   public void deleteQueue(@PathParam("queue-name") String name) throws Exception
   {
      QueueResource queue = queues.remove(name);
      if (queue != null)
      {
         try
         {
            queue.stop();
         }
         catch (Exception e)
         {

         }
      }

      ClientSession session = manager.getSessionFactory().createSession(false, false, false);
      try
      {

         SimpleString queueName = new SimpleString(name);
         ClientSession.QueueQuery query = session.queueQuery(queueName);
         if (query.isExists())
         {
            session.deleteQueue(queueName);
         }
         else
         {
            throw new WebApplicationException(Response.status(405).type("text/plain").entity("Queue '" + name + "' does not exist").build());
         }
      }
      finally
      {
         try { session.close(); } catch (Exception ignored) {}
      }

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
               throw new WebApplicationException(Response.status(404).type("text/plain").entity("Queue '" + name + "' does not exist").build());
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
      queueResource.setServiceManager(manager);

      ConsumersResource consumers = new ConsumersResource();
      consumers.setConsumerTimeoutSeconds(timeoutSeconds);
      consumers.setDestination(queueName);
      consumers.setSessionFactory(manager.getConsumerSessionFactory());
      consumers.setServiceManager(manager);
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
      sender.setServiceManager(manager);
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

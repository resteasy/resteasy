package org.jboss.resteasy.star.messaging.topic;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.client.ClientSession;
import org.jboss.resteasy.star.messaging.queue.DestinationSettings;
import org.jboss.resteasy.star.messaging.queue.PostMessage;
import org.jboss.resteasy.star.messaging.queue.PostMessageDupsOk;
import org.jboss.resteasy.star.messaging.queue.PostMessageNoDups;

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
@Path("/topics")
public class TopicDestinationsResource
{
   private Map<String, TopicResource> topics = new ConcurrentHashMap<String, TopicResource>();
   private TopicServiceManager manager;

   public TopicDestinationsResource(TopicServiceManager manager)
   {
      this.manager = manager;
   }

   @Path("/{topic-name}")
   public TopicResource findQueue(@PathParam("topic-name") String name) throws Exception
   {
      TopicResource topic = topics.get(name);
      if (topic == null)
      {
         ClientSession session = manager.getSessionFactory().createSession(false, false, false);
         try
         {
            ClientSession.QueueQuery query = session.queueQuery(new SimpleString(name));
            if (!query.isExists())
            {
               throw new WebApplicationException(Response.status(404).type("text/plain").entity("Queue does not exist").build());
            }
            DestinationSettings queueSettings = manager.getDefaultSettings();
            boolean defaultDurable = queueSettings.isDurableSend() || query.isDurable();

            topic = createTopicResource(name, defaultDurable, queueSettings.getConsumerSessionTimeoutSeconds(), queueSettings.isDuplicatesAllowed());
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
      return topic;
   }

   public Map<String, TopicResource> getTopics()
   {
      return topics;
   }

   public TopicResource createTopicResource(String topicName, boolean defaultDurable, long timeoutSeconds, boolean duplicates) throws Exception
   {
      TopicResource topicResource = new TopicResource();
      topicResource.setDestination(topicName);
      SubscriptionsResource subscriptionsResource = new SubscriptionsResource();
      topicResource.setSubscriptions(subscriptionsResource);
      subscriptionsResource.setAckTimeoutSeconds(timeoutSeconds);
      subscriptionsResource.setAckTimeoutService(manager.getThreadPool());

      subscriptionsResource.setDestination(topicName);
      subscriptionsResource.setSessionFactory(manager.getSessionFactory());
      PushSubscriptionsResource push = new PushSubscriptionsResource();
      push.setDestination(topicName);
      push.setSessionFactory(manager.getSessionFactory());
      topicResource.setPushSubscriptions(push);

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
      sender.setDestination(topicName);
      sender.setSessionFactory(manager.getSessionFactory());
      topicResource.setSender(sender);

      if (manager.getPushStore() != null)
      {
         push.setPushStore(manager.getPushStore());
         List<PushTopicRegistration> regs = manager.getPushStore().getByTopic(topicName);
         for (PushTopicRegistration reg : regs)
         {
            push.addRegistration(reg);
         }
      }


      getTopics().put(topicName, topicResource);
      topicResource.start();
      return topicResource;
   }
}
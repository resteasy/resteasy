package org.jboss.resteasy.star.messaging.topic;

import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.core.client.impl.ClientSessionFactoryImpl;
import org.hornetq.core.remoting.impl.invm.InVMConnectorFactory;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.star.messaging.queue.PostMessage;
import org.jboss.resteasy.star.messaging.queue.PostMessageDupsOk;
import org.jboss.resteasy.star.messaging.queue.PostMessageNoDups;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class TopicDeployer
{
   protected Registry registry;
   protected List<TopicDeployment> topics = new ArrayList<TopicDeployment>();
   protected TopicDestinationsResource destination = new TopicDestinationsResource();
   protected ExecutorService ackTimeoutExecutorService;
   protected ClientSessionFactory sessionFactory;
   protected boolean started;

   public Registry getRegistry()
   {
      return registry;
   }

   public void setRegistry(Registry registry)
   {
      this.registry = registry;
   }

   public List<TopicDeployment> getTopics()
   {
      return topics;
   }

   public void setTopics(List<TopicDeployment> topics)
   {
      this.topics = topics;
   }

   public ExecutorService getAckTimeoutExecutorService()
   {
      return ackTimeoutExecutorService;
   }

   public void setAckTimeoutExecutorService(ExecutorService ackTimeoutExecutorService)
   {
      this.ackTimeoutExecutorService = ackTimeoutExecutorService;
   }

   public ClientSessionFactory getSessionFactory()
   {
      return sessionFactory;
   }

   public void setSessionFactory(ClientSessionFactory sessionFactory)
   {
      this.sessionFactory = sessionFactory;
   }

   public void start() throws Exception
   {

      if (ackTimeoutExecutorService == null) ackTimeoutExecutorService = Executors.newCachedThreadPool();
      if (sessionFactory == null)
         sessionFactory = new ClientSessionFactoryImpl(new TransportConfiguration(InVMConnectorFactory.class.getName()));


      started = true;

      for (TopicDeployment topic : topics)
      {
         deploy(topic);
      }
      registry.addSingletonResource(destination);
   }

   public void deploy(TopicDeployment topicDeployment)
           throws Exception
   {
      if (!started)
      {
         throw new Exception("You must start() this class instance before deploying");
      }
      String queueName = topicDeployment.getName();
      ClientSession session = sessionFactory.createSession(false, false, false);
      ClientSession.QueueQuery query = session.queueQuery(new SimpleString(queueName));
      boolean defaultDurable = topicDeployment.isDurableSend();
      if (query.isExists())
      {
         defaultDurable = query.isDurable();
      }
      else
      {
         session.createQueue(queueName, queueName, topicDeployment.isDurableSend());
      }
      session.close();

      TopicResource topicResource = new TopicResource();
      topicResource.setDestination(queueName);
      SubscriptionsResource subscriptionsResource = new SubscriptionsResource();
      topicResource.setSubscriptions(subscriptionsResource);

      if (topicDeployment.isAutoAcknowledge())
      {
         subscriptionsResource.setConsumerFactory(SubscriptionResource.getFactory());
      }
      else
      {
         subscriptionsResource.setConsumerFactory(AcknowledgedSubscriptionResource.getFactory(ackTimeoutExecutorService, topicDeployment.getAckTimeoutSeconds()));
      }
      subscriptionsResource.setDestination(queueName);
      subscriptionsResource.setSessionFactory(sessionFactory);
      PushSubscriptionsResource push = new PushSubscriptionsResource();
      push.setDestination(queueName);
      push.setSessionFactory(sessionFactory);
      topicResource.setPushSubscriptions(push);

      PostMessage sender = null;
      if (topicDeployment.isDuplicatesAllowed())
      {
         sender = new PostMessageDupsOk();
      }
      else
      {
         sender = new PostMessageNoDups();
      }
      sender.setDefaultDurable(defaultDurable);
      sender.setDestination(queueName);
      sender.setSessionFactory(sessionFactory);
      topicResource.setSender(sender);

      destination.getTopics().put(queueName, topicResource);
      topicResource.start();
   }

   public void stop() throws Exception
   {
      for (TopicResource topic : destination.getTopics().values())
      {
         topic.stop();
      }
      try
      {
         sessionFactory.close();
      }
      catch (Exception e)
      {
      }
   }
}
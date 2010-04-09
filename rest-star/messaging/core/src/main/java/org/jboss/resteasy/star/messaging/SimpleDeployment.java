package org.jboss.resteasy.star.messaging;

import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.client.ClientSession;
import org.hornetq.api.core.client.ClientSessionFactory;
import org.hornetq.core.client.impl.ClientSessionFactoryImpl;
import org.hornetq.core.config.Configuration;
import org.hornetq.core.config.impl.ConfigurationImpl;
import org.hornetq.core.remoting.impl.invm.InVMAcceptorFactory;
import org.hornetq.core.remoting.impl.invm.InVMConnectorFactory;
import org.hornetq.core.server.HornetQServer;
import org.hornetq.core.server.HornetQServers;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.star.messaging.queue.QueuePublisher;
import org.jboss.resteasy.star.messaging.queue.QueueResource;
import org.jboss.resteasy.star.messaging.topic.CurrentTopicIndex;
import org.jboss.resteasy.star.messaging.topic.TopicMessageRepository;
import org.jboss.resteasy.star.messaging.topic.TopicPublisher;
import org.jboss.resteasy.star.messaging.topic.TopicResource;
import org.jboss.resteasy.star.messaging.topic.TopicSequencer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Assembles all the pieces for a simple server
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class SimpleDeployment
{
   protected HornetQServer server;
   protected Registry registry;
   protected Map<String, TopicSequencer> sequencers = new HashMap<String, TopicSequencer>();
   protected List<TopicDeployment> topics = new ArrayList<TopicDeployment>();
   protected List<QueueDeployment> queues = new ArrayList<QueueDeployment>();
   protected DestinationResource destination;

   public HornetQServer getServer()
   {
      return server;
   }

   public void setServer(HornetQServer server)
   {
      this.server = server;
   }

   public Registry getRegistry()
   {
      return registry;
   }

   public void setRegistry(Registry registry)
   {
      this.registry = registry;
   }

   public Map<String, TopicSequencer> getSequencers()
   {
      return sequencers;
   }

   public void setSequencers(Map<String, TopicSequencer> sequencers)
   {
      this.sequencers = sequencers;
   }

   public List<TopicDeployment> getTopics()
   {
      return topics;
   }

   public void setTopics(List<TopicDeployment> topics)
   {
      this.topics = topics;
   }

   public DestinationResource getDestination()
   {
      return destination;
   }

   public void setDestination(DestinationResource destination)
   {
      this.destination = destination;
   }

   public List<QueueDeployment> getQueues()
   {
      return queues;
   }

   public void setQueues(List<QueueDeployment> queues)
   {
      this.queues = queues;
   }

   public void start() throws Exception
   {

      // Step 1. Create the Configuration, and set the properties accordingly
      Configuration configuration = new ConfigurationImpl();
      configuration.setPersistenceEnabled(false);
      configuration.setSecurityEnabled(false);
      configuration.getAcceptorConfigurations().add(new TransportConfiguration(InVMAcceptorFactory.class.getName()));

      // Step 2. Create and start the server
      server = HornetQServers.newHornetQServer(configuration);
      server.start();


      // Step 3. As we are not using a JNDI environment we instantiate the objects directly
      ClientSessionFactory sf = new ClientSessionFactoryImpl(new TransportConfiguration(InVMConnectorFactory.class.getName()));

      destination = new DestinationResource();

      for (TopicDeployment topicDeployment : topics)
      {
         ClientSession session = sf.createSession(false, false, false);
         String topicName = topicDeployment.getName();
         session.createQueue(topicName, topicName, true);
         session.close();

         TopicMessageRepository repository = new TopicMessageRepository();
         repository.setDestination(topicName);
         CurrentTopicIndex messageIndex = new CurrentTopicIndex();
         TopicPublisher pub = new TopicPublisher();
         pub.setDestination(topicName);
         pub.setRepository(repository);
         pub.setSessionFactory(sf);
         Object sender = null;
         if (topicDeployment.isDuplicatesAllowed())
         {
            sender = new CreateMessage(repository, pub);
         }
         else
         {
            sender = new ReliableCreateMessage(repository, pub);
         }
         TopicResource topic = new TopicResource(repository, messageIndex, sf, topicName, sender);
         destination.getTopics().put(topicName, topic);
         TopicSequencer sequencer = new TopicSequencer();
         sequencer.setCurrent(messageIndex);
         sequencer.setIncoming(topicName);
         sequencer.setRepository(repository);
         sequencer.setFactory(sf);
         sequencer.start();
         sequencers.put(topicName, sequencer);
      }
      for (QueueDeployment queueDeployment : queues)
      {
         String queueName = queueDeployment.getName();
         ClientSession session = sf.createSession(false, false, false);
         session.createQueue(queueName, queueName, true);
         session.close();

         QueueResource queue = new QueueResource();
         queue.setDestination(queueName);
         queue.setSessionFactory(sf);
         queue.getRepository().setDestination(queueName);

         QueuePublisher pub = new QueuePublisher();
         pub.setDestination(queueName);
         pub.setRepository(queue.getRepository());
         pub.setSessionFactory(sf);

         Object sender = null;
         if (queueDeployment.isDuplicatesAllowed())
         {
            sender = new CreateMessage(queue.getRepository(), pub);
         }
         else
         {
            sender = new ReliableCreateMessage(queue.getRepository(), pub);
         }
         queue.setSender(sender);

         destination.getQueues().put(queueName, queue);
         queue.start();
      }
      registry.addSingletonResource(destination);
   }

   public void stop() throws Exception
   {
      for (TopicSequencer sequencer : sequencers.values())
      {
         sequencer.stop();
      }
      for (QueueResource queue : destination.getQueues().values())
      {
         queue.stop();
      }
      server.stop();
   }
}

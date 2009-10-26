package org.jboss.resteasy.star.messaging;

import org.hornetq.core.client.ClientSession;
import org.hornetq.core.client.ClientSessionFactory;
import org.hornetq.core.client.impl.ClientSessionFactoryImpl;
import org.hornetq.core.config.Configuration;
import org.hornetq.core.config.TransportConfiguration;
import org.hornetq.core.config.impl.ConfigurationImpl;
import org.hornetq.core.remoting.impl.invm.InVMAcceptorFactory;
import org.hornetq.core.remoting.impl.invm.InVMConnectorFactory;
import org.hornetq.core.server.HornetQ;
import org.hornetq.core.server.HornetQServer;
import org.jboss.resteasy.spi.Registry;

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
   protected List<String> topics = new ArrayList<String>();
   protected List<String> queues = new ArrayList<String>();
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

   public List<String> getTopics()
   {
      return topics;
   }

   public void setTopics(List<String> topics)
   {
      this.topics = topics;
   }

   public List<String> getQueues()
   {
      return queues;
   }

   public void setQueues(List<String> queues)
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
      server = HornetQ.newHornetQServer(configuration);
      server.start();


      // Step 3. As we are not using a JNDI environment we instantiate the objects directly
      ClientSessionFactory sf = new ClientSessionFactoryImpl(new TransportConfiguration(InVMConnectorFactory.class.getName()));

      destination = new DestinationResource();

      for (String topicName : topics)
      {
         ClientSession session = sf.createSession(false, false, false);
         session.createQueue(topicName, topicName, true);
         session.close();

         TopicMessageRepository repository = new TopicMessageRepository();
         CurrentTopicIndex messageIndex = new CurrentTopicIndex();
         TopicResource topic = new TopicResource(repository, messageIndex, sf, topicName);
         destination.getTopics().put(topicName, topic);
         TopicSequencer sequencer = new TopicSequencer();
         sequencer.setCurrent(messageIndex);
         sequencer.setIncoming(topicName);
         sequencer.setRepository(repository);
         sequencer.setFactory(sf);
         sequencer.start();
         sequencers.put(topicName, sequencer);
      }
      for (String queueName : queues)
      {
         ClientSession session = sf.createSession(false, false, false);
         session.createQueue(queueName, queueName, true);
         session.close();

         QueueResource queue = new QueueResource();
         queue.setDestination(queueName);
         queue.setSessionFactory(sf);
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

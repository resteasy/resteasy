package org.jboss.resteasy.star.messaging;

import org.hornetq.core.server.HornetQServer;
import org.hornetq.core.server.HornetQ;
import org.hornetq.core.config.Configuration;
import org.hornetq.core.config.TransportConfiguration;
import org.hornetq.core.config.impl.ConfigurationImpl;
import org.hornetq.core.remoting.impl.invm.InVMAcceptorFactory;
import org.hornetq.core.remoting.impl.invm.InVMConnectorFactory;
import org.hornetq.core.client.ClientSessionFactory;
import org.hornetq.core.client.ClientSession;
import org.hornetq.core.client.impl.ClientSessionFactoryImpl;
import org.jboss.resteasy.spi.Registry;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

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
   protected Map<String, Sequencer> sequencers = new HashMap<String, Sequencer>();
   protected List<String> topics = new ArrayList<String>();

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

   public Map<String, Sequencer> getSequencers()
   {
      return sequencers;
   }

   public void setSequencers(Map<String, Sequencer> sequencers)
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

      DestinationResource destination = new DestinationResource();

      for (String topicName : topics)
      {
         ClientSession session = sf.createSession(false, false, false);
         session.createQueue(topicName, topicName, true);
         session.close();
         
         MessageRepository repository = new MessageRepository();
         CurrentMessageIndex messageIndex = new CurrentMessageIndex();
         TopicResource topic = new TopicResource(repository, messageIndex, sf, topicName);
         destination.getTopics().put(topicName, topic);
         Sequencer sequencer = new Sequencer();
         sequencer.setCurrent(messageIndex);
         sequencer.setIncoming(topicName);
         sequencer.setRepository(repository);
         sequencer.setFactory(sf);
         sequencers.put(topicName, sequencer);
         Thread thread = new Thread(sequencer);
         thread.start();

      }
      registry.addSingletonResource(destination);
   }

   public void stop() throws Exception
   {
      for (Sequencer sequencer : sequencers.values())
      {
         sequencer.shutdown();
      }
      server.stop();
   }
}

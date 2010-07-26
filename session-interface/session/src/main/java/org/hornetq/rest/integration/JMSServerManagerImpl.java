package org.hornetq.rest.integration;

import org.hornetq.api.core.HornetQException;
import org.hornetq.api.core.Pair;
import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.TransportConfiguration;
import org.hornetq.api.core.management.AddressControl;
import org.hornetq.api.core.management.ResourceNames;
import org.hornetq.api.jms.HornetQJMSClient;
import org.hornetq.core.config.Configuration;
import org.hornetq.core.config.DiscoveryGroupConfiguration;
import org.hornetq.core.deployers.DeploymentManager;
import org.hornetq.core.deployers.impl.FileDeploymentManager;
import org.hornetq.core.deployers.impl.XmlDeployer;
import org.hornetq.core.logging.Logger;
import org.hornetq.core.postoffice.Binding;
import org.hornetq.core.postoffice.BindingType;
import org.hornetq.core.security.Role;
import org.hornetq.core.server.ActivateCallback;
import org.hornetq.core.server.HornetQServer;
import org.hornetq.core.settings.impl.AddressSettings;
import org.hornetq.jms.client.HornetQConnectionFactory;
import org.hornetq.jms.client.HornetQDestination;
import org.hornetq.jms.client.HornetQQueue;
import org.hornetq.jms.client.HornetQTopic;
import org.hornetq.jms.client.SelectorTranslator;
import org.hornetq.jms.persistence.JMSStorageManager;
import org.hornetq.jms.persistence.config.PersistedConnectionFactory;
import org.hornetq.jms.persistence.config.PersistedDestination;
import org.hornetq.jms.persistence.config.PersistedJNDI;
import org.hornetq.jms.persistence.config.PersistedType;
import org.hornetq.jms.persistence.impl.journal.JMSJournalStorageManagerImpl;
import org.hornetq.jms.persistence.impl.nullpm.NullJMSStorageManagerImpl;
import org.hornetq.jms.server.JMSServerManager;
import org.hornetq.jms.server.config.ConnectionFactoryConfiguration;
import org.hornetq.jms.server.config.JMSConfiguration;
import org.hornetq.jms.server.config.JMSQueueConfiguration;
import org.hornetq.jms.server.config.TopicConfiguration;
import org.hornetq.jms.server.config.impl.ConnectionFactoryConfigurationImpl;
import org.hornetq.jms.server.impl.JMSServerDeployer;
import org.hornetq.jms.server.management.JMSManagementService;
import org.hornetq.jms.server.management.impl.JMSManagementServiceImpl;
import org.hornetq.utils.TimeAndCounterIDGenerator;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A Deployer used to create and add to JNDI queues, topics and connection
 * factories. Typically this would only be used in an app server env.
 * <p/>
 * JMS Connection Factories & Destinations can be configured either
 * using configuration files or using a JMSConfiguration object.
 * <p/>
 * If configuration files are used, JMS resources are redeployed if the
 * files content is changed.
 * If a JMSConfiguration object is used, the JMS resources can not be
 * redeployed.
 *
 * @author <a href="ataylor@redhat.com">Andy Taylor</a>
 * @author <a href="jmesnil@redhat.com">Jeff Mesnil</a>
 * @author <a href="tim.fox@jboss.com">Tim Fox</a>
 */
public class JMSServerManagerImpl implements JMSServerManager, ActivateCallback
{
   private static final Logger log = Logger.getLogger(JMSServerManagerImpl.class);

   private static final String REJECT_FILTER = "__HQX=-1";

   /**
    * the context to bind to
    */
   private Context context;

   private BindingRegistry registry;

   private Map<String, HornetQQueue> queues = new HashMap<String, HornetQQueue>();


   private Map<String, HornetQTopic> topics = new HashMap<String, HornetQTopic>();

   private final Map<String, HornetQConnectionFactory> connectionFactories = new HashMap<String, HornetQConnectionFactory>();

   private final Map<String, List<String>> queueJNDI = new HashMap<String, List<String>>();

   private final Map<String, List<String>> topicJNDI = new HashMap<String, List<String>>();

   private final Map<String, List<String>> connectionFactoryJNDI = new HashMap<String, List<String>>();

   private final HornetQServer server;

   private JMSManagementService jmsManagementService;

   private XmlDeployer jmsDeployer;

   private boolean started;

   private boolean active;

   private DeploymentManager deploymentManager;

   private final String configFileName;

   private boolean contextSet;

   private JMSConfiguration config;

   private Configuration coreConfig;

   private JMSStorageManager storage;

   public JMSServerManagerImpl(final HornetQServer server) throws Exception
   {
      this.server = server;

      this.coreConfig = server.getConfiguration();

      configFileName = null;
   }

   public JMSServerManagerImpl(final HornetQServer server, final String configFileName) throws Exception
   {
      this.server = server;

      this.coreConfig = server.getConfiguration();

      this.configFileName = configFileName;
   }

   public JMSServerManagerImpl(final HornetQServer server, final JMSConfiguration configuration) throws Exception
   {
      this.server = server;

      this.coreConfig = server.getConfiguration();

      configFileName = null;

      config = configuration;
   }

   public JMSServerManagerImpl(HornetQServer server, String configFilename, JMSStorageManager storageManager)
   {
      this.server = server;

      configFileName = null;

      storage = storageManager;
   }

   // ActivateCallback implementation -------------------------------------

   public void preActivate()
   {

   }

   public synchronized void activated()
   {
      active = true;

      jmsManagementService = new JMSManagementServiceImpl(server.getManagementService(), this);

      try
      {
         jmsManagementService.registerJMSServer(this);

         initJournal();

         // start the JMS deployer only if the configuration is not done using the JMSConfiguration object
         if (config == null)
         {
            if (server.getConfiguration().isFileDeploymentEnabled())
            {
               jmsDeployer = new JMSServerDeployer(this, deploymentManager);

               if (configFileName != null)
               {
                  jmsDeployer.setConfigFileNames(new String[]{configFileName});
               }

               jmsDeployer.start();

               deploymentManager.start();
            }
         }
         else
         {
            deploy();
         }

      }
      catch (Exception e)
      {
         JMSServerManagerImpl.log.error("Failed to start jms deployer", e);
      }
   }

   // HornetQComponent implementation -----------------------------------

   public synchronized void start() throws Exception
   {
      if (started)
      {
         return;
      }

      if (registry == null && !contextSet)
      {
         context = new InitialContext();
         registry = new JndiComponentRegistry(context);
      }

      deploymentManager = new FileDeploymentManager(server.getConfiguration().getFileDeployerScanPeriod());

      server.registerActivateCallback(this);

      server.start();

      if (server.getReplicationEndpoint() != null)
      {
         createJournal();
         storage.installReplication(server.getReplicationEndpoint());
      }

      started = true;
   }

   public synchronized void stop() throws Exception
   {
      if (!started)
      {
         return;
      }

      if (jmsDeployer != null)
      {
         jmsDeployer.stop();
      }

      if (deploymentManager != null)
      {
         deploymentManager.stop();
      }

      // Storage could be null on a shared store backup server before initialization
      if (storage != null)
      {
         storage.stop();
      }

      unbindJNDI(queueJNDI);

      unbindJNDI(topicJNDI);

      unbindJNDI(connectionFactoryJNDI);

      for (String connectionFactory : new HashSet<String>(connectionFactories.keySet()))
      {
         destroyConnectionFactory(connectionFactory);
      }

      connectionFactories.clear();
      connectionFactoryJNDI.clear();

      queueJNDI.clear();
      queues.clear();

      topicJNDI.clear();
      topics.clear();

      if (registry != null)
      {
         registry.close();
      }

      // it could be null if a backup
      if (jmsManagementService != null)
      {
         jmsManagementService.unregisterJMSServer();

         jmsManagementService.stop();
      }

      server.stop();

      started = false;
   }

   public void setRegistry(BindingRegistry registry)
   {
      this.registry = registry;
   }

   public boolean isStarted()
   {
      return server.isStarted();
   }

   // JMSServerManager implementation -------------------------------

   public HornetQServer getHornetQServer()
   {
      return server;
   }

   public void addAddressSettings(final String address, final AddressSettings addressSettings)
   {
      server.getAddressSettingsRepository().addMatch(address, addressSettings);
   }

   public AddressSettings getAddressSettings(final String address)
   {
      return server.getAddressSettingsRepository().getMatch(address);
   }

   public void addSecurity(final String addressMatch, final Set<Role> roles)
   {
      server.getSecurityRepository().addMatch(addressMatch, roles);
   }

   public Set<Role> getSecurity(final String addressMatch)
   {
      return server.getSecurityRepository().getMatch(addressMatch);
   }

   public synchronized void setContext(final Context context)
   {
      this.context = context;

      contextSet = true;
   }

   public synchronized String getVersion()
   {
      checkInitialised();

      return server.getVersion().getFullVersion();
   }

   public synchronized boolean createQueue(final boolean storeConfig,
                                           final String queueName,
                                           final String selectorString,
                                           final boolean durable,
                                           final String... jndi) throws Exception
   {
      checkInitialised();

      boolean added = internalCreateQueue(queueName, selectorString, durable);

      if (!added)
      {
         return false;
      }

      HornetQDestination destination = queues.get(queueName);
      if (destination == null)
      {
         // sanity check. internalCreateQueue should already have done this check
         throw new IllegalArgumentException("Queue does not exist");
      }

      ArrayList<String> bindings = new ArrayList<String>();

      for (String jndiItem : jndi)
      {
         if (bindToJndi(jndiItem, destination))
         {
            bindings.add(jndiItem);
         }
      }

      String[] usedJNDI = bindings.toArray(new String[bindings.size()]);
      addToBindings(queueJNDI, queueName, usedJNDI);

      if (storeConfig && durable)
      {
         storage.storeDestination(new PersistedDestination(PersistedType.Queue, queueName, selectorString, durable));
         storage.addJNDI(PersistedType.Queue, queueName, usedJNDI);
      }

      return added;
   }

   public synchronized boolean createTopic(final boolean storeConfig, final String topicName, final String... jndi) throws Exception
   {
      checkInitialised();

      boolean added = internalCreateTopic(topicName);

      if (!added)
      {
         return false;
      }
      else
      {
         HornetQDestination destination = topics.get(topicName);

         if (destination == null)
         {
            // sanity check. internalCreateQueue should already have done this check
            throw new IllegalArgumentException("Queue does not exist");
         }

         ArrayList<String> bindings = new ArrayList<String>();

         for (String jndiItem : jndi)
         {
            if (bindToJndi(jndiItem, destination))
            {
               bindings.add(jndiItem);
            }
         }

         String[] usedJNDI = bindings.toArray(new String[bindings.size()]);
         addToBindings(topicJNDI, topicName, usedJNDI);

         if (storeConfig)
         {
            storage.storeDestination(new PersistedDestination(PersistedType.Topic, topicName));
            storage.addJNDI(PersistedType.Topic, topicName, usedJNDI);
         }

         return true;
      }
   }

   public boolean addTopicToJndi(final String topicName, final String jndiBinding) throws Exception
   {
      checkInitialised();

      HornetQTopic destination = topics.get(topicName);
      if (destination == null)
      {
         throw new IllegalArgumentException("Topic does not exist");
      }
      if (destination.getTopicName() == null)
      {
         throw new IllegalArgumentException(topicName + " is not a topic");
      }
      boolean added = bindToJndi(jndiBinding, destination);

      if (added)
      {
         addToBindings(topicJNDI, topicName, jndiBinding);
         storage.addJNDI(PersistedType.Topic, topicName, jndiBinding);
      }
      return added;
   }

   public String[] getJNDIOnQueue(String queue)
   {
      return getJNDIList(queueJNDI, queue);
   }

   public String[] getJNDIOnTopic(String topic)
   {
      return getJNDIList(topicJNDI, topic);
   }

   public String[] getJNDIOnConnectionFactory(String factoryName)
   {
      return getJNDIList(connectionFactoryJNDI, factoryName);
   }

   public boolean addQueueToJndi(final String queueName, final String jndiBinding) throws Exception
   {
      checkInitialised();

      HornetQQueue destination = queues.get(queueName);
      if (destination == null)
      {
         throw new IllegalArgumentException("Queue does not exist");
      }
      if (destination.getQueueName() == null)
      {
         throw new IllegalArgumentException(queueName + " is not a queue");
      }
      boolean added = bindToJndi(jndiBinding, destination);
      if (added)
      {
         addToBindings(queueJNDI, queueName, jndiBinding);
         storage.addJNDI(PersistedType.Queue, queueName, jndiBinding);
      }
      return added;
   }

   public boolean addConnectionFactoryToJNDI(final String name, final String jndiBinding) throws Exception
   {
      checkInitialised();

      HornetQConnectionFactory factory = connectionFactories.get(name);
      if (factory == null)
      {
         throw new IllegalArgumentException("Factory does not exist");
      }
      boolean added = bindToJndi(jndiBinding, factory);
      if (added)
      {
         addToBindings(connectionFactoryJNDI, name, jndiBinding);
         storage.addJNDI(PersistedType.ConnectionFactory, name, jndiBinding);
      }
      return added;
   }

   /* (non-Javadoc)
    * @see org.hornetq.jms.server.JMSServerManager#removeQueueFromJNDI(java.lang.String, java.lang.String)
    */

   public boolean removeQueueFromJNDI(String name, String jndi) throws Exception
   {
      checkInitialised();

      boolean removed = removeFromJNDI(queueJNDI, name, jndi);

      if (removed)
      {
         storage.deleteJNDI(PersistedType.Queue, name, jndi);
      }

      return removed;
   }

   /* (non-Javadoc)
    * @see org.hornetq.jms.server.JMSServerManager#removeQueueFromJNDI(java.lang.String, java.lang.String)
    */

   public boolean removeQueueFromJNDI(String name) throws Exception
   {
      checkInitialised();

      if (removeFromJNDI(queues, queueJNDI, name))
      {
         storage.deleteDestination(PersistedType.Queue, name);
         return true;
      }
      else
      {
         return false;
      }
   }

   /* (non-Javadoc)
    * @see org.hornetq.jms.server.JMSServerManager#removeTopicFromJNDI(java.lang.String, java.lang.String)
    */

   public boolean removeTopicFromJNDI(String name, String jndi) throws Exception
   {
      checkInitialised();

      if (removeFromJNDI(topicJNDI, name, jndi))
      {
         storage.deleteJNDI(PersistedType.Topic, name, jndi);
         return true;
      }
      else
      {
         return false;
      }
   }

   /* (non-Javadoc)
   * @see org.hornetq.jms.server.JMSServerManager#removeTopicFromJNDI(java.lang.String, java.lang.String)
   */

   public boolean removeTopicFromJNDI(String name) throws Exception
   {
      checkInitialised();

      boolean removed = removeFromJNDI(topics, topicJNDI, name);

      if (removed)
      {
         storage.deleteDestination(PersistedType.Topic, name);
      }

      return removed;
   }

   /* (non-Javadoc)
    * @see org.hornetq.jms.server.JMSServerManager#removeConnectionFactoryFromJNDI(java.lang.String, java.lang.String)
    */

   public boolean removeConnectionFactoryFromJNDI(String name, String jndi) throws Exception
   {
      checkInitialised();

      removeFromJNDI(connectionFactoryJNDI, name, jndi);

      storage.deleteJNDI(PersistedType.ConnectionFactory, name, jndi);

      return true;
   }

   /* (non-Javadoc)
    * @see org.hornetq.jms.server.JMSServerManager#removeConnectionFactoryFromJNDI(java.lang.String, java.lang.String)
    */

   public boolean removeConnectionFactoryFromJNDI(String name) throws Exception
   {
      checkInitialised();

      removeFromJNDI(connectionFactories, connectionFactoryJNDI, name);

      storage.deleteConnectionFactory(name);

      return true;
   }

   public synchronized boolean destroyQueue(final String name) throws Exception
   {
      checkInitialised();

      removeFromJNDI(queues, queueJNDI, name);

      queues.remove(name);
      queueJNDI.remove(name);

      jmsManagementService.unregisterQueue(name);

      server.destroyQueue(HornetQDestination.createQueueAddressFromName(name), null);

      storage.deleteDestination(PersistedType.Queue, name);

      return true;
   }

   public synchronized boolean destroyTopic(final String name) throws Exception
   {
      checkInitialised();

      removeFromJNDI(topics, topicJNDI, name);

      topics.remove(name);
      topicJNDI.remove(name);

      jmsManagementService.unregisterTopic(name);

      AddressControl addressControl = (AddressControl) server.getManagementService()
              .getResource(ResourceNames.CORE_ADDRESS + HornetQDestination.createTopicAddressFromName(name));
      if (addressControl != null)
      {
         for (String queueName : addressControl.getQueueNames())
         {
            Binding binding = server.getPostOffice().getBinding(new SimpleString(queueName));
            if (binding == null)
            {
               log.warn("Queue " + queueName +
                       " doesn't exist on the topic " +
                       name +
                       ". It was deleted manually probably.");
               continue;
            }

            // We can't remove the remote binding. As this would be the bridge associated with the topic on this case
            if (binding.getType() != BindingType.REMOTE_QUEUE)
            {
               server.destroyQueue(SimpleString.toSimpleString(queueName), null);
            }
         }
      }
      storage.deleteDestination(PersistedType.Topic, name);
      return true;
   }

   public synchronized void createConnectionFactory(final String name,
                                                    final List<Pair<TransportConfiguration, TransportConfiguration>> connectorConfigs,
                                                    String... jndiBindings) throws Exception
   {
      checkInitialised();
      HornetQConnectionFactory cf = connectionFactories.get(name);
      if (cf == null)
      {
         ConnectionFactoryConfiguration configuration = new ConnectionFactoryConfigurationImpl(name, connectorConfigs);
         createConnectionFactory(true, configuration, jndiBindings);
      }
   }

   public synchronized void createConnectionFactory(final String name,
                                                    final String clientID,
                                                    final List<Pair<TransportConfiguration, TransportConfiguration>> connectorConfigs,
                                                    String... jndiBindings) throws Exception
   {
      checkInitialised();
      HornetQConnectionFactory cf = connectionFactories.get(name);
      if (cf == null)
      {
         ConnectionFactoryConfiguration configuration = new ConnectionFactoryConfigurationImpl(name, connectorConfigs);
         configuration.setClientID(clientID);
         createConnectionFactory(true, configuration, jndiBindings);
      }
   }

   public synchronized void createConnectionFactory(final String name,
                                                    final List<Pair<TransportConfiguration, TransportConfiguration>> connectorConfigs,
                                                    final String clientID,
                                                    final long clientFailureCheckPeriod,
                                                    final long connectionTTL,
                                                    final long callTimeout,
                                                    final boolean cacheLargeMessagesClient,
                                                    final int minLargeMessageSize,
                                                    final int consumerWindowSize,
                                                    final int consumerMaxRate,
                                                    final int confirmationWindowSize,
                                                    final int producerWindowSize,
                                                    final int producerMaxRate,
                                                    final boolean blockOnAcknowledge,
                                                    final boolean blockOnDurableSend,
                                                    final boolean blockOnNonDurableSend,
                                                    final boolean autoGroup,
                                                    final boolean preAcknowledge,
                                                    final String loadBalancingPolicyClassName,
                                                    final int transactionBatchSize,
                                                    final int dupsOKBatchSize,
                                                    final boolean useGlobalPools,
                                                    final int scheduledThreadPoolMaxSize,
                                                    final int threadPoolMaxSize,
                                                    final long retryInterval,
                                                    final double retryIntervalMultiplier,
                                                    final long maxRetryInterval,
                                                    final int reconnectAttempts,
                                                    final boolean failoverOnInitialConnection,
                                                    final boolean failoverOnServerShutdown,
                                                    final String groupId,
                                                    String... jndiBindings) throws Exception
   {
      checkInitialised();
      HornetQConnectionFactory cf = connectionFactories.get(name);
      if (cf == null)
      {
         ConnectionFactoryConfiguration configuration = new ConnectionFactoryConfigurationImpl(name, connectorConfigs);
         configuration.setClientID(clientID);
         configuration.setClientFailureCheckPeriod(clientFailureCheckPeriod);
         configuration.setConnectionTTL(connectionTTL);
         configuration.setCallTimeout(callTimeout);
         configuration.setCacheLargeMessagesClient(cacheLargeMessagesClient);
         configuration.setMinLargeMessageSize(minLargeMessageSize);
         configuration.setConsumerWindowSize(consumerWindowSize);
         configuration.setConsumerMaxRate(consumerMaxRate);
         configuration.setConfirmationWindowSize(confirmationWindowSize);
         configuration.setProducerWindowSize(producerWindowSize);
         configuration.setProducerMaxRate(producerMaxRate);
         configuration.setBlockOnAcknowledge(blockOnAcknowledge);
         configuration.setBlockOnDurableSend(blockOnDurableSend);
         configuration.setBlockOnNonDurableSend(blockOnNonDurableSend);
         configuration.setAutoGroup(autoGroup);
         configuration.setPreAcknowledge(preAcknowledge);
         configuration.setLoadBalancingPolicyClassName(loadBalancingPolicyClassName);
         configuration.setTransactionBatchSize(transactionBatchSize);
         configuration.setDupsOKBatchSize(dupsOKBatchSize);
         configuration.setUseGlobalPools(useGlobalPools);
         configuration.setScheduledThreadPoolMaxSize(scheduledThreadPoolMaxSize);
         configuration.setThreadPoolMaxSize(threadPoolMaxSize);
         configuration.setRetryInterval(retryInterval);
         configuration.setRetryIntervalMultiplier(retryIntervalMultiplier);
         configuration.setMaxRetryInterval(maxRetryInterval);
         configuration.setReconnectAttempts(reconnectAttempts);
         configuration.setFailoverOnInitialConnection(failoverOnInitialConnection);
         configuration.setFailoverOnServerShutdown(failoverOnServerShutdown);
         configuration.setGroupID(groupId);
         createConnectionFactory(true, configuration, jndiBindings);
      }
   }

   public synchronized void createConnectionFactory(final String name,
                                                    final String localBindAddress,
                                                    final String discoveryAddress,
                                                    final int discoveryPort,
                                                    final String clientID,
                                                    final long discoveryRefreshTimeout,
                                                    final long discoveryInitialWaitTimeout,
                                                    final long clientFailureCheckPeriod,
                                                    final long connectionTTL,
                                                    final long callTimeout,
                                                    final boolean cacheLargeMessagesClient,
                                                    final int minLargeMessageSize,
                                                    final int consumerWindowSize,
                                                    final int consumerMaxRate,
                                                    final int confirmationWindowSize,
                                                    final int producerWindowSize,
                                                    final int producerMaxRate,
                                                    final boolean blockOnAcknowledge,
                                                    final boolean blockOnDurableSend,
                                                    final boolean blockOnNonDurableSend,
                                                    final boolean autoGroup,
                                                    final boolean preAcknowledge,
                                                    final String loadBalancingPolicyClassName,
                                                    final int transactionBatchSize,
                                                    final int dupsOKBatchSize,
                                                    final boolean useGlobalPools,
                                                    final int scheduledThreadPoolMaxSize,
                                                    final int threadPoolMaxSize,
                                                    final long retryInterval,
                                                    final double retryIntervalMultiplier,
                                                    final long maxRetryInterval,
                                                    final int reconnectAttempts,
                                                    final boolean failoverOnInitialConnection,
                                                    final boolean failoverOnServerShutdown,
                                                    final String groupId,
                                                    final String... jndiBindings) throws Exception
   {
      checkInitialised();
      HornetQConnectionFactory cf = connectionFactories.get(name);
      if (cf == null)
      {
         ConnectionFactoryConfiguration configuration = new ConnectionFactoryConfigurationImpl(name,
                 localBindAddress,
                 discoveryAddress,
                 discoveryPort);
         configuration.setClientID(clientID);
         configuration.setDiscoveryRefreshTimeout(discoveryRefreshTimeout);
         configuration.setInitialWaitTimeout(discoveryInitialWaitTimeout);
         configuration.setClientFailureCheckPeriod(clientFailureCheckPeriod);
         configuration.setConnectionTTL(connectionTTL);
         configuration.setCallTimeout(callTimeout);
         configuration.setCacheLargeMessagesClient(cacheLargeMessagesClient);
         configuration.setMinLargeMessageSize(minLargeMessageSize);
         configuration.setConsumerWindowSize(consumerWindowSize);
         configuration.setConsumerMaxRate(consumerMaxRate);
         configuration.setConfirmationWindowSize(confirmationWindowSize);
         configuration.setProducerWindowSize(producerWindowSize);
         configuration.setProducerMaxRate(producerMaxRate);
         configuration.setBlockOnAcknowledge(blockOnAcknowledge);
         configuration.setBlockOnDurableSend(blockOnDurableSend);
         configuration.setBlockOnNonDurableSend(blockOnNonDurableSend);
         configuration.setAutoGroup(autoGroup);
         configuration.setPreAcknowledge(preAcknowledge);
         configuration.setLoadBalancingPolicyClassName(loadBalancingPolicyClassName);
         configuration.setTransactionBatchSize(transactionBatchSize);
         configuration.setDupsOKBatchSize(dupsOKBatchSize);
         configuration.setUseGlobalPools(useGlobalPools);
         configuration.setScheduledThreadPoolMaxSize(scheduledThreadPoolMaxSize);
         configuration.setThreadPoolMaxSize(threadPoolMaxSize);
         configuration.setRetryInterval(retryInterval);
         configuration.setRetryIntervalMultiplier(retryIntervalMultiplier);
         configuration.setMaxRetryInterval(maxRetryInterval);
         configuration.setReconnectAttempts(reconnectAttempts);
         configuration.setFailoverOnInitialConnection(failoverOnInitialConnection);
         configuration.setFailoverOnServerShutdown(failoverOnServerShutdown);
         createConnectionFactory(true, configuration, jndiBindings);
      }
   }

   public synchronized void createConnectionFactory(final String name,
                                                    final String discoveryAddress,
                                                    final int discoveryPort,
                                                    final String... jndiBindings) throws Exception
   {
      checkInitialised();
      HornetQConnectionFactory cf = connectionFactories.get(name);
      if (cf == null)
      {
         ConnectionFactoryConfiguration configuration = new ConnectionFactoryConfigurationImpl(name,
                 discoveryAddress,
                 discoveryPort);
         createConnectionFactory(true, configuration, jndiBindings);
      }
   }

   public synchronized void createConnectionFactory(final String name,
                                                    final String clientID,
                                                    final String discoveryAddress,
                                                    final int discoveryPort,
                                                    final String... jndiBindings) throws Exception
   {
      checkInitialised();

      HornetQConnectionFactory cf = connectionFactories.get(name);
      if (cf == null)
      {
         ConnectionFactoryConfiguration configuration = new ConnectionFactoryConfigurationImpl(name,
                 discoveryAddress,
                 discoveryPort);
         configuration.setClientID(clientID);
         createConnectionFactory(true, configuration, jndiBindings);
      }
   }

   public synchronized void createConnectionFactory(final boolean storeConfig,
                                                    final ConnectionFactoryConfiguration cfConfig,
                                                    String... jndi) throws Exception
   {
      HornetQConnectionFactory cf = internalCreateCF(cfConfig);

      ArrayList<String> bindings = new ArrayList<String>();

      for (String jndiItem : jndi)
      {
         if (bindToJndi(jndiItem, cf))
         {
            bindings.add(jndiItem);
         }
      }

      String[] usedJNDI = bindings.toArray(new String[bindings.size()]);
      addToBindings(connectionFactoryJNDI, cfConfig.getName(), usedJNDI);

      if (storeConfig)
      {
         storage.storeConnectionFactory(new PersistedConnectionFactory(cfConfig));
         storage.addJNDI(PersistedType.ConnectionFactory, cfConfig.getName(), usedJNDI);
      }
   }

   private HornetQConnectionFactory internalCreateConnectionFactory(final String name,
                                                                    final String localBindAddress,
                                                                    final String discoveryAddress,
                                                                    final int discoveryPort,
                                                                    final String clientID,
                                                                    final long discoveryRefreshTimeout,
                                                                    final long discoveryInitialWaitTimeout,
                                                                    final long clientFailureCheckPeriod,
                                                                    final long connectionTTL,
                                                                    final long callTimeout,
                                                                    final boolean cacheLargeMessagesClient,
                                                                    final int minLargeMessageSize,
                                                                    final int consumerWindowSize,
                                                                    final int consumerMaxRate,
                                                                    final int confirmationWindowSize,
                                                                    final int producerWindowSize,
                                                                    final int producerMaxRate,
                                                                    final boolean blockOnAcknowledge,
                                                                    final boolean blockOnDurableSend,
                                                                    final boolean blockOnNonDurableSend,
                                                                    final boolean autoGroup,
                                                                    final boolean preAcknowledge,
                                                                    final String loadBalancingPolicyClassName,
                                                                    final int transactionBatchSize,
                                                                    final int dupsOKBatchSize,
                                                                    final boolean useGlobalPools,
                                                                    final int scheduledThreadPoolMaxSize,
                                                                    final int threadPoolMaxSize,
                                                                    final long retryInterval,
                                                                    final double retryIntervalMultiplier,
                                                                    final long maxRetryInterval,
                                                                    final int reconnectAttempts,
                                                                    final boolean failoverOnInitialConnection,
                                                                    final boolean failoverOnServerShutdown,
                                                                    final String groupId) throws Exception
   {
      checkInitialised();
      HornetQConnectionFactory cf = connectionFactories.get(name);
      if (cf == null)
      {
         cf = (HornetQConnectionFactory) HornetQJMSClient.createConnectionFactory(discoveryAddress, discoveryPort);
         cf.setClientID(clientID);
         cf.setLocalBindAddress(localBindAddress);
         cf.setDiscoveryRefreshTimeout(discoveryRefreshTimeout);
         cf.setDiscoveryInitialWaitTimeout(discoveryInitialWaitTimeout);
         cf.setClientFailureCheckPeriod(clientFailureCheckPeriod);
         cf.setConnectionTTL(connectionTTL);
         cf.setCallTimeout(callTimeout);
         cf.setCacheLargeMessagesClient(cacheLargeMessagesClient);
         cf.setMinLargeMessageSize(minLargeMessageSize);
         cf.setConsumerWindowSize(consumerWindowSize);
         cf.setConsumerMaxRate(consumerMaxRate);
         cf.setConfirmationWindowSize(confirmationWindowSize);
         cf.setProducerWindowSize(producerWindowSize);
         cf.setProducerMaxRate(producerMaxRate);
         cf.setBlockOnAcknowledge(blockOnAcknowledge);
         cf.setBlockOnDurableSend(blockOnDurableSend);
         cf.setBlockOnNonDurableSend(blockOnNonDurableSend);
         cf.setAutoGroup(autoGroup);
         cf.setPreAcknowledge(preAcknowledge);
         cf.setConnectionLoadBalancingPolicyClassName(loadBalancingPolicyClassName);
         cf.setTransactionBatchSize(transactionBatchSize);
         cf.setDupsOKBatchSize(dupsOKBatchSize);
         cf.setUseGlobalPools(useGlobalPools);
         cf.setScheduledThreadPoolMaxSize(scheduledThreadPoolMaxSize);
         cf.setThreadPoolMaxSize(threadPoolMaxSize);
         cf.setRetryInterval(retryInterval);
         cf.setRetryIntervalMultiplier(retryIntervalMultiplier);
         cf.setMaxRetryInterval(maxRetryInterval);
         cf.setReconnectAttempts(reconnectAttempts);
         cf.setFailoverOnInitialConnection(failoverOnInitialConnection);
         cf.setFailoverOnServerShutdown(failoverOnServerShutdown);
      }

      return cf;
   }

   private HornetQConnectionFactory internalCreateConnectionFactory(final String name,
                                                                    final List<Pair<TransportConfiguration, TransportConfiguration>> connectorConfigs,
                                                                    final String clientID,
                                                                    final long clientFailureCheckPeriod,
                                                                    final long connectionTTL,
                                                                    final long callTimeout,
                                                                    final boolean cacheLargeMessagesClient,
                                                                    final int minLargeMessageSize,
                                                                    final int consumerWindowSize,
                                                                    final int consumerMaxRate,
                                                                    final int confirmationWindowSize,
                                                                    final int producerWindowSize,
                                                                    final int producerMaxRate,
                                                                    final boolean blockOnAcknowledge,
                                                                    final boolean blockOnDurableSend,
                                                                    final boolean blockOnNonDurableSend,
                                                                    final boolean autoGroup,
                                                                    final boolean preAcknowledge,
                                                                    final String loadBalancingPolicyClassName,
                                                                    final int transactionBatchSize,
                                                                    final int dupsOKBatchSize,
                                                                    final boolean useGlobalPools,
                                                                    final int scheduledThreadPoolMaxSize,
                                                                    final int threadPoolMaxSize,
                                                                    final long retryInterval,
                                                                    final double retryIntervalMultiplier,
                                                                    final long maxRetryInterval,
                                                                    final int reconnectAttempts,
                                                                    final boolean failoverOnInitialConnection,
                                                                    final boolean failoverOnServerShutdown,
                                                                    final String groupId) throws Exception
   {
      checkInitialised();
      HornetQConnectionFactory cf = connectionFactories.get(name);
      if (cf == null)
      {
         cf = (HornetQConnectionFactory) HornetQJMSClient.createConnectionFactory(connectorConfigs);
         cf.setClientID(clientID);
         cf.setClientFailureCheckPeriod(clientFailureCheckPeriod);
         cf.setConnectionTTL(connectionTTL);
         cf.setCallTimeout(callTimeout);
         cf.setCacheLargeMessagesClient(cacheLargeMessagesClient);
         cf.setMinLargeMessageSize(minLargeMessageSize);
         cf.setConsumerWindowSize(consumerWindowSize);
         cf.setConsumerMaxRate(consumerMaxRate);
         cf.setConfirmationWindowSize(confirmationWindowSize);
         cf.setProducerWindowSize(producerWindowSize);
         cf.setProducerMaxRate(producerMaxRate);
         cf.setBlockOnAcknowledge(blockOnAcknowledge);
         cf.setBlockOnDurableSend(blockOnDurableSend);
         cf.setBlockOnNonDurableSend(blockOnNonDurableSend);
         cf.setAutoGroup(autoGroup);
         cf.setPreAcknowledge(preAcknowledge);
         cf.setConnectionLoadBalancingPolicyClassName(loadBalancingPolicyClassName);
         cf.setTransactionBatchSize(transactionBatchSize);
         cf.setDupsOKBatchSize(dupsOKBatchSize);
         cf.setUseGlobalPools(useGlobalPools);
         cf.setScheduledThreadPoolMaxSize(scheduledThreadPoolMaxSize);
         cf.setThreadPoolMaxSize(threadPoolMaxSize);
         cf.setRetryInterval(retryInterval);
         cf.setRetryIntervalMultiplier(retryIntervalMultiplier);
         cf.setMaxRetryInterval(maxRetryInterval);
         cf.setReconnectAttempts(reconnectAttempts);
         cf.setFailoverOnInitialConnection(failoverOnInitialConnection);
         cf.setFailoverOnServerShutdown(failoverOnServerShutdown);
         cf.setGroupID(groupId);
      }
      return cf;
   }

   private String[] getJNDIList(final Map<String, List<String>> map, final String name)
   {
      List<String> result = map.get(name);
      if (result == null)
      {
         return new String[0];
      }
      else
      {
         String[] strings = new String[result.size()];
         result.toArray(strings);
         return strings;
      }
   }

   private boolean internalCreateQueue(final String queueName, final String selectorString, final boolean durable) throws Exception
   {
      if (queues.get(queueName) != null)
      {
         return false;
      }
      else
      {
         HornetQQueue hqQueue = HornetQDestination.createQueue(queueName);

         // Convert from JMS selector to core filter
         String coreFilterString = null;

         if (selectorString != null)
         {
            coreFilterString = SelectorTranslator.convertToHornetQFilterString(selectorString);
         }

         server.deployQueue(SimpleString.toSimpleString(hqQueue.getAddress()),
                 SimpleString.toSimpleString(hqQueue.getAddress()),
                 SimpleString.toSimpleString(coreFilterString),
                 durable,
                 false);
         queues.put(queueName, hqQueue);

         jmsManagementService.registerQueue(hqQueue);

         return true;
      }
   }

   /**
    * Performs the internal creation without activating any storage.
    * The storage load will call this method
    *
    * @param topicName
    * @return
    * @throws Exception
    */
   private boolean internalCreateTopic(final String topicName) throws Exception
   {

      if (topics.get(topicName) != null)
      {
         return false;
      }
      else
      {
         HornetQTopic hqTopic = HornetQDestination.createTopic(topicName);
         // We create a dummy subscription on the topic, that never receives messages - this is so we can perform JMS
         // checks when routing messages to a topic that
         // does not exist - otherwise we would not be able to distinguish from a non existent topic and one with no
         // subscriptions - core has no notion of a topic
         server.deployQueue(SimpleString.toSimpleString(hqTopic.getAddress()),
                 SimpleString.toSimpleString(hqTopic.getAddress()),
                 SimpleString.toSimpleString(JMSServerManagerImpl.REJECT_FILTER),
                 true,
                 false);

         topics.put(topicName, hqTopic);

         jmsManagementService.registerTopic(hqTopic);

         return true;
      }
   }

   /**
    * @param cfConfig
    * @throws org.hornetq.api.core.HornetQException
    *
    * @throws Exception
    */
   private HornetQConnectionFactory internalCreateCF(final ConnectionFactoryConfiguration cfConfig) throws HornetQException,
           Exception
   {
      List<Pair<TransportConfiguration, TransportConfiguration>> connectorConfigs = lookupConnectors(cfConfig);

      lookupDiscovery(cfConfig);
      HornetQConnectionFactory cf;
      if (cfConfig.getDiscoveryAddress() != null)
      {
         cf = internalCreateConnectionFactory(cfConfig.getName(),
                 cfConfig.getLocalBindAddress(),
                 cfConfig.getDiscoveryAddress(),
                 cfConfig.getDiscoveryPort(),
                 cfConfig.getClientID(),
                 cfConfig.getDiscoveryRefreshTimeout(),
                 cfConfig.getInitialWaitTimeout(),
                 cfConfig.getClientFailureCheckPeriod(),
                 cfConfig.getConnectionTTL(),
                 cfConfig.getCallTimeout(),
                 cfConfig.isCacheLargeMessagesClient(),
                 cfConfig.getMinLargeMessageSize(),
                 cfConfig.getConsumerWindowSize(),
                 cfConfig.getConsumerMaxRate(),
                 cfConfig.getConfirmationWindowSize(),
                 cfConfig.getProducerWindowSize(),
                 cfConfig.getProducerMaxRate(),
                 cfConfig.isBlockOnAcknowledge(),
                 cfConfig.isBlockOnDurableSend(),
                 cfConfig.isBlockOnNonDurableSend(),
                 cfConfig.isAutoGroup(),
                 cfConfig.isPreAcknowledge(),
                 cfConfig.getLoadBalancingPolicyClassName(),
                 cfConfig.getTransactionBatchSize(),
                 cfConfig.getDupsOKBatchSize(),
                 cfConfig.isUseGlobalPools(),
                 cfConfig.getScheduledThreadPoolMaxSize(),
                 cfConfig.getThreadPoolMaxSize(),
                 cfConfig.getRetryInterval(),
                 cfConfig.getRetryIntervalMultiplier(),
                 cfConfig.getMaxRetryInterval(),
                 cfConfig.getReconnectAttempts(),
                 cfConfig.isFailoverOnInitialConnection(),
                 cfConfig.isFailoverOnServerShutdown(),
                 cfConfig.getGroupID());
      }
      else
      {
         cf = internalCreateConnectionFactory(cfConfig.getName(),
                 connectorConfigs,
                 cfConfig.getClientID(),
                 cfConfig.getClientFailureCheckPeriod(),
                 cfConfig.getConnectionTTL(),
                 cfConfig.getCallTimeout(),
                 cfConfig.isCacheLargeMessagesClient(),
                 cfConfig.getMinLargeMessageSize(),
                 cfConfig.getConsumerWindowSize(),
                 cfConfig.getConsumerMaxRate(),
                 cfConfig.getConfirmationWindowSize(),
                 cfConfig.getProducerWindowSize(),
                 cfConfig.getProducerMaxRate(),
                 cfConfig.isBlockOnAcknowledge(),
                 cfConfig.isBlockOnDurableSend(),
                 cfConfig.isBlockOnNonDurableSend(),
                 cfConfig.isAutoGroup(),
                 cfConfig.isPreAcknowledge(),
                 cfConfig.getLoadBalancingPolicyClassName(),
                 cfConfig.getTransactionBatchSize(),
                 cfConfig.getDupsOKBatchSize(),
                 cfConfig.isUseGlobalPools(),
                 cfConfig.getScheduledThreadPoolMaxSize(),
                 cfConfig.getThreadPoolMaxSize(),
                 cfConfig.getRetryInterval(),
                 cfConfig.getRetryIntervalMultiplier(),
                 cfConfig.getMaxRetryInterval(),
                 cfConfig.getReconnectAttempts(),
                 cfConfig.isFailoverOnInitialConnection(),
                 cfConfig.isFailoverOnServerShutdown(),
                 cfConfig.getGroupID());
      }
      connectionFactories.put(cfConfig.getName(), cf);

      jmsManagementService.registerConnectionFactory(cfConfig.getName(), cf);

      return cf;
   }

   public synchronized void createConnectionFactory(final String name,
                                                    final TransportConfiguration liveTC,
                                                    final String... jndiBindings) throws Exception
   {
      checkInitialised();
      HornetQConnectionFactory cf = connectionFactories.get(name);
      if (cf == null)
      {
         ConnectionFactoryConfiguration configuration = new ConnectionFactoryConfigurationImpl(name, liveTC);
         createConnectionFactory(true, configuration, jndiBindings);
      }
   }

   public synchronized void createConnectionFactory(final String name,
                                                    final String clientID,
                                                    final TransportConfiguration liveTC,
                                                    final String... jndiBindings) throws Exception
   {
      checkInitialised();
      HornetQConnectionFactory cf = connectionFactories.get(name);
      if (cf == null)
      {
         ConnectionFactoryConfiguration configuration = new ConnectionFactoryConfigurationImpl(name, liveTC);
         configuration.setClientID(clientID);
         createConnectionFactory(true, configuration, jndiBindings);
      }
   }

   public synchronized void createConnectionFactory(final String name,
                                                    final TransportConfiguration liveTC,
                                                    final TransportConfiguration backupTC,
                                                    final String... jndiBindings) throws Exception
   {
      checkInitialised();
      HornetQConnectionFactory cf = connectionFactories.get(name);
      if (cf == null)
      {
         ConnectionFactoryConfiguration configuration = new ConnectionFactoryConfigurationImpl(name, liveTC, backupTC);
         createConnectionFactory(true, configuration, jndiBindings);
      }
   }

   public synchronized void createConnectionFactory(final String name,
                                                    final String clientID,
                                                    final TransportConfiguration liveTC,
                                                    final TransportConfiguration backupTC,
                                                    final String... jndiBindings) throws Exception
   {
      checkInitialised();
      HornetQConnectionFactory cf = connectionFactories.get(name);
      if (cf == null)
      {
         ConnectionFactoryConfiguration configuration = new ConnectionFactoryConfigurationImpl(name, liveTC, backupTC);
         configuration.setClientID(clientID);
         createConnectionFactory(true, configuration, jndiBindings);
      }
   }

   public synchronized boolean destroyConnectionFactory(final String name) throws Exception
   {
      checkInitialised();
      List<String> jndiBindings = connectionFactoryJNDI.get(name);
      if (jndiBindings == null || jndiBindings.size() == 0)
      {
         return false;
      }
      if (registry != null)
      {
         for (String jndiBinding : jndiBindings)
         {
            try
            {
               registry.unbind(jndiBinding);
            }
            catch (Exception e)
            {
               // this is ok.
            }
         }
      }
      connectionFactoryJNDI.remove(name);
      connectionFactories.remove(name);

      jmsManagementService.unregisterConnectionFactory(name);

      return true;
   }

   public String[] listRemoteAddresses() throws Exception
   {
      checkInitialised();
      return server.getHornetQServerControl().listRemoteAddresses();
   }

   public String[] listRemoteAddresses(final String ipAddress) throws Exception
   {
      checkInitialised();
      return server.getHornetQServerControl().listRemoteAddresses(ipAddress);
   }

   public boolean closeConnectionsForAddress(final String ipAddress) throws Exception
   {
      checkInitialised();
      return server.getHornetQServerControl().closeConnectionsForAddress(ipAddress);
   }

   public String[] listConnectionIDs() throws Exception
   {
      return server.getHornetQServerControl().listConnectionIDs();
   }

   public String[] listSessions(final String connectionID) throws Exception
   {
      checkInitialised();
      return server.getHornetQServerControl().listSessions(connectionID);
   }

   // Public --------------------------------------------------------

   // Private -------------------------------------------------------

   private synchronized void checkInitialised()
   {
      if (!active)
      {
         throw new IllegalStateException("Cannot access JMS Server, core server is not yet active");
      }
   }

   private void addToBindings(Map<String, List<String>> map, String name, String... jndi)
   {
      List<String> list = map.get(name);
      if (list == null)
      {
         list = new ArrayList<String>();
         map.put(name, list);
      }
      for (String jndiItem : jndi)
      {
         list.add(jndiItem);
      }
   }

   private boolean bindToJndi(final String jndiName, final Object objectToBind) throws NamingException
   {
      if (registry != null)
      {
         return registry.bind(jndiName, objectToBind);
      }
      return true;
   }

   /**
    * @param cfConfig
    * @throws HornetQException
    */
   private void lookupDiscovery(final ConnectionFactoryConfiguration cfConfig) throws HornetQException
   {
      if (cfConfig.getDiscoveryGroupName() != null)
      {
         Configuration configuration = server.getConfiguration();

         DiscoveryGroupConfiguration discoveryGroupConfiguration = null;
         discoveryGroupConfiguration = configuration.getDiscoveryGroupConfigurations()
                 .get(cfConfig.getDiscoveryGroupName());

         if (discoveryGroupConfiguration == null)
         {
            JMSServerManagerImpl.log.warn("There is no discovery group with name '" + cfConfig.getDiscoveryGroupName() +
                    "' deployed.");

            throw new HornetQException(HornetQException.ILLEGAL_STATE,
                    "There is no discovery group with name '" + cfConfig.getDiscoveryGroupName() +
                            "' deployed.");
         }

         cfConfig.setLocalBindAddress(discoveryGroupConfiguration.getLocalBindAddress());
         cfConfig.setDiscoveryAddress(discoveryGroupConfiguration.getGroupAddress());
         cfConfig.setDiscoveryPort(discoveryGroupConfiguration.getGroupPort());
         cfConfig.setDiscoveryRefreshTimeout(discoveryGroupConfiguration.getRefreshTimeout());

      }
   }

   private void deploy() throws Exception
   {
      if (config == null)
      {
         return;
      }

      if (config.getContext() != null)
      {
         setContext(config.getContext());
      }

      List<ConnectionFactoryConfiguration> connectionFactoryConfigurations = config.getConnectionFactoryConfigurations();
      for (ConnectionFactoryConfiguration config : connectionFactoryConfigurations)
      {
         createConnectionFactory(false, config, config.getBindings());
      }

      List<JMSQueueConfiguration> queueConfigs = config.getQueueConfigurations();
      for (JMSQueueConfiguration config : queueConfigs)
      {
         String[] bindings = config.getBindings();
         createQueue(false, config.getName(), config.getSelector(), config.isDurable(), bindings);
      }

      List<TopicConfiguration> topicConfigs = config.getTopicConfigurations();
      for (TopicConfiguration config : topicConfigs)
      {
         String[] bindings = config.getBindings();
         createTopic(false, config.getName(), bindings);
      }
   }

   /**
    * @param param
    */
   private void unbindJNDI(Map<String, List<String>> param)
   {
      if (registry != null)
      {
         for (List<String> elementList : param.values())
         {
            for (String key : elementList)
            {
               try
               {
                  registry.unbind(key);
               }
               catch (Exception e)
               {
                  log.warn("Impossible to unbind key " + key + " from registry", e);
               }
            }
         }
      }
   }

   /**
    * @param server
    */
   private void initJournal() throws Exception
   {
      this.coreConfig = server.getConfiguration();

      createJournal();

      storage.load();

      List<PersistedConnectionFactory> cfs = storage.recoverConnectionFactories();

      for (PersistedConnectionFactory cf : cfs)
      {
         internalCreateCF(cf.getConfig());
      }

      List<PersistedDestination> destinations = storage.recoverDestinations();

      for (PersistedDestination destination : destinations)
      {
         if (destination.getType() == PersistedType.Queue)
         {
            internalCreateQueue(destination.getName(), destination.getSelector(), destination.isDurable());
         }
         else if (destination.getType() == PersistedType.Topic)
         {
            internalCreateTopic(destination.getName());
         }
      }

      List<PersistedJNDI> jndiSpace = storage.recoverPersistedJNDI();
      for (PersistedJNDI record : jndiSpace)
      {
         Map<String, List<String>> mapJNDI;
         Map<String, ?> objects;

         switch (record.getType())
         {
            case Queue:
               mapJNDI = queueJNDI;
               objects = queues;
               break;
            case Topic:
               mapJNDI = topicJNDI;
               objects = topics;
               break;
            default:
            case ConnectionFactory:
               mapJNDI = connectionFactoryJNDI;
               objects = connectionFactories;
               break;
         }

         Object objectToBind = objects.get(record.getName());

         if (objectToBind == null)
         {
            continue;
         }

         List<String> jndiList = mapJNDI.get(record.getName());
         if (jndiList == null)
         {
            jndiList = new ArrayList<String>();
            mapJNDI.put(record.getName(), jndiList);
         }

         for (String jndi : record.getJndi())
         {
            jndiList.add(jndi);
            bindToJndi(jndi, objectToBind);
         }
      }
   }

   /**
    * @throws Exception
    */
   private void createJournal() throws Exception
   {
      if (storage == null)
      {
         if (coreConfig.isPersistenceEnabled())
         {
            storage = new JMSJournalStorageManagerImpl(new TimeAndCounterIDGenerator(),
                    server.getConfiguration(),
                    server.getReplicationManager());
         }
         else
         {
            storage = new NullJMSStorageManagerImpl();
         }
      }
      else
      {
         if (storage.isStarted())
         {
            storage.stop();
         }
      }

      storage.start();
   }

   private synchronized boolean removeFromJNDI(final Map<String, ?> keys,
                                               final Map<String, List<String>> jndiMap,
                                               final String name) throws Exception
   {
      checkInitialised();
      List<String> jndiBindings = jndiMap.remove(name);
      if (jndiBindings == null || jndiBindings.size() == 0)
      {
         return false;
      }
      else
      {
         keys.remove(name);
      }
      if (registry != null)
      {
         Iterator<String> iter = jndiBindings.iterator();
         while (iter.hasNext())
         {
            String jndiBinding = iter.next();
            registry.unbind(jndiBinding);
            iter.remove();
         }
      }
      return true;
   }

   private synchronized boolean removeFromJNDI(final Map<String, List<String>> jndiMap,
                                               final String name,
                                               final String jndi) throws Exception
   {
      checkInitialised();
      List<String> jndiBindings = jndiMap.get(name);
      if (jndiBindings == null || jndiBindings.size() == 0)
      {
         return false;
      }

      if (jndiBindings.remove(jndi))
      {
         registry.unbind(jndi);
         return true;
      }
      else
      {
         return false;
      }
   }

   /**
    * @param cfConfig
    * @return
    * @throws HornetQException
    */
   private List<Pair<TransportConfiguration, TransportConfiguration>> lookupConnectors(final ConnectionFactoryConfiguration cfConfig) throws HornetQException
   {
      if (cfConfig.getConnectorConfigs() != null && cfConfig.getConnectorConfigs().size() > 0)
      {
         return cfConfig.getConnectorConfigs();
      }
      else if (cfConfig.getConnectorNames() != null)
      {
         Configuration configuration = server.getConfiguration();
         List<Pair<TransportConfiguration, TransportConfiguration>> connectorConfigs = new ArrayList<Pair<TransportConfiguration, TransportConfiguration>>();

         for (Pair<String, String> configConnector : cfConfig.getConnectorNames())
         {
            String connectorName = configConnector.a;
            String backupConnectorName = configConnector.b;

            TransportConfiguration connector = configuration.getConnectorConfigurations().get(connectorName);

            if (connector == null)
            {
               JMSServerManagerImpl.log.warn("There is no connector with name '" + connectorName + "' deployed.");
               throw new HornetQException(HornetQException.ILLEGAL_STATE,
                       "There is no connector with name '" + connectorName + "' deployed.");
            }

            TransportConfiguration backupConnector = null;

            if (backupConnectorName != null)
            {
               backupConnector = configuration.getConnectorConfigurations().get(backupConnectorName);

               if (backupConnector == null)
               {
                  JMSServerManagerImpl.log.warn("There is no backup connector with name '" + backupConnectorName +
                          "' deployed.");
                  throw new HornetQException(HornetQException.ILLEGAL_STATE,
                          "There is no backup connector with name '" + backupConnectorName +
                                  "' deployed.");
               }
            }

            connectorConfigs.add(new Pair<TransportConfiguration, TransportConfiguration>(connector, backupConnector));
         }
         return connectorConfigs;

      }
      else
      {
         return null;
      }
   }
}

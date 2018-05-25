package org.jboss.resteasy.plugins.server.netty;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.group.ChannelGroup;
import org.jboss.netty.channel.group.DefaultChannelGroup;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.embedded.EmbeddedJaxrsServer;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.spi.ResteasyDeployment;

import javax.net.ssl.SSLContext;
import javax.ws.rs.ApplicationPath;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

/**
 * An HTTP server that sends back the content of the received HTTP request
 * in a pretty plaintext form.
 *
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 * @author Norman Maurer
 * @version $Rev: 2080 $, $Date: 2010-01-26 18:04:19 +0900 (Tue, 26 Jan 2010) $
 */
public class NettyJaxrsServer implements EmbeddedJaxrsServer
{
   protected ServerBootstrap bootstrap;
   protected Channel channel;
   protected String hostname = null;
   protected int configuredPort = 8080;
   protected int runtimePort = -1;
   protected ResteasyDeployment deployment = new ResteasyDeployment();
   protected String root = "";
   protected SecurityDomain domain;
   private int ioWorkerCount = Runtime.getRuntime().availableProcessors() * 2;
   private int executorThreadCount = 16;
   private SSLContext sslContext;
   private int maxRequestSize = 1024 * 1024 * 10;
   private boolean isKeepAlive = true;
   private List<ChannelHandler> channelHandlers = Collections.emptyList();
   private Map<String, Object> channelOptions = Collections.emptyMap();

   static final ChannelGroup allChannels = new DefaultChannelGroup("NettyJaxrsServer");

   public void setSSLContext(SSLContext sslContext) 
   {
      this.sslContext = sslContext;
   }
   
   /**
    * Specify the worker count to use. For more informations about this please see the javadocs of {@link NioServerSocketChannelFactory}
    * 
    * @param ioWorkerCount worker count
    */
   public void setIoWorkerCount(int ioWorkerCount) 
   {
       this.ioWorkerCount = ioWorkerCount;
   }
   
   /**
    * Set the number of threads to use for the Executor. For more informations please see the javadocs of {@link OrderedMemoryAwareThreadPoolExecutor}. 
    * If you want to disable the use of the {@link ExecutionHandler} specify a value {@literal <=} 0.  This should only be done if you are 100% sure that you don't have any blocking
    * code in there.
    * 
    * 
    * @param executorThreadCount thread count
    */
   public void setExecutorThreadCount(int executorThreadCount)
   {
       this.executorThreadCount = executorThreadCount;
   }

   /**
    * Set the max. request size in bytes. If this size is exceed we will send a "413 Request Entity Too Large" to the client.
    * 
    * @param maxRequestSize the max request size. This is 10mb by default.
    */
   public void setMaxRequestSize(int maxRequestSize) 
   {
       this.maxRequestSize  = maxRequestSize;
   }
   
   public void setKeepAlive(boolean isKeepAlive) 
   {
       this.isKeepAlive = isKeepAlive;
   }

   public String getHostname() {
       return hostname;
   }

   public void setHostname(String hostname) {
       this.hostname = hostname;
   }

   public int getPort()
   {
      return runtimePort > 0 ? runtimePort : configuredPort;
   }

   public void setPort(int port)
   {
      this.configuredPort = port;
   }

    /**
     * Add additional {@link org.jboss.netty.channel.ChannelHandler}s to the {@link org.jboss.netty.bootstrap.ServerBootstrap}.
     * <p>The additional channel handlers are being added <em>before</em> the HTTP handling.</p>
     *
     * @param channelHandlers the additional {@link org.jboss.netty.channel.ChannelHandler}s.
     */
    public void setChannelHandlers(final List<ChannelHandler> channelHandlers) {
        this.channelHandlers = channelHandlers == null ? Collections.<ChannelHandler>emptyList() : channelHandlers;
    }

    /**
     * Add channel options to Netty {@link org.jboss.netty.bootstrap.ServerBootstrap}.
     *
     * @param channelOptions a {@link java.util.Map} containing the Netty bootstrap options.
     * @see org.jboss.netty.bootstrap.ServerBootstrap#setOptions(java.util.Map)
     */
    public void setChannelOptions(final Map<String, Object> channelOptions) {
        this.channelOptions = channelOptions == null ? Collections.<String, Object>emptyMap() : channelOptions;
    }

   @Override
   public void setDeployment(ResteasyDeployment deployment)
   {
      this.deployment = deployment;
   }

   @Override
   public void setRootResourcePath(String rootResourcePath)
   {
      root = rootResourcePath;
      if (root != null && root.equals("/")) root = "";
   }

   @Override
   public ResteasyDeployment getDeployment()
   {
      return deployment;
   }

   @Override
   public void setSecurityDomain(SecurityDomain sc)
   {
      this.domain = sc;
   }

   @Override
   public void start()
   {
      deployment.start();
      // dynamically set the root path (the user can rewrite it by calling setRootResourcePath)
      if (deployment.getApplication() != null) {
         ApplicationPath appPath = deployment.getApplication().getClass().getAnnotation(ApplicationPath.class);
         if (appPath != null && (root == null || "".equals(root))) {
            // annotation is present and original root is not set
            String path = appPath.value();
            setRootResourcePath(path);
         }
      }
      RequestDispatcher dispatcher = new RequestDispatcher((SynchronousDispatcher)deployment.getDispatcher(), deployment.getProviderFactory(), domain);

      // Configure the server.
      bootstrap = new ServerBootstrap(
              new NioServerSocketChannelFactory(
                      Executors.newCachedThreadPool(),
                      Executors.newCachedThreadPool(), 
                      ioWorkerCount));

      ChannelPipelineFactory factory;
      if (sslContext == null) {
          factory = new HttpServerPipelineFactory(dispatcher, root, executorThreadCount, maxRequestSize, isKeepAlive, channelHandlers);
      } else {
          factory = new HttpsServerPipelineFactory(dispatcher, root, executorThreadCount, maxRequestSize, isKeepAlive, channelHandlers, sslContext);
      }
      // Set up the event pipeline factory.
      bootstrap.setPipelineFactory(factory);

      // Add custom bootstrap options
      bootstrap.setOptions(channelOptions);

      // Bind and start to accept incoming connections.
      final InetSocketAddress socketAddress;
      if(null == hostname || hostname.isEmpty()) {
          socketAddress = new InetSocketAddress(configuredPort);
      } else {
          socketAddress = new InetSocketAddress(hostname, configuredPort);
      }

      channel = bootstrap.bind(socketAddress);
      allChannels.add(channel);
      runtimePort = ((InetSocketAddress) channel.getLocalAddress()).getPort();
   }

   @Override
   public void stop()
   {
      runtimePort = -1;
      allChannels.close().awaitUninterruptibly();
      if (bootstrap != null) {
          bootstrap.releaseExternalResources();
      }
      deployment.stop();
   }
}
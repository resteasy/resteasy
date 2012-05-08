package org.jboss.resteasy.plugins.server.netty;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.embedded.EmbeddedJaxrsServer;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.spi.ResteasyDeployment;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * An HTTP server that sends back the content of the received HTTP request
 * in a pretty plaintext form.
 *
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 * @version $Rev: 2080 $, $Date: 2010-01-26 18:04:19 +0900 (Tue, 26 Jan 2010) $
 */
public class JaxrsServer implements EmbeddedJaxrsServer
{
   protected ServerBootstrap bootstrap;
   protected Channel channel;
   protected int port = 8080;
   protected ResteasyDeployment deployment = new ResteasyDeployment();
   protected String root = "";
   protected SecurityDomain domain;
   private int ioWorkerCount = Runtime.getRuntime().availableProcessors() * 2;
   private int executorThreadCount = 16;

   public void setIoWorkerCount(int ioWorkerCount) 
   {
       this.ioWorkerCount = ioWorkerCount;
   }
   
   public void setExecutorThreadCount(int executorThreadCount)
   {
       this.executorThreadCount = executorThreadCount;
   }
   
   public int getPort()
   {
      return port;
   }

   public void setPort(int port)
   {
      this.port = port;
   }

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

   public void start()
   {
      deployment.start();
      RequestDispatcher dispatcher = new RequestDispatcher((SynchronousDispatcher)deployment.getDispatcher(), deployment.getProviderFactory(), domain);

      // Configure the server.
      ServerBootstrap bootstrap = new ServerBootstrap(
              new NioServerSocketChannelFactory(
                      Executors.newCachedThreadPool(),
                      Executors.newCachedThreadPool(), 
                      ioWorkerCount));

      ExecutionHandler executionHandler = null;
      if (executorThreadCount > 0) {
          executionHandler = new ExecutionHandler(new OrderedMemoryAwareThreadPoolExecutor(executorThreadCount, 0L, 0L));
      }
      // Set up the event pipeline factory.
      bootstrap.setPipelineFactory(new HttpServerPipelineFactory(dispatcher, root, executionHandler));

      // Bind and start to accept incoming connections.
      channel = bootstrap.bind(new InetSocketAddress(port));
   }

   public void stop()
   {
      ChannelFuture future = channel.close();
      try
      {
         future.await(5000);
      }
      catch (InterruptedException e)
      {

      }
   }
}
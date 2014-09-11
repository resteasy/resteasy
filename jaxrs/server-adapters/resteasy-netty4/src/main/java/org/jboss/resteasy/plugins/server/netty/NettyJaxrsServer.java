package org.jboss.resteasy.plugins.server.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.EventExecutor;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.embedded.EmbeddedJaxrsServer;
import org.jboss.resteasy.plugins.server.embedded.SecurityDomain;
import org.jboss.resteasy.spi.ResteasyDeployment;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import java.net.InetSocketAddress;

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
   protected ServerBootstrap bootstrap = new ServerBootstrap();
   protected String hostname = null;
   protected int port = 8080;
   protected ResteasyDeployment deployment = new ResteasyDeployment();
   protected String root = "";
   protected SecurityDomain domain;
   private EventLoopGroup eventLoopGroup;
   private EventLoopGroup eventExecutor;
   private int ioWorkerCount = Runtime.getRuntime().availableProcessors() * 2;
   private int executorThreadCount = 16;
   private SSLContext sslContext;
   private int maxRequestSize = 1024 * 1024 * 10;
   private int backlog = 128;

   public void setSSLContext(SSLContext sslContext)
   {
      this.sslContext = sslContext;
   }

   /**
    * Specify the worker count to use. For more information about this please see the javadocs of {@link EventLoopGroup}
    *
    * @param ioWorkerCount
    */
   public void setIoWorkerCount(int ioWorkerCount)
   {
       this.ioWorkerCount = ioWorkerCount;
   }

   /**
    * Set the number of threads to use for the EventExecutor. For more information please see the javadocs of {@link EventExecutor}.
    * If you want to disable the use of the {@link EventExecutor} specify a value <= 0.  This should only be done if you are 100% sure that you don't have any blocking
    * code in there.
    *
    * @param executorThreadCount
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

   public String getHostname() {
       return hostname;
   }

   public void setHostname(String hostname) {
       this.hostname = hostname;
   }

   public int getPort()
   {
      return port;
   }

   public void setPort(int port)
   {
      this.port = port;
   }

    public void setBacklog(int backlog)
    {
        this.backlog = backlog;
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

   protected RequestDispatcher createRequestDispatcher()
   {
       return new RequestDispatcher((SynchronousDispatcher)deployment.getDispatcher(),
               deployment.getProviderFactory(), domain);
   }

   @Override
   public void start()
   {
      eventLoopGroup = new NioEventLoopGroup(ioWorkerCount);
      eventExecutor = new NioEventLoopGroup(executorThreadCount);
      deployment.start();
      final RequestDispatcher dispatcher = this.createRequestDispatcher();
       // Configure the server.
       if (sslContext == null) {
           bootstrap.group(eventLoopGroup)
                   .channel(NioServerSocketChannel.class)
                   .childHandler(new ChannelInitializer<SocketChannel>() {
                       @Override
                       public void initChannel(SocketChannel ch) throws Exception {
                           ch.pipeline().addLast(new HttpRequestDecoder());
                           ch.pipeline().addLast(new HttpObjectAggregator(maxRequestSize));
                           ch.pipeline().addLast(new HttpResponseEncoder());
                           ch.pipeline().addLast(new RestEasyHttpRequestDecoder(dispatcher.getDispatcher(), root, RestEasyHttpRequestDecoder.Protocol.HTTP));
                           ch.pipeline().addLast(new RestEasyHttpResponseEncoder());
                           ch.pipeline().addLast(eventExecutor, new RequestHandler(dispatcher));
                       }
                   })
                   .option(ChannelOption.SO_BACKLOG, backlog)
                   .childOption(ChannelOption.SO_KEEPALIVE, true);
       } else {
           final SSLEngine engine = sslContext.createSSLEngine();
           engine.setUseClientMode(false);
           bootstrap.group(eventLoopGroup)
                   .channel(NioServerSocketChannel.class)
                   .childHandler(new ChannelInitializer<SocketChannel>() {
                       @Override
                       public void initChannel(SocketChannel ch) throws Exception {
                           ch.pipeline().addFirst(new SslHandler(engine));
                           ch.pipeline().addLast(new HttpRequestDecoder());
                           ch.pipeline().addLast(new HttpObjectAggregator(maxRequestSize));
                           ch.pipeline().addLast(new HttpResponseEncoder());
                           ch.pipeline().addLast(new RestEasyHttpRequestDecoder(dispatcher.getDispatcher(), root, RestEasyHttpRequestDecoder.Protocol.HTTPS));
                           ch.pipeline().addLast(new RestEasyHttpResponseEncoder());
                           ch.pipeline().addLast(eventExecutor, new RequestHandler(dispatcher));

                       }
                   })
                   .option(ChannelOption.SO_BACKLOG, backlog)
                   .childOption(ChannelOption.SO_KEEPALIVE, true);
       }

       final InetSocketAddress socketAddress;
       if(null == hostname || hostname.isEmpty()) {
           socketAddress = new InetSocketAddress(port);
       } else {
           socketAddress = new InetSocketAddress(hostname, port);
       }

       bootstrap.bind(socketAddress).syncUninterruptibly();
   }

   @Override
   public void stop()
   {
       eventLoopGroup.shutdownGracefully();
       eventExecutor.shutdownGracefully();
   }
}
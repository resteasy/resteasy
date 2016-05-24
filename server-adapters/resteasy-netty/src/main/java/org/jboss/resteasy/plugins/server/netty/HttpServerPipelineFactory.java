package org.jboss.resteasy.plugins.server.netty;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.execution.ExecutionHandler;
import org.jboss.netty.handler.execution.OrderedMemoryAwareThreadPoolExecutor;
import org.jboss.resteasy.plugins.server.netty.RestEasyHttpRequestDecoder.Protocol;

import java.util.List;

import static org.jboss.netty.channel.Channels.pipeline;

/**
 * The {@link ChannelPipelineFactory} which is used to serve HTTP Traffic.
 * 
 * 
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 * @author Norman Maurer
 * @version $Rev: 2080 $, $Date: 2010-01-26 18:04:19 +0900 (Tue, 26 Jan 2010) $
 */
public class HttpServerPipelineFactory implements ChannelPipelineFactory
{
   private final ChannelHandler resteasyEncoder;
   private final ChannelHandler resteasyDecoder;
   private final ChannelHandler resteasyRequestHandler;
   private final ChannelHandler executionHandler;
   private final List<ChannelHandler> additionalChannelHandlers;
   private final int maxRequestSize;
   
   public HttpServerPipelineFactory(RequestDispatcher dispatcher, String root, int executorThreadCount, int maxRequestSize, boolean isKeepAlive, List<ChannelHandler> additionalChannelHandlers)
   {
      this.resteasyDecoder = new RestEasyHttpRequestDecoder(dispatcher.getDispatcher(), root, getProtocol(), isKeepAlive);
      this.resteasyEncoder = new RestEasyHttpResponseEncoder(dispatcher);
      this.resteasyRequestHandler = new RequestHandler(dispatcher);
      if (executorThreadCount > 0) 
      {
          this.executionHandler = new ExecutionHandler(new OrderedMemoryAwareThreadPoolExecutor(executorThreadCount, 0L, 0L));
      } 
      else 
      {
          this.executionHandler = null;
      }
      this.maxRequestSize = maxRequestSize;
      this.additionalChannelHandlers = additionalChannelHandlers;
   }

   @Override
   public ChannelPipeline getPipeline() throws Exception
   {
      // Create a default pipeline implementation.
      ChannelPipeline pipeline = pipeline();

      // Add custom channel handlers
      for (ChannelHandler channelHandler : additionalChannelHandlers) {
         pipeline.addLast(channelHandler.getClass().getSimpleName(), channelHandler);
      }

      pipeline.addLast("decoder", new HttpRequestDecoder());
      pipeline.addLast("aggregator", new HttpChunkAggregator(maxRequestSize));
      pipeline.addLast("resteasyDecoder", resteasyDecoder);
      pipeline.addLast("encoder", new HttpResponseEncoder());
      pipeline.addLast("resteasyEncoder", resteasyEncoder);

      if (executionHandler != null) {
         pipeline.addLast("executionHandler", executionHandler);
      }
      
      pipeline.addLast("handler", resteasyRequestHandler);
      return pipeline;
   }
   
   protected Protocol getProtocol() {
       return Protocol.HTTP;
   }
}


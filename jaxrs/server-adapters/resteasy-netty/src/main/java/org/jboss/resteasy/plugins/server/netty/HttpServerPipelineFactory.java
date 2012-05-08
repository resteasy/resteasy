package org.jboss.resteasy.plugins.server.netty;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.execution.ExecutionHandler;

import static org.jboss.netty.channel.Channels.*;

/**
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 * @version $Rev: 2080 $, $Date: 2010-01-26 18:04:19 +0900 (Tue, 26 Jan 2010) $
 */
public class HttpServerPipelineFactory implements ChannelPipelineFactory
{
   private final ChannelHandler resteasyEncoder;
   private final ChannelHandler resteasyDecoder;
   private final ChannelHandler resteasyRequestHandler;
   private ChannelHandler executionHandler;
   
   public HttpServerPipelineFactory(RequestDispatcher dispatcher, String root, ExecutionHandler executionHandler)
   {
      this.resteasyEncoder = new RestEasyHttpRequestDecoder(dispatcher.getDispatcher(), root);
      this.resteasyDecoder = new RestEasyHttpResponseEncoder(dispatcher);
      this.resteasyRequestHandler = new RequestHandler(dispatcher);
      this.executionHandler = executionHandler;
   }

   public ChannelPipeline getPipeline() throws Exception
   {
      // Create a default pipeline implementation.
      ChannelPipeline pipeline = pipeline();

      // Uncomment the following line if you want HTTPS
      //SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();
      //engine.setUseClientMode(false);
      //pipeline.addLast("ssl", new SslHandler(engine));

      pipeline.addLast("decoder", new HttpRequestDecoder());
      pipeline.addLast("resteasyDecoder", resteasyDecoder);
      
      // Uncomment the following line if you don't want to handle HttpChunks.
      pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
      pipeline.addLast("resteasyEncoder", resteasyEncoder);
      pipeline.addLast("encoder", new HttpResponseEncoder());
      // Remove the following line if you don't want automatic content compression.
      //pipeline.addLast("deflater", new HttpContentCompressor());

      if (executionHandler != null) {
         pipeline.addLast("executionHandler", executionHandler);
      }
      
      pipeline.addLast("handler", resteasyRequestHandler);
      return pipeline;
   }
}


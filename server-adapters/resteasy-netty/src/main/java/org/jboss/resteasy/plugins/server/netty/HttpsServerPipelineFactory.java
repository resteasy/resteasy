package org.jboss.resteasy.plugins.server.netty;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.resteasy.plugins.server.netty.RestEasyHttpRequestDecoder.Protocol;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;

import java.util.List;

/**
 * {@link HttpServerPipelineFactory} subclass which enable the use of HTTPS
 *
 * @author Norman Maurer
 *
 */
public class HttpsServerPipelineFactory extends HttpServerPipelineFactory
{

   private final SSLContext context;

   private SSLParameters sslParameters;

   public HttpsServerPipelineFactory(final RequestDispatcher dispatcher, final String root, final int executorThreadCount, final int maxRequestSize, final boolean isKeepAlive, final List<ChannelHandler> additionalChannelHandlers, final SSLContext context)
   {
      super(dispatcher, root, executorThreadCount, maxRequestSize, isKeepAlive, additionalChannelHandlers);
      this.context = context;
   }

   @Override
   public ChannelPipeline getPipeline() throws Exception
   {
      ChannelPipeline cp = super.getPipeline();
      SSLEngine engine = context.createSSLEngine();
      engine.setUseClientMode(false);
      if (sslParameters != null)
      {
          engine.setNeedClientAuth(sslParameters.getNeedClientAuth());
          engine.setWantClientAuth(sslParameters.getWantClientAuth());
      }
      cp.addFirst("sslHandler", new SslHandler(engine));
      return cp;
   }

   @Override
   protected Protocol getProtocol()
   {
      return Protocol.HTTPS;
   }

   public void setSSLParameters(SSLParameters params)
   {
      this.sslParameters = params;
   }
}

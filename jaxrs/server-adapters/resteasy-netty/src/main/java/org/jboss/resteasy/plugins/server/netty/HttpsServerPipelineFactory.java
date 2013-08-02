package org.jboss.resteasy.plugins.server.netty;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.resteasy.plugins.server.netty.RestEasyHttpRequestDecoder.Protocol;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

/**
 * {@link HttpServerPipelineFactory} subclass which enable the use of HTTPS
 * 
 * @author Norman Maurer
 *
 */
public class HttpsServerPipelineFactory extends HttpServerPipelineFactory 
{

    private final SSLContext context;

    public HttpsServerPipelineFactory(RequestDispatcher dispatcher, String root, int executorThreadCount, int maxRequestSize, SSLContext context) 
    {
        super(dispatcher, root, executorThreadCount, maxRequestSize);
        this.context = context;
    }

    @Override
    public ChannelPipeline getPipeline() throws Exception 
    {
        ChannelPipeline cp = super.getPipeline();
        SSLEngine engine = context.createSSLEngine();
        engine.setUseClientMode(false);
        cp.addFirst("sslHandler", new SslHandler(engine));
        return cp;
    }

    @Override
    protected Protocol getProtocol() 
    {
        return Protocol.HTTPS;
    }

}

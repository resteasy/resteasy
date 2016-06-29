package org.jboss.resteasy.plugins.server.netty;

import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.netty.i18n.LogMessages;
import org.jboss.resteasy.plugins.server.netty.i18n.Messages;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyUriInfo;

/**
 * This {@link OneToOneDecoder} is responsible for decode {@link org.jboss.netty.handler.codec.http.HttpRequest}
 * to {@link NettyHttpRequest}'s
 * 
 * This implementation is {@link Sharable}
 * 
 * @author Norman Maurer
 *
 */
@Sharable
public class RestEasyHttpRequestDecoder extends OneToOneDecoder 
{

    private final SynchronousDispatcher dispatcher;
    private final String servletMappingPrefix;
    private final String proto;
    private final boolean isKeepAlive;
    
    public enum Protocol 
    {
        HTTPS,
        HTTP
    }
    
    public RestEasyHttpRequestDecoder(SynchronousDispatcher dispatcher, String servletMappingPrefix, Protocol protocol, boolean isKeepAlive) 
    {
        this.dispatcher = dispatcher;
        this.servletMappingPrefix = servletMappingPrefix;
        if (protocol == Protocol.HTTP) 
        {
            proto = "http";
        } 
        else 
        {
            proto = "https";
        }
        this.isKeepAlive = isKeepAlive;
    }
    
    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception 
    {
        if (!(msg instanceof org.jboss.netty.handler.codec.http.HttpRequest)) 
        {
            return msg;
        }
        
        org.jboss.netty.handler.codec.http.HttpRequest request = (org.jboss.netty.handler.codec.http.HttpRequest) msg;
        boolean keepAlive = org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive(request) & isKeepAlive;

        NettyHttpResponse response = new NettyHttpResponse(channel, keepAlive, request.getMethod());

        ResteasyHttpHeaders headers = null;
        ResteasyUriInfo uriInfo = null;
        try
        {
           headers = NettyUtil.extractHttpHeaders(request);

           uriInfo = NettyUtil.extractUriInfo(request, servletMappingPrefix, proto);
           HttpRequest nettyRequest = new NettyHttpRequest(headers, uriInfo, request.getMethod().getName(), dispatcher, response, org.jboss.netty.handler.codec.http.HttpHeaders.is100ContinueExpected(request) );
           ChannelBufferInputStream is = new ChannelBufferInputStream(request.getContent());
           nettyRequest.setInputStream(is);
           return nettyRequest;
        }
        catch (Exception e)
        {
           response.sendError(400);
           // made it warn so that people can filter this.
           LogMessages.LOGGER.warn(Messages.MESSAGES.failedToParseRequest(), e);
           
           return null;
        }

    }

}

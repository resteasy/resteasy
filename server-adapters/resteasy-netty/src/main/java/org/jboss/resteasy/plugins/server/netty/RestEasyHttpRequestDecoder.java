package org.jboss.resteasy.plugins.server.netty;

import javax.ws.rs.core.HttpHeaders;

import org.jboss.netty.buffer.ChannelBufferInputStream;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.handler.codec.oneone.OneToOneDecoder;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.specimpl.UriInfoImpl;
import org.jboss.resteasy.spi.HttpRequest;

/**
 * This {@link OneToOneDecoder} is responsible for decode {@link org.jboss.netty.handler.codec.http.HttpRequest}
 * to {@link NettyHttpRequest}'s
 * 
 * @author Norman Maurer
 *
 */
@Sharable
public class RestEasyHttpRequestDecoder extends OneToOneDecoder {
    private final static Logger logger = Logger.getLogger(RestEasyHttpRequestDecoder.class);

    private final SynchronousDispatcher dispatcher;
    private final String servletMappingPrefix;

    public RestEasyHttpRequestDecoder(SynchronousDispatcher dispatcher, String servletMappingPrefix) {
        this.dispatcher = dispatcher;
        this.servletMappingPrefix = servletMappingPrefix;
    }
    
    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (!(msg instanceof org.jboss.netty.handler.codec.http.HttpRequest)) 
        {
            return msg;
        }
        org.jboss.netty.handler.codec.http.HttpRequest request = (org.jboss.netty.handler.codec.http.HttpRequest) msg;
        NettyHttpResponse response = new NettyHttpResponse(channel);

        HttpHeaders headers = null;
        UriInfoImpl uriInfo = null;
        try
        {
           headers = NettyUtil.extractHttpHeaders(request);
           uriInfo = NettyUtil.extractUriInfo(request, servletMappingPrefix, "http");
           HttpRequest nettyRequest = new NettyHttpRequest(headers, uriInfo, request.getMethod().getName(), dispatcher, response, org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive(request), org.jboss.netty.handler.codec.http.HttpHeaders.is100ContinueExpected(request) );
           ChannelBufferInputStream is = new ChannelBufferInputStream(request.getContent());
           nettyRequest.setInputStream(is);
           return request;
        }
        catch (Exception e)
        {
           response.sendError(400);
           // made it warn so that people can filter this.
           logger.warn("Failed to parse request.", e);
           
           return null;
        }

    }

}

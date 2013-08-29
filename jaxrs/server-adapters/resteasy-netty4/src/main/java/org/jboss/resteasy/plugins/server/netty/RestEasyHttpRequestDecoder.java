package org.jboss.resteasy.plugins.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.jboss.resteasy.spi.ResteasyUriInfo;

import java.util.List;

import static io.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;

/**
 * This {@link MessageToMessageDecoder} is responsible for decode {@link io.netty.handler.codec.http.HttpRequest}
 * to {@link NettyHttpRequest}'s
 *
 * This implementation is {@link Sharable}
 *
 * @author Norman Maurer
 *
 */
@Sharable
public class RestEasyHttpRequestDecoder extends MessageToMessageDecoder<io.netty.handler.codec.http.HttpRequest>
{
    private final static Logger logger = Logger.getLogger(RestEasyHttpRequestDecoder.class);
    private final SynchronousDispatcher dispatcher;
    private final String servletMappingPrefix;
    private final String proto;

    public enum Protocol
    {
        HTTPS,
        HTTP
    }

    public RestEasyHttpRequestDecoder(SynchronousDispatcher dispatcher, String servletMappingPrefix, Protocol protocol)
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
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, io.netty.handler.codec.http.HttpRequest request, List<Object> out) throws Exception
    {
        boolean keepAlive = HttpHeaders.isKeepAlive(request);
        final NettyHttpResponse response = new NettyHttpResponse(ctx, keepAlive);
        final ResteasyHttpHeaders headers;
        final ResteasyUriInfo uriInfo;
        try
        {
           headers = NettyUtil.extractHttpHeaders(request);

           uriInfo = NettyUtil.extractUriInfo(request, servletMappingPrefix, proto);
           NettyHttpRequest nettyRequest = new NettyHttpRequest(ctx, headers, uriInfo, request.getMethod().name(), dispatcher, response, is100ContinueExpected(request) );
           if (request instanceof HttpContent)
           {
               HttpContent content = (HttpContent) request;
               ByteBuf buf = content.content().retain();
               ByteBufInputStream in = new ByteBufInputStream(buf);
               nettyRequest.setInputStream(in);
               out.add(nettyRequest);
           }
        }
        catch (Exception e)
        {
           response.sendError(400);
           // made it warn so that people can filter this.
           logger.warn("Failed to parse request.", e);
        }
    }
}

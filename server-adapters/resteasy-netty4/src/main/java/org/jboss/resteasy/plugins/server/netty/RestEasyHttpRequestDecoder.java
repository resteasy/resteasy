package org.jboss.resteasy.plugins.server.netty;

import static io.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;

import java.util.List;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.server.netty.i18n.LogMessages;
import org.jboss.resteasy.plugins.server.netty.i18n.Messages;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.jboss.resteasy.spi.ResteasyUriInfo;

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
        final NettyHttpResponse response = new NettyHttpResponse(ctx, keepAlive, dispatcher.getProviderFactory(), request.method());
        
        DecoderResult decoderResult = request.decoderResult();
        if (decoderResult.isFailure())
        {
           Throwable t = decoderResult.cause();
           if (t != null && t.getLocalizedMessage() != null)
           {
              response.sendError(400, t.getLocalizedMessage());
           }
           else
           {
              response.sendError(400);
           }
           return;
        }
        
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
               ByteBuf byteBuf = content.content();

               // Does the request contain a body that will need to be retained
               if(byteBuf.readableBytes() > 0) {
                 ByteBuf buf = byteBuf.retain();
                 nettyRequest.setContentBuffer(buf);
               }

               out.add(nettyRequest);
           }
        }
        catch (Exception e)
        {
           response.sendError(400);
           // made it warn so that people can filter this.
           LogMessages.LOGGER.warn(Messages.MESSAGES.failedToParseRequest(), e);
        }
    }
}

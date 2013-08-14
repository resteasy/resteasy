package org.jboss.resteasy.plugins.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

import javax.ws.rs.ext.RuntimeDelegate;
import java.util.List;
import java.util.Map;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


/**
 * {@link MessageToMessageEncoder} implementation which encodes {@link org.jboss.resteasy.spi.HttpResponse}'s to
 * {@link HttpResponse}'s
 *
 * This implementation is {@link Sharable}
 *
 * @author Norman Maurer
 *
 */
@Sharable
public class RestEasyHttpResponseEncoder extends MessageToMessageEncoder<NettyHttpResponse>
{

    private final RequestDispatcher dispatcher;

    public RestEasyHttpResponseEncoder(RequestDispatcher dispatcher)
    {
        this.dispatcher = dispatcher;
    }


    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected void encode(ChannelHandlerContext ctx, NettyHttpResponse nettyResponse, List<Object> out) throws Exception
    {
        // Build the response object.
        HttpResponseStatus status = HttpResponseStatus.valueOf(nettyResponse.getStatus());
        ByteBuf buffer = nettyResponse.getBuffer();
        HttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, status, buffer);

        for (Map.Entry<String, List<Object>> entry : nettyResponse.getOutputHeaders().entrySet())
        {
           String key = entry.getKey();
           for (Object value : entry.getValue())
           {
              RuntimeDelegate.HeaderDelegate delegate = dispatcher.providerFactory.getHeaderDelegate(value.getClass());
              if (delegate != null)
              {
                  response.headers().add(key, delegate.toString(value));
              }
              else
              {
                 response.headers().set(key, value.toString());
              }
           }
        }

        if (nettyResponse.isKeepAlive())
        {
            // Add content length and connection header if needed
            response.headers().set(Names.CONTENT_LENGTH, buffer.readableBytes());
            response.headers().set(Names.CONNECTION, Values.KEEP_ALIVE);
        }
        out.add(response);
    }

}

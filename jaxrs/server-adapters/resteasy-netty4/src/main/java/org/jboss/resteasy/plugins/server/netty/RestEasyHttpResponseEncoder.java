package org.jboss.resteasy.plugins.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.ext.RuntimeDelegate;
import java.util.List;
import java.util.Map;


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
        ByteBuf buffer = nettyResponse.getBuffer();
        HttpResponse response = nettyResponse.getDefaultFullHttpResponse();

        transformHeaders(nettyResponse, response, dispatcher.providerFactory);

        out.add(response);
    }

   public static void transformHeaders(NettyHttpResponse nettyResponse, HttpResponse response, ResteasyProviderFactory factory)
   {
      ByteBuf buffer = nettyResponse.getBuffer();
      if (nettyResponse.isKeepAlive())
      {
         // Add content length and connection header if needed
         response.headers().set(Names.CONTENT_LENGTH, buffer.readableBytes());
         response.headers().set(Names.CONNECTION, Values.KEEP_ALIVE);
      }
      for (Map.Entry<String, List<Object>> entry : nettyResponse.getOutputHeaders().entrySet())
      {
         String key = entry.getKey();
         for (Object value : entry.getValue())
         {
            RuntimeDelegate.HeaderDelegate delegate = factory.getHeaderDelegate(value.getClass());
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
   }

}

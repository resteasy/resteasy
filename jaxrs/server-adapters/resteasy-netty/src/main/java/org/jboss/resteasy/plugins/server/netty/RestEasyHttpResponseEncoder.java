package org.jboss.resteasy.plugins.server.netty;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpHeaders.Values;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

import javax.ws.rs.ext.RuntimeDelegate;
import java.util.List;
import java.util.Map;

import static org.jboss.netty.handler.codec.http.HttpVersion.*;


/**
 * {@link OneToOneEncoder} implementation which encodes {@link org.jboss.resteasy.spi.HttpResponse}'s to
 * {@link HttpResponse}'s
 * 
 * This implementation is {@link Sharable}
 * 
 * @author Norman Maurer
 *
 */
@Sharable
public class RestEasyHttpResponseEncoder extends OneToOneEncoder 
{
    
    private final RequestDispatcher dispatcher;

    public RestEasyHttpResponseEncoder(RequestDispatcher dispatcher) 
    {
        this.dispatcher = dispatcher;
    }
    
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception 
    {
        if (msg instanceof org.jboss.resteasy.spi.HttpResponse) {
            NettyHttpResponse nettyResponse = (NettyHttpResponse) msg;
            // Build the response object.
            HttpResponseStatus status = HttpResponseStatus.valueOf(nettyResponse.getStatus());
            HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);

            for (Map.Entry<String, List<Object>> entry : nettyResponse.getOutputHeaders().entrySet())
            {
               String key = entry.getKey();
               for (Object value : entry.getValue())
               {
                  RuntimeDelegate.HeaderDelegate delegate = dispatcher.providerFactory.createHeaderDelegate(value.getClass());
                  if (delegate != null)
                  {
                     response.addHeader(key, delegate.toString(value));
                  }
                  else
                  {
                     response.setHeader(key, value.toString());
                  }
               }
            }

            nettyResponse.getOutputStream().flush();
            response.setContent(nettyResponse.getBuffer());

            if (nettyResponse.isKeepAlive()) 
            {
                // Add content length and connection header if needed
                response.setHeader(Names.CONTENT_LENGTH, response.getContent().readableBytes());
                response.setHeader(Names.CONNECTION, Values.KEEP_ALIVE);
            }
            return response;
        }
        return msg;

    }

}

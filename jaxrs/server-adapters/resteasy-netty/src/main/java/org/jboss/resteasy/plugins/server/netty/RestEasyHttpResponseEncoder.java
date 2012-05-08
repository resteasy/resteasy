package org.jboss.resteasy.plugins.server.netty;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.ext.RuntimeDelegate;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;


public class RestEasyHttpResponseEncoder extends OneToOneEncoder {
    
    private final RequestDispatcher dispatcher;

    public RestEasyHttpResponseEncoder(RequestDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }
    
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    protected Object encode(ChannelHandlerContext ctx, Channel channel, Object msg) throws Exception {
        if (msg instanceof NettyHttpResponse) {
            NettyHttpResponse nettyResponse = (NettyHttpResponse) msg;
            // Build the response object.
            HttpResponseStatus status = null;
            if (nettyResponse.getMessage() != null)
            {
               status = new HttpResponseStatus(nettyResponse.getStatus(), nettyResponse.getMessage());
            }
            else
            {
                status = HttpResponseStatus.valueOf(nettyResponse.getStatus());
            }
            HttpResponse response = new DefaultHttpResponse(HTTP_1_1, status);

            for (Map.Entry<String, List<Object>> entry : nettyResponse.getOutputHeaders().entrySet())
            {
               String key = entry.getKey();
               for (Object value : entry.getValue())
               {
                  RuntimeDelegate.HeaderDelegate delegate = dispatcher.providerFactory.createHeaderDelegate(value.getClass());
                  if (delegate != null)
                  {
                     //System.out.println("addResponseHeader: " + key + " " + delegate.toString(value));
                     response.addHeader(key, delegate.toString(value));
                  }
                  else
                  {
                     //System.out.println("addResponseHeader: " + key + " " + value.toString());
                     response.setHeader(key, value.toString());
                  }
               }
            }

            try
            {
               nettyResponse.getOutputStream().flush();
            }
            catch (IOException e1)
            {
               throw new RuntimeException(e1);
            }
            response.setContent(nettyResponse.getBuffer());
            response.setHeader(CONTENT_LENGTH, response.getContent().readableBytes());
            
            return response;
        }
        return msg;

    }

}

package org.jboss.resteasy.plugins.server.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.jboss.netty.util.CharsetUtil;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.spi.Failure;

import javax.ws.rs.ext.RuntimeDelegate;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.*;
import static org.jboss.netty.handler.codec.http.HttpHeaders.*;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.*;
import static org.jboss.netty.handler.codec.http.HttpVersion.*;

/**
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 * @version $Rev: 2368 $, $Date: 2010-10-18 17:19:03 +0900 (Mon, 18 Oct 2010) $
 */
public class RequestHandler extends SimpleChannelUpstreamHandler
{
   protected RequestDispatcher dispatcher;
   private final static Logger logger = Logger.getLogger(RequestHandler.class);

   public RequestHandler(RequestDispatcher dispatcher)
   {
      this.dispatcher = dispatcher;
   }

   @Override
   public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception
   {
      HttpRequest request = (HttpRequest) e.getMessage();

      if (is100ContinueExpected(request))
      {
         send100Continue(e);
      }

      NettyHttpResponse response = new NettyHttpResponse();
      try
      {
         dispatcher.service("http", request, response, true);
      }
      catch (Failure e1)
      {
         response.reset();
         response.setStatus(e1.getErrorCode());
      }
      catch (Exception ex)
      {
         response.reset();
         response.setStatus(500);
         logger.error("Unexpected", ex);
      }

      writeResponse(request, response, e);
   }

   private void writeResponse(HttpRequest request, NettyHttpResponse nettyResponse, MessageEvent e)
   {
      // Decide whether to close the connection or not.
      boolean keepAlive = isKeepAlive(request);

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

      // not sure why this is done
      /*
      if (keepAlive)
      {
         // Add 'Content-Length' header only for a keep-alive connection.
         response.setHeader(CONTENT_LENGTH, response.getContent().readableBytes());
      }
      */

      // Write the response.
      ChannelFuture future = e.getChannel().write(response);

      // Close the non-keep-alive connection after the write operation is done.
      if (!keepAlive)
      {
         future.addListener(ChannelFutureListener.CLOSE);
      }
   }

   private void send100Continue(MessageEvent e)
   {
      HttpResponse response = new DefaultHttpResponse(HTTP_1_1, CONTINUE);
      e.getChannel().write(response);
   }

   @Override
   public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
           throws Exception
   {
      e.getCause().printStackTrace();
      e.getChannel().close();
   }
}

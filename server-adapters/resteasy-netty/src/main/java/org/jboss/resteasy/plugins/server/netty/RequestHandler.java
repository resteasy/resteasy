package org.jboss.resteasy.plugins.server.netty;

import static org.jboss.netty.handler.codec.http.HttpHeaders.is100ContinueExpected;
import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.spi.Failure;

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
      

      // Write the response.
      ChannelFuture future = e.getChannel().write(response);

      // Decide whether to close the connection or not.
      boolean keepAlive = isKeepAlive(request);
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

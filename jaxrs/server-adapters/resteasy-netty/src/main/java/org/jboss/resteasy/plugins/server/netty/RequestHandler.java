package org.jboss.resteasy.plugins.server.netty;

import static org.jboss.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.spi.Failure;

/**
 * @author <a href="http://www.jboss.org/netty/">The Netty Project</a>
 * @author Andy Taylor (andy.taylor@jboss.org)
 * @author <a href="http://gleamynode.net/">Trustin Lee</a>
 * @author Norman Maurer
 * @version $Rev: 2368 $, $Date: 2010-10-18 17:19:03 +0900 (Mon, 18 Oct 2010) $
 */
@Sharable
public class RequestHandler extends SimpleChannelUpstreamHandler
{
   protected final RequestDispatcher dispatcher;
   private final static Logger logger = Logger.getLogger(RequestHandler.class);

   public RequestHandler(RequestDispatcher dispatcher)
   {
      this.dispatcher = dispatcher;
   }

   @Override
   public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception
   {
      if (e.getMessage() instanceof NettyHttpRequest) {
          NettyHttpRequest request = (NettyHttpRequest) e.getMessage();

          if (request.is100ContinueExpected())
          {
             send100Continue(e);
          }

          NettyHttpResponse response = request.getResponse();
          try
          {
             dispatcher.service(request, response, true);
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

          // Close the non-keep-alive connection after the write operation is done.
          if (!request.isKeepAlive())
          {
             future.addListener(ChannelFutureListener.CLOSE);
          }
      }
      super.messageReceived(ctx, e);
      
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

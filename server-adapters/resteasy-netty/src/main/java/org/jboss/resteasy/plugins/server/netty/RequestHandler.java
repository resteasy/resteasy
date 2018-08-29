package org.jboss.resteasy.plugins.server.netty;

import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler.Sharable;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.frame.TooLongFrameException;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.resteasy.plugins.server.netty.i18n.LogMessages;
import org.jboss.resteasy.plugins.server.netty.i18n.Messages;
import org.jboss.resteasy.spi.Failure;

import static org.jboss.netty.handler.codec.http.HttpResponseStatus.CONTINUE;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * {@link SimpleChannelUpstreamHandler} which handles the requests and dispatch them.
 *
 * This class is {@link Sharable}.
 *
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
             dispatcher.service(ctx, request, response, true);
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
             LogMessages.LOGGER.error(Messages.MESSAGES.unexpected(), ex);
          }

          // Write the response.
          ChannelFuture future = e.getChannel().write(response);

          //NETTY-391
          NettyJaxrsServer.allChannels.add(e.getChannel());

          // Close the non-keep-alive connection after the write operation is done.
          if (!request.isKeepAlive())
          {
             future.addListener(ChannelFutureListener.CLOSE);
          }
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
      // handle the case of to big requests.
      if (e.getCause() instanceof TooLongFrameException)
      {
          DefaultHttpResponse response = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE);
          e.getChannel().write(response).addListener(ChannelFutureListener.CLOSE);
      }
      else
      {
          LogMessages.LOGGER.info(Messages.MESSAGES.exceptionCaught(), e.getCause());
          e.getChannel().close();
      }

   }
}

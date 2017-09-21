package org.jboss.resteasy.plugins.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;

import org.jboss.resteasy.plugins.server.netty.i18n.Messages;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;

import java.io.IOException;
import java.io.OutputStream;

import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class NettyHttpResponse implements HttpResponse
{
   private static final int EMPTY_CONTENT_LENGTH = 0;
   private int status = 200;
   private OutputStream os;
   private MultivaluedMap<String, Object> outputHeaders;
   private final ChannelHandlerContext ctx;
   private boolean committed;
   private boolean keepAlive;
   private ResteasyProviderFactory providerFactory;
   private HttpMethod method;

   public NettyHttpResponse(ChannelHandlerContext ctx, boolean keepAlive, ResteasyProviderFactory providerFactory)
   {
	   this(ctx, keepAlive, providerFactory, null);
   }

   public NettyHttpResponse(ChannelHandlerContext ctx, boolean keepAlive, ResteasyProviderFactory providerFactory, HttpMethod method)
   {
      outputHeaders = new MultivaluedMapImpl<String, Object>();
      this.method = method;
      os = (method == null || !method.equals(HttpMethod.HEAD)) ? new ChunkOutputStream(this, ctx, 1000) : null; //[RESTEASY-1627]
      this.ctx = ctx;
      this.keepAlive = keepAlive;
      this.providerFactory = providerFactory;
   }

   @Override
   public void setOutputStream(OutputStream os)
   {
      this.os = os;
   }

   @Override
   public int getStatus()
   {
      return status;
   }

   @Override
   public void setStatus(int status)
   {
      this.status = status;
   }

   @Override
   public MultivaluedMap<String, Object> getOutputHeaders()
   {
      return outputHeaders;
   }

   @Override
   public OutputStream getOutputStream() throws IOException
   {
      return os;
   }

   @Override
   public void addNewCookie(NewCookie cookie)
   {
      outputHeaders.add(javax.ws.rs.core.HttpHeaders.SET_COOKIE, cookie);
   }

   @Override
   public void sendError(int status) throws IOException
   {
      sendError(status, null);
   }

   @Override
   public void sendError(int status, String message) throws IOException
   {
      if (committed)
      {
         throw new IllegalStateException();
      }

      final HttpResponseStatus responseStatus;
      if (message != null)
      {
         responseStatus = new HttpResponseStatus(status, message);
         setStatus(status);
      }
      else
      {
         responseStatus = HttpResponseStatus.valueOf(status);
         setStatus(status);
      }
      io.netty.handler.codec.http.HttpResponse response = null;
      if (message != null)
      {
         ByteBuf byteBuf = ctx.alloc().buffer();
         byteBuf.writeBytes(message.getBytes());

         response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, responseStatus, byteBuf);
      }
      else
      {
         response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, responseStatus);

      }
      if (keepAlive)
      {
         // Add keep alive and content length if needed
         response.headers().add(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
         if (message == null) response.headers().add(HttpHeaderNames.CONTENT_LENGTH, 0);
         else response.headers().add(HttpHeaderNames.CONTENT_LENGTH, message.getBytes().length);
      }
      ctx.writeAndFlush(response);
      committed = true;
   }

   @Override
   public boolean isCommitted()
   {
      return committed;
   }

   @Override
   public void reset()
   {
      if (committed)
      {
         throw new IllegalStateException(Messages.MESSAGES.alreadyCommitted());
      }
      outputHeaders.clear();
      outputHeaders.clear();
   }

   public boolean isKeepAlive()
   {
      return keepAlive;
   }

   public DefaultHttpResponse getDefaultHttpResponse()
   {
       DefaultHttpResponse res = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(getStatus()));
       transformResponseHeaders(res);
       return res;
   }

   public DefaultHttpResponse getEmptyHttpResponse()
   {
       DefaultFullHttpResponse res = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(getStatus()));
       if (method == null || !method.equals(HttpMethod.HEAD)) //[RESTEASY-1627]
       {
          res.headers().add(HttpHeaderNames.CONTENT_LENGTH, EMPTY_CONTENT_LENGTH);
       }
       transformResponseHeaders(res);
       return res;
   }

   private void transformResponseHeaders(io.netty.handler.codec.http.HttpResponse res) {
       RestEasyHttpResponseEncoder.transformHeaders(this, res, providerFactory);
   }

   public void prepareChunkStream() {
      committed = true;
      DefaultHttpResponse response = getDefaultHttpResponse();
      HttpUtil.setTransferEncodingChunked(response, true);
      ctx.write(response);
   }

   public void finish() throws IOException {
      if (os != null)
         os.flush();
      ChannelFuture future;
      if (isCommitted()) {
         // if committed this means the output stream was used.
         future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
      } else {
         future = ctx.writeAndFlush(getEmptyHttpResponse());
      }
      
      if(!isKeepAlive()) {
         future.addListener(ChannelFutureListener.CLOSE);
      }

   }

   @Override
   public void flushBuffer() throws IOException {
	   if(os != null)
		   os.flush();
	   ctx.flush();
   }
}

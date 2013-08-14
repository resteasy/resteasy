package org.jboss.resteasy.plugins.server.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders.Names;
import io.netty.handler.codec.http.HttpHeaders.Values;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.spi.HttpResponse;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class NettyHttpResponse implements HttpResponse
{
   private int status = 200;
   private ByteBuf byteBuf;
   private OutputStream os;
   private MultivaluedMap<String, Object> outputHeaders;
   private final ChannelHandlerContext ctx;
   private boolean committed;
   private boolean keepAlive;

   public NettyHttpResponse(ChannelHandlerContext ctx, boolean keepAlive)
   {
      outputHeaders = new MultivaluedMapImpl<String, Object>();
      byteBuf = Unpooled.buffer();
      os = new ByteBufOutputStream(byteBuf);
      this.ctx = ctx;
      this.keepAlive = keepAlive;
   }

   @Override
   public void setOutputStream(OutputStream os)
   {
      this.os = os;
   }

   public ByteBuf getBuffer()
   {
      return byteBuf;
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
      outputHeaders.add(HttpHeaders.SET_COOKIE, cookie);
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
       final io.netty.handler.codec.http.HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, responseStatus);
       if (keepAlive)
       {
           // Add keep alive and content length if needed
           response.headers().add(Names.CONNECTION, Values.KEEP_ALIVE);
           response.headers().add(Names.CONTENT_LENGTH, 0);
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
          throw new IllegalStateException("Already committed");
      }
      outputHeaders.clear();
      byteBuf.clear();
      outputHeaders.clear();
   }

   public boolean isKeepAlive() {
       return keepAlive;
   }
}

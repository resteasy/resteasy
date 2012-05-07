package org.jboss.resteasy.plugins.server.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channels;
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
   private ChannelBuffer buffer;
   private ChannelBufferOutputStream os;
   private MultivaluedMap<String, Object> outputHeaders;
   private String message = null;

   public NettyHttpResponse()
   {
      outputHeaders = new MultivaluedMapImpl<String, Object>();
      buffer = ChannelBuffers.dynamicBuffer();
      os = new ChannelBufferOutputStream(buffer);
   }

   public ChannelBuffer getBuffer()
   {
      return buffer;
   }

   public String getMessage()
   {
      return message;
   }

   public int getStatus()
   {
      return status;
   }

   public void setStatus(int status)
   {
      this.status = status;
   }

   public MultivaluedMap<String, Object> getOutputHeaders()
   {
      return outputHeaders;
   }

   public OutputStream getOutputStream() throws IOException
   {
      return os;
   }

   public void addNewCookie(NewCookie cookie)
   {
      outputHeaders.add(HttpHeaders.SET_COOKIE, cookie);
   }

   public void sendError(int status) throws IOException
   {
      this.status = status;
   }

   public void sendError(int status, String message) throws IOException
   {
      this.status = status;
      this.message = message;
   }

   public boolean isCommitted()
   {
      return false;
   }

   public void reset()
   {
      outputHeaders.clear();
      buffer.clear();
   }
}

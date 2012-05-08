package org.jboss.resteasy.plugins.server.netty;

import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
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
   private final Channel channel;
   private boolean committed;
   public NettyHttpResponse(Channel channel)
   {
      outputHeaders = new MultivaluedMapImpl<String, Object>();
      buffer = ChannelBuffers.dynamicBuffer();
      os = new ChannelBufferOutputStream(buffer);
      this.channel = channel;
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
      sendError(status, null);
   }

   public void sendError(int status, String message) throws IOException
   {
       if (committed) 
       {
           throw new IllegalStateException();
       }
       
       HttpResponseStatus responseStatus = null;
       if (message != null)
       {
           responseStatus = new HttpResponseStatus(status, message);
       }
       else
       {
           responseStatus = HttpResponseStatus.valueOf(status);
       }
       DefaultHttpResponse response = new DefaultHttpResponse(HTTP_1_1, responseStatus);
       channel.write(response);
       committed = true;
   }

   public boolean isCommitted()
   {
      return committed;
   }

   public void reset()
   {
      if (committed) 
      {
          throw new IllegalStateException("HttpResponse is committed");
      }
      outputHeaders.clear();
      buffer.clear();
      outputHeaders.clear();
   }

}

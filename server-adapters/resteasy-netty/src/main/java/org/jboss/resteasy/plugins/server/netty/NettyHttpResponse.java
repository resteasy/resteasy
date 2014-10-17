package org.jboss.resteasy.plugins.server.netty;

import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;

import java.io.IOException;
import java.io.OutputStream;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBufferOutputStream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpHeaders.Values;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.resteasy.plugins.server.netty.i18n.Messages;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.spi.HttpResponse;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class NettyHttpResponse implements HttpResponse
{
   private int status = 200;
   private ChannelBufferOutputStream os;
   private MultivaluedMap<String, Object> outputHeaders;
   private final Channel channel;
   private boolean committed;
   private boolean keepAlive;
   
   public NettyHttpResponse(Channel channel, boolean keepAlive)
   {
      outputHeaders = new MultivaluedMapImpl<String, Object>();
      os = new ChannelBufferOutputStream(ChannelBuffers.dynamicBuffer());
      this.channel = channel;
      this.keepAlive = keepAlive;
   }

   public ChannelBuffer getBuffer()
   {
      return os.buffer();
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
       if (keepAlive) 
       {
           // Add keep alive and content length if needed
           response.addHeader(Names.CONNECTION, Values.KEEP_ALIVE);
           response.addHeader(Names.CONTENT_LENGTH, 0);
       }
       channel.write(response);
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
      os.buffer().clear();
      outputHeaders.clear();
   }
   
   public boolean isKeepAlive() {
       return keepAlive;
   }
}

package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpServletResponseWrapper implements HttpResponse
{
   protected HttpServletResponse response;
   protected int status = 200;
   protected MultivaluedMap<String, Object> outputHeaders;
   protected ResteasyProviderFactory factory;
   protected OutputStream outputStream = new DeferredOutputStream();

   /**
    * RESTEASY-684 wants to defer access to outputstream until a write/flush/close happens
    *
    */
   protected class DeferredOutputStream extends OutputStream
   {
      @Override
      public void write(int i) throws IOException
      {
         response.getOutputStream().write(i);
      }

      @Override
      public void write(byte[] bytes) throws IOException
      {
         response.getOutputStream().write(bytes);
      }

      @Override
      public void write(byte[] bytes, int i, int i1) throws IOException
      {
         response.getOutputStream().write(bytes, i, i1);
      }

      @Override
      public void flush() throws IOException
      {
         response.getOutputStream().flush();
      }

      @Override
      public void close() throws IOException
      {
         response.getOutputStream().close();
      }
   }

   public HttpServletResponseWrapper(HttpServletResponse response, ResteasyProviderFactory factory)
   {
      this.response = response;
      outputHeaders = new HttpServletResponseHeaders(response, factory);
      this.factory = factory;
   }

   public int getStatus()
   {
      return status;
   }

   public void setStatus(int status)
   {
      this.status = status;
      this.response.setStatus(status);
   }

   public MultivaluedMap<String, Object> getOutputHeaders()
   {
      return outputHeaders;
   }

   public OutputStream getOutputStream() throws IOException
   {
      return outputStream;
   }

   @Override
   public void setOutputStream(OutputStream os)
   {
      this.outputStream = os;
   }

   public void addNewCookie(NewCookie cookie)
   {
      outputHeaders.add(javax.ws.rs.core.HttpHeaders.SET_COOKIE, cookie);
   }

   public void sendError(int status) throws IOException
   {
      response.sendError(status);
   }

   public void sendError(int status, String message) throws IOException
   {
      response.sendError(status, message);
   }

   public boolean isCommitted()
   {
      return response.isCommitted();
   }

   public void reset()
   {
      response.reset();
      outputHeaders = new HttpServletResponseHeaders(response, factory);
   }

   @Override
   public void flushBuffer() throws IOException
   {
      response.flushBuffer();
   }

}

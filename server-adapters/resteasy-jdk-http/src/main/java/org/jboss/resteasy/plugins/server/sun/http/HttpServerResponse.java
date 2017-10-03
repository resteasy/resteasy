package org.jboss.resteasy.plugins.server.sun.http;

import com.sun.net.httpserver.HttpExchange;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.CaseInsensitiveMap;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.ext.RuntimeDelegate;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpServerResponse implements HttpResponse
{
   private int status = 200;
   private MultivaluedMap<String, Object> outputHeaders;
   private HttpExchange exchange;
   private OutputStream streamWrapper;
   private boolean committed;
   private ResteasyProviderFactory factory;

   public HttpServerResponse(ResteasyProviderFactory factory, HttpExchange ex)
   {
      this.exchange = ex;
      this.factory = factory;
      outputHeaders = new CaseInsensitiveMap<Object>();
      streamWrapper = new OutputStream()
      {
         @Override
         public void write(int i) throws IOException
         {
            commitHeaders();
            exchange.getResponseBody().write(i);
         }

         @Override
         public void write(byte[] bytes) throws IOException
         {
            commitHeaders();
            exchange.getResponseBody().write(bytes);
         }

         @Override
         public void write(byte[] bytes, int i, int i1) throws IOException
         {
            commitHeaders();
            exchange.getResponseBody().write(bytes, i, i1);
         }

         @Override
         public void flush() throws IOException
         {
            commitHeaders();
            exchange.getResponseBody().flush();
         }
      };
   }

   protected void addHeader(String name, Object value)
   {
      @SuppressWarnings(value = "unchecked")
      RuntimeDelegate.HeaderDelegate<Object> delegate = factory.getHeaderDelegate(value.getClass());
      if (delegate != null)
      {
         //System.out.println("addResponseHeader: " + key + " " + delegate.toString(value));
         exchange.getResponseHeaders().add(name, delegate.toString(value));
      }
      else
      {
         //System.out.println("addResponseHeader: " + key + " " + value.toString());
         exchange.getResponseHeaders().add(name, value.toString());
      }
   }

   public void commitHeaders() throws IOException
   {
      if (committed) return;
      long len = 0;
      if (outputHeaders.containsKey("Content-Length"))
      {
         len = Long.valueOf(outputHeaders.getFirst("Content-Length").toString());
      }
      else if (outputHeaders.containsKey("Content-Type"))
      {
         len = 0;
      }
      else
      {
         len = -1;
      }

      for (Map.Entry<String, List<Object>> entry : outputHeaders.entrySet())
      {
         for (Object val : entry.getValue())
         {
            addHeader(entry.getKey(), val);
         }
      }

      exchange.sendResponseHeaders(status, len);
      committed = true;
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
      return streamWrapper;
   }

   @Override
   public void setOutputStream(OutputStream os)
   {
      streamWrapper = os;
   }

   public void addNewCookie(NewCookie cookie)
   {
      outputHeaders.add(HttpHeaders.SET_COOKIE, cookie);
   }

   public void sendError(int status) throws IOException
   {
      this.status = status;
      exchange.sendResponseHeaders(status, -1);
      committed = true;
   }

   public void sendError(int status, String message) throws IOException
   {
      this.status = status;
      exchange.sendResponseHeaders(status, -1);
      committed = true;
   }

   public boolean isCommitted()
   {
      return committed;
   }

   public void reset()
   {
      outputHeaders.clear();
   }

   @Override
   public void flushBuffer() throws IOException {
	   commitHeaders();
	   exchange.getResponseBody().flush();
   }
}

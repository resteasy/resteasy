package org.jboss.resteasy.plugins.server.vertx;

import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import org.jboss.resteasy.plugins.server.vertx.i18n.Messages;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.ext.RuntimeDelegate;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

public class VertxHttpResponse implements HttpResponse
{
   private int status = 200;
   private OutputStream os;
   private MultivaluedMap<String, Object> outputHeaders;
   final HttpServerResponse response;
   private boolean committed;
   private ResteasyProviderFactory providerFactory;
   private final HttpMethod method;
   private Throwable vertxException;

   public VertxHttpResponse(HttpServerResponse response, ResteasyProviderFactory providerFactory)
   {
      this(response, providerFactory, null);
   }

   public VertxHttpResponse(HttpServerResponse response, ResteasyProviderFactory providerFactory, HttpMethod method)
   {
      outputHeaders = new MultivaluedMapImpl<String, Object>();
      this.method = method;
      os = (method == null || !method.equals(HttpMethod.HEAD)) ? new ChunkOutputStream(this, 1000) : null;
      this.response = response;
      this.providerFactory = providerFactory;
      response.exceptionHandler(t -> vertxException = t);
      response.closeHandler(v -> vertxException = new IOException("Connection closed"));
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

   void checkException() throws IOException
   {
      if(vertxException instanceof IOException)
         throw (IOException)vertxException;
      if(vertxException != null)
         throw new IOException(vertxException);
   }
   
   @Override
   public void sendError(int status) throws IOException
   {
      checkException();
      sendError(status, null);
   }

   @Override
   public void sendError(int status, String message) throws IOException
   {
      checkException();
      if (committed)
      {
         throw new IllegalStateException();
      }
      response.setStatusCode(status);
      if (message != null)
      {
         response.end(message);
      } else
      {
         response.end();
      }
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
   }

   @SuppressWarnings({"rawtypes", "unchecked"})
   public static void transformHeaders(VertxHttpResponse vertxResponse, HttpServerResponse response, ResteasyProviderFactory factory)
   {
      for (Map.Entry<String, List<Object>> entry : vertxResponse.getOutputHeaders().entrySet())
      {
         String key = entry.getKey();
         for (Object value : entry.getValue())
         {
            RuntimeDelegate.HeaderDelegate delegate = factory.getHeaderDelegate(value.getClass());
            if (delegate != null)
            {
               response.headers().add(key, delegate.toString(value));
            } else
            {
               response.headers().set(key, value.toString());
            }
         }
      }
   }

   public void prepareChunkStream()
   {
      committed = true;
      response.setStatusCode(getStatus());
      response.setChunked(true);
      transformHeaders(this, response, providerFactory);
   }
   
   private void prepareEmptyResponse()
   {
      committed = true;
      response.setStatusCode(getStatus());
      transformHeaders(this, response, providerFactory);
      response.headersEndHandler(new Handler<Void>()
      {
         @Override
         public void handle(Void event)
         {
            response.headers().remove(HttpHeaders.CONTENT_LENGTH);
            response.headers().set(HttpHeaders.CONNECTION, HttpHeaders.KEEP_ALIVE);
         }
      });
   }

   public void finish() throws IOException
   {
      checkException();
      if (os != null) {
         os.flush();
         if (!isCommitted())
         {
            prepareChunkStream();
         }
      } else {
         prepareEmptyResponse();
      }
      response.end();
   }

   @Override
   public void flushBuffer() throws IOException {
      checkException();
	   if(os != null) {
		   os.flush();
	   }
   }
}

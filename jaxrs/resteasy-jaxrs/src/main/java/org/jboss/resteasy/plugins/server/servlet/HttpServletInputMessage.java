package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.spi.AsynchronousResponse;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.util.Encode;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Abstraction for an inbound http request on the server, or a response from a server to a client
 * <p/>
 * We have this abstraction so that we can reuse marshalling objects in a client framework and serverside framework
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpServletInputMessage implements HttpRequest
{
   protected HttpHeaders httpHeaders;
   protected HttpServletRequest request;
   protected CountDownLatch latch;
   protected long suspendTimeout;
   protected SynchronousDispatcher dispatcher;
   protected HttpResponse httpResponse;
   protected boolean suspended;
   protected UriInfo uri;
   protected String httpMethod;
   protected String preProcessedPath;
   protected MultivaluedMap<String, String> formParameters;
   protected MultivaluedMap<String, String> decodedFormParameters;


   public HttpServletInputMessage(HttpServletRequest request, HttpResponse httpResponse, HttpHeaders httpHeaders, UriInfo uri, String httpMethod, SynchronousDispatcher dispatcher)
   {
      this.request = request;
      this.dispatcher = dispatcher;
      this.httpResponse = httpResponse;
      this.httpHeaders = httpHeaders;
      this.httpMethod = httpMethod;
      this.uri = uri;
      this.preProcessedPath = uri.getPath(false);

   }

   public MultivaluedMap<String, String> getFormParameters()
   {
      if (formParameters != null) return formParameters;
      formParameters = Encode.encode(getDecodedFormParameters());
      return formParameters;
   }

   public MultivaluedMap<String, String> getDecodedFormParameters()
   {
      if (decodedFormParameters != null) return decodedFormParameters;
      decodedFormParameters = new MultivaluedMapImpl<String, String>();
      Map<String, String[]> params = request.getParameterMap();
      for (Map.Entry<String, String[]> entry : params.entrySet())
      {
         String name = entry.getKey();
         String[] values = entry.getValue();
         MultivaluedMap<String, String> queryParams = uri.getQueryParameters();
         List<String> queryValues = queryParams.get(name);
         if (queryValues == null)
         {
            for (String val : values) decodedFormParameters.add(name, val);
         }
         else
         {
            for (String val : values)
            {
               if (!queryValues.contains(val))
               {
                  decodedFormParameters.add(name, val);
               }
            }
         }
      }
      return decodedFormParameters;

   }

   public HttpHeaders getHttpHeaders()
   {
      return httpHeaders;
   }

   public InputStream getInputStream()
   {
      try
      {
         return request.getInputStream();
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   public UriInfo getUri()
   {
      return uri;
   }

   public String getHttpMethod()
   {
      return httpMethod;
   }

   public String getPreprocessedPath()
   {
      return preProcessedPath;
   }

   public void setPreprocessedPath(String path)
   {
      preProcessedPath = path;
   }


   public AsynchronousResponse createAsynchronousResponse(long suspendTimeout)
   {
      suspended = true;
      latch = new CountDownLatch(1);
      this.suspendTimeout = suspendTimeout;
      return new AsynchronousResponse()
      {
         public void setResponse(Response response)
         {
            try
            {
               dispatcher.asynchronousDelivery(HttpServletInputMessage.this, httpResponse, response);
            }
            finally
            {
               latch.countDown();
            }
         }
      };
   }

   public boolean isInitial()
   {
      return true;
   }

   public boolean isTimeout()
   {
      return false;
   }

   public boolean isSuspended()
   {
      return suspended;
   }


   public void initialRequestThreadFinished()
   {
      if (latch == null) return; // only block if createAsynchronousResponse was called.
      try
      {
         latch.await(suspendTimeout + 100, TimeUnit.MILLISECONDS);
      }
      catch (InterruptedException e)
      {
         throw new RuntimeException(e);
      }
   }
}

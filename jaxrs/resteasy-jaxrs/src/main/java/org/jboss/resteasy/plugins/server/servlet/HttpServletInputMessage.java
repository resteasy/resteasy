package org.jboss.resteasy.plugins.server.servlet;

import org.jboss.resteasy.core.AbstractAsynchronousResponse;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.providers.FormUrlEncodedProvider;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.spi.AsynchronousResponse;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.util.Encode;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
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
   protected AbstractAsynchronousResponse asynchronousResponse;
   protected InputStream overridenStream;


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

   public MultivaluedMap<String, String> getPutFormParameters()
   {
      if (formParameters != null) return formParameters;
      if (MediaType.APPLICATION_FORM_URLENCODED_TYPE.isCompatible(getHttpHeaders().getMediaType()))
      {
         try
         {
            formParameters = FormUrlEncodedProvider.parseForm(getInputStream());
         }
         catch (IOException e)
         {
            throw new RuntimeException(e);
         }
      }
      else
      {
         throw new IllegalArgumentException("Request media type is not application/x-www-form-urlencoded");
      }
      return formParameters;
   }

   public MultivaluedMap<String, String> getPutDecodedFormParameters()
   {
      if (decodedFormParameters != null) return decodedFormParameters;
      decodedFormParameters = Encode.decode(getFormParameters());
      return decodedFormParameters;
   }


   public Object getAttribute(String attribute)
   {
      return request.getAttribute(attribute);
   }

   public void setAttribute(String name, Object value)
   {
      request.setAttribute(name, value);
   }

   public void removeAttribute(String name)
   {
      request.removeAttribute(name);
   }

   public MultivaluedMap<String, String> getFormParameters()
   {
      if (formParameters != null) return formParameters;
      // Tomcat does not set getParameters() if it is a PUT request
      // so pull it out manually
      if (request.getMethod().equals("PUT") && (request.getParameterMap() == null || request.getParameterMap().isEmpty()))
      {
         return getPutFormParameters();
      }
      formParameters = Encode.encode(getDecodedFormParameters());
      return formParameters;
   }

   public MultivaluedMap<String, String> getDecodedFormParameters()
   {
      if (decodedFormParameters != null) return decodedFormParameters;
      // Tomcat does not set getParameters() if it is a PUT request
      // so pull it out manually
      if (request.getMethod().equals("PUT") && (request.getParameterMap() == null || request.getParameterMap().isEmpty()))
      {
         return getPutDecodedFormParameters();
      }
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
      if (overridenStream != null) return overridenStream;
      try
      {
         return request.getInputStream();
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   public void setInputStream(InputStream stream)
   {
      this.overridenStream = stream;
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
      asynchronousResponse = new AbstractAsynchronousResponse()
      {
         public void setResponse(Response response)
         {
            try
            {
               setupResponse(ServerResponse.convertToServerResponse(response));
               dispatcher.asynchronousDelivery(HttpServletInputMessage.this, httpResponse, response);
            }
            finally
            {
               latch.countDown();
            }
         }
      };
      return asynchronousResponse;
   }

   public AsynchronousResponse getAsynchronousResponse()
   {
      return asynchronousResponse;
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

package org.jboss.resteasy.plugins.server.netty;

import org.jboss.resteasy.core.AbstractAsynchronousResponse;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.providers.FormUrlEncodedProvider;
import org.jboss.resteasy.spi.AsynchronousResponse;
import org.jboss.resteasy.util.Encode;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
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
public class NettyHttpRequest implements org.jboss.resteasy.spi.HttpRequest
{
   protected HttpHeaders httpHeaders;
   protected CountDownLatch latch;
   protected long suspendTimeout;
   protected SynchronousDispatcher dispatcher;
   protected boolean suspended;
   protected UriInfo uri;
   protected String httpMethod;
   protected String preProcessedPath;
   protected MultivaluedMap<String, String> formParameters;
   protected MultivaluedMap<String, String> decodedFormParameters;
   protected AbstractAsynchronousResponse asynchronousResponse;
   protected InputStream inputStream;
   protected Map<String, Object> attributes = new HashMap<String, Object>();
   protected NettyHttpResponse httpResponse;
   private final boolean iskeepAlive;
   private final boolean is100ContinueExpected;


   public NettyHttpRequest(HttpHeaders httpHeaders, UriInfo uri, String httpMethod, SynchronousDispatcher dispatcher, NettyHttpResponse httpResponse, boolean isKeepAlive, boolean is100ContinueExpected)
   {
      this.is100ContinueExpected = is100ContinueExpected;
      this.iskeepAlive = isKeepAlive;
      this.httpResponse = httpResponse;
      this.dispatcher = dispatcher;
      this.httpHeaders = httpHeaders;
      this.httpMethod = httpMethod;
      this.uri = uri;
      this.preProcessedPath = uri.getPath(false);

   }

   public MultivaluedMap<String, String> getFormParameters()
   {
      if (formParameters != null) return formParameters;
      if (getHttpHeaders().getMediaType().isCompatible(MediaType.valueOf("application/x-www-form-urlencoded")))
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

   public MultivaluedMap<String, String> getDecodedFormParameters()
   {
      if (decodedFormParameters != null) return decodedFormParameters;
      decodedFormParameters = Encode.decode(getFormParameters());
      return decodedFormParameters;
   }


   public Object getAttribute(String attribute)
   {
      return attributes.get(attribute);
   }

   public void setAttribute(String name, Object value)
   {
      attributes.put(name, value);
   }

   public void removeAttribute(String name)
   {
      attributes.remove(name);
   }

   public HttpHeaders getHttpHeaders()
   {
      return httpHeaders;
   }

   public InputStream getInputStream()
   {
      return inputStream;
   }

   public void setInputStream(InputStream stream)
   {
      this.inputStream = stream;
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
               setupResponse((ServerResponse) response);
               dispatcher.asynchronousDelivery(NettyHttpRequest.this, httpResponse, response);
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
   
   public NettyHttpResponse getResponse() 
   {
       return httpResponse;
   }
   
   public boolean isKeepAlive() {
       return iskeepAlive;
   }
   
   public boolean is100ContinueExpected() {
       return is100ContinueExpected;
   }
}

package org.jboss.resteasy.plugins.server.netty;

import org.jboss.resteasy.core.AbstractAsynchronousResponse;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.plugins.providers.FormUrlEncodedProvider;
import org.jboss.resteasy.plugins.server.netty.i18n.Messages;
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
 * @author Norman Maurer
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
   private final boolean is100ContinueExpected;


   public NettyHttpRequest(HttpHeaders httpHeaders, UriInfo uri, String httpMethod, SynchronousDispatcher dispatcher, NettyHttpResponse httpResponse, boolean is100ContinueExpected)
   {
      this.is100ContinueExpected = is100ContinueExpected;
      this.httpResponse = httpResponse;
      this.dispatcher = dispatcher;
      this.httpHeaders = httpHeaders;
      this.httpMethod = httpMethod;
      this.uri = uri;
      this.preProcessedPath = uri.getPath(false);

   }

   @Override
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
         throw new IllegalArgumentException(Messages.MESSAGES.requestMediaType());
      }
      return formParameters;
   }

   @Override
   public MultivaluedMap<String, String> getDecodedFormParameters()
   {
      if (decodedFormParameters != null) return decodedFormParameters;
      decodedFormParameters = Encode.decode(getFormParameters());
      return decodedFormParameters;
   }


   @Override
   public Object getAttribute(String attribute)
   {
      return attributes.get(attribute);
   }

   @Override
   public void setAttribute(String name, Object value)
   {
      attributes.put(name, value);
   }

   @Override
   public void removeAttribute(String name)
   {
      attributes.remove(name);
   }

   @Override
   public HttpHeaders getHttpHeaders()
   {
      return httpHeaders;
   }

   @Override
   public InputStream getInputStream()
   {
      return inputStream;
   }

   @Override
   public void setInputStream(InputStream stream)
   {
      this.inputStream = stream;
   }

   @Override
   public UriInfo getUri()
   {
      return uri;
   }

   @Override
   public String getHttpMethod()
   {
      return httpMethod;
   }

   @Override
   public String getPreprocessedPath()
   {
      return preProcessedPath;
   }

   @Override
   public void setPreprocessedPath(String path)
   {
      preProcessedPath = path;
   }

   @Override
   public AsynchronousResponse createAsynchronousResponse(long suspendTimeout)
   {
      suspended = true;
      latch = new CountDownLatch(1);
      this.suspendTimeout = suspendTimeout;
      asynchronousResponse = new AbstractAsynchronousResponse()
      {
          @Override
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

         @Override
         public void setFailure(Exception ex)
         {
            try
            {
               dispatcher.asynchronousDelivery(NettyHttpRequest.this, httpResponse, ex);
            }
            finally
            {
               latch.countDown();
            }

         }
      };
      return asynchronousResponse;
   }

   @Override
   public AsynchronousResponse getAsynchronousResponse()
   {
      return asynchronousResponse;
   }

   @Override
   public boolean isInitial()
   {
      return true;
   }
   
   @Override
   public boolean isSuspended()
   {
      return suspended;
   }


   @Override
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
   
   public boolean isKeepAlive() 
   {
       return httpResponse.isKeepAlive();
   }

   public boolean is100ContinueExpected() 
   {
       return is100ContinueExpected;
   }
}

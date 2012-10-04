package org.jboss.resteasy.plugins.server.netty;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.core.SynchronousExecutionContext;
import org.jboss.resteasy.plugins.providers.FormUrlEncodedProvider;
import org.jboss.resteasy.spi.ResteasyAsynchronousContext;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.jboss.resteasy.util.Encode;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
   protected SynchronousDispatcher dispatcher;
   protected ResteasyUriInfo uriInfo;
   protected String httpMethod;
   protected MultivaluedMap<String, String> formParameters;
   protected MultivaluedMap<String, String> decodedFormParameters;
   protected InputStream inputStream;
   protected Map<String, Object> attributes = new HashMap<String, Object>();
   protected NettyHttpResponse httpResponse;
   private final boolean is100ContinueExpected;


   public NettyHttpRequest(HttpHeaders httpHeaders, ResteasyUriInfo uri, String httpMethod, SynchronousDispatcher dispatcher, NettyHttpResponse httpResponse, boolean is100ContinueExpected)
   {
      this.is100ContinueExpected = is100ContinueExpected;
      this.httpResponse = httpResponse;
      this.dispatcher = dispatcher;
      this.httpHeaders = httpHeaders;
      this.httpMethod = httpMethod;
      this.uriInfo = uri;

   }

   @Override
   public void setHttpMethod(String method)
   {
      this.httpMethod = method;
   }

   @Override
   public Enumeration<String> getAttributeNames()
   {
      Enumeration<String> en = new Enumeration<String>()
      {
         private Iterator<String> it = attributes.keySet().iterator();
         @Override
         public boolean hasMoreElements()
         {
            return it.hasNext();
         }

         @Override
         public String nextElement()
         {
            return it.next();
         }
      };
      return en;
   }

   @Override
   public ResteasyAsynchronousContext getAsyncContext()
   {
      return new SynchronousExecutionContext(dispatcher, this, httpResponse);
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
         throw new IllegalArgumentException("Request media type is not application/x-www-form-urlencoded");
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
   public ResteasyUriInfo getUri()
   {
      return uriInfo;
   }

   @Override
   public String getHttpMethod()
   {
      return httpMethod;
   }

   @Override
   public void setRequestUri(URI requestUri) throws IllegalStateException
   {
      uriInfo = uriInfo.relative(requestUri);
   }

   @Override
   public void setRequestUri(URI baseUri, URI requestUri) throws IllegalStateException
   {
      uriInfo = new ResteasyUriInfo(baseUri, requestUri);
   }


   @Override
   public boolean isInitial()
   {
      return true;
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

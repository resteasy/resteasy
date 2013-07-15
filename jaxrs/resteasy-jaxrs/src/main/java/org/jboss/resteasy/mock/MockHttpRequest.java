package org.jboss.resteasy.mock;

import org.jboss.resteasy.plugins.providers.FormUrlEncodedProvider;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyAsynchronousContext;
import org.jboss.resteasy.spi.ResteasyAsynchronousResponse;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.jboss.resteasy.util.CaseInsensitiveMap;
import org.jboss.resteasy.util.Encode;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.ReadFromStream;

import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MockHttpRequest implements HttpRequest
{
   protected ResteasyHttpHeaders httpHeaders;
   protected InputStream inputStream;
   protected ResteasyUriInfo uri;
   protected String httpMethod;
   protected MultivaluedMap<String, String> formParameters;
   protected MultivaluedMap<String, String> decodedFormParameters;
   protected Map<String, Object> attributes = new HashMap<String, Object>();
   protected ResteasyAsynchronousContext asynchronousContext;


   protected MockHttpRequest()
   {
   }

   protected static final URI EMPTY_URI = URI.create("");

   protected static MockHttpRequest initWithUri(String uri) throws URISyntaxException
   {
      URI absoluteUri = new URI(uri);
      //URI baseUri = absoluteUri;
      URI baseUri = EMPTY_URI;
      return initWithUri(absoluteUri, baseUri);
   }

   private static MockHttpRequest initWithUri(URI absoluteUri, URI baseUri)
   {
      if (baseUri == null) baseUri = EMPTY_URI;
      MockHttpRequest request = new MockHttpRequest();
      request.httpHeaders = new ResteasyHttpHeaders(new CaseInsensitiveMap<String>());
      //request.uri = new UriInfoImpl(absoluteUri, absoluteUri, absoluteUri.getPath(), absoluteUri.getQuery(), PathSegmentImpl.parseSegments(absoluteUri.getPath()));
      
      // remove query part
      URI absolutePath = UriBuilder.fromUri(absoluteUri).replaceQuery(null).build();
      // path must be relative to the application's base uri
	   URI relativeUri = baseUri.relativize(absoluteUri);
      relativeUri = UriBuilder.fromUri(relativeUri.getRawPath()).replaceQuery(absoluteUri.getRawQuery()).build();
		
      request.uri = new ResteasyUriInfo(baseUri, relativeUri);
      return request;
   }

   public static MockHttpRequest create(String httpMethod, String uri) throws URISyntaxException
   {
      MockHttpRequest request = initWithUri(uri);
      request.httpMethod = httpMethod;
      return request;
   }

   public static MockHttpRequest create(String httpMethod, URI uriObj, URI baseUri)
   {
      MockHttpRequest request = initWithUri(uriObj, baseUri);
      request.httpMethod = httpMethod;
      return request;
   }

   public static MockHttpRequest options(String uri) throws URISyntaxException
   {
      MockHttpRequest request = initWithUri(uri);
      request.httpMethod = "OPTIONS";
      return request;
   }



   public static MockHttpRequest get(String uri) throws URISyntaxException
   {
      MockHttpRequest request = initWithUri(uri);
      request.httpMethod = "GET";
      return request;
   }

   public static MockHttpRequest post(String uri) throws URISyntaxException
   {
      MockHttpRequest request = initWithUri(uri);
      request.httpMethod = "POST";
      return request;
   }

   public static MockHttpRequest put(String uri) throws URISyntaxException
   {
      MockHttpRequest request = initWithUri(uri);
      request.httpMethod = "PUT";
      return request;
   }

   public static MockHttpRequest delete(String uri) throws URISyntaxException
   {
      MockHttpRequest request = initWithUri(uri);
      request.httpMethod = "DELETE";
      return request;
   }

   public static MockHttpRequest head(String uri) throws URISyntaxException
   {
      MockHttpRequest request = initWithUri(uri);
      request.httpMethod = "HEAD";
      return request;
   }

   public static MockHttpRequest deepCopy(HttpRequest request) throws IOException
   {
      MockHttpRequest mock = new MockHttpRequest();
      mock.uri = request.getUri();
      mock.httpHeaders = (ResteasyHttpHeaders) request.getHttpHeaders();
      mock.httpMethod = request.getHttpMethod();
      byte[] bytes = ReadFromStream.readFromStream(1024, request.getInputStream());
      mock.inputStream = new ByteArrayInputStream(bytes);
      return mock;
   }

   @Override
   public void setHttpMethod(String method)
   {
      httpMethod = method;
   }

   public ResteasyAsynchronousContext getAsynchronousContext()
   {
      return asynchronousContext;
   }

   public void setAsynchronousContext(ResteasyAsynchronousContext asynchronousContext)
   {
      this.asynchronousContext = asynchronousContext;
   }

   public MockHttpRequest header(String name, String value)
   {
      httpHeaders.getRequestHeaders().add(name, value);
      return this;
   }

   public MockHttpRequest accept(List<MediaType> accepts)
   {
      for (MediaType accept : accepts)
      {
         accept(accept);
      }
      return this;
   }

   public MockHttpRequest accept(MediaType accept)
   {
      httpHeaders.getMutableHeaders().add(HttpHeaders.ACCEPT, accept.toString());
      return this;
   }

   public MockHttpRequest accept(String type)
   {
      httpHeaders.getMutableHeaders().add(HttpHeaderNames.ACCEPT, type);
      return this;
   }

   public MockHttpRequest language(String language)
   {
      httpHeaders.getMutableHeaders().add(HttpHeaderNames.ACCEPT_LANGUAGE, language);
      return this;
   }

   public MockHttpRequest cookie(String name, String value)
   {
      Cookie cookie = new Cookie(name, value);
      httpHeaders.getCookies().put(name, cookie);
      return this;
   }

   public MockHttpRequest contentType(String type)
   {
      httpHeaders.getMutableHeaders().add(HttpHeaderNames.CONTENT_TYPE, type);
      return this;
   }

   public MockHttpRequest contentType(MediaType type)
   {
      if (type == null)
      {
         httpHeaders.getMutableHeaders().remove(HttpHeaderNames.CONTENT_TYPE);
         return this;
      }
      httpHeaders.getMutableHeaders().add(HttpHeaderNames.CONTENT_TYPE, type.toString());
      return this;
   }

   public MockHttpRequest content(byte[] bytes)
   {
      inputStream = new ByteArrayInputStream(bytes);
      return this;
   }

   public MockHttpRequest content(InputStream stream)
   {
      inputStream = stream;
      return this;
   }

   /**
    * Set CONTENT-TYPE to ""application/x-www-form-urlencoded"
    *
    * @param name
    * @param value
    * @return
    */
   public MockHttpRequest addFormHeader(String name, String value)
   {
      if (decodedFormParameters == null)
      {
         decodedFormParameters = new MultivaluedMapImpl<String, String>();
         contentType("application/x-www-form-urlencoded");
      }
      decodedFormParameters.add(name, value);
      return this;
   }

   public HttpHeaders getHttpHeaders()
   {
      return httpHeaders;
   }

   @Override
   public MultivaluedMap<String, String> getMutableHeaders()
   {
      return httpHeaders.getMutableHeaders();
   }

   public InputStream getInputStream()
   {
      return inputStream;
   }

   public void setInputStream(InputStream stream)
   {
      this.inputStream = stream;
   }

   public ResteasyUriInfo getUri()
   {
      return uri;
   }

   public String getHttpMethod()
   {
      return httpMethod;
   }

   public MultivaluedMap<String, String> getFormParameters()
   {
      if (formParameters != null) return formParameters;
      if (decodedFormParameters != null)
      {
         formParameters = Encode.encode(decodedFormParameters);
         return formParameters;
      }

      if (getHttpHeaders().getMediaType().isCompatible(MediaType.valueOf("application/x-www-form-urlencoded")))
      {
         try
         {
            formParameters = FormUrlEncodedProvider.parseForm(getInputStream());
         }
         catch (IOException e)
         {
            throw new BadRequestException(e);
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

   @Override
   public void setRequestUri(URI requestUri) throws IllegalStateException
   {
      uri = uri.setRequestUri(requestUri);
   }

   @Override
   public void setRequestUri(URI baseUri, URI requestUri) throws IllegalStateException
   {
      uri = new ResteasyUriInfo(baseUri.resolve(requestUri));
   }

   public boolean isInitial()
   {
      return true;
   }

   public void initialRequestThreadFinished()
   {
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
      if (asynchronousContext != null) return asynchronousContext;
      else return  new ResteasyAsynchronousContext()
      {
         @Override
         public boolean isSuspended()
         {
            return false;
         }

         @Override
         public ResteasyAsynchronousResponse getAsyncResponse()
         {
            return null;
         }

         @Override
         public ResteasyAsynchronousResponse suspend() throws IllegalStateException
         {
            return null;
         }

         @Override
         public ResteasyAsynchronousResponse suspend(long millis) throws IllegalStateException
         {
            return null;
         }

         @Override
         public ResteasyAsynchronousResponse suspend(long time, TimeUnit unit) throws IllegalStateException
         {
            return null;
         }
      };
   }
}

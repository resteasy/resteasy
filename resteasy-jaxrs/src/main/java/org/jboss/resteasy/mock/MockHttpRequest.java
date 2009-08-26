package org.jboss.resteasy.mock;

import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.plugins.providers.FormUrlEncodedProvider;
import org.jboss.resteasy.specimpl.HttpHeadersImpl;
import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.specimpl.PathSegmentImpl;
import org.jboss.resteasy.specimpl.UriInfoImpl;
import org.jboss.resteasy.spi.AsynchronousResponse;
import org.jboss.resteasy.spi.BadRequestException;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.util.Encode;
import org.jboss.resteasy.util.HttpHeaderNames;
import org.jboss.resteasy.util.LocaleHelper;
import org.jboss.resteasy.util.ReadFromStream;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MockHttpRequest implements HttpRequest
{
   protected HttpHeadersImpl httpHeaders;
   protected InputStream inputStream;
   protected UriInfo uri;
   protected String httpMethod;
   protected String preprocessedPath;
   protected MultivaluedMap<String, String> formParameters;
   protected MultivaluedMap<String, String> decodedFormParameters;
   protected Map<String, Object> attributes = new HashMap<String, Object>();


   protected MockHttpRequest()
   {
   }

   protected static MockHttpRequest initWithUri(String uri) throws URISyntaxException
   {
      URI absoluteUri = new URI(uri);
      MockHttpRequest request = new MockHttpRequest();
      request.httpHeaders = new HttpHeadersImpl();
      request.httpHeaders.setAcceptableLanguages(new ArrayList<String>());
      request.httpHeaders.setAcceptableMediaTypes(new ArrayList<MediaType>());
      request.httpHeaders.setCookies(new HashMap<String, Cookie>());
      request.httpHeaders.setRequestHeaders(new Headers<String>());
      //request.uri = new UriInfoImpl(absoluteUri, absoluteUri, absoluteUri.getPath(), absoluteUri.getQuery(), PathSegmentImpl.parseSegments(absoluteUri.getPath()));
      request.uri = new UriInfoImpl(absoluteUri, absoluteUri, absoluteUri.getPath(), absoluteUri.getRawQuery(), PathSegmentImpl.parseSegments(absoluteUri.getPath()));
      request.preprocessedPath = request.uri.getPath(false);
      return request;

   }

   public static MockHttpRequest create(String httpMethod, String uri) throws URISyntaxException
   {
      MockHttpRequest request = initWithUri(uri);
      request.httpMethod = httpMethod;
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
      mock.httpHeaders = (HttpHeadersImpl) request.getHttpHeaders();
      mock.httpMethod = request.getHttpMethod();
      mock.inputStream = new ByteArrayInputStream(ReadFromStream.readFromStream(1024, request.getInputStream()));
      mock.preprocessedPath = request.getPreprocessedPath();
      return mock;
   }


   public MockHttpRequest header(String name, String value)
   {
      httpHeaders.getRequestHeaders().add(name, value);
      return this;
   }

   public MockHttpRequest accept(List<MediaType> accepts)
   {
      httpHeaders.setAcceptableMediaTypes(accepts);
      return this;
   }

   public MockHttpRequest accept(String type)
   {
      httpHeaders.getRequestHeaders().add(HttpHeaderNames.ACCEPT, type);
      httpHeaders.getAcceptableMediaTypes().add(MediaType.valueOf(type));
      return this;
   }

   public MockHttpRequest language(String language)
   {
      httpHeaders.getRequestHeaders().add(HttpHeaderNames.ACCEPT_LANGUAGE, language);
      httpHeaders.getAcceptableLanguages().add(LocaleHelper.extractLocale(language));
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
      httpHeaders.getRequestHeaders().add(HttpHeaderNames.CONTENT_TYPE, type);
      httpHeaders.setMediaType(MediaType.valueOf(type));
      return this;
   }

   public MockHttpRequest contentType(MediaType type)
   {
      httpHeaders.getRequestHeaders().add(HttpHeaderNames.CONTENT_TYPE, type.toString());
      httpHeaders.setMediaType(type);
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

   public InputStream getInputStream()
   {
      return inputStream;
   }

   public UriInfo getUri()
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

   public String getPreprocessedPath()
   {
      return preprocessedPath;
   }

   public void setPreprocessedPath(String path)
   {
      preprocessedPath = path;
   }

   public void suspend()
   {
      throw new UnsupportedOperationException();
   }

   public void suspend(long timeout)
   {
      throw new UnsupportedOperationException();
   }

   public void complete()
   {
      throw new UnsupportedOperationException();
   }

   public boolean isInitial()
   {
      return true;
   }

   public boolean isSuspended()
   {
      return false;
   }

   public boolean isTimeout()
   {
      return false;
   }

   public AsynchronousResponse createAsynchronousResponse(long suspendTimeout)
   {
      throw new UnsupportedOperationException("NOT SUPPORTED");
   }

   public AsynchronousResponse getAsynchronousResponse()
   {
      throw new UnsupportedOperationException("NOT SUPPORTED");
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
}

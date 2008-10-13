package org.jboss.resteasy.util;

import org.jboss.resteasy.plugins.providers.FormUrlEncodedProvider;
import org.jboss.resteasy.spi.HttpRequest;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;

/**
 * Http request abstraction implementation
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class HttpRequestImpl implements HttpRequest
{
   protected HttpHeaders httpHeaders;
   protected InputStream inputStream;
   protected UriInfo uri;
   protected String httpMethod;
   protected String preProcessedPath;
   protected MultivaluedMap<String, String> formParameters;
   protected MultivaluedMap<String, String> decodedFormParameters;

   public HttpRequestImpl(InputStream inputStream, HttpHeaders httpHeaders, String httpMethod, UriInfo uri)
   {
      this.inputStream = inputStream;
      this.httpHeaders = httpHeaders;
      this.httpMethod = httpMethod;
      this.uri = uri;
      this.preProcessedPath = uri.getPath(false);
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

   public String getPreprocessedPath()
   {
      return preProcessedPath;
   }

   public void setPreprocessedPath(String path)
   {
      preProcessedPath = path;
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

   public void suspend()
   {
      throw new UnsupportedOperationException("UNSUPPORTED OPERATION");
   }

   public void suspend(long timeout)
   {
      throw new UnsupportedOperationException("UNSUPPORTED OPERATION");
   }

   public void complete()
   {
      throw new UnsupportedOperationException("UNSUPPORTED OPERATION");
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
}

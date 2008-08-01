package org.jboss.resteasy.util;

import org.jboss.resteasy.plugins.providers.FormUrlEncodedProvider;
import org.jboss.resteasy.spi.HttpRequest;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Http request abstraction implementation
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpRequestImpl implements HttpRequest
{
   protected HttpHeaders httpHeaders;
   protected InputStream inputStream;
   protected UriInfo uri;
   protected String httpMethod;
   protected List<PathSegment> preProcessedSegments;
   protected MultivaluedMap<String, String> formParameters;
   protected MultivaluedMap<String, String> decodedFormParameters;

   public HttpRequestImpl(InputStream inputStream, HttpHeaders httpHeaders, String httpMethod, UriInfo uri)
   {
      this.inputStream = inputStream;
      this.httpHeaders = httpHeaders;
      this.httpMethod = httpMethod;
      this.uri = uri;
      this.preProcessedSegments = uri.getPathSegments(false);
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

   public List<PathSegment> getPreProcessedSegments()
   {
      return preProcessedSegments;
   }

   public void setPreProcessedSegments(List<PathSegment> segments)
   {
      this.preProcessedSegments = segments;
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
}

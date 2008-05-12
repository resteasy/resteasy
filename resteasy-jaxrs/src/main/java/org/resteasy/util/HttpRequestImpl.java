package org.resteasy.util;

import org.resteasy.spi.HttpRequest;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriInfo;
import java.io.InputStream;
import java.util.List;

/**
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

   public HttpRequestImpl(InputStream inputStream, HttpHeaders httpHeaders, String httpMethod, UriInfo uri)
   {
      this.inputStream = inputStream;
      this.httpHeaders = httpHeaders;
      this.httpMethod = httpMethod;
      this.uri = uri;
      this.preProcessedSegments = uri.getPathSegments();
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
}

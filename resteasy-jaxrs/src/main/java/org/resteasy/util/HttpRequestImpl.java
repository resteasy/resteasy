package org.resteasy.util;

import org.resteasy.spi.HttpRequest;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.io.InputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpRequestImpl implements HttpRequest
{
   protected HttpHeaders httpHeaders;
   protected InputStream inputStream;
   protected UriInfo uri;
   protected MultivaluedMap<String, String> parameters;
   protected String httpMethod;

   public HttpRequestImpl(InputStream inputStream, HttpHeaders httpHeaders, String httpMethod, UriInfo uri, MultivaluedMap<String, String> parameters)
   {
      this.inputStream = inputStream;
      this.httpHeaders = httpHeaders;
      this.httpMethod = httpMethod;
      this.uri = uri;
      this.parameters = parameters;
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

   public MultivaluedMap<String, String> getParameters()
   {
      return parameters;
   }

   public String getHttpMethod()
   {
      return httpMethod;
   }
}

package org.jboss.resteasy.plugins.server.sun.http;

import com.sun.net.httpserver.HttpExchange;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.core.SynchronousExecutionContext;
import org.jboss.resteasy.plugins.server.BaseHttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyAsynchronousContext;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpServerRequest extends BaseHttpRequest
{
   protected HttpExchange exchange;
   protected HttpHeaders httpHeaders;
   protected UriInfo uriInfo;
   protected String preProcessedPath;
   protected InputStream inputStream;
   protected Map<String, Object> attributes = new HashMap<String, Object>();


   public HttpServerRequest(SynchronousDispatcher dispatcher, HttpResponse httpResponse, HttpExchange exchange)
   {
      super( dispatcher);
      this.httpResponse = httpResponse;
      this.exchange = exchange;
      this.uriInfo = HttpExchangeUtil.extractUriInfo(exchange);
      this.httpHeaders = HttpExchangeUtil.extractHttpHeaders(exchange);
      this.preProcessedPath = uriInfo.getPath(false);
   }

   @Override
   public Map<String, Object> getProperties()
   {
      return attributes;
   }

   @Override
   public HttpHeaders getHttpHeaders()
   {
      return httpHeaders;
   }

   @Override
   public InputStream getInputStream()
   {
      return exchange.getRequestBody();
   }

   @Override
   public void setInputStream(InputStream stream)
   {
      exchange.setStreams(stream, exchange.getResponseBody());
   }

   @Override
   public UriInfo getUri()
   {
      return uriInfo;
   }

   @Override
   public String getHttpMethod()
   {
      return exchange.getRequestMethod().toUpperCase();
   }

   @Override
   public String getPreprocessedPath()
   {
      return preProcessedPath;
   }

   @Override
   public void setPreprocessedPath(String path)
   {
      this.preProcessedPath = path;
   }

   @Override
   public Object getAttribute(String attribute)
   {
      Object val = attributes.get(attribute);
      if (val != null) return val;
      return exchange.getAttribute(attribute);
   }

   @Override
   public void setAttribute(String name, Object value)
   {
      attributes.put(name, value);
      exchange.setAttribute(name, value);
   }

   @Override
   public void removeAttribute(String name)
   {
      attributes.remove(name);
      exchange.setAttribute(name, null);
   }

   @Override
   public ResteasyAsynchronousContext getExecutionContext()
   {
      return new SynchronousExecutionContext(dispatcher, this, httpResponse);
   }
}

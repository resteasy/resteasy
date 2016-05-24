package org.jboss.resteasy.plugins.server.sun.http;

import com.sun.net.httpserver.HttpExchange;
import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.core.SynchronousExecutionContext;
import org.jboss.resteasy.plugins.server.BaseHttpRequest;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.NotImplementedYetException;
import org.jboss.resteasy.spi.ResteasyAsynchronousContext;
import org.jboss.resteasy.spi.ResteasyUriInfo;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import java.io.InputStream;
import java.net.URI;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpServerRequest extends BaseHttpRequest
{
   protected SynchronousDispatcher dispatcher;
   protected HttpResponse httpResponse;
   protected HttpExchange exchange;
   protected ResteasyHttpHeaders httpHeaders;
   protected String preProcessedPath;
   protected Map<String, Object> attributes = new HashMap<String, Object>();
   protected String httpMethod;


   public HttpServerRequest(SynchronousDispatcher dispatcher, HttpResponse httpResponse, HttpExchange exchange)
   {
      super(HttpExchangeUtil.extractUriInfo(exchange));
      this.dispatcher = dispatcher;
      this.httpResponse = httpResponse;
      this.exchange = exchange;
      this.httpHeaders = HttpExchangeUtil.extractHttpHeaders(exchange);
      this.preProcessedPath = uri.getPath(false);
      this.httpMethod = exchange.getRequestMethod().toUpperCase();
   }

   @Override
   public MultivaluedMap<String, String> getMutableHeaders()
   {
      return httpHeaders.getMutableHeaders();
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
   public String getHttpMethod()
   {
      return httpMethod;
   }

   @Override
   public void setHttpMethod(String method)
   {
      this.httpMethod = method;
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
   public void forward(String path)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public boolean wasForwarded()
   {
      return false;
   }
}

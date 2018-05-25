package org.jboss.resteasy.plugins.server.netty;

import org.jboss.resteasy.core.SynchronousDispatcher;
import org.jboss.resteasy.core.SynchronousExecutionContext;
import org.jboss.resteasy.plugins.providers.FormUrlEncodedProvider;
import org.jboss.resteasy.plugins.server.BaseHttpRequest;
import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.jboss.resteasy.spi.NotImplementedYetException;
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
 * <p>
 * We have this abstraction so that we can reuse marshalling objects in a client framework and serverside framework
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @author Norman Maurer
 * @version $Revision: 1 $
 */
public class NettyHttpRequest extends BaseHttpRequest
{
   protected ResteasyHttpHeaders httpHeaders;
   protected SynchronousDispatcher dispatcher;
   protected String httpMethod;
   protected InputStream inputStream;
   protected Map<String, Object> attributes = new HashMap<String, Object>();
   protected NettyHttpResponse httpResponse;
   private final boolean is100ContinueExpected;


   public NettyHttpRequest(ResteasyHttpHeaders httpHeaders, ResteasyUriInfo uri, String httpMethod, SynchronousDispatcher dispatcher, NettyHttpResponse httpResponse, boolean is100ContinueExpected)
   {
      super(uri);
      this.is100ContinueExpected = is100ContinueExpected;
      this.httpResponse = httpResponse;
      this.dispatcher = dispatcher;
      this.httpHeaders = httpHeaders;
      this.httpMethod = httpMethod;

   }

   @Override
   public MultivaluedMap<String, String> getMutableHeaders()
   {
      return httpHeaders.getMutableHeaders();
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
   public String getHttpMethod()
   {
      return httpMethod;
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

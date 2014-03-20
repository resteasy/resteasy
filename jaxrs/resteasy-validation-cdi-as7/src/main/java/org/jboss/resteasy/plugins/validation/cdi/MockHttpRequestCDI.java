package org.jboss.resteasy.plugins.validation.cdi;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;

import org.jboss.resteasy.specimpl.ResteasyHttpHeaders;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyAsynchronousContext;
import org.jboss.resteasy.spi.ResteasyUriInfo;
import org.jboss.resteasy.util.CaseInsensitiveMap;

/**
 * 
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Feb 11, 2014
 */
public class MockHttpRequestCDI implements HttpRequest
{
   private HttpHeaders headers;
   private Map<String, Object> attributes = new HashMap<String, Object>();
   
   public MockHttpRequestCDI(HttpServletRequest request)
   {
      MultivaluedMap<String, String> map = new CaseInsensitiveMap<String>();
      if (request != null)
      {
         for (Enumeration<String> e = request.getHeaderNames(); e.hasMoreElements(); )
         {
            String key = e.nextElement();
            ArrayList<String> list = new ArrayList<String>();
            for (Enumeration<String> e2 = request.getHeaders(key); e2.hasMoreElements(); )
            {
               list.add(e2.nextElement());
            }
            map.put(key, list);
         }
      }
      headers = new ResteasyHttpHeaders(map);
   }
   
   @Override
   public HttpHeaders getHttpHeaders()
   {
      return headers;
   }

   @Override
   public MultivaluedMap<String, String> getMutableHeaders()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public InputStream getInputStream()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void setInputStream(InputStream stream)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public ResteasyUriInfo getUri()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public String getHttpMethod()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public void setHttpMethod(String method)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void setRequestUri(URI requestUri) throws IllegalStateException
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public void setRequestUri(URI baseUri, URI requestUri)
         throws IllegalStateException
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public MultivaluedMap<String, String> getFormParameters()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public MultivaluedMap<String, String> getDecodedFormParameters()
   {
      // TODO Auto-generated method stub
      return null;
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
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public boolean isInitial()
   {
      // TODO Auto-generated method stub
      return false;
   }

   @Override
   public void forward(String path)
   {
      // TODO Auto-generated method stub
      
   }

   @Override
   public boolean wasForwarded()
   {
      // TODO Auto-generated method stub
      return false;
   }

}

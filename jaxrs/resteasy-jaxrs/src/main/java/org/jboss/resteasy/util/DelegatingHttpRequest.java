package org.jboss.resteasy.util;

import org.jboss.resteasy.spi.AsynchronousResponse;
import org.jboss.resteasy.spi.HttpRequest;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.io.InputStream;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class DelegatingHttpRequest implements HttpRequest
{
   private HttpRequest delegate;

   public DelegatingHttpRequest(HttpRequest delegate)
   {
      this.delegate = delegate;
   }

   @Override
   public HttpHeaders getHttpHeaders()
   {
      return delegate.getHttpHeaders();
   }

   @Override
   public InputStream getInputStream()
   {
      return delegate.getInputStream();
   }

   @Override
   public void setInputStream(InputStream stream)
   {
      delegate.setInputStream(stream);
   }

   @Override
   public UriInfo getUri()
   {
      return delegate.getUri();
   }

   @Override
   public String getHttpMethod()
   {
      return delegate.getHttpMethod();
   }

   @Override
   public String getPreprocessedPath()
   {
      return delegate.getPreprocessedPath();
   }

   @Override
   public void setPreprocessedPath(String path)
   {
      delegate.setPreprocessedPath(path);
   }

   @Override
   public MultivaluedMap<String, String> getFormParameters()
   {
      return delegate.getFormParameters();
   }

   @Override
   public MultivaluedMap<String, String> getDecodedFormParameters()
   {
      return delegate.getDecodedFormParameters();
   }

   @Override
   public Object getAttribute(String attribute)
   {
      return delegate.getAttribute(attribute);
   }

   @Override
   public void setAttribute(String name, Object value)
   {
      delegate.setAttribute(name, value);
   }

   @Override
   public void removeAttribute(String name)
   {
      delegate.removeAttribute(name);
   }

   @Override
   public boolean isInitial()
   {
      return delegate.isInitial();
   }

   @Override
   public boolean isSuspended()
   {
      return delegate.isSuspended();
   }

   @Override
   public AsynchronousResponse createAsynchronousResponse(long suspendTimeout)
   {
      return delegate.createAsynchronousResponse(suspendTimeout);
   }

   @Override
   public AsynchronousResponse getAsynchronousResponse()
   {
      return delegate.getAsynchronousResponse();
   }

   @Override
   public void initialRequestThreadFinished()
   {
      delegate.initialRequestThreadFinished();
   }
}

package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.NotImplementedYetException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.MessageProcessingException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class PreMatchContainerRequestContext implements ContainerRequestContext
{
   protected final HttpRequest httpRequest;
   protected Response response;

   public PreMatchContainerRequestContext(HttpRequest request)
   {
      this.httpRequest = request;
   }

   public HttpRequest getHttpRequest()
   {
      return httpRequest;
   }

   public Response getResponseAbortedWith()
   {
      return response;
   }

   @Override
   public Object getProperty(String name)
   {
      return httpRequest.getAttribute(name);
   }

   @Override
   public Enumeration<String> getPropertyNames()
   {
      return httpRequest.getAttributeNames();
   }

   @Override
   public void setProperty(String name, Object object)
   {
      httpRequest.setAttribute(name, object);
   }

   @Override
   public void removeProperty(String name)
   {
      httpRequest.removeAttribute(name);
   }

   @Override
   public UriInfo getUriInfo()
   {
      return httpRequest.getUri();
   }

   @Override
   public void setRequestUri(URI requestUri) throws IllegalStateException
   {
      httpRequest.setRequestUri(requestUri);
   }

   @Override
   public void setRequestUri(URI baseUri, URI requestUri) throws IllegalStateException
   {
      httpRequest.setRequestUri(baseUri, requestUri);
   }

   @Override
   public String getMethod()
   {
      return httpRequest.getHttpMethod();
   }

   @Override
   public void setMethod(String method)
   {
      httpRequest.setHttpMethod(method);
   }

   @Override
   public MultivaluedMap<String, String> getHeaders()
   {
      return httpRequest.getHttpHeaders().getRequestHeaders();
   }

   @Override
   public Date getDate()
   {
      return httpRequest.getHttpHeaders().getDate();
   }

   @Override
   public Locale getLanguage()
   {
      return httpRequest.getHttpHeaders().getLanguage();
   }

   @Override
   public int getLength()
   {
      return httpRequest.getHttpHeaders().getLength();
   }

   @Override
   public MediaType getMediaType()
   {
      return httpRequest.getHttpHeaders().getMediaType();
   }

   @Override
   public List<MediaType> getAcceptableMediaTypes()
   {
      return httpRequest.getHttpHeaders().getAcceptableMediaTypes();
   }

   @Override
   public List<Locale> getAcceptableLanguages()
   {
      return httpRequest.getHttpHeaders().getAcceptableLanguages();
   }

   @Override
   public Map<String, Cookie> getCookies()
   {
      return httpRequest.getHttpHeaders().getCookies();
   }

   @Override
   public boolean hasEntity()
   {
      return false;
   }

   @Override
   public InputStream getEntityStream()
   {
      return httpRequest.getInputStream();
   }

   @Override
   public void setEntityStream(InputStream entityStream)
   {
      httpRequest.setInputStream(entityStream);
   }

   @Override
   public SecurityContext getSecurityContext()
   {
      return ResteasyProviderFactory.getContextData(SecurityContext.class);
   }

   @Override
   public void setSecurityContext(SecurityContext context)
   {
      ResteasyProviderFactory.pushContext(SecurityContext.class, context);
   }

   @Override
   public Request getRequest()
   {
      return ResteasyProviderFactory.getContextData(Request.class);
   }

   @Override
   public void abortWith(Response response)
   {
      this.response = response;
   }

   @Override
   public String getHeaderString(String name)
   {
      return httpRequest.getFormParameters().getFirst(name);
   }
}

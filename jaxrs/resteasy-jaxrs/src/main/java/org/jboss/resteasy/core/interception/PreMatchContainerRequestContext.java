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
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.util.Date;
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
   public Map<String, Object> getProperties()
   {
      return httpRequest.getProperties();
   }

   @Override
   public URI getBaseUri()
   {
      return httpRequest.getUri().getBaseUri();
   }

   @Override
   public String getPath()
   {
      return httpRequest.getUri().getPath();
   }

   @Override
   public void setBaseUri(URI uri)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public void setPath(String path)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public String getMethod()
   {
      return httpRequest.getHttpMethod();
   }

   @Override
   public void setMethod(String method)
   {
      throw new NotImplementedYetException();
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
   public <T> void writeEntity(Class<T> type, Annotation[] annotations, MediaType mediaType, T entity)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public <T> void writeEntity(GenericType<T> genericType, Annotation[] annotations, MediaType mediaType, T entity)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public <T> T readEntity(Class<T> type) throws MessageProcessingException
   {
      throw new NotImplementedYetException();
   }

   @Override
   public <T> T readEntity(GenericType<T> entityType) throws MessageProcessingException
   {
      throw new NotImplementedYetException();
   }

   @Override
   public <T> T readEntity(Class<T> type, Annotation[] annotations) throws MessageProcessingException
   {
      throw new NotImplementedYetException();
   }

   @Override
   public <T> T readEntity(GenericType<T> entityType, Annotation[] annotations) throws MessageProcessingException
   {
      throw new NotImplementedYetException();
   }

   @Override
   public void bufferEntity() throws MessageProcessingException
   {
      throw new NotImplementedYetException();
   }

   @Override
   public SecurityContext getSecurityContext()
   {
      return ResteasyProviderFactory.getContextData(SecurityContext.class);
   }

   @Override
   public void setSecurityContext(SecurityContext context)
   {
      throw new NotImplementedYetException();
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

}

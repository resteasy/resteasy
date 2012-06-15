package org.jboss.resteasy.client.jaxrs.internal;

import org.jboss.resteasy.spi.NotImplementedYetException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.Configuration;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.OutputStream;
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
public class ClientRequestContextImpl implements ClientRequestContext
{
   protected ClientInvocation invocation;
   protected Response abortedWithResponse;

   public ClientRequestContextImpl(ClientInvocation invocation)
   {
      this.invocation = invocation;
   }

   public Response getAbortedWithResponse()
   {
      return abortedWithResponse;
   }

   @Override
   public Map<String, Object> getProperties()
   {
      return invocation.getMutableProperties();
   }

   @Override
   public URI getUri()
   {
      return invocation.getUri();
   }

   @Override
   public void setUri(URI uri)
   {
      invocation.setUri(uri);
   }

   @Override
   public String getMethod()
   {
      return invocation.getMethod();
   }

   @Override
   public void setMethod(String method)
   {
      invocation.setMethod(method);
   }

   @Override
   public MultivaluedMap<String, Object> getHeaders()
   {
      return invocation.getHeaders().getHeaders();
   }

   @Override
   public Date getDate()
   {
      return invocation.getHeaders().getDate();
   }

   @Override
   public Locale getLanguage()
   {
      return invocation.getHeaders().getLanguage();
   }

   @Override
   public MediaType getMediaType()
   {
      return invocation.getHeaders().getMediaType();
   }

   @Override
   public List<MediaType> getAcceptableMediaTypes()
   {
      return invocation.getHeaders().getAcceptableMediaTypes();
   }

   @Override
   public List<Locale> getAcceptableLanguages()
   {
      return invocation.getHeaders().getAcceptableLanguages();
   }

   @Override
   public Map<String, Cookie> getCookies()
   {
      return invocation.getHeaders().getCookies();
   }

   @Override
   public boolean hasEntity()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public OutputStream getEntityStream()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public void setEntityStream(OutputStream entityStream)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public <T> void setEntity(Class<T> type, Annotation[] annotations, MediaType mediaType, T entity)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public <T> void setEntity(GenericType<T> genericType, Annotation[] annotations, MediaType mediaType, T entity)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Object getEntity()
   {
      return invocation.getEntity();
   }

   @Override
   public GenericType<?> getDeclaredEntityType()
   {
      if (invocation.getEntity() == null) return null;
      if (invocation.getEntity() instanceof GenericType) return (GenericType)invocation.getEntity();

      return GenericType.of(invocation.getEntity().getClass(), null);
   }

   @Override
   public Annotation[] getEntityAnnotations()
   {
      return invocation.getEntityAnnotations();
   }

   @Override
   public Client getClient()
   {
      return invocation.getClient();
   }

   @Override
   public Configuration getConfiguration()
   {
      return invocation.getConfiguration();
   }

   @Override
   public void abortWith(Response response)
   {
      abortedWithResponse = response;
   }
}

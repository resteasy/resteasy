package org.jboss.resteasy.client.jaxrs.internal;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
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
   public Object getProperty(String name)
   {
      return invocation.getMutableProperties().get(name);
   }

   @Override
   public Collection<String> getPropertyNames()
   {
      return Collections.unmodifiableSet(invocation.getMutableProperties().keySet());
   }

   @Override
   public void setProperty(String name, Object object)
   {
      invocation.getMutableProperties().put(name, object);
   }

   @Override
   public void removeProperty(String name)
   {
      invocation.getMutableProperties().remove(name);
   }

   @Override
   public Class<?> getEntityClass()
   {
      return invocation.getEntityClass();
   }

   @Override
   public Type getEntityType()
   {
      return invocation.getEntityGenericType();
   }

   @Override
   public void setEntity(Object entity)
   {
      if (entity instanceof Entity)
      {
         invocation.setEntity((Entity)entity);
      }
      else
      {

         invocation.setEntityObject(entity);
      }

   }

   @Override
   public void setEntity(Object entity, Annotation[] annotations, MediaType mediaType)
   {
      if (entity instanceof Entity)
      {
         invocation.setEntity((Entity)entity);
      }
      else
      {
         invocation.setEntity(Entity.entity(entity, mediaType));
      }
      invocation.setEntityAnnotations(annotations);
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
      List<MediaType> rtn = invocation.getHeaders().getAcceptableMediaTypes();
      if (rtn.size() == 0)
      {
         rtn = new ArrayList<MediaType>();
         rtn.add(MediaType.WILDCARD_TYPE);
      }
      return rtn;
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
      return invocation.getEntity() != null;
   }

   @Override
   public OutputStream getEntityStream()
   {
      return invocation.getEntityStream();
   }

   @Override
   public void setEntityStream(OutputStream entityStream)
   {
      invocation.setEntityStream(entityStream);
   }

   @Override
   public Object getEntity()
   {
      return invocation.getEntity();
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

   @Override
   public MultivaluedMap<String, String> getStringHeaders()
   {
      return invocation.getHeaders().asMap();
   }

   @Override
   public String getHeaderString(String name)
   {
      return invocation.getHeaders().getHeader(name);
   }
}

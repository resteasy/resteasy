package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;

import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ContainerResponseContextImpl implements ContainerResponseContext
{
   protected final HttpRequest request;
   protected final HttpResponse httpResponse;
   protected final BuiltResponse jaxrsResponse;

   public ContainerResponseContextImpl(HttpRequest request, HttpResponse httpResponse, BuiltResponse serverResponse)
   {
      this.request = request;
      this.httpResponse = httpResponse;
      this.jaxrsResponse = serverResponse;
   }

   public BuiltResponse getJaxrsResponse()
   {
      return jaxrsResponse;
   }

   public HttpResponse getHttpResponse()
   {
      return httpResponse;
   }

   @Override
   public int getStatus()
   {
      return httpResponse.getStatus();
   }

   @Override
   public void setStatus(int code)
   {
      httpResponse.setStatus(code);
   }

   @Override
   public Response.StatusType getStatusInfo()
   {
      return Response.Status.fromStatusCode(httpResponse.getStatus());
   }

   @Override
   public void setStatusInfo(Response.StatusType statusInfo)
   {
      httpResponse.setStatus(statusInfo.getStatusCode());
   }

   @Override
   public Class<?> getEntityClass()
   {
      if (jaxrsResponse.getEntity() == null) return null;
      return jaxrsResponse.getEntity().getClass();
   }

   @Override
   public Type getEntityType()
   {
      return jaxrsResponse.getGenericType();
   }

   @Override
   public void setEntity(Object entity)
   {
      jaxrsResponse.setEntity(entity);
   }

   @Override
   public void setEntity(Object entity, Annotation[] annotations, MediaType mediaType)
   {
      jaxrsResponse.setEntity(entity);
      jaxrsResponse.setAnnotations(annotations);
      jaxrsResponse.getHeaders().putSingle(HttpHeaders.CONTENT_TYPE, mediaType);
   }

   @Override
   public MultivaluedMap<String, Object> getHeaders()
   {
      return jaxrsResponse.getMetadata();
   }

   @Override
   public Set<String> getAllowedMethods()
   {
     return jaxrsResponse.getAllowedMethods();
   }

   @Override
   public Date getDate()
   {
      return jaxrsResponse.getDate();
   }

   @Override
   public Locale getLanguage()
   {
      return jaxrsResponse.getLanguage();
   }

   @Override
   public int getLength()
   {
      return jaxrsResponse.getLength();
   }

   @Override
   public MediaType getMediaType()
   {
      return jaxrsResponse.getMediaType();
   }

   @Override
   public Map<String, NewCookie> getCookies()
   {
      return jaxrsResponse.getCookies();
   }

   @Override
   public EntityTag getEntityTag()
   {
      return jaxrsResponse.getEntityTag();
   }

   @Override
   public Date getLastModified()
   {
      return jaxrsResponse.getLastModified();
   }

   @Override
   public URI getLocation()
   {
      return jaxrsResponse.getLocation();
   }

   @Override
   public Set<Link> getLinks()
   {
      return jaxrsResponse.getLinks();
   }

   @Override
   public boolean hasLink(String relation)
   {
      return jaxrsResponse.hasLink(relation);
   }

   @Override
   public Link getLink(String relation)
   {
      return jaxrsResponse.getLink(relation);
   }

   @Override
   public Link.Builder getLinkBuilder(String relation)
   {
      return jaxrsResponse.getLinkBuilder(relation);
   }

   @Override
   public boolean hasEntity()
   {
      return jaxrsResponse.hasEntity();
   }

   @Override
   public Object getEntity()
   {
      return jaxrsResponse.getEntity();
   }

   @Override
   public OutputStream getEntityStream()
   {
      try
      {
         return httpResponse.getOutputStream();
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   public void setEntityStream(OutputStream entityStream)
   {
      httpResponse.setOutputStream(entityStream);
   }

   @Override
   public Annotation[] getEntityAnnotations()
   {
      return jaxrsResponse.getAnnotations();
   }

   @Override
   public MultivaluedMap<String, String> getStringHeaders()
   {
      return jaxrsResponse.getStringHeaders();
   }

   @Override
   public String getHeaderString(String name)
   {
      return jaxrsResponse.getHeaderString(name);
   }
}

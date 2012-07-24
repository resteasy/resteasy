package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.specimpl.BuiltResponse;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;

import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.EntityTag;
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
   protected final BuiltResponse jaxrsResposne;

   public ContainerResponseContextImpl(HttpRequest request, HttpResponse httpResponse, BuiltResponse serverResponse)
   {
      this.request = request;
      this.httpResponse = httpResponse;
      this.jaxrsResposne = serverResponse;
   }

   public BuiltResponse getJaxrsResposne()
   {
      return jaxrsResposne;
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
      if (jaxrsResposne.getEntity() == null) return null;
      return jaxrsResposne.getEntity().getClass();
   }

   @Override
   public Type getEntityType()
   {
      return jaxrsResposne.getGenericType();
   }

   @Override
   public void setEntity(Object entity, Annotation[] annotations, MediaType mediaType)
   {
      jaxrsResposne.setEntity(entity);
      jaxrsResposne.setAnnotations(annotations);
      jaxrsResposne.setAnnotations(annotations);
   }

   @Override
   public MultivaluedMap<String, Object> getHeaders()
   {
      return jaxrsResposne.getMetadata();
   }

   @Override
   public Set<String> getAllowedMethods()
   {
     return jaxrsResposne.getAllowedMethods();
   }

   @Override
   public Date getDate()
   {
      return jaxrsResposne.getDate();
   }

   @Override
   public Locale getLanguage()
   {
      return jaxrsResposne.getLanguage();
   }

   @Override
   public int getLength()
   {
      return jaxrsResposne.getLength();
   }

   @Override
   public MediaType getMediaType()
   {
      return jaxrsResposne.getMediaType();
   }

   @Override
   public Map<String, NewCookie> getCookies()
   {
      return jaxrsResposne.getCookies();
   }

   @Override
   public EntityTag getEntityTag()
   {
      return jaxrsResposne.getEntityTag();
   }

   @Override
   public Date getLastModified()
   {
      return jaxrsResposne.getLastModified();
   }

   @Override
   public URI getLocation()
   {
      return jaxrsResposne.getLocation();
   }

   @Override
   public Set<Link> getLinks()
   {
      return jaxrsResposne.getLinks();
   }

   @Override
   public boolean hasLink(String relation)
   {
      return jaxrsResposne.hasLink(relation);
   }

   @Override
   public Link getLink(String relation)
   {
      return jaxrsResposne.getLink(relation);
   }

   @Override
   public Link.Builder getLinkBuilder(String relation)
   {
      return jaxrsResposne.getLinkBuilder(relation);
   }

   @Override
   public boolean hasEntity()
   {
      return jaxrsResposne.hasEntity();
   }

   @Override
   public Object getEntity()
   {
      return jaxrsResposne.getEntity();
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
      return jaxrsResposne.getAnnotations();
   }

   @Override
   public MultivaluedMap<String, String> getStringHeaders()
   {
      return jaxrsResposne.getStringHeaders();
   }

   @Override
   public String getHeaderString(String name)
   {
      return jaxrsResposne.getHeaderString(name);
   }
}

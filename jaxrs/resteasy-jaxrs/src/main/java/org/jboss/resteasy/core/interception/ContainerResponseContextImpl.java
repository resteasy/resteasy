package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.NotImplementedYetException;

import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericType;
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
   protected final ServerResponse serverResponse;

   public ContainerResponseContextImpl(HttpRequest request, HttpResponse httpResponse, ServerResponse serverResponse)
   {
      this.request = request;
      this.httpResponse = httpResponse;
      this.serverResponse = serverResponse;
   }

   public ServerResponse getServerResponse()
   {
      return serverResponse;
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
      if (serverResponse.getEntity() == null) return null;
      return serverResponse.getEntity().getClass();
   }

   @Override
   public Type getEntityType()
   {
      return serverResponse.getGenericType();
   }

   @Override
   public void setEntity(Object entity, Annotation[] annotations, MediaType mediaType)
   {
      serverResponse.setEntity(entity);
      serverResponse.setAnnotations(annotations);
      serverResponse.setAnnotations(annotations);
   }

   @Override
   public MultivaluedMap<String, Object> getHeaders()
   {
      return serverResponse.getMetadata();
   }

   @Override
   public Set<String> getAllowedMethods()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Date getDate()
   {
      return serverResponse.getDate();
   }

   @Override
   public Locale getLanguage()
   {
      return serverResponse.getLanguage();
   }

   @Override
   public int getLength()
   {
      return serverResponse.getLength();
   }

   @Override
   public MediaType getMediaType()
   {
      return serverResponse.getMediaType();
   }

   @Override
   public Map<String, NewCookie> getCookies()
   {
      return serverResponse.getCookies();
   }

   @Override
   public EntityTag getEntityTag()
   {
      return serverResponse.getEntityTag();
   }

   @Override
   public Date getLastModified()
   {
      return serverResponse.getLastModified();
   }

   @Override
   public URI getLocation()
   {
      return serverResponse.getLocation();
   }

   @Override
   public Set<Link> getLinks()
   {
      return serverResponse.getLinks();
   }

   @Override
   public boolean hasLink(String relation)
   {
      return serverResponse.hasLink(relation);
   }

   @Override
   public Link getLink(String relation)
   {
      return serverResponse.getLink(relation);
   }

   @Override
   public Link.Builder getLinkBuilder(String relation)
   {
      return serverResponse.getLinkBuilder(relation);
   }

   @Override
   public boolean hasEntity()
   {
      return serverResponse.hasEntity();
   }

   @Override
   public Object getEntity()
   {
      return serverResponse.getEntity();
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
      throw new NotImplementedYetException();
   }

   @Override
   public Annotation[] getEntityAnnotations()
   {
      return serverResponse.getAnnotations();
   }
}

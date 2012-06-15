package org.jboss.resteasy.specimpl;

import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.spi.NotImplementedYetException;
import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.MessageProcessingException;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * A response object not attached to a client or server invocation.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class BuiltResponse extends Response
{
   protected Object entity;
   protected int status = HttpResponseCodes.SC_OK;
   protected Headers<Object> metadata = new Headers<Object>();
   protected Annotation[] entityAnnotations;
   protected Type genericType;

   public BuiltResponse()
   {
   }

   public BuiltResponse(int status, Headers<Object> metadata, Object entity, Annotation[] entityAnnotations)
   {
      this.entity = entity;
      this.status = status;
      this.metadata = metadata;
      this.entityAnnotations = entityAnnotations;
   }

   @Override
   public StatusType getStatusInfo()
   {
      return Status.fromStatusCode(status);
   }

   @Override
   public Object getEntity()
   {
      return entity;
   }

   @Override
   public int getStatus()
   {
      return status;
   }

   @Override
   public MultivaluedMap<String, Object> getMetadata()
   {
      return metadata;
   }

   public void setEntity(Object entity)
   {
      this.entity = entity;
   }

   public void setStatus(int status)
   {
      this.status = status;
   }

   public void setMetadata(MultivaluedMap<String, Object> metadata)
   {
      this.metadata.clear();
      this.metadata.putAll(metadata);
   }

   public Annotation[] getEntityAnnotations()
   {
      return entityAnnotations;
   }

   public Type getGenericType()
   {
      return genericType;
   }

   public void setGenericType(Type genericType)
   {
      this.genericType = genericType;
   }

   @Override
   public <T> T readEntity(Class<T> entityType) throws MessageProcessingException, IllegalStateException
   {
      throw new NotImplementedYetException();
   }

   @Override
   public <T> T readEntity(GenericType<T> entityType) throws MessageProcessingException, IllegalStateException
   {
      throw new NotImplementedYetException();
   }

   @Override
   public <T> T readEntity(Class<T> entityType, Annotation[] annotations) throws MessageProcessingException, IllegalStateException
   {
      throw new NotImplementedYetException();
   }

   @Override
   public <T> T readEntity(GenericType<T> entityType, Annotation[] annotations) throws MessageProcessingException, IllegalStateException
   {
      throw new NotImplementedYetException();
   }

   @Override
   public boolean hasEntity()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public boolean bufferEntity() throws MessageProcessingException
   {
      throw new NotImplementedYetException();
   }

   @Override
   public void close() throws MessageProcessingException
   {
      throw new NotImplementedYetException();
   }

   @Override
   public String getHeader(String name)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public MediaType getMediaType()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Locale getLanguage()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public int getLength()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Map<String, NewCookie> getCookies()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public EntityTag getEntityTag()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Date getDate()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Date getLastModified()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public URI getLocation()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Set<Link> getLinks()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public boolean hasLink(String relation)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Link getLink(String relation)
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Link.Builder getLinkBuilder(String relation)
   {
      throw new NotImplementedYetException();
   }
}

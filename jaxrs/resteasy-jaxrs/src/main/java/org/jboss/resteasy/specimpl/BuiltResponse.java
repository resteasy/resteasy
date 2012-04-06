package org.jboss.resteasy.specimpl;

import org.jboss.resteasy.core.Headers;
import org.jboss.resteasy.spi.NotImplementedYetException;
import org.jboss.resteasy.util.HttpResponseCodes;

import javax.ws.rs.core.MessageProcessingException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.ResponseHeaders;
import javax.ws.rs.core.TypeLiteral;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

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
   public Map<String, Object> getProperties()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public Status getStatusEnum()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public ResponseHeaders getHeaders()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public <T> T readEntity(Class<T> type) throws MessageProcessingException
   {
      throw new NotImplementedYetException();
   }

   @Override
   public <T> T readEntity(TypeLiteral<T> entityType) throws MessageProcessingException
   {
      throw new NotImplementedYetException();
   }

   @Override
   public boolean hasEntity()
   {
      throw new NotImplementedYetException();
   }

   @Override
   public void bufferEntity() throws MessageProcessingException
   {
      throw new NotImplementedYetException();
   }

   @Override
   public void close() throws MessageProcessingException
   {
      throw new NotImplementedYetException();
   }

   @Override
   public <T> T readEntity(Class<T> type, Annotation[] annotations) throws MessageProcessingException
   {
      throw new NotImplementedYetException();
   }

   @Override
   public <T> T readEntity(TypeLiteral<T> entityType, Annotation[] annotations) throws MessageProcessingException
   {
      throw new NotImplementedYetException();
   }

   @Override
   public boolean isEntityRetrievable()
   {
      throw new NotImplementedYetException();
   }
}

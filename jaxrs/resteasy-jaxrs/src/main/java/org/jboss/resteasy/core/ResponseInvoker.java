package org.jboss.resteasy.core;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyWriter;

import org.jboss.resteasy.specimpl.ResponseImpl;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

@SuppressWarnings("unchecked")
public class ResponseInvoker
{

   private Response jaxrsResponse;
   private Object entity;
   private Type genericType = null;
   private Annotation[] annotations = null;
   private ResteasyProviderFactory providerFactory;
   private Class<? extends Object> type;
   private MediaType contentType;
   private MessageBodyWriter writer;

   public ResponseInvoker(Response jaxrsResponse,
         MediaType contentType, ResteasyProviderFactory providerFactory)
   {
      this.jaxrsResponse = jaxrsResponse;
      this.entity = jaxrsResponse.getEntity();
      this.providerFactory = providerFactory;
      if (entity != null)
      {
         this.contentType = contentType;
         initialize();
      }
   }

   protected void initialize()
   {
      if (entity instanceof GenericEntity)
      {
         GenericEntity ge = (GenericEntity) entity;
         genericType = ge.getType();
         entity = ge.getEntity();
         type = entity.getClass();
      }
      if (jaxrsResponse instanceof ResponseImpl)
      {
         // if we haven't set it in GenericEntity processing...
         if (genericType == null)
            genericType = ((ResponseImpl) jaxrsResponse).getGenericType();

         annotations = ((ResponseImpl) jaxrsResponse).getAnnotations();
      }
      type = entity.getClass();
      writer = getProviderFactory().getMessageBodyWriter(
            type, genericType, annotations, contentType);
   }

   public ResteasyProviderFactory getProviderFactory()
   {
      return this.providerFactory;
   }

   public MessageBodyWriter getWriter()
   {
      return writer;
   }

   public long getResponseSize()
   {
      if( writer == null )
         return -1;
      return writer.getSize(entity, type, genericType, annotations,
            contentType);
   }

   public Class<? extends Object> getType()
   {
      return type;
   }

   public MediaType getContentType()
   {
      return contentType;
   }
   
   public void setContentType(MediaType contentType)
   {
      this.contentType = contentType;
   }

   public void writeTo(HttpResponse response) throws WebApplicationException,
         IOException
   {
      writer.writeTo(entity, type, genericType, annotations,
            contentType, response.getOutputHeaders(), response
                  .getOutputStream());
   }

   public Object getEntity()
   {
      return entity;
   }
}
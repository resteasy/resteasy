package org.jboss.resteasy.client.core;

import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.methods.EntityEnclosingMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class MessageBodyParameterMarshaller implements Marshaller
{
   private Class type;
   private ResteasyProviderFactory factory;
   private MediaType mediaType;
   private Type genericType;
   private Annotation[] annotations;

   public MessageBodyParameterMarshaller(MediaType mediaType, Class type, Type genericType, Annotation[] annotations, ResteasyProviderFactory factory)
   {
      this.type = type;
      this.factory = factory;
      this.mediaType = mediaType;
      this.genericType = genericType;
      this.annotations = annotations;
   }

   public void buildUri(Object object, UriBuilderImpl uri)
   {
   }

   public void setHeaders(Object object, HttpMethodBase httpMethod)
   {
   }

   public void buildRequest(Object object, HttpMethodBase httpMethod)
   {
      final MessageBodyWriter writer = getMessageBodyWriter();
      final Object target = object;
      final HttpClientHeaderWrapper wrapper = new HttpClientHeaderWrapper(httpMethod, factory);
      RequestEntity body = new RequestEntity()
      {
         public boolean isRepeatable()
         {
            return true;
         }

         public void writeRequest(OutputStream outputStream) throws IOException
         {
            wrapper.sync();
            writer.writeTo(target, type, genericType, annotations, mediaType, wrapper, outputStream);
         }

         public long getContentLength()
         {
            return writer.getSize(target, type, genericType, annotations, mediaType);
         }

         public String getContentType()
         {
            return mediaType.toString();
         }
      };
      ((EntityEnclosingMethod) httpMethod).setRequestEntity(body);
   }

   public MessageBodyWriter getMessageBodyWriter()
   {
      MessageBodyWriter writer = factory.getMessageBodyWriter(type, genericType, annotations, mediaType);
      return writer;
   }

   public Class getType()
   {
      return type;
   }

   public MediaType getMediaType()
   {
      return mediaType;
   }
}
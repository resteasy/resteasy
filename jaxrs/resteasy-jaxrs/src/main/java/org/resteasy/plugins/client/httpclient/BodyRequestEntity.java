package org.resteasy.plugins.client.httpclient;

import org.apache.commons.httpclient.methods.RequestEntity;
import org.resteasy.MessageBodyParameterMarshaller;

import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class BodyRequestEntity implements RequestEntity
{

   private MessageBodyParameterMarshaller marshaller;
   private Object object;
   private MultivaluedMap<String, Object> httpHeaders;
   private Type genericType;
   private Annotation[] annotations;

   public BodyRequestEntity(Object object, Type genericType, Annotation[] annotations, MessageBodyParameterMarshaller marshaller, MultivaluedMap<String, Object> httpHeaders)
   {
      this.marshaller = marshaller;
      this.object = object;
      this.httpHeaders = httpHeaders;
      this.genericType = genericType;
      this.annotations = annotations;
   }

   public boolean isRepeatable()
   {
      return true;
   }

   public void writeRequest(OutputStream outputStream) throws IOException
   {
      marshaller.getMessageBodyWriter().writeTo(object, object.getClass(), genericType, annotations, marshaller.getMediaType(), httpHeaders, outputStream);
   }

   public long getContentLength()
   {
      return marshaller.getMessageBodyWriter().getSize(object);
   }

   public String getContentType()
   {
      return marshaller.getMediaType().toString();
   }
}

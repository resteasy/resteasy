package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.spi.interception.MessageBodyReaderContext;
import org.jboss.resteasy.spi.interception.MessageBodyReaderInterceptor;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@SuppressWarnings("unchecked")
public abstract class MessageBodyReaderContextImpl implements MessageBodyReaderContext
{
   protected MessageBodyReaderInterceptor[] interceptors;
   protected MessageBodyReader reader;
   protected Class type;
   protected Type genericType;
   protected Annotation[] annotations;
   protected MediaType mediaType;
   protected MultivaluedMap<String, String> headers;
   protected InputStream inputStream;
   protected int index = 0;


   public MessageBodyReaderContextImpl(MessageBodyReaderInterceptor[] interceptors, MessageBodyReader reader, Class type,
                                       Type genericType, Annotation[] annotations, MediaType mediaType,
                                       MultivaluedMap<String, String> headers, InputStream inputStream)
   {
      this.interceptors = interceptors;
      this.reader = reader;
      this.type = type;
      this.genericType = genericType;
      this.annotations = annotations;
      this.mediaType = mediaType;
      this.headers = headers;
      this.inputStream = inputStream;
   }

   public Class getType()
   {
      return type;
   }

   public void setType(Class type)
   {
      this.type = type;
   }

   public Type getGenericType()
   {
      return genericType;
   }

   public void setGenericType(Type genericType)
   {
      this.genericType = genericType;
   }

   public Annotation[] getAnnotations()
   {
      return annotations;
   }

   public void setAnnotations(Annotation[] annotations)
   {
      this.annotations = annotations;
   }

   public MediaType getMediaType()
   {
      return mediaType;
   }

   public void setMediaType(MediaType mediaType)
   {
      this.mediaType = mediaType;
   }

   public MultivaluedMap<String, String> getHeaders()
   {
      return headers;
   }

   public InputStream getInputStream()
   {
      return inputStream;
   }

   public void setInputStream(InputStream inputStream)
   {
      this.inputStream = inputStream;
   }

   public Object proceed() throws IOException, WebApplicationException
   {
      if (interceptors == null || index >= interceptors.length)
         return reader.readFrom(type, genericType, annotations, mediaType, headers, inputStream);
      try
      {
         return interceptors[index++].read(this);
      }
      finally
      {
         index--;
      }
   }
}

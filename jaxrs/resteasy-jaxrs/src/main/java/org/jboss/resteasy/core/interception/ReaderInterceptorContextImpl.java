package org.jboss.resteasy.core.interception;

import org.jboss.resteasy.spi.interception.MessageBodyReaderInterceptor;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class ReaderInterceptorContextImpl implements ReaderInterceptorContext
{
   protected ReaderInterceptor[] interceptors;
   protected MessageBodyReader reader;
   protected Class type;
   protected Type genericType;
   protected Annotation[] annotations;
   protected MediaType mediaType;
   protected MultivaluedMap<String, String> headers;
   protected InputStream inputStream;
   protected int index = 0;
   protected Map<String, Object> properties;

   public ReaderInterceptorContextImpl(ReaderInterceptor[] interceptors, MessageBodyReader reader, Class type,
                                       Type genericType, Annotation[] annotations, MediaType mediaType,
                                       MultivaluedMap<String, String> headers, InputStream inputStream,
                                       Map<String, Object> properties)
   {
      this.interceptors = interceptors;
      this.reader = reader;
      this.type = type;
      this.genericType = genericType;
      this.annotations = annotations;
      this.mediaType = mediaType;
      this.headers = headers;
      this.inputStream = inputStream;
      this.properties = properties;
   }

   @Override
   public Object proceed() throws IOException
   {
      if (interceptors == null || index >= interceptors.length)
         return reader.readFrom(type, genericType, annotations, mediaType, headers, inputStream);
      try
      {
         return interceptors[index++].aroundReadFrom(this);
      }
      finally
      {
         index--;
      }
   }

   @Override
   public InputStream getInputStream()
   {
      return inputStream;
   }

   @Override
   public void setInputStream(InputStream is)
   {
      this.inputStream = is;
   }

   @Override
   public MultivaluedMap<String, String> getHeaders()
   {
      return headers;
   }

   @Override
   public Map<String, Object> getProperties()
   {
      return properties;
   }

   @Override
   public Annotation[] getAnnotations()
   {
      return annotations;
   }

   @Override
   public void setAnnotations(Annotation[] annotations)
   {
      this.annotations = annotations;
   }

   @Override
   public Class getType()
   {
      return type;
   }

   @Override
   public void setType(Class type)
   {
      this.type = type;
   }

   @Override
   public Type getGenericType()
   {
      return genericType;
   }

   @Override
   public void setGenericType(Type genericType)
   {
      this.genericType = genericType;
   }

   @Override
   public MediaType getMediaType()
   {
      return mediaType;
   }

   @Override
   public void setMediaType(MediaType mediaType)
   {
      this.mediaType = mediaType;
   }
}

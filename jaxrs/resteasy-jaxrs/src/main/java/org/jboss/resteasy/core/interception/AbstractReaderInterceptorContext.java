package org.jboss.resteasy.core.interception;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public abstract class AbstractReaderInterceptorContext implements ReaderInterceptorContext
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

   public AbstractReaderInterceptorContext(MediaType mediaType, MessageBodyReader reader, Annotation[] annotations, ReaderInterceptor[] interceptors, MultivaluedMap<String, String> headers, Type genericType, Class type, InputStream inputStream)
   {
      this.mediaType = mediaType;
      this.reader = reader;
      this.annotations = annotations;
      this.interceptors = interceptors;
      this.headers = headers;
      this.genericType = genericType;
      this.type = type;
      this.inputStream = inputStream;
   }

   @Override
   public Object proceed() throws IOException
   {
      if (interceptors == null || index >= interceptors.length)
         return reader.readFrom(type, genericType, annotations, mediaType, headers, inputStream);
      return interceptors[index++].aroundReadFrom(this);
      // index--;  we used to pop the index, but the TCK does not like this
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
   public Annotation[] getAnnotations()
   {
      return annotations;
   }

   @Override
   public void setAnnotations(Annotation[] annotations)
   {
      if (annotations == null) throw new NullPointerException("annotations param was null");
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

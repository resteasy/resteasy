package org.jboss.resteasy.plugins.providers;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;


/**
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Created Mar 23, 2012
 */
@Provider
@Produces("application/x-java-serialized-object")
@Consumes("application/x-java-serialized-object")
public class SerializableProvider implements MessageBodyReader<Serializable>, MessageBodyWriter<Serializable>
{
   public static final MediaType APPLICATION_SERIALIZABLE_TYPE = new MediaType("application", "x-java-serialized-object");
   public static final String APPLICATION_SERIALIZABLE = APPLICATION_SERIALIZABLE_TYPE.toString();
   
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return Serializable.class.isAssignableFrom(type) && "application/x-java-serialized-object".equals(mediaType.toString());
   }

   public long getSize(Serializable t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   public void writeTo(Serializable t, Class<?> type, Type genericType,
         Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
         throws IOException, WebApplicationException
   {
      BufferedOutputStream bos = new BufferedOutputStream(entityStream);
      ObjectOutputStream oos = new ObjectOutputStream(bos);
      oos.writeObject(t);
      oos.close();
   }

   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return Serializable.class.isAssignableFrom(type) && "application/x-java-serialized-object".equals(mediaType.toString());
   }

   public Serializable readFrom(Class<Serializable> type, Type genericType,
         Annotation[] annotations, MediaType mediaType,
         MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
         throws IOException, WebApplicationException
   {
      BufferedInputStream bis = new BufferedInputStream(entityStream);
      ObjectInputStream ois = new ObjectInputStream(bis);
      try
      {
         return Serializable.class.cast(ois.readObject());
      }
      catch (ClassNotFoundException e)
      {
         throw new WebApplicationException(e);
      }
   }
}
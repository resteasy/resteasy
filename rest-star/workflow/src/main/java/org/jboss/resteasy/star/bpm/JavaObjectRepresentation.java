package org.jboss.resteasy.star.bpm;

import org.jboss.resteasy.specimpl.MultivaluedMapImpl;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GenericType;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JavaObjectRepresentation extends Representation
{
   private static final Annotation[] emptyAnnotations = {};
   private static final MultivaluedMap<String, String> emptyHeaders = new MultivaluedMapImpl<String, String>();

   public JavaObjectRepresentation()
   {
   }

   public JavaObjectRepresentation(String mediaType, byte[] representation)
   {
      super(mediaType, representation);
      readJavaObject(representation);

   }

   protected void readJavaObject(byte[] representation)
   {
      try
      {
         ByteArrayInputStream bais = new ByteArrayInputStream(representation);
         ObjectInputStream ois = new ObjectInputStream(bais);
         javaObject = ois.readObject();
         javaType = javaObject.getClass();
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      catch (ClassNotFoundException e)
      {
         throw new RuntimeException(e);
      }
   }

   private Class javaType;
   private Type genericType;
   private Object javaObject;

   public void setJavaType(Class type)
   {
      this.javaType = type;
   }

   public void setJavaType(GenericType type)
   {
      this.javaType = type.getType();
      this.genericType = type.getType();
   }

   public void readFrom(Representation representation, ResteasyProviderFactory factory)
   {
      MediaType from =representation.getMediaType();
      MessageBodyReader reader = factory.getMessageBodyReader(javaType, genericType, emptyAnnotations, from);
      if (reader == null)
      {
         throw new RuntimeException("Unable to find reader for: " + representation.getMediaType());
      }
      ByteArrayInputStream bais = new ByteArrayInputStream(representation.getRepresentation());
      try
      {
         javaObject = reader.readFrom(javaType, genericType, emptyAnnotations, from, emptyHeaders, bais);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   @Override
   public Representation transformTo(MediaType to, ResteasyProviderFactory factory)
   {
      MessageBodyWriter writer = factory.getMessageBodyWriter(javaType, genericType, emptyAnnotations, to);
      if (writer == null)
      {
         return null;
      }
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try
      {
         writer.writeTo(javaObject, javaType, genericType, emptyAnnotations, to, new MultivaluedMapImpl<String, Object>(), baos);
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
      return new Representation(to, baos.toByteArray());

   }
}

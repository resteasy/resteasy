package org.jboss.resteasy.star.messaging;

import org.hornetq.api.core.client.ClientMessage;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GenericType;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Hornetq
{

   /**
    * Sets the message body to a serialized
    * byte array of the object.  HTTP consumers will have to provide an Accept header to marshal the object
    *
    * @param message
    * @param object
    */
   public static void setEntity(ClientMessage message, Serializable object)
   {
      setEntity(message, object, null);
   }

   /**
    * Sets a message property to be the Content-Type passed in.  Sets the message body to a serialized
    * byte array of the object.
    *
    * @param message
    * @param object
    * @param contentType HTTP Content-Type header
    */
   public static void setEntity(ClientMessage message, Serializable object, String contentType)
   {
      if (contentType != null) message.putStringProperty(HttpHeaderProperty.CONTENT_TYPE, contentType);
      byte[] data = new byte[0];
      try
      {
         ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);

         ObjectOutputStream oos = new ObjectOutputStream(baos);

         oos.writeObject(object);

         oos.flush();

         data = baos.toByteArray();
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }

      message.getBodyBuffer().writeInt(data.length);
      message.getBodyBuffer().writeBytes(data);

   }

   public static void setHttpHeader(ClientMessage message, String name, String value)
   {
      message.putStringProperty(HttpHeaderProperty.toPropertyName(name), value);
   }


   /**
    * Get an HTTP header value from a JMS Message
    *
    * @param message
    * @param name
    * @return
    */
   public static String getHttpHeader(ClientMessage message, String name)
   {
      return message.getStringProperty(HttpHeaderProperty.toPropertyName(name));
   }

   /**
    * Extract an object using a built-in RESTEasy JAX-RS MessageBodyReader
    *
    * @param message
    * @param type
    * @param <T>
    * @return
    */
   public static <T> T getEntity(ClientMessage message, Class<T> type)
   {
      return getEntity(message, type, null, ResteasyProviderFactory.getInstance());
   }

   /**
    * Extract an object using a built-in RESTEasy JAX-RS MessageBodyReader
    *
    * @param message
    * @param type
    * @param factory
    * @param <T>
    * @return
    */
   public static <T> T getEntity(ClientMessage message, Class<T> type, ResteasyProviderFactory factory)
   {
      return getEntity(message, type, null, factory);
   }

   /**
    * Extract an object using a built-in RESTEasy JAX-RS MessageBodyReader
    *
    * @param message
    * @param type
    * @param factory
    * @param <T>
    * @return
    * @throws UnknownMediaType
    * @throws UnmarshalException
    */
   public static <T> T getEntity(ClientMessage message, GenericType<T> type, ResteasyProviderFactory factory) throws UnknownMediaType, UnmarshalException
   {
      return getEntity(message, type.getType(), type.getGenericType(), factory);
   }

   public static <T> T getEntity(ClientMessage msg, Class<T> type, Type genericType, ResteasyProviderFactory factory)
   {
      int size = msg.getBodyBuffer().readInt();
      if (size <= 0) return null;

      byte[] body = new byte[size];
      msg.getBodyBuffer().readBytes(body);


      String contentType = msg.getStringProperty(HttpHeaderProperty.CONTENT_TYPE);
      if (contentType == null)
      {
         throw new UnknownMediaType("Message did not have a Content-Type header cannot extract entity");
      }
      MediaType ct = MediaType.valueOf(contentType);
      MessageBodyReader<T> reader = factory.getMessageBodyReader(type, genericType, null, ct);
      if (reader == null)
      {
         throw new UnmarshalException("Unable to find a JAX-RS reader for type " + type.getName() + " and media type " + contentType);
      }
      try
      {
         return reader.readFrom(type, genericType, null, ct, null, new ByteArrayInputStream(body));
      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }
}

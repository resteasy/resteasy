package org.hornetq.rest;

import org.hornetq.api.core.client.ClientMessage;
import org.hornetq.jms.client.HornetQMessage;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.GenericType;

import javax.jms.JMSException;
import javax.jms.Message;
import java.lang.reflect.Type;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Jms
{
   /**
    * Set a JMS Message property to the value of an HTTP header
    *
    * @param message
    * @param name
    * @param value
    */
   public static void setHttpHeader(Message message, String name, String value)
   {
      try
      {
         message.setStringProperty(HttpHeaderProperty.toPropertyName(name), value);
      }
      catch (JMSException e)
      {
         throw new RuntimeException(e);
      }
   }

   /**
    * Get an HTTP header value from a JMS Message
    *
    * @param message
    * @param name
    * @return
    */
   public static String getHttpHeader(Message message, String name)
   {
      try
      {
         return message.getStringProperty(HttpHeaderProperty.toPropertyName(name));
      }
      catch (JMSException e)
      {
         throw new RuntimeException(e);
      }
   }

   /**
    * Extract an object using a built-in RESTEasy JAX-RS MessageBodyReader
    *
    * @param message
    * @param type
    * @param <T>
    * @return
    */
   public static <T> T getEntity(Message message, Class<T> type)
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
   public static <T> T getEntity(Message message, Class<T> type, ResteasyProviderFactory factory)
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
   public static <T> T getEntity(Message message, GenericType<T> type, ResteasyProviderFactory factory) throws UnknownMediaType, UnmarshalException
   {
      return getEntity(message, type.getType(), type.getGenericType(), factory);
   }

   public static boolean isHttpMessage(Message message)
   {
      ClientMessage msg = ((HornetQMessage) message).getCoreMessage();
      return Hornetq.isHttpMessage(msg);
   }

   /**
    * Extract an object using a built-in RESTEasy JAX-RS MessageBodyReader
    *
    * @param message
    * @param type
    * @param genericType
    * @param factory
    * @param <T>
    * @return
    * @throws UnknownMediaType
    * @throws UnmarshalException
    */
   public static <T> T getEntity(Message message, Class<T> type, Type genericType, ResteasyProviderFactory factory) throws UnknownMediaType, UnmarshalException
   {
      ClientMessage msg = ((HornetQMessage) message).getCoreMessage();
      if (!Hornetq.isHttpMessage(msg))
      {
         throw new UnmarshalException("JMS Message was not posted from HTTP engine");
      }
      return Hornetq.getEntity(msg, type, genericType, factory);
   }

}

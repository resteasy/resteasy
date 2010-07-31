package org.hornetq.rest.util;

import org.hornetq.api.core.SimpleString;
import org.hornetq.api.core.client.ClientMessage;
import org.jboss.resteasy.client.ClientRequest;
import org.hornetq.rest.HttpHeaderProperty;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class HttpMessageHelper
{
   public static final String POSTED_AS_HTTP_MESSAGE = "postedAsHttpMessage";

   public static boolean isTransferableHttpHeader(String key)
   {
      String lowerKey = key.toLowerCase();
      return lowerKey.toLowerCase().startsWith("content") || lowerKey.toLowerCase().equals("link");
   }

   public static void buildMessage(ClientMessage message, Response.ResponseBuilder builder)
   {
      for (SimpleString key : message.getPropertyNames())
      {
         String k = key.toString();
         String headerName = HttpHeaderProperty.fromPropertyName(k);
         if (headerName == null) continue;
         builder.header(headerName, message.getStringProperty(k));
      }
      int size = message.getBodyBuffer().readInt();
      if (size > 0)
      {
         byte[] body = new byte[size];
         message.getBodyBuffer().readBytes(body);
         Boolean aBoolean = message.getBooleanProperty(POSTED_AS_HTTP_MESSAGE);
         if (aBoolean != null && aBoolean.booleanValue())
         {
            builder.entity(body);
         }
         else
         {
            ByteArrayInputStream bais = new ByteArrayInputStream(body);
            Object obj = null;
            try
            {
               ObjectInputStream ois = new ObjectInputStream(bais);
               obj = ois.readObject();
            }
            catch (Exception e)
            {
               throw new RuntimeException(e);
            }
            builder.entity(obj);
         }
      }
   }

   public static void buildMessage(ClientMessage message, ClientRequest request, String contentType)
   {
      for (SimpleString key : message.getPropertyNames())
      {
         String k = key.toString();
         String headerName = HttpHeaderProperty.fromPropertyName(k);
         if (headerName == null) continue;
         String value = message.getStringProperty(k);
         request.header(headerName, value);
         // override default content type if it is set as a message property
         if (headerName.equalsIgnoreCase("content-type"))
         {
            contentType = value;
         }
      }
      int size = message.getBodyBuffer().readInt();
      if (size > 0)
      {
         byte[] body = new byte[size];
         message.getBodyBuffer().readBytes(body);
         Boolean aBoolean = message.getBooleanProperty(POSTED_AS_HTTP_MESSAGE);
         if (aBoolean != null && aBoolean.booleanValue())
         {
            //System.out.println("Building Message from HTTP message");
            request.body(contentType, body);
         }
         else
         {
            // assume posted as a JMS or HornetQ object message
            ByteArrayInputStream bais = new ByteArrayInputStream(body);
            Object obj = null;
            try
            {
               ObjectInputStream ois = new ObjectInputStream(bais);
               obj = ois.readObject();
               //System.out.println("**** Building Message from object: " + obj.toString());
               request.body(contentType, obj);
            }
            catch (Exception e)
            {
               throw new RuntimeException(e);
            }
         }
      }
   }

   public static void writeHttpMessage(HttpHeaders headers, byte[] body, ClientMessage message) throws Exception
   {

      MultivaluedMap<String, String> hdrs = headers.getRequestHeaders();
      for (String key : hdrs.keySet())
      {
         if (isTransferableHttpHeader(key))
         {
            List<String> vals = hdrs.get(key);
            String value = concatenateHeaderValue(vals);
            message.putStringProperty(HttpHeaderProperty.toPropertyName(key), value);
         }
      }
      message.putBooleanProperty(POSTED_AS_HTTP_MESSAGE, true);
      message.getBodyBuffer().writeInt(body.length);
      message.getBodyBuffer().writeBytes(body);
   }

   public static String concatenateHeaderValue(List<String> vals)
   {
      if (vals == null) return "";
      StringBuffer val = null;
      for (String v : vals)
      {
         if (val == null)
         {
            val = new StringBuffer(v);
         }
         else
         {
            val.append(",").append(v);
         }
      }
      String value = "";
      if (val != null) value = val.toString();
      return value;
   }

}

package org.jboss.resteasy.plugins.providers.jsonp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.json.JsonNumber;
import javax.json.JsonReader;
import javax.json.JsonString;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;

import org.jboss.resteasy.core.messagebody.AsyncBufferedMessageBodyWriter;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
/**
 * @author <a href="mailto:ema@redhat.com">Jim Ma</a>
 */
@Consumes({"application/json", "application/*+json", "text/json"})
@Produces({"application/json", "application/*+json", "text/json"})
public class JsonValueProvider extends AbstractJsonpProvider implements MessageBodyReader<JsonValue>, AsyncBufferedMessageBodyWriter<JsonValue>
{   @Override
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return JsonNumber.class.isAssignableFrom(type) || JsonString.class.isAssignableFrom(type);
   }

   @Override
   public JsonValue readFrom(Class<JsonValue> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException
   {
      LogMessages.LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());
      JsonReader reader = findReader(mediaType, entityStream);
      try
      {
         return reader.readValue();
      }
      finally
      {
         reader.close();
      }
   }

   @Override
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return JsonNumber.class.isAssignableFrom(type) || JsonString.class.isAssignableFrom(type);
   }

   @Override
   public long getSize(JsonValue jsonStructure, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   @Override
   public void writeTo(JsonValue jsonValue, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
   {
      LogMessages.LOGGER.debugf("Provider : %s,  Method : writeTo", getClass().getName());
      JsonWriter writer = findWriter(mediaType, entityStream);
      try
      {
         writer.write(jsonValue);
      }
      finally
      {
         writer.close();
      }
   }

}

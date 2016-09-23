package org.jboss.resteasy.plugins.providers.jsonp;

import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonWriter;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import org.jboss.logging.Logger.Level;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Consumes({"application/*+json", "text/json"})
@Produces({"application/*+json", "text/json"})
public class JsonStructureProvider extends AbstractJsonpProvider implements MessageBodyReader<JsonStructure>, MessageBodyWriter<JsonStructure>
{
   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Call of provider : org.jboss.resteasy.plugins.providers.jsonp.JsonStructureProvider , method call : isReadable .")
   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return JsonStructure.class.isAssignableFrom(type);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Call of provider : org.jboss.resteasy.plugins.providers.jsonp.JsonStructureProvider , method call : readFrom .")
   public JsonStructure readFrom(Class<JsonStructure> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException
   {
      JsonReader reader = findReader(mediaType, entityStream);
      try
      {
         return reader.read();
      }
      finally
      {
         reader.close();
      }
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Call of provider : org.jboss.resteasy.plugins.providers.jsonp.JsonStructureProvider , method call : isWriteable .")
   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return JsonStructure.class.isAssignableFrom(type);
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Call of provider : org.jboss.resteasy.plugins.providers.jsonp.JsonStructureProvider , method call : getSize .")
   public long getSize(JsonStructure jsonStructure, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return -1;
   }

   @Override
   @LogMessage(level = Level.DEBUG)
   @Message(value = "Call of provider : org.jboss.resteasy.plugins.providers.jsonp.JsonStructureProvider , method call : writeTo .")
   public void writeTo(JsonStructure jsonStructure, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
   {
      JsonWriter writer = findWriter(mediaType, entityStream);
      try
      {
         writer.write(jsonStructure);
      }
      finally
      {
         writer.close();
      }
   }
}

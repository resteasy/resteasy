package org.jboss.resteasy.plugins.providers.jaxb.json;

import org.jboss.resteasy.plugins.providers.jaxb.JAXBContextFinder;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBMarshalException;
import org.jboss.resteasy.plugins.providers.jaxb.JAXBUnmarshalException;
import org.jboss.resteasy.plugins.providers.jaxb.MapProvider;
import org.jboss.resteasy.plugins.providers.jaxb.json.i18n.Messages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.util.Types;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
@Produces({"application/json", "application/*+json"})
@Consumes({"application/json", "application/*+json"})
public class JsonMapProvider extends MapProvider
{

   @SuppressWarnings("unchecked")
   public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException, WebApplicationException
   {
      LogMessages.LOGGER.debugf("Provider : %s,  Method : readFrom", getClass().getName());
      Class baseType = Types.getMapValueType(genericType);
      Reader reader = null;
      String charset = mediaType.getParameters().get("charset");
      if (charset != null)
      {
         reader = new BufferedReader(new InputStreamReader(entityStream, charset));
      }
      else
      {
         reader = new BufferedReader(new InputStreamReader(entityStream));
      }


      char c = JsonParsing.eatWhitspace(reader, false);
      if (c != '{') throw new JAXBUnmarshalException(Messages.MESSAGES.expectingJsonArray());
      c = JsonParsing.eatWhitspace(reader, true);
      HashMap map = new HashMap();
      if (c != '}')
      {
         MessageBodyReader messageReader = providers.getMessageBodyReader(baseType, null, annotations, mediaType);
         LogMessages.LOGGER.debugf("MessageBodyReader: %s", messageReader.getClass().getName());

         do
         {
            String key = JsonParsing.getJsonString(reader);
            c = JsonParsing.eatWhitspace(reader, false);

            if (c != ':')
            {
               throw new JAXBUnmarshalException(Messages.MESSAGES.expectingColonMap());
            }

            c = JsonParsing.eatWhitspace(reader, true);

            String str = JsonParsing.extractJsonMapString(reader);
            ByteArrayInputStream stream = new ByteArrayInputStream(str.getBytes());
            Object obj = messageReader.readFrom(baseType, null, annotations, mediaType, httpHeaders, stream);
            map.put(key, obj);

            c = JsonParsing.eatWhitspace(reader, false);

            if (c == '}') break;

            if (c != ',')
            {
               throw new JAXBUnmarshalException(Messages.MESSAGES.expectingCommaJsonArray());
            }
            c = JsonParsing.eatWhitspace(reader, true);
         } while (c != -1);
      }
      return map;
   }

   public static String getCharset(MediaType mediaType)
   {
      if (mediaType != null)
      {
         String charset = mediaType.getParameters().get("charset");
         if (charset != null) return charset;
      }
      return StandardCharsets.UTF_8.name();
   }

   @SuppressWarnings("unchecked")
   public void writeTo(Object target, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException
   {
      LogMessages.LOGGER.debugf("Provider : %s,  Method : writeTo", getClass().getName());
      JAXBContextFinder finder = getFinder(mediaType);
      if (finder == null)
      {
         throw new JAXBMarshalException(Messages.MESSAGES.unableToFindJAXBContext(mediaType));
      }
      Class valueType = Types.getMapValueType(genericType);
      OutputStreamWriter writer = new OutputStreamWriter(entityStream, getCharset(mediaType));
      MessageBodyWriter messageWriter = providers.getMessageBodyWriter(valueType, null, annotations, mediaType);
      LogMessages.LOGGER.debugf("MessageBodyWriter: %s", messageWriter.getClass().getName());

      writer.write('{');
      Map<Object, Object> targetMap = (Map) target;
      Iterator<Map.Entry<Object, Object>> it = targetMap.entrySet().iterator();
      while (it.hasNext())
      {
         Map.Entry mapEntry = it.next();
         writer.write('"');
         writer.write(mapEntry.getKey().toString());
         writer.write('"');
         writer.write(':');
         writer.flush();
         messageWriter.writeTo(mapEntry.getValue(), valueType, null, annotations, mediaType, httpHeaders, entityStream);
         if (it.hasNext())
         {
            writer.write(',');
            writer.flush();
         }

      }
      writer.write('}');
      writer.flush();
   }
}

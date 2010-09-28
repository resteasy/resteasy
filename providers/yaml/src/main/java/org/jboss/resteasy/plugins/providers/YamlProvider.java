package org.jboss.resteasy.plugins.providers;

import org.ho.yaml.Yaml;
import org.ho.yaml.exception.YamlException;
import org.jboss.resteasy.logging.Logger;
import org.jboss.resteasy.spi.ReaderException;
import org.jboss.resteasy.spi.WriterException;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provider for YAML &lt;-> Object marshalling. Uses the following mime
 * types:<pre><code>
 *   text/yaml
 *   text/x-yaml
 *   application/x-yaml</code></pre>
 *
 * @author Martin Algesten
 */
@Provider
@Consumes({"text/yaml", "text/x-yaml", "application/x-yaml"})
@Produces({"text/yaml", "text/x-yaml", "application/x-yaml"})
public class YamlProvider extends AbstractEntityProvider<Object>
{


   final static Logger logger = Logger.getLogger(YamlProvider.class);

   // MessageBodyReader

   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return true;
   }

   public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                          MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException,
           WebApplicationException
   {

      try
      {

         return Yaml.load(entityStream);

      }
      catch (YamlException ye)
      {
         logger.debug("Failed to decode Yaml: {}", ye.getMessage());
         throw new ReaderException("Failed to decode Yaml", ye);
      }
      catch (Exception e)
      {
         logger.debug("Failed to decode Yaml: {}", e.getMessage());
         throw new ReaderException("Failed to decode Yaml", e);
      }


   }

   // MessageBodyWriter

   protected boolean isValidType(Class type)
   {
      if (List.class.isAssignableFrom(type)
              || Set.class.isAssignableFrom(type)
              || Map.class.isAssignableFrom(type)
              || type.isArray())
      {
         return true;
      }
      if (StreamingOutput.class.isAssignableFrom(type)) return false;
      String className = type.getName();
      if (className.startsWith("java.")) return false;
      if (className.startsWith("javax.")) return false;
      if (type.isPrimitive()) return false;

      return true;
   }


   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType)
   {
      return isValidType(type);
   }

   public void writeTo(Object t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                       MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException,
           WebApplicationException
   {

      try
      {

         Yaml.dump(t, entityStream);

      }
      catch (Exception e)
      {

         logger.debug("Failed to encode yaml for object: {}", t.toString());
         throw new WriterException(e);

      }

   }

}

package org.jboss.resteasy.plugins.providers;

import org.ho.yaml.Yaml;
import org.ho.yaml.exception.YamlException;
import org.jboss.resteasy.util.HttpResponseCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

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


   final static Logger logger = LoggerFactory.getLogger(YamlProvider.class);

   // MessageBodyReader

   public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations)
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
         throw new WebApplicationException(ye, HttpResponseCodes.SC_BAD_REQUEST);
      }
      catch (Exception e)
      {
         logger.debug("Failed to decode Yaml: {}", e.getMessage());
         throw new WebApplicationException(e, HttpResponseCodes.SC_BAD_REQUEST);
      }


   }

   // MessageBodyWriter

   public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations)
   {

      return true;

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
         throw new WebApplicationException(e, HttpResponseCodes.SC_INTERNAL_SERVER_ERROR);

      }

   }

}

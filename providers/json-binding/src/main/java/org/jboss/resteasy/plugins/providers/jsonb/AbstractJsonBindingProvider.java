package org.jboss.resteasy.plugins.providers.jsonb;

import org.eclipse.yasson.JsonBindingProvider;
import org.eclipse.yasson.internal.JsonBindingBuilder;
import org.glassfish.json.JsonProviderImpl;

import javax.json.bind.Jsonb;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import java.nio.charset.Charset;


/**
 * Created by rsearls
 */
public class AbstractJsonBindingProvider extends JsonBindingProvider {

   private static final String JSON = "json";
   private static final String PLUS_JSON = "+json";

   @Context
   jakarta.ws.rs.ext.Providers providers;
   private volatile Jsonb jsonbObj;

   protected Jsonb getJsonb(Class<?> type) {
      ContextResolver<Jsonb> contextResolver = providers.getContextResolver(Jsonb.class, MediaType.APPLICATION_JSON_TYPE);
      if (contextResolver != null)
      {
         return contextResolver.getContext(type);
      }
      else
      {
         Jsonb currentJsonbObj = jsonbObj;
         if (currentJsonbObj == null)
         {
            synchronized (this)
            {
               currentJsonbObj = jsonbObj;
               if (currentJsonbObj == null) {
                  JsonProviderImpl jProviderImpl = new JsonProviderImpl();
                  JsonBindingBuilder jbBuilder = new JsonBindingBuilder();
                  currentJsonbObj = jbBuilder.withProvider(jProviderImpl).build();
                  this.jsonbObj = currentJsonbObj;
               }
            }
         }
         return currentJsonbObj;
      }
   }

   public static Charset getCharset(final MediaType mediaType) {
      return Charset.forName("utf-8");
   }

   public static boolean isSupportedMediaType(final MediaType mediaType) {
      return mediaType.getSubtype().equals(JSON) || mediaType.getSubtype().endsWith(PLUS_JSON);
   }
}

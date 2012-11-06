package org.jboss.resteasy.skeleton.key.keystone.model;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonRootName;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.ws.rs.core.Configurable;
import javax.ws.rs.ext.ContextResolver;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class Mappers
{
   public static final ObjectMapper DEFAULT_MAPPER = new ObjectMapper();
   public static final ObjectMapper WRAPPED_MAPPER = new ObjectMapper();

   static
   {
      DEFAULT_MAPPER.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
      //DEFAULT_MAPPER.enable(SerializationConfig.Feature.INDENT_OUTPUT);
      DEFAULT_MAPPER.enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);


      WRAPPED_MAPPER.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
      //WRAPPED_MAPPER.enable(SerializationConfig.Feature.INDENT_OUTPUT);
      WRAPPED_MAPPER.enable(SerializationConfig.Feature.WRAP_ROOT_VALUE);
      WRAPPED_MAPPER.enable(DeserializationConfig.Feature.UNWRAP_ROOT_VALUE);
      WRAPPED_MAPPER.enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

   }

   public static void registerContextResolver(Configurable configurable)
   {
      configurable.register(new ContextResolver<ObjectMapper>()
      {

         public ObjectMapper getContext(Class<?> type)
         {
            return type.getAnnotation(JsonRootName.class) == null ? Mappers.DEFAULT_MAPPER : Mappers.WRAPPED_MAPPER;
         }

      });

   }
}

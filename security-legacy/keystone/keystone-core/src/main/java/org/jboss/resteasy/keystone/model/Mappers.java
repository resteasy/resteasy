package org.jboss.resteasy.keystone.model;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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
      DEFAULT_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      //DEFAULT_MAPPER.enable(SerializationConfig.Feature.INDENT_OUTPUT);
      DEFAULT_MAPPER.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);


      WRAPPED_MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      //WRAPPED_MAPPER.enable(SerializationConfig.Feature.INDENT_OUTPUT);
      WRAPPED_MAPPER.enable(SerializationFeature.WRAP_ROOT_VALUE);
      WRAPPED_MAPPER.enable(DeserializationFeature.UNWRAP_ROOT_VALUE);
      WRAPPED_MAPPER.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

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

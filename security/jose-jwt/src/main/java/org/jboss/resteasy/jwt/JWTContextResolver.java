package org.jboss.resteasy.jwt;

import jakarta.ws.rs.ext.ContextResolver;

import org.jboss.resteasy.plugins.providers.jackson.WhiteListPolymorphicTypeValidatorBuilder;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Any class that extends JsonWebToken will use NON_DEFAULT inclusion
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class JWTContextResolver implements ContextResolver<ObjectMapper>
{
   protected ObjectMapper mapper = new ObjectMapper();

   public JWTContextResolver()
   {
      mapper.setSerializationInclusion(JsonInclude.Include.NON_DEFAULT);
      mapper.setPolymorphicTypeValidator(new WhiteListPolymorphicTypeValidatorBuilder().build());
   }

   public JWTContextResolver(final boolean indent)
   {
      this();
      if (indent)
      {
         mapper.enable(SerializationFeature.INDENT_OUTPUT);
      }
   }


   @Override
   public ObjectMapper getContext(Class<?> type)
   {
      if (JsonWebToken.class.isAssignableFrom(type)) return mapper;
      return null;
   }
}

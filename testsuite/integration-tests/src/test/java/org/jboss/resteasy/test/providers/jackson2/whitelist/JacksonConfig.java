package org.jboss.resteasy.test.providers.jackson2.whitelist;

import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.plugins.providers.jackson.WhiteListPolymorphicTypeValidatorBuilder;
import org.jboss.resteasy.test.providers.jackson2.whitelist.model.land.Automobile2;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

@Provider
@Produces(MediaType.APPLICATION_JSON)
public class JacksonConfig implements ContextResolver<ObjectMapper>
{
   private final ObjectMapper objectMapper;

   public JacksonConfig()
   {
      objectMapper = new ObjectMapper();
      objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
      if (objectMapper.getPolymorphicTypeValidator() != null && !(objectMapper.getPolymorphicTypeValidator() instanceof LaissezFaireSubTypeValidator)) {
         throw new RuntimeException(
               "Unexpected default allow-everything implementation of Polymorphic Type Validator. If Jackson 2 actually changed this, RESTEasy has to be fixed accordingly, see changes for RESTEASY-2469.");
      }
      objectMapper.setPolymorphicTypeValidator(
            new WhiteListPolymorphicTypeValidatorBuilder().allowIfBaseType(Automobile2.class).allowIfSubType(Automobile2.class).build());
   }

   @Override
   public ObjectMapper getContext(Class<?> type)
   {
      return objectMapper;
   }
}

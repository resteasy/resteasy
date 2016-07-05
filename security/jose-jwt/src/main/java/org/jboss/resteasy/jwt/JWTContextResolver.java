package org.jboss.resteasy.jwt;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import javax.ws.rs.ext.ContextResolver;

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
      mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
   }

   public JWTContextResolver(boolean indent)
   {
      mapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_DEFAULT);
      if (indent)
      {
         mapper.enable(SerializationConfig.Feature.INDENT_OUTPUT);
      }
   }


   @Override
   public ObjectMapper getContext(Class<?> type)
   {
      if (JsonWebToken.class.isAssignableFrom(type)) return mapper;
      return null;
   }
}

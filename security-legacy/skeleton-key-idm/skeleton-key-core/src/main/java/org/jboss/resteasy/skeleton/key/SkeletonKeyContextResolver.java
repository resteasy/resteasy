package org.jboss.resteasy.skeleton.key;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Any class with package org.jboss.resteasy.skeleton.key will use NON_DEFAULT inclusion
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Provider
public class SkeletonKeyContextResolver implements ContextResolver<ObjectMapper>
{
   protected ObjectMapper mapper = new ObjectMapper();

   public SkeletonKeyContextResolver()
   {
      mapper.setSerializationInclusion(Include.NON_DEFAULT);
   }

   public SkeletonKeyContextResolver(final boolean indent)
   {
      mapper.setSerializationInclusion(Include.NON_DEFAULT);
      if (indent)
      {
         mapper.enable(SerializationFeature.INDENT_OUTPUT);
      }
   }


   @Override
   public ObjectMapper getContext(Class<?> type)
   {
      if (type.getPackage().getName().startsWith("org.jboss.resteasy.skeleton.key")) return mapper;
      return null;
   }
}

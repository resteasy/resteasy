package org.jboss.resteasy.plugins.interceptors.encoding;

import org.jboss.resteasy.annotations.ContentEncoding;

import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Configurable;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@ConstrainedTo(ConstrainedTo.Type.SERVER)
public class ServerContentEncodingAnnotationFeature implements DynamicFeature
{
   @Override
   public void configure(ResourceInfo resourceInfo, Configurable configurable)
   {
      Set<String> encodings = getEncodings(resourceInfo.getResourceMethod().getAnnotations());
      if (encodings.size() <= 0)
      {
         encodings = getEncodings(resourceInfo.getResourceClass().getAnnotations());
         if (encodings.size() <= 0) return;
      }
      configurable.register(createFilter(encodings));
   }

   protected ServerContentEncodingAnnotationFilter createFilter(Set<String> encodings)
   {
      return new ServerContentEncodingAnnotationFilter(encodings);
   }

   protected Set<String> getEncodings(Annotation[] annotations)
   {
      Set<String> encodings = new HashSet<String>();
      for (Annotation annotation : annotations)
      {
         if (annotation.annotationType().isAnnotationPresent(ContentEncoding.class))
         {
           encodings.add(annotation.annotationType().getAnnotation(ContentEncoding.class).value().toLowerCase());
         }
      }
      return encodings;
   }
}

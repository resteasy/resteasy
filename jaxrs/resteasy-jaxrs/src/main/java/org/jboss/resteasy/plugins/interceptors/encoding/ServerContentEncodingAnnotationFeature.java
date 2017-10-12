package org.jboss.resteasy.plugins.interceptors.encoding;

import org.jboss.resteasy.annotations.ContentEncoding;

import javax.ws.rs.ConstrainedTo;
import javax.ws.rs.RuntimeType;
import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.FeatureContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@ConstrainedTo(RuntimeType.SERVER)
public class ServerContentEncodingAnnotationFeature implements DynamicFeature
{
   @Override
   public void configure(ResourceInfo resourceInfo, FeatureContext configurable)
   {
      final Class declaring = resourceInfo.getResourceClass();
      final Method method = resourceInfo.getResourceMethod();

      if (declaring == null || method == null) return;
      Set<String> encodings = getEncodings(method.getAnnotations());
      if (encodings.size() <= 0)
      {
         encodings = getEncodings(declaring.getAnnotations());
         if (encodings.size() <= 0) return;
      }
      // check if GZIP encoder has been registered
      if (!isGZipRegistered(configurable.getConfiguration()))
      {
         encodings.remove("gzip");
      }
      configurable.register(createFilter(encodings));
   }
   
   protected boolean isGZipRegistered(Configuration configuration)
   {
      //TODO replace with the line below when dropping resteasy-legacy
      //return configuration.isRegistered(GZIPEncodingInterceptor.class);
      for (Class<?> c : configuration.getClasses()) {
         if (GZIPEncodingInterceptor.class.isAssignableFrom(c)) {
            return true;
         }
      }
      return false;
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

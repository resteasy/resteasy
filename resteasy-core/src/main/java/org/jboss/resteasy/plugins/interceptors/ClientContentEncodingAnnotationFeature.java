package org.jboss.resteasy.plugins.interceptors;

import org.jboss.resteasy.annotations.ContentEncoding;

import jakarta.ws.rs.ConstrainedTo;
import jakarta.ws.rs.RuntimeType;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.FeatureContext;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@ConstrainedTo(RuntimeType.CLIENT)
public class ClientContentEncodingAnnotationFeature implements DynamicFeature
{
   @Override
   public void configure(ResourceInfo resourceInfo, FeatureContext configurable)
   {
      @SuppressWarnings("rawtypes")
      final Class declaring = resourceInfo.getResourceClass();
      final Method method = resourceInfo.getResourceMethod();

      if (declaring == null || method == null) return;

      for (Annotation[] annotations : method.getParameterAnnotations())
      {
         String encoding = getEncoding(annotations);
         if (encoding != null)
         {
            configurable.register(new ClientContentEncodingAnnotationFilter(encoding));
            return;
         }
      }
   }

   protected String getEncoding(Annotation[] annotations)
   {
      for (Annotation annotation : annotations)
      {
         if (annotation.annotationType().isAnnotationPresent(ContentEncoding.class))
         {
            return annotation.annotationType().getAnnotation(ContentEncoding.class).value();
         }
      }
      return null;
   }

}

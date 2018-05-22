package org.jboss.resteasy.core.interception.jaxrs;

import org.jboss.resteasy.annotations.DecorateTypes;
import org.jboss.resteasy.annotations.Decorator;
import org.jboss.resteasy.core.MediaTypeMap;
import org.jboss.resteasy.spi.DecoratorProcessor;

import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;

/**
 * Finds DecoratorProcessors and calls decorates on them by introspecting annotations.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * @see org.jboss.resteasy.spi.DecoratorProcessor
 * @see org.jboss.resteasy.annotations.DecorateTypes
 * @see org.jboss.resteasy.annotations.Decorator
 */
public class DecoratorMatcher
{
   /**
    * @param targetClass i.e. Marshaller
    * @param target target object
    * @param type        i.e. a JAXB annotated class
    * @param annotations i.e. method or parameter annotations
    * @param mediaType media type
    * @param <T> type
    * @return decorated target object
    */
   @SuppressWarnings(value = "unchecked")
   public <T> T decorate(Class<T> targetClass, T target, Class type, Annotation[] annotations, MediaType mediaType)
   {
      HashMap<Class<?>, Annotation> meta = new HashMap<Class<?>, Annotation>();
      if (type != null)
      {
	      registerDecorators(targetClass, meta, type.getAnnotations());
      }
      // override any class level ones
      if (annotations != null)
      {
         registerDecorators(targetClass, meta, annotations);
      }
      if (meta.size() == 0) return target;

      MediaTypeMap<Class<?>> typeMap = new MediaTypeMap<Class<?>>();
      for (Class<?> decoratorAnnotation : meta.keySet())
      {
         Decorator decorator = decoratorAnnotation.getAnnotation(Decorator.class);
         String[] mediaTypes = {"*/*"};
         DecorateTypes produces = decorator.processor().getAnnotation(DecorateTypes.class);
         if (produces != null)
         {
            mediaTypes = produces.value();
         }
         for (String pType : mediaTypes)
         {
            typeMap.add(MediaType.valueOf(pType), decoratorAnnotation);
         }
      }

      List<Class<?>> list = typeMap.getPossible(mediaType);
      for (Class<?> decoratorAnnotation : list)
      {
         Annotation annotation = meta.get(decoratorAnnotation);
         Decorator decorator = decoratorAnnotation.getAnnotation(Decorator.class);
         DecoratorProcessor processor = null;
         try
         {
            processor = decorator.processor().newInstance();
         }
         catch (InstantiationException e)
         {
            throw new RuntimeException(e.getCause());
         }
         catch (IllegalAccessException e)
         {
            throw new RuntimeException(e);
         }
         target = (T) processor.decorate(target, annotation, type, annotations, mediaType);
      }

      return target;
   }

	private <T> void registerDecorators(Class<T> targetClass, HashMap<Class<?>, Annotation> meta, Annotation[] annotations) {
	   for (Annotation annotation : annotations)
	   {
		   Decorator decorator = annotation.annotationType().getAnnotation(Decorator.class);
		   if (decorator != null && targetClass.isAssignableFrom(decorator.target()))
		   {
			   meta.put(annotation.annotationType(), annotation);
		   }
	   }
	}
}

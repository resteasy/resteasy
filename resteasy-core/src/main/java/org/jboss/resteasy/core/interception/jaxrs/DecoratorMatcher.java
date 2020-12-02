package org.jboss.resteasy.core.interception.jaxrs;

import org.jboss.resteasy.annotations.DecorateTypes;
import org.jboss.resteasy.annotations.Decorator;
import org.jboss.resteasy.annotations.Decorators;
import org.jboss.resteasy.core.MediaTypeMap;
import org.jboss.resteasy.spi.DecoratorProcessor;

import jakarta.ws.rs.core.MediaType;
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
   @SuppressWarnings(value = "rawtypes")
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
         Decorators decorators = decoratorAnnotation.getAnnotation(Decorators.class);
         if (decorators != null) {
            for (Decorator decorator : decorators.values()) {
               injectDecorator(typeMap, decoratorAnnotation, decorator);
            }
         } else {
            Decorator decorator = decoratorAnnotation.getAnnotation(Decorator.class);
            injectDecorator(typeMap, decoratorAnnotation, decorator);
         }
      }

      List<Class<?>> list = typeMap.getPossible(mediaType);
      for (Class<?> decoratorAnnotation : list)
      {
         Annotation annotation = meta.get(decoratorAnnotation);
         Decorators decorators = decoratorAnnotation.getAnnotation(Decorators.class);
         if (decorators != null) {
            for (Decorator decorator : decorators.values()) {
               if (decorator.target().isAssignableFrom(target.getClass())) {
                  target = doDecoration(target, type, annotations, mediaType, annotation, decorator);
               }
            }
         } else {
            Decorator decorator = decoratorAnnotation.getAnnotation(Decorator.class);
            target = doDecoration(target, type, annotations, mediaType, annotation, decorator);
         }
      }

      return target;
   }

   @SuppressWarnings({"rawtypes", "unchecked"})
   private <T> T doDecoration(T target, Class type, Annotation[] annotations, MediaType mediaType, Annotation annotation, Decorator decorator) {
      DecoratorProcessor processor = null;
      try {
         processor = decorator.processor().newInstance();
      } catch (InstantiationException e) {
         throw new RuntimeException(e.getCause());
      } catch (IllegalAccessException e) {
         throw new RuntimeException(e);
      }
      target = (T) processor.decorate(target, annotation, type, annotations, mediaType);
      return target;
   }

   private void injectDecorator(MediaTypeMap<Class<?>> typeMap, Class<?> decoratorAnnotation, Decorator decorator) {
      String[] mediaTypes = {"*/*"};
      DecorateTypes produces = decorator.processor().getAnnotation(DecorateTypes.class);
      if (produces != null) {
         mediaTypes = produces.value();
      }
      for (String pType : mediaTypes) {
         typeMap.add(pType, decoratorAnnotation);
      }
   }

   public <T> boolean hasDecorator(Class<T> targetClass, Annotation[] annotations) {
      if (targetClass == null || annotations == null)
         return false;
      for (Annotation annotation : annotations) {
         Decorators decorators = annotation.annotationType().getAnnotation(Decorators.class);
         if (decorators != null) {
            for (Decorator decorator : decorators.values()) {
               if (decorator != null && decorator.target().isAssignableFrom(targetClass))
                  return true;
            }
         } else {
            Decorator decorator = annotation.annotationType().getAnnotation(Decorator.class);
            if (decorator != null && decorator.target().isAssignableFrom(targetClass))
               return true;
         }
      }
      return false;
   }

   private <T> void registerDecorators(Class<T> targetClass, HashMap<Class<?>, Annotation> meta, Annotation[] annotations) {
      for (Annotation annotation : annotations) {
         Decorators decorators = annotation.annotationType().getAnnotation(Decorators.class);
         if (decorators != null) {
            for (Decorator decorator : decorators.values()) {
               putMeta(targetClass, meta, annotation, decorator);
            }
         } else {
            Decorator decorator = annotation.annotationType().getAnnotation(Decorator.class);
            putMeta(targetClass, meta, annotation, decorator);
         }
      }
   }

   private <T> void putMeta(Class<T> targetClass, HashMap<Class<?>, Annotation> meta, Annotation annotation, Decorator decorator) {
      if (decorator != null && targetClass.isAssignableFrom(decorator.target())) {
         meta.put(annotation.annotationType(), annotation);
      }
   }
}

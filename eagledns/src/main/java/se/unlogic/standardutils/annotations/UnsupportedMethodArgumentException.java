package se.unlogic.standardutils.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;


public class UnsupportedMethodArgumentException extends RuntimeException {

   private static final long serialVersionUID = 2449699127133031293L;

   private final Class<?> beanClass;
   private final Class<? extends Annotation> annotation;
   private final Method method;

   public UnsupportedMethodArgumentException(final String message, final Method method, final Class<? extends Annotation> annotation, final Class<?> beanClass) {
      super(message);

      this.beanClass = beanClass;
      this.annotation = annotation;
      this.method = method;
   }

   public Class<?> getBeanClass() {
      return beanClass;
   }

   public Method getMethod() {
      return method;
   }

   public Class<? extends Annotation> getAnnotation() {
      return annotation;
   }
}

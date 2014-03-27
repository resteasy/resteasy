package org.jboss.resteasy.plugins.validation.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.util.AnnotationLiteral;
import javax.interceptor.Interceptors;

/**
 * 
 * @author <a href="ron.sigal@jboss.com">Ron Sigal</a>
 * @version $Revision: 1.1 $
 *
 * Copyright Feb 7, 2014
 */
public class ResteasyValidationCdiAnnotatedType<TYPE> implements AnnotatedType<TYPE>
{
   private Class<?>[] interceptorClasses;
   private Annotation interceptors;
   
   public abstract static class InterceptorQualifer extends AnnotationLiteral<Interceptors> implements Interceptors
   {
      private static final long serialVersionUID = 3415188955049850838L;
   }
   
   private AnnotatedType<TYPE> delegate;
   private Set<Annotation> annotations = new HashSet<Annotation>();
   
   public ResteasyValidationCdiAnnotatedType(AnnotatedType<TYPE> delegate)
   {
      this.delegate = delegate;
      for (Iterator<Annotation> it = delegate.getAnnotations().iterator(); it.hasNext(); )
      {
         Annotation a = it.next();
         if (!Interceptors.class.equals(a.annotationType()))
         {
            annotations.add(a);
            System.out.println("adding " + a);
         }
      }
System.out.println("annotations: " + annotations.size());
      if (delegate.getAnnotation(Interceptors.class) != null)
      {
         System.out.println("annotations: " + annotations.size());
         Class<?>[] oldClasses = delegate.getAnnotation(Interceptors.class).value();
         interceptorClasses = new Class[oldClasses.length + 1];
         System.arraycopy(oldClasses, 0, interceptorClasses, 0, oldClasses.length);
         interceptorClasses[oldClasses.length] = ResteasyValidationCdiInterceptor.class;
      }
      else
      {
         interceptorClasses = new Class[] {ResteasyValidationCdiInterceptor.class};
      }
      interceptors = new InterceptorQualifer()
      {
         private static final long serialVersionUID = -786183470311915886L;

         public Class[] value()
         {
            return interceptorClasses;
         }
      };
      this.annotations.add(interceptors);
      System.out.println("Annotations: ");
      for (Iterator<Annotation> it = annotations.iterator(); it.hasNext(); )
      {
         System.out.println(it.next());
      }
   }

   public Set<AnnotatedConstructor<TYPE>> getConstructors()
   {
      return delegate.getConstructors();
   }

   public Set<AnnotatedField<? super TYPE>> getFields()
   {
      return delegate.getFields();
   }

   public Class<TYPE> getJavaClass()
   {
      return delegate.getJavaClass();
   }

   public Set<AnnotatedMethod<? super TYPE>> getMethods()
   {
      return delegate.getMethods();
   }

   @SuppressWarnings("unchecked")
   public <T extends Annotation> T getAnnotation(Class<T> annotationType)
   {
      if (annotationType.equals(Interceptors.class))
      {
         return (T) interceptors;
      }
      return delegate.getAnnotation(annotationType);
   }

   public Set<Annotation> getAnnotations()
   {
      return Collections.unmodifiableSet(annotations);
   }

   public Type getBaseType()
   {
      return delegate.getBaseType();
   }

   public Set<Type> getTypeClosure()
   {
      return delegate.getTypeClosure();
   }

   public boolean isAnnotationPresent(Class<? extends Annotation> annotationType)
   {
      return delegate.isAnnotationPresent(annotationType);
   }
}

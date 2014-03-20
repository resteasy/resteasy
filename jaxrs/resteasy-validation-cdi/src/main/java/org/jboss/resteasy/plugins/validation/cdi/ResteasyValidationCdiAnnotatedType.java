package org.jboss.resteasy.plugins.validation.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.util.AnnotationLiteral;

/**
 * This wrapper allows additional metadata to be added during bootstrap.
 * 
 * @author Jozef Hartinger
 * @author rsigal
 * 
 */
public class ResteasyValidationCdiAnnotatedType<TYPE> implements AnnotatedType<TYPE>
{
   public static final Annotation validationCdiEnabledLiteral = new AnnotationLiteral<ResteasyValidationCdiEnabledAnnotation>()
   {
      private static final long serialVersionUID = 2863165642588583802L;
   };
   
   private AnnotatedType<TYPE> delegate;
   private Set<Annotation> annotations = new HashSet<Annotation>();
   
   public ResteasyValidationCdiAnnotatedType(AnnotatedType<TYPE> delegate)
   {
      this.delegate = delegate;
      this.annotations.addAll(delegate.getAnnotations());
      this.annotations.add(validationCdiEnabledLiteral);
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
      if (annotationType.equals(ResteasyValidationCdiEnabledAnnotation.class))
      {
         return (T) validationCdiEnabledLiteral;
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

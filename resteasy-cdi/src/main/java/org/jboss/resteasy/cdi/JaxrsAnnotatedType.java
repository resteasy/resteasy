package org.jboss.resteasy.cdi;

import jakarta.enterprise.inject.spi.AnnotatedConstructor;
import jakarta.enterprise.inject.spi.AnnotatedField;
import jakarta.enterprise.inject.spi.AnnotatedMethod;
import jakarta.enterprise.inject.spi.AnnotatedType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * This wrapper allows additional metadata to be added during bootstrap.
 *
 * @author Jozef Hartinger
 *
 */
public class JaxrsAnnotatedType<TYPE> implements AnnotatedType<TYPE>
{

   private AnnotatedType<TYPE> delegate;
   private Set<Annotation> annotations = new HashSet<Annotation>();

   public JaxrsAnnotatedType(final AnnotatedType<TYPE> delegate, final Annotation scope)
   {
      this.delegate = delegate;
      this.annotations.addAll(delegate.getAnnotations());
      this.annotations.add(scope);
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

   public <T extends Annotation> T getAnnotation(Class<T> annotationType)
   {
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

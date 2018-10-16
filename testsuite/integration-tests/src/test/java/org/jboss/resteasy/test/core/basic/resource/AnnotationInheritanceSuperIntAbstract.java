package org.jboss.resteasy.test.core.basic.resource;

public abstract class AnnotationInheritanceSuperIntAbstract implements AnnotationInheritanceSuperInt {
   @Override
   public String getFoo() {
      return "Foo: " + getName();
   }

   protected abstract String getName();
}

package org.jboss.resteasy.test.asynch.resource;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


@Retention(RUNTIME)
@Target({ FIELD, METHOD, PARAMETER })
public @interface AsyncInjectionPrimitiveInjectorSpecifier
{
   enum Type {
      VALUE, NULL, NO_RESULT;
   }
   
   Type value() default Type.VALUE;
}
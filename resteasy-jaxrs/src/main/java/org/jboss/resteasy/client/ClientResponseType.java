package org.jboss.resteasy.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("unchecked")
public @interface ClientResponseType
{
   Class entityType() ;
   Class genericType() default Void.class;
}

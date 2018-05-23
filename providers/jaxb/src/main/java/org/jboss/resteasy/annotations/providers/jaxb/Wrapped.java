package org.jboss.resteasy.annotations.providers.jaxb;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Put this on a method or parameter when you want to marshal or unmarshal a collection or array of JAXB objects
 * <p>
 * i.e.
 * <pre>
 * {@literal @}PUT
 * {@literal @}Consumes("application/xml") public void put(@Wrapped User[] users);
 * </pre>
 * <p>
 * User is a jaxb annotated class.  The input should be:
 * <p>
 * {@literal <}resteasy:collection xmlns:resteasy="http://jboss.org/resteasy" xmlns:ns2="whatever"{@literal >}
 * {@literal <}ns2:user{@literal >}...{@literal <}/ns2:user{@literal >}
 * {@literal <}ns2:user{@literal >}...{@literal <}/ns2:user{@literal >}
 * {@literal <}/resteasy:collection{@literal >}
 * <p>
 * or
 * {@literal @}GET
 * {@literal @}Wrapped public User[] getUsers();
 * 
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Wrapped
{
   String element() default "collection";

   String namespace() default "";

   String prefix() default "";
}

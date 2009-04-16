package org.jboss.resteasy.annotations.providers.jaxb;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Put this on a method or parameter when you want to marshal or unmarshal a collection or array of JAXB objects
 * <p/>
 * i.e.
 * <p/>
 * <pre>
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * @PUT
 * @Consumes("application/xml") public void put(@Wrapped User[] users);
 * </pre>
 * <p/>
 * User is a jaxb annotated class.  The input should be:
 * <p/>
 * <resteasy:collection xmlns:resteasy="http://jboss.org/resteasy" xmlns:ns2="whatever">
 * <ns2:user>...</ns2:user>
 * <ns2:user>...</ns2:user>
 * <p/>
 * </resteasy:collection>
 * <p/>
 * or
 * @GET
 * @Wrapped public User[] getUsers();
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Wrapped
{
   String element() default "collection";

   String namespace() default "";

   String prefix() default "";
}

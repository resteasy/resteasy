package org.jboss.resteasy.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This can be used as a value object for incoming/outgoing request/responses.
 * You can re-use @*Param annotations on fields/methods of the parameter to
 * unmarshall from the request or marshall to the response depending if you're
 * using server-side JAX-RS or the Resteasy client framework
 * <p>
 * When using this on the server side, you must put your @*Param annotations
 * on either fields or setter methods.
 * <p>
 * When using this with the Resteasy client framework, you must put your @*Param
 * annotations on either fields or getter methods.
 *
 * @author <a href="bill@burkecentral.com">Bill Burke</a>
 * @version $Revision:$
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
public @interface Form
{
   /**
    * This is a form parameter prefix you want applied to any @FormParam variables in the object
    * See documentation for more details.
    *
    * @return prefix
    */
   String prefix() default "";
}

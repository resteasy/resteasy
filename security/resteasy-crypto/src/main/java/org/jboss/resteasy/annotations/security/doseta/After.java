package org.jboss.resteasy.annotations.security.doseta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to add an expiration attribute when signing or as a stale check for verification.  Calculate on current time plus the values included within
 * this annotation.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface After
{
   int seconds() default 0;

   int minutes() default 0;

   int hours() default 0;

   int days() default 0;

   int months() default 0;

   int years() default 0;
}

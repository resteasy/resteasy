package org.jboss.resteasy.annotations.interception;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Should be placed on a PreProcessInterceptor.
 * This annotation specifies ordering of interceptors.
 * Will run after SecurityPrecedence.  These types of interceptors may redirect the request.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Precedence("REDIRECT")
public @interface RedirectPrecedence
{
   public static final String PRECEDENCE_STRING = "REDIRECT";
}

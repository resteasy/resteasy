package org.jboss.resteasy.annotations.interception;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Should be placed on a PreProcessInterceptor.
 * This annotation specifies ordering of interceptors.
 * Security-based interceptors should always come first.  They may look at headers, but they don't read the input
 * stream.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Precedence("SECURITY")
public @interface SecurityPrecedence
{
   String PRECEDENCE_STRING = "SECURITY";
}

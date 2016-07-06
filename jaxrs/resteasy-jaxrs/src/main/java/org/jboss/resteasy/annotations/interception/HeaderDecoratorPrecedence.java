package org.jboss.resteasy.annotations.interception;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * HeaderDecoratorPrecedence interceptors should always come first as they decorate a response (on the server), or an
 * outgoing request (on the client) with special, user-defined, headers.  These headers may trigger behavior in other interceptors.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * 
 * @deprecated The Resteasy interceptor facility introduced in release 2.x
 * is replaced by the JAX-RS 2.0 compliant interceptor facility in release 3.0.x.
 * 
 * @see jaxrs-api (https://jcp.org/en/jsr/detail?id=339)
 */
@Deprecated
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Precedence("HEADER_DECORATOR")
public @interface HeaderDecoratorPrecedence
{
   public static final String PRECEDENCE_STRING = "HEADER_DECORATOR";
}

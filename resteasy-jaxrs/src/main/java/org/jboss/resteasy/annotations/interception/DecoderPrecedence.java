package org.jboss.resteasy.annotations.interception;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This interceptor is an Content-Encoding decoder.  It is used with MessageBodyWriter interceptors.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Precedence("DECODER")
public @interface DecoderPrecedence
{
   String PRECEDENCE_STRING = "DECODER";
}
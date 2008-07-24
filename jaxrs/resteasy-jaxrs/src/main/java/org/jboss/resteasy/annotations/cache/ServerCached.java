package org.jboss.resteasy.annotations.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that the response to this jax-rs method should be cached on the server.
 * Will reuse @Cache settings.  Can override @Cache settings with maxAge() attribute.
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ServerCached
{
   /**
    * Override @Cache settings if they must be different or if you do not want
    * to send response Cache-Control headers
    *
    * @return
    */
   int maxAge() default -1;

   /**
    * Do not cache response, but do cache maxAge delta.  This allows server cache
    * to handle revalidation requests easily
    *
    * @return
    */
   boolean revalidationOnly() default false;
}

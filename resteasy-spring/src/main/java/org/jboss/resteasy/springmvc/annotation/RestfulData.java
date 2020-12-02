package org.jboss.resteasy.springmvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author <a href="mailto:sduskis@gmail.com">Solomn Duskis</a>
 * @version $Revision: 1 $
 *
 */

@Target(
{ ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestfulData
{

   /**
    * what content type does this support? This has the same allowed values as
    * jakarta.ws.rs.Consumes
    *
    * @see jakarta.ws.rs.Consumes
    * */
   String[] value = null;

}

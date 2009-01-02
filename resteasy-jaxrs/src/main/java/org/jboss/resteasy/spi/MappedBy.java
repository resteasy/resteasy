package org.jboss.resteasy.spi;

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

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MappedBy
{
   String template() default "";
   @SuppressWarnings("unchecked")
   Class resourceClass() default Void.class;
   String resourceMethodName() default "";
}

package org.jboss.resteasy.springmvc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RestfulData {

    /** what content type does this support?  This has the same allowed values as javax.ws.rs.Consumes
     * 
     * @see javax.ws.rs.Consumes
     * */
    String[] value = null;

}

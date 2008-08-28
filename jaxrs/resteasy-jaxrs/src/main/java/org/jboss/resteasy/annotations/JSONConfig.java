/*
 * JBoss, the OpenSource J2EE webOS Distributable under LGPL license. See terms of license at gnu.org.
 */
package org.jboss.resteasy.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A JSONConfig.
 * 
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(
{ElementType.METHOD, ElementType.TYPE})
public @interface JSONConfig 
{

   /**
    * FIXME Comment this
    * 
    * @return
    */
   JSONConvention value() default JSONConvention.BADGERFISH;
   
   /**
    * FIXME Comment this
    * 
    * @return
    */
   String[] ignoredElements() default "";
   
   /**
    * FIXME Comment this
    * 
    * @return
    */
   JSONToXml jsonToXml() default @JSONToXml(xmlElement = "", jsonName = "");
   
   
}

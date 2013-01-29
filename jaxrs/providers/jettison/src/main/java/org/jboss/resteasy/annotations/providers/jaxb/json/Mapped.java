package org.jboss.resteasy.annotations.providers.jaxb.json;

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
        {ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
public @interface Mapped
{
   /**
    * List of JSON attributes that should be regarded as Elements
    *
    * @return
    */
   public String[] attributesAsElements() default {};

   /**
    * Map the XML namespace to a JSON namespace
    *
    * @return
    */
   public XmlNsMap[] namespaceMap() default {};


}
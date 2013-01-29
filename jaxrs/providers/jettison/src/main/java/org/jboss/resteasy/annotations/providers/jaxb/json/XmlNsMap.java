package org.jboss.resteasy.annotations.providers.jaxb.json;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A JSONToXml.
 *
 * @author <a href="ryan@damnhandy.com">Ryan J. McDonough</a>
 * @version $Revision:$
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface XmlNsMap
{

   /**
    * XML Namespace
    *
    * @return
    */
   String namespace();

   /**
    * JSON name prefix
    *
    * @return
    */
   String jsonName();
}

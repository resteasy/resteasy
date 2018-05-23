package org.jboss.resteasy.annotations.providers.jaxb;

import javax.xml.XMLConstants;
import javax.xml.bind.annotation.XmlNs;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(
        {ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
public @interface JAXBConfig
{

   boolean useNameSpacePrefix() default false;

   XmlNs[] namespaces() default {};

   boolean validate() default false;

   String schema() default "";

   String schemaType() default XMLConstants.W3C_XML_SCHEMA_NS_URI;

}

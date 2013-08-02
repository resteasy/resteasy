package org.jboss.resteasy.annotations;

import org.jboss.resteasy.spi.StringParameterUnmarshaller;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Meta-annotation to be placed on another annotation that triggers a StringParameterUnmarshaller to be applied
 * to a string based annotation injector i.e. @HeaderParam, @PathParam, @QueryParam
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * @see org.jboss.resteasy.spi.StringParameterUnmarshaller
 */
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface StringParameterUnmarshallerBinder
{
   Class<? extends StringParameterUnmarshaller> value();
}
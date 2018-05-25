package org.jboss.resteasy.annotations.providers.multipart;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This can be used as a value object for incoming/outgoing request/responses
 * of the multipart/form-data mime type.  Parts are marshalled to and from
 * properties of the value object annotated with the JAX-RS @FormParam
 * anntotation.
 * <p>
 * When using the form class as input, you must put @FormParam annotations
 * on either fields or setter methods.
 * <p>
 * When using this form class as output, you must put @FormParam
 * annotations on either fields or getter methods.
 *
 * @author <a href="bill@burkecentral.com">Bill Burke</a>
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MultipartForm
{
}

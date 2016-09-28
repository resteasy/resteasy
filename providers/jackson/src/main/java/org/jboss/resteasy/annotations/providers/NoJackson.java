package org.jboss.resteasy.annotations.providers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Placed on class, parameter, field or method when you don't want the Jackson provider to be triggered. i.e.
 * when you want to use JAXB provider
 *
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 * @deprecated Use resteasy-jackson2-provider
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Deprecated
public @interface NoJackson
{
}

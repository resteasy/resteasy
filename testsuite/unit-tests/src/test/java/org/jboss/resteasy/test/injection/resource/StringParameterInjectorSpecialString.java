package org.jboss.resteasy.test.injection.resource;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.jboss.resteasy.annotations.StringParameterUnmarshallerBinder;

@Retention(RetentionPolicy.RUNTIME)
@StringParameterUnmarshallerBinder(StringParameterInjectorUnmarshaller.class)
public @interface StringParameterInjectorSpecialString {
}

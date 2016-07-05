package org.jboss.resteasy.test.injection.resource;

import org.jboss.resteasy.annotations.StringParameterUnmarshallerBinder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@StringParameterUnmarshallerBinder(StringParameterInjectorUnmarshaller.class)
public @interface StringParameterInjectorSpecialString {
}

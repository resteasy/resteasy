package org.jboss.resteasy.test.providers.custom.resource;

import org.jboss.resteasy.core.InjectorFactoryImpl;
import org.jboss.resteasy.core.ValueInjector;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.Parameter;
import org.jboss.resteasy.util.FindAnnotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Type;

public class CustomValueInjectorInjectorFactoryImpl extends InjectorFactoryImpl {
    @Override
    public ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, String defaultName, Class type,
                                                  Type genericType, Annotation[] annotations, ResteasyProviderFactory factory) {
        final CustomValueInjectorHello hello = FindAnnotation.findAnnotation(annotations, CustomValueInjectorHello.class);
        if (hello == null) {
            return super.createParameterExtractor(injectTargetClass, injectTarget, defaultName, type, genericType, annotations, factory);
        } else {
            return new ValueInjector() {
                public Object inject(HttpRequest request, HttpResponse response) {
                    return hello.value();
                }

                public Object inject() {
                    return hello.value();
                }
            };
        }
    }

    @Override
    public ValueInjector createParameterExtractor(Parameter parameter, ResteasyProviderFactory providerFactory) {
        final CustomValueInjectorHello hello = FindAnnotation.findAnnotation(parameter.getAnnotations(), CustomValueInjectorHello.class);
        if (hello == null) {
            return super.createParameterExtractor(parameter, providerFactory);
        } else {
            return new ValueInjector() {
                public Object inject(HttpRequest request, HttpResponse response) {
                    return hello.value();
                }

                public Object inject() {
                    return hello.value();
                }
            };
        }
    }
}

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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class CustomValueInjectorInjectorFactoryImpl extends InjectorFactoryImpl {
    @Override
    public ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, String defaultName, Class type,
                                                  Type genericType, Annotation[] annotations, ResteasyProviderFactory factory) {
        final CustomValueInjectorHello hello = FindAnnotation.findAnnotation(annotations, CustomValueInjectorHello.class);
        if (hello == null) {
            return super.createParameterExtractor(injectTargetClass, injectTarget, defaultName, type, genericType, annotations, factory);
        } else {
            return new ValueInjector() {
                @Override
                public CompletionStage<Object> inject(HttpRequest request, HttpResponse response, boolean unwrapAsync) {
                    return CompletableFuture.completedFuture(hello.value());
                }

                @Override
                public CompletionStage<Object> inject(boolean unwrapAsync) {
                   // do nothing.
                   return CompletableFuture.completedFuture(hello.value());
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
                @Override
                public CompletionStage<Object> inject(HttpRequest request, HttpResponse response, boolean unwrapAsync) {
                    return CompletableFuture.completedFuture(hello.value());
                }

                @Override
                public CompletionStage<Object> inject(boolean unwrapAsync) {
                   // do nothing.
                   return CompletableFuture.completedFuture(hello.value());
                }
            };
        }
    }
}

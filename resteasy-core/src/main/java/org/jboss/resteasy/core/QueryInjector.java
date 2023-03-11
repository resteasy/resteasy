package org.jboss.resteasy.core;

import java.lang.reflect.Constructor;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.ValueInjector;

/**
 * Created by Simon Ström on 7/17/14.
 */
@SuppressWarnings(value = "unchecked")
public class QueryInjector implements ValueInjector {

    private Class type;
    private ConstructorInjector constructorInjector;
    private PropertyInjector propertyInjector;

    public QueryInjector(final Class type, final ResteasyProviderFactory factory) {
        this.type = type;
        Constructor<?> constructor;

        try {
            constructor = type.getConstructor();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to instantiate @Query class. No no-arg constructor.");
        }

        constructorInjector = factory.getInjectorFactory().createConstructor(constructor, factory);
        propertyInjector = factory.getInjectorFactory().createPropertyInjector(type, factory);
    }

    @Override
    public Object inject(boolean unwrapAsync) {
        throw new IllegalStateException("You cannot inject outside the scope of an HTTP request");
    }

    @Override
    public Object inject(HttpRequest request, HttpResponse response, boolean unwrapAsync) {
        Object obj = constructorInjector.construct(unwrapAsync);
        if (obj instanceof CompletionStage) {
            CompletionStage<Object> stage = (CompletionStage<Object>) obj;
            return stage.thenCompose(target -> {
                CompletionStage<Void> propertyStage = propertyInjector.inject(request, response, target, unwrapAsync);
                return propertyStage == null ? CompletableFuture.completedFuture(target)
                        : propertyStage
                                .thenApply(v -> target);
            });
        }
        CompletionStage<Void> propertyStage = propertyInjector.inject(request, response, obj, unwrapAsync);
        return propertyStage == null ? obj : propertyStage.thenApply(v -> obj);
    }
}

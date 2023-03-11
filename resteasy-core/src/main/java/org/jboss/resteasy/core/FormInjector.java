package org.jboss.resteasy.core;

import java.lang.reflect.Constructor;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.ValueInjector;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public class FormInjector implements ValueInjector {
    private Class type;
    private ConstructorInjector constructorInjector;
    private PropertyInjector propertyInjector;

    @SuppressWarnings(value = "unchecked")
    public FormInjector(final Class type, final ResteasyProviderFactory factory) {
        this.type = type;
        Constructor<?> constructor = null;

        try {
            constructor = type.getDeclaredConstructor();
            constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(Messages.MESSAGES.unableToInstantiateForm());
        }

        constructorInjector = factory.getInjectorFactory().createConstructor(constructor, factory);
        propertyInjector = factory.getInjectorFactory().createPropertyInjector(type, factory);

    }

    @Override
    public Object inject(boolean unwrapAsync) {
        throw new IllegalStateException(Messages.MESSAGES.cannotInjectIntoForm());
    }

    @Override
    public Object inject(HttpRequest request, HttpResponse response, boolean unwrapAsync) {
        Object obj = constructorInjector.construct(unwrapAsync);
        if (obj instanceof CompletionStage) {
            @SuppressWarnings("unchecked")
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

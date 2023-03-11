package org.jboss.resteasy.cdi;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.function.Function;

import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.enterprise.inject.spi.InjectionTarget;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;

import org.jboss.resteasy.cdi.i18n.LogMessages;
import org.jboss.resteasy.cdi.i18n.Messages;
import org.jboss.resteasy.core.PropertyInjectorImpl;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.util.Types;
import org.jboss.resteasy.spi.validation.GeneralValidatorCDI;
import org.jboss.resteasy.util.GetRestful;

/**
 * This implementation of InjectionTarget is a wrapper that allows JAX-RS
 * property injection to be performed just after CDI injection.
 *
 * @author Jozef Hartinger
 *
 */
public class JaxrsInjectionTarget<T> implements InjectionTarget<T> {

    private final InjectionTarget<T> delegate;
    private final Class<T> clazz;
    private PropertyInjector propertyInjector;

    private final boolean hasPostConstruct;

    private static final Function<Method, Boolean> validatePostConstructParameters = (Method m) -> {
        if (m.getParameterCount() == 0)
            return true;
        else
            return m.getParameterCount() == 1
                    && InvocationContext.class.equals(m.getParameterTypes()[0])
                    && m.getAnnotation(AroundInvoke.class) != null;
    };

    public JaxrsInjectionTarget(final InjectionTarget<T> delegate, final Class<T> clazz) {
        this.delegate = delegate;
        this.clazz = clazz;
        hasPostConstruct = Types.hasPostConstruct(clazz, validatePostConstructParameters);
    }

    @Override
    public void inject(T instance, CreationalContext<T> ctx) {
        delegate.inject(instance, ctx);

        // We need to load PropertyInjector lazily since RESTEasy starts
        // after the CDI lifecycle events are executed
        if (propertyInjector == null) {
            propertyInjector = getPropertyInjector();
        }

        HttpRequest request = ResteasyContext.getContextData(HttpRequest.class);
        HttpResponse response = ResteasyContext.getContextData(HttpResponse.class);

        if ((request != null) && (response != null)) {
            propertyInjector.inject(request, response, instance, false);
        } else {
            propertyInjector.inject(instance, false);
        }

        if (request != null && !hasPostConstruct) {
            validate(request, instance);
        } else {
            LogMessages.LOGGER.debug(Messages.MESSAGES.skippingValidationOutsideResteasyContext());
        }
    }

    @Override
    public void postConstruct(T instance) {
        delegate.postConstruct(instance);
        if (hasPostConstruct) {
            HttpRequest request = ResteasyContext.getContextData(HttpRequest.class);
            if (request != null) {
                validate(request, instance);
            } else {
                LogMessages.LOGGER.debug(Messages.MESSAGES.skippingValidationOutsideResteasyContext());
            }
        }
    }

    @Override
    public void preDestroy(T instance) {
        delegate.preDestroy(instance);
    }

    @Override
    public void dispose(T instance) {
        delegate.dispose(instance);
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return delegate.getInjectionPoints();
    }

    @Override
    public T produce(CreationalContext<T> ctx) {
        return delegate.produce(ctx);
    }

    private PropertyInjector getPropertyInjector() {
        return new PropertyInjectorImpl(clazz, ResteasyProviderFactory.getInstance());
    }

    private void validate(HttpRequest request, T instance) {
        if (GetRestful.isRootResource(clazz)) {
            ResteasyProviderFactory providerFactory = ResteasyProviderFactory.getInstance();
            ContextResolver<GeneralValidatorCDI> resolver = providerFactory.getContextResolver(GeneralValidatorCDI.class,
                    MediaType.WILDCARD_TYPE);
            GeneralValidatorCDI validator = null;
            if (resolver != null) {
                validator = providerFactory.getContextResolver(GeneralValidatorCDI.class, MediaType.WILDCARD_TYPE)
                        .getContext(null);
            }
            if (validator != null && validator.isValidatableFromCDI(clazz)) {
                validator.validate(request, instance);
                validator.checkViolationsfromCDI(request);
            }
        }
    }
}

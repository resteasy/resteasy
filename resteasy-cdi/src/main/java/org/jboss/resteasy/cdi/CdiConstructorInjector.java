package org.jboss.resteasy.cdi;

import java.lang.reflect.Type;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.cdi.i18n.LogMessages;
import org.jboss.resteasy.cdi.i18n.Messages;
import org.jboss.resteasy.spi.ApplicationException;
import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;

/**
 * This ConstructorInjector implementation uses CDI's BeanManager to obtain
 * a contextual instance of a bean.
 *
 * @author Jozef Hartinger
 *
 */
public class CdiConstructorInjector implements ConstructorInjector {
    private final BeanManager manager;
    private final Type type;

    public CdiConstructorInjector(final Type type, final BeanManager manager) {
        this.type = type;
        this.manager = manager;
    }

    @Override
    public Object construct(boolean unwrapAsync) {
        return construct((HttpRequest) null);
    }

    @Override
    public Object construct(HttpRequest request, HttpResponse response, boolean unwrapAsync)
            throws Failure, WebApplicationException, ApplicationException {
        return construct(request);
    }

    @Override
    public Object injectableArguments(boolean unwrapAsync) {
        return null;
    }

    @Override
    public Object injectableArguments(HttpRequest request, HttpResponse response, boolean unwrapAsync) throws Failure {
        return injectableArguments(unwrapAsync);
    }

    private Object construct(final HttpRequest request) {
        if (LogMessages.LOGGER.isDebugEnabled()) //keep this check for performance reasons, as toString() is expensive on CDI Bean
        {
            LogMessages.LOGGER.debug(Messages.MESSAGES.beansFound(type, manager.getBeans(type)));
        }

        // Ambiguous dependency may occur if a resource has subclasses, which findBean() excludes
        final Bean<?> bean = BeanManagerSupport.findBean(manager, type);
        if (request == null) {
            warnIfDependentProvider(bean);
        }
        return BeanManagerSupport.resolveBean(request, manager, type, bean);
    }

    /**
     * Logs a warning if the bean of a provider is {@code @Dependent}. An instance created without a request is
     * long-lived, e.g. a provider. A {@link Provider @Provider} class explicitly declared {@code @Dependent} already
     * fails the deployment, see {@link ResteasyCdiExtension#validateProviderScope}. Other providers, e.g. those
     * without {@code @Provider} or with a producer or synthetic bean, are only known once they are registered.
     * <p>
     * The class being constructed is checked rather than {@link Bean#getBeanClass()}, which is not the provider class
     * for a producer or synthetic bean. A discovered provider class without a scope is not {@code @Dependent}, as the
     * {@link ResteasyCdiExtension} assigns its scope.
     * </p>
     *
     * @param bean the bean being created
     */
    private void warnIfDependentProvider(final Bean<?> bean) {
        if (bean == null || bean.getScope() != Dependent.class || !(type instanceof Class<?> providerClass)) {
            return;
        }
        // A provider whose class declares type variables must be @Dependent
        if (providerClass.getTypeParameters().length == 0 && Utils.isProviderContract(providerClass)) {
            LogMessages.LOGGER.dependentProvider(providerClass.getName());
        }
    }
}

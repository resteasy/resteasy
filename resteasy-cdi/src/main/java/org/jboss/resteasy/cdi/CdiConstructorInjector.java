package org.jboss.resteasy.cdi;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.ws.rs.WebApplicationException;

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
        Set<Bean<?>> beans = manager.getBeans(type);

        if (beans.size() > 1) {
            Set<Bean<?>> modifiableBeans = new HashSet<>(beans);
            // Ambiguous dependency may occur if a resource has subclasses
            // Therefore we remove those beans
            // remove Beans that have clazz in their type closure but not as a base class
            modifiableBeans.removeIf(bean -> !bean.getBeanClass().equals(type) && !bean.isAlternative());
            beans = modifiableBeans;
        }

        if (LogMessages.LOGGER.isDebugEnabled()) //keep this check for performance reasons, as toString() is expensive on CDI Bean
        {
            LogMessages.LOGGER.debug(Messages.MESSAGES.beansFound(type, beans));
        }

        Bean<?> bean = manager.resolve(beans);
        CreationalContext<?> context = manager.createCreationalContext(bean);
        return manager.getReference(bean, type, context);
    }

    @Override
    public Object construct(HttpRequest request, HttpResponse response, boolean unwrapAsync)
            throws Failure, WebApplicationException, ApplicationException {
        return construct(unwrapAsync);
    }

    @Override
    public Object injectableArguments(boolean unwrapAsync) {
        return null;
    }

    @Override
    public Object injectableArguments(HttpRequest request, HttpResponse response, boolean unwrapAsync) throws Failure {
        return injectableArguments(unwrapAsync);
    }
}

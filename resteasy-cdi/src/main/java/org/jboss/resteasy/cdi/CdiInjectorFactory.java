/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Map;

import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.core.Context;

import org.jboss.resteasy.cdi.i18n.LogMessages;
import org.jboss.resteasy.cdi.i18n.Messages;
import org.jboss.resteasy.core.InjectorFactoryImpl;
import org.jboss.resteasy.core.PropertyInjectorImpl;
import org.jboss.resteasy.core.servlet.ResteasyHttpServletResponse;
import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.MethodInjector;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.StreamValueInjector;
import org.jboss.resteasy.spi.ValueInjector;
import org.jboss.resteasy.spi.metadata.Parameter;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.metadata.ResourceConstructor;
import org.jboss.resteasy.spi.metadata.ResourceLocator;

/**
 * @author Jozef Hartinger
 */
@SuppressWarnings("rawtypes")
public class CdiInjectorFactory implements InjectorFactory {
    public static final String BEAN_MANAGER_ATTRIBUTE_PREFIX = "org.jboss.weld.environment.servlet.";
    private static final String SERVLET_RESPONSE_ATTRIBUTE = ResteasyHttpServletResponse.class.getName();
    private final BeanManager manager;
    private final InjectorFactory delegate = new InjectorFactoryImpl();
    private final ResteasyCdiExtension extension;
    private final Map<Class<?>, Type> sessionBeanInterface;

    @SuppressWarnings("unused")
    public CdiInjectorFactory() {
        this.manager = lookupBeanManager();
        this.extension = lookupResteasyCdiExtension();
        sessionBeanInterface = extension.getSessionBeanInterface();
    }

    public CdiInjectorFactory(final BeanManager manager) {
        this.manager = manager;
        this.extension = lookupResteasyCdiExtension();
        sessionBeanInterface = extension.getSessionBeanInterface();
    }

    @Override
    public ValueInjector createParameterExtractor(Parameter parameter, ResteasyProviderFactory providerFactory) {
        if (parameter.getParamType() == Parameter.ParamType.CONTEXT && parameter.isContextType()
                && extension.beanContainer().contains(parameter.getResourceClass().getClazz())) {
            final Bean<?> bean = manager.resolve(manager.getBeans(parameter.getType(), ContextLiteral.INSTANCE));
            if (bean == null) {
                throw Messages.MESSAGES.unableToResolveBeanType(parameter.getType().getName(), Context.class.getName());
            }
            if (HttpServletResponse.class.isAssignableFrom(parameter.getType())) {
                // The wrapper holds the output stream of the response, so it's created for each request
                return new StreamValueInjector() {
                    @Override
                    public boolean isContentAvailable(final HttpRequest request) {
                        return request.getAttribute(
                                SERVLET_RESPONSE_ATTRIBUTE) instanceof ResteasyHttpServletResponse servletResponse
                                && servletResponse.isWritten();
                    }

                    @Override
                    public Object inject(final boolean unwrapAsync) {
                        return ResteasyHttpServletResponse.of((HttpServletResponse) BeanManagerSupport.resolveBean(null,
                                manager, parameter.getType(), bean));
                    }

                    @Override
                    public Object inject(final HttpRequest request, final HttpResponse response, final boolean unwrapAsync) {
                        Object servletResponse = request.getAttribute(SERVLET_RESPONSE_ATTRIBUTE);
                        if (servletResponse == null) {
                            servletResponse = ResteasyHttpServletResponse.of((HttpServletResponse) BeanManagerSupport
                                    .resolveBean(request, manager, parameter.getType(), bean));
                            request.setAttribute(SERVLET_RESPONSE_ATTRIBUTE, servletResponse);
                        }
                        return servletResponse;
                    }
                };
            }
            return new ValueInjector() {
                @Override
                public Object inject(final boolean unwrapAsync) {
                    return BeanManagerSupport.resolveBean(null, manager, parameter.getType(), bean);
                }

                @Override
                public Object inject(final HttpRequest request, final HttpResponse response, final boolean unwrapAsync) {
                    return BeanManagerSupport.resolveBean(request, manager, parameter.getType(), bean);
                }
            };
        }
        return delegate.createParameterExtractor(parameter, providerFactory);
    }

    @Override
    public MethodInjector createMethodInjector(ResourceLocator method, ResteasyProviderFactory factory) {
        return delegate.createMethodInjector(method, factory);
    }

    @Override
    public PropertyInjector createPropertyInjector(ResourceClass resourceClass, ResteasyProviderFactory providerFactory) {
        return new CdiPropertyInjector(delegate.createPropertyInjector(resourceClass, providerFactory),
                resourceClass.getClazz(), sessionBeanInterface, manager);
    }

    @Override
    public ConstructorInjector createConstructor(ResourceConstructor constructor, ResteasyProviderFactory providerFactory) {
        Class<?> clazz = constructor.getConstructor().getDeclaringClass();

        ConstructorInjector injector = cdiConstructor(clazz);
        if (injector != null)
            return injector;

        LogMessages.LOGGER.debug(Messages.MESSAGES.noCDIBeansFound(clazz));
        return delegate.createConstructor(constructor, providerFactory);
    }

    @Override
    public ConstructorInjector createConstructor(Constructor constructor, ResteasyProviderFactory factory) {
        Class<?> clazz = constructor.getDeclaringClass();

        ConstructorInjector injector = cdiConstructor(clazz);
        if (injector != null)
            return injector;

        LogMessages.LOGGER.debug(Messages.MESSAGES.noCDIBeansFound(clazz));
        return delegate.createConstructor(constructor, factory);
    }

    @Override
    public ConstructorInjector createConstructor(final Class<?> clazz, final ResteasyProviderFactory providerFactory) {
        final ConstructorInjector injector = cdiConstructor(clazz);
        if (injector != null) {
            return injector;
        }

        LogMessages.LOGGER.debug(Messages.MESSAGES.noCDIBeansFound(clazz));
        return delegate.createConstructor(clazz, providerFactory);
    }

    /**
     * Returns the contextual instance of a CDI client proxy, creating it if it does not exist yet. Other instances are
     * returned as is.
     * <p>
     * The client proxy of a session bean is returned as is. The EJB container creates the instance when the method is
     * invoked, after the parameters were validated, and the {@link JaxrsInjectionTarget} reports the violations of the
     * instance together with those of the parameters.
     * </p>
     *
     * @param instance the instance, which may be a CDI client proxy
     *
     * @return the contextual instance of the client proxy, or the instance if it is not a client proxy
     */
    @Override
    public Object unwrapProxy(final Object instance) {
        if (instance == null || isSessionBean(instance.getClass())) {
            return instance;
        }
        return CdiProxyUnwrapper.unwrapIfRequired(instance);
    }

    protected ConstructorInjector cdiConstructor(Class<?> clazz) {
        if (!manager.getBeans(clazz).isEmpty()) {
            LogMessages.LOGGER.debug(Messages.MESSAGES.usingCdiConstructorInjector(clazz));
            return new CdiConstructorInjector(clazz, manager);
        }

        if (sessionBeanInterface.containsKey(clazz)) {
            Type intfc = sessionBeanInterface.get(clazz);
            LogMessages.LOGGER.debug(Messages.MESSAGES.usingInterfaceForLookup(intfc, clazz));
            return new CdiConstructorInjector(intfc, manager);
        }

        return null;
    }

    public PropertyInjector createPropertyInjector(Class resourceClass, ResteasyProviderFactory factory) {
        // Create the property injector directly as it needs to skip the members annotated with @Inject. The members
        // converted by the ResteasyCdiExtension are not skipped as the instance may not have been created by CDI, e.g.
        // an Application.getSingletons() instance.
        final PropertyInjector propertyInjector = new PropertyInjectorImpl(resourceClass, factory,
                Utils::isInjectAnnotated);
        return new CdiPropertyInjector(propertyInjector, resourceClass, sessionBeanInterface, manager);
    }

    public ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, String defaultName,
            Class type, Type genericType, Annotation[] annotations, ResteasyProviderFactory factory) {
        return delegate.createParameterExtractor(injectTargetClass, injectTarget, defaultName, type, genericType, annotations,
                factory);
    }

    public ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, String defaultName,
            Class type,
            Type genericType, Annotation[] annotations, boolean useDefault, ResteasyProviderFactory factory) {
        return delegate.createParameterExtractor(injectTargetClass, injectTarget, defaultName, type, genericType, annotations,
                useDefault, factory);
    }

    /**
     * Do a lookup for BeanManager instance. JNDI and ServletContext is searched.
     *
     * @return BeanManager instance
     */
    protected BeanManager lookupBeanManager() {
        return BeanManagerSupport.findBeanManager();
    }

    public static BeanManager lookupBeanManagerCDIUtil() {
        BeanManager bm = null;
        try {
            bm = CDI.current().getBeanManager();
        } catch (NoClassDefFoundError e) {
            LogMessages.LOGGER.debug(Messages.MESSAGES.unableToFindCDIClass(), e);
        } catch (Exception e) {
            LogMessages.LOGGER.debug(Messages.MESSAGES.errorOccurredLookingUpViaCDIUtil(), e);
        }
        return bm;
    }

    /**
     * Lookup ResteasyCdiExtension instance that was instantiated during CDI bootstrap
     *
     * @return ResteasyCdiExtension instance
     */
    private ResteasyCdiExtension lookupResteasyCdiExtension() {
        return manager.getExtension(ResteasyCdiExtension.class);
    }

    private boolean isSessionBean(final Class<?> type) {
        // The client proxy class extends the bean class
        Class<?> current = type;
        while (current != null && current != Object.class) {
            if (extension.isSessionBean(current)) {
                return true;
            }
            current = current.getSuperclass();
        }
        return false;
    }
}

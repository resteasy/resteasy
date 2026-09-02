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

import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;

import org.jboss.resteasy.cdi.i18n.LogMessages;
import org.jboss.resteasy.cdi.i18n.Messages;
import org.jboss.resteasy.core.InjectorFactoryImpl;
import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.InjectorFactory;
import org.jboss.resteasy.spi.MethodInjector;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
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
        return new CdiPropertyInjector(delegate.createPropertyInjector(resourceClass, factory), resourceClass,
                sessionBeanInterface, manager);
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
}

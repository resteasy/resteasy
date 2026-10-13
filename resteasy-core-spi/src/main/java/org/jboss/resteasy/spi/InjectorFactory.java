package org.jboss.resteasy.spi;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

import org.jboss.resteasy.spi.metadata.Parameter;
import org.jboss.resteasy.spi.metadata.ResourceClass;
import org.jboss.resteasy.spi.metadata.ResourceConstructor;
import org.jboss.resteasy.spi.metadata.ResourceLocator;
import org.jboss.resteasy.spi.util.PickConstructor;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision: 1 $
 */
public interface InjectorFactory {

    /**
     * Attempts to create a constructor injector for a class without pre-selecting a constructor. This allows injector
     * factories (such as CDI) to handle classes that may not have public constructors.
     * <p>
     * The default implementation uses {@link PickConstructor#pickSingletonConstructor(Class)} to selecta constructor
     * and delegates to {@link #createConstructor(Constructor, ResteasyProviderFactory)}. Implementations can override
     * this to provide custom constructor selection logic.
     * </p>
     *
     * @param clazz           the class to create a constructor injector for
     * @param providerFactory the provider factory
     * @return a constructor injector, or {@code null} if no suitable constructor can be found
     */
    default ConstructorInjector createConstructor(final Class<?> clazz, final ResteasyProviderFactory providerFactory) {
        final Constructor<?> constructor = PickConstructor.pickSingletonConstructor(clazz);
        if (constructor == null) {
            return null;
        }
        return createConstructor(constructor, providerFactory);
    }

    ConstructorInjector createConstructor(Constructor constructor, ResteasyProviderFactory factory);

    PropertyInjector createPropertyInjector(Class resourceClass, ResteasyProviderFactory factory);

    ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, String defaultName,
            Class type, Type genericType, Annotation[] annotations, ResteasyProviderFactory factory);

    ValueInjector createParameterExtractor(Class injectTargetClass, AccessibleObject injectTarget, String defaultName,
            Class type, Type genericType, Annotation[] annotations, boolean useDefault, ResteasyProviderFactory factory);

    ValueInjector createParameterExtractor(Parameter parameter, ResteasyProviderFactory providerFactory);

    MethodInjector createMethodInjector(ResourceLocator method, ResteasyProviderFactory factory);

    PropertyInjector createPropertyInjector(ResourceClass resourceClass, ResteasyProviderFactory providerFactory);

    ConstructorInjector createConstructor(ResourceConstructor constructor, ResteasyProviderFactory providerFactory);

    /**
     * Returns the instance a container proxy delegates to. If the instance is not a proxy, it is returned as is.
     * <p>
     * The container may create the instance when this is invoked, e.g. for a CDI client proxy of a normal scoped bean.
     * </p>
     * <p>
     * The default implementation returns the instance.
     * </p>
     *
     * @param instance the instance, which may be a container proxy
     *
     * @return the instance behind the proxy, or the instance if it is not a container proxy
     *
     * @since 8.0
     */
    default Object unwrapProxy(final Object instance) {
        return instance;
    }

    /**
     * Returns the type a container proxy class delegates to, e.g. the bean class of a CDI client proxy class. This
     * does not create the instance behind the proxy. If the type is not a proxy class, it is returned as is.
     * <p>
     * The default implementation resolves {@linkplain Class#isSynthetic() synthetic} proxy classes, e.g. CDI client
     * proxies and intercepted subclasses, to their superclass. Other types are returned as is. Implementations for
     * containers which do not mark proxy classes as synthetic should override this.
     * </p>
     *
     * @param type the type, which may be a container proxy class
     * @param <T>  the type
     *
     * @return the type the proxy class delegates to, or the type if it is not a proxy class
     *
     * @since 8.0
     */
    default <T> Class<? super T> resolveType(final Class<T> type) {
        return ProxyTypes.resolve(type);
    }
}

package org.jboss.resteasy.core.providerfactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionStage;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Link;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.Variant;
import jakarta.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.specimpl.LinkBuilderImpl;
import org.jboss.resteasy.specimpl.ResponseBuilderImpl;
import org.jboss.resteasy.specimpl.ResteasyUriBuilderImpl;
import org.jboss.resteasy.specimpl.VariantListBuilderImpl;
import org.jboss.resteasy.spi.ConstructorInjector;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.ResteasyProviderFactory;

@SuppressWarnings({ "rawtypes", "unchecked" })
public final class Utils {
    private Utils() {
    }

    static boolean isA(Class target, Class type, Map<Class<?>, Integer> contracts) {
        if (!type.isAssignableFrom(target))
            return false;
        if (contracts == null || contracts.size() == 0)
            return true;
        for (Class<?> contract : contracts.keySet()) {
            if (contract.equals(type))
                return true;
        }
        return false;
    }

    static boolean isA(Object target, Class type, Map<Class<?>, Integer> contracts) {
        return isA(target.getClass(), type, contracts);
    }

    /**
     * Returns the priority of a component. The priority override is used first, then the priority of the contract,
     * then the {@link Priority @Priority} annotation of the component.
     *
     * @param override  the priority override, or {@code null} or {@code -1} if there is none
     * @param contracts the contracts and their priorities, may be {@code null}
     * @param type      the contract to get the priority for
     * @param component the class of the component. If the component is a container proxy, this must be the
     *                  {@linkplain org.jboss.resteasy.spi.InjectorFactory#resolveType(Class) resolved type}.
     *
     * @return the priority of the component
     *
     * @see #getPriority(Class)
     */
    static int getPriority(Integer override, Map<Class<?>, Integer> contracts, Class type, Class<?> component) {
        if (override != null && override != -1)
            return override;
        if (contracts != null) {
            Integer p = contracts.get(type);
            if (p != null)
                return p;
        }
        return getPriority(component);
    }

    /**
     * Returns the value of the {@link Priority @Priority} annotation of the component, or {@link Priorities#USER} if
     * the component is not annotated. Superclasses are not searched.
     * <p>
     * If the component is a container proxy, e.g. a CDI client proxy, the annotation is not present on the proxy
     * class. The {@linkplain org.jboss.resteasy.spi.InjectorFactory#resolveType(Class) resolved type} must be passed
     * instead.
     * </p>
     *
     * @param component the class of the component
     *
     * @return the priority of the component
     */
    public static int getPriority(Class<?> component) {
        Priority priority = component.getAnnotation(Priority.class);
        if (priority == null)
            return Priorities.USER;
        return priority.value();
    }

    public static void injectProperties(ResteasyProviderFactory rpf, Class declaring, Object obj) {
        CompletionStage<Void> propertyStage = rpf.getInjectorFactory().createPropertyInjector(declaring, rpf).inject(obj,
                false);
        if (propertyStage != null) {
            propertyStage.toCompletableFuture()
                    .getNow(null);
        }
    }

    static void injectProperties(ResteasyProviderFactory rpf, Object obj) {
        CompletionStage<Void> propertyStage = rpf.getInjectorFactory()
                .createPropertyInjector(rpf.getInjectorFactory().resolveType(obj.getClass()), rpf).inject(obj, false);
        if (propertyStage != null) {
            propertyStage.toCompletableFuture()
                    .getNow(null);
        }
    }

    static void injectProperties(ResteasyProviderFactory rpf, Object obj, HttpRequest request, HttpResponse response) {
        CompletionStage<Void> propertyStage = rpf.getInjectorFactory()
                .createPropertyInjector(rpf.getInjectorFactory().resolveType(obj.getClass()), rpf)
                .inject(request, response, obj, false);
        if (propertyStage != null)
            propertyStage
                    .toCompletableFuture().getNow(null);
    }

    public static <T> T createProviderInstance(ResteasyProviderFactory rpf, Class<? extends T> clazz) {
        ConstructorInjector constructorInjector = createConstructorInjector(rpf, clazz);

        Object obj = constructorInjector.construct(false);
        if (obj instanceof CompletionStage) {
            obj = ((CompletionStage<Object>) obj).toCompletableFuture().getNow(null);
        }
        return (T) obj;
    }

    private static <T> ConstructorInjector createConstructorInjector(ResteasyProviderFactory rpf, Class<? extends T> clazz) {
        ConstructorInjector constructorInjector = rpf.getInjectorFactory().createConstructor(clazz, rpf);
        if (constructorInjector == null) {
            throw new IllegalArgumentException(
                    Messages.MESSAGES.unableToFindPublicConstructorForProvider(clazz.getName()));
        }
        return constructorInjector;
    }

    static UriBuilder createUriBuilder() {
        return new ResteasyUriBuilderImpl();
    }

    static Response.ResponseBuilder createResponseBuilder() {
        return new ResponseBuilderImpl();
    }

    static Variant.VariantListBuilder createVariantListBuilder() {
        return new VariantListBuilderImpl();
    }

    static Link.Builder createLinkBuilder() {
        return new LinkBuilderImpl();
    }

    static <T> HeaderDelegate<T> createHeaderDelegate(Map<Class<?>, HeaderDelegate> headerDelegates,
            Set<Class<?>> nullDelegates, Class<T> tClass) {
        if (nullDelegates.contains(tClass)) {
            return null;
        }
        Class<?> clazz = tClass;
        while (clazz != null) {
            HeaderDelegate<T> delegate = headerDelegates.get(clazz);
            if (delegate != null) {
                return delegate;
            }
            delegate = createHeaderDelegateFromInterfaces(headerDelegates, clazz.getInterfaces());
            if (delegate != null) {
                return delegate;
            }
            clazz = clazz.getSuperclass();
        }

        HeaderDelegate result = createHeaderDelegateFromInterfaces(headerDelegates, tClass.getInterfaces());
        if (result == null) {
            nullDelegates.add(tClass);
        }
        return result;
    }

    private static <T> HeaderDelegate<T> createHeaderDelegateFromInterfaces(Map<Class<?>, HeaderDelegate> headerDelegates,
            Class<?>[] interfaces) {
        HeaderDelegate<T> delegate = null;
        for (int i = 0; i < interfaces.length; i++) {
            delegate = headerDelegates.get(interfaces[i]);
            if (delegate != null) {
                return delegate;
            }
            delegate = createHeaderDelegateFromInterfaces(headerDelegates, interfaces[i].getInterfaces());
            if (delegate != null) {
                return delegate;
            }
        }
        return null;
    }
}

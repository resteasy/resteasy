/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;

import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.inject.Inject;
import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.client.ClientResponseFilter;
import jakarta.ws.rs.client.RxInvokerProvider;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.container.DynamicFeature;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.ext.ContextResolver;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.ReaderInterceptor;
import jakarta.ws.rs.ext.RuntimeDelegate;
import jakarta.ws.rs.ext.WriterInterceptor;

import org.jboss.resteasy.spi.AsyncClientResponseProvider;
import org.jboss.resteasy.spi.AsyncResponseProvider;
import org.jboss.resteasy.spi.AsyncStreamProvider;
import org.jboss.resteasy.spi.ContextInjector;
import org.jboss.resteasy.spi.concurrent.ThreadContext;
import org.jboss.resteasy.spi.metadata.ResourceClassProcessor;

/**
 * Utility methods for detecting CDI scopes and JAX-RS components.
 *
 * @author Jozef Hartinger
 *
 */
public class Utils {

    private static final List<Class<?>> REST_INTERFACES = List.of(
            ContainerRequestFilter.class,
            ContainerResponseFilter.class,
            ContextResolver.class,
            DynamicFeature.class,
            ExceptionMapper.class,
            Feature.class,
            MessageBodyReader.class,
            MessageBodyWriter.class,
            ParamConverterProvider.class,
            ReaderInterceptor.class,
            WriterInterceptor.class);

    // The contracts a provider is registered for, see ResteasyProviderFactoryImpl.registerProvider(). This must be kept
    // in sync when a contract is added there. StringParameterUnmarshaller is excluded as an instance is created for each
    // parameter, and InjectorFactory is excluded as it's infrastructure rather than an application provider.
    private static final List<Class<?>> PROVIDER_CONTRACTS = List.of(
            // Jakarta REST
            ClientRequestFilter.class,
            ClientResponseFilter.class,
            ContainerRequestFilter.class,
            ContainerResponseFilter.class,
            ContextResolver.class,
            DynamicFeature.class,
            ExceptionMapper.class,
            Feature.class,
            MessageBodyReader.class,
            MessageBodyWriter.class,
            ParamConverterProvider.class,
            ReaderInterceptor.class,
            WriterInterceptor.class,
            // RESTEasy
            AsyncClientResponseProvider.class,
            AsyncResponseProvider.class,
            AsyncStreamProvider.class,
            ContextInjector.class,
            RuntimeDelegate.HeaderDelegate.class,
            ResourceClassProcessor.class,
            RxInvokerProvider.class,
            ThreadContext.class);

    /**
     * Finds out if a given class is decorated with JAX-RS annotations.
     * Interfaces of the class are not scanned for JAX-RS annotations.
     *
     * @param clazz class
     * @return true if a given interface has @Path annotation or if any of its
     *         methods is decorated with @Path annotation or a request method
     *         designator.
     */
    public static boolean isJaxrsAnnotatedClass(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Path.class)) {
            return true;
        }
        if (clazz.isAnnotationPresent(Provider.class)) {
            return true;
        }
        return hasEndpointMethod(clazz);
    }

    /**
     * Checks each method on the class looking for any method annotated with {@link Path @Path} or any of the
     * {@link HttpMethod}'s.
     *
     * @param type the class to process the methods on
     *
     * @return {@code true} if this is determined to be an endpoint method
     */
    public static boolean hasEndpointMethod(final Class<?> type) {
        for (Method method : type.getMethods()) {
            if (method.isAnnotationPresent(Path.class)) {
                return true;
            }
            for (Annotation annotation : method.getAnnotations()) {
                if (annotation.annotationType().isAnnotationPresent(HttpMethod.class)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true if and only if the given class is a JAX-RS root resource or a
     * sub-resource. The class itself as well as its interfaces are scanned for
     * JAX-RS annotations.
     *
     * @param clazz class
     * @return true if the given class is JAX-RS resource or sub-resource
     */
    public static boolean isJaxrsResource(Class<?> clazz) {
        if (isJaxrsAnnotatedClass(clazz)) {
            return true;
        }
        // Check if this implements any known Jakarta REST interfaces
        for (Class<?> intf : REST_INTERFACES) {
            if (intf.isAssignableFrom(clazz)) {
                return true;
            }
        }
        for (Class<?> intf : clazz.getInterfaces()) {
            if (isJaxrsAnnotatedClass(intf)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Find out if a given class is a JAX-RS component
     *
     * @param clazz class
     * @return true if and only if a give class is a JAX-RS resource, provider or
     *         jakarta.ws.rs.core.Application subclass.
     */
    public static boolean isJaxrsComponent(Class<?> clazz) {
        return ((clazz.isAnnotationPresent(Provider.class)) || (isJaxrsResource(clazz))
                || (Application.class.isAssignableFrom(clazz)));
    }

    /**
     * Checks if the type implements a contract a provider can be registered for.
     *
     * @param clazz the type to check
     *
     * @return {@code true} if the type implements a provider contract
     */
    static boolean isProviderContract(final Class<?> clazz) {
        for (Class<?> contract : PROVIDER_CONTRACTS) {
            if (contract.isAssignableFrom(clazz)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Find out if a given annotated type is explicitly bound to a scope.
     *
     * @param annotatedType annotated type
     * @param manager       bean manager
     * @return true if and only if a given annotated type is annotated with a scope
     *         annotation or with a stereotype which (transitively) declares a
     *         scope
     */
    public static boolean isScopeDefined(AnnotatedType<?> annotatedType, BeanManager manager) {
        for (Annotation annotation : annotatedType.getAnnotations()) {
            if (manager.isScope(annotation.annotationType())) {
                return true;
            }
            if (manager.isStereotype(annotation.annotationType())) {
                if (isScopeDefined(annotation.annotationType(), manager)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the annotated type declares a scope, either directly or through a stereotype other than the ones given.
     * This is used to determine if the default scope of the given stereotypes applies to the type.
     *
     * @param annotatedType the annotated type
     * @param manager       the bean manager
     * @param stereotypes   the stereotypes whose default scope is ignored
     *
     * @return {@code true} if the type declares a scope
     */
    @SafeVarargs
    static boolean isScopeDeclared(final AnnotatedType<?> annotatedType, final BeanManager manager,
            final Class<? extends Annotation>... stereotypes) {
        final List<Class<? extends Annotation>> ignored = List.of(stereotypes);
        for (Annotation annotation : annotatedType.getAnnotations()) {
            final Class<? extends Annotation> type = annotation.annotationType();
            if (manager.isScope(type)) {
                return true;
            }
            if (!ignored.contains(type) && manager.isStereotype(type) && isScopeDefined(type, manager)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check for select case of unproxyable bean type.
     * (see CDI 2.0 spec, section 3.11)
     *
     * @param clazz the type to check
     *
     * @return {@code true} if this type cannot be a CDI proxy, otherwise {@code false}
     */
    static boolean isUnproxyableClass(final Class<?> clazz) {
        // Unproxyable bean type: classes which are declared final,
        // or expose final methods,
        // or have no non-private no-args constructor
        return Modifier.isFinal(clazz.getModifiers()) ||
                hasNonPrivateNonStaticFinalMethod(clazz) ||
                !hasValidConstructor(clazz);
    }

    /**
     * Checks if the member is annotated with {@link Inject @Inject}, in which case CDI injects it.
     *
     * @param member the member to check
     *
     * @return {@code true} if the member is annotated with {@code @Inject}
     */
    static boolean isInjectAnnotated(final Member member) {
        return ((AnnotatedElement) member).isAnnotationPresent(Inject.class);
    }

    // Adapted from weld-core-impl:3.0.5.Final's Reflections.getNonPrivateNonStaticFinalMethod()
    private static boolean hasNonPrivateNonStaticFinalMethod(Class<?> type) {
        for (Class<?> clazz = type; clazz != null && clazz != Object.class; clazz = clazz.getSuperclass()) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (Modifier.isFinal(method.getModifiers()) && !Modifier.isPrivate(method.getModifiers())
                        && !Modifier.isStatic(method.getModifiers())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasValidConstructor(final Class<?> clazz) {
        try {
            return !Modifier.isPrivate(clazz.getDeclaredConstructor().getModifiers());
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Find out if a given class is explicitly bound to a scope.
     *
     * @param clazz   class
     * @param manager bean manager
     * @return <code>true</code> if a given class is annotated with a scope
     *         annotation or with a stereotype which (transitively) declares a
     *         scope
     */
    private static boolean isScopeDefined(Class<?> clazz, BeanManager manager) {
        for (Annotation annotation : clazz.getAnnotations()) {
            if (manager.isScope(annotation.annotationType())) {
                return true;
            }
            if (manager.isStereotype(annotation.annotationType())) {
                if (isScopeDefined(annotation.annotationType(), manager)) {
                    return true;
                }
            }
        }
        return false;
    }
}

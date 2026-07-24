/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.core.extractors;

import java.lang.annotation.Annotation;
import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.RuntimeDelegate;

import org.jboss.resteasy.annotations.StringParameterUnmarshallerBinder;
import org.jboss.resteasy.resteasy_jaxrs.i18n.LogMessages;
import org.jboss.resteasy.resteasy_jaxrs.i18n.Messages;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.StringParameterUnmarshaller;
import org.jboss.resteasy.spi.util.Types;
import org.jboss.resteasy.util.StringToPrimitive;

/**
 * Converts string parameter values from an HTTP request into their target Java types.
 * <p>
 * Handles single values, arrays, and collections ({@link List}, {@link Set}, {@link SortedSet} and their common
 * concrete subtypes). Conversion is resolved once at construction time via a priority chain:
 * </p>
 * <ol>
 * <li>{@link ParamConverter} registered with the {@link ResteasyProviderFactory}</li>
 * <li>{@link org.jboss.resteasy.spi.StringParameterUnmarshaller StringParameterUnmarshaller}</li>
 * <li>{@link StringParameterUnmarshallerBinder} meta-annotation</li>
 * <li>{@link RuntimeDelegate.HeaderDelegate} (for {@code @HeaderParam} only)</li>
 * <li>Public single-{@code String} constructor</li>
 * <li>Static {@code fromString} / {@code valueOf} method</li>
 * <li>Primitive type conversion</li>
 * </ol>
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
class StringParameterConverter {

    private static final ParamConverter<Character> CHARACTER_PARAM_CONVERTER = new ParamConverter<>() {
        @Override
        public Character fromString(final String value) {
            return (value != null && value.length() == 1) ? value.charAt(0) : null;
        }

        @Override
        public String toString(final Character value) {
            return null;
        }
    };

    private final Class<?> baseType;
    private final String signature;
    private final String defaultValue;
    private final boolean isArray;
    private final CollectionHandler collectionHandler;
    private final StringConverter converter;
    private final BiFunction<String, Throwable, WebApplicationException> exceptionMapper;

    private StringParameterConverter(final Class<?> baseType, final String paramName, final Class<?> paramAnnotation,
            final String defaultValue, final boolean isArray, final CollectionHandler collectionHandler,
            final StringConverter converter,
            final BiFunction<String, Throwable, WebApplicationException> exceptionMapper) {
        this.baseType = baseType;
        this.signature = (paramAnnotation != null ? paramAnnotation.getName() : "") + "(\"" + paramName + "\")";
        this.defaultValue = defaultValue;
        this.isArray = isArray;
        this.collectionHandler = collectionHandler;
        this.converter = converter;
        this.exceptionMapper = exceptionMapper != null ? exceptionMapper : BadRequestException::new;
    }

    /**
     * Creates a converter for the given injection type and annotations.
     *
     * @param injectionType   the target type to convert to (may be a collection, array, or scalar)
     * @param genericType     the generic type of the injection point, used to resolve collection element types
     * @param paramAnnotation the parameter annotation type (e.g. {@code @PathParam}, {@code @QueryParam}), or {@code null}
     * @param paramName       the parameter name, used in error messages
     * @param defaultValue    the default value to use when no request value is present, or {@code null}
     * @param annotations     all annotations on the injection point
     * @param factory         the provider factory used to look up {@link ParamConverter ParamConverters} and other
     *                        conversion mechanisms
     *
     * @return a converter for extracting and converting string parameter values
     *
     * @throws RuntimeException if no suitable conversion mechanism can be found for the target type
     */
    static StringParameterConverter of(final Class<?> injectionType, final Type genericType,
            final Class<? extends Annotation> paramAnnotation,
            final String paramName, final String defaultValue, final Set<Annotation> annotations,
            final ResteasyProviderFactory factory) {
        return of(injectionType, genericType, paramAnnotation, paramName, defaultValue, annotations, factory,
                BadRequestException::new);
    }

    /**
     * Creates a converter for the given injection type and annotations.
     *
     * @param injectionType   the target type to convert to (may be a collection, array, or scalar)
     * @param genericType     the generic type of the injection point, used to resolve collection element types
     * @param paramAnnotation the parameter annotation type (e.g. {@code @PathParam}, {@code @QueryParam}), or {@code null}
     * @param paramName       the parameter name, used in error messages
     * @param defaultValue    the default value to use when no request value is present, or {@code null}
     * @param annotations     all annotations on the injection point
     * @param factory         the provider factory used to look up {@link ParamConverter ParamConverters} and other
     *                        conversion mechanisms
     * @param exceptionMapper maps conversion errors to a {@link WebApplicationException}, or {@code null} to default to
     *                        {@link BadRequestException}
     *
     * @return a converter for extracting and converting string parameter values
     *
     * @throws RuntimeException if no suitable conversion mechanism can be found for the target type
     */
    static StringParameterConverter of(final Class<?> injectionType, final Type genericType,
            final Class<? extends Annotation> paramAnnotation,
            final String paramName, final String defaultValue, final Set<Annotation> annotations,
            final ResteasyProviderFactory factory,
            final BiFunction<String, Throwable, WebApplicationException> exceptionMapper) {

        final Annotation[] annotationArray = annotations.toArray(new Annotation[0]);

        // Try the raw injection type first. This gives ParamConverterProviders (e.g.
        // MultiValuedParamConverterProvider for @Separator) a chance to handle the
        // full collection/array type before we decompose it into a component type.
        final ParamConverter<?> rawConverter = factory.getParamConverter(injectionType, genericType, annotationArray);
        if (rawConverter != null) {
            return new StringParameterConverter(injectionType, paramName, paramAnnotation,
                    defaultValue, false, null, rawConverter::fromString, exceptionMapper);
        }

        final boolean isArray = injectionType.isArray();
        Class<?> baseType = isArray ? injectionType.getComponentType() : injectionType;
        Type baseGenericType = genericType;
        CollectionHandler collectionHandler = null;

        if (!isArray) {
            collectionHandler = CollectionHandler.of(injectionType);
            if (collectionHandler != null) {
                if (genericType instanceof ParameterizedType) {
                    final ParameterizedType parameterizedType = (ParameterizedType) baseGenericType;
                    baseType = Types.getRawType(parameterizedType.getActualTypeArguments()[0]);
                    baseGenericType = parameterizedType.getActualTypeArguments()[0];
                } else {
                    baseType = String.class;
                    baseGenericType = null;
                }
            }
        }

        final StringConverter converter = resolveConverter(baseType, baseGenericType, paramAnnotation,
                annotationArray, factory);
        if (converter == null) {
            final String signature = (paramAnnotation != null ? paramAnnotation.getName() : "") + "(\"" + paramName + "\")";
            throw Messages.MESSAGES.unableToFindStringConstructor(signature, baseType.getName());
        }
        return new StringParameterConverter(baseType, paramName, paramAnnotation, defaultValue,
                isArray, collectionHandler, converter, exceptionMapper);
    }

    /**
     * Extracts and converts multiple string values into the target type. For array or collection targets, each string
     * is individually converted and collected. For scalar targets, only the first value is used.
     *
     * @param value the raw string values from the request, or {@code null} if the parameter was absent
     *
     * @return the converted value (scalar, array, or collection), or {@code null} if no value and no default
     */
    Object extractValues(final List<String> value) {
        List<String> valueToConvert = value;
        if (valueToConvert == null && (isArray || collectionHandler != null) && defaultValue != null) {
            valueToConvert = Collections.singletonList(defaultValue);
        } else if (valueToConvert == null) {
            valueToConvert = Collections.emptyList();
        }

        if (isArray) {
            final Object vals = Array.newInstance(baseType, valueToConvert.size());
            for (int i = 0; i < valueToConvert.size(); i++) {
                Array.set(vals, i, extractValue(valueToConvert.get(i)));
            }
            return vals;
        } else if (collectionHandler != null) {
            final Collection<Object> collection = collectionHandler.create();
            for (final String str : valueToConvert) {
                collection.add(extractValue(str));
            }
            return collectionHandler.finish(collection);
        } else {
            return extractValue(valueToConvert.isEmpty() ? null : valueToConvert.get(0));
        }
    }

    /**
     * Converts a single string value into the target type. Falls back to the configured default value when
     * {@code value} is {@code null}.
     *
     * @param value the raw string value, or {@code null}
     *
     * @return the converted value, or {@code null} if both the value and default are absent
     */
    Object extractValue(final String value) {
        String valueToConvert = value;
        if (valueToConvert == null) {
            if (defaultValue == null) {
                return StringToPrimitive.isPrimitive(baseType)
                        ? StringToPrimitive.stringToPrimitiveBoxType(baseType, null)
                        : null;
            }
            valueToConvert = defaultValue;
        }

        try {
            return converter.convert(valueToConvert);
        } catch (WebApplicationException wae) {
            throw wae;
        } catch (Exception e) {
            handleException(e, valueToConvert);
        }
        return null;
    }

    /**
     * Returns {@code true} if the target type is either a collection or an array.
     *
     * @return {@code true} if this is a collection or array, otherwise {@code false}
     */
    boolean isCollectionOrArray() {
        return collectionHandler != null || isArray;
    }

    private void handleException(final Throwable e, final String strVal) {
        Throwable targetException = e;
        if (targetException instanceof InvocationTargetException ite) {
            targetException = ite.getTargetException();
        }
        // Passed null for the AccessibleObject target since it has been removed
        LogMessages.LOGGER.unableToExtractParameter(targetException, signature, strVal, null);
        if (converter.allowNullOrBlank() && e instanceof InvocationTargetException) {
            if (strVal != null && strVal.isBlank()) {
                return;
            }
        }
        if (targetException instanceof WebApplicationException wae) {
            throw wae;
        }
        throw exceptionMapper.apply(Messages.MESSAGES.unableToExtractParameter(signature, encode(strVal)), targetException);
    }

    private String encode(final String strVal) {
        return strVal == null ? "null" : URLEncoder.encode(strVal, StandardCharsets.UTF_8);
    }

    /**
     * Resolves a {@link StringConverter} for the given component type by walking the conversion priority chain.
     *
     * @return the resolved converter, or {@code null} if no conversion mechanism was found
     */
    private static StringConverter resolveConverter(
            final Class<?> baseType, final Type baseGenericType, final Class<?> paramAnnotation,
            final Annotation[] annotations, final ResteasyProviderFactory factory) {

        final ParamConverter<?> paramConverter = factory.getParamConverter(baseType, baseGenericType, annotations);
        if (paramConverter != null) {
            return new StringConverter() {
                @Override
                public Object convert(final String value) {
                    return paramConverter.fromString(value);
                }

                @Override
                public boolean allowNullOrBlank() {
                    return false;
                }
            };
        }

        final StringParameterUnmarshaller<?> unmarshaller = factory.createStringParameterUnmarshaller(baseType);
        if (unmarshaller != null) {
            unmarshaller.setAnnotations(annotations);
            return unmarshaller::fromString;
        }

        for (final Annotation annotation : annotations) {
            final StringParameterUnmarshallerBinder binder = annotation.annotationType()
                    .getAnnotation(StringParameterUnmarshallerBinder.class);
            if (binder != null) {
                try {
                    final StringParameterUnmarshaller<?> boundUnmarshaller = binder.value().getDeclaredConstructor()
                            .newInstance();
                    factory.injectProperties(boundUnmarshaller);
                    boundUnmarshaller.setAnnotations(annotations);
                    return boundUnmarshaller::fromString;
                } catch (final Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

        if (HeaderParam.class.equals(paramAnnotation)
                || org.jboss.resteasy.annotations.jaxrs.HeaderParam.class.equals(paramAnnotation)) {
            final RuntimeDelegate.HeaderDelegate<?> delegate = factory.getHeaderDelegate(baseType);
            if (delegate != null) {
                return delegate::fromString;
            }
        }

        try {
            final Constructor<?> constructor = baseType.getConstructor(String.class);
            if (Modifier.isPublic(constructor.getModifiers())) {
                return createFastConstructor(constructor);
            }
        } catch (final NoSuchMethodException ignored) {
        }

        Method valueOf = null;
        Method fromString = null;
        try {
            final Method fromValue = baseType.getDeclaredMethod("fromValue", String.class);
            if (Modifier.isPublic(fromValue.getModifiers())) {
                for (final Annotation ann : baseType.getAnnotations()) {
                    if (ann.annotationType().getName().equals("jakarta.xml.bind.annotation.XmlEnum")) {
                        valueOf = fromValue;
                    }
                }
            }
        } catch (final NoSuchMethodException ignored) {
        }

        if (StringToPrimitive.isPrimitive(baseType)) {
            return val -> StringToPrimitive.stringToPrimitiveBoxType(baseType, val);
        }

        if (valueOf == null) {
            try {
                final Method fs = baseType.getDeclaredMethod("fromString", String.class);
                if (Modifier.isStatic(fs.getModifiers()))
                    fromString = fs;
            } catch (final NoSuchMethodException ignored) {
            }
            try {
                final Method vo = baseType.getDeclaredMethod("valueOf", String.class);
                if (Modifier.isStatic(vo.getModifiers()))
                    valueOf = vo;
            } catch (final NoSuchMethodException ignored) {
            }
        }

        if (baseType.isEnum()) {
            // Enums need to prefer fromString(String) over valueOf(String)
            final Method chosen = fromString != null ? fromString : valueOf;
            if (chosen != null) {
                return createFastMethodInvoker(chosen);
            }
        } else {
            // Other types prefer valueOf(String) over fromString(value)
            final Method chosen = valueOf != null ? valueOf : fromString;
            if (chosen != null) {
                return createFastMethodInvoker(chosen);
            }
        }

        if (Character.class.equals(baseType)) {
            return CHARACTER_PARAM_CONVERTER::fromString;
        }

        return null;
    }

    /**
     * Creates a {@link StringConverter} backed by a static method, using {@link LambdaMetafactory} for near-direct
     * invocation speed. Falls back to reflective invocation if metafactory linkage fails.
     */
    private static StringConverter createFastMethodInvoker(final Method method) {
        try {
            final MethodHandles.Lookup lookup = MethodHandles.publicLookup();
            final MethodHandle handle = lookup.unreflect(method);
            final CallSite site = LambdaMetafactory.metafactory(
                    lookup,
                    "convert",
                    MethodType.methodType(StringConverter.class),
                    MethodType.methodType(Object.class, String.class),
                    handle,
                    MethodType.methodType(method.getReturnType(), String.class));
            return (StringConverter) site.getTarget().invokeExact();
        } catch (final Throwable t) {
            return val -> method.invoke(null, val);
        }
    }

    /**
     * Creates a {@link StringConverter} backed by a single-{@link String} constructor, using
     * {@link LambdaMetafactory} for near-direct invocation speed. Falls back to reflective invocation if metafactory
     * linkage fails.
     */
    private static StringConverter createFastConstructor(final Constructor<?> constructor) {
        try {
            final MethodHandles.Lookup lookup = MethodHandles.publicLookup();
            final MethodHandle handle = lookup.unreflectConstructor(constructor);
            final CallSite site = LambdaMetafactory.metafactory(
                    lookup,
                    "convert",
                    MethodType.methodType(StringConverter.class),
                    MethodType.methodType(Object.class, String.class),
                    handle,
                    MethodType.methodType(constructor.getDeclaringClass(), String.class));
            return (StringConverter) site.getTarget().invokeExact();
        } catch (final Throwable t) {
            return constructor::newInstance;
        }
    }

    /**
     * Encapsulates the creation and post-processing of a collection for a given target type. For interface types
     * ({@link List}, {@link Set}, {@link SortedSet}), the finisher wraps the collection as unmodifiable. For concrete
     * types ({@link ArrayList}, {@link HashSet}, {@link TreeSet}), the mutable collection is returned as-is.
     */
    private static final class CollectionHandler {
        private final Supplier<Collection<Object>> factory;
        private final Function<Collection<Object>, Collection<?>> finisher;

        private CollectionHandler(final Supplier<Collection<Object>> factory,
                final Function<Collection<Object>, Collection<?>> finisher) {
            this.factory = factory;
            this.finisher = finisher;
        }

        static CollectionHandler of(final Class<?> type) {
            if (List.class.equals(type)) {
                return new CollectionHandler(ArrayList::new,
                        c -> Collections.unmodifiableList((List<?>) c));
            }
            if (ArrayList.class.equals(type)) {
                return new CollectionHandler(ArrayList::new, c -> c);
            }
            if (SortedSet.class.equals(type)) {
                return new CollectionHandler(TreeSet::new,
                        c -> Collections.unmodifiableSortedSet((SortedSet<?>) c));
            }
            if (TreeSet.class.equals(type)) {
                return new CollectionHandler(TreeSet::new, c -> c);
            }
            if (Set.class.equals(type)) {
                return new CollectionHandler(HashSet::new,
                        c -> Collections.unmodifiableSet((Set<?>) c));
            }
            if (HashSet.class.equals(type)) {
                return new CollectionHandler(HashSet::new, c -> c);
            }
            return null;
        }

        Collection<Object> create() {
            return factory.get();
        }

        Collection<?> finish(final Collection<Object> collection) {
            return finisher.apply(collection);
        }
    }

    /**
     * Converts a single string value into the target type. Resolved once at construction time and invoked per-request.
     */
    @FunctionalInterface
    private interface StringConverter {
        Object convert(String value) throws Exception;

        /**
         * Indicates that if a string value is {@code null} or an {@linkplain String#isBlank() blank} string that no
         * exception is thrown when the converter cannot invoke its method or constructor.
         * <p>
         * The default is {@code true} allowing failed invocations to pass and not throw an exception
         * </p>
         *
         * @return {@code true} if exceptions should not be thrown on failed blank or null strings, otherwise
         *         {@code false} will throw an exception if an {@link InvocationTargetException} is thrown
         */
        default boolean allowNullOrBlank() {
            return true;
        }
    }
}

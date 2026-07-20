/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.decorator.Decorator;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.UnsatisfiedResolutionException;
import jakarta.enterprise.inject.literal.InjectLiteral;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.Annotated;
import jakarta.enterprise.inject.spi.AnnotatedConstructor;
import jakarta.enterprise.inject.spi.AnnotatedField;
import jakarta.enterprise.inject.spi.AnnotatedMethod;
import jakarta.enterprise.inject.spi.AnnotatedParameter;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeforeBeanDiscovery;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;
import jakarta.enterprise.inject.spi.ProcessInjectionPoint;
import jakarta.enterprise.inject.spi.WithAnnotations;
import jakarta.enterprise.inject.spi.configurator.AnnotatedConstructorConfigurator;
import jakarta.enterprise.inject.spi.configurator.AnnotatedFieldConfigurator;
import jakarta.enterprise.inject.spi.configurator.AnnotatedMethodConfigurator;
import jakarta.enterprise.inject.spi.configurator.AnnotatedParameterConfigurator;
import jakarta.enterprise.inject.spi.configurator.AnnotatedTypeConfigurator;
import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.enterprise.util.Nonbinding;
import jakarta.inject.Inject;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.Encoded;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.MatrixParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

import org.jboss.resteasy.cdi.i18n.Messages;
import org.jboss.resteasy.core.FormInjector;
import org.jboss.resteasy.core.extractors.ParameterExtractors;
import org.jboss.resteasy.core.extractors.RequestParameterExtractor;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.util.Types;

/**
 * A CDI {@link Extension} that enables Jakarta REST parameter annotations ({@code @CookieParam}, {@code @FormParam},
 * {@code @HeaderParam}, {@code @MatrixParam}, {@code @PathParam}, {@code @QueryParam}, and {@code @BeanParam}) to
 * function as CDI qualifiers. This allows Jakarta REST resource classes to receive parameter values via CDI
 * constructor injection rather than requiring a public no-arg constructor.
 * <p>
 * The extension operates in four CDI lifecycle phases:
 * </p>
 * <ol>
 * <li>{@link BeforeBeanDiscovery} &mdash; registers each {@code @*Param} annotation as a CDI qualifier</li>
 * <li>{@link ProcessAnnotatedType} &mdash; adds {@link Inject @Inject} to {@code @*Param}-annotated fields,
 * setter methods, and constructors so that CDI resolves parameter values automatically. RESTEasy-specific
 * annotations ({@code org.jboss.resteasy.annotations.jaxrs.*Param}) are also handled by adding the corresponding
 * Jakarta REST annotation alongside {@code @Inject}.</li>
 * <li>{@link ProcessInjectionPoint} &mdash; collects the types of all {@code @*Param}-annotated injection points</li>
 * <li>{@link AfterBeanDiscovery} &mdash; registers synthetic beans that produce parameter values at request time
 * by delegating to {@link ParameterExtractors}</li>
 * </ol>
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
public class ResteasyParamCdiExtension implements Extension {
    // Conversion of primitives to their respective object types
    private static final Map<Class<?>, Class<?>> PRIMITIVE_WRAPPERS = Map.of(
            int.class, Integer.class,
            boolean.class, Boolean.class,
            byte.class, Byte.class,
            char.class, Character.class,
            double.class, Double.class,
            float.class, Float.class,
            long.class, Long.class,
            short.class, Short.class);

    private final Set<Type> paramTypes = ConcurrentHashMap.newKeySet();
    private final Map<CacheKey, RequestParameterExtractor> extractorCache = new ConcurrentHashMap<>();
    private final Map<Class<?>, FormInjector> formInjectorCache = new ConcurrentHashMap<>();

    private final Set<Class<? extends Annotation>> paramAnnotations = Set.of(
            BeanParam.class,
            CookieParam.class,
            FormParam.class,
            HeaderParam.class,
            MatrixParam.class,
            PathParam.class,
            QueryParam.class);
    private final boolean enhancedCdiSupportEnabled;

    public ResteasyParamCdiExtension() {
        enhancedCdiSupportEnabled = CdiOptions.ENHANCED_CDI_SUPPORT.getValue();
    }

    /**
     * Registers each {@code @*Param} annotation as a CDI qualifier, marking all annotation members as
     * {@link Nonbinding} so that qualifier resolution matches on presence alone rather than on member values.
     *
     * @param event the before bean discovery event
     */
    public void registerQualifiers(@Observes final BeforeBeanDiscovery event) {
        if (enhancedCdiSupportEnabled) {
            // Register the @*Param annotations as qualifiers
            for (Class<? extends Annotation> param : paramAnnotations) {
                event.configureQualifier(param)
                        .methods()
                        .forEach(method -> method.add(Nonbinding.Literal.INSTANCE));
            }
        }
    }

    /**
     * Observes Jakarta REST resource types and adds {@link Inject @Inject} to fields, setter methods, and constructors
     * annotated with {@code @*Param} annotations so that CDI can resolve parameter values via the synthetic beans
     * registered by this extension. This covers both Jakarta REST annotations ({@code jakarta.ws.rs.*Param}) and
     * RESTEasy-specific annotations ({@code org.jboss.resteasy.annotations.jaxrs.*Param}).
     *
     * @param event the annotated type being processed
     * @param <T>   the type being processed
     */
    public <T> void observeParamResources(@WithAnnotations({ Path.class }) @Observes ProcessAnnotatedType<T> event) {
        if (enhancedCdiSupportEnabled) {
            final AnnotatedType<T> annotatedType = event.getAnnotatedType();
            if (!annotatedType.getJavaClass().isInterface()
                    && !annotatedType.isAnnotationPresent(Decorator.class)
                    && !Utils.isUnproxyableClass(annotatedType.getJavaClass())) {
                addInject(event);
            }
        }
    }

    /**
     * Adds {@link Inject @Inject} to {@code @*Param}-annotated injection points so that CDI can inject parameter
     * values via the synthetic producer beans registered by this extension.
     * <p>
     * For Jakarta REST annotations ({@code jakarta.ws.rs.*Param}), only {@code @Inject} is added since these
     * annotations are already registered as CDI qualifiers. For RESTEasy-specific annotations
     * ({@code org.jboss.resteasy.annotations.jaxrs.*Param}), the corresponding Jakarta REST annotation is also
     * added so that the existing CDI qualifier and producer infrastructure handles them.
     * </p>
     */
    private void addInject(final ProcessAnnotatedType<?> pat) {
        final AnnotatedTypeConfigurator<?> configurator = pat.configureAnnotatedType();

        // Fields
        for (AnnotatedFieldConfigurator<?> fieldConfig : configurator.fields()) {
            final AnnotatedField<?> field = fieldConfig.getAnnotated();
            if (field.isAnnotationPresent(Inject.class)) {
                continue;
            }
            if (paramAnnotations.stream().anyMatch(field::isAnnotationPresent)) {
                fieldConfig.add(InjectLiteral.INSTANCE);
            } else {
                final Annotation jakartaEquiv = toJakartaParam(field);
                if (jakartaEquiv != null) {
                    fieldConfig.add(InjectLiteral.INSTANCE);
                    fieldConfig.add(jakartaEquiv);
                }
            }
        }

        // Setter methods
        for (AnnotatedMethodConfigurator<?> methodConfig : configurator.methods()) {
            final AnnotatedMethod<?> method = methodConfig.getAnnotated();
            if (method.isAnnotationPresent(Inject.class)) {
                continue;
            }
            if (paramAnnotations.stream().anyMatch(method::isAnnotationPresent)) {
                methodConfig.add(InjectLiteral.INSTANCE);
            } else {
                final Annotation jakartaEquiv = toJakartaParam(method);
                if (jakartaEquiv != null) {
                    methodConfig.add(InjectLiteral.INSTANCE);
                    methodConfig.add(jakartaEquiv);
                }
            }
        }

        // Constructors
        for (AnnotatedConstructorConfigurator<?> ctorConfig : configurator.constructors()) {
            final AnnotatedConstructor<?> ctor = ctorConfig.getAnnotated();
            if (ctor.isAnnotationPresent(Inject.class)) {
                continue;
            }
            final boolean hasParamAnnotation = ctor.getParameters().stream()
                    .anyMatch(p -> paramAnnotations.stream().anyMatch(p::isAnnotationPresent)
                            || toJakartaParam(p) != null);
            if (hasParamAnnotation) {
                ctorConfig.add(InjectLiteral.INSTANCE);
                for (AnnotatedParameterConfigurator<?> paramConfig : ctorConfig.params()) {
                    final Annotation jakartaEquiv = toJakartaParam(paramConfig.getAnnotated());
                    if (jakartaEquiv != null) {
                        paramConfig.add(jakartaEquiv);
                    }
                }
            }
        }
    }

    /**
     * If the annotated element has a RESTEasy-specific {@code @*Param} annotation, returns the corresponding
     * Jakarta REST annotation literal with the same value. Returns {@code null} if no RESTEasy annotation is present.
     */
    private static Annotation toJakartaParam(final Annotated annotated) {
        final var cookie = annotated.getAnnotation(org.jboss.resteasy.annotations.jaxrs.CookieParam.class);
        if (cookie != null) {
            return new CookieParamLiteral(cookie.value());
        }
        final var form = annotated.getAnnotation(org.jboss.resteasy.annotations.jaxrs.FormParam.class);
        if (form != null) {
            return new FormParamLiteral(form.value());
        }
        final var header = annotated.getAnnotation(org.jboss.resteasy.annotations.jaxrs.HeaderParam.class);
        if (header != null) {
            return new HeaderParamLiteral(header.value());
        }
        final var matrix = annotated.getAnnotation(org.jboss.resteasy.annotations.jaxrs.MatrixParam.class);
        if (matrix != null) {
            return new MatrixParamLiteral(matrix.value());
        }
        final var path = annotated.getAnnotation(org.jboss.resteasy.annotations.jaxrs.PathParam.class);
        if (path != null) {
            return new PathParamLiteral(path.value());
        }
        final var query = annotated.getAnnotation(org.jboss.resteasy.annotations.jaxrs.QueryParam.class);
        if (query != null) {
            return new QueryParamLiteral(query.value());
        }
        return null;
    }

    /**
     * Registers the {@code @*Param} annotations as CDI qualifiers with producers.
     *
     * @param afd the after bean discovery event
     */
    public void registerParamQualifiers(@Observes final AfterBeanDiscovery afd) {
        // Register synthetic beans for the @*Param annotated injection points
        for (Type type : paramTypes) {
            // Register the bean with the BeanParam qualifier
            afd.addBean()
                    .addType(type)
                    .scope(Dependent.class)
                    .addQualifier(BeanParamLiteral.DEFAULT)
                    .produceWith(instance -> {
                        final InjectionPoint ip = instance.select(InjectionPoint.class).get();
                        final Type beanParamType = ip.getType();
                        if (!(beanParamType instanceof Class<?> beanParamClass)) {
                            return null;
                        }
                        // If the type is a CDI-managed bean, let CDI create and inject it
                        try {
                            return instance.select(beanParamClass).get();
                        } catch (UnsatisfiedResolutionException e) {
                            // Fall back to FormInjector for non-CDI-managed types
                            final ResteasyProviderFactory rpf = ResteasyProviderFactory.getInstance();
                            final HttpRequest request = rpf.getContextData(HttpRequest.class);
                            if (request == null) {
                                return null;
                            }
                            final FormInjector formInjector = formInjectorCache.computeIfAbsent(beanParamClass,
                                    (ignored) -> new FormInjector(beanParamClass, rpf));
                            return formInjector.inject(request, null, false);
                        }
                    });
            // Register the bean with the CookieParam qualifier
            afd.addBean()
                    .addType(type)
                    .scope(Dependent.class)
                    .addQualifier(CookieParamLiteral.DEFAULT)
                    .produceWith(instance -> {
                        // Get the injection point on where to
                        final InjectionPoint ip = instance.select(InjectionPoint.class).get();
                        final CookieParam param = findAnnotation(ip, CookieParam.class);
                        final String name = resolveName(ip.getAnnotated(), param.value(), CookieParam.class);
                        return extractParam(ip, CookieParam.class, name);
                    });
            // Register the bean with the FormParam qualifier
            afd.addBean()
                    .addType(type)
                    .scope(Dependent.class)
                    .addQualifier(FormParamLiteral.DEFAULT)
                    .produceWith(instance -> {
                        // Get the injection point on where to
                        final InjectionPoint ip = instance.select(InjectionPoint.class).get();
                        final FormParam param = findAnnotation(ip, FormParam.class);
                        final String name = resolveName(ip.getAnnotated(), param.value(), FormParam.class);
                        return extractParam(ip, FormParam.class, name);
                    });
            // Register the bean with the HeaderParam qualifier
            afd.addBean()
                    .addType(type)
                    .scope(Dependent.class)
                    .addQualifier(HeaderParamLiteral.DEFAULT)
                    .produceWith(instance -> {
                        // Get the injection point on where to
                        final InjectionPoint ip = instance.select(InjectionPoint.class).get();
                        final HeaderParam param = findAnnotation(ip, HeaderParam.class);
                        final String name = resolveName(ip.getAnnotated(), param.value(), HeaderParam.class);
                        return extractParam(ip, HeaderParam.class, name);
                    });
            // Register the bean with the MatrixParam qualifier
            afd.addBean()
                    .addType(type)
                    .scope(Dependent.class)
                    .addQualifier(MatrixParamLiteral.DEFAULT)
                    .produceWith(instance -> {
                        // Get the injection point on where to
                        final InjectionPoint ip = instance.select(InjectionPoint.class).get();
                        final MatrixParam param = findAnnotation(ip, MatrixParam.class);
                        final String name = resolveName(ip.getAnnotated(), param.value(), MatrixParam.class);
                        return extractParam(ip, MatrixParam.class, name);
                    });
            // Register the bean with the PathParam qualifier
            afd.addBean()
                    .addType(type)
                    .scope(Dependent.class)
                    .addQualifier(PathParamLiteral.DEFAULT)
                    .produceWith(instance -> {
                        // Get the injection point on where to
                        final InjectionPoint ip = instance.select(InjectionPoint.class).get();
                        final PathParam param = findAnnotation(ip, PathParam.class);
                        final String name = resolveName(ip.getAnnotated(), param.value(), PathParam.class);
                        return extractParam(ip, PathParam.class, name);
                    });
            // Register the bean with the Query qualifier
            afd.addBean()
                    .addType(type)
                    .scope(Dependent.class)
                    .addQualifier(QueryParamLiteral.DEFAULT)
                    .produceWith(instance -> {
                        // Get the injection point on where to
                        final InjectionPoint ip = instance.select(InjectionPoint.class).get();
                        final QueryParam param = findAnnotation(ip, QueryParam.class);
                        final String name = resolveName(ip.getAnnotated(), param.value(), QueryParam.class);
                        return extractParam(ip, QueryParam.class, name);
                    });
        }
        // We no longer need the paramTypes
        paramTypes.clear();
    }

    /**
     * Observes each injection point during bean discovery and records the types of any injection points annotated
     * with a {@code @*Param} qualifier. These types are later used to register synthetic producer beans.
     *
     * @param pip the process injection point event
     * @param <T> the bean class of the bean that declares the injection point
     * @param <X> the declared type of the injection point
     */
    public <T, X> void processInjectionPoint(@Observes final ProcessInjectionPoint<T, X> pip) {
        if (enhancedCdiSupportEnabled) {
            final InjectionPoint ip = pip.getInjectionPoint();
            final Annotated annotated = ip.getAnnotated();
            for (Class<? extends Annotation> type : paramAnnotations) {
                if (annotated.isAnnotationPresent(type)) {
                    addParamType(ip.getType());
                    return;
                }
            }
            // Check if this is a setter parameter whose method has the annotation
            if (annotated instanceof AnnotatedParameter<?> param) {
                final Annotated method = param.getDeclaringCallable();
                for (Class<? extends Annotation> type : paramAnnotations) {
                    final Annotation ann = method.getAnnotation(type);
                    if (ann != null) {
                        pip.configureInjectionPoint().addQualifier(ann);
                        addParamType(ip.getType());
                        return;
                    }
                }
            }
        }
    }

    /**
     * Adds the given type to the set of parameter types, normalizing primitive types to their wrapper equivalents.
     * CDI treats primitive types and their corresponding wrapper types as identical for bean resolution, so registering
     * both would produce ambiguous dependencies.
     *
     * @param type the injection point type to add
     */
    private void addParamType(final Type type) {
        if (type instanceof Class<?> clazz && clazz.isPrimitive() && PRIMITIVE_WRAPPERS.containsKey(clazz)) {
            paramTypes.add(PRIMITIVE_WRAPPERS.get(clazz));
        } else {
            paramTypes.add(type);
        }
    }

    /**
     * Extracts a parameter value from the current HTTP request for the given injection point. The
     * {@link RequestParameterExtractor} is resolved once and cached for subsequent requests.
     *
     * @param injectionPoint the CDI injection point requesting the value
     * @param annotationType the parameter annotation type (e.g. {@code QueryParam.class})
     * @param paramName      the resolved parameter name
     *
     * @return the extracted and converted parameter value, or {@code null} if the request or type is unavailable
     */
    private Object extractParam(final InjectionPoint injectionPoint, final Class<? extends Annotation> annotationType,
            final String paramName) {
        final Type genericType = injectionPoint.getType();
        final ResteasyProviderFactory rpf = ResteasyProviderFactory.getInstance();
        final HttpRequest request = rpf.getContextData(HttpRequest.class);
        if (request == null) {
            return null;
        }
        final Class<?> rawType = Types.getRawTypeNoException(genericType);
        if (rawType == null) {
            return null;
        }
        final Bean<?> bean = injectionPoint.getBean();
        final boolean encode = injectionPoint.getAnnotated().isAnnotationPresent(Encoded.class) ||
                (bean != null && bean.getBeanClass().isAnnotationPresent(Encoded.class));
        final String defaultValue = findDefaultValue(injectionPoint.getAnnotated());
        final CacheKey key = new CacheKey(annotationType, paramName, genericType, encode, defaultValue);
        final Set<Annotation> annotations = injectionPoint.getQualifiers();
        final RequestParameterExtractor extractor = extractorCache.computeIfAbsent(key,
                (current) -> ParameterExtractors.of(rawType, genericType, annotations, encode,
                        paramName, defaultValue, annotationType, rpf));
        return extractor.extract(request);
    }

    private static String findDefaultValue(final Annotated annotated) {
        if (annotated instanceof AnnotatedParameter<?> annotatedParameter) {
            final Parameter parameter = annotatedParameter.getJavaParameter();
            DefaultValue defaultValue = parameter.getAnnotation(DefaultValue.class);
            if (defaultValue != null) {
                return defaultValue.value();
            }
            // Let's see if this parent member is a method, if so check for the annotation on there
            if (annotatedParameter.getDeclaringCallable() instanceof AnnotatedMethod<?> annotatedMethod) {
                defaultValue = annotatedMethod.getAnnotation(DefaultValue.class);
                if (defaultValue != null) {
                    return defaultValue.value();
                }
            }
        } else if (annotated instanceof AnnotatedField<?> annotatedField) {
            final DefaultValue defaultValue = annotatedField.getAnnotation(DefaultValue.class);
            if (defaultValue != null) {
                return defaultValue.value();
            }
        }
        return null;
    }

    private static String resolveName(final Annotated annotated, final String definedName,
            final Class<? extends Annotation> annotationType) {
        // If the name is not defined on the annotation, attempt to resolve the name from the parameter or field
        if (definedName.isBlank()) {
            if (annotated instanceof AnnotatedParameter<?> annotatedParameter) {
                final Parameter parameter = annotatedParameter.getJavaParameter();
                // Check if the annotation is on the parent
                if (parameter.isAnnotationPresent(annotationType)) {
                    if (parameter.isNamePresent()) {
                        return parameter.getName();
                    }
                } else {
                    // Let's see if this parent member is a method, if so check for the annotation on there
                    if (annotatedParameter.getDeclaringCallable() instanceof AnnotatedMethod<?> annotatedMethod) {
                        final String methodName = annotatedMethod.getJavaMember().getName();
                        final int len = methodName.length();
                        if (len > 3 && methodName.startsWith("set")) {
                            if (len == 4) {
                                return String.valueOf(Character.toLowerCase(methodName.charAt(3)));
                            }
                            return Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
                        }
                    }
                }
            } else if (annotated instanceof AnnotatedField<?> annotatedField) {
                return annotatedField.getJavaMember().getName();
            }
            throw Messages.MESSAGES.cannotResolveParamName(annotationType.getName(), definedName);
        }
        return definedName;
    }

    private static <T extends Annotation> T findAnnotation(final InjectionPoint ip, final Class<T> annotationType) {
        for (Annotation annotation : ip.getQualifiers()) {
            if (annotation.annotationType() == annotationType) {
                return annotationType.cast(annotation);
            }
        }
        throw Messages.MESSAGES.failedToFindAnnotation(annotationType.getName(), ip.getAnnotated().toString());
    }

    @SuppressWarnings("ClassExplicitlyAnnotation")
    private static class BeanParamLiteral extends AnnotationLiteral<BeanParam> implements BeanParam {
        static final BeanParam DEFAULT = new BeanParamLiteral();

        private BeanParamLiteral() {
        }
    }

    @SuppressWarnings("ClassExplicitlyAnnotation")
    private static class CookieParamLiteral extends AnnotationLiteral<CookieParam> implements CookieParam {
        static final CookieParam DEFAULT = new CookieParamLiteral("");
        private final String value;

        private CookieParamLiteral(final String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }
    }

    @SuppressWarnings("ClassExplicitlyAnnotation")
    private static class FormParamLiteral extends AnnotationLiteral<FormParam> implements FormParam {
        static final FormParam DEFAULT = new FormParamLiteral("");
        private final String value;

        private FormParamLiteral(final String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }
    }

    @SuppressWarnings("ClassExplicitlyAnnotation")
    private static class HeaderParamLiteral extends AnnotationLiteral<HeaderParam> implements HeaderParam {
        static final HeaderParam DEFAULT = new HeaderParamLiteral("");
        private final String value;

        private HeaderParamLiteral(final String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }
    }

    @SuppressWarnings("ClassExplicitlyAnnotation")
    private static class MatrixParamLiteral extends AnnotationLiteral<MatrixParam> implements MatrixParam {
        static final MatrixParam DEFAULT = new MatrixParamLiteral("");
        private final String value;

        private MatrixParamLiteral(final String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }
    }

    @SuppressWarnings("ClassExplicitlyAnnotation")
    private static class PathParamLiteral extends AnnotationLiteral<PathParam> implements PathParam {
        static final PathParam DEFAULT = new PathParamLiteral("");
        private final String value;

        private PathParamLiteral(final String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }
    }

    @SuppressWarnings("ClassExplicitlyAnnotation")
    private static class QueryParamLiteral extends AnnotationLiteral<QueryParam> implements QueryParam {
        static final QueryParam DEFAULT = new QueryParamLiteral("");
        private final String value;

        private QueryParamLiteral(final String value) {
            this.value = value;
        }

        @Override
        public String value() {
            return value;
        }
    }

    private record CacheKey(Class<? extends Annotation> annotationType, String paramName, Type type, boolean encode,
            String defaultValue) {
    }
}

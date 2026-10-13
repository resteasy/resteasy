/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.decorator.Decorator;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.NormalScope;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.AmbiguousResolutionException;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.Stereotype;
import jakarta.enterprise.inject.literal.InjectLiteral;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.AfterDeploymentValidation;
import jakarta.enterprise.inject.spi.AfterTypeDiscovery;
import jakarta.enterprise.inject.spi.Annotated;
import jakarta.enterprise.inject.spi.AnnotatedConstructor;
import jakarta.enterprise.inject.spi.AnnotatedField;
import jakarta.enterprise.inject.spi.AnnotatedMethod;
import jakarta.enterprise.inject.spi.AnnotatedParameter;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
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
import jakarta.inject.Scope;
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
import jakarta.ws.rs.core.Context;

import org.jboss.resteasy.cdi.i18n.LogMessages;
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
 * The extension operates in six CDI lifecycle phases:
 * </p>
 * <ol>
 * <li>{@link BeforeBeanDiscovery} &mdash; registers each {@code @*Param} annotation as a CDI qualifier</li>
 * <li>{@link ProcessAnnotatedType} &mdash; adds {@link Inject @Inject} to {@code @*Param}-annotated fields,
 * setter methods, and constructors so that CDI resolves parameter values automatically. RESTEasy-specific
 * annotations ({@code org.jboss.resteasy.annotations.jaxrs.*Param}) are also handled by adding the corresponding
 * Jakarta REST annotation alongside {@code @Inject}. The {@code @BeanParam} aggregators of resources are
 * recorded.</li>
 * <li>{@link AfterTypeDiscovery} &mdash; makes each recorded {@code @BeanParam} aggregator which is not a bean a
 * {@code @Dependent} bean</li>
 * <li>{@link ProcessInjectionPoint} &mdash; collects the types of all {@code @*Param}-annotated injection points</li>
 * <li>{@link AfterBeanDiscovery} &mdash; registers synthetic beans that produce parameter values at request time
 * by delegating to {@link ParameterExtractors}</li>
 * <li>{@link AfterDeploymentValidation} &mdash; warns about {@code @BeanParam} aggregators with a normal scope</li>
 * </ol>
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
public class ResteasyParamCdiExtension implements Extension {
    private static final String BEAN_PARAM_ID = ResteasyParamCdiExtension.class.getName() + ".beanParam";
    private static final Set<Class<? extends Annotation>> PARAM_ANNOTATIONS = Set.of(
            BeanParam.class,
            CookieParam.class,
            FormParam.class,
            HeaderParam.class,
            MatrixParam.class,
            PathParam.class,
            QueryParam.class);

    private final Set<Type> paramTypes = ConcurrentHashMap.newKeySet();
    private final Set<Class<?>> beanParamTypes = ConcurrentHashMap.newKeySet();
    private final Set<Class<?>> discoveredTypes = ConcurrentHashMap.newKeySet();
    private final Map<CacheKey, RequestParameterExtractor> extractorCache = new ConcurrentHashMap<>();
    private final Map<Class<?>, FormInjector> formInjectorCache = new ConcurrentHashMap<>();

    /**
     * Registers each {@code @*Param} annotation as a CDI qualifier, marking all annotation members as
     * {@link Nonbinding} so that qualifier resolution matches on presence alone rather than on member values.
     *
     * @param event the before bean discovery event
     */
    public void registerQualifiers(@Observes final BeforeBeanDiscovery event) {
        // Register the @*Param annotations as qualifiers
        for (Class<? extends Annotation> param : PARAM_ANNOTATIONS) {
            event.configureQualifier(param)
                    .methods()
                    .forEach(method -> method.add(Nonbinding.Literal.INSTANCE));
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
        final AnnotatedType<T> annotatedType = event.getAnnotatedType();
        collectBeanParamTypes(annotatedType.getJavaClass());
        if (!annotatedType.getJavaClass().isInterface()
                && !annotatedType.isAnnotationPresent(Decorator.class)
                && !Utils.isUnproxyableClass(annotatedType.getJavaClass())) {
            addInject(event.configureAnnotatedType());
        }
    }

    /**
     * Records the discovered types which may be {@link BeanParam @BeanParam} aggregators, so that
     * {@link #addBeanParamTypes(AfterTypeDiscovery)} does not add a type which is already a bean. The scope and
     * stereotype meta-annotations match a type with a bean defining annotation, which may have only
     * {@link Context @Context} members or none. Any other type without these annotations has nothing for CDI to inject.
     *
     * @param event the annotated type being processed
     * @param <T>   the type being processed
     */
    public <T> void observeBeanParamCandidates(@WithAnnotations({ BeanParam.class, CookieParam.class, FormParam.class,
            HeaderParam.class, MatrixParam.class, PathParam.class, QueryParam.class, Inject.class, Context.class,
            NormalScope.class, Scope.class, Stereotype.class,
            org.jboss.resteasy.annotations.jaxrs.CookieParam.class, org.jboss.resteasy.annotations.jaxrs.FormParam.class,
            org.jboss.resteasy.annotations.jaxrs.HeaderParam.class, org.jboss.resteasy.annotations.jaxrs.MatrixParam.class,
            org.jboss.resteasy.annotations.jaxrs.PathParam.class,
            org.jboss.resteasy.annotations.jaxrs.QueryParam.class }) @Observes final ProcessAnnotatedType<T> event) {
        // Types added by an extension are recorded too, as they are beans as well
        discoveredTypes.add(event.getAnnotatedType().getJavaClass());
    }

    /**
     * Makes each {@link BeanParam @BeanParam} aggregator of a resource which is not a bean a {@link Dependent @Dependent}
     * bean, and adds {@link Inject @Inject} to its {@code @*Param}-annotated members. CDI then creates the aggregator,
     * and injects all its members, as for an aggregator which is a bean. An aggregator which CDI cannot create, e.g.
     * one without a no-argument or {@code @Inject} constructor, is left to RESTEasy.
     *
     * @param event the after type discovery event
     */
    public void addBeanParamTypes(@Observes final AfterTypeDiscovery event) {
        for (Class<?> type : beanParamTypes) {
            if (!discoveredTypes.contains(type) && hasBeanConstructor(type)) {
                final AnnotatedTypeConfigurator<?> configurator = event.addAnnotatedType(type,
                        "%s.%s".formatted(BEAN_PARAM_ID, type.getName()));
                configurator.add(Dependent.Literal.INSTANCE);
                addInject(configurator);
            }
        }
    }

    /**
     * Logs a warning for each {@link BeanParam @BeanParam} aggregator whose bean has a normal scope. The aggregator is
     * then a client proxy, whose fields do not hold the parameter values, e.g. when validating a {@code @Valid}
     * aggregator.
     *
     * @param event       the after deployment validation event
     * @param beanManager the bean manager
     */
    public void validateBeanParamScopes(@Observes final AfterDeploymentValidation event, final BeanManager beanManager) {
        for (Class<?> type : beanParamTypes) {
            try {
                final Bean<?> bean = beanManager.resolve(beanManager.getBeans(type));
                if (bean != null && beanManager.isNormalScope(bean.getScope())) {
                    LogMessages.LOGGER.normalScopedBeanParam(type.getName(), "@" + bean.getScope().getSimpleName());
                }
            } catch (AmbiguousResolutionException ignore) {
                // The aggregator is obtained through its @BeanParam qualified bean, so another bean of its type is
                // not an error here
            }
        }
        beanParamTypes.clear();
        // Cleared here rather than after type discovery, as the types added then are processed afterward
        discoveredTypes.clear();
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
    private void addInject(final AnnotatedTypeConfigurator<?> configurator) {
        // Fields
        for (AnnotatedFieldConfigurator<?> fieldConfig : configurator.fields()) {
            final AnnotatedField<?> field = fieldConfig.getAnnotated();
            if (field.isAnnotationPresent(Inject.class)) {
                continue;
            }
            if (PARAM_ANNOTATIONS.stream().anyMatch(field::isAnnotationPresent)) {
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
            if (PARAM_ANNOTATIONS.stream().anyMatch(method::isAnnotationPresent)) {
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
                    .anyMatch(p -> PARAM_ANNOTATIONS.stream().anyMatch(p::isAnnotationPresent)
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
     * Records the types of the {@link BeanParam @BeanParam} fields, setter methods and method and constructor
     * parameters of the type and its super types, including those of nested aggregators.
     *
     * @param type the resource or aggregator type
     */
    private void collectBeanParamTypes(final Class<?> type) {
        for (Class<?> current = type; current != null && current != Object.class; current = current.getSuperclass()) {
            for (Field field : current.getDeclaredFields()) {
                if (field.isAnnotationPresent(BeanParam.class)) {
                    addBeanParamType(field.getType());
                }
            }
            for (Method method : current.getDeclaredMethods()) {
                if (method.isAnnotationPresent(BeanParam.class) && method.getParameterCount() == 1) {
                    addBeanParamType(method.getParameterTypes()[0]);
                }
                collectBeanParamTypes(method.getParameters());
            }
            for (Constructor<?> constructor : current.getDeclaredConstructors()) {
                collectBeanParamTypes(constructor.getParameters());
            }
        }
    }

    private void collectBeanParamTypes(final Parameter[] parameters) {
        for (Parameter parameter : parameters) {
            if (parameter.isAnnotationPresent(BeanParam.class)) {
                addBeanParamType(parameter.getType());
            }
        }
    }

    private void addBeanParamType(final Class<?> type) {
        if (type.isInterface() || type.isPrimitive() || type.isArray() || Modifier.isAbstract(type.getModifiers())
                || (type.getEnclosingClass() != null && !Modifier.isStatic(type.getModifiers()))) {
            return;
        }
        if (beanParamTypes.add(type)) {
            collectBeanParamTypes(type);
        }
    }

    /**
     * Checks if CDI can create the type, i.e. if it has a constructor without parameters or one annotated with
     * {@link Inject @Inject}.
     */
    private static boolean hasBeanConstructor(final Class<?> type) {
        for (Constructor<?> constructor : type.getDeclaredConstructors()) {
            if (constructor.getParameterCount() == 0 || constructor.isAnnotationPresent(Inject.class)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Resolves the bean of a {@link BeanParam @BeanParam} aggregator from the beans of the instance. If subclasses of
     * the aggregator are beans as well, the bean of the aggregator itself is used, see
     * {@link BeanManagerSupport#findBean(BeanManager, Type, Set)}.
     *
     * @param beanManager    the bean manager
     * @param beanParamClass the aggregator type
     * @param candidates     the instance of the aggregator type
     *
     * @return the bean or {@code null} if RESTEasy creates the aggregator
     */
    private static Bean<?> findBeanParamBean(final BeanManager beanManager, final Class<?> beanParamClass,
            final Instance<?> candidates) {
        if (candidates.isUnsatisfied()) {
            return null;
        }
        final Set<Bean<?>> beans = new HashSet<>();
        candidates.handlesStream().forEach(handle -> beans.add(handle.getBean()));
        try {
            return BeanManagerSupport.findBean(beanManager, beanParamClass, beans);
        } catch (AmbiguousResolutionException e) {
            // None of the beans is the aggregator itself, e.g. it is @Vetoed while subclasses of it are beans
            return null;
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
     * @param afd         the after bean discovery event
     * @param beanManager the bean manager
     */
    public void registerParamQualifiers(@Observes final AfterBeanDiscovery afd, final BeanManager beanManager) {
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
                        // If the type is a CDI-managed bean, let CDI create and inject it. The instance is created
                        // through its handle, so a @Dependent instance is destroyed with the injection target.
                        final Instance<?> candidates = instance.select(beanParamClass);
                        final Bean<?> bean = findBeanParamBean(beanManager, beanParamClass, candidates);
                        if (bean != null) {
                            return candidates.handlesStream()
                                    .filter(handle -> handle.getBean().equals(bean))
                                    .findFirst()
                                    .orElseThrow()
                                    .get();
                        }
                        // Fall back to FormInjector for non-CDI-managed types
                        final ResteasyProviderFactory rpf = ResteasyProviderFactory.getInstance();
                        final HttpRequest request = rpf.getContextData(HttpRequest.class);
                        if (request == null) {
                            return null;
                        }
                        final FormInjector formInjector = formInjectorCache.computeIfAbsent(beanParamClass,
                                (ignored) -> new FormInjector(beanParamClass, rpf));
                        return formInjector.inject(request, null, false);
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
        final InjectionPoint ip = pip.getInjectionPoint();
        final Annotated annotated = ip.getAnnotated();
        for (Class<? extends Annotation> type : PARAM_ANNOTATIONS) {
            if (annotated.isAnnotationPresent(type)) {
                paramTypes.add(Types.boxPrimitives(ip.getType()));
                return;
            }
        }
        // Check if this is a setter parameter whose method has the annotation
        if (annotated instanceof AnnotatedParameter<?> param) {
            final Annotated method = param.getDeclaringCallable();
            for (Class<? extends Annotation> type : PARAM_ANNOTATIONS) {
                final Annotation ann = method.getAnnotation(type);
                if (ann != null) {
                    pip.configureInjectionPoint().addQualifier(ann);
                    paramTypes.add(Types.boxPrimitives(ip.getType()));
                    return;
                }
            }
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
        // The qualifiers do not include annotations such as @Separator, which the extractor requires
        final Set<Annotation> annotations = new HashSet<>(injectionPoint.getQualifiers());
        annotations.addAll(injectionPoint.getAnnotated().getAnnotations());
        // For a setter method, the annotations are on the method rather than the parameter
        if (injectionPoint.getAnnotated() instanceof AnnotatedParameter<?> parameter
                && parameter.getDeclaringCallable() instanceof AnnotatedMethod<?> method) {
            annotations.addAll(method.getAnnotations());
        }
        final CacheKey key = new CacheKey(annotationType, paramName, genericType, encode, defaultValue, annotations);
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
            String defaultValue, Set<Annotation> annotations) {
    }
}

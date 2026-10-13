/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import jakarta.decorator.Decorator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Default;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.literal.InjectLiteral;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.AfterDeploymentValidation;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.BeforeBeanDiscovery;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.InjectionTarget;
import jakarta.enterprise.inject.spi.ProcessAnnotatedType;
import jakarta.enterprise.inject.spi.ProcessBean;
import jakarta.enterprise.inject.spi.ProcessBeanAttributes;
import jakarta.enterprise.inject.spi.ProcessInjectionTarget;
import jakarta.enterprise.inject.spi.ProcessManagedBean;
import jakarta.enterprise.inject.spi.ProcessSessionBean;
import jakarta.enterprise.inject.spi.ProcessSyntheticBean;
import jakarta.enterprise.inject.spi.WithAnnotations;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.interceptor.Interceptor;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.container.ResourceContext;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Request;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.ext.Providers;
import jakarta.ws.rs.sse.Sse;

import org.jboss.resteasy.cdi.i18n.LogMessages;
import org.jboss.resteasy.cdi.i18n.Messages;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.plugins.providers.sse.SseImpl;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.RequiredContextTypes;

/**
 * A CDI {@link Extension} that integrates Jakarta REST components with CDI. This extension:
 * <ul>
 * <li>Assigns default scopes to discovered Jakarta REST resources ({@link RequestScoped}), providers
 * ({@link ApplicationScoped}), and {@link Application} subclasses ({@link ApplicationScoped})</li>
 * <li>Adds {@link jakarta.inject.Inject @Inject} to {@link jakarta.ws.rs.core.Context @Context}-annotated fields,
 * setter methods, and constructors so that CDI can inject context values</li>
 * <li>Registers the {@link Context @Context} and {@link Default @Default} beans for the Jakarta REST context types</li>
 * <li>Wraps {@link InjectionTarget} instances for Jakarta REST components within {@link JaxrsInjectionTarget}
 * to handle property injection and validation</li>
 * <li>Builds the session bean interface map used by {@link CdiInjectorFactory} for EJB lookup</li>
 * <li>Tracks CDI-managed Jakarta REST components in the {@link ResteasyBeanContainer}</li>
 * </ul>
 *
 * @author Jozef Hartinger
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@SuppressWarnings("removal")
public class ResteasyCdiExtension implements Extension {
    private static boolean active;
    private static final String JAKARTA_EJB_STATEFUL = "jakarta.ejb.Stateful";
    private static final String JAKARTA_EJB_STATELESS = "jakarta.ejb.Stateless";
    private static final String JAKARTA_EJB_SINGLETON = "jakarta.ejb.Singleton";

    // Context types which change per request or per deployment, resolved on each invocation by a proxy
    private static final List<Class<?>> PROXIED_CONTEXT_TYPES = List.of(
            Configuration.class,
            HttpHeaders.class,
            HttpRequest.class,
            Providers.class,
            Request.class,
            ResourceContext.class,
            ResourceInfo.class,
            SecurityContext.class,
            UriInfo.class);

    public static boolean isCDIActive() {
        return active;
    }

    private final Map<Class<?>, Type> sessionBeanInterface = new HashMap<>();
    private final Set<Class<?>> sessionBeans = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<Class<?>> beanContainer = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<Class<? extends Application>> applications = Collections.newSetFromMap(new ConcurrentHashMap<>());
    // Types whose scope is decided in observeBeanAttributes(), once it's known whether CDI accepted the type as a bean
    private final Set<Class<?>> resourceTypes = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<Class<?>> providerTypes = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private final Set<Class<?>> applicationTypes = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private boolean generateClientBean = true;
    private volatile ResteasyBeanContainer resteasyBeanContainer;

    /**
     * Obtain BeanManager reference for future use.
     *
     * @param event event
     */
    public void observeBeforeBeanDiscovery(@Observes BeforeBeanDiscovery event) {
        active = true;
        event.addQualifier(Context.class);
    }

    /**
     * Process any client beans.
     *
     * @param pb the bean being processed
     */
    public void processClientBean(@Observes final ProcessBean<?> pb) {
        // this method will get notified if there is any bean of type `Client` created
        if (pb.getBean().getTypes().contains(Client.class)) {
            final Set<Annotation> qualifiers = pb.getBean()
                    .getQualifiers(); // you want to detect beans with no explicit qualifiers
            if (qualifiers.contains(Any.Literal.INSTANCE) && qualifiers.contains(Default.Literal.INSTANCE)) {
                generateClientBean = false;
            }
        }
    }

    /**
     * Fails the deployment if a bean other than the ones registered by this extension is a {@link Default @Default} or
     * {@link Context @Context} bean for a Jakarta REST context type. These would be ambiguous with the beans this
     * extension always registers.
     *
     * @param event the bean being processed
     */
    public void validateContextTypeBeans(@Observes final ProcessBean<?> event) {
        if (event instanceof ProcessSyntheticBean<?> syntheticBean
                && syntheticBean.getSource() instanceof ResteasyCdiExtension) {
            return;
        }
        final Bean<?> bean = event.getBean();
        final Set<Annotation> qualifiers = bean.getQualifiers();
        if (!qualifiers.contains(Default.Literal.INSTANCE) && !qualifiers.contains(ContextLiteral.INSTANCE)) {
            return;
        }
        for (Type type : bean.getTypes()) {
            if (type == Sse.class || PROXIED_CONTEXT_TYPES.contains(type)) {
                event.addDefinitionError(Messages.MESSAGES.contextTypeBeanDefined(bean, type.getTypeName()));
            }
        }
    }

    /**
     * Fails the deployment if a provider is explicitly declared {@link Dependent @Dependent}. Providers are application
     * singletons, while each injection of a {@code @Dependent} bean creates a new instance. A provider which declares
     * type variables is excluded, as CDI requires such a bean to be {@code @Dependent}. Session beans are excluded, as an
     * unscoped session bean is made {@code @Dependent}.
     *
     * @param event the managed bean being processed
     */
    public void validateProviderScope(@Observes final ProcessManagedBean<?> event) {
        final AnnotatedType<?> type = event.getAnnotatedBeanClass();
        // A bean whose type declares type variables must be @Dependent
        if (providerTypes.contains(type.getJavaClass()) && type.isAnnotationPresent(Dependent.class)
                && type.getJavaClass().getTypeParameters().length == 0) {
            event.addDefinitionError(Messages.MESSAGES.dependentProvider(type.getJavaClass().getName()));
        }
    }

    /**
     * Registers producers for required injections types and for a {@link Client REST client}.
     *
     * @param event the after bean discovery event
     */
    public void registerBeans(@Observes final AfterBeanDiscovery event) {
        if (generateClientBean) {
            event.addBean().addTransitiveTypeClosure(Client.class)
                    .scope(ApplicationScoped.class)
                    .produceWith(instance -> {
                        // If the application defines a ClientBuilder bean, build the client from it
                        final Instance<ClientBuilder> builder = instance.select(ClientBuilder.class);
                        if (builder.isResolvable()) {
                            return builder.get().build();
                        }
                        return ClientBuilder
                                .newClient(RegisterBuiltin.getClientInitializedResteasyProviderFactory(getClassLoader()));
                    })
                    .disposeWith((client, instance) -> client.close());
        }
        resteasyBeanContainer = new DefaultResteasyBeanContainer(beanContainer);
        beanContainer.clear();
    }

    /**
     * Registers producers for the Jakarta REST context types, the {@link Application} and the Jakarta Servlet types
     * which are required to be injectable.
     * <p>
     * The Jakarta REST context types are owned by the implementation, so the {@link Context @Context} and
     * {@link Default @Default} beans are always registered. For the {@link Application} and the Servlet types, the
     * {@link Default @Default} bean is only registered if no other bean already provides the type.
     * </p>
     *
     * @param event       the after bean discovery event
     * @param beanManager the bean manager
     */
    public void registerContextProducers(@Observes final AfterBeanDiscovery event, final BeanManager beanManager) {
        for (Class<?> type : PROXIED_CONTEXT_TYPES) {
            registerContextTypeProxy(event, type);
        }
        event.addBean().addType(Sse.class)
                .addQualifiers(Any.Literal.INSTANCE, Default.Literal.INSTANCE, ContextLiteral.INSTANCE)
                .scope(ApplicationScoped.class)
                .produceWith(instance -> new SseImpl());

        // Register a producer for the application if one was not provided, e.g. no application with a @ApplicationPath
        // or the application was defined in the web.xml
        if (beanManager.getBeans(Application.class).isEmpty()) {
            event.addBean().addType(Application.class)
                    .addQualifier(Any.Literal.INSTANCE)
                    .addQualifier(Default.Literal.INSTANCE)
                    .scope(ApplicationScoped.class)
                    .produceWith(instance -> new ContextApplication());
        }
        // Delegates to the Application of the current request, as the beans are shared by each deployed application
        event.addBean().addType(Application.class)
                .addQualifier(Any.Literal.INSTANCE)
                .addQualifier(ContextLiteral.INSTANCE)
                .scope(ApplicationScoped.class)
                .produceWith(instance -> new ContextApplication());
        for (Class<? extends Application> applicationClass : applications) {
            registerApplicationProducer(event, applicationClass);
        }

        // Register producers, if they don't exist, for known servlet types
        registerContextProducer(event, beanManager, HttpServletRequest.class);
        registerContextProducer(event, beanManager, HttpServletResponse.class);
        registerContextProducer(event, beanManager, ServletContext.class);
        registerContextProducer(event, beanManager, ServletConfig.class);
    }

    /**
     * Clears the types recorded during bean discovery, which are not used once the deployment is validated.
     *
     * @param event the after deployment validation event
     */
    public void clearDiscoveredTypes(@Observes final AfterDeploymentValidation event) {
        resourceTypes.clear();
        providerTypes.clear();
        applicationTypes.clear();
        applications.clear();
    }

    /**
     * Adds {@link Inject @Inject} to the {@link Context @Context} injection points of resources and records them, so
     * their scope and whether they are managed by CDI can be decided in
     * {@link #observeBeanAttributes(ProcessBeanAttributes, BeanManager)}.
     *
     * @param <T>         type
     * @param event       event
     * @param beanManager the bean manager
     */
    public <T> void observeResources(@WithAnnotations({ Path.class }) @Observes ProcessAnnotatedType<T> event,
            final BeanManager beanManager) {
        final AnnotatedType<T> annotatedType = event.getAnnotatedType();
        final Class<T> javaClass = annotatedType.getJavaClass();
        if (javaClass.isInterface() || annotatedType.isAnnotationPresent(Decorator.class)) {
            return;
        }
        // Add @Inject to any @Context injection points. If the type does not end up being a bean, the RESTEasy property
        // injection still injects them.
        addInject(event);
        if (isSessionBean(annotatedType)) {
            makeSessionBeanDependent(event, beanManager);
        } else {
            LogMessages.LOGGER.debug(Messages.MESSAGES.discoveredCDIBeanJaxRsResource(javaClass.getCanonicalName()));
            resourceTypes.add(javaClass);
        }
    }

    /**
     * Adds {@link Inject @Inject} to the {@link Context @Context} injection points of providers and records them, so
     * their scope and whether they are managed by CDI can be decided in
     * {@link #observeBeanAttributes(ProcessBeanAttributes, BeanManager)}.
     *
     * @param <T>         type
     * @param event       event
     * @param beanManager the bean manager
     */
    public <T> void observeProviders(@WithAnnotations({ Provider.class }) @Observes ProcessAnnotatedType<T> event,
            final BeanManager beanManager) {
        final AnnotatedType<T> annotatedType = event.getAnnotatedType();
        final Class<T> javaClass = annotatedType.getJavaClass();
        if (javaClass.isInterface() || annotatedType.isAnnotationPresent(Decorator.class)) {
            return;
        }
        // Add @Inject to any @Context injection points. If the type does not end up being a bean, the RESTEasy property
        // injection still injects them.
        addInject(event);
        if (isSessionBean(annotatedType)) {
            makeSessionBeanDependent(event, beanManager);
        } else {
            LogMessages.LOGGER.debug(Messages.MESSAGES.discoveredCDIBeanJaxRsProvider(javaClass.getCanonicalName()));
            providerTypes.add(javaClass);
        }
    }

    /**
     * Adds {@link Inject @Inject} to the {@link Context @Context} injection points of a discovered type which implements
     * a provider contract, is not annotated with {@link Provider @Provider} and does not declare a scope. Such a type
     * is made an {@link ApplicationScoped @ApplicationScoped} provider, or a {@link Singleton @Singleton}
     * provider if the type cannot be proxied, in {@link #observeBeanAttributes(ProcessBeanAttributes, BeanManager)}.
     * Providers are application singletons, while such a type would otherwise be {@link Dependent @Dependent}. This can
     * only happen when the type is discovered without a bean defining annotation, e.g. in a
     * {@code bean-discovery-mode="all"} archive, and is registered as a provider through
     * {@link Application#getClasses()} or a {@link jakarta.ws.rs.core.Feature Feature}.
     * <p>
     * Only types with a {@link Context @Context} annotation are observed, as other types have no injection points to
     * change. The scope of all such types is set in
     * {@link #observeBeanAttributes(ProcessBeanAttributes, BeanManager)}.
     * </p>
     *
     * @param event       the annotated type being processed
     * @param beanManager the bean manager
     * @param <T>         the type
     */
    public <T> void observeProviderContracts(@WithAnnotations(Context.class) @Observes final ProcessAnnotatedType<T> event,
            final BeanManager beanManager) {
        if (isUnannotatedProviderContract(event.getAnnotatedType(), beanManager)) {
            addInject(event);
        }
    }

    /**
     * Decides the scope of the resources, providers and {@link Application} subclasses recorded when their annotated
     * type was processed, and whether they are managed by CDI, i.e. added to the bean container. This is only invoked
     * for beans, so a type which is not a bean, e.g. one with a constructor CDI cannot satisfy, is not added to the bean
     * container and is created by RESTEasy.
     * <p>
     * The {@link Path @Path}, {@link Provider @Provider} and {@link jakarta.ws.rs.ApplicationPath @ApplicationPath}
     * stereotypes may define the default scopes. The same defaults are set if no scope is defined, e.g. if the
     * annotations are not stereotypes in the environment or {@code @Path} is only present on methods. In addition:
     * </p>
     * <ul>
     * <li>A resource which does not define a scope is made {@link RequestScoped @RequestScoped}. An unproxyable resource
     * which does not declare a scope is made {@link Dependent @Dependent}, which does not require a client proxy and is
     * still created for each request.</li>
     * <li>A {@link Provider @Provider} which does not define a scope is made {@link ApplicationScoped @ApplicationScoped}.
     * An unproxyable one which does not declare a scope is made {@link Singleton @Singleton}, which does not require a
     * client proxy.</li>
     * <li>A provider without {@link Provider @Provider} which does not declare a scope is made
     * {@link ApplicationScoped @ApplicationScoped}, or {@link Singleton @Singleton} if it cannot be proxied.</li>
     * <li>A resource or provider which declares type variables and does not declare a scope is made
     * {@link Dependent @Dependent}, as CDI requires for a bean whose type contains type variables. RESTEasy still
     * creates a single instance of such a provider.</li>
     * <li>An {@link Application} subclass which does not declare a scope, e.g. without {@code @ApplicationPath}, is made
     * {@link ApplicationScoped @ApplicationScoped}.</li>
     * </ul>
     * A warning is logged when the default scope of the {@code @Path} or {@code @Provider} stereotype is overridden, as
     * the application should declare the scope.
     *
     * @param event       the bean attributes being processed
     * @param beanManager the bean manager
     * @param <T>         the type
     */
    public <T> void observeBeanAttributes(@Observes final ProcessBeanAttributes<T> event, final BeanManager beanManager) {
        if (!(event.getAnnotated() instanceof AnnotatedType<?> annotatedType)) {
            return;
        }
        final Class<?> javaClass = annotatedType.getJavaClass();
        final boolean unproxyable = Utils.isUnproxyableClass(javaClass);
        // A bean whose type declares type variables must be @Dependent
        final boolean generic = javaClass.getTypeParameters().length > 0;
        // Stateful session beans are not considered managed by the CDI container, so they are not added to the bean
        // container. Their scope is still set.
        final boolean managed = !isStatefulBean(annotatedType);
        Class<? extends Annotation> scope = event.getBeanAttributes().getScope();
        if (resourceTypes.contains(javaClass)) {
            if (!Utils.isScopeDeclared(annotatedType, beanManager, Path.class)) {
                if (generic) {
                    if (annotatedType.isAnnotationPresent(Path.class)) {
                        LogMessages.LOGGER.genericBean(javaClass.getName());
                    }
                    scope = Dependent.class;
                } else if (unproxyable) {
                    if (annotatedType.isAnnotationPresent(Path.class)) {
                        LogMessages.LOGGER.unproxyableResource(javaClass.getName());
                    }
                    scope = Dependent.class;
                } else if (!Utils.isScopeDefined(annotatedType, beanManager)) {
                    // The @Path annotation does not define the default scope, e.g. it is only present on methods
                    scope = RequestScoped.class;
                }
            }
            // A @Dependent resource has no client proxy, so it does not need to be proxyable
            if (managed && (scope == Dependent.class || (beanManager.isNormalScope(scope) && !unproxyable))) {
                beanContainer.add(javaClass);
            }
        } else if (providerTypes.contains(javaClass)) {
            if (!Utils.isScopeDeclared(annotatedType, beanManager, Provider.class)) {
                if (generic) {
                    // RESTEasy still creates a single instance of the provider
                    LogMessages.LOGGER.genericBean(javaClass.getName());
                    scope = Dependent.class;
                } else if (unproxyable) {
                    LogMessages.LOGGER.unproxyableProvider(javaClass.getName());
                    scope = Singleton.class;
                } else if (!Utils.isScopeDefined(annotatedType, beanManager)) {
                    // The @Provider annotation does not define the default scope
                    scope = ApplicationScoped.class;
                }
            }
            // A provider without a client proxy, e.g. @Singleton, does not need to be proxyable
            if (managed && (!beanManager.isNormalScope(scope) || !unproxyable)) {
                beanContainer.add(javaClass);
            }
        } else if (isUnannotatedProviderContract(annotatedType, beanManager)) {
            LogMessages.LOGGER.debug(Messages.MESSAGES.discoveredCDIBeanJaxRsProvider(javaClass.getCanonicalName()));
            if (generic) {
                scope = Dependent.class;
            } else {
                scope = unproxyable ? Singleton.class : ApplicationScoped.class;
            }
            beanContainer.add(javaClass);
        } else if (applicationTypes.contains(javaClass)) {
            if (!Utils.isScopeDefined(annotatedType, beanManager)) {
                scope = ApplicationScoped.class;
            }
            if (managed && beanManager.isNormalScope(scope) && !unproxyable) {
                beanContainer.add(javaClass);
                @SuppressWarnings("unchecked")
                final Class<? extends Application> applicationClass = (Class<? extends Application>) javaClass;
                applications.add(applicationClass);
            }
        } else {
            return;
        }
        if (scope != event.getBeanAttributes().getScope()) {
            event.configureBeanAttributes().scope(scope);
        }
    }

    /**
     * Adds {@link Inject @Inject} to the {@link Context @Context} injection points of {@link Application} subclasses
     * and records them, so their scope and whether they are managed by CDI can be decided in
     * {@link #observeBeanAttributes(ProcessBeanAttributes, BeanManager)}. A stateless or singleton session bean is not
     * recorded, and is made {@link Dependent @Dependent} if it does not declare a scope.
     *
     * @param <T>         type
     * @param event       event
     * @param beanManager the bean manager
     */
    public <T extends Application> void observeApplications(@Observes ProcessAnnotatedType<T> event,
            final BeanManager beanManager) {
        final AnnotatedType<T> annotatedType = event.getAnnotatedType();
        final Class<T> applicationClass = annotatedType.getJavaClass();
        if (Modifier.isAbstract(applicationClass.getModifiers())) {
            return;
        }
        // If we're not an interface or decorator, add @Inject to any @Context injection points
        if (!applicationClass.isInterface()
                && !annotatedType.isAnnotationPresent(Decorator.class)
                && !Utils.isUnproxyableClass(applicationClass)) {
            addInject(event);
        }
        if (isSessionBean(annotatedType)) {
            makeSessionBeanDependent(event, beanManager);
        } else {
            applicationTypes.add(applicationClass);
        }
    }

    /**
     * Wrap InjectionTarget of JAX-RS components within JaxrsInjectionTarget
     * which takes care of JAX-RS property injection.
     *
     * @param <T>   type
     * @param event event
     */
    public <T> void observeInjectionTarget(@Observes ProcessInjectionTarget<T> event) {
        if (Utils.isJaxrsComponent(event.getAnnotatedType().getJavaClass())) {
            event.setInjectionTarget(wrapInjectionTarget(event));
        }
    }

    /**
     * Observes ProcessSessionBean events and creates a (Bean class {@literal ->} Local
     * interface) map for Session beans with local interfaces. This map is
     * necessary since RESTEasy identifies a bean class as JAX-RS components
     * while CDI requires a local interface to be used for lookup.
     *
     * @param <T>   type
     * @param event event
     */
    public <T> void observeSessionBeans(@Observes ProcessSessionBean<T> event) {
        Bean<Object> sessionBean = event.getBean();

        if (Utils.isJaxrsComponent(sessionBean.getBeanClass())) {
            sessionBeans.add(sessionBean.getBeanClass());
            addSessionBeanInterface(sessionBean);
        }
    }

    public Map<Class<?>, Type> getSessionBeanInterface() {
        return sessionBeanInterface;
    }

    @SuppressWarnings("removal")
    protected <T> InjectionTarget<T> wrapInjectionTarget(ProcessInjectionTarget<T> event) {
        final AnnotatedType<T> annotatedType = event.getAnnotatedType();
        return new JaxrsInjectionTarget<>(event.getInjectionTarget(), annotatedType.getJavaClass(),
                cdiInjectedMembers(annotatedType));
    }

    /**
     * Checks if the class is a session bean which is a Jakarta REST component. Session beans are created by the
     * container, but are not in the {@link #beanContainer() bean container}.
     *
     * @param type the class to check
     *
     * @return {@code true} if the class is a session bean
     */
    boolean isSessionBean(final Class<?> type) {
        return sessionBeans.contains(type);
    }

    ResteasyBeanContainer beanContainer() {
        if (resteasyBeanContainer == null) {
            throw Messages.MESSAGES.unableToResolveBean(ResteasyBeanContainer.class.getName());
        }
        return resteasyBeanContainer;
    }

    /**
     * Checks if the type is a discovered type which implements a provider contract, is not annotated with
     * {@link Provider @Provider} and does not declare a scope. See
     * {@link #observeProviderContracts(ProcessAnnotatedType, BeanManager)}.
     *
     * @param annotatedType the annotated type
     * @param beanManager   the bean manager
     *
     * @return {@code true} if the type is an unannotated provider
     */
    private boolean isUnannotatedProviderContract(final AnnotatedType<?> annotatedType, final BeanManager beanManager) {
        final Class<?> javaClass = annotatedType.getJavaClass();
        // Types annotated with @Provider are handled by observeProviders()
        return Utils.isProviderContract(javaClass)
                && !javaClass.isInterface()
                && !Modifier.isAbstract(javaClass.getModifiers())
                && !annotatedType.isAnnotationPresent(Provider.class)
                && !annotatedType.isAnnotationPresent(Decorator.class)
                && !annotatedType.isAnnotationPresent(Interceptor.class)
                && !isSessionBean(annotatedType)
                && !Utils.isScopeDefined(annotatedType, beanManager);
    }

    private void addSessionBeanInterface(Bean<?> bean) {
        for (Type type : bean.getTypes()) {
            if ((type instanceof Class<?>) && ((Class<?>) type).isInterface()) {
                Class<?> clazz = (Class<?>) type;
                final Class<?> beanClass = bean.getBeanClass();
                if (Utils.isJaxrsAnnotatedClass(beanClass) || Utils.hasEndpointMethod(clazz)) {
                    sessionBeanInterface.put(bean.getBeanClass(), type);
                    LogMessages.LOGGER.debug(Messages.MESSAGES.typeWillBeUsedForLookup(type, beanClass));
                    return;
                }
            }
        }
        LogMessages.LOGGER.debug(Messages.MESSAGES.noLookupInterface(bean.getBeanClass()));
    }

    private boolean isSessionBean(AnnotatedType<?> annotatedType) {
        for (Annotation annotation : annotatedType.getAnnotations()) {
            Class<?> annotationType = annotation.annotationType();
            if (annotationType.getName().equals(JAKARTA_EJB_STATELESS)
                    || annotationType.getName().equals(JAKARTA_EJB_SINGLETON)) {
                LogMessages.LOGGER.debug(Messages.MESSAGES.beanIsSLSBOrSingleton(annotatedType.getJavaClass()));
                return true; // Do not modify scopes of SLSBs and Singletons
            }
        }
        return false;
    }

    private boolean isStatefulBean(final AnnotatedType<?> annotatedType) {
        for (Annotation annotation : annotatedType.getAnnotations()) {
            Class<?> annotationType = annotation.annotationType();
            if (annotationType.getName().equals(JAKARTA_EJB_STATEFUL)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds {@link Inject @Inject} to {@link Context @Context}-annotated injection points so that CDI can inject
     * context values. This covers:
     * <ul>
     * <li>Fields annotated with {@code @Context}</li>
     * <li>Setter methods annotated with {@code @Context}</li>
     * <li>The public constructor with the most parameters, if any of its parameters is annotated with
     * {@code @Context} and no constructor is already annotated with {@code @Inject}</li>
     * </ul>
     *
     * @param pat the annotated type being processed
     */
    private void addInject(final ProcessAnnotatedType<?> pat) {
        // Add @Inject to fields
        pat.configureAnnotatedType()
                .filterFields(f -> !f.isAnnotationPresent(Inject.class) && f.isAnnotationPresent(Context.class)
                        && RequiredContextTypes.isContextType(f.getJavaMember().getType()))
                .forEach(f -> f.add(InjectLiteral.INSTANCE));
        // Add @Inject to methods. A method already annotated with @Inject still needs the @Context parameter.
        pat.configureAnnotatedType()
                .filterMethods(m -> m.isAnnotationPresent(Context.class)
                        && isSetter(m.getJavaMember())
                        && RequiredContextTypes.isContextType(m.getJavaMember().getParameterTypes()[0]))
                .forEach(m -> {
                    if (!m.getAnnotated().isAnnotationPresent(Inject.class)) {
                        m.add(InjectLiteral.INSTANCE);
                    }
                    // Qualifiers on an initializer method do not apply to its parameters, so the parameter needs @Context
                    // to resolve the @Context qualified bean
                    final var parameter = m.params().get(0);
                    if (!parameter.getAnnotated().isAnnotationPresent(Context.class)) {
                        parameter.add(ContextLiteral.INSTANCE);
                    }
                });
        // Add @Inject to the public constructor with the most parameters, the constructor chosen without CDI. CDI allows
        // only one @Inject constructor, so nothing is added if one is already declared.
        final var constructors = pat.configureAnnotatedType().constructors();
        if (constructors.stream().noneMatch(c -> c.getAnnotated().isAnnotationPresent(Inject.class))) {
            constructors.stream()
                    .filter(c -> Modifier.isPublic(c.getAnnotated().getJavaMember().getModifiers())
                            && c.getAnnotated().getParameters().stream()
                                    .anyMatch(p -> p.isAnnotationPresent(Context.class)
                                            && RequiredContextTypes.isContextType(p.getJavaParameter().getType())))
                    .max(Comparator.comparingInt(c -> c.getAnnotated().getParameters().size()))
                    .ifPresent(c -> c.add(InjectLiteral.INSTANCE));
        }
    }

    /**
     * Makes a stateless or singleton session bean which does not declare a scope {@link Dependent @Dependent}. The
     * default scopes of the {@link ApplicationPath @ApplicationPath}, {@link Path @Path} and
     * {@link Provider @Provider} stereotypes are not allowed on these session beans, and {@code @Dependent} is the
     * scope they have without the stereotypes.
     *
     * @param event       the event for the session bean
     * @param beanManager the bean manager
     * @param <T>         the type
     */
    private static <T> void makeSessionBeanDependent(final ProcessAnnotatedType<T> event, final BeanManager beanManager) {
        if (!Utils.isScopeDeclared(event.getAnnotatedType(), beanManager, ApplicationPath.class, Path.class,
                Provider.class)) {
            event.configureAnnotatedType().add(Dependent.Literal.INSTANCE);
        }
    }

    private static void registerContextTypeProxy(final AfterBeanDiscovery event, final Class<?> type) {
        // The proxy resolves the context object on each invocation, so it is valid on any thread processing the
        // request, e.g. after asynchronous resumption
        event.addBean().addType(type)
                .addQualifiers(Any.Literal.INSTANCE, Default.Literal.INSTANCE, ContextLiteral.INSTANCE)
                .scope(Dependent.class)
                .produceWith(instance -> ContextTypeInvocationHandler.createProxy(type));
    }

    private static void registerContextProducer(final AfterBeanDiscovery event, final BeanManager beanManager,
            final Class<?> beanType) {
        registerContextProducer(event, beanManager, beanType, RequestScoped.class);
    }

    private static void registerContextProducer(final AfterBeanDiscovery event, final BeanManager beanManager,
            final Class<?> beanType, final Class<? extends Annotation> scope) {
        if (beanManager.getBeans(beanType).isEmpty()) {
            registerContextProducer(event, beanType, scope);
        }
        // Register a synthetic producer for the @Context CDI qualifier
        event.addBean().addType(beanType)
                .addQualifier(Any.Literal.INSTANCE)
                .addQualifier(ContextLiteral.INSTANCE)
                .scope(scope)
                .produceWith(instance -> ResteasyContext.getRequiredContextData(beanType));
    }

    private static void registerContextProducer(final AfterBeanDiscovery event, final Class<?> beanType,
            final Class<? extends Annotation> scope) {
        // Only the required type is added. Adding the transitive type closure would resolve super types, e.g.
        // ServletRequest, which are neither required to be injectable nor checked for an existing bean.
        event.addBean().addType(beanType)
                .addQualifier(Any.Literal.INSTANCE)
                .addQualifier(Default.Literal.INSTANCE)
                .scope(scope)
                .produceWith(instance -> ResteasyContext.getRequiredContextData(beanType));
    }

    private static void registerApplicationProducer(final AfterBeanDiscovery event,
            final Class<? extends Application> applicationClass) {
        event.addBean().addType(applicationClass)
                .addQualifier(Any.Literal.INSTANCE)
                .addQualifier(ContextLiteral.INSTANCE)
                .scope(ApplicationScoped.class)
                .produceWith(instance -> {
                    final Application application = ResteasyContext.getRequiredContextData(Application.class);
                    if (applicationClass.isInstance(application)) {
                        return applicationClass.cast(application);
                    }
                    throw Messages.MESSAGES.applicationTypeNotDeployed(applicationClass.getName(),
                            application.getClass().getName());
                });
    }

    private static ClassLoader getClassLoader() {
        ClassLoader result = Thread.currentThread().getContextClassLoader();
        if (result == null) {
            result = ResteasyCdiExtension.class.getClassLoader();
        }
        return result;
    }

    /**
     * Checks if the method is a setter RESTEasy would use for property injection. Only these are converted to CDI
     * initializer methods, so methods RESTEasy did not previously inject do not become definition errors.
     *
     * @param method the method to check
     *
     * @return {@code true} if the method is a non-static, non-generic, single parameter setter
     */
    private static boolean isSetter(final Method method) {
        return method.getName().startsWith("set") && method.getParameterCount() == 1
                && !Modifier.isStatic(method.getModifiers()) && method.getTypeParameters().length == 0;
    }

    /**
     * Creates a filter for RESTEasy property injection which returns {@code true} for the members CDI injects. These are
     * the fields and methods annotated with {@link Inject @Inject} on the final annotated type, which includes the
     * {@code @Inject} annotations the RESTEasy extensions added, e.g. on {@link Context @Context} and {@code @*Param}
     * members.
     *
     * @param annotatedType the annotated type of the bean
     *
     * @return the filter for members RESTEasy should skip
     */
    private static Predicate<Member> cdiInjectedMembers(final AnnotatedType<?> annotatedType) {
        final Set<Member> members = new HashSet<>();
        annotatedType.getFields().stream()
                .filter(f -> f.isAnnotationPresent(Inject.class))
                .forEach(f -> members.add(f.getJavaMember()));
        annotatedType.getMethods().stream()
                .filter(m -> m.isAnnotationPresent(Inject.class))
                .forEach(m -> members.add(m.getJavaMember()));
        return members::contains;
    }

}

package org.jboss.resteasy.cdi;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.function.Function;

import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.InjectionPoint;
import jakarta.enterprise.inject.spi.InjectionTarget;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.ContextResolver;

import org.jboss.resteasy.cdi.i18n.LogMessages;
import org.jboss.resteasy.cdi.i18n.Messages;
import org.jboss.resteasy.core.PropertyInjectorImpl;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.util.Types;
import org.jboss.resteasy.spi.validation.GeneralValidatorCDI;
import org.jboss.resteasy.util.GetRestful;

/**
 * An {@link InjectionTarget} wrapper for Jakarta REST components that performs property injection and validation after
 * CDI injection.
 * <p>
 * For components not managed by CDI (e.g. EJB session beans), property injection via {@link PropertyInjectorImpl} is
 * used to populate {@code @Context} and {@code @*Param}-annotated fields. For CDI-managed components (those registered
 * in the {@link ResteasyCdiExtension ResteasyCdiExtension.beanContainer}), property injection is skipped since CDI handles
 * field injection directly through producers.
 * </p>
 * <p>
 * Validation is performed for all root resource classes after injection, using a {@link GeneralValidatorCDI} resolved
 * from a {@link ContextResolver}. If the class has a {@code @PostConstruct} method, validation is deferred to the
 * {@link #postConstruct(Object)} phase.
 * </p>
 *
 * @author Jozef Hartinger
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 *
 * @deprecated this should not be used outside of this module and may be removed in a future release
 */
@Deprecated(forRemoval = true, since = "7.0.3")
public class JaxrsInjectionTarget<T> implements InjectionTarget<T> {

    private final InjectionTarget<T> delegate;
    private final Class<T> clazz;
    private final boolean hasPostConstruct;
    private final boolean usePropertyInjector;
    private PropertyInjector propertyInjector;

    private static final Function<Method, Boolean> validatePostConstructParameters = (Method m) -> {
        if (m.getParameterCount() == 0)
            return true;
        else
            return m.getParameterCount() == 1
                    && InvocationContext.class.equals(m.getParameterTypes()[0])
                    && m.getAnnotation(AroundInvoke.class) != null;
    };

    /**
     * @deprecated this should not be used outside of this module
     */
    @Deprecated(forRemoval = true, since = "7.0.3")
    public JaxrsInjectionTarget(final InjectionTarget<T> delegate, final Class<T> clazz) {
        this(delegate, clazz, true);
    }

    /**
     * Creates a new wrapped {@link InjectionTarget} for custom RESTEasy bean validation and optional property inject.
     * <p>
     * If the {@code usePropertyInjector} is set to {@code true}, a new {@link PropertyInjector} is used to inject
     * Jakarta REST resources. These could be {@link jakarta.ws.rs.core.Context @Context} injection points or the
     * {@link @*Param} injection points. For full CDI managed beans, this should typically be {@code false}.
     * </p>
     *
     * @param delegate            the delegate injection target
     * @param clazz               the type of the bean
     * @param usePropertyInjector {@code true} if a {@link PropertyInjector} should be used and this is not a fully
     *                            managed CDI bean, otherwise {@link false}
     */
    protected JaxrsInjectionTarget(final InjectionTarget<T> delegate, final Class<T> clazz, final boolean usePropertyInjector) {
        this.delegate = delegate;
        this.clazz = clazz;
        hasPostConstruct = Types.hasPostConstruct(clazz, validatePostConstructParameters);
        this.usePropertyInjector = usePropertyInjector;
    }

    @Override
    public void inject(T instance, CreationalContext<T> ctx) {
        delegate.inject(instance, ctx);

        // We use a property injector for non-CDI managed resoruces
        if (usePropertyInjector && propertyInjector == null) {
            propertyInjector = getPropertyInjector();
        }

        HttpRequest request = ResteasyContext.getContextData(HttpRequest.class);
        HttpResponse response = ResteasyContext.getContextData(HttpResponse.class);

        if (propertyInjector != null) {
            if ((request != null) && (response != null)) {
                propertyInjector.inject(request, response, instance, false);
            } else {
                propertyInjector.inject(instance, false);
            }
        }

        if (request != null && !hasPostConstruct) {
            validate(request, instance);
        } else {
            LogMessages.LOGGER.debug(Messages.MESSAGES.skippingValidationOutsideResteasyContext());
        }
    }

    @Override
    public void postConstruct(T instance) {
        delegate.postConstruct(instance);
        if (hasPostConstruct) {
            HttpRequest request = ResteasyContext.getContextData(HttpRequest.class);
            if (request != null) {
                validate(request, instance);
            } else {
                LogMessages.LOGGER.debug(Messages.MESSAGES.skippingValidationOutsideResteasyContext());
            }
        }
    }

    @Override
    public void preDestroy(T instance) {
        delegate.preDestroy(instance);
    }

    @Override
    public void dispose(T instance) {
        delegate.dispose(instance);
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return delegate.getInjectionPoints();
    }

    @Override
    public T produce(CreationalContext<T> ctx) {
        return delegate.produce(ctx);
    }

    private PropertyInjector getPropertyInjector() {
        return usePropertyInjector ? new PropertyInjectorImpl(clazz, ResteasyProviderFactory.getInstance()) : null;
    }

    private void validate(HttpRequest request, T instance) {
        if (GetRestful.isRootResource(clazz)) {
            ResteasyProviderFactory providerFactory = ResteasyProviderFactory.getInstance();
            ContextResolver<GeneralValidatorCDI> resolver = providerFactory.getContextResolver(GeneralValidatorCDI.class,
                    MediaType.WILDCARD_TYPE);
            GeneralValidatorCDI validator = null;
            if (resolver != null) {
                validator = providerFactory.getContextResolver(GeneralValidatorCDI.class, MediaType.WILDCARD_TYPE)
                        .getContext(null);
            }
            if (validator != null && validator.isValidatableFromCDI(clazz)) {
                validator.validate(request, instance);
                validator.checkViolationsfromCDI(request);
            }
        }
    }
}

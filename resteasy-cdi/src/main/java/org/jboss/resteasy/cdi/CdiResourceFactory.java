/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.cdi;

import java.util.concurrent.CompletionStage;

import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;

import org.jboss.resteasy.cdi.i18n.LogMessages;
import org.jboss.resteasy.cdi.i18n.Messages;
import org.jboss.resteasy.plugins.server.resourcefactory.POJOResourceFactory;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.HttpResponse;
import org.jboss.resteasy.spi.PropertyInjector;
import org.jboss.resteasy.spi.ResourceFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.ResourceClass;

/**
 * A {@link ResourceFactory} that delegates resource instantiation to CDI. This allows CDI to manage the full lifecycle
 * of the resource, including constructor injection of CDI beans without requiring a no-arg constructor.
 *
 * <p>
 * Unlike {@link POJOResourceFactory}, this does NOT require a no-arg constructor or Jakarta REST-annotated constructors.
 * CDI handles all constructor injection.
 * </p>
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
class CdiResourceFactory implements ResourceFactory {

    private final Class<?> resourceClass;
    private final BeanManager beanManager;
    private final ResourceClass metadata;

    private Bean<?> bean;
    private PropertyInjector propertyInjector;

    /**
     * Create a CDI-based resource factory.
     *
     * @param resourceClass the resource class (must be a CDI bean)
     * @param beanManager   the CDI BeanManager
     * @param metadata      the resource metadata
     */
    CdiResourceFactory(final Class<?> resourceClass, final BeanManager beanManager, final ResourceClass metadata) {
        this.resourceClass = resourceClass;
        this.beanManager = beanManager;
        this.metadata = metadata;
    }

    @Override
    public void registered(final ResteasyProviderFactory factory) {
        // Resolve the CDI bean once at registration time. If another registered @Path resource class extends this one,
        // both beans are assignable to resourceClass, so only the exact bean class, or an enabled alternative, is
        // resolved. An enabled alternative must still replace the resource, as it does for @Inject.
        this.bean = BeanManagerSupport.findBean(beanManager, resourceClass);

        if (bean == null) {
            throw Messages.MESSAGES.unableToResolveBean(resourceClass.getName());
        }

        LogMessages.LOGGER.debugf("Using %s for class %s", getClass().getName(), resourceClass);

        // Create property injector for Jakarta REST property/field injection
        // This handles @Context, @PathParam, etc. on fields and setters
        this.propertyInjector = factory.getInjectorFactory().createPropertyInjector(metadata, factory);
    }

    @Override
    public Object createResource(final HttpRequest request,
            final HttpResponse response,
            final ResteasyProviderFactory factory) {
        // For a normal scoped resource the reference is a client proxy. The contextual instance is created after the
        // request filters were invoked, see ResourceMethodInvoker.invokeOnTargetAfterFilter().
        final Object resource = BeanManagerSupport.resolveBean(request, beanManager, resourceClass, bean);
        // Synchronous property injection
        final CompletionStage<Void> propertyStage = propertyInjector.inject(request, response, resource, true);
        return propertyStage == null
                ? resource
                : propertyStage.thenApply(v -> resource);
    }

    @Override
    public void unregistered() {
        // Nothing to clean up - CDI manages the lifecycle
    }

    @Override
    public Class<?> getScannableClass() {
        return resourceClass;
    }

    @Override
    public void requestFinished(final HttpRequest request, final HttpResponse response, final Object resource) {
        // CDI handles destruction based on scope
        // @RequestScoped beans are automatically destroyed at end of request
        // No explicit cleanup needed here
    }

}

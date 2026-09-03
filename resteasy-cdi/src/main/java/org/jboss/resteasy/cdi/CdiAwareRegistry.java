/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.cdi;

import jakarta.annotation.Priority;
import jakarta.enterprise.inject.spi.BeanManager;

import org.jboss.resteasy.cdi.i18n.LogMessages;
import org.jboss.resteasy.cdi.i18n.Messages;
import org.jboss.resteasy.core.ResourceMethodRegistry;
import org.jboss.resteasy.spi.Registry;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.spi.metadata.ResourceBuilder;
import org.jboss.resteasy.spi.metadata.ResourceClass;

/**
 * A {@linkplain Registry registry} which is CDI aware and will create per-request resources from the CDI container. If
 * the resource is not a CDI component, it delegates to the {@link ResourceMethodRegistry}.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@SuppressWarnings("CdiManagedBeanInconsistencyInspection")
@Priority(10)
public class CdiAwareRegistry extends ResourceMethodRegistry implements Registry {

    private volatile ResteasyBeanContainer beanContainer;
    private volatile BeanManager beanManager;

    public CdiAwareRegistry(final ResteasyProviderFactory providerFactory) {
        super(providerFactory);
    }

    @Override
    public void addPerRequestResource(final Class<?> clazz) {
        // Check if this is a CDI-managed resource
        if (isCdiManaged(clazz)) {
            // Build resource metadata
            final ResourceClass metadata = resourceBuilder.getRootResourceFromAnnotations(clazz);
            addResource(metadata, resourceBuilder, null);
        } else {
            super.addPerRequestResource(clazz);
        }
    }

    @Override
    public void addPerRequestResource(final Class<?> clazz, final ResourceBuilder resourceBuilder) {
        // Check if this is a CDI-managed resource
        if (isCdiManaged(clazz)) {
            // Build resource metadata
            final ResourceClass metadata = resourceBuilder.getRootResourceFromAnnotations(clazz);
            addResource(metadata, resourceBuilder, null);
        } else {
            // Not CDI-managed, delegate normally (will create POJOResourceFactory)
            super.addPerRequestResource(clazz, resourceBuilder);
        }
    }

    @Override
    public void addPerRequestResource(final Class<?> clazz, final String basePath) {
        if (isCdiManaged(clazz)) {
            // Build resource metadata
            final ResourceClass metadata = resourceBuilder.getRootResourceFromAnnotations(clazz);
            addResource(metadata, resourceBuilder, basePath);
        } else {
            super.addPerRequestResource(clazz, basePath);
        }
    }

    @Override
    public void addPerRequestResource(final ResourceClass clazz) {
        // Check if this is a CDI-managed resource
        if (isCdiManaged(clazz.getClazz())) {
            addResource(clazz, resourceBuilder, null);
        } else {
            super.addPerRequestResource(clazz);
        }
    }

    @Override
    public void addPerRequestResource(final ResourceClass clazz, final String basePath) {
        // Check if this is a CDI-managed resource
        if (isCdiManaged(clazz.getClazz())) {
            addResource(clazz, resourceBuilder, basePath);
        } else {
            super.addPerRequestResource(clazz, basePath);
        }
    }

    private void addResource(final ResourceClass clazz, final ResourceBuilder resourceBuilder, final String basePath) {
        // Get BeanManager
        final BeanManager beanManager = getBeanManager();

        // Create CdiResourceFactory
        final CdiResourceFactory resourceFactory = new CdiResourceFactory(
                clazz.getClazz(),
                beanManager,
                clazz);
        super.addResourceFactory(resourceFactory, resourceBuilder, basePath);

        LogMessages.LOGGER.debugf("Registered CDI resource: %s using %s", clazz.getClazz()
                .getName(), getClass().getName());
    }

    /**
     * Check if a class is managed by CDI.
     */
    private boolean isCdiManaged(final Class<?> clazz) {
        try {
            // Check if CDI is active
            if (!ResteasyCdiExtension.isCDIActive()) {
                return false;
            }
            if (resolveBeanContainer().contains(clazz)) {
                return true;
            }

        } catch (Exception e) {
            LogMessages.LOGGER.failedToDiscoverCdiBean(e, clazz.getName());
            return false;
        }
        return false;
    }

    /**
     * Lookup ManagedResources instance that was instantiated during CDI bootstrap
     *
     * @return ManagedResources instance
     */
    private ResteasyBeanContainer resolveBeanContainer() {
        if (beanContainer == null) {
            synchronized (this) {
                if (beanContainer == null) {
                    final BeanManager manager = getBeanManager();
                    final ResteasyCdiExtension extension = manager.getExtension(ResteasyCdiExtension.class);
                    final ResteasyBeanContainer container = extension.beanContainer();
                    if (container == null) {
                        throw Messages.MESSAGES.unableToResolveBean(ResteasyBeanContainer.class.getName());
                    }
                    beanContainer = container;
                }
            }
        }
        return beanContainer;
    }

    private BeanManager getBeanManager() {
        if (beanManager == null) {
            synchronized (this) {
                if (beanManager == null) {
                    beanManager = BeanManagerSupport.findBeanManager();
                }
            }
        }
        return beanManager;
    }
}

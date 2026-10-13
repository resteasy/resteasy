/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.cdi;

import java.lang.reflect.Type;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.ServletContext;

import org.jboss.resteasy.cdi.i18n.LogMessages;
import org.jboss.resteasy.cdi.i18n.Messages;
import org.jboss.resteasy.core.ResteasyContext;
import org.jboss.resteasy.spi.HttpRequest;

/**
 * BeanManager utilities for CDI support.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
class BeanManagerSupport {
    private static final String BEAN_MANAGER_ATTRIBUTE_PREFIX = "org.jboss.weld.environment.servlet.";
    private static final String REQUEST_REFERENCES = BeanManagerSupport.class.getName() + ".requestReferences";

    /**
     * Resolves the bean for the type. If several beans have the type, e.g. because subclasses of the type are beans as
     * well, only the bean whose class is the type, or an enabled alternative, is a candidate. If none of them is, all the
     * beans are resolved.
     *
     * @param beanManager the bean manager
     * @param type        the bean type
     *
     * @return the resolved bean or {@code null} if there is no bean for the type
     *
     * @throws jakarta.enterprise.inject.AmbiguousResolutionException if the bean cannot be resolved
     */
    static Bean<?> findBean(final BeanManager beanManager, final Type type) {
        return findBean(beanManager, type, beanManager.getBeans(type));
    }

    /**
     * Resolves the bean for the type from the given beans, as {@link #findBean(BeanManager, Type)} does.
     *
     * @param beanManager the bean manager
     * @param type        the bean type
     * @param beans       the beans of the type, e.g. those of an {@link jakarta.enterprise.inject.Instance}
     *
     * @return the resolved bean or {@code null} if there is no bean for the type
     *
     * @throws jakarta.enterprise.inject.AmbiguousResolutionException if the bean cannot be resolved
     */
    static Bean<?> findBean(final BeanManager beanManager, final Type type, final Set<Bean<?>> beans) {
        if (beans.size() > 1) {
            final Set<Bean<?>> candidates = beans.stream()
                    .filter(bean -> type.equals(bean.getBeanClass()) || bean.isAlternative())
                    .collect(Collectors.toSet());
            if (!candidates.isEmpty()) {
                return beanManager.resolve(candidates);
            }
        }
        return beanManager.resolve(beans);
    }

    /**
     * Returns the instance of the bean. A {@code @Dependent} instance is tracked with the request and destroyed by
     * {@link #releaseReferences(HttpRequest)}. If there is no request, e.g. when a provider is created, the instance is
     * not tracked. The request is not looked up from the current context, as a long-lived instance may be created while
     * a request is being processed.
     *
     * @param request     the request the instance is created for or {@code null} if the instance is not created for a
     *                    request
     * @param beanManager the bean manager
     * @param type        the bean type
     * @param bean        the bean
     *
     * @return the bean instance
     */
    static Object resolveBean(final HttpRequest request, final BeanManager beanManager, final Type type,
            final Bean<?> bean) {
        final CreationalContext<?> creationalContext = beanManager.createCreationalContext(bean);
        final Object instance = beanManager.getReference(bean, type, creationalContext);
        final BeanReference reference = new BeanReference(bean, creationalContext, instance);
        // Only track the instance when the caller explicitly passes the request. A caller without a request is creating
        // a long-lived instance, e.g. a provider, which must not be destroyed when a request on the current thread ends.
        if (request != null && reference.isDependent()) {
            requestReferences(request, true).add(reference);
        }
        return instance;
    }

    /**
     * Destroys the {@code @Dependent} instances created during the request. Invoking this more than once has no
     * effect.
     *
     * @param request the request which has finished
     */
    @SuppressWarnings("SynchronizationOnLocalVariableOrMethodParameter")
    static void releaseReferences(final HttpRequest request) {
        final Queue<BeanReference> references;
        // Remove the queue under the same lock the queue is created with, so a queue created concurrently is not
        // left behind unreleased
        synchronized (request) {
            references = requestReferences(request, false);
            if (references != null) {
                request.removeAttribute(REQUEST_REFERENCES);
            }
        }
        if (references != null) {
            BeanReference reference;
            // Released outside the lock, as @PreDestroy of a dependent instance runs application code
            while ((reference = references.poll()) != null) {
                reference.release();
            }
        }
    }

    /**
     * Finds the bean manager for our current context.
     * <p>
     * The following order is used to find the bean manager
     * <ol>
     * <li>Look up the JNDI name {@code java:comp/BeanManager}</li>
     * <li>Look up the JNDI name {@code java:app/BeanManager}</li>
     * <li>Use {@link CDI#getBeanManager() CDI.current().getBeanManager()}</li>
     * <li>Attempt to look up in the {@link ServletContext}</li>
     * </ol>
     * If none of those return a bean manager, a {@link RuntimeException} is thrown.
     * </p>
     *
     * @return the bean manager
     * @throws RuntimeException if the bean manager cannot be found
     */
    static BeanManager findBeanManager() {
        BeanManager beanManager;

        // Do a lookup for BeanManager in JNDI (this is the only *portable* way)
        beanManager = lookupBeanManagerInJndi("java:comp/BeanManager");
        if (beanManager != null) {
            LogMessages.LOGGER.debug(Messages.MESSAGES.foundBeanManagerAtJavaComp());
            return beanManager;
        }

        // Do a lookup for BeanManager at an alternative JNDI location (workaround for WELDINT-19)
        beanManager = lookupBeanManagerInJndi("java:app/BeanManager");
        if (beanManager != null) {
            LogMessages.LOGGER.debug(Messages.MESSAGES.foundBeanManagerAtJavaApp());
            return beanManager;
        }

        beanManager = lookupBeanManagerCDIUtil();
        if (beanManager != null) {
            LogMessages.LOGGER.debug(Messages.MESSAGES.foundBeanManagerViaCDI());
            return beanManager;
        }

        beanManager = lookupBeanManagerViaServletContext();
        if (beanManager != null) {
            LogMessages.LOGGER.debug(Messages.MESSAGES.foundBeanManagerInServletContext());
            return beanManager;
        }

        throw new RuntimeException(Messages.MESSAGES.unableToLookupBeanManager());
    }

    @SuppressWarnings({ "unchecked", "SynchronizationOnLocalVariableOrMethodParameter" })
    private static Queue<BeanReference> requestReferences(final HttpRequest request, final boolean create) {
        // Instances may be created for the request on more than one thread, e.g. a provider invoked after
        // asynchronous resumption, and the attributes of the request may not be thread-safe, so the queue is read and
        // created under a lock on the request
        synchronized (request) {
            Queue<BeanReference> references = (Queue<BeanReference>) request.getAttribute(REQUEST_REFERENCES);
            if (references == null && create) {
                references = new ConcurrentLinkedQueue<>();
                request.setAttribute(REQUEST_REFERENCES, references);
            }
            return references;
        }
    }

    private static BeanManager lookupBeanManagerInJndi(String name) {
        try {
            InitialContext ctx = new InitialContext();
            LogMessages.LOGGER.debug(Messages.MESSAGES.doingALookupForBeanManager(name));
            return (BeanManager) ctx.lookup(name);
        } catch (NamingException e) {
            LogMessages.LOGGER.debug(Messages.MESSAGES.unableToObtainBeanManager(name));
            return null;
        } catch (NoClassDefFoundError ncdfe) {
            LogMessages.LOGGER.debug(Messages.MESSAGES.unableToPerformJNDILookups());
            return null;
        }
    }

    private static BeanManager lookupBeanManagerViaServletContext() {
        BeanManager beanManager = null;
        try {
            // Look for BeanManager in ServletContext
            ServletContext servletContext = ResteasyContext.getContextData(ServletContext.class);
            // null check for RESTEASY-1009
            if (servletContext != null) {
                beanManager = (BeanManager) servletContext
                        .getAttribute(BEAN_MANAGER_ATTRIBUTE_PREFIX + BeanManager.class.getName());
                if (beanManager != null) {
                    LogMessages.LOGGER.debug(Messages.MESSAGES.foundBeanManagerInServletContext());
                    return beanManager;
                }

                // Look for BeanManager in ServletContext (the old attribute name for backwards compatibility)
                beanManager = (BeanManager) servletContext.getAttribute(BeanManager.class.getName());
                if (beanManager != null) {
                    LogMessages.LOGGER.debug(Messages.MESSAGES.foundBeanManagerInServletContext());
                    return beanManager;
                }
            }
        } catch (NoClassDefFoundError e) {
            LogMessages.LOGGER.debug(Messages.MESSAGES.unableToFindServletContextClass(), e);
        } catch (Exception e) {
            LogMessages.LOGGER.debug(Messages.MESSAGES.errorOccurredLookingUpServletContext(), e);
        }
        return beanManager;
    }

    private static BeanManager lookupBeanManagerCDIUtil() {
        try {
            return CDI.current().getBeanManager();
        } catch (NoClassDefFoundError e) {
            LogMessages.LOGGER.debug(Messages.MESSAGES.unableToFindCDIClass(), e);
        } catch (Exception e) {
            LogMessages.LOGGER.debug(Messages.MESSAGES.errorOccurredLookingUpViaCDIUtil(), e);
        }
        return null;
    }
}

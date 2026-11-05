/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.cdi;

import java.lang.reflect.Type;
import java.util.Set;
import java.util.stream.Collectors;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.ServletContext;

import org.jboss.resteasy.cdi.i18n.LogMessages;
import org.jboss.resteasy.cdi.i18n.Messages;
import org.jboss.resteasy.core.ResteasyContext;

/**
 * BeanManager utilities for CDI support.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
class BeanManagerSupport {
    private static final String BEAN_MANAGER_ATTRIBUTE_PREFIX = "org.jboss.weld.environment.servlet.";

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

/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.cdi;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.Bean;

/**
 * A reference to a bean and the {@link CreationalContext} it was created with.
 *
 * @param bean              the bean
 * @param creationalContext the creational context the reference was created with
 * @param instance          the reference
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
record BeanReference(Bean<?> bean, CreationalContext<?> creationalContext, Object instance) {

    /**
     * Indicates whether the bean is {@link Dependent}, in which case the caller owns the lifecycle of the instance. For
     * any other scope, the context of the scope creates and destroys the instance.
     *
     * @return {@code true} if the bean is {@link Dependent}
     */
    boolean isDependent() {
        return bean.getScope() == Dependent.class;
    }

    /**
     * Destroys the instance if the bean is {@link Dependent}. For any other scope this does nothing, as the context of
     * the scope may still use the creational context.
     */
    void release() {
        if (isDependent()) {
            creationalContext.release();
        }
    }
}
/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.cdi;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.ws.rs.core.Context;

/**
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
@SuppressWarnings("ClassExplicitlyAnnotation")
class ContextLiteral extends AnnotationLiteral<Context> implements Context {
    static final Context INSTANCE = new ContextLiteral();

    private ContextLiteral() {
    }
}

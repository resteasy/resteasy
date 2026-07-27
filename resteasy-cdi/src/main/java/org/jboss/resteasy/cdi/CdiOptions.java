/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.cdi;

import java.util.function.Supplier;

import org.jboss.resteasy.spi.config.Options;

/**
 * Configuration options for the RESTEasy CDI integration. These options extend the {@link Options} framework and
 * can be configured via system properties, environment variables, or MicroProfile Config.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 */
public class CdiOptions<T> extends Options<T> {

    /**
     * An option for enabling or disabling the enhanced CDI support. When enabled, Jakarta REST annotations such as
     * {@link jakarta.ws.rs.core.Context @Context}, {@link jakarta.ws.rs.QueryParam @QueryParam},
     * {@link jakarta.ws.rs.PathParam @PathParam}, {@link jakarta.ws.rs.HeaderParam @HeaderParam},
     * {@link jakarta.ws.rs.MatrixParam @MatrixParam}, {@link jakarta.ws.rs.CookieParam @CookieParam},
     * {@link jakarta.ws.rs.FormParam @FormParam}, and {@link jakarta.ws.rs.BeanParam @BeanParam} are treated as CDI
     * qualifiers, allowing parameter values to be injected via CDI.
     * <p>
     * The default is {@code true}.
     * </p>
     */
    public static final CdiOptions<Boolean> ENHANCED_CDI_SUPPORT = new CdiOptions<>("dev.resteasy.cdi.enhanced.enabled",
            Boolean.class, () -> true);

    private CdiOptions(final String key, final Class<T> name, final Supplier<T> dftValue) {
        super(key, name, dftValue);
    }
}

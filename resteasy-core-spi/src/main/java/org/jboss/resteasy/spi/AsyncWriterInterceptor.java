/*
 * Copyright (c) 2011, 2017 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.jboss.resteasy.spi;

import java.util.concurrent.CompletionStage;

import javax.ws.rs.ext.WriterInterceptor;

/**
 * Interface for message body writer interceptors that wrap around calls
 * to {@link javax.ws.rs.ext.MessageBodyWriter#writeTo}.
 *
 * <p>
 * Providers implementing {@code WriterInterceptor} contract must be either programmatically
 * registered in an API runtime or must be annotated with
 * {@link javax.ws.rs.ext.Provider &#64;Provider} annotation to be automatically discovered
 * by the runtime during a provider scanning phase.
 * Message body interceptor instances may also be discovered and
 * bound {@link javax.ws.rs.container.DynamicFeature dynamically} to particular resource methods.
 * </p>
 *
 * @author Santiago Pericas-Geertsen
 * @author Bill Burke
 * @author Marek Potociar
 * @see MessageBodyWriter
 * @since 2.0
 */
public interface AsyncWriterInterceptor extends WriterInterceptor {

    /**
     * Interceptor method wrapping calls to {@link MessageBodyWriter#writeTo} method.
     * The parameters of the wrapped method called are available from {@code context}.
     * Implementations of this method SHOULD explicitly call
     * {@link WriterInterceptorContext#proceed} to invoke the next interceptor in the chain,
     * and ultimately the wrapped {@code MessageBodyWriter.writeTo} method.
     *
     * @param context invocation context.
     * @throws java.io.IOException if an IO error arises or is thrown by the wrapped
     *                             {@code MessageBodyWriter.writeTo} method.
     * @throws javax.ws.rs.WebApplicationException
     *                             thrown by the wrapped {@code MessageBodyWriter.writeTo} method.
     */
    CompletionStage<Void> asyncAroundWriteTo(AsyncWriterInterceptorContext context);
}

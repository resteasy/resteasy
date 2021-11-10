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

import jakarta.ws.rs.ext.WriterInterceptor;

/**
 * Writer interceptors which support async IO.
 */
public interface AsyncWriterInterceptor extends WriterInterceptor {

    /**
     * Interceptor method wrapping calls to {@link AsyncMessageBodyWriter#asyncWriteTo} method.
     * The parameters of the wrapped method called are available from {@code context}.
     * Implementations of this method SHOULD explicitly call
     * {@link AsyncWriterInterceptorContext#asyncProceed} to invoke the next interceptor in the chain,
     * and ultimately the wrapped {@code AsyncMessageBodyWriter.asyncWriteTo} method.
     *
     * @param context invocation context.
     * @return a {@link CompletionStage} indicating completion
     * @throws java.io.IOException if an IO error arises or is thrown by the wrapped
     *                             {@code AsyncMessageBodyWriter.asyncWriteTo} method, in the returned {@link CompletionStage}.
     * @throws jakarta.ws.rs.WebApplicationException
     *                             thrown by the wrapped {@code AsyncMessageBodyWriter.asyncWriteTo} method, in the returned {@link CompletionStage}.
     */
    CompletionStage<Void> asyncAroundWriteTo(AsyncWriterInterceptorContext context);
}

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011-2012 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * http://glassfish.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package javax.ws.rs.client;

import java.net.URI;

import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;

/**
 * Client is the main entry point to the fluent API used to build and execute client
 * requests in order to consume responses returned.
 * <p/>
 * Clients are heavy-weight objects that manage the client-side communication
 * infrastructure. Initialization as well as disposal of a {@code Client} instance
 * may be a rather expensive operation. It is therefore advised to construct only
 * a small number of {@code Client} instances in the application. Client instances
 * must be {@link #close() properly closed} before being disposed to avoid leaking
 * resources.
 *
 * @author Marek Potociar
 * @see Configuration
 * @since 2.0
 */
public interface Client {

    /**
     * Close client instance and all it's associated resources. Subsequent calls
     * have no effect and are ignored. Once the client is closed, invoking any
     * other method on the client instance would result in an {@link IllegalStateException}
     * being thrown.
     * <p/>
     * Calling this method effectively invalidates all {@link WebTarget resource targets}
     * produced by the client instance. Invoking any method on such targets once the client
     * is closed would result in an {@link IllegalStateException} being thrown.
     */
    void close();

    /**
     * Get access to the underlying {@link Configuration configuration} of the
     * client instance.
     *
     * @return a mutable client configuration.
     */
    public Configuration configuration();

    /**
     * Build a new web resource target.
     *
     * @param uri web resource URI.
     * @return web resource target bound to the provided URI.
     * @throws IllegalArgumentException in case the supplied string is not a valid URI.
     * @throws NullPointerException in case the supplied argument is null.
     */
    WebTarget target(String uri) throws IllegalArgumentException, NullPointerException;

    /**
     * Build a new web resource target.
     *
     * @param uri web resource URI.
     * @return web resource target bound to the provided URI.
     * @throws NullPointerException in case the supplied argument is null.
     */
    WebTarget target(URI uri) throws NullPointerException;

    /**
     * Build a new web resource target.
     *
     * @param uriBuilder web resource URI represented as URI builder.
     * @return web resource target bound to the provided URI.
     * @throws NullPointerException in case the supplied argument is null.
     */
    WebTarget target(UriBuilder uriBuilder) throws NullPointerException;

    /**
     * Build a new web resource target.
     *
     * @param link link to a web resource.
     * @return web resource target bound to the linked web resource.
     * @throws NullPointerException in case the supplied argument is null.
     */
    WebTarget target(Link link) throws NullPointerException;

    /**
     * <p>Build an invocation from a link. The method and URI are obtained from the
     * link. The HTTP Accept header is initialized to the value of the "produces"
     * parameter in the link. If the operation requires an entity, use the overloaded
     * form of this method.</p>
     *
     * <p>This method will throw an {@link java.lang.IllegalArgumentException} if there
     * is not enough information to build an invocation (e.g. no HTTP method or entity
     * when required).</p>
     *
     * @param link link to build invocation from.
     * @return newly created invocation.
     * @throws NullPointerException in case argument is null.
     * @throws IllegalArgumentException in case link is incomplete to build invocation.
     */
    Invocation invocation(Link link) throws NullPointerException, IllegalArgumentException;

    /**
     * <p>Build an invocation from a link. The method and URI are obtained from the
     * link. The HTTP Accept header is initialized to the value of the "produces"
     * parameter in the link.If the operation does not require an entity, use the
     * overloaded form of this method.</p>
     *
     * <p>This method will throw an {@link java.lang.IllegalArgumentException} if there
     * is not enough information to build and invocation (e.g. no HTTP method).</p>
     *
     * @param link link to build invocation from.
     * @param entity request entity to be send when the invocation is invoked.
     * @return newly created invocation.
     * @throws NullPointerException in case argument is null.
     * @throws IllegalArgumentException in case link is incomplete to build invocation.
     */
    Invocation invocation(Link link, Entity<?> entity) throws NullPointerException, IllegalArgumentException;
}

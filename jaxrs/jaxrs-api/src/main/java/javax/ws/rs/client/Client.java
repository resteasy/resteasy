/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2011-2013 Oracle and/or its affiliates. All rights reserved.
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

import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.UriBuilder;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;

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
 * @see javax.ws.rs.core.Configurable
 * @since 2.0
 */
public interface Client extends Configurable<Client> {

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
    public void close();

    /**
     * Build a new web resource target.
     *
     * @param uri web resource URI. May contain template parameters. Must not be {@code null}.
     * @return web resource target bound to the provided URI.
     * @throws IllegalArgumentException in case the supplied string is not a valid URI template.
     * @throws NullPointerException     in case the supplied argument is {@code null}.
     */
    public WebTarget target(String uri);

    /**
     * Build a new web resource target.
     *
     * @param uri web resource URI. Must not be {@code null}.
     * @return web resource target bound to the provided URI.
     * @throws NullPointerException in case the supplied argument is {@code null}.
     */
    public WebTarget target(URI uri);

    /**
     * Build a new web resource target.
     *
     * @param uriBuilder web resource URI represented as URI builder. Must not be {@code null}.
     * @return web resource target bound to the provided URI.
     * @throws NullPointerException in case the supplied argument is {@code null}.
     */
    public WebTarget target(UriBuilder uriBuilder);

    /**
     * Build a new web resource target.
     *
     * @param link link to a web resource. Must not be {@code null}.
     * @return web resource target bound to the linked web resource.
     * @throws NullPointerException in case the supplied argument is {@code null}.
     */
    public WebTarget target(Link link);

    /**
     * <p>Build an invocation builder from a link. It uses the URI and the type
     * of the link to initialize the invocation builder. The type is used as the
     * initial value for the HTTP Accept header, if present.</p>
     *
     * @param link link to build invocation from. Must not be {@code null}.
     * @return newly created invocation builder.
     * @throws NullPointerException     in case link is {@code null}.
     */
    public Invocation.Builder invocation(Link link);

    /**
     * Get the SSL context configured to be used with the current client run-time.
     *
     * @return SSL context configured to be used with the current client run-time.
     */
    public SSLContext getSslContext();

    /**
     * Get the hostname verifier configured in the client or {@code null} in case
     * no hostname verifier has been configured.
     *
     * @return client hostname verifier or {@code null} if not set.
     */
    public HostnameVerifier getHostnameVerifier();
}

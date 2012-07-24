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

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Map;

/**
 * A resource target identified by the resource URI.
 *
 * @author Marek Potociar
 * @since 2.0
 */
public interface WebTarget {

    // Getters

    /**
     * Get the URI identifying the resource.
     *
     * @return the resource URI.
     */
    public URI getUri();

    /**
     * Get the URI builder initialized with the {@link URI} of the current
     * resource target. The returned URI builder is detached from the target,
     * i.e. any updates in the URI builder MUST NOT have any effects on the
     * URI of the originating target.
     *
     * @return the initialized URI builder.
     */
    public UriBuilder getUriBuilder();

    /**
     * Get access to the underlying {@link Configuration configuration}.
     *
     * @return a mutable configuration bound to the instance.
     */
    public Configuration configuration();

    /**
     * Create a new {@code WebTarget} instance by appending path to the URI of
     * the current target instance.
     * <p />
     * When constructing the final path, a '/' separator will be inserted between
     * the existing path and the supplied path if necessary. Existing '/' characters
     * are preserved thus a single value can represent multiple URI path segments.
     * <p />
     * A snapshot of the present configuration of the current (parent) target
     * instance is taken and is inherited by the newly constructed (child) target
     * instance.
     *
     * @param path the path, may contain URI template parameters.
     * @return a new target instance.
     * @throws NullPointerException if path is null.
     */
    public WebTarget path(String path) throws NullPointerException;

    /**
     * Create a new {@code WebTarget} instance by replacing existing path parameter
     * in the URI of the current target instance with a supplied value.
     * <p />
     * A snapshot of the present configuration of the current (parent) target
     * instance is taken and is inherited by the newly constructed (child) target
     * instance.
     *
     * @param name  path parameter template name.
     * @param value value to be used to replace the template.
     * @return a new target instance.
     * @throws IllegalArgumentException if there is no such path parameter.
     * @throws NullPointerException     if name or value is null.
     */
    public WebTarget pathParam(String name, Object value)
            throws IllegalArgumentException, NullPointerException;

    /**
     * Create a new {@code WebTarget} instance by replacing one or more existing path parameters
     * in the URI of the current target instance with supplied values.
     * <p />
     * A snapshot of the present configuration of the current (parent) target
     * instance is taken and is inherited by the newly constructed (child) target
     * instance.
     *
     * @param parameters a map of URI template parameter names and values.
     * @return a new target instance.
     * @throws IllegalArgumentException if the supplied map is empty.
     * @throws NullPointerException     if the parameter map or any of the names or values is null.
     */
    public WebTarget pathParams(Map<String, Object> parameters)
            throws IllegalArgumentException, NullPointerException;

    /**
     * Create a new {@code WebTarget} instance by appending a matrix parameter to
     * the existing set of matrix parameters of the current final segment of the
     * URI of the current target instance. If multiple values are supplied
     * the parameter will be added once per value. Note that the matrix parameters
     * are tied to a particular path segment; appending a value to an existing
     * matrix parameter name  will not affect the position of the matrix parameter
     * in the URI path.
     * <p />
     * A snapshot of the present configuration of the current (parent) target
     * instance is taken and is inherited by the newly constructed (child) target
     * instance.
     *
     * @param name   the matrix parameter name, may contain URI template parameters.
     * @param values the matrix parameter value(s), each object will be converted
     *               to a {@code String} using its {@code toString()} method. Stringified
     *               values may contain URI template parameters.
     * @return a new target instance.
     * @throws NullPointerException if the name or any of the values is null.
     * @see <a href="http://www.w3.org/DesignIssues/MatrixURIs.html">Matrix URIs</a>
     */
    public WebTarget matrixParam(String name, Object... values)
            throws NullPointerException;

    /**
     * Create a new {@code WebTarget} instance by adding a query parameter to the URI
     * of the current target instance. If multiple values are supplied the parameter
     * will be added once per value.
     * <p />
     * A snapshot of the present configuration of the current (parent) target
     * instance is taken and is inherited by the newly constructed (child) target
     * instance.
     *
     * @param name   the query parameter name, may contain URI template parameters
     * @param values the query parameter value(s), each object will be converted
     *               to a {@code String} using its {@code toString()} method. Stringified
     *               values may contain URI template parameters.
     * @return a new target instance.
     * @throws NullPointerException if name or any of the values is {@code null}.
     */
    public WebTarget queryParam(String name, Object... values)
            throws NullPointerException;

    /**
     * Create a new {@code WebTarget} instance by adding one or more query parameters and
     * respective values to the URI of the current target instance.
     * <p />
     * A snapshot of the present configuration of the current (parent) target
     * instance is taken and is inherited by the newly constructed (child) target
     * instance.
     *
     * @param parameters a map of query parameter names and values.
     * @return a new target instance.
     * @throws IllegalArgumentException if the supplied map is empty.
     * @throws NullPointerException     if the parameter map or any of the names or values is null.
     */
    public WebTarget queryParams(MultivaluedMap<String, Object> parameters)
            throws IllegalArgumentException, NullPointerException;

    /**
     * Start building a request to the targeted web resource.
     *
     * @return builder for a request targeted at the URI referenced by this target instance.
     */
    public Invocation.Builder request();

    /**
     * Start building a request to the targeted web resource and define the accepted
     * response media types.
     *
     * @param acceptedResponseTypes accepted response media types.
     * @return builder for a request targeted at the URI referenced by this target instance.
     */
    public Invocation.Builder request(String... acceptedResponseTypes);

    /**
     * Start building a request to the targeted web resource and define the accepted
     * response media types.
     *
     * @param acceptedResponseTypes accepted response media types.
     * @return builder for a request targeted at the URI referenced by this target instance.
     */
    public Invocation.Builder request(MediaType... acceptedResponseTypes);
}

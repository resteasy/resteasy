/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2010-2012 Oracle and/or its affiliates. All rights reserved.
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
package javax.ws.rs.core;

import java.net.URI;
import java.util.List;

/**
 * An injectable interface that provides access to application and request
 * URI information. Relative URIs are relative to the base URI of the
 * application, see {@link #getBaseUri}.
 *
 * <p>All methods throw <code>java.lang.IllegalStateException</code>
 * if called outside the scope of a request (e.g. from a provider constructor).</p>
 *
 * @author Paul Sandoz
 * @author Marc Hadley
 * @see Context
 * @since 1.0
 */
public interface UriInfo {

    /**
     * Get the path of the current request relative to the base URI as a string.
     * All sequences of escaped octets are decoded, equivalent to
     * {@link #getPath(boolean) getPath(true)}.
     *
     * @return the relative URI path.
     * @throws java.lang.IllegalStateException if called outside the scope of
     *     a request.
     */
    public String getPath();

    /**
     * Get the path of the current request relative to the base URI as a string.
     *
     * @param decode controls whether sequences of escaped octets are decoded
     *     ({@code true}) or not ({@code false}).
     * @return the relative URI path
     * @throws java.lang.IllegalStateException if called outside the scope of
     *     a request.
     */
    public String getPath(boolean decode);

    /**
     * Get the path of the current request relative to the base URI as a
     * list of {@link PathSegment}. This method is useful when the
     * path needs to be parsed, particularly when matrix parameters may be
     * present in the path. All sequences of escaped octets in path segments
     * and matrix parameter values are decoded,
     * equivalent to <code>getPathSegments(true)</code>.
     * @return an unmodifiable list of {@link PathSegment}. The matrix parameter
     * map of each path segment is also unmodifiable.
     * @throws java.lang.IllegalStateException if called outside the scope of a request
     * @see PathSegment
     * @see <a href="http://www.w3.org/DesignIssues/MatrixURIs.html">Matrix URIs</a>
     */
    public List<PathSegment> getPathSegments();

    /**
     * Get the path of the current request relative to the base URI as a list of
     * {@link PathSegment}. This method is useful when the path needs to be parsed,
     * particularly when matrix parameters may be present in the path.
     *
     * @param decode controls whether sequences of escaped octets in path segments
     *     and matrix parameter values are decoded ({@code true}) or not ({@code false}).
     * @return an unmodifiable list of {@link PathSegment}. The matrix parameter
     *     map of each path segment is also unmodifiable.
     * @throws java.lang.IllegalStateException if called outside the scope of a request
     * @see PathSegment
     * @see <a href="http://www.w3.org/DesignIssues/MatrixURIs.html">Matrix URIs</a>
     */
    public List<PathSegment> getPathSegments(boolean decode);

    /**
     * Get the absolute request URI including any query parameters.
     * @return the absolute request URI
     * @throws java.lang.IllegalStateException if called outside the scope of a request
     */
    public URI getRequestUri();

    /**
     * Get the absolute request URI in the form of a UriBuilder.
     *
     * @return a UriBuilder initialized with the absolute request URI.
     * @throws java.lang.IllegalStateException if called outside the scope of a
     *     request.
     */
    public UriBuilder getRequestUriBuilder();

    /**
     * Get the absolute path of the request. This includes everything preceding
     * the path (host, port etc) but excludes query parameters.
     * This is a shortcut for
     * {@code uriInfo.getBaseUri().resolve(uriInfo.getPath()).}
     *
     * @return the absolute path of the request.
     * @throws java.lang.IllegalStateException if called outside the scope of a
     *     request.
     */
    public URI getAbsolutePath();

    /**
     * Get the absolute path of the request in the form of a UriBuilder.
     * This includes everything preceding the path (host, port etc) but excludes
     * query parameters.
     *
     * @return a UriBuilder initialized with the absolute path of the request.
     * @throws java.lang.IllegalStateException if called outside the scope of a
     *     request.
     */
    public UriBuilder getAbsolutePathBuilder();

    /**
     * Get the base URI of the application. URIs of root resource classes
     * are all relative to this base URI.
     *
     * @return the base URI of the application.
     */
    public URI getBaseUri();

    /**
     * Get the base URI of the application in the form of a UriBuilder.
     *
     * @return a UriBuilder initialized with the base URI of the application.
     */
    public UriBuilder getBaseUriBuilder();

    /**
     * Get the values of any embedded URI template parameters. All sequences of
     * escaped octets are decoded, equivalent to
     * {@link #getPathParameters(boolean) getPathParameters(true)}.
     *
     * @return an unmodifiable map of parameter names and values.
     * @throws java.lang.IllegalStateException if called outside the scope of a
     *     request.
     * @see javax.ws.rs.Path
     * @see javax.ws.rs.PathParam
     */
    public MultivaluedMap<String, String> getPathParameters();

    /**
     * Get the values of any embedded URI template parameters.
     *
     * @param decode controls whether sequences of escaped octets are decoded
     *     ({@code true}) or not ({@code false}).
     * @return an unmodifiable map of parameter names and values
     * @throws java.lang.IllegalStateException if called outside the scope of a
     *     request.
     * @see javax.ws.rs.Path
     * @see javax.ws.rs.PathParam
     */
    public MultivaluedMap<String, String> getPathParameters(boolean decode);

    /**
     * Get the URI query parameters of the current request. The map keys are the
     * names of the query parameters with any escaped characters decoded. All sequences
     * of escaped octets in parameter names and values are decoded, equivalent to
     * {@link #getQueryParameters(boolean) getQueryParameters(true)}.
     *
     * @return an unmodifiable map of query parameter names and values.
     * @throws java.lang.IllegalStateException if called outside the scope of a
     *     request.
     */
    public MultivaluedMap<String, String> getQueryParameters();

    /**
     * Get the URI query parameters of the current request. The map keys are the
     * names of the query parameters with any escaped characters decoded.
     *
     * @param decode controls whether sequences of escaped octets in parameter
     *     names and values are decoded ({@code true}) or not ({@code false}).
     * @return an unmodifiable map of query parameter names and values.
     * @throws java.lang.IllegalStateException if called outside the scope of a
     *     request.
     */
    public MultivaluedMap<String, String> getQueryParameters(boolean decode);

    /**
     * Get a read-only list of URIs for matched resources. Each entry is a
     * relative URI that matched a resource class, a
     * sub-resource method or a sub-resource locator. All sequences of escaped
     * octets are decoded, equivalent to {@code getMatchedURIs(true)}.
     * Entries do not include query parameters but do include matrix parameters
     * if present in the request URI. Entries are ordered in reverse request
     * URI matching order, with the current resource URI first.  E.g. given the
     * following resource classes:
     *
     * <pre>&#064;Path("foo")
     *public class FooResource {
     *  &#064;GET
     *  public String getFoo() {...}
     *
     *  &#064;Path("bar")
     *  public BarResource getBarResource() {...}
     *}
     *
     *public class BarResource {
     *  &#064;GET
     *  public String getBar() {...}
     *}
     * </pre>
     *
     * <p>The values returned by this method based on request uri and where
     * the method is called from are:</p>
     *
     * <table border="1">
     * <tr>
     *   <th>Request</th>
     *   <th>Called from</th>
     *   <th>Value(s)</th>
     * </tr>
     * <tr>
     *   <td>GET /foo</td>
     *   <td>FooResource.getFoo</td>
     *   <td>foo</td>
     * </tr>
     * <tr>
     *   <td>GET /foo/bar</td>
     *   <td>FooResource.getBarResource</td>
     *   <td>foo/bar, foo</td>
     * </tr>
     * <tr>
     *   <td>GET /foo/bar</td>
     *   <td>BarResource.getBar</td>
     *   <td>foo/bar, foo</td>
     * </tr>
     * </table>
     *
     *
     * @return a read-only list of URI paths for matched resources.
     */
    public List<String> getMatchedURIs();

    /**
     * Get a read-only list of URIs for matched resources. Each entry is a
     * relative URI that matched a resource class, a sub-resource
     * method or a sub-resource locator. Entries do not include query
     * parameters but do include matrix parameters if present in the request URI.
     * Entries are ordered in reverse request URI matching order, with the
     * current resource URI first. See {@link #getMatchedURIs()} for an
     * example.
     *
     * @param decode controls whether sequences of escaped octets are decoded
     *     ({@code true}) or not ({@code false}).
     * @return a read-only list of URI paths for matched resources.
     */
    public List<String> getMatchedURIs(boolean decode);

    /**
     * Get a read-only list of the currently matched resource class instances.
     * Each entry is a resource class instance that matched the request URI
     * either directly or via a sub-resource method or a sub-resource locator.
     * Entries are ordered according to reverse request URI matching order,
     * with the current resource first. E.g. given the following resource
     * classes:
     *
     * <pre>&#064;Path("foo")
     *public class FooResource {
     *  &#064;GET
     *  public String getFoo() {...}
     *
     *  &#064;Path("bar")
     *  public BarResource getBarResource() {...}
     *}
     *
     *public class BarResource {
     *  &#064;GET
     *  public String getBar() {...}
     *}
     * </pre>
     *
     * <p>The values returned by this method based on request uri and where
     * the method is called from are:</p>
     *
     * <table border="1">
     * <tr>
     *   <th>Request</th>
     *   <th>Called from</th>
     *   <th>Value(s)</th>
     * </tr>
     * <tr>
     *   <td>GET /foo</td>
     *   <td>FooResource.getFoo</td>
     *   <td>FooResource</td>
     * </tr>
     * <tr>
     *   <td>GET /foo/bar</td>
     *   <td>FooResource.getBarResource</td>
     *   <td>FooResource</td>
     * </tr>
     * <tr>
     *   <td>GET /foo/bar</td>
     *   <td>BarResource.getBar</td>
     *   <td>BarResource, FooResource</td>
     * </tr>
     * </table>
     *
     * @return a read-only list of matched resource class instances.
     */
    public List<Object> getMatchedResources();
}

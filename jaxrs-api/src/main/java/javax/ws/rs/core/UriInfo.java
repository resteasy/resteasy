/*
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the "License").  You may not use this file except
 * in compliance with the License.
 * 
 * You can obtain a copy of the license at
 * http://www.opensource.org/licenses/cddl1.php
 * See the License for the specific language governing
 * permissions and limitations under the License.
 */

/*
 * UriInfo.java
 *
 * Created on April 13, 2007, 2:55 PM
 *
 */

package javax.ws.rs.core;

import java.net.URI;
import java.util.List;

/**
 * An injectable interface that provides access to application and request
 * URI information. Relative URIs are relative to the base URI of the
 * application, see {@link #getBaseUri}.
 * <p/>
 * <p>All methods throw <code>java.lang.IllegalStateException</code>
 * if called outside the scope of a request (e.g. from a provider constructor).</p>
 *
 * @see Context
 */
public interface UriInfo
{

   /**
    * Get the path of the current request relative to the base URI as
    * a string. All sequences of escaped octets are decoded, equivalent to
    * <code>getPath(true)</code>.
    *
    * @return the relative URI path
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    */
   public String getPath();

   /**
    * Get the path of the current request relative to the base URI as
    * a string.
    *
    * @param decode controls whether sequences of escaped octets are decoded
    *               (true) or not (false).
    * @return the relative URI path
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    */
   public String getPath(boolean decode);

   /**
    * Get the path of the current request relative to the base URI as a
    * list of {@link PathSegment}. This method is useful when the
    * path needs to be parsed, particularly when matrix parameters may be
    * present in the path. All sequences of escaped octets in path segments
    * and matrix parameter values are decoded,
    * equivalent to <code>getPathSegments(true)</code>.
    *
    * @return an unmodifiable list of {@link PathSegment}. The matrix parameter
    *         map of each path segment is also unmodifiable.
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    * @see PathSegment
    * @see <a href="http://www.w3.org/DesignIssues/MatrixURIs.html">Matrix URIs</a>
    */
   public List<PathSegment> getPathSegments();

   /**
    * Get the path of the current request relative to the base URI as a
    * list of {@link PathSegment}. This method is useful when the
    * path needs to be parsed, particularly when matrix parameters may be
    * present in the path.
    *
    * @param decode controls whether sequences of escaped octets in path segments
    *               and matrix parameter values are decoded (true) or not (false).
    * @return an unmodifiable list of {@link PathSegment}. The matrix parameter
    *         map of each path segment is also unmodifiable.
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    * @see PathSegment
    * @see <a href="http://www.w3.org/DesignIssues/MatrixURIs.html">Matrix URIs</a>
    */
   public List<PathSegment> getPathSegments(boolean decode);

   /**
    * Get the absolute request URI including any query parameters.
    *
    * @return the absolute request URI
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    */
   public URI getRequestUri();

   /**
    * Get the absolute request URI in the form of a UriBuilder.
    *
    * @return a UriBuilder initialized with the absolute request URI
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    */
   public UriBuilder getRequestUriBuilder();

   /**
    * Get the absolute path of the request. This includes everything preceding
    * the path (host, port etc) but excludes query parameters.
    * This is a shortcut for
    * <code>uriInfo.getBase().resolve(uriInfo.getPath()).</code>
    *
    * @return the absolute path of the request
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    */
   public URI getAbsolutePath();

   /**
    * Get the absolute path of the request in the form of a UriBuilder.
    * This includes everything preceding the path (host, port etc) but excludes
    * query parameters.
    *
    * @return a UriBuilder initialized with the absolute path of the request
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    */
   public UriBuilder getAbsolutePathBuilder();

   /**
    * Get the base URI of the application. URIs of root resource classes
    * are all relative to this base URI.
    *
    * @return the base URI of the application
    */
   public URI getBaseUri();

   /**
    * Get the base URI of the application in the form of a UriBuilder.
    *
    * @return a UriBuilder initialized with the base URI of the application.
    */
   public UriBuilder getBaseUriBuilder();

   /**
    * Get the values of any embedded URI template parameters.
    * All sequences of escaped octets are decoded,
    * equivalent to <code>getPathParameters(true)</code>.
    *
    * @return an unmodifiable map of parameter names and values
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    * @see javax.ws.rs.Path
    * @see javax.ws.rs.PathParam
    */
   public MultivaluedMap<String, String> getPathParameters();

   /**
    * Get the values of any embedded URI template parameters.
    *
    * @param decode controls whether sequences of escaped octets are decoded
    *               (true) or not (false).
    * @return an unmodifiable map of parameter names and values
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    * @see javax.ws.rs.Path
    * @see javax.ws.rs.PathParam
    */
   public MultivaluedMap<String, String> getPathParameters(boolean decode);

   /**
    * Get the URI query parameters of the current request.
    * The map keys are the names of the query parameters with any
    * escaped characters decoded.
    * All sequences of escaped octets in parameter values are decoded,
    * equivalent to <code>getQueryParameters(true)</code>.
    *
    * @return an unmodifiable map of query parameter names and values
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    */
   public MultivaluedMap<String, String> getQueryParameters();

   /**
    * Get the URI query parameters of the current request.
    * The map keys are the names of the query parameters with any
    * escaped characters decoded.
    *
    * @param decode controls whether sequences of escaped octets in parameter
    *               values are decoded (true) or not (false).
    * @return an unmodifiable map of query parameter names and values
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
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
    * <p/>
    * <pre>&#064;Path("foo")
    * public class FooResource {
    *  &#064;GET
    *  public String getFoo() {...}
    * <p/>
    *  &#064;Path("bar")
    *  public BarResource getBarResource() {...}
    * }
    * <p/>
    * public class BarResource {
    *  &#064;GET
    *  public String getBar() {...}
    * }
    * </pre>
    * <p/>
    * <p>The values returned by this method based on request uri and where
    * the method is called from are:</p>
    * <p/>
    * <table border="1">
    * <tr>
    * <th>Request</th>
    * <th>Called from</th>
    * <th>Value(s)</th>
    * </tr>
    * <tr>
    * <td>GET /foo</td>
    * <td>FooResource.getFoo</td>
    * <td>foo</td>
    * </tr>
    * <tr>
    * <td>GET /foo/bar</td>
    * <td>FooResource.getBarResource</td>
    * <td>foo/bar, foo</td>
    * </tr>
    * <tr>
    * <td>GET /foo/bar</td>
    * <td>BarResource.getBar</td>
    * <td>foo/bar, foo</td>
    * </tr>
    * </table>
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
    *               (true) or not (false).
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
    * <p/>
    * <pre>&#064;Path("foo")
    * public class FooResource {
    *  &#064;GET
    *  public String getFoo() {...}
    * <p/>
    *  &#064;Path("bar")
    *  public BarResource getBarResource() {...}
    * }
    * <p/>
    * public class BarResource {
    *  &#064;GET
    *  public String getBar() {...}
    * }
    * </pre>
    * <p/>
    * <p>The values returned by this method based on request uri and where
    * the method is called from are:</p>
    * <p/>
    * <table border="1">
    * <tr>
    * <th>Request</th>
    * <th>Called from</th>
    * <th>Value(s)</th>
    * </tr>
    * <tr>
    * <td>GET /foo</td>
    * <td>FooResource.getFoo</td>
    * <td>FooResource</td>
    * </tr>
    * <tr>
    * <td>GET /foo/bar</td>
    * <td>FooResource.getBarResource</td>
    * <td>FooResource</td>
    * </tr>
    * <tr>
    * <td>GET /foo/bar</td>
    * <td>BarResource.getBar</td>
    * <td>BarResource, FooResource</td>
    * </tr>
    * </table>
    *
    * @return a read-only list of matched resource class instances.
    */
   public List<Object> getMatchedResources();
}

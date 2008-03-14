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
 * <p>All methods except {@link #getBaseUri} and
 * {@link #getBaseUriBuilder} throw <code>java.lang.IllegalStateException</code>
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
    * present in the path. All sequences of escaped octets are decoded,
    * equivalent to <code>getPathSegments(true)</code>.
    *
    * @return an unmodifiable list of {@link PathSegment}. The matrix parameter
    *         map of each path segment is also unmodifiable.
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    * @see PathSegment
    */
   public List<PathSegment> getPathSegments();

   /**
    * Get the path of the current request relative to the base URI as a
    * list of {@link PathSegment}. This method is useful when the
    * path needs to be parsed, particularly when matrix parameters may be
    * present in the path.
    *
    * @param decode controls whether sequences of escaped octets are decoded
    *               (true) or not (false).
    * @return an unmodifiable list of {@link PathSegment}. The matrix parameter
    *         map of each path segment is also unmodifiable.
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    * @see PathSegment
    */
   public List<PathSegment> getPathSegments(boolean decode);

   /**
    * Get the absolute request URI. This includes query parameters and
    * any supplied fragment.
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
    * the path (host, port etc) but excludes query parameters and fragment.
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
    * This includes everything preceding
    * the path (host, port etc) but excludes query parameters and fragment.
    *
    * @return a UriBuilder initialized with the absolute path of the request
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    */
   public UriBuilder getAbsolutePathBuilder();

   /**
    * Get the base URI of the application. URIs of resource beans
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
    * equivalent to <code>getTemplateParameters(true)</code>.
    *
    * @return an unmodifiable map of parameter names and values
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    * @see javax.ws.rs.Path
    */
   public MultivaluedMap<String, String> getTemplateParameters();

   /**
    * Get the values of any embedded URI template parameters.
    *
    * @param decode controls whether sequences of escaped octets are decoded
    *               (true) or not (false).
    * @return an unmodifiable map of parameter names and values
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    * @see javax.ws.rs.Path
    */
   public MultivaluedMap<String, String> getTemplateParameters(boolean decode);

   /**
    * Get the URI query parameters of the current request.
    * All sequences of escaped octets are decoded,
    * equivalent to <code>getQueryParameters(true)</code>.
    *
    * @return an unmodifiable map of query parameter names and values
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    */
   public MultivaluedMap<String, String> getQueryParameters();

   /**
    * Get the URI query parameters of the current request.
    *
    * @param decode controls whether sequences of escaped octets are decoded
    *               (true) or not (false).
    * @return an unmodifiable map of query parameter names and values
    * @throws java.lang.IllegalStateException
    *          if called outside the scope of a request
    */
   public MultivaluedMap<String, String> getQueryParameters(boolean decode);

   /**
    * Get a read-only list of URIs for ancestor resources. Each entry is a relative URI
    * that is a partial path that matched a resource class, a sub-resource
    * method or a sub-resource locator. Entries do not include query
    * parameters but do include matrix parameters if present in the request URI.
    * Entries are ordered in reverse request URI matching order, with the
    * root resource URI last. E.g.:
    * <p/>
    * <pre>&#064;Path("foo")
    * public class FooResource {
    *  &#064;GET
    *  public String getFoo() {...}
    * <p/>
    *  &#064;Path("bar")
    *  &#064;GET
    *  public String getFooBar() {...}
    * }</pre>
    * <p/>
    * <p>A request <code>GET /foo</code> would return an empty list since
    * <code>FooResource</code> is a root resource.</p>
    * <p/>
    * <p>A request <code>GET /foo/bar</code> would return a list with one
    * entry: "foo".</p>
    *
    * @return a read-only list of URI paths for ancestor resources.
    */
   public List<String> getAncestorResourceURIs();

   /**
    * Get a read-only list of ancestor resource class instances. Each entry is a resource
    * class instance that matched a resource class, a sub-resource method or
    * a sub-resource locator. Entries are ordered according in reverse request URI
    * matching order, with the root resource last. E.g.:
    * <p/>
    * <pre>&#064;Path("foo")
    * public class FooResource {
    *  &#064;GET
    *  public String getFoo() {...}
    * <p/>
    *  &#064;Path("bar")
    *  &#064;GET
    *  public String getFooBar() {...}
    * }</pre>
    * <p/>
    * <p>A request <code>GET /foo</code> would return an empty list since
    * <code>FooResource</code> is a root resource.</p>
    * <p/>
    * <p>A request <code>GET /foo/bar</code> would return a list with one
    * entry: an instance of
    * <code>FooResource</code>.</p>
    *
    * @return a read-only list of ancestor resource class instances.
    */
   public List<Object> getAncestorResources();
}

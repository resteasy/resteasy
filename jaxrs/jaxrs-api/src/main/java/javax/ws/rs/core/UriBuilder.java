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
 * UriBuilder.java
 *
 * Created on July 18, 2007, 11:53 AM
 *
 */

package javax.ws.rs.core;

import javax.ws.rs.ext.RuntimeDelegate;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;

/**
 * URI template aware utility class for building URIs from their components. See
 * {@link javax.ws.rs.Path#value} for an explanation of URI templates.
 * <p/>
 * <p>Builder methods perform contextual encoding of characters not permitted in
 * the corresponding URI component following the rules of the
 * <a href="http://www.w3.org/TR/html4/interact/forms.html#h-17.13.4.1">application/x-www-form-urlencoded</a>
 * media type for query parameters and
 * <a href="http://ietf.org/rfc/rfc3986.txt">RFC 3986</a> for all other
 * components. Note that only characters not permitted in a particular component
 * are subject to encoding so, e.g., a path supplied to one of the {@code path}
 * methods may contain matrix parameters or multiple path segments since the
 * separators are legal characters and will not be encoded. Percent encoded
 * values are also recognized where allowed and will not be double encoded.</p>
 * <p/>
 * <p>URI templates are allowed in most components of a URI but their value is
 * restricted to a particular component. E.g.
 * <blockquote><code>UriBuilder.fromPath("{arg1}").build("foo#bar");</code></blockquote>
 * would result in encoding of the '#' such that the resulting URI is
 * "foo%23bar". To create a URI "foo#bar" use
 * <blockquote><code>UriBuilder.fromPath("{arg1}").fragment("{arg2}").build("foo", "bar")</code></blockquote>
 * instead. URI template names and delimiters are never encoded but their
 * values are encoded when a URI is built.
 * Template parameter regular expressions are ignored when building a URI, i.e.
 * no validation is performed.
 *
 * @see java.net.URI
 * @see javax.ws.rs.Path
 */
public abstract class UriBuilder
{

   /**
    * Protected constructor, use one of the static <code>from<i>XXX</i></code>
    * methods to obtain an instance.
    */
   protected UriBuilder()
   {
   }

   /**
    * Creates a new instance of UriBuilder.
    *
    * @return a new instance of UriBuilder
    */
   protected static UriBuilder newInstance()
   {
      UriBuilder b = RuntimeDelegate.getInstance().createUriBuilder();
      return b;
   }

   /**
    * Create a new instance initialized from an existing URI.
    *
    * @param uri a URI that will be used to initialize the UriBuilder.
    * @return a new UriBuilder
    * @throws IllegalArgumentException if uri is null
    */
   public static UriBuilder fromUri(URI uri) throws IllegalArgumentException
   {
      UriBuilder b = newInstance();
      b.uri(uri);
      return b;
   }

   /**
    * Create a new instance initialized from an existing URI.
    *
    * @param uri a URI that will be used to initialize the UriBuilder, may not
    *            contain URI parameters.
    * @return a new UriBuilder
    * @throws IllegalArgumentException if uri is not a valid URI or is null
    */
   public static UriBuilder fromUri(String uri) throws IllegalArgumentException
   {
      URI u;
      try
      {
         u = URI.create(uri);
      }
      catch (NullPointerException ex)
      {
         throw new IllegalArgumentException(ex.getMessage(), ex);
      }
      return fromUri(u);
   }

   /**
    * Create a new instance representing a relative URI initialized from a
    * URI path.
    *
    * @param path a URI path that will be used to initialize the UriBuilder,
    *             may contain URI template parameters.
    * @return a new UriBuilder
    * @throws IllegalArgumentException if path is null
    */
   public static UriBuilder fromPath(String path) throws IllegalArgumentException
   {
      if (path == null) throw new IllegalArgumentException("path parameter is null");
      UriBuilder b = newInstance();
      b.replacePath(path);
      return b;
   }

   /**
    * Create a new instance representing a relative URI initialized from a
    * root resource class.
    *
    * @param resource a root resource whose {@link javax.ws.rs.Path} value will
    *                 be used to initialize the UriBuilder.
    * @return a new UriBuilder
    * @throws IllegalArgumentException if resource is not annotated with
    *                                  {@link javax.ws.rs.Path} or resource is null.
    */
   public static UriBuilder fromResource(Class<?> resource) throws IllegalArgumentException
   {
      UriBuilder b = newInstance();
      b.path(resource);
      return b;
   }

   /**
    * Create a copy of the UriBuilder preserving its state. This is a more
    * efficient means of creating a copy than constructing a new UriBuilder
    * from a URI returned by the {@link #build} method.
    *
    * @return a copy of the UriBuilder
    */
   @Override
   public abstract UriBuilder clone();

   /**
    * Copies the non-null components of the supplied URI to the UriBuilder replacing
    * any existing values for those components.
    *
    * @param uri the URI to copy components from
    * @return the updated UriBuilder
    * @throws IllegalArgumentException if uri is null
    */
   public abstract UriBuilder uri(URI uri) throws IllegalArgumentException;

   /**
    * Set the URI scheme.
    *
    * @param scheme the URI scheme, may contain URI template parameters.
    *               A null value will unset the URI scheme.
    * @return the updated UriBuilder
    * @throws IllegalArgumentException if scheme is invalid
    */
   public abstract UriBuilder scheme(String scheme) throws IllegalArgumentException;

   /**
    * Set the URI scheme-specific-part (see {@link java.net.URI}). This
    * method will overwrite any existing
    * values for authority, user-info, host, port and path.
    *
    * @param ssp the URI scheme-specific-part, may contain URI template parameters
    * @return the updated UriBuilder
    * @throws IllegalArgumentException if ssp cannot be parsed or is null
    */
   public abstract UriBuilder schemeSpecificPart(String ssp) throws IllegalArgumentException;

   /**
    * Set the URI user-info.
    *
    * @param ui the URI user-info, may contain URI template parameters.
    *           A null value will unset userInfo component of the URI.
    * @return the updated UriBuilder
    */
   public abstract UriBuilder userInfo(String ui);

   /**
    * Set the URI host.
    *
    * @param host the URI host, may contain URI template parameters.
    *             A null value will unset the host component of the URI.
    * @return the updated UriBuilder
    * @throws IllegalArgumentException if host is invalid.
    */
   public abstract UriBuilder host(String host) throws IllegalArgumentException;

   /**
    * Set the URI port.
    *
    * @param port the URI port, a value of -1 will unset an explicit port.
    * @return the updated UriBuilder
    * @throws IllegalArgumentException if port is invalid
    */
   public abstract UriBuilder port(int port) throws IllegalArgumentException;

   /**
    * Set the URI path. This method will overwrite
    * any existing path and associated matrix parameters.
    * Existing '/' characters are preserved thus a single value can
    * represent multiple URI path segments.
    *
    * @param path the path, may contain URI template parameters.
    *             A null value will unset the path component of the URI.
    * @return the updated UriBuilder
    */
   public abstract UriBuilder replacePath(String path);

   /**
    * Append path to the existing path.
    * When constructing the final path, a '/' separator will be inserted
    * between the existing path and the supplied path if necessary.
    * Existing '/' characters are preserved thus a single value can
    * represent multiple URI path segments.
    *
    * @param path the path, may contain URI template parameters
    * @return the updated UriBuilder
    * @throws IllegalArgumentException if path is null
    */
   public abstract UriBuilder path(String path) throws IllegalArgumentException;

   /**
    * Append the path from a Path-annotated class to the
    * existing path.
    * When constructing the final path, a '/' separator will be inserted
    * between the existing path and the supplied path if necessary.
    *
    * @param resource a resource whose {@link javax.ws.rs.Path} value will be
    *                 used to obtain the path to append.
    * @return the updated UriBuilder
    * @throws IllegalArgumentException if resource is null, or
    *                                  if resource is not annotated with {@link javax.ws.rs.Path}
    */
   public abstract UriBuilder path(Class resource) throws IllegalArgumentException;

   /**
    * Append the path from a Path-annotated method to the
    * existing path.
    * When constructing the final path, a '/' separator will be inserted
    * between the existing path and the supplied path if necessary.
    * This method is a convenience shortcut to <code>path(Method)</code>, it
    * can only be used in cases where there is a single method with the
    * specified name that is annotated with {@link javax.ws.rs.Path}.
    *
    * @param resource the resource containing the method
    * @param method   the name of the method whose {@link javax.ws.rs.Path} value will be
    *                 used to obtain the path to append
    * @return the updated UriBuilder
    * @throws IllegalArgumentException if resource or method is null,
    *                                  or there is more than or less than one variant of the method annotated with
    *                                  {@link javax.ws.rs.Path}
    */
   public abstract UriBuilder path(Class resource, String method) throws IllegalArgumentException;

   /**
    * Append the path from a {@link javax.ws.rs.Path}-annotated method to the
    * existing path.
    * When constructing the final path, a '/' separator will be inserted
    * between the existing path and the supplied path if necessary.
    *
    * @param method a method whose {@link javax.ws.rs.Path} value will be
    *               used to obtain the path to append to the existing path
    * @return the updated UriBuilder
    * @throws IllegalArgumentException if method is null or is
    *                                  not annotated with a {@link javax.ws.rs.Path}
    */
   public abstract UriBuilder path(Method method) throws IllegalArgumentException;

   /**
    * Append path segments to the existing path.
    * When constructing the final path, a '/' separator will be inserted
    * between the existing path and the first path segment if necessary and
    * each supplied segment will also be separated by '/'.
    * Existing '/' characters are encoded thus a single value can
    * only represent a single URI path segment.
    *
    * @param segments the path segment values, each may contain URI template
    *                 parameters
    * @return the updated UriBuilder
    * @throws IllegalArgumentException if segments or any element of segments
    *                                  is null
    */
   public abstract UriBuilder segment(String... segments) throws IllegalArgumentException;

   /**
    * Set the matrix parameters of the current final segment of the current URI path.
    * This method will overwrite any existing matrix parameters on the current final
    * segment of the current URI path. Note that the matrix parameters
    * are tied to a particular path segment; subsequent addition of path segments
    * will not affect their position in the URI path.
    *
    * @param matrix the matrix parameters, may contain URI template parameters.
    *               A null value will remove all matrix parameters of the current final segment
    *               of the current URI path.
    * @return the updated UriBuilder
    * @throws IllegalArgumentException if matrix cannot be parsed
    * @see <a href="http://www.w3.org/DesignIssues/MatrixURIs.html">Matrix URIs</a>
    */
   public abstract UriBuilder replaceMatrix(String matrix) throws IllegalArgumentException;

   /**
    * Append a matrix parameter to the existing set of matrix parameters of
    * the current final segment of the URI path. If multiple values are supplied
    * the parameter will be added once per value. Note that the matrix parameters
    * are tied to a particular path segment; subsequent addition of path segments
    * will not affect their position in the URI path.
    *
    * @param name   the matrix parameter name, may contain URI template parameters
    * @param values the matrix parameter value(s), each object will be converted
    *               to a {@code String} using its {@code toString()} method. Stringified
    *               values may contain URI template parameters.
    * @return the updated UriBuilder
    * @throws IllegalArgumentException if name or values is null
    * @see <a href="http://www.w3.org/DesignIssues/MatrixURIs.html">Matrix URIs</a>
    */
   public abstract UriBuilder matrixParam(String name, Object... values) throws IllegalArgumentException;

   /**
    * Replace the existing value(s) of a matrix parameter on
    * the current final segment of the URI path. If multiple values are supplied
    * the parameter will be added once per value. Note that the matrix parameters
    * are tied to a particular path segment; subsequent addition of path segments
    * will not affect their position in the URI path.
    *
    * @param name   the matrix parameter name, may contain URI template parameters
    * @param values the matrix parameter value(s), each object will be converted
    *               to a {@code String} using its {@code toString()} method. Stringified
    *               values may contain URI template parameters. If {@code values} is empty
    *               or null then all current values of the parameter are removed.
    * @return the updated UriBuilder
    * @throws IllegalArgumentException if name is null.
    * @see <a href="http://www.w3.org/DesignIssues/MatrixURIs.html">Matrix URIs</a>
    */
   public abstract UriBuilder replaceMatrixParam(String name, Object... values) throws IllegalArgumentException;

   /**
    * Set the URI query string. This method will overwrite any existing query
    * parameters.
    *
    * @param query the URI query string, may contain URI template parameters.
    *              A null value will remove all query parameters.
    * @return the updated UriBuilder
    * @throws IllegalArgumentException if query cannot be parsed
    */
   public abstract UriBuilder replaceQuery(String query) throws IllegalArgumentException;

   /**
    * Append a query parameter to the existing set of query parameters. If
    * multiple values are supplied the parameter will be added once per value.
    *
    * @param name   the query parameter name, may contain URI template parameters
    * @param values the query parameter value(s), each object will be converted
    *               to a {@code String} using its {@code toString()} method. Stringified
    *               values may contain URI template parameters.
    * @return the updated UriBuilder
    * @throws IllegalArgumentException if name or values is null
    */
   public abstract UriBuilder queryParam(String name, Object... values) throws IllegalArgumentException;

   /**
    * Replace the existing value(s) of a query parameter. If
    * multiple values are supplied the parameter will be added once per value.
    *
    * @param name   the query parameter name, may contain URI template parameters
    * @param values the query parameter value(s), each object will be converted
    *               to a {@code String} using its {@code toString()} method. Stringified
    *               values may contain URI template parameters. If {@code values} is empty
    *               or null then all current values of the parameter are removed.
    * @return the updated UriBuilder
    * @throws IllegalArgumentException if name is null
    */
   public abstract UriBuilder replaceQueryParam(String name, Object... values) throws IllegalArgumentException;

   /**
    * Set the URI fragment.
    *
    * @param fragment the URI fragment, may contain URI template parameters.
    *                 A null value will remove any existing fragment.
    * @return the updated UriBuilder
    */
   public abstract UriBuilder fragment(String fragment);

   /**
    * Build a URI, any URI template parameters will be replaced by the value in
    * the supplied map. Values are converted to <code>String</code> using
    * their <code>toString</code> method and are then encoded to match the
    * rules of the URI component to which they pertain.  All '%' characters
    * in the stringified values will be encoded.
    * The state of the builder is unaffected; this method may be called
    * multiple times on the same builder instance.
    *
    * @param values a map of URI template parameter names and values
    * @return the URI built from the UriBuilder
    * @throws IllegalArgumentException if there are any URI template parameters
    *                                  without a supplied value, or if a template parameter value is null.
    * @throws UriBuilderException      if a URI cannot be constructed based on the
    *                                  current state of the builder.
    */
   public abstract URI buildFromMap(Map<String, ? extends Object> values)
           throws IllegalArgumentException, UriBuilderException;

   /**
    * Build a URI, any URI template parameters will be replaced by the value in
    * the supplied map. Values are converted to <code>String</code> using
    * their <code>toString</code> method and are then encoded to match the
    * rules of the URI component to which they pertain.  All % characters in
    * the stringified values that are not followed by two hexadecimal numbers
    * will be encoded.
    * The state of the builder is unaffected; this method may be called
    * multiple times on the same builder instance.
    *
    * @param values a map of URI template parameter names and values
    * @return the URI built from the UriBuilder
    * @throws IllegalArgumentException if there are any URI template parameters
    *                                  without a supplied value, or if a template parameter value is null.
    * @throws UriBuilderException      if a URI cannot be constructed based on the
    *                                  current state of the builder.
    */
   public abstract URI buildFromEncodedMap(Map<String, ? extends Object> values)
           throws IllegalArgumentException, UriBuilderException;

   /**
    * Build a URI, using the supplied values in order to replace any URI
    * template parameters. Values are converted to <code>String</code> using
    * their <code>toString</code> method and are then encoded to match the
    * rules of the URI component to which they pertain. All '%' characters
    * in the stringified values will be encoded.
    * The state of the builder is unaffected; this method may be called
    * multiple times on the same builder instance.
    * <p>All instances of the same template parameter
    * will be replaced by the same value that corresponds to the position of the
    * first instance of the template parameter. e.g. the template "{a}/{b}/{a}"
    * with values {"x", "y", "z"} will result in the the URI "x/y/x", <i>not</i>
    * "x/y/z".
    *
    * @param values a list of URI template parameter values
    * @return the URI built from the UriBuilder
    * @throws IllegalArgumentException if there are any URI template parameters
    *                                  without a supplied value, or if a value is null.
    * @throws UriBuilderException      if a URI cannot be constructed based on the
    *                                  current state of the builder.
    */
   public abstract URI build(Object... values)
           throws IllegalArgumentException, UriBuilderException;

   /**
    * Build a URI.
    * Any URI templates parameters will be replaced with the supplied values in
    * order. Values are converted to <code>String</code> using
    * their <code>toString</code> method and are then encoded to match the
    * rules of the URI component to which they pertain. All % characters in
    * the stringified values that are not followed by two hexadecimal numbers
    * will be encoded.
    * The state of the builder is unaffected; this method may be called
    * multiple times on the same builder instance.
    * <p>All instances of the same template parameter
    * will be replaced by the same value that corresponds to the position of the
    * first instance of the template parameter. e.g. the template "{a}/{b}/{a}"
    * with values {"x", "y", "z"} will result in the the URI "x/y/x", <i>not</i>
    * "x/y/z".
    *
    * @param values a list of URI template parameter values
    * @return the URI built from the UriBuilder
    * @throws IllegalArgumentException if there are any URI template parameters
    *                                  without a supplied value, or if a value is null.
    * @throws UriBuilderException      if a URI cannot be constructed based on the
    *                                  current state of the builder.
    */
   public abstract URI buildFromEncoded(Object... values)
           throws IllegalArgumentException, UriBuilderException;
}

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

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;
import javax.ws.rs.ext.Contract;
import javax.ws.rs.ext.ProviderFactory;

/**
 * URI template aware utility class for building URIs from their components. See
 * {@link javax.ws.rs.Path#value} for an explanation of URI templates.
 *
 * <p>Many methods support automatic encoding of illegal characters, see
 * {@link #encode} method. Encoding and validation of URI
 * components follow the rules of the 
 * <a href="http://www.w3.org/TR/html4/interact/forms.html#h-17.13.4.1">application/x-www-form-urlencoded</a>
 * media type for query parameters and
 * <a href="http://ietf.org/rfc/rfc3986.txt">RFC 3986</a> for all other
 * components.</p>
 *
 * <p>URI templates are allowed in most components of a URI but their value is
 * restricted to a particular component. E.g. 
 * <blockquote><code>UriBuilder.fromPath("{arg1}").build("foo#bar");</code></blockquote>
 * would result in encoding of the '#' such that the resulting URI is 
 * "foo%23bar". To create a URI "foo#bar" use
 * <blockquote><code>UriBuilder.fromPath("{arg1}").fragment("{arg2}").build("foo", "bar")</code></blockquote>
 * instead.
 *
 * @see java.net.URI
 * @see javax.ws.rs.Path
 */
@Contract
public abstract class UriBuilder {
    
    /**
     * Creates a new instance of UriBuilder with automatic encoding 
     * (see {@link #encode} method) turned on.
     * @return a new instance of UriBuilder
     */
    protected static synchronized UriBuilder newInstance() {
        UriBuilder b = ProviderFactory.getInstance().createInstance(UriBuilder.class);
        if (b==null)
            throw new UnsupportedOperationException("No UriBuilder implementation found");
        return b;
    }
    
    /**
     * Create a new instance initialized from an existing URI with automatic encoding 
     * (see {@link #encode} method) turned on.
     * @param uri a URI that will be used to initialize the UriBuilder.
     * @return a new UriBuilder
     * @throws IllegalArgumentException if uri is null
     */
    public static UriBuilder fromUri(URI uri) throws IllegalArgumentException {
        UriBuilder b = newInstance();
        b.encode(true);
        b.uri(uri);
        return b;
    }
    
    /**
     * Create a new instance initialized from an existing URI with automatic encoding 
     * (see {@link #encode} method) turned on.
     * @param uri a URI that will be used to initialize the UriBuilder, may not
     * contain URI parameters.
     * @return a new UriBuilder
     * @throws IllegalArgumentException if uri is not a valid URI or is null
     */
    public static UriBuilder fromUri(String uri) throws IllegalArgumentException {
        URI u;
        try {
            u = URI.create(uri);
        } catch (NullPointerException ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
        return fromUri(u);
    }
    
    /**
     * Create a new instance initialized from an unencoded URI path,
     * equivalent to <code>fromPath(path, true)</code>.
     * @param path a URI path that will be used to initialize the UriBuilder, may contain URI template parameters.
     * @return a new UriBuilder
     * @throws IllegalArgumentException if path is null
     */
    public static UriBuilder fromPath(String path) throws IllegalArgumentException {
        return fromPath(path, true);
    }

    /**
     * Create a new instance initialized from a URI path.
     * @param path a URI path that will be used to initialize the UriBuilder, may contain URI template parameters.
     * @param encode controls whether the supplied value is automatically encoded
     * (true) or not (false). If false, the value must be valid with all illegal
     * characters already escaped. The supplied value will remain in force for subsequent
     * operations and may be altered by calling the encode method.
     * @return a new UriBuilder
     * @throws IllegalArgumentException if path is null, or 
     * if encode is false and path contains illegal characters
     */
    public static UriBuilder fromPath(String path, boolean encode) throws IllegalArgumentException {
        UriBuilder b = newInstance();
        b.encode(encode);
        b.replacePath(path);
        return b;
    }

    /**
     * Create a new instance initialized from a root resource class with automatic encoding 
     * (see {@link #encode} method) turned on.
     * @param resource a root resource whose @Path value will be used
     * to initialize the UriBuilder. The value of the encode property of the Path
     * annotation will be used when processing the value of the @Path but it
     * will not be used to modify the state of automaic encoding for the builder.
     * @return a new UriBuilder
     * @throws IllegalArgumentException if resource is not annotated with Path, or
     * if resource.encode is false and resource.value, or
     * if resource is null
     * contains illegal characters
     */
    public static UriBuilder fromResource(Class<?> resource) throws IllegalArgumentException {
        UriBuilder b = newInstance();
        b.path(resource);
        return b;
    }
    
    /**
     * Create a copy of the UriBuilder preserving its state. This is a more
     * efficient means of creating a copy than constructing a new UriBuilder
     * from a URI returned by the {@link #build} method.
     * @return a copy of the UriBuilder
     */
    public abstract UriBuilder clone();
    
    /**
     * Controls whether the UriBuilder will automatically encode URI components
     * added by subsequent operations or not.
     * @param enable automatic encoding (true) or disable it (false). 
     * If false, subsequent components added must be valid with all illegal
     * characters already escaped.
     * @return the updated UriBuilder
     */
    public abstract UriBuilder encode(boolean enable);

    /**
     * Copies the non-null components of the supplied URI to the UriBuilder replacing
     * any existing values for those components.
     * @param uri the URI to copy components from
     * @return the updated UriBuilder
     * @throws IllegalArgumentException if uri is null
     */
    public abstract UriBuilder uri(URI uri) throws IllegalArgumentException;
    
    /**
     * Set the URI scheme.
     * @param scheme the URI scheme, may contain URI template parameters
     * @return the updated UriBuilder
     * @throws IllegalArgumentException if scheme is invalid or is null
     */
    public abstract UriBuilder scheme(String scheme) throws IllegalArgumentException;
    
    /**
     * Set the URI scheme-specific-part (see {@link java.net.URI}). This 
     * method will overwrite any existing
     * values for authority, user-info, host, port and path.
     * @param ssp the URI scheme-specific-part, may contain URI template parameters
     * @return the updated UriBuilder
     * @throws IllegalArgumentException if ssp cannot be parsed or is null
     */
    public abstract UriBuilder schemeSpecificPart(String ssp) throws IllegalArgumentException;
    
    /**
     * Set the URI user-info.
     * @param ui the URI user-info, may contain URI template parameters
     * @return the updated UriBuilder
     * @throws IllegalArgumentException if automatic encoding is disabled and
     * ui contains illegal characters, or
     * if ui is null
     */
    public abstract UriBuilder userInfo(String ui) throws IllegalArgumentException;
    
    /**
     * Set the URI host.
     * @return the updated UriBuilder
     * @param host the URI host, may contain URI template parameters
     * @throws IllegalArgumentException if host is invalid or is null
     */
    public abstract UriBuilder host(String host) throws IllegalArgumentException;
    
    /**
     * Set the URI port.
     * @param port the URI port, a value of -1 will unset an explicit port.
     * @return the updated UriBuilder
     * @throws IllegalArgumentException if port is invalid
     */
    public abstract UriBuilder port(int port) throws IllegalArgumentException;
    
    /**
     * Set the URI path. This method will overwrite 
     * any existing path segments and associated matrix parameters.
     * @param path the URI path, may contain URI template parameters
     * @return the updated UriBuilder
     * @throws IllegalArgumentException if automatic encoding is disabled and
     * path contains illegal characters, or
     * if path is null
     */
    public abstract UriBuilder replacePath(String path) throws IllegalArgumentException;

    /**
     * Append path segments to the existing list of segments. When constructing
     * the final path, each segment will be separated by '/' if necessary. 
     * Existing '/' characters are preserved thus a single segment value can 
     * represent multiple URI path segments.
     * @param segments the path segments, may contain URI template parameters
     * @return the updated UriBuilder
     * @throws IllegalArgumentException if any element of segments is null, or
     * if automatic encoding is disabled and
     * any element of segments contains illegal characters
     */
    public abstract UriBuilder path(String... segments) throws IllegalArgumentException;

    /**
     * Append path segments from a Path-annotated class to the
     * existing list of segments. When constructing
     * the final path, each segment will be separated by '/' if necessary.
     * The value of the encode property of the Path
     * annotation will be used when processing the value of the @Path but it
     * will not be used to modify the state of automaic encoding for the builder.
     * @param resource a resource whose @Path value will be
     * used to obtain the path segment.
     * @return the updated UriBuilder
     * @throws IllegalArgumentException if resource is null, or
     * if resource.encode is false and resource.value contains illegal characters, or
     * if resource is not annotated with Path
     * 
     */
    public abstract UriBuilder path(Class resource) throws IllegalArgumentException;
    
    /**
     * Append path segments from a Path-annotated method to the
     * existing list of segments. When constructing
     * the final path, each segment will be separated by '/' if necessary.
     * This method is a convenience shortcut to <code>path(Method)</code>, it
     * can only be used in cases where there is a single method with the
     * specified name that is annotated with @Path.
     * @param resource the resource containing the method
     * @param method the name of the method whose @Path value will be
     * used to obtain the path segment
     * @return the updated UriBuilder
     * @throws IllegalArgumentException if resource or method is null, or
     * if the specified method does not exist,
     * or there is more than or less than one variant of the method annotated with 
     * Path
     */
    public abstract UriBuilder path(Class resource, String method) throws IllegalArgumentException;
    
    /**
     * Append path segments from a list of Path-annotated methods to the
     * existing list of segments. When constructing
     * the final path, each segment will be separated by '/' if necessary.
     * The value of the encode property of the Path
     * annotation will be used when processing the value of the @Path but it
     * will not be used to modify the state of automaic encoding for the builder.
     * @param methods a list of methods whose @Path values will be
     * used to obtain the path segments
     * @return the updated UriBuilder
     * @throws IllegalArgumentException if any element of methods is null or is
     * not annotated with a Path
     */
    public abstract UriBuilder path(Method... methods) throws IllegalArgumentException;
    
    /**
     * Set the matrix parameters of the final segment of the current URI path.
     * This method will overwrite any existing matrix parameters on the final
     * segment of the current URI path.
     * @param matrix the matrix parameters, may contain URI template parameters
     * @return the updated UriBuilder
     * @throws IllegalArgumentException if matrix cannot be parsed or is null, or
     * if automatic encoding is disabled and
     * any matrix parameter name or value contains illegal characters
     */
    public abstract UriBuilder replaceMatrixParams(String matrix) throws IllegalArgumentException;

    /**
     * Append a matrix parameter to the existing set of matrix parameters of 
     * the final segment of the current URI path.
     * @param name the matrix parameter name, may contain URI template parameters
     * @param value the matrix parameter value, may contain URI template parameters
     * @return the updated UriBuilder
     * @throws IllegalArgumentException if name or value is null, or
     * if automatic encoding is disabled and
     * name or value contains illegal characters
     */
    public abstract UriBuilder matrixParam(String name, String value) throws IllegalArgumentException;

    /**
     * Set the URI query string. This method will overwrite any existing query
     * parameters.
     * @param query the URI query string, may contain URI template parameters
     * @return the updated UriBuilder
     * @throws IllegalArgumentException if query cannot be parsed or is null, or
     * if automatic encoding is disabled and
     * any query parameter name or value contains illegal characters
     */
    public abstract UriBuilder replaceQueryParams(String query) throws IllegalArgumentException;

    /**
     * Append a query parameter to the existing set of query parameters.
     * @param name the query parameter name, may contain URI template parameters
     * @param value the query parameter value, may contain URI template parameters
     * @return the updated UriBuilder
     * @throws IllegalArgumentException if name or value is null, or
     * if automatic encoding is disabled and
     * name or value contains illegal characters
     */
    public abstract UriBuilder queryParam(String name, String value) throws IllegalArgumentException;
    
    /**
     * Set the URI fragment using an unencoded value.
     * @param fragment the URI fragment, may contain URI template parameters
     * @return the updated UriBuilder
     * @throws IllegalArgumentException if fragment is null, or
     * if automatic encoding is disabled and
     * fragment contains illegal characters
     */
    public abstract UriBuilder fragment(String fragment) throws IllegalArgumentException;
    
    /**
     * Build a URI, any URI template parameters will be replaced by the empty
     * string. The <code>build</code> method does not change the state of the
     * <code>UriBuilder</code> and it may be called multiple times on the same
     * builder instance.
     * @return the URI built from the UriBuilder
     * @throws UriBuilderException if there are any URI template parameters, or
     * if a URI cannot be constructed based on the
     * current state of the builder.
     */
    public abstract URI build() throws UriBuilderException;

    /**
     * Build a URI, any URI template parameters will be replaced by the value in
     * the supplied map. The <code>build</code> method does not change the state of the
     * <code>UriBuilder</code> and it may be called multiple times on the same
     * builder instance.
     * @param values a map of URI template parameter names and values
     * @return the URI built from the UriBuilder
     * @throws IllegalArgumentException if automatic encoding is disabled and
     * a supplied value contains illegal characters, or
     * if there are any URI template parameters without
     * a supplied value
     * @throws UriBuilderException if a URI cannot be constructed based on the
     * current state of the builder.
     */
    public abstract URI build(Map<String, String> values) throws IllegalArgumentException, UriBuilderException;
    
    /**
     * Build a URI, using the supplied values in order to replace any URI
     * template parameters. The <code>build</code> method does not change the state of the
     * <code>UriBuilder</code> and it may be called multiple times on the same
     * builder instance.
     * <p>All instances of the same template parameter
     * will be replaced by the same value that corresponds to the position of the
     * first instance of the template parameter. e.g. the template "{a}/{b}/{a}"
     * with values {"x", "y", "z"} will result in the the URI "x/y/x", <i>not</i>
     * "x/y/z".
     * @param values a list of URI template parameter values
     * @return the URI built from the UriBuilder
     * @throws IllegalArgumentException if automatic encoding is disabled and
     * a supplied value contains illegal characters, or
     * if there are any URI template parameters without
     * a supplied value
     * @throws UriBuilderException if a URI cannot be constructed based on the
     * current state of the builder.
     */
    public abstract URI build(String... values) throws IllegalArgumentException, UriBuilderException;
}

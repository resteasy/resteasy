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
package javax.ws.rs.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.Consumes;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.ext.RuntimeDelegate;
import javax.ws.rs.ext.RuntimeDelegate.HeaderDelegate;

import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.namespace.QName;

/**
 * <p>Class representing hypermedia links. A hypermedia link may include additional
 * parameters beyond its underlying URI. Parameters such as "rel" or "method"
 * provide additional meta-data and can be used to easily create instances of
 * {@link javax.ws.rs.client.Invocation} in order to follow links.</p>
 *
 * <p>The methods {@link #toString} and {@link #valueOf} can be used to serialize
 * and de-serialize a link into a link header (RFC 5988).</p>
 *
 * @author Marek Potociar
 * @author Santiago Pericas-Geertsen
 * @see javax.ws.rs.client.Client#invocation
 * @since 2.0
 */
public final class Link {

    public static final String CONSUMES = "consumes";
    public static final String METHOD = "method";
    public static final String PRODUCES = "produces";
    public static final String TITLE = "title";
    public static final String REL = "rel";
    public static final String TYPE = "type";

    /**
     * The underlying link URI.
     */
    private URI uri;

    /**
     * A map for all the link parameters such as "rel", "type", "method", etc.
     */
    private MultivaluedHashMap<String, String> map = new MultivaluedHashMap<String, String>();

    /**
     * Underlying implementation delegate to serialize as link header.
     */
    private static final HeaderDelegate<Link> delegate =
            RuntimeDelegate.getInstance().createHeaderDelegate(Link.class);

    /**
     * Returns the underlying URI associated with this link.
     *
     * @return underlying URI.
     */
    public URI getUri() {
        return uri;
    }

    /**
     * Convenience method that returns a {@link javax.ws.rs.core.UriBuilder}
     * initialized with this link's underlying URI.
     *
     * @return UriBuilder initialized using underlying URI.
     */
    public UriBuilder getUriBuilder() {
        return UriBuilder.fromUri(uri);
    }

    /**
     * Returns an immutable list containing all the relation types defined
     * on this link via the "rel" parameter. If no relation types are
     * defined, this method returns an empty list.
     *
     * @return list of relation types.
     */
    public List<String> getRel() {
        List<String> l = map.get(REL);
        return (l != null) ? new ArrayList<String>(l) : Collections.<String>emptyList();
    }

    /**
     * Returns the value associated with the link "title" param, or
     * {@code null} if this param is not specified.
     *
     * @return value of "title" parameter or {@code null}.
     */
    public String getTitle() {
        return map.getFirst(TITLE);
    }

    /**
     * Returns the value associated with the link "type" param, or
     * {@code null} if this param is not specified.
     *
     * @return value of "type" parameter or {@code null}.
     */
    public String getType() {
        return map.getFirst(TYPE);
    }

    /**
     * Returns the value associated with the link "method" param, or
     * {@code null} if this param is not specified.
     *
     * @return value of "method" parameter or {@code null}.
     */
    public String getMethod() {
        return map.getFirst(METHOD);
    }

    /**
     * Returns an immutable list containing all the types defined on
     * this link via the "produces" parameter. If no produces types are
     * defined, this method returns an empty list.
     *
     * @return list of produces types.
     */
    public List<String> getProduces() {
        List<String> l = map.get(PRODUCES);
        return (l != null) ? new ArrayList<String>(l) : Collections.<String>emptyList();
    }

    /**
     * Returns an immutable list containing all the types defined on
     * this link via the "consumes" parameter. If no consumes types are
     * defined, this method returns an empty list.
     *
     * @return list of consumes types.
     */
    public List<String> getConsumes() {
        List<String> l = map.get(CONSUMES);
        return (l != null) ? new ArrayList<String>(l) : Collections.<String>emptyList();
    }

    /**
     * Returns an immutable map that includes all the link parameters
     * defined on this link. If defined, this map will include entries
     * for "rel", "title" and "type".
     *
     * @return Immutable map of link parameters.
     */
    public MultivaluedMap<String, String> getParams() {
        return new MultivaluedHashMap<String, String>(map);
    }

    /**
     * Equality test for links.
     *
     * @param other Object to compare against.
     * @return {@code true} if equal, {@code false} otherwise.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Link) {
            final Link otherLink = (Link) other;
            return uri.equals(otherLink.uri) && map.equalsIgnoreValueOrder(otherLink.map);
        }
        return false;
    }

    /**
     * Hash code computation for links.
     *
     * @return Hash code for this link.
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 89 * hash + (this.uri != null ? this.uri.hashCode() : 0);
        hash = 89 * hash + (this.map != null ? this.map.hashCode() : 0);
        return hash;
    }

    /**
     * Returns a string representation as a link header (RFC 5988).
     * All link params are serialized as link-param="value" where value
     * is a list of space-separated tokens. For example,
     *
     * <http://foo.bar/employee/john>; title="employee"; rel="manager friend"
     *
     * @return string link header representation for this link.
     */
    @Override
    public String toString() {
        return delegate.toString(this);
    }

    /**
     * Simple parser to convert link header string representations into a link.
     * <pre>
     * link ::= '<' uri '>' (';' link-param)*
     * link-param ::= name '=' quoted-string
     * </pre>
     *
     * The resulting language is similar to that defined in RFC 5988.
     *
     * @param value String representation.
     * @return newly parsed link.
     * @throws IllegalArgumentException if a syntax error is found.
     */
    public static Link valueOf(String value) throws IllegalArgumentException {
        return delegate.fromString(value);
    }

    /**
     * Create a new instance initialized from an existing URI.
     *
     * @param uri a URI that will be used to initialize the Builder.
     * @return a new builder.
     * @throws IllegalArgumentException if uri is {@code null}.
     */
    public static Builder fromUri(URI uri) throws IllegalArgumentException {
        Builder b = new Builder();
        b.uri(uri);
        return b;
    }

    /**
     * Create a new instance initialized from an existing URI.
     *
     * @param uri a URI that will be used to initialize the Builder.
     * @return a new builder.
     * @throws IllegalArgumentException if uri is {@code null}.
     */
    public static Builder fromUri(String uri) throws IllegalArgumentException {
        Builder b = new Builder();
        b.uri(uri);
        return b;
    }

    /**
     * Create a new instance initialized from another link.
     *
     * @param link other link used for initialization.
     * @return a new builder.
     * @since 2.0
     */
    public static Builder fromLink(Link link) {
        Builder b = new Builder();
        b.uri(link.uri);
        b.link.map = new MultivaluedHashMap<String, String>(link.map);
        return b;
    }

    /**
     * Generate a link by introspecting a resource method. This method is a shorthand
     * for {@code fromResourceMethod(resource, method, method)}.
     *
     * @param resource resource class.
     * @param method   name of resource method.
     * @return link builder to further configure link.
     * @throws IllegalArgumentException if any argument is {@code null} or no method is found.
     * @see Link#fromResourceMethod(java.lang.Class, java.lang.String, java.lang.String)
     */
    public static Builder fromResourceMethod(Class<?> resource, String method)
            throws IllegalArgumentException {
        return fromResourceMethod(resource, method, method);
    }

    /**
     * Generate a link by introspecting a resource method. Finds the first method
     * of a given name and generates a link that includes parameters "method",
     * "produces" and "consumes". If "produces" is not defined,
     * {@link javax.ws.rs.core.MediaType#WILDCARD} is used. Likewise, if "consumes"
     * is not defined, {@link javax.ws.rs.core.MediaType#WILDCARD} is used but
     * only when the HTTP method is POST or PUT. The value of "rel" must be specified
     * as an argument.
     *
     * @param resource resource class.
     * @param method   name of resource method.
     * @param rel      value of {@code "rel"} parameter.
     * @return link builder to further configure link.
     * @throws IllegalArgumentException if any argument is {@code null} or no method is found.
     */
    public static Builder fromResourceMethod(Class<?> resource, String method, String rel)
            throws IllegalArgumentException {
        if (resource == null || method == null || rel == null) {
            throw new IllegalArgumentException("All parameters must be non-null");
        }

        Builder lb = Link.fromUri(UriBuilder.fromResource(resource).build());
        lb.rel(rel);
        Method[] methods = resource.getMethods();
        for (Method m : methods) {
            if (m.getName().equals(method)) {
                String httpMethod = null;
                for (Annotation a : m.getAnnotations()) {
                    Class<? extends Annotation> at = a.annotationType();
                    HttpMethod hm = at.getAnnotation(HttpMethod.class);
                    if (hm != null) {
                        httpMethod = at.getSimpleName();
                        lb.method(httpMethod);
                        break;
                    }
                }
                if (httpMethod == null) {
                    throw new IllegalArgumentException("Unable to find HTTP method annotation in " + method);
                }
                Path path = m.getAnnotation(Path.class);
                if (path != null) {
                    lb.path(m);
                }
                Produces ps = m.getAnnotation(Produces.class);
                if (ps == null) {
                    lb.produces(MediaType.WILDCARD);
                } else {
                    for (String p : ps.value()) {
                        lb.produces(p);
                    }
                }
                Consumes cs = m.getAnnotation(Consumes.class);
                if (cs == null) {
                    if (httpMethod.equals(HttpMethod.POST) || httpMethod.equals(HttpMethod.PUT)) {
                        lb.consumes(MediaType.WILDCARD);
                    }
                } else {
                    for (String c : cs.value()) {
                        lb.consumes(c);
                    }
                }
                return lb;
            }
        }
        throw new IllegalArgumentException("Method '" + method + "' not found in class '"
                + resource.getName() + "'");
    }

    /**
     * Builder class for hypermedia links.
     *
     * @see Link
     * @since 2.0
     */
    public static class Builder {

        /**
         * Link being built by the builder.
         */
        private Link link = new Link();

        /**
         * Underlying builder for link's URI.
         */
        private UriBuilder uriBuilder;

        /**
         * Set underlying URI template for the link being constructed.
         *
         * @param uri underlying URI for link
         * @return the updated builder.
         */
        public Builder uri(URI uri) {
            uriBuilder = UriBuilder.fromUri(uri);
            return this;
        }

        /**
         * Set underlying string representing URI template for the link being
         * constructed.
         *
         * @param uri underlying URI for link.
         * @return the updated builder.
         * @throws IllegalArgumentException if string representation of URI is invalid.
         */
        public Builder uri(String uri) throws IllegalArgumentException {
            uriBuilder = UriBuilder.fromUri(uri);
            return this;
        }

        /**
         * Convenience method to set a link relation. More than one rel value can
         * be specified by using one or more whitespace characters as delimiters.
         *
         * @param name relation name.
         * @return the updated builder.
         */
        public Builder rel(String name) {
            link.map.addAll(REL, toValueList(name));
            return this;
        }

        /**
         * Convenience method to set a title on this link. If called more than once,
         * the previous value of title is overwritten.
         *
         * @param title title parameter of this link.
         * @return the updated builder.
         */
        public Builder title(String title) {
            link.map.putSingle(TITLE, title);
            return this;

        }

        /**
         * Convenience method to set a on this link. If called more than once,
         * the previous value of title is overwritten.
         *
         * @param type type parameter of this link.
         * @return the updated builder.
         */
        public Builder type(String type) {
            link.map.putSingle(TYPE, type);
            return this;
        }

        /**
         * Convenience method to set a type on this link. If called more than once,
         * the previous value of method is overwritten.
         *
         * @param method HTTP method name.
         * @return the updated builder.
         */
        public Builder method(String method) {
            link.map.putSingle(METHOD, method);
            return this;
        }

        /**
         * Convenience method to set a produces type on this link. More than one
         * value can be specified by using one or more whitespace characters as
         * delimiters.
         *
         * @param type link type as string.
         * @return the updated builder.
         */
        public Builder produces(String type) {
            link.map.addAll(PRODUCES, toValueList(type));
            return this;
        }

        /**
         * Convenience method to set a produces type on this link. More than one
         * value can be specified by using one or more whitespace characters as
         * delimiters.
         *
         * @param type link type as string.
         * @return the updated builder.
         */
        public Builder consumes(String type) {
            link.map.addAll(CONSUMES, toValueList(type));
            return this;
        }

        /**
         * Set an arbitrary parameter on this link. This method supports adding
         * more than one value for each parameter using one or more whitespace delimiters.
         * It is recommended to use the more specific methods {@link #method} or
         * {@link #title} when setting these single-valued parameters.
         *
         * @param name  the name of the parameter.
         * @param value the value set for the parameter.
         * @return the updated builder.
         * @throws IllegalArgumentException if either the name or value are {@code null}.
         */
        public Builder param(String name, String value) throws IllegalArgumentException {
            if (name == null || value == null) {
                throw new IllegalArgumentException("Link parameter name or value is null");
            }
            link.map.addAll(name, toValueList(value));
            return this;
        }

        /**
         * Finish building this link and return the instance.
         *
         * @return newly built link.
         * @throws IllegalArgumentException if there are any URI template parameters
         *                                  without a supplied value.
         * @throws UriBuilderException      if a URI cannot be constructed based on the
         *                                  current state of the builder.
         */
        public Link build() {
            link.uri = uriBuilder.build();
            return link;
        }

        /**
         * Finish building this link using the supplied values as URI parameters.
         *
         * @param values parameters used to build underlying URI.
         * @return the updated builder.
         * @throws IllegalArgumentException if there are any URI template parameters
         *                                  without a supplied value, or if a value is {@code null}.
         * @throws UriBuilderException      if a URI cannot be constructed based on the
         *                                  current state of the underlying URI builder.
         */
        public Link build(Object... values) throws UriBuilderException {
            link.uri = uriBuilder.build(values);
            return link;
        }

        /**
         * Adds a path to the existing URI builder. For internal use only.
         *
         * @param method method from which to get path.
         * @return the updated builder.
         */
        private Builder path(Method method) {
            uriBuilder.path(method);
            return this;
        }

        /**
         * Split a string value using one or more whitespace chars as separators.
         *
         * @param value values representation as string.
         * @return list of strings.
         */
        public static List<String> toValueList(String value) {
            if (value == null) {
                throw new IllegalArgumentException("Link parameter value is null");
            }
            final String[] vs = value.split("\\s+");
            for (int i = 0; i < vs.length; i++) {
                vs[i] = vs[i].trim();
            }

            return Arrays.asList(vs);
        }
    }

    /**
     * Value type for {@link javax.ws.rs.core.Link} that can be marshalled and
     * unmarshalled by JAXB.
     *
     * @see javax.ws.rs.core.Link.JaxbAdapter
     * @since 2.0
     */
    public static class JaxbLink {

        private URI uri;
        private Map<QName, Object> params;

        public JaxbLink() {
        }

        public JaxbLink(URI uri) {
            this.uri = uri;
        }

        public JaxbLink(URI uri, Map<QName, Object> params) {
            this.uri = uri;
            this.params = params;
        }

        @XmlAttribute(name = "href")
        public URI getUri() {
            return uri;
        }

        @XmlAnyAttribute
        public Map<QName, Object> getParams() {
            if (params == null) {
                params = new HashMap<QName, Object>();
            }
            return params;
        }
    }

    /**
     * <p>An implementation of JAXB {@link javax.xml.bind.annotation.adapters.XmlAdapter}
     * that maps the JAX-RS {@link javax.ws.rs.core.Link} type to a value that can be
     * marshalled and unmarshalled by JAXB.</p>
     *
     * <p>All link parameters are treated as multi valued except for "title" and "method".</p>
     *
     * @see javax.ws.rs.core.Link.JaxbLink
     * @since 2.0
     */
    public static class JaxbAdapter extends XmlAdapter<JaxbLink, Link> {

        @Override
        public Link unmarshal(JaxbLink v) throws Exception {
            Link.Builder lb = Link.fromUri(v.getUri());
            for (Entry<QName, Object> e : v.getParams().entrySet()) {
                final String name = e.getKey().getLocalPart();
                if (TITLE.equals(name) || METHOD.equals(name)) {
                    lb.param(name, e.getValue().toString());
                } else {
                    String[] values = e.getValue().toString().split(" ");
                    for (String value : values) {
                        lb.param(name, value);
                    }
                }
            }
            return lb.build();
        }

        @Override
        public JaxbLink marshal(Link v) throws Exception {
            JaxbLink jl = new JaxbLink(v.getUri());
            for (Entry<String, List<String>> e : v.getParams().entrySet()) {
                final String name = e.getKey();
                if (TITLE.equals(name) || METHOD.equals(name)) {
                    jl.getParams().put(new QName("", name), e.getValue().get(0));
                } else {
                    boolean first = true;
                    StringBuilder sb = new StringBuilder();
                    for (String value : e.getValue()) {
                        if (first) {
                            first = false;
                        } else {
                            sb.append(" ");
                        }
                        sb.append(value);
                    }
                    jl.getParams().put(new QName("", e.getKey()), sb.toString());
                }
            }
            return jl;
        }
    }
}
